package com.jeg.te.justenoughgoods.main;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.awesomebar.ActionItem;
import com.github.florent37.awesomebar.AwesomeBar;
import com.jeg.te.justenoughgoods.R;
import com.jeg.te.justenoughgoods.bluetooth.BluetoothConnection;
import com.jeg.te.justenoughgoods.bluetooth.BluetoothDeviceListActivity;
import com.jeg.te.justenoughgoods.database.DbContract;
import com.jeg.te.justenoughgoods.database.DbOperation;
import com.jeg.te.justenoughgoods.log.FragmentMonthlyLog;
import com.jeg.te.justenoughgoods.log.FragmentYearlyLog;
import com.jeg.te.justenoughgoods.raspberry.FragmentRaspberryConfiguration;
import com.jeg.te.justenoughgoods.remaining_amount.FragmentRemainingAmount;
import com.jeg.te.justenoughgoods.slave_configuration.FragmentSlaveConfiguration;
import com.jeg.te.justenoughgoods.slave_list.FragmentSlaveList;
import com.jeg.te.justenoughgoods.database.DbOperationForSlaveData;

import java.util.ArrayList;

public class ActivityMain extends AppCompatActivity {
    // Constants
    private static final int REQUEST_ENABLE_BLUETOOTH = 1; // Bluetooth機能の有効化要求時の識別コード

    // Bluetooth connector
    private BluetoothConnection raspberryBluetoothConnection = null;

    // Slaves Database operator
    private DbOperation dbOperation = null;
    private DbOperationForSlaveData dbOperationForSlaveData;

    //Thread instances.
//    private UpdateChecker updateChecker = null;
//    private UpdateReceiveWaiter updateReceiveWaiter = null;

    // Thread active flags.
    private boolean updateCheckerIsActive = false;
    private boolean updateReceiveWaiterIsActive = false;

    // GUIs
    private AwesomeBar toolbar_main;
    private DrawerLayout main_navigation;

    private TextView navigationHeaderSlavesCountAllValue;
    private TextView navigationHeaderSlavesCountSoonValue;
    private TextView navigationHeaderSlavesCountLackValue;

    // Fragments
    private FragmentHome fragmentHome;
    private FragmentRemainingAmount fragmentRemainingAmount;
    private FragmentMonthlyLog fragmentMonthlyLog;
    private FragmentYearlyLog fragmentYearlyLog;
    private FragmentSlaveList fragmentSlaveList;
    private FragmentSlaveConfiguration fragmentSlaveConfiguration;
    private FragmentRaspberryConfiguration fragmentRaspberryConfiguration;

    // Displaying Fragment flag.
    private boolean fragmentRemainingAmountIsDisplay = false;

    // Inner thread class
    private final Handler handler = new Handler();

    /**
     * Confirm whether there is an update every 10 seconds
     */
    private final Runnable updateChecker = new Runnable() {
        @Override
        public void run() {
            if(raspberryBluetoothConnection != null){
                Log.d("UpdateChecker", "Running");

                raspberryBluetoothConnection.connect(); // Connect to Raspberry Pi with Bluetooth.
                if(raspberryBluetoothConnection.checkUpdatable()){
                    Log.d("UpdateChecker", "Start UpdateReceiveWaiter.");
                    startUpdateWaiting();
                    return;
                }
                else {
                    checkUpdate();
                }
            }

            handler.postDelayed(this, 10000);
        }
    };

    /**
     * Called when there is an update, check reception status every 3 seconds.
     */
    private final Runnable updateReceiveWaiter = new Runnable() {
        @Override
        public void run() {
            if (raspberryBluetoothConnection != null) {
                if (!raspberryBluetoothConnection.checkReceiving()) {
                    Log.d("UpdateReceiveWaiter", "Start Update.");
                    getUpdateAndInsert();
                    return;
                }
            }

            handler.postDelayed(this, 3000);
        }
    };

