package com.jeg.te.justenoughgoods;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.jeg.te.justenoughgoods.database.DbContract.MeasurementDataTable;
import com.jeg.te.justenoughgoods.database.DbOperation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.jeg.te.justenoughgoods.Utilities.convertMinutesToDate;
import static com.jeg.te.justenoughgoods.Utilities.getDifferenceFromNow;

public class LogViewActivity extends Activity implements View.OnClickListener {
    // 子機データベース
    private DbOperation dbOperation = null;

    // 子機データ
    private String sId;
    private String name;

    // メニューの宣言
    private MenuItem logMonthly;
    private MenuItem logYearly;

    // ボタンの宣言
    private Button btLogPrevious;
    private Button btLogNext;

    // グラフの宣言
    private LineChart logChart = null;

    // 月間ログフラグ
    private boolean monthly = true;
    private int month_num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbOperation = DbOperation.getDbOperation();

        Intent intent = getIntent();
        sId = intent.getStringExtra("sid");
        name = intent.getStringExtra("name");

        displayMonthlyLog(); // 月間ログ画面
    }

    /*
        画面遷移とコンポネント設定処理
     */
    // オプションメニュー作成時の処理
    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        getMenuInflater().inflate( R.menu.log_menu, menu );
        logMonthly = menu.findItem( R.id.menu_item_log_monthly );
        logYearly = menu.findItem( R.id.menu_item_log_yearly );

        // メニューアイテムの表示非表示
        if(monthly){
            logMonthly.setVisible(false);
            logYearly.setVisible(true);
        }
        else{
            logMonthly.setVisible(true);
            logYearly.setVisible(false);
        }
        return true;
    }

    // オプションメニューのアイテム選択時の処理
    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        switch( item.getItemId() )
        {
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menu_item_log_monthly:
                monthly = true;
                displayMonthlyLog();
                return true;
            case R.id.menu_item_log_yearly:
                monthly = false;
                displayYearlyLog();
                return true;
        }
        return false;
    }

    // ボタンによる画面遷移処理
    @Override
    public void onClick( View v )
    {
        // viewのIDを取得して分岐
        String idName = getResources().getResourceEntryName(v.getId());
        switch ( idName ){
            case "bt_logPrevious":
                setHeaderMonthNum(-1); // 月の再表示
                drawingGraph(true); // データを再取得しグラフを描画
                break;
            case "bt_logNext":
                setHeaderMonthNum(1); // 月の再表示
                drawingGraph(true); // データを再取得しグラフを描画
                break;
        }
    }

    @Override
    protected void onPause() {
        dbOperation.closeConnection();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        dbOperation.closeConnection();
        super.onDestroy();
    }

    // 月間ログ表示
    public void displayMonthlyLog(){
        // タイトル設定
        setTitle(getResources().getString(R.string.app_name_log_monthly, name));
        // 画面表示とGUI設定
        setContentView(R.layout.fragment_log_monthly);
        setActionBar((Toolbar) findViewById(R.id.toolbar_logMonthly));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        setHeaderMonthNum(0); // 月の表示

        setButtonListener(); // ボタンアクション設定

        drawingGraph(true); // データを取得しグラフを描画
    }

    // 年間ログ表示
    public void displayYearlyLog(){
        // タイトル設定
        setTitle((getResources().getString(R.string.app_name_log_yearly, name)));
        // 画面表示とGUI設定
        setContentView(R.layout.fragment_log_yearly);
        setActionBar((Toolbar) findViewById(R.id.toolbar_logYearly));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // 年表示
        Calendar calendar = Calendar.getInstance();
        TextView logYearNum = findViewById(R.id.textView_logYearNum);
        logYearNum.setText(getResources().getString(R.string.log_year_num, calendar.get(Calendar.YEAR)));

        setButtonListener(); // ボタンアクション設定

        drawingGraph(false); // データを取得しグラフを描画
    }

    // ボタンのリスナ登録
    private void setButtonListener(){
        // GUIアイテム設定
        btLogPrevious = findViewById(R.id.bt_logPrevious);
        if(btLogPrevious != null) btLogPrevious.setOnClickListener(this);
        btLogNext = findViewById(R.id.bt_logNext);
        if(btLogNext != null) btLogNext.setOnClickListener(this);
    }

    // 月取得とタイトル表示
    private void setHeaderMonthNum(int changeNum){
        // changeNumによって分岐
        // 0:現在の月, 1:表示している次の月, -1:表示している前の月
        if(changeNum == 0){
            // 現在の月を取得
            Calendar calendar = Calendar.getInstance();
            month_num = calendar.get(Calendar.MONTH) + 1;
        }
        else if(changeNum == 1){
            if(month_num == 12) month_num = 1;
            else month_num++;
        }
        else if(changeNum == -1){
            if(month_num == 1) month_num = 12;
            else month_num--;
        }

        TextView logMonthNum = findViewById(R.id.textView_logMonthNum);
        logMonthNum.setText( getResources().getString(R.string.log_month_num, month_num));
    }

    // グラフの描画
    private void drawingGraph(boolean monthly){
        // 初回にグラフオブジェクトを格納
        logChart = findViewById(R.id.chart_StaticLineGraph);

        // データベースから計測データを取得
        ArrayList<Entry> measurementData = getMeasurementData(monthly);

        // グラフ描画用のY軸最大値を取得
        float yMax = 0;
        for(Entry entry : measurementData){
            float y = entry.getY();
            if(yMax < y) yMax =y;
        }

        // グラフへのデータセット
        LineDataSet lineDataSet = new LineDataSet(measurementData, name); // 第一引数にデータ、第二引数にラベル名
        lineDataSet.setColor(Color.rgb(0xb9, 0x40, 0x47)); // 線の色設定

        // 計測データがあるか
        if(measurementData.size() != 0) {
            lineDataSet.setDrawCircles(true); // 値のプロット点の有無
        }
        else {
            lineDataSet.setDrawCircles(false); // 値のプロット点の有無
            yMax = 100; // 架空の最大値をセット
            measurementData.add(new Entry(1, 0)); // 架空のデータを1件セット
        }

        LineData lineData = new LineData(lineDataSet);
        logChart.setData(lineData);

        //        initChart(yMax, 0, 10); // グラフの初期化
        initChart(yMax + 100, measurementData.get(0).getX(), measurementData.get(measurementData.size() - 1).getX()); // グラフの初期化

        logChart.invalidate(); // グラフの更新
    }

    /**
     * データベースから取得
     */
    private ArrayList<Entry> getMeasurementData(boolean monthly){
        // データ取得
        // SELECT
        String[] projection = {
                MeasurementDataTable.AMOUNT,
                MeasurementDataTable.DATETIME,
                MeasurementDataTable.MONTH_NUM
        };

        // WHERE
        String selection = null;
        String[] selectionArgs = new String[(monthly ? 2 : 1)];
        // 月間だったらWHEREをに月を追加
        if(monthly){
            selection = MeasurementDataTable.S_ID + " = ? AND " + MeasurementDataTable.MONTH_NUM + " = ?";
            selectionArgs[0] = sId;
            selectionArgs[1] = String.valueOf(month_num);
        }
        else{
            selection = MeasurementDataTable.S_ID + " = ?";
            selectionArgs[0] = sId;
        }

        // ORDER BY
        String sortOrder = MeasurementDataTable.DATETIME + " ASC";

        String[][] mData = dbOperation.selectData(
            false,
                MeasurementDataTable.TABLE_NAME,
                null,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder,
                null
        );

        ArrayList<Entry> data = new ArrayList<>();
        for(String[] value : mData) {
            float datetime = getDifferenceFromNow(Long.valueOf(value[1]));
            // データを少数第三位で四捨五入
            String amount = String.valueOf(new BigDecimal(Double.valueOf(value[0])  * 1000.0 ).setScale(3, BigDecimal.ROUND_HALF_UP));
            data.add(new Entry(datetime, Float.valueOf(amount))); // データ追加
        }
        return data;
    }

    /**
     * グラフの初期化
     */
    private void initChart(float yMax, float xMin, float xMax) {
        logChart.getDescription().setEnabled(false); // グラフ説明テキストを表示するか
        logChart.setTouchEnabled(true); // グラフへのタッチジェスチャーを有効にするか

        logChart.getLegend().setEnabled(false);

        // グラフのスケーリングを有効にするか
        logChart.setScaleEnabled(true);
        //logChart.setScaleXEnabled(true);     // X軸のみに対しての設定
        //logChart.setScaleYEnabled(true);     // Y軸のみに対しての設定

        logChart.setDragEnabled(true); // グラフのドラッギングを有効にするか
        logChart.setPinchZoom(true); // グラフのピンチ/ズームを有効にするか

        logChart.setBackgroundColor(Color.WHITE); // グラフの背景色設定

        // Y軸(左)の設定
        YAxis leftYAxis = logChart.getAxisLeft(); // Y軸(左)の取得
        leftYAxis.setAxisMaximum(yMax); // Y軸(左)の最大値設定
        leftYAxis.setAxisMinimum(0); // Y軸(左)の最小値設定

        // Y軸(右)の設定
//        logChart.getAxisRight().setEnabled(true); // Y軸(右)は表示しない
        YAxis rightYAxis = logChart.getAxisRight(); // Y軸(右)の取得
        rightYAxis.setAxisMaximum(yMax); // Y軸(右)の最大値設定
        rightYAxis.setAxisMinimum(0); // Y軸(右)の最小値設定

        // X軸の設定
        XAxis xAxis = logChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(45f);
        xAxis.setAxisMaximum(xMax); // X軸の最大値設定
        xAxis.setAxisMinimum(xMin); // X軸の最小値設定
        // X軸の値表示設定
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                // nullを返すと落ちるので、値を書かない場合は空文字を返す
                return convertMinutesToDate(new Float(value).longValue());
            }
        });
    }

    private String getSaveFileName() {
        Date d = new Date();
        return String.format("StaticLineGraph_%tY%tm%td%tH%tM%tS.jpg", d, d, d, d, d, d);
    }
}
