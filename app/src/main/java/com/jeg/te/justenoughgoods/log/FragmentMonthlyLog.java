package com.jeg.te.justenoughgoods.log;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.jeg.te.justenoughgoods.main.ActivityMain;
import com.jeg.te.justenoughgoods.R;

/**
 * Log Fragment (Monthly)
 */
public class FragmentMonthlyLog extends Fragment {
    // Chart configurator instance
    private LogChartDrawer logChartDrawer;

    // GUIs
    private TextView textViewMonthNum;
    private LineChart logChart = null;

    // Slave information.
    private String sId;
    private String name;

    // Constructor
    public FragmentMonthlyLog(){}

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
        // Set slave name
        TextView textViewSlaveName = view.findViewById(R.id.textView_logTitleName);
        textViewSlaveName.setText(getString(R.string.log_slave_name, name));

        logChart = view.findViewById(R.id.chart_log);
        logChartDrawer = new LogChartDrawer(sId, true, logChart); // Get instances.

        textViewMonthNum = view.findViewById(R.id.textView_logTitleNum);

        // Initialize month num
        logChartDrawer.changeShowingMonth(0);
        textViewMonthNum.setText( getResources().getString(R.string.log_month_num, logChartDrawer.getShowingMonth()));

        // Previous Button Listener
        view.findViewById(R.id.bt_logPrevious).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logChartDrawer.changeShowingMonth(-1);
                textViewMonthNum.setText( getResources().getString(R.string.log_month_num, logChartDrawer.getShowingMonth()));
                logChartDrawer.drawingGraph();
            }
        });

        // Next Button Listener
        view.findViewById(R.id.bt_logNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logChartDrawer.changeShowingMonth(1);
                textViewMonthNum.setText( getResources().getString(R.string.log_month_num, logChartDrawer.getShowingMonth()));
                logChartDrawer.drawingGraph();
            }
        });

        // Log Change Button Listener
        view.findViewById(R.id.bt_logChange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityMain activityMain = (ActivityMain) getActivity();
                activityMain.createFragmentYearlyLog(sId, name);
            }
        });

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
