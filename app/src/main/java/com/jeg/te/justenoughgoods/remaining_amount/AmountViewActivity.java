package com.jeg.te.justenoughgoods.remaining_amount;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.awesomebar.ActionItem;
import com.github.florent37.awesomebar.AwesomeBar;
import com.jeg.te.justenoughgoods.R;
import com.jeg.te.justenoughgoods.Slave;
import com.jeg.te.justenoughgoods.SlaveConfigurationActivity;
import com.jeg.te.justenoughgoods.bluetooth.BluetoothConnection;
import com.jeg.te.justenoughgoods.bluetooth.BluetoothDeviceListActivity;
import com.jeg.te.justenoughgoods.bluetooth.BluetoothPairingStartDialog;
import com.jeg.te.justenoughgoods.database.DbContract.SlavesTable;
import com.jeg.te.justenoughgoods.database.DbContract.MeasurementDataTable;
import com.jeg.te.justenoughgoods.database.DbOperation;

import java.util.ArrayList;

public class AmountViewActivity extends Activity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    // 定数
    private static final int REQUEST_ENABLE_BLUETOOTH = 1; // Bluetooth機能の有効化要求時の識別コード
    private static final int REQUEST_CONNECT_DEVICE = 2; // デバイス接続要求時の識別コード

    // Bluetooth関係
    private BluetoothConnection raspberryBluetoothConnection;

    // 子機データベース
    private DbOperation dbOperation = null;

    // リストビューの内容
    private SlavesRemainingAmountListAdapter slavesRemainingAmountListAdapter;
    ArrayList<String> lacks;

    private AwesomeBar toolbar_main;
    private DrawerLayout main_navigation;

    final Handler handler = new Handler();
    final Runnable updateCheck = new Runnable() {
        @Override
        public void run()
        {
            System.out.println("DEBUG HANDLER RUNNING.");
            raspberryBluetoothConnection.connect();
            if(raspberryBluetoothConnection.checkUpdatable()){
                System.out.println("DEBUG UPDATABLE.");
                handler.removeCallbacks(updateCheck);
                startUpdate();
            }
            else {
                System.out.println("DEBUG CHECK UPDATE.");
                checkUpdate();
            }

            handler.postDelayed(this, 3000);
        }
    };

    final Runnable updateWait = new Runnable() {
        @Override
        public void run(){
            if(raspberryBluetoothConnection.checkReceiving()){
                // データ受信待ち
                System.out.println("DEBUG RECEIVING.");
            }
            else {
                System.out.println("DEBUG UPDATE.");
                handler.removeCallbacks(updateWait);
                updateData();
                return;
            }

            handler.postDelayed(this, 3000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // タイトル設定
        setTitle(R.string.app_name);

        // Bluetoothインスタンス取得
        raspberryBluetoothConnection = BluetoothConnection.getBluetoothConnection();

        // Bluetoothにデバイスが対応しているか
        if(raspberryBluetoothConnection.checkBluetoothSupport() == null){
            Toast.makeText( this, R.string.bluetooth_is_not_supported, Toast.LENGTH_SHORT ).show();
        }

        // データベース
        dbOperation = DbOperation.getDbOperation();

        // メイン画面表示
        displayAmounts();
    }

//    // オプションメニュー作成時の処理
//    @Override
//    public boolean onCreateOptionsMenu( Menu menu )
//    {
//        getMenuInflater().inflate( R.menu.main_menu, menu );
//        return true;
//    }
//
//    // オプションメニューのアイテム選択時の処理
//    @Override
//    public boolean onOptionsItemSelected( MenuItem item )
//    {
//        switch( item.getItemId() )
//        {
//            case R.id.menu_item_raspberry_config:
//                Intent intent = new Intent(getApplication(), RaspberryConfigurationActivity.class);
//                startActivity(intent);
//                return true;
//            case R.id.menu_item_init_config:
//                insertTestData();
//                return true;
//            case R.id.menu_re:
//                dbOperation.initDatabase();
//                return true;
//        }
//        return false;
//    }

    // 子機のリストがタップされた
    @Override
    public void onItemClick( AdapterView<?> parent, View view, int position, long id ){
//        Intent slaveLogIntent = new Intent(getApplication(), LogViewActivity.class);
//        // タップされた項目のSIDを次のアクティビティへ
//        slaveLogIntent.putExtra("sid", (String) ((TextView) view.findViewById(R.id.textView_slaveId)).getText());
//        slaveLogIntent.putExtra("name", (String) ((TextView) view.findViewById(R.id.textView_slaveName)).getText());
//        startActivity(slaveLogIntent);
    }

    // 子機のリストがロングタップされた
    @Override
    public boolean onItemLongClick( AdapterView<?> parent, View view, int position, long id ){
        Intent slaveConfigIntent = new Intent(getApplication(), SlaveConfigurationActivity.class);
        // ロングタップされた項目のSIDを次のアクティビティへ
        slaveConfigIntent.putExtra("sid", (String) ((TextView) view.findViewById(R.id.textView_slaveId)).getText());
        startActivity(slaveConfigIntent);
        return true;
    }

    // 初回表示時、および、ポーズからの復帰時
    @Override
    protected void onResume()
    {
        // Android端末のBluetooth機能の有効化要求
        requestBluetoothFeature();

        if(raspberryBluetoothConnection.getRaspberryAddress().equals("")) {
            BluetoothPairingStartDialog bluetoothPairingStartDialog = new BluetoothPairingStartDialog();
            bluetoothPairingStartDialog.show(getFragmentManager(), "bluetoothPairingStartDialog");
        }

        super.onResume();

        handler.post(updateCheck);
    }

    // アプリがバックグラウンドへ
    @Override
    protected void onPause() {
        handler.removeCallbacks(updateCheck);

        if(dbOperation != null)
            dbOperation.closeConnection();

        super.onPause();

        // Bluetooth切断
        if(raspberryBluetoothConnection != null )
            raspberryBluetoothConnection.disconnect();
    }

    // アプリが終了する直前
    @Override
    protected void onDestroy() {
        handler.removeCallbacks(updateCheck);

        if(dbOperation != null)
            dbOperation.closeConnection();

        super.onDestroy();

        // Bluetooth切断
        if(raspberryBluetoothConnection != null )
            raspberryBluetoothConnection.disconnect();
    }

    // Android端末のBluetooth機能の有効化要求
    private void requestBluetoothFeature()
    {
        if( raspberryBluetoothConnection.checkBluetoothEnable() )
        {
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
                    return;
                }
                break;
            case REQUEST_CONNECT_DEVICE: // デバイス接続要求
                if( Activity.RESULT_OK == resultCode )
                {
                    // デバイスリストアクティビティからの情報の取得
//                    strDeviceName = data.getStringExtra( BluetoothDeviceListActivity.EXTRAS_DEVICE_NAME );
//                    mDeviceAddress = data.getStringExtra( BluetoothDeviceListActivity.EXTRAS_DEVICE_ADDRESS );
                }
                break;
        }
        super.onActivityResult( requestCode, resultCode, data );
    }

    // Bluetoothデバイスのペアリング選択アクティビティ
    public void showBluetoothDeviceListActivity(){
            Intent bluetoothDeviceListActivityIntent = new Intent(getApplication(), BluetoothDeviceListActivity.class);
            startActivityForResult(bluetoothDeviceListActivityIntent, REQUEST_CONNECT_DEVICE);
    }

    // データの更新を確認
    private void checkUpdate(){
        String[][] lastUpdate = dbOperation.selectData(
                false,
                MeasurementDataTable.TABLE_NAME,
                null,
                new String[]{"max(" + MeasurementDataTable.DATETIME + ")"},
                null,
                null,
                null,
                null,
                null,
                null
                );
        if(lastUpdate[0][0] == null)
            raspberryBluetoothConnection.write("10");
        else
            raspberryBluetoothConnection.write(lastUpdate[0][0]);
    }

    // データの更新を行う
    private void updateData(){
        System.out.println("DEBUG UPDATING.");
        ArrayList<String> update = raspberryBluetoothConnection.getUpdateData();

        if(update.size() > 1) {
            System.out.println("DEBUG UPDATE RAW : " + update.get(0));
            for (String data : update) {
                if(!data.equals("1")) {
                    System.out.println("DEBUG UPDATE RAW : " + data.equals("1"));
                    String[] splitData = data.split(",", 0);
                    System.out.println("DEBUG UPDATE RAW : " + data);
                    System.out.println("DEBUG UPDATE DATA SPLIT : " + splitData[0] + splitData[1] + splitData[2] + splitData[3]);
                    dbOperation.insertData(
                            MeasurementDataTable.TABLE_NAME,
                            new String[]{MeasurementDataTable.S_ID, MeasurementDataTable.AMOUNT, MeasurementDataTable.DATETIME, MeasurementDataTable.MONTH_NUM},
                            new String[]{splitData[0], splitData[1], splitData[2], splitData[3]},
                            new String[]{"string", "double", "long", "int"}
                    );
                }
            }

            // 子機IDの照合をして新規を検出
            String[][] havingSlavesSId = dbOperation.selectData(false, SlavesTable.TABLE_NAME, null, new String[]{SlavesTable.S_ID}, null, null, null, null, null, null);
            String[][] receivedSlavesSId = dbOperation.selectData(true, MeasurementDataTable.TABLE_NAME, null, new String[]{MeasurementDataTable.S_ID}, null, null, null, null, null, null);

            // 二次元配列を１次元に
            ArrayList<String> _havingSlavesSId = new ArrayList<>();
            for (String[] tmp : havingSlavesSId) {
                _havingSlavesSId.add(tmp[0]);
            }
            ArrayList<String> _receivedSlavesSId = new ArrayList<>();
            for (String[] tmp : receivedSlavesSId) {
                _receivedSlavesSId.add(tmp[0]);
            }

            // 比較
            ArrayList<String> newSIds = new ArrayList<>();
            for (String sid : _receivedSlavesSId) {
                if (!_havingSlavesSId.contains(sid)) {
                    newSIds.add(sid);
                }
            }

            // 新しく検出した子機を登録する
            if (newSIds.size() != 0)
                registrationNewSlaves(newSIds);

            Toast.makeText(this, R.string.amount_finish_update, Toast.LENGTH_SHORT).show();
        }

        handler.post(updateCheck); // アップデート確認を開始
    }

    // 子機の新規登録
    public void registrationNewSlaves(ArrayList<String> newSlaveSIds){
        // とりあえず登録
        for(String sid : newSlaveSIds){
            dbOperation.insertData(
                    SlavesTable.TABLE_NAME,
                    new String[]{SlavesTable.S_ID, SlavesTable.NAME, SlavesTable.NOTIFICATION_AMOUNT},
                    new String[]{sid, "子機" + sid, "0.01"},
                    new String[]{"string", "string", "double"}
            );
        }

        getAndSetSlavesAmountData();
    }

    // アップデートを開始する
    private void startUpdate(){
        Toast.makeText( this, R.string.amount_start_update, Toast.LENGTH_SHORT ).show();
        handler.post(updateWait); // アップデート待機を登録
    }

    // 画面表示
    private void displayAmounts(){
        setContentView(R.layout.activity_main);
//        setActionBar((Toolbar) findViewById(R.id.toolbar_main));

        toolbar_main = findViewById(R.id.toolbar_main);
        main_navigation = findViewById(R.id.main_drawer);

        toolbar_main.addAction(R.drawable.awsb_ic_edit_animated, "Compose");

        toolbar_main.setActionItemClickListener(new AwesomeBar.ActionItemClickListener() {
            @Override
            public void onActionItemClicked(int position, ActionItem actionItem) {
                Toast.makeText(getBaseContext(), actionItem.getText()+" clicked", Toast.LENGTH_LONG).show();
            }
        });

        toolbar_main.setOnMenuClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_navigation.openDrawer(Gravity.START);
            }
        });

