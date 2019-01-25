package com.jeg.te.justenoughgoods.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.jeg.te.justenoughgoods.R;

import java.util.ArrayList;

public class BluetoothDeviceListActivity extends Activity implements AdapterView.OnItemClickListener {
    static class DeviceListAdapter extends BaseAdapter
    {
        private ArrayList<BluetoothDevice> bluetoothDeviceList;
        private LayoutInflater deviceListLayoutInflater;

        public DeviceListAdapter( Activity activity )
        {
            super();
            bluetoothDeviceList = new ArrayList<>();
            deviceListLayoutInflater = activity.getLayoutInflater();
        }

        public void addDeviceList( ArrayList<BluetoothDevice> bluetoothDevices ){
            bluetoothDeviceList = bluetoothDevices;
            notifyDataSetChanged();    // ListViewの更新
        }

        // リストへの追加
        public void addDevice( BluetoothDevice device )
        {
            if( !bluetoothDeviceList.contains( device ) )
            {    // 加えられていなければ加える
                bluetoothDeviceList.add( device );
                notifyDataSetChanged();    // ListViewの更新
            }
        }

        // リストのクリア
        public void clear()
        {
            bluetoothDeviceList.clear();
            notifyDataSetChanged();    // ListViewの更新
        }

        @Override
        public int getCount()
        {
            return bluetoothDeviceList.size();
        }

        @Override
        public Object getItem( int position )
        {
            return bluetoothDeviceList.get( position );
        }

        @Override
        public long getItemId( int position )
        {
            return position;
        }

        static class ViewHolder
        {
            TextView deviceName;
            TextView deviceAddress;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent )
        {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if( null == convertView )
            {
                convertView = deviceListLayoutInflater.inflate( R.layout.bluetooth_device_listitem, parent, false );
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = convertView.findViewById( R.id.textView_deviceAddress);
                viewHolder.deviceName = convertView.findViewById( R.id.textView_deviceName);
                convertView.setTag( viewHolder );
            }
            else
            {
                viewHolder = (ViewHolder)convertView.getTag();
            }

            BluetoothDevice device     = bluetoothDeviceList.get( position );
            String          deviceName = device.getName();
            if( null != deviceName && 0 < deviceName.length() )
            {
                viewHolder.deviceName.setText( deviceName );
            }
            else
            {
                viewHolder.deviceName.setText( R.string.unknown_device);
            }
            viewHolder.deviceAddress.setText( device.getAddress() );

            return convertView;
        }
    }

    // 定数
    private final int REQUEST_PERMISSION = 1000;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1; // Bluetooth機能の有効化要求時の識別コード

    // メンバー変数
    private BluetoothConnection raspberryBluetoothConnection;
    private DeviceListAdapter deviceListAdapter;    // リストビューの内容

    private String deviceName;
    private String deviceAddress;

    final Handler handler = new Handler();
    final Runnable updateDeviceList = new Runnable() {
        @Override
        public void run()
        {
            if(raspberryBluetoothConnection.getBluetoothDevices() != null){
                deviceListAdapter.addDeviceList( raspberryBluetoothConnection.getBluetoothDevices() );
            }
            if(!raspberryBluetoothConnection.checkScanning()) {
                // リストの更新終了
                handler.removeCallbacks(this);
                return;
            }

            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        // タイトル設定
        setTitle(R.string.bluetooth_device_list_title);
        setContentView( R.layout.fragment_bluetooth_device_list);

        setActionBar((Toolbar) findViewById(R.id.toolbar_bluetooth));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // 戻り値の初期化
        setResult( Activity.RESULT_CANCELED );

        // リストビューの設定
        deviceListAdapter = new DeviceListAdapter( this ); // ビューアダプターの初期化
        ListView listView = findViewById( R.id.deviceList);    // リストビューの取得
        listView.setAdapter(deviceListAdapter);    // リストビューにビューアダプターをセット
        listView.setOnItemClickListener( this ); // クリックリスナーオブジェクトのセット

        // Bluetoothインスタンスの取得
        raspberryBluetoothConnection = BluetoothConnection.getBluetoothConnection();
    }

    // 初回表示時、および、ポーズからの復帰時
    @Override
    protected void onResume()
    {
        super.onResume();

        // デバイスのBluetooth機能の有効化要求
        requestBluetoothFeature();

        // 既に許可している
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){

            // スキャン開始
            startScan();
        }
        // 拒否していた場合
        else{
            requestLocationPermission();
        }
    }

