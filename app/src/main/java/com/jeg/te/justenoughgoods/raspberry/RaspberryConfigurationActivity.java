package com.jeg.te.justenoughgoods.raspberry;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.jeg.te.justenoughgoods.MyApplication;
import com.jeg.te.justenoughgoods.R;
import com.jeg.te.justenoughgoods.bluetooth.BluetoothDeviceListActivity;

public class RaspberryConfigurationActivity extends Activity implements View.OnClickListener {

    // 定数
    private static final int REQUEST_CONNECT_DEVICE = 2; // デバイス接続要求時の識別コード

    // GUIアイテム
    private Button btRaspberryDeviceSearch;
    private Button btRaspberryDeviceDelete;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        // タイトル設定
        setTitle(R.string.app_name_raspberry_config);

        setContentView( R.layout.fragment_raspberry_configuration);
        setActionBar((Toolbar) findViewById(R.id.toolbar_raspberry));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        setDeviceInfo();

        // GUIアイテム
        btRaspberryDeviceSearch = findViewById(R.id.bt_raspberryDeviceSearch);
        btRaspberryDeviceSearch.setOnClickListener(this);
        btRaspberryDeviceDelete = findViewById(R.id.bt_raspberryDeviceDelete);
        btRaspberryDeviceDelete.setOnClickListener(this);
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
        }
        return false;
    }

    // 初回表示時、および、ポーズからの復帰時
    @Override
    protected void onResume()
    {
        setDeviceInfo();

        super.onResume();
    }

    // 別のアクティビティ（か別のアプリ）に移行したことで、バックグラウンドに追いやられた時
    @Override
    protected void onPause()
    {
        super.onPause();
    }

    // アクティビティの終了直前
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onClick( View v )
    {
        if( btRaspberryDeviceSearch.getId() == v.getId() ){
            Intent bluetoothDeviceListActivityIntent = new Intent(getApplication(), BluetoothDeviceListActivity.class);
            startActivityForResult(bluetoothDeviceListActivityIntent, REQUEST_CONNECT_DEVICE);
            return;
        }
        else if( btRaspberryDeviceDelete.getId() == v.getId()){
            RaspberryDeviceDeleteConfirmDialog raspberryDeviceDeleteConfirmDialog = new RaspberryDeviceDeleteConfirmDialog();
            raspberryDeviceDeleteConfirmDialog.show(getFragmentManager(), "raspberryDeviceDeleteConfirmDialog");
        }
    }

    // 親機情報取得して表示
    private void setDeviceInfo(){
        TextView textViewDeviceName = findViewById(R.id.textView_deviceName);
        TextView textViewDeviceAddress = findViewById(R.id.textView_deviceAddress);

        SharedPreferences data = MyApplication.getContext().getSharedPreferences("jegAppData", MODE_PRIVATE);
        textViewDeviceName.setText(data.getString("raspberryName", ""));
        textViewDeviceAddress.setText(data.getString("raspberryAddress", ""));
    }

    // 親機情報を初期化
    public void clearDevice(){
        SharedPreferences data = MyApplication.getContext().getSharedPreferences("jegAppData", MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putString("raspberryName", "");
        editor.putString("raspberryAddress", "");
        editor.apply();

        // 表示を初期化
        TextView textViewDeviceName = findViewById(R.id.textView_deviceName);
        TextView textViewDeviceAddress = findViewById(R.id.textView_deviceAddress);
        textViewDeviceName.setText("");
        textViewDeviceAddress.setText("");

        Toast toast = Toast.makeText(this, R.string.raspberry_device_delete_done, Toast.LENGTH_SHORT);
        toast.show();
    }
}