    // 遺物
//    class UpdateChecker extends Thread {
//        public void run(){
//            try{
//                if(raspberryBluetoothConnection != null){
//                    updateCheckerIsActive = true;
//
//                    while(updateCheckerIsActive){
//                        Log.d("UpdateChecker", "Running");
//                        raspberryBluetoothConnection.connect(); // Connect to Raspberry Pi with Bluetooth.
//                        if(raspberryBluetoothConnection.checkUpdatable()){
//                            Log.d("UpdateChecker", "Start UpdateReceiveWaiter.");
//                            startUpdateWaiting();
//                        }
//                        else {
//                            checkUpdate();
//                        }
//                        Thread.sleep(10000);
//                    }
//
//                }
//            } catch (InterruptedException e){
//                Log.w("InterruptedException", "Got throw InterruptedException in UpdateChecker. \n" + e.getMessage());
//            }
//        }
//    }

//    class UpdateReceiveWaiter extends Thread {
//        public void run() {
//            try {
//                if (raspberryBluetoothConnection != null) {
//                    updateReceiveWaiterIsActive = true;
//
//                    while(updateReceiveWaiterIsActive){
//                        if (!raspberryBluetoothConnection.checkReceiving()) {
//                            Log.d("UpdateReceiveWaiter", "Start Update.");
//                            getUpdateAndInsert();
//                        }
//                        Thread.sleep(3000);
//                    }
//
//                }
//            } catch (InterruptedException e) {
//                Log.w("InterruptedException", "Got throw InterruptedException in UpdateReceiveWaiter. \n" + e.getMessage());
//            }
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Get instances.
        raspberryBluetoothConnection = BluetoothConnection.getBluetoothConnection();
        dbOperation = DbOperation.getDbOperation();
        dbOperationForSlaveData = new DbOperationForSlaveData();

        // Set main view.
        setContentView(R.layout.activity_main);

        toolbar_main = findViewById(R.id.toolbar_main);
        main_navigation = findViewById(R.id.main_drawer);

        toolbar_main.setOnMenuClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_navigation.openDrawer(Gravity.START);
            }
        });

        toolbar_main.displayHomeAsUpEnabled(false);

        // Set a navigation view.
        NavigationView navigationView = findViewById(R.id.left_drawer);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch(menuItem.getItemId()){
                            case R.id.nav_home:
                                createFragmentHomeView();
                                break;
                            case R.id.nav_remaining_amount_list:
                                createFragmentRemainingAmount();
                                break;
                            case R.id.nav_slave_list:
                                createFragmentSlaveList();
                                break;
                            case R.id.menu_item_raspberry_config:
                                createFragmentRaspberryConfiguration();
                                break;

                            // DEBUG MENU
                            case R.id.menu_debug_select_data:
                                checkDbData();
                                break;
                            case R.id.menu_debug_insert_data:
                                insertTestData();
                                if(fragmentRemainingAmountIsDisplay) fragmentRemainingAmount.getAndSetSlavesRemainingAmountData();
                                break;
                            case R.id.menu_debug_init_database:
                                dbOperation.initDatabase();
                                break;
                        }

                        main_navigation.closeDrawer(Gravity.START); // Close navigation.
                        return true;
                    }
                }
        );

        navigationHeaderSlavesCountAllValue = navigationView.getHeaderView(0).findViewById(R.id.textView_slavesCountAllValue);
        navigationHeaderSlavesCountSoonValue = navigationView.getHeaderView(0).findViewById(R.id.textView_slavesCountSoonValue);
        navigationHeaderSlavesCountLackValue = navigationView.getHeaderView(0).findViewById(R.id.textView_slavesCountLackValue);

        // Create fragment.
        fragmentHome = new FragmentHome();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_contents, fragmentHome);
        transaction.commit();
    }

    @Override
    public void onResume(){
        super.onResume();

        requestBluetoothFeature();

        //If the Bluetooth address of the Raspberry pi is not registered, show dialog.
        if(raspberryBluetoothConnection.getRaspberryAddress().equals("")) {
            new AlertDialog.Builder( this )
                    .setTitle( R.string.bluetooth_pairing_start_title )
                    .setMessage( R.string.bluetooth_pairing_start_text )
                    .setPositiveButton( R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            createFragmentRaspberryConfiguration();
                        }
                    })
                    .setNegativeButton( R.string.no, null)
                    .show();
        }

        navigationHeaderSlavesCountAllValue.setText(getString(R.string.nav_header_slaves_count_all_value, dbOperationForSlaveData.getSlaveListCountAll()));
        navigationHeaderSlavesCountSoonValue.setText(getString(R.string.nav_header_slaves_count_soon_value, 0));
        navigationHeaderSlavesCountLackValue.setText(getString(R.string.nav_header_slaves_count_lack_value, dbOperationForSlaveData.getSlaveCountLack()));

        // Get Data and display in SlavesRemainingAmount.
