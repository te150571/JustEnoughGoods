package com.jeg.te.justenoughgoods.log;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.github.mikephil.charting.charts.LineChart;
import com.jeg.te.justenoughgoods.main.ActivityMain;
import com.jeg.te.justenoughgoods.R;

import java.util.Calendar;

/**
 * Log Fragment (Yearly)
 */
public class FragmentYearlyLog extends Fragment {
    // Chart configurator instance
    private LogChartDrawer logChartDrawer;

    // GUIs
    private LineChart logChart = null;

    // Slave information.
    private String sId;
    private String name;

    // Constructor
    public FragmentYearlyLog(){}

    // Called to do initial creation of the fragment.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get Bundle values.
        Bundle args = getArguments();
        if(args != null){
            sId = args.getString("sid");
            name = args.getString("name");
        }
    }

    // Creates and returns the view hierarchy associated with the fragment.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log, container, false);
    }

    // Called when view generation is complete.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // GUI setting
        // Hide month change buttons
        view.findViewById(R.id.bt_logPrevious).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.bt_logNext).setVisibility(View.INVISIBLE);

        // Change button text and set listener
        BootstrapButton btLogChange = view.findViewById(R.id.bt_logChange);
        btLogChange.setText(R.string.log_change_to_monthly);
        btLogChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityMain activityMain = (ActivityMain) getActivity();
                activityMain.createFragmentMonthlyLog(sId, name);
            }
        });

        // Set slave name
        TextView textViewSlaveName = view.findViewById(R.id.textView_logTitleName);
        textViewSlaveName.setText(getString(R.string.log_slave_name, name));

        logChart = view.findViewById(R.id.chart_log);

        TextView textViewYearNum = view.findViewById(R.id.textView_logTitleNum);
        Calendar calendar = Calendar.getInstance(); // Get Year
        textViewYearNum.setText( getResources().getString(R.string.log_year_num, calendar.get(Calendar.YEAR)));

        logChartDrawer = new LogChartDrawer(sId, false, logChart); // Get instance.

        // Back Button Listener
        view.findViewById(R.id.bt_logBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityMain activityMain = (ActivityMain) getActivity();
                activityMain.createFragmentRemainingAmount();
            }
        });
    }

    // Called when just before the user can operate.
    @Override
    public void onResume(){
        super.onResume();

        logChartDrawer.drawingGraph();
    }
}
