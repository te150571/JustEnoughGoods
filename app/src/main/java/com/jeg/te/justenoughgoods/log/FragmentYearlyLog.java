package com.jeg.te.justenoughgoods.log;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.jeg.te.justenoughgoods.R;

import java.util.Calendar;

public class FragmentYearlyLog extends Fragment {
    // Chart configurator instance
    private LogChartDrawer logChartDrawer;

    // GUIs
    private TextView textViewYearNum;
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
        return inflater.inflate(R.layout.fragment_log_yearly, container, false);
    }

    // Called when view generation is complete.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // GUI setting.
        logChart = view.findViewById(R.id.chart_StaticLineGraph);
        textViewYearNum = view.findViewById(R.id.textView_logYearNum);

        // Get Year.
        Calendar calendar = Calendar.getInstance();
        textViewYearNum.setText( getResources().getString(R.string.log_month_num, calendar.get(Calendar.YEAR)));

        logChartDrawer = new LogChartDrawer(sId, false, logChart); // Get instance.
    }

    @Override
    public void onResume(){
        super.onResume();

        logChartDrawer.drawingGraph();
    }
}