//        if(fragmentRemainingAmountIsDisplay) fragmentRemainingAmount.getAndSetSlavesRemainingAmountData();

        checkLackingSlaveAndShowActionButton();

        // Start UpdateChecker.
        handler.post(updateChecker);
//        if(updateChecker == null) {
//            updateChecker = new UpdateChecker();
//            updateChecker.start();
//        }
    }

    @Override
    public void onPause(){
        // Stop UpdateChecker.
        handler.removeCallbacks(updateChecker);
//        updateCheckerIsActive = false;
//        updateChecker = null;

        if(raspberryBluetoothConnection != null)
            raspberryBluetoothConnection.disconnect(); // Bluetooth disconnect.

        super.onPause();
    }

    @Override
    public void onDestroy(){
        if(dbOperation != null)
            dbOperation.closeConnection(); // Database disconnect.

        super.onDestroy();
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
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        switch( requestCode ) {
            case REQUEST_ENABLE_BLUETOOTH:
                if( AppCompatActivity.RESULT_CANCELED == resultCode ) {
                    Toast.makeText( this, R.string.bluetooth_is_not_working, Toast.LENGTH_SHORT ).show();
                    return;
                }
                break;
        }
        super.onActivityResult( requestCode, resultCode, data );
    }

    // Action Button.
    public void checkLackingSlaveAndShowActionButton(){
        toolbar_main.clearActions();

        if(dbOperationForSlaveData.getSlaveCountLack() > 0){
            toolbar_main.addAction(R.drawable.lack_icon, "不足あり");
            toolbar_main.setActionItemClickListener(new AwesomeBar.ActionItemClickListener() {
                @Override
                public void onActionItemClicked(int position, ActionItem actionItem) {
                    createFragmentRemainingAmount();

                    toolbar_main.clearActions();
                }
            });
        }
    }

    // Fragments creation methods.
    /**
     * Create Main view.
     */
    public void createFragmentHomeView(){
        fragmentHome = new FragmentHome();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_contents, fragmentHome);
        transaction.commit();
    }

    /**
     * Create Remaining Amount view.
     */
    public void createFragmentRemainingAmount(){
        fragmentRemainingAmount = new FragmentRemainingAmount();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_contents, fragmentRemainingAmount);
        transaction.commit();
    }

    /**
     * Create Log (Monthly) view.
     */
    public void createFragmentMonthlyLog(String sId, String name){
        fragmentMonthlyLog = new FragmentMonthlyLog();

        Bundle args = new Bundle();
        args.putString("sid", sId);
        args.putString("name", name);
        fragmentMonthlyLog.setArguments(args);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_contents, fragmentMonthlyLog);
        transaction.commit();
    }

    /**
     * Create Log (Monthly) view.
     */
    public void createFragmentYearlyLog(String sId, String name){
        fragmentYearlyLog = new FragmentYearlyLog();

        Bundle args = new Bundle();
        args.putString("sid", sId);
        args.putString("name", name);
        fragmentYearlyLog.setArguments(args);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_contents, fragmentYearlyLog);
        transaction.commit();
    }

    /**
     * Create slave list view.
     */
    public void createFragmentSlaveList(){
        fragmentSlaveList = new FragmentSlaveList();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_contents, fragmentSlaveList);
        transaction.commit();
    }

    /**
     * Create slave configuration view.
     */
    public void createFragmentSlaveConfiguration(String sId, String callFrom){
        fragmentSlaveConfiguration = new FragmentSlaveConfiguration();
        Bundle args = new Bundle();
        args.putString("sid", sId);
        args.putString("callFrom", callFrom);
        fragmentSlaveConfiguration.setArguments(args);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_contents, fragmentSlaveConfiguration);
        transaction.commit();
    }

    /**
     * Create Raspberry Pi configuration view.
     */
    public void createFragmentRaspberryConfiguration(){
        fragmentRaspberryConfiguration = new FragmentRaspberryConfiguration();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_contents, fragmentRaspberryConfiguration);
        transaction.commit();
    }

    /**
     * Create BluetoothDeviceList Activity.
     */
    public void transitionBluetoothDeviceListActivity(){
        Intent bluetoothDeviceListActivityIntent = new Intent(getApplication(), BluetoothDeviceListActivity.class);
        startActivity(bluetoothDeviceListActivityIntent);
    }

    // Slaves data operation methods.
    /**
     * Get last update datetime form MeasurementDataTable and send it.
     */
    private void checkUpdate(){
        String datetimeOfLastUpdate = dbOperationForSlaveData.getDatetimeOfLastUpdate();
        if(datetimeOfLastUpdate == null) {
            raspberryBluetoothConnection.write("10"); // If Data is empty, send 10.
        }
        else {
            raspberryBluetoothConnection.write(datetimeOfLastUpdate);
        }
    }

    /**
     * Start UpdateReceiveWaiter and stop UpdateChecker.
     */
    private void startUpdateWaiting(){
        // Stop UpdateChecker.
        handler.removeCallbacks(updateChecker);
//        updateCheckerIsActive = false;
//        updateChecker = null;

        // Start UpdateReceiveWaiter.
        handler.post(updateReceiveWaiter);
//        if(updateReceiveWaiter == null) {
//            updateReceiveWaiter = new UpdateReceiveWaiter();
//            updateReceiveWaiter.start();
//        }

        Toast.makeText( this, R.string.amount_start_update, Toast.LENGTH_SHORT ).show();
    }

    /**
     * Start updating.
     */
    private void getUpdateAndInsert() {
        handler.removeCallbacks(updateReceiveWaiter);
//        updateReceiveWaiterIsActive = false;
//        updateReceiveWaiter = null;

        // Get Data.
        ArrayList<String> update = raspberryBluetoothConnection.getUpdateData();

        if (update.size() > 1) {
            dbOperationForSlaveData.putMeasurementData(update);

            // Detection new SID.
            ArrayList<String> _havingSlavesSId = dbOperationForSlaveData.getSIdInSlavesTable();
            ArrayList<String> _receivedSlavesSId = dbOperationForSlaveData.getSIdInMeasurementTable();

            // Compare.
            ArrayList<String> newSIds = new ArrayList<>();
            for (String sid : _receivedSlavesSId) {
                if (!_havingSlavesSId.contains(sid)) {
                    newSIds.add(sid);
                }
            }

            // Register detected new SID.
            if (newSIds.size() != 0)
                dbOperationForSlaveData.putNewSlave(newSIds);
        }

        Toast.makeText( this, R.string.amount_finish_update, Toast.LENGTH_SHORT ).show();

        // Refresh remaining data.
//        if(fragmentRemainingAmountIsDisplay) fragmentRemainingAmount.getAndSetSlavesRemainingAmountData();

        // Start UpdateChecker again.
        handler.post(updateChecker);
//        if(updateChecker == null) {
//            updateChecker = new UpdateChecker();
//            updateChecker.start();
//        }
    }

    public ArrayList<String> getNoticesText(){
        ArrayList<String> notices = new ArrayList<>();

        if(dbOperationForSlaveData.getSlaveCountLack() > 0){
            notices.add(getString(R.string.home_notice_lacking));
        }
        if(dbOperationForSlaveData.getSlaveCountNew() > 0){
            notices.add(getString(R.string.home_notice_Unregistered));
        }
        if(raspberryBluetoothConnection.getRaspberryAddress().equals("")){
            notices.add(getString(R.string.home_notice_not_ras));
        }

        return notices;
    }


    // Debug methods.
    private void insertTestData(){
        // Insert slaves data.
        dbOperation.insertData(
                DbContract.SlavesTable.TABLE_NAME,
                new String[]{DbContract.SlavesTable.S_ID, DbContract.SlavesTable.NAME, DbContract.SlavesTable.NOTIFICATION_AMOUNT, DbContract.SlavesTable.IS_NEW},
                new String[]{"ID00001A", "醤油", "0.2", "0"},
                new String[]{"string", "string", "double", "int"}
        );

        dbOperation.insertData(
                DbContract.SlavesTable.TABLE_NAME,
                new String[]{DbContract.SlavesTable.S_ID, DbContract.SlavesTable.NAME, DbContract.SlavesTable.NOTIFICATION_AMOUNT, DbContract.SlavesTable.IS_NEW},
                new String[]{"ID00002A", "酢", "0.1", "0"},
                new String[]{"string", "string", "double", "int"}
        );

        dbOperation.insertData(
                DbContract.SlavesTable.TABLE_NAME,
                new String[]{DbContract.SlavesTable.S_ID, DbContract.SlavesTable.NAME, DbContract.SlavesTable.NOTIFICATION_AMOUNT},
                new String[]{"ID00003A", "サラダ油", "0.5"},
                new String[]{"string", "string", "double"}
        );

        dbOperation.insertData(
                DbContract.SlavesTable.TABLE_NAME,
                new String[]{DbContract.SlavesTable.S_ID, DbContract.SlavesTable.NAME, DbContract.SlavesTable.NOTIFICATION_AMOUNT},
                new String[]{"ID00004A", "シャンプー", "0.5"},
                new String[]{"string", "string", "double"}
        );

        // Insert measurement data.
        // Get datetime in milliseconds.
        long nowTime = System.currentTimeMillis();

        dbOperation.insertData(
                DbContract.MeasurementDataTable.TABLE_NAME,
                new String[]{DbContract.MeasurementDataTable.S_ID, DbContract.MeasurementDataTable.AMOUNT, DbContract.MeasurementDataTable.DATETIME, DbContract.MeasurementDataTable.MONTH_NUM},
                new String[]{"ID00001A", "0.524", String.valueOf(nowTime - 86400000 * 3), "1"},
                new String[]{"string", "double", "long", "int"}
        );

        dbOperation.insertData(
                DbContract.MeasurementDataTable.TABLE_NAME,
                new String[]{DbContract.MeasurementDataTable.S_ID, DbContract.MeasurementDataTable.AMOUNT, DbContract.MeasurementDataTable.DATETIME, DbContract.MeasurementDataTable.MONTH_NUM},
                new String[]{"ID00001A", "0.449", String.valueOf(nowTime - 86400000 * 2), "1"},
                new String[]{"string", "double", "long", "int"}
        );

        dbOperation.insertData(
                DbContract.MeasurementDataTable.TABLE_NAME,
                new String[]{DbContract.MeasurementDataTable.S_ID, DbContract.MeasurementDataTable.AMOUNT, DbContract.MeasurementDataTable.DATETIME, DbContract.MeasurementDataTable.MONTH_NUM},
                new String[]{"ID00001A", "0.403", String.valueOf(nowTime - 86400000), "1"},
                new String[]{"string", "double", "long", "int"}
        );

        dbOperation.insertData(
                DbContract.MeasurementDataTable.TABLE_NAME,
                new String[]{DbContract.MeasurementDataTable.S_ID, DbContract.MeasurementDataTable.AMOUNT, DbContract.MeasurementDataTable.DATETIME, DbContract.MeasurementDataTable.MONTH_NUM},
                new String[]{"ID00002A", "0.32345", String.valueOf(nowTime - 86400000), "1"},
                new String[]{"string", "double", "long", "int"}
        );

        dbOperation.insertData(
                DbContract.MeasurementDataTable.TABLE_NAME,
                new String[]{DbContract.MeasurementDataTable.S_ID, DbContract.MeasurementDataTable.AMOUNT, DbContract.MeasurementDataTable.DATETIME, DbContract.MeasurementDataTable.MONTH_NUM},
                new String[]{"ID00003A", "2.0", String.valueOf(nowTime - 86400000), "1"},
                new String[]{"string", "double", "long", "int"}
        );

        dbOperation.insertData(
                DbContract.MeasurementDataTable.TABLE_NAME,
                new String[]{DbContract.MeasurementDataTable.S_ID, DbContract.MeasurementDataTable.AMOUNT, DbContract.MeasurementDataTable.DATETIME, DbContract.MeasurementDataTable.MONTH_NUM},
                new String[]{"ID00004A", "0.45", String.valueOf(nowTime - 86400000), "1"},
                new String[]{"string", "double", "long", "int"}
        );
    }

    private void checkDbData(){
        String[][] debugSlaves = dbOperation.selectData(
                false,
                DbContract.SlavesTable.TABLE_NAME,
                null,
                new String[]{DbContract.SlavesTable.S_ID, DbContract.SlavesTable.NAME, DbContract.SlavesTable.IS_NEW},
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
            Log.d("SLAVES IS NEW", debugSlaves[row][2] );
        }

        String[] pro = {
                DbContract.MeasurementDataTable._ID,
                DbContract.MeasurementDataTable.S_ID,
                DbContract.MeasurementDataTable.AMOUNT,
                DbContract.MeasurementDataTable.DATETIME
        };

        String[][] debug = dbOperation.selectData(
                false,
                DbContract.MeasurementDataTable.TABLE_NAME,
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