//        toolbar_main.addOverflowItem("overflow 1");
//        toolbar_main.addOverflowItem("overflow 2");
//
//        toolbar_main.setOverflowActionItemClickListener(new AwesomeBar.OverflowActionItemClickListener() {
//            @Override
//            public void onOverflowActionItemClicked(int position, String item) {
//
//            }
//        });

        toolbar_main.displayHomeAsUpEnabled(true);

        // Set a navigation view.
        this.setNavigationView();

        // GUIアイテム設定
        // リストビューの設定
        slavesRemainingAmountListAdapter = new SlavesRemainingAmountListAdapter( this ); // ビューアダプターの初期化
        ListView listView = findViewById( R.id.listView_slavesAmount);    // リストビューの取得
        listView.setAdapter(slavesRemainingAmountListAdapter); // リストビューにビューアダプターをセット
        listView.setOnItemClickListener( this ); // クリックリスナーオブジェクトのセット
        listView.setOnItemLongClickListener( this ); // ロングクリックリスナーオブジェクトのセット

        getAndSetSlavesAmountData();
    }

    /**
     * Set a navigation view.
     */
    private void setNavigationView() {
        NavigationView navigationView = findViewById(R.id.left_drawer);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        return true;
                    }
                }
        );
    }

    // データの取得と表示
    private void getAndSetSlavesAmountData() {
        slavesRemainingAmountListAdapter.clearSlaves();

        // データベースから取得しSlavesクラスへ
        // SELECT
        String[] projection = { // SELECT する列
                "s." + SlavesTable.S_ID,
                "s." + SlavesTable.NAME,
                "s." + SlavesTable.NOTIFICATION_AMOUNT,
                "s." + SlavesTable.AMOUNT_NOTIFICATION_ENABLE,
                "s." + SlavesTable.EXCEPTION_FLAG,
                "s." + SlavesTable.EXCEPTION_NOTIFICATION_ENABLE,
                "m." + MeasurementDataTable.AMOUNT,
                "m." + MeasurementDataTable.DATETIME
        };
        // JOIN
        String tableJoin = "as s LEFT JOIN (" +
                "SELECT * FROM "+ MeasurementDataTable.TABLE_NAME + " a " +
                "WHERE NOT EXISTS ( SELECT 1 FROM " + MeasurementDataTable.TABLE_NAME + " b WHERE a.s_id = b.s_id AND a.datetime < b.datetime ) ) as m ON s.s_id = m.s_id";
        String sortOrder = "s." + SlavesTable.S_ID + " ASC"; // ORDER

        String[][] selectResult = dbOperation.selectData(
                false,
                SlavesTable.TABLE_NAME,
                tableJoin,
                projection,
                null,
                null,
                null,
                null,
                sortOrder,
                null
        );

        ArrayList<Slave> testSlaves = new ArrayList<>();
        for(int row=0; row<selectResult.length; row++){
            Slave slave = new Slave();
            slave.setSId( selectResult[row][0] );
            slave.setName( selectResult[row][1] );
            slave.setAmount( Double.valueOf(selectResult[row][6]) );
            slave.setNotificationAmount( Double.valueOf(selectResult[row][2]) );
            slave.setLastUpdate( Long.valueOf(selectResult[row][7]) );
            testSlaves.add(slave);
        }

        for (Slave slave : testSlaves){
            slavesRemainingAmountListAdapter.addSlaves(slave);
        }

        // 不足を通知
        lacks = slavesRemainingAmountListAdapter.getLackList();
        if(lacks.size() > 0){
            AmountLackNotificationDialog amountLackNotificationDialog = new AmountLackNotificationDialog();
            amountLackNotificationDialog.show(getFragmentManager(), "amountLackNotificationDialog");
        }
    }

    // 不足リストを取得
    public ArrayList<String> getLacks(){
        return lacks;
    }

    private void insertTestData(){
        // INSERT
        // 子機設定データ
        dbOperation.insertData(
                SlavesTable.TABLE_NAME,
                new String[]{SlavesTable.S_ID, SlavesTable.NAME, SlavesTable.NOTIFICATION_AMOUNT},
                new String[]{"ID00001A", "醤油", "0.2"},
                new String[]{"string", "string", "double"}
        );

        dbOperation.insertData(
                SlavesTable.TABLE_NAME,
                new String[]{SlavesTable.S_ID, SlavesTable.NAME, SlavesTable.NOTIFICATION_AMOUNT},
                new String[]{"ID00002A", "酢", "0.1"},
                new String[]{"string", "string", "double"}
        );

        dbOperation.insertData(
                SlavesTable.TABLE_NAME,
                new String[]{SlavesTable.S_ID, SlavesTable.NAME, SlavesTable.NOTIFICATION_AMOUNT},
                new String[]{"ID00003A", "サラダ油", "0.5"},
                new String[]{"string", "string", "double"}
        );

        dbOperation.insertData(
                SlavesTable.TABLE_NAME,
                new String[]{SlavesTable.S_ID, SlavesTable.NAME, SlavesTable.NOTIFICATION_AMOUNT},
                new String[]{"ID00004A", "シャンプー", "0.5"},
                new String[]{"string", "string", "double"}
        );

        // 計測データ
        // 現在日時を取得
        long nowTime = System.currentTimeMillis();

        dbOperation.insertData(
                MeasurementDataTable.TABLE_NAME,
                new String[]{MeasurementDataTable.S_ID, MeasurementDataTable.AMOUNT, MeasurementDataTable.DATETIME, MeasurementDataTable.MONTH_NUM},
                new String[]{"ID00001A", "0.524", String.valueOf(nowTime - 86400000 * 3), "1"},
                new String[]{"string", "double", "long", "int"}
        );

        dbOperation.insertData(
                MeasurementDataTable.TABLE_NAME,
                new String[]{MeasurementDataTable.S_ID, MeasurementDataTable.AMOUNT, MeasurementDataTable.DATETIME, MeasurementDataTable.MONTH_NUM},
                new String[]{"ID00001A", "0.449", String.valueOf(nowTime - 86400000 * 2), "1"},
                new String[]{"string", "double", "long", "int"}
        );

        dbOperation.insertData(
                MeasurementDataTable.TABLE_NAME,
                new String[]{MeasurementDataTable.S_ID, MeasurementDataTable.AMOUNT, MeasurementDataTable.DATETIME, MeasurementDataTable.MONTH_NUM},
                new String[]{"ID00001A", "0.403", String.valueOf(nowTime - 86400000), "1"},
                new String[]{"string", "double", "long", "int"}
        );

        dbOperation.insertData(
                MeasurementDataTable.TABLE_NAME,
                new String[]{MeasurementDataTable.S_ID, MeasurementDataTable.AMOUNT, MeasurementDataTable.DATETIME, MeasurementDataTable.MONTH_NUM},
                new String[]{"ID00002A", "0.32345", String.valueOf(nowTime - 86400000), "1"},
                new String[]{"string", "double", "long", "int"}
        );

        dbOperation.insertData(
                MeasurementDataTable.TABLE_NAME,
                new String[]{MeasurementDataTable.S_ID, MeasurementDataTable.AMOUNT, MeasurementDataTable.DATETIME, MeasurementDataTable.MONTH_NUM},
                new String[]{"ID00003A", "2.0", String.valueOf(nowTime - 86400000), "1"},
                new String[]{"string", "double", "long", "int"}
        );

        dbOperation.insertData(
                MeasurementDataTable.TABLE_NAME,
                new String[]{MeasurementDataTable.S_ID, MeasurementDataTable.AMOUNT, MeasurementDataTable.DATETIME, MeasurementDataTable.MONTH_NUM},
                new String[]{"ID00004A", "0.45", String.valueOf(nowTime - 86400000), "1"},
                new String[]{"string", "double", "long", "int"}
        );
    }

    private void checkDB(){
        // DEBUG
        String[][] debugSlaves = dbOperation.selectData(
                false,
                SlavesTable.TABLE_NAME,
                null,
                new String[]{SlavesTable.S_ID, SlavesTable.NAME},
                null,
                null,
                null,
                null,
                null,
                null
        );
        for(int row=0; row<debugSlaves.length; row++){
            Log.d("SLAVES SID", debugSlaves[row][0] );
            Log.d("SLAVES NAME", debugSlaves[row][1] );
        }

        String[] pro = {
                MeasurementDataTable._ID,
                MeasurementDataTable.S_ID,
                MeasurementDataTable.AMOUNT,
                MeasurementDataTable.DATETIME
        };

        String[][] debug = dbOperation.selectData(
                false,
                MeasurementDataTable.TABLE_NAME,
                null,
                pro,
                null,
                null,
                null,
                null,
                null,
                null
        );

        for(int row=0; row<debug.length; row++){
            Log.d("M_DATA ID", debug[row][0] );
            Log.d("M_DATA SID", debug[row][1] );
            Log.d("M_DATA AMOUNT", debug[row][2] );
            Log.d("M_DATA DATETIME", debug[row][3] );
        }
    }
}



