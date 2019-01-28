package com.jeg.te.justenoughgoods.log;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.jeg.te.justenoughgoods.database.DbContract;
import com.jeg.te.justenoughgoods.database.DbOperation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;

import static com.jeg.te.justenoughgoods.utilities.DateTimeConvertUtilities.convertMinutesToDate;
import static com.jeg.te.justenoughgoods.utilities.DateTimeConvertUtilities.getDifferenceFromNow;

public class LogChartDrawer {

    // Database operator
    private DbOperation dbOperation;

    // Slave params.
    private String sId;
    private boolean isMonthly;

    private int showingMonth = 0;

    private LineChart lineChart;

    private ArrayList<Entry> logData = null;
    private float yMax, xMax, xMin = 0;

    public LogChartDrawer(String sId, boolean isMonthly, LineChart lineChart){
        this.sId = sId;
        this.isMonthly = isMonthly;
        this.lineChart = lineChart;

        // Get instance.
        dbOperation = DbOperation.getDbOperation();
    }

    /**
     * Draw Chart.
     */
    public void drawingGraph(){
        // Get data.
        getMeasurementData();

        // Set data to chart data set.
        LineDataSet lineDataSet = new LineDataSet(logData, "");
        lineDataSet.setColor(Color.rgb(0xb9, 0x40, 0x47)); // Line color.

        // 計測データがあるか
        if(logData.size() != 0) {
            lineDataSet.setDrawCircles(true); // Chart dot enable or disable.
        }
        else {
            lineDataSet.setDrawCircles(false);
            // Set fake data,
            yMax = 100;
            logData.add(new Entry(1, 0));
        }

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        initChart(lineChart);

        lineChart.invalidate();
    }

    /**
     * Get Log data form database.
     */
    private void getMeasurementData(){
        // SELECT
        String[] projection = {
                DbContract.MeasurementDataTable.AMOUNT,
                DbContract.MeasurementDataTable.DATETIME,
                DbContract.MeasurementDataTable.MONTH_NUM
        };

        // WHERE
        String selection;
        String[] selectionArgs = new String[(isMonthly ? 2 : 1)];

        // If monthly set month to where.
        if(isMonthly){
            selection = DbContract.MeasurementDataTable.S_ID + " = ? AND " + DbContract.MeasurementDataTable.MONTH_NUM + " = ?";
            selectionArgs[0] = sId;
            selectionArgs[1] = String.valueOf(showingMonth);
        }
        else{
            selection = DbContract.MeasurementDataTable.S_ID + " = ?";
            selectionArgs[0] = sId;
        }

        // ORDER BY
        String sortOrder = DbContract.MeasurementDataTable.DATETIME + " ASC";

        String[][] mData = dbOperation.selectData(
                false,
                DbContract.MeasurementDataTable.TABLE_NAME,
                null,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder,
                null
        );

        logData = new ArrayList<>();
        for(String[] value : mData) {
            float datetime = getDifferenceFromNow(Long.valueOf(value[1]));
            String amount = String.valueOf(new BigDecimal(Double.valueOf(value[0])  * 1000.0 ).setScale(3, BigDecimal.ROUND_HALF_UP));
            logData.add(new Entry(datetime, Float.valueOf(amount)));
        }

        // Check Y and X axis Max.
        if(logData != null && logData.size() > 0){
            for(Entry entry : logData){
                float y = entry.getY();
                if(yMax < y) yMax =y;
            }

            xMax = logData.get(logData.size() - 1).getX();
            xMin = logData.get(0).getX();
        }
    }

    /**
     * Initialize Chart.
     */
    private void initChart(LineChart lineChart) {
        lineChart.getDescription().setEnabled(false); // Chart description enable or disable.
        lineChart.setTouchEnabled(true); // Touch gesture enable or disable.

        lineChart.getLegend().setEnabled(false);

        lineChart.setScaleEnabled(true);
        //logChart.setScaleXEnabled(true);
        //logChart.setScaleYEnabled(true);

        lineChart.setDragEnabled(true); // Drag enable or disable.
        lineChart.setPinchZoom(true); // Pinch, Zoom enable or disable.

        lineChart.setBackgroundColor(Color.WHITE); // Chart's background color or disable.

        // Y axis (left) setting.
        YAxis leftYAxis = lineChart.getAxisLeft();
        leftYAxis.setAxisMaximum(yMax + yMax / 10);
        leftYAxis.setAxisMinimum(0);

        // Y axis (right) setting.
//        logChart.getAxisRight().setEnabled(true);
        YAxis rightYAxis = lineChart.getAxisRight();
        rightYAxis.setAxisMaximum(yMax);
        rightYAxis.setAxisMinimum(0);

        // X axis setting.
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(45f);
        xAxis.setAxisMaximum(xMax);
        xAxis.setAxisMinimum(xMin);
        // X axis value display setting.
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return convertMinutesToDate(new Float(value).longValue());
            }
        });
    }

    /**
     * Change show month.
     */
    public void changeShowingMonth(int changeNum){
        if(changeNum == 0){
            Calendar calendar = Calendar.getInstance();
            showingMonth = calendar.get(Calendar.MONTH) + 1;
        }
        else if(changeNum == 1){
            if(showingMonth == 12) showingMonth = 1;
            else showingMonth++;
        }
        else if(changeNum == -1){
            if(showingMonth == 1) showingMonth = 12;
            else showingMonth--;
        }
    }

    // Getter
    public int getShowingMonth(){
        return showingMonth;
    }
}
