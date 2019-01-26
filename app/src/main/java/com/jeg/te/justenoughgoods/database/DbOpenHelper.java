package com.jeg.te.justenoughgoods.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jeg.te.justenoughgoods.database.DbContract.SlavesTable;
import com.jeg.te.justenoughgoods.database.DbContract.MeasurementDataTable;

public class DbOpenHelper extends SQLiteOpenHelper {

    // データーベースのバージョン
    public static final int DATABASE_VERSION = 1;
    // データーベース名
    private static final String DATABASE_NAME = "db_jeg";

    // 子機設定テーブル
    private static final String SQL_CREATE_SLAVES =
            "CREATE TABLE " + SlavesTable.TABLE_NAME + " (" +
                    SlavesTable.S_ID + " TEXT PRIMARY KEY," +
                    SlavesTable.NAME + " TEXT UNIQUE  NOT NULL," +
                    SlavesTable.NOTIFICATION_AMOUNT + " REAL NOT NULL," +
                    SlavesTable.AMOUNT_NOTIFICATION_ENABLE + " INTEGER NOT NULL DEFAULT 1," +
                    SlavesTable.EXCEPTION_FLAG + " INTEGER NOT NULL DEFAULT 0," +
                    SlavesTable.EXCEPTION_NOTIFICATION_ENABLE + " INTEGER NOT NULL DEFAULT 1," +
                    SlavesTable.IS_NEW + " INTEGER DEFAULT 1)";

    private static final String SQL_DELETE_SLAVES =
            "DROP TABLE IF EXISTS " + SlavesTable.TABLE_NAME;

    // 計測データテーブル
    private static final String SQL_CREATE_MEASUREMENT_DATA =
            "CREATE TABLE " + MeasurementDataTable.TABLE_NAME + " (" +
                    MeasurementDataTable._ID + " INTEGER PRIMARY KEY," +
                    MeasurementDataTable.S_ID + " TEXT NOT NULL," +
                    MeasurementDataTable.AMOUNT + " REAL NOT NULL," +
                    MeasurementDataTable.DATETIME + " INTEGER NOT NULL," +
                    MeasurementDataTable.MONTH_NUM + " INTEGER NOT NULL)";

    private static final String SQL_DELETE_MEASUREMENT =
            "DROP TABLE IF EXISTS " + SlavesTable.TABLE_NAME;

    public DbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // テーブル作成
        db.execSQL(SQL_CREATE_SLAVES);
        db.execSQL(SQL_CREATE_MEASUREMENT_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
        // アップデートの判別、古いバージョンは削除して新規作成
        db.execSQL(SQL_DELETE_SLAVES);
        db.execSQL(SQL_DELETE_MEASUREMENT);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
