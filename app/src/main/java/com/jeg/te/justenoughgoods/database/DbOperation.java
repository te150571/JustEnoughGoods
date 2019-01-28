package com.jeg.te.justenoughgoods.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jeg.te.justenoughgoods.utilities.MyApplication;

public class DbOperation {

    private static DbOperation dbOperation = new DbOperation();
    private DbOpenHelper dbOpenHelper = null;

    private DbOperation(){
        openConnection();
    }

    public String[][] selectData(boolean distinct, String table, String join, String[] projection, String where, String[] whereParam, String group, String having, String sortOrder, String limit){
        if(dbOpenHelper == null)
            openConnection();

        SQLiteDatabase reader = dbOpenHelper.getReadableDatabase();
        Cursor cursor = reader.query(
                distinct,
                table + (join != null ? " " + join : ""), // The table to query
                projection,         // The columns to return
                where,          // The columns for the WHERE clause
                whereParam,      // The values for the WHERE clause
                group,               // don't group the rows
                having,               // don't filter by row groups
                sortOrder,           // The sort order
                limit
        );

        String[][] result = new String[cursor.getCount()][cursor.getColumnCount()];
        int rowCount = 0;
        while(cursor.moveToNext()) {
            int columnCount = 0;
            for(String column : projection){
                result[rowCount][columnCount] = cursor.getString(cursor.getColumnIndexOrThrow(column));
                columnCount++;
            }
            rowCount++;
        }

        cursor.close();

        return result;
    }

    public long insertData(String table, String[] column, String[] value, String[] type){
        if(dbOpenHelper == null)
            openConnection();

        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        ContentValues data = new ContentValues();
        for(int i=0; i<column.length; i++){
            if(type[i].equals("int"))
                data.put(column[i], Integer.valueOf(value[i]));
            else if(type[i].equals("long"))
                data.put(column[i], Long.valueOf(value[i]));
            else if(type[i].equals("double"))
                data.put(column[i], Double.valueOf(value[i]));
            else
                data.put(column[i], value[i]);
        }
        return writer.insert(table, null, data);
    }

    public long updateData(String table, String[] column, String[] value, String[] type, String where, String[] whereParam){
        if(dbOpenHelper == null)
            openConnection();

        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        ContentValues data = new ContentValues();
        for(int i=0; i<column.length; i++){
            if(type[i].equals("int"))
                data.put(column[i], Integer.valueOf(value[i]));
            else if(type[i].equals("long"))
                data.put(column[i], Long.valueOf(value[i]));
            else if(type[i].equals("double"))
                data.put(column[i], Double.valueOf(value[i]));
            else
                data.put(column[i], value[i]);
        }
        try{
            return writer.update(table, data, where, whereParam);
        }
        catch (SQLiteConstraintException e){
            Log.w("SQL Update", "Failed update. " + e.getMessage());
            return 0;
        }
    }

    public void deleteData(String table, String where, String[] whereParam){

    }

    public void initDatabase(){
        if(dbOpenHelper == null)
            openConnection();

        SQLiteDatabase.deleteDatabase(MyApplication.getContext().getDatabasePath(dbOpenHelper.getDatabaseName()));
    }

    private void openConnection(){
        if(dbOpenHelper == null) {
            dbOpenHelper = new DbOpenHelper(MyApplication.getContext());
        }
    }

    public void closeConnection(){
        if(dbOpenHelper != null){
            dbOpenHelper.close();
            dbOpenHelper = null;
        }
    }

    public static DbOperation getDbOperation() {
        return dbOperation;
    }
}
