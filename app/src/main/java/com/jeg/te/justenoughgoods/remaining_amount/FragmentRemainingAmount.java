package com.jeg.te.justenoughgoods.remaining_amount;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.jeg.te.justenoughgoods.list_item_data_class.Slave;
import com.jeg.te.justenoughgoods.main.ActivityMain;
import com.jeg.te.justenoughgoods.R;
import com.jeg.te.justenoughgoods.database.DbContract;
import com.jeg.te.justenoughgoods.database.DbOperation;
import com.jeg.te.justenoughgoods.utilities.DbOperationForSlaveData;

import java.util.ArrayList;

/**
 * Remainder list fragment
 */
public class FragmentRemainingAmount extends Fragment {
    // Database operator
    private DbOperationForSlaveData dbOperationForSlaveData;

    // Slave list Adapter
    private SlavesRemainingAmountListAdapter slavesRemainingAmountListAdapter;

    private AwesomeTextView awesomeTextViewLackMark;

    // Constructor
    public FragmentRemainingAmount(){

    }

    // Called to do initial creation of the fragment.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get instances.
        dbOperationForSlaveData = new DbOperationForSlaveData();
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

        // ListView
        slavesRemainingAmountListAdapter = new SlavesRemainingAmountListAdapter( getActivity() );
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
                ActivityMain activityMain = (ActivityMain) getActivity();
                activityMain.createFragmentSlaveConfiguration((String)textViewSlaveId.getText(), "remaining");
                return true;
            }
        });

        TabLayout tabLayout = view.findViewById(R.id.tab_remainingAmountList);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        getAndSetSlavesRemainingAmountData(false);
                        break;
                    case 1:
                        getAndSetSlavesRemainingAmountData(true);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        awesomeTextViewLackMark = view.findViewById(R.id.awesomeTextView_lackSlaveMark);
    }

    // Called when just before the user can operate.
    @Override
    public void onResume() {
        super.onResume();

        getAndSetSlavesRemainingAmountData(false); // Get Data and display it.

        if(dbOperationForSlaveData.getSlaveListWithRemainingAmountData(true).size() > 0)
            awesomeTextViewLackMark.setVisibility(View.VISIBLE);
        else
            awesomeTextViewLackMark.setVisibility(View.GONE);
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