    // 別のアクティビティ（か別のアプリ）に移行したことで、バックグラウンドに追いやられた時
    @Override
    protected void onPause()
    {
        super.onPause();

        // スキャンの停止
        stopScan();

        deviceListAdapter.clear();
    }

    // デバイスのBluetooth機能の有効化要求
    private void requestBluetoothFeature()
    {
        if( raspberryBluetoothConnection.checkBluetoothEnable() ) {
            return;
        }
        // デバイスのBluetooth機能が有効になっていないときは、有効化要求（ダイアログ表示）
        Intent enableBtIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
        startActivityForResult( enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
    }

    // 機能の有効化ダイアログの操作結果
    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
        switch( requestCode )
        {
            case REQUEST_ENABLE_BLUETOOTH: // Bluetooth有効化要求
                if( Activity.RESULT_CANCELED == resultCode )
                {    // 有効にされなかった
                    Toast.makeText( this, R.string.bluetooth_is_not_working, Toast.LENGTH_SHORT ).show();
                    finish();
                    return;
                }
                break;
        }
        super.onActivityResult( requestCode, resultCode, data );
    }

    // スキャンの開始
    private void startScan()
    {
        // リストビューの内容を空にする。
        deviceListAdapter.clear();

        raspberryBluetoothConnection.startDiscovery();

        // メニューの更新
        invalidateOptionsMenu();

        // リストの更新開始
        handler.post(updateDeviceList);
    }

    // スキャンの停止
    private void stopScan()
    {
        raspberryBluetoothConnection.stopDiscovery();

        // メニューの更新
        invalidateOptionsMenu();
    }

    // リストビューのアイテムクリック時の処理
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id )
    {
        /*
         * アイテムを選択したときに落ちるバグあり 時間あれば対応
         */

        // クリックされたアイテムの取得
        BluetoothDevice device = (BluetoothDevice) deviceListAdapter.getItem( position );
        if( null == device )
        {
            return;
        }

        deviceName = device.getName();
        deviceAddress = device.getAddress();

        BluetoothPairingConfirmDialog bluetoothPairingConfirmDialog = new BluetoothPairingConfirmDialog();
        bluetoothPairingConfirmDialog.show(getFragmentManager(), "bluetoothPairingConfirmDialog");
    }

    // オプションメニュー作成時の処理
    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        getMenuInflater().inflate( R.menu.bluetooth_device_list, menu );
        if( !raspberryBluetoothConnection.checkScanning())
        {
            menu.findItem( R.id.menuItem_stop ).setVisible( false );
            menu.findItem( R.id.menuItem_scan ).setVisible( true );
            menu.findItem( R.id.menuItem_progress ).setActionView( null );
        }
        else
        {
            menu.findItem( R.id.menuItem_stop ).setVisible( true );
            menu.findItem( R.id.menuItem_scan ).setVisible( false );
            menu.findItem( R.id.menuItem_progress ).setActionView( R.layout.actionbar_indeterminate_progress );
        }
        return true;
    }

    // オプションメニューのアイテム選択時の処理
    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        switch( item.getItemId() )
        {
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menuItem_scan:
                startScan();    // スキャンの開始
                break;
            case R.id.menuItem_stop:
                stopScan();    // スキャンの停止
                break;
        }
        return true;
    }

    // 許可を求める
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION);

        } else {
            Toast toast = Toast.makeText(this,
                    "許可されないとアプリが実行できません", Toast.LENGTH_SHORT);
            toast.show();

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,}, REQUEST_PERMISSION);

        }
    }

    // 結果の受け取り
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScan();

            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this,
                        "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    // 選択されたデバイスを保存する
    public void saveDevice(){
        // データを保存
        SharedPreferences data = getSharedPreferences("jegAppData", MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();

        editor.putString("raspberryName", deviceName);
        editor.putString("raspberryAddress", deviceAddress);
        editor.apply();

        Toast toast = Toast.makeText(this, R.string.bluetooth_pairing_confirm_done, Toast.LENGTH_SHORT);
        toast.show();

        finish();
    }

    // デバイス名getter
    public String getDeviceName(){
        return deviceName;
    }

    // デバイスアドレスgetter
    public String getDeviceAddress(){
        return deviceAddress;
    }
}
