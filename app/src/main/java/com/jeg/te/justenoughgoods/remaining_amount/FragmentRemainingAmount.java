package com.jeg.te.justenoughgoods.remaining_amount;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.jeg.te.justenoughgoods.ActivityMain;
import com.jeg.te.justenoughgoods.R;
import com.jeg.te.justenoughgoods.Slave;
import com.jeg.te.justenoughgoods.bluetooth.BluetoothConnection;
import com.jeg.te.justenoughgoods.database.DbContract;
import com.jeg.te.justenoughgoods.database.DbOperation;

import java.util.ArrayList;

/**
 * Remainder list fragment
 */
public class FragmentRemainingAmount extends Fragment {
    // Bluetooth connector
    private BluetoothConnection raspberryBluetoothConnection = null;

    // Database operator
    private DbOperation dbOperation = null;

    // Slave list Adapter
    private SlavesRemainingAmountListAdapter slavesRemainingAmountListAdapter;
    private ArrayList<String> lackOfRemainingList;

    // Constructor
    public FragmentRemainingAmount(){

    }

    // Called to do initial creation of the fragment.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get instances.
        raspberryBluetoothConnection = BluetoothConnection.getBluetoothConnection();
        dbOperation = DbOperation.getDbOperation();
    }

    // Creates and returns the view hierarchy associated with the fragment.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_remaining_amount, container, false);
    }

    // Called when view generation is complete.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // リストビューの設定
        slavesRemainingAmountListAdapter = new SlavesRemainingAmountListAdapter( getActivity() );
        Log.d("ListAdapter", String.valueOf(slavesRemainingAmountListAdapter == null));
        ListView listView = view.findViewById( R.id.listView_slavesAmount);
        listView.setAdapter(slavesRemainingAmountListAdapter);

        // List was tapped.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textViewSlaveId = view.findViewById(R.id.textView_slaveId);
                TextView textViewSlaveName = view.findViewById(R.id.textView_slaveName);
                ActivityMain activityMain = (ActivityMain) getActivity();
                activityMain.createFragmentMonthlyLog((String)textViewSlaveId.getText(), (String)textViewSlaveName.getText());
            }
        });

        // List was long tapped.
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textViewSlaveId = view.findViewById(R.id.textView_slaveId);
                return true;
            }
        });
    }

    // Called when just before the user can operate.
    @Override
    public void onResume() {
        super.onResume();

        getAndSetSlavesRemainingAmountData(); // Get Data and display it.
    }

    // データの取得と表示
    public void getAndSetSlavesRemainingAmountData() {
        slavesRemainingAmountListAdapter.clearSlaves();

        // Get slaves data from database.
        // SELECT
        String[] projection = {
                "s." + DbContract.SlavesTable.S_ID,
                "s." + DbContract.SlavesTable.NAME,
                "s." + DbContract.SlavesTable.NOTIFICATION_AMOUNT,
                "m." + DbContract.MeasurementDataTable.AMOUNT,
                "m." + DbContract.MeasurementDataTable.DATETIME
        };
        // JOIN
        String tableJoin = "as s LEFT JOIN (" +
                "SELECT * FROM "+ DbContract.MeasurementDataTable.TABLE_NAME + " a " +
                "WHERE NOT EXISTS ( SELECT 1 FROM " + DbContract.MeasurementDataTable.TABLE_NAME + " b WHERE a.s_id = b.s_id AND a.datetime < b.datetime ) ) as m ON s.s_id = m.s_id";
        // ORDER
        String sortOrder = "s." + DbContract.SlavesTable.S_ID + " ASC";

        String[][] selectResult = dbOperation.selectData(
                false,
                DbContract.SlavesTable.TABLE_NAME,
                tableJoin,
                projection,
                null,
                null,
                null,
                null,
                sortOrder,
                null
        );

        ArrayList<Slave> slavesData = new ArrayList<>();
        for(String[] rowData : selectResult){
            Slave slave = new Slave();
            slave.setSId( rowData[0] );
            slave.setName( rowData[1] );
            slave.setAmount( Double.valueOf(rowData[3]) );
            slave.setNotificationAmount( Double.valueOf(rowData[2]) );
            slave.setLastUpdate( Long.valueOf(rowData[4]) );
            slavesData.add(slave);
        }

        // Add slaves.
        for (Slave slave : slavesData){
            slavesRemainingAmountListAdapter.addSlaves(slave);
        }

        // 不足を通知
//        lacks = SlavesRemainingAmountListAdapter.getLackList();
//        if(lacks.size() > 0){
//            AmountLackNotificationDialog amountLackNotificationDialog = new AmountLackNotificationDialog();
//            amountLackNotificationDialog.show(getFragmentManager(), "amountLackNotificationDialog");
//        }
    }
}
