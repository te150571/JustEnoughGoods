package com.jeg.te.justenoughgoods.main;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jeg.te.justenoughgoods.R;
import com.jeg.te.justenoughgoods.list_item_data_class.Slave;
import com.jeg.te.justenoughgoods.remaining_amount.SlavesRemainingAmountListAdapter;
import com.jeg.te.justenoughgoods.utilities.DbOperationForSlaveData;

import java.util.ArrayList;

public class FragmentHome extends Fragment {
    // Database operator
    private DbOperationForSlaveData dbOperationForSlaveData;

    // Slave list Adapter
    private SlavesRemainingAmountListAdapter slavesRemainingAmountListAdapter;

    // Constructor
    public FragmentHome(){}

    // Called to do initial creation of the fragment.
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Get instances.
        dbOperationForSlaveData = new DbOperationForSlaveData();
    }

    // Creates and returns the view hierarchy associated with the fragment.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    // Called when view generation is complete.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
            疲れたので残量表示用のものを流用
         */

        // ListView
        slavesRemainingAmountListAdapter = new SlavesRemainingAmountListAdapter( getActivity() );
        ListView listView = view.findViewById( R.id.listView_homeLackList);
        listView.setAdapter(slavesRemainingAmountListAdapter);
    }

    // Called when just before the user can operate.
    @Override
    public void onResume() {
        super.onResume();

        getAndSetSlavesRemainingAmountData(true); // Get Data and display it.
    }

    // Data acquisition and display.
    public void getAndSetSlavesRemainingAmountData(boolean onlyLack) {
        slavesRemainingAmountListAdapter.clearSlaves();

        ArrayList<Slave> slaves = dbOperationForSlaveData.getSlaveListWithRemainingAmountData(onlyLack);

        // Add slaves.
        for (Slave slave : slaves){
            slavesRemainingAmountListAdapter.addSlaves(slave);
        }
    }
}
