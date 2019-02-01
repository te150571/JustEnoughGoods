package com.jeg.te.justenoughgoods.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import com.jeg.te.justenoughgoods.R;

import java.util.ArrayList;

public class BluetoothDeviceListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

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

        // Add to list.
        public void addDevice( BluetoothDevice device )
        {
            if( !bluetoothDeviceList.contains( device ) )
            {
                bluetoothDeviceList.add( device );
                notifyDataSetChanged();
            }
        }

        // Clear list.
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

        static class ViewHolder {
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
                convertView = deviceListLayoutInflater.inflate( R.layout.fragment_bluetooth_device_listitem, parent, false );
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

    // Constants
    private final int REQUEST_PERMISSION = 1000;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;

    // Member variable
    private BluetoothConnection raspberryBluetoothConnection;
    private DeviceListAdapter deviceListAdapter;

    // Device params.
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

        // Set title.
        setTitle(R.string.bluetooth_device_list_title);
        setContentView( R.layout.fragment_bluetooth_device_list);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_bluetooth));

        // ListView
        deviceListAdapter = new DeviceListAdapter( this );
        ListView listView = findViewById( R.id.deviceList);
        listView.setAdapter(deviceListAdapter);
        listView.setOnItemClickListener( this );

        // Get instance.
        raspberryBluetoothConnection = BluetoothConnection.getBluetoothConnection();
    }

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
            menu.findItem( R.id.menuItem_progress ).setActionView( R.layout.actionbar_progress);
        }
        return true;
    }

    // オプションメニューのアイテム選択時の処理
    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        switch( item.getItemId() )
        {
            case R.id.menuItem_scan:
                startScan();
                invalidateOptionsMenu();
                break;
            case R.id.menuItem_stop:
                stopScan();
                break;
        }
        return true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        requestBluetoothFeature();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){

            startScan();
        }
        else{
            requestLocationPermission();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        stopScan();

        deviceListAdapter.clear();
    }

    /**
     * Request to activate Bluetooth function of Android.
     */
    private void requestBluetoothFeature()
    {
        if( raspberryBluetoothConnection.checkBluetoothEnable() ) {
            return;
        }
        // If Bluetooth disable, show dialog.
        Intent enableBtIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
        startActivityForResult( enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
    }

    /**
     * Operation result of function enable dialog
     */
    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
        switch( requestCode )
        {
            case REQUEST_ENABLE_BLUETOOTH:
                if( Activity.RESULT_CANCELED == resultCode )
                {
                    Toast.makeText( this, R.string.bluetooth_is_not_working, Toast.LENGTH_SHORT ).show();
                    finish();
                    return;
                }
                break;
        }
        super.onActivityResult( requestCode, resultCode, data );
    }

    /**
     * Clicked ListView item.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id )
    {
        /*
         * アイテムを選択したときに落ちるバグあり 時間あれば対応
         */

        try {
            BluetoothDevice device = (BluetoothDevice) deviceListAdapter.getItem( position );
            if( null == device )
            {
                return;
            }

            deviceName = device.getName();
            deviceAddress = device.getAddress();

            new AlertDialog.Builder( this )
                    .setTitle( R.string.bluetooth_pairing_confirm_title )
                    .setMessage( getString(R.string.bluetooth_pairing_confirm_text, deviceName, deviceAddress) )
                    .setPositiveButton( R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            saveDevice();
                        }
                    })
                    .setNegativeButton( R.string.no, null)
                    .show();
        }
        catch (Exception e){
            Log.w("Bluetooth Registration", e.getMessage());
        }
    }

    /**
     * If permission is not permitted in this application
     */
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION);

        } else {
            Toast.makeText(this, R.string.bluetooth_permission_request, Toast.LENGTH_SHORT).show();

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,}, REQUEST_PERMISSION);
        }
    }

    /**
     * Permission dialog result.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScan();

            } else {
                Toast.makeText(this, R.string.bluetooth_permission_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Start scan.
     */
    private void startScan()
    {
        deviceListAdapter.clear(); // Clear device list.
        invalidateOptionsMenu(); // Refresh menu.

        raspberryBluetoothConnection.startDiscovery();

        handler.post(updateDeviceList);
    }

    /**
     * Stop scan.
     */
    private void stopScan()
    {
        invalidateOptionsMenu(); // Refresh menu.

        raspberryBluetoothConnection.stopDiscovery();
    }

    // Save selected device to application.
    public void saveDevice(){
        SharedPreferences data = getSharedPreferences("jegAppData", MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();

        editor.putString("raspberryName", deviceName);
        editor.putString("raspberryAddress", deviceAddress);
        editor.apply();

        Toast toast = Toast.makeText(this, R.string.bluetooth_pairing_confirm_done, Toast.LENGTH_SHORT);
        toast.show();

        finish();
    }

    // Getters
    public String getDeviceName(){
        return deviceName;
    }

    public String getDeviceAddress(){
        return deviceAddress;
    }
}
