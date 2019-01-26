package com.jeg.te.justenoughgoods.slave_list;

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
import com.jeg.te.justenoughgoods.R;
import com.jeg.te.justenoughgoods.main.ActivityMain;
import com.jeg.te.justenoughgoods.utilities.DbOperationForSlaveData;
import com.jeg.te.justenoughgoods.list_item_data_class.Slave;

import java.util.ArrayList;

/**
 * Slave list fragment
 */
public class FragmentSlaveList extends Fragment {

    private DbOperationForSlaveData dbOperationForSlaveData;

    // Slave list Adapter
    private SlaveListAdapter slaveListAdapter;

    private AwesomeTextView awesomeTextView;

    // Constructor
    public FragmentSlaveList(){}

    // Called to do initial creation of the fragment.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get instance.
        dbOperationForSlaveData = new DbOperationForSlaveData();
    }

    // Creates and returns the view hierarchy associated with the fragment.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_slave_list, container, false);
    }

    // Called when view generation is complete.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ListView
        slaveListAdapter = new SlaveListAdapter(getActivity());

        ListView listView = view.findViewById( R.id.listView_slavesList);
        listView.setAdapter(slaveListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textViewSlaveId = view.findViewById(R.id.textView_slaveId);
                ActivityMain activityMain = (ActivityMain) getActivity();
                activityMain.createFragmentSlaveConfiguration((String)textViewSlaveId.getText(), "slaveList");
            }
        });

        TabLayout tabLayout = view.findViewById(R.id.tab_slaveList);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        getAndSetSlaveData(false);
                        break;
                    case 1:
                        getAndSetSlaveData(true);
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

        awesomeTextView = view.findViewById(R.id.awesomeTextView_newSlaveMark);
    }

    // Called when just before the user can operate.
    @Override
    public void onResume() {
        super.onResume();

        getAndSetSlaveData(false);

        if(dbOperationForSlaveData.getSlaveListWithIsNewParam(true).size() > 0)
            awesomeTextView.setVisibility(View.VISIBLE);
        else
            awesomeTextView.setVisibility(View.GONE);
    }

    // Data acquisition and display.
    private void getAndSetSlaveData(boolean onlyNew){
        slaveListAdapter.clearSlaves();

        ArrayList<Slave> slaves = dbOperationForSlaveData.getSlaveListWithIsNewParam(onlyNew);

        // Add slaves.
        for (Slave slave : slaves){
            slaveListAdapter.addSlaves(slave);
        }
    }
}
