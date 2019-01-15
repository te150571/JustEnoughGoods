package com.jeg.te.justenoughgoods;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.jeg.te.justenoughgoods.database.DbContract.SlavesTable;
import com.jeg.te.justenoughgoods.database.DbContract.MeasurementDataTable;
import com.jeg.te.justenoughgoods.database.DbOpenHelper;

import java.util.ArrayList;
import java.util.Iterator;

public class AmountViewActivity extends Activity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{
    // 子機データベース
    private DbOpenHelper dbOpenHelper = null;

    // リストビューの内容
    private SlavesListAdapter slavesListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // タイトル設定
        setTitle(R.string.app_name);
        // メイン画面表示
        displayAmounts();
    }

    // オプションメニュー作成時の処理
    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        getMenuInflater().inflate( R.menu.main_menu, menu );
        return true;
    }

    // オプションメニューのアイテム選択時の処理
    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        switch( item.getItemId() )
        {
            case R.id.menu_item_raspberry_config:
                Intent intent = new Intent(getApplication(), RaspberryConfigurationActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_item_init_config:
                SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
                // DELETE
                String deleteSelection = " 1 = 1"; // WHERE 句
//                String[] deleteSelectionArgs = { "5" };
                writer.delete(SlavesTable.TABLE_NAME, deleteSelection, null);
                slavesListAdapter.clearSlaves();
                getAndSetSlavesAmountData();
                return true;
        }
        return false;
    }

    @Override
    public void onItemClick( AdapterView<?> parent, View view, int position, long id ){
        Intent slaveLogIntent = new Intent(getApplication(), LogViewActivity.class);
        startActivity(slaveLogIntent);
        return;
    }

    @Override
    public boolean onItemLongClick( AdapterView<?> parent, View view, int position, long id ){
        Intent slaveConfigIntent = new Intent(getApplication(), SlaveConfigurationActivity.class);
        slaveConfigIntent.putExtra("sid", (String) ((TextView) findViewById(R.id.textView_slaveId)).getText());
        startActivity(slaveConfigIntent);
        return true;
    }

    @Override
    protected void onPause()
    {
        if(dbOpenHelper != null) {
            dbOpenHelper.close(); // コネクションを閉じる。
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(dbOpenHelper != null) {
            dbOpenHelper.close(); // コネクションを閉じる。
        }
        super.onDestroy();
    }

    // 画面表示
    private void displayAmounts(){
        setContentView(R.layout.amount_list);
        setActionBar((Toolbar) findViewById(R.id.toolbar_main));

        // GUIアイテム設定
        // リストビューの設定
        slavesListAdapter = new SlavesListAdapter( this ); // ビューアダプターの初期化
        ListView listView = findViewById( R.id.listView_slavesAmount);    // リストビューの取得
        listView.setAdapter( slavesListAdapter ); // リストビューにビューアダプターをセット
        listView.setOnItemClickListener( this ); // クリックリスナーオブジェクトのセット
        listView.setOnItemLongClickListener( this ); // ロングクリックリスナーオブジェクトのセット

        getAndSetSlavesAmountData();
    }

    // データの取得と表示
    private void getAndSetSlavesAmountData() {

//        dbOpenHelper = new DbOpenHelper(getApplicationContext());
//        SQLiteDatabase.deleteDatabase(getApplication().getDatabasePath(dbOpenHelper.getDatabaseName()));

        // テストデータをデータベースへ登録
        dbOpenHelper = new DbOpenHelper(getApplicationContext());
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();

        // INSERT
        // 子機設定データ
        ContentValues slave1 = new ContentValues();
        slave1.put(SlavesTable.S_ID, "ID00001A");
        slave1.put(SlavesTable.NAME, "醤油（DEBUG）");
        slave1.put(SlavesTable.NOTIFICATION_AMOUNT, new Double(0.2));
        slave1.put(SlavesTable.AMOUNT_NOTIFICATION_ENABLE, 1);
        slave1.put(SlavesTable.EXCEPTION_FLAG, 0);
        slave1.put(SlavesTable.EXCEPTION_NOTIFICATION_ENABLE, 1);
        writer.insert(SlavesTable.TABLE_NAME, null, slave1);

        ContentValues slave2 = new ContentValues();
        slave2.put(SlavesTable.S_ID, "ID00002A");
        slave2.put(SlavesTable.NAME, "酢（DEBUG）");
        slave2.put(SlavesTable.NOTIFICATION_AMOUNT, new Double(0.1));
        slave2.put(SlavesTable.AMOUNT_NOTIFICATION_ENABLE, 1);
        slave2.put(SlavesTable.EXCEPTION_FLAG, 0);
        slave2.put(SlavesTable.EXCEPTION_NOTIFICATION_ENABLE, 1);
        writer.insert(SlavesTable.TABLE_NAME, null, slave2);

        ContentValues slave3 = new ContentValues();
        slave3.put(SlavesTable.S_ID, "ID00003A");
        slave3.put(SlavesTable.NAME, "サラダ油（DEBUG）");
        slave3.put(SlavesTable.NOTIFICATION_AMOUNT, new Double(0.5));
        slave3.put(SlavesTable.AMOUNT_NOTIFICATION_ENABLE, 1);
        slave3.put(SlavesTable.EXCEPTION_FLAG, 0);
        slave3.put(SlavesTable.EXCEPTION_NOTIFICATION_ENABLE, 1);
        writer.insert(SlavesTable.TABLE_NAME, null, slave3);

        ContentValues slave4 = new ContentValues();
        slave4.put(SlavesTable.S_ID, "ID00004A");
        slave4.put(SlavesTable.NAME, "シャンプー（DEBUG）");
        slave4.put(SlavesTable.NOTIFICATION_AMOUNT, new Double(0.5));
        slave4.put(SlavesTable.AMOUNT_NOTIFICATION_ENABLE, 1);
        slave4.put(SlavesTable.EXCEPTION_FLAG, 0);
        slave4.put(SlavesTable.EXCEPTION_NOTIFICATION_ENABLE, 1);
        writer.insert(SlavesTable.TABLE_NAME, null, slave4);

        // 計測データ
        // 現在日時を取得
        long nowTime = System.currentTimeMillis();
        System.out.println("DEBUG DATETIME : " + MyApplication.convertLongToDateFormat(nowTime));

        ContentValues value1 = new ContentValues();
        value1.put(MeasurementDataTable.S_ID, "ID00001A");
        value1.put(MeasurementDataTable.AMOUNT, new Double(0.52345));
        value1.put(MeasurementDataTable.DATETIME, nowTime);
        writer.insert(MeasurementDataTable.TABLE_NAME, null, value1);

        ContentValues value2 = new ContentValues();
        value2.put(MeasurementDataTable.S_ID, "ID00002A");
        value2.put(MeasurementDataTable.AMOUNT, new Double(0.32345));
        value2.put(MeasurementDataTable.DATETIME, nowTime);
        writer.insert(MeasurementDataTable.TABLE_NAME, null, value2);

        ContentValues value3 = new ContentValues();
        value3.put(MeasurementDataTable.S_ID, "ID00003A");
        value3.put(MeasurementDataTable.AMOUNT, new Double(2.0));
        value3.put(MeasurementDataTable.DATETIME, nowTime);
        writer.insert(MeasurementDataTable.TABLE_NAME, null, value3);

        ContentValues value4 = new ContentValues();
        value4.put(MeasurementDataTable.S_ID, "ID00004A");
        value4.put(MeasurementDataTable.AMOUNT, new Double(0.45));
        value4.put(MeasurementDataTable.DATETIME, nowTime);
        writer.insert(MeasurementDataTable.TABLE_NAME, null, value4);

        // データベースから取得しSlavesクラスへ
        SQLiteDatabase reader = dbOpenHelper.getReadableDatabase();

        // SELECT
        String[] projection = { // SELECT する列
                "s." + SlavesTable.S_ID,
                "s." + SlavesTable.NAME,
                "s." + SlavesTable.NOTIFICATION_AMOUNT,
                "s." + SlavesTable.AMOUNT_NOTIFICATION_ENABLE,
                "s." + SlavesTable.EXCEPTION_FLAG,
                "s." + SlavesTable.EXCEPTION_NOTIFICATION_ENABLE,
                "m." + MeasurementDataTable.AMOUNT,
                "m." + MeasurementDataTable.DATETIME
        };
//        String selection = ""; // WHERE
//        String[] selectionArgs = { "" };
        String sortOrder = "s." + SlavesTable.S_ID + " DESC"; // ORDER

        // JOIN
        String tableJoin = " as s LEFT JOIN (" +
                "SELECT * FROM "+ MeasurementDataTable.TABLE_NAME + " a " +
                "WHERE NOT EXISTS ( SELECT 1 FROM " + MeasurementDataTable.TABLE_NAME + " b WHERE a.s_id = b.s_id AND a.datetime < b.datetime ) ) as m ON s.s_id = m.s_id";

        Cursor cursor = reader.query(
                SlavesTable.TABLE_NAME + tableJoin, // The table to query
                projection,         // The columns to return
                null,          // The columns for the WHERE clause
                null,      // The values for the WHERE clause
                null,               // don't group the rows
                null,               // don't filter by row groups
                sortOrder           // The sort order
        );

        ArrayList<Slaves> testSlaves = new ArrayList<>();
        while(cursor.moveToNext()) {
            Slaves slave = new Slaves();
            slave.setSId( cursor.getString(cursor.getColumnIndexOrThrow(SlavesTable.S_ID)) );
            slave.setName( cursor.getString(cursor.getColumnIndexOrThrow(SlavesTable.NAME)) );
            slave.setAmount( cursor.getDouble(cursor.getColumnIndexOrThrow(MeasurementDataTable.AMOUNT)) );
            slave.setNotificationAmount( cursor.getDouble(cursor.getColumnIndexOrThrow(SlavesTable.NOTIFICATION_AMOUNT)) );
            slave.setLastUpdate( cursor.getLong(cursor.getColumnIndexOrThrow(MeasurementDataTable.DATETIME)) );
            testSlaves.add(slave);
        }

        Iterator<Slaves> slaves = testSlaves.iterator();
        while (slaves.hasNext()){
            slavesListAdapter.addSlaves(slaves.next());
        }
        cursor.close();

        // DEBUG
        String[] pro = {
                MeasurementDataTable._ID,
                MeasurementDataTable.S_ID,
                MeasurementDataTable.AMOUNT,
                MeasurementDataTable.DATETIME
        };

        Cursor cur = reader.query(
                MeasurementDataTable.TABLE_NAME, // The table to query
                pro,         // The columns to return
                null,          // The columns for the WHERE clause
                null,      // The values for the WHERE clause
                null,               // don't group the rows
                null,               // don't filter by row groups
                null           // The sort order
        );

        while(cur.moveToNext()) {
            System.out.println("DEBUG M_DATA ID : " + cur.getInt(cur.getColumnIndexOrThrow(MeasurementDataTable._ID)) );
            System.out.println("DEBUG M_DATA SID : " + cur.getString(cur.getColumnIndexOrThrow(MeasurementDataTable.S_ID)) );
            System.out.println("DEBUG M_DATA AMOUNT : " + cur.getDouble(cur.getColumnIndexOrThrow(MeasurementDataTable.AMOUNT)) );
            System.out.println("DEBUG M_DATA DATETIME : " + cur.getLong(cur.getColumnIndexOrThrow(MeasurementDataTable.DATETIME)) );
        }

        cur.close();
    }
}



