package com.jeg.te.justenoughgoods.remaining_amount;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.awesomebar.AwesomeBar;
import com.jeg.te.justenoughgoods.R;
import com.jeg.te.justenoughgoods.bluetooth.BluetoothConnection;
import com.jeg.te.justenoughgoods.database.DbOperation;

import java.util.ArrayList;

/**
 * Remainder list fragment
 */
public class FragmentRemainingAmount extends Fragment {
    // Constants
    private static final int REQUEST_ENABLE_BLUETOOTH = 1; // Bluetooth機能の有効化要求時の識別コード
    private static final int REQUEST_CONNECT_DEVICE = 2; // デバイス接続要求時の識別コード

    // Bluetooth connector
    private BluetoothConnection raspberryBluetoothConnection = null;

    // Slaves Database operator
    private DbOperation dbOperation = null;

    // Slave list Adapter
    private AmountListAdapter amountListAdapter;
    private ArrayList<String> lackOfRemainingList;

    // GUI
    private AwesomeBar toolbar_main;
    private DrawerLayout main_navigation;

    // Inner thread class
    class UpdateCheker extends Thread{
        public void run(){
            try{
                if(raspberryBluetoothConnection != null){
                    raspberryBluetoothConnection.connect();

                    if(raspberryBluetoothConnection.checkUpdatable()){
//                startUpdate();
                    }
                    else {
//                    checkUpdate();
                    }
                    Thread.sleep(3000);
                }
            }
            catch (InterruptedException e){

            }
        }
    }

    // Constructor
    public FragmentRemainingAmount(){

    }

    // Called to do initial creation of the fragment.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Creates and returns the view hierarchy associated with the fragment.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_amount_view, container, false);
    }
}
