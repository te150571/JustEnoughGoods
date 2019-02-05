package com.jeg.te.justenoughgoods.database;

import android.util.Log;

import com.jeg.te.justenoughgoods.list_item_data_class.Slave;

import java.util.ArrayList;

public class DbOperationForSlaveData {

    private DbOperation dbOperation;

    public DbOperationForSlaveData(){
        dbOperation = DbOperation.getDbOperation();
    }

    // Selecting methods.

    /**
     * SQL of all slave count.
     * @return int : result of slave count.
     */
    public int getSlaveListCountAll(){
        String[] projection = { DbContract.SlavesTable.S_ID };
        String order = DbContract.SlavesTable.S_ID + " ASC";
        String[][] result = dbOperation.selectDataNotWhereAndGroupHaving(
                true,
                DbContract.SlavesTable.TABLE_NAME,
                null,
                projection,
                order,
                null
        );

        return result.length;
    }

    public int getSlaveCountNew(){
        String[] projection = { DbContract.SlavesTable.S_ID };
        String where = DbContract.SlavesTable.IS_NEW + " = ?";
        String[] whereParam = { "1" };
        String order = DbContract.SlavesTable.S_ID + " ASC";
        String[][] result = dbOperation.selectDataNotGroupAndHaving(
                true,
                DbContract.SlavesTable.TABLE_NAME,
                null,
                projection,
                where,
                whereParam,
                order,
                null
        );

        return result.length;
    }

    public int getSlaveCountLack(){
        String[] projection = {
                "s." + DbContract.SlavesTable.S_ID,
                "s." + DbContract.SlavesTable.NAME,
                "s." + DbContract.SlavesTable.NOTIFICATION_AMOUNT,
                "m." + DbContract.MeasurementDataTable.AMOUNT,
                "m." + DbContract.MeasurementDataTable.DATETIME
        };
        String join = "as s LEFT JOIN (" +
                "SELECT * FROM "+ DbContract.MeasurementDataTable.TABLE_NAME + " a " +
                "WHERE NOT EXISTS ( SELECT 1 FROM " + DbContract.MeasurementDataTable.TABLE_NAME + " b WHERE a.s_id = b.s_id AND a.datetime < b.datetime ) ) as m ON s.s_id = m.s_id ";
        String where = "s." + DbContract.SlavesTable.NOTIFICATION_AMOUNT + " >= " + "m." + DbContract.MeasurementDataTable.AMOUNT;
        String order = "s." + DbContract.SlavesTable.S_ID + " ASC";
        String[][] result = dbOperation.selectDataNotGroupAndHaving(
                false,
                DbContract.SlavesTable.TABLE_NAME,
                join,
                projection,
                where,
                null,
                order,
                null
        );

        return result.length;
    }

    public ArrayList<Slave> getSlaveListWithRemainingAmountData(){
        String[] projection = {
                "s." + DbContract.SlavesTable.S_ID,
                "s." + DbContract.SlavesTable.NAME,
                "s." + DbContract.SlavesTable.NOTIFICATION_AMOUNT,
                "m." + DbContract.MeasurementDataTable.AMOUNT,
                "m." + DbContract.MeasurementDataTable.DATETIME
        };
        String join = "as s LEFT JOIN (" +
                "SELECT * FROM "+ DbContract.MeasurementDataTable.TABLE_NAME + " a " +
                "WHERE NOT EXISTS ( SELECT 1 FROM " + DbContract.MeasurementDataTable.TABLE_NAME + " b WHERE a.s_id = b.s_id AND a.datetime < b.datetime ) ) as m ON s.s_id = m.s_id ";
        String order = "s." + DbContract.SlavesTable.S_ID + " ASC";

        String[][] result = dbOperation.selectDataNotWhereAndGroupHaving(
                false,
                DbContract.SlavesTable.TABLE_NAME,
                join,
                projection,
                order,
                null
        );

        ArrayList<Slave> slaves = new ArrayList<>();
        for(String[] rowData : result){
            Slave slave = new Slave();
            slave.setSId( rowData[0] );
            slave.setName( rowData[1] );
            slave.setAmount( Double.valueOf(rowData[3]) );
            slave.setNotificationAmount( Double.valueOf(rowData[2]) );
            slave.setLastUpdate( Long.valueOf(rowData[4]) );
            slaves.add(slave);
        }

        return slaves;
    }

    public ArrayList<Slave> getSlaveListWithRemainingAmountDataOnlyLack(){
        String[] projection = {
                "s." + DbContract.SlavesTable.S_ID,
                "s." + DbContract.SlavesTable.NAME,
                "s." + DbContract.SlavesTable.NOTIFICATION_AMOUNT,
                "m." + DbContract.MeasurementDataTable.AMOUNT,
                "m." + DbContract.MeasurementDataTable.DATETIME
        };
        String join = "as s LEFT JOIN (" +
                "SELECT * FROM "+ DbContract.MeasurementDataTable.TABLE_NAME + " a " +
                "WHERE NOT EXISTS ( SELECT 1 FROM " + DbContract.MeasurementDataTable.TABLE_NAME + " b WHERE a.s_id = b.s_id AND a.datetime < b.datetime ) ) as m ON s.s_id = m.s_id ";
        String where = "s." + DbContract.SlavesTable.NOTIFICATION_AMOUNT + " >= " + "m." + DbContract.MeasurementDataTable.AMOUNT;
        String order = "s." + DbContract.SlavesTable.S_ID + " ASC";
        String[][] result = dbOperation.selectDataNotGroupAndHaving(
                false,
                DbContract.SlavesTable.TABLE_NAME,
                join,
                projection,
                where,
                null,
                order,
                null
        );

        ArrayList<Slave> slaves = new ArrayList<>();
        for(String[] rowData : result){
            Slave slave = new Slave();
            slave.setSId( rowData[0] );
            slave.setName( rowData[1] );
            slave.setAmount( Double.valueOf(rowData[3]) );
            slave.setNotificationAmount( Double.valueOf(rowData[2]) );
            slave.setLastUpdate( Long.valueOf(rowData[4]) );
            slaves.add(slave);
        }

        return slaves;
    }

    public ArrayList<Slave> getSlaveListWithIsNewParam(boolean onlyNew){
        String[] projection = {
                "s." + DbContract.SlavesTable.S_ID,
                "s." + DbContract.SlavesTable.NAME,
                "s." + DbContract.SlavesTable.IS_NEW,
                "m." + DbContract.MeasurementDataTable.DATETIME
        };
        String tableJoin = "as s LEFT JOIN (" +
                "SELECT * FROM "+ DbContract.MeasurementDataTable.TABLE_NAME + " a " +
                "WHERE NOT EXISTS ( SELECT 1 FROM " + DbContract.MeasurementDataTable.TABLE_NAME + " b WHERE a.s_id = b.s_id AND a.datetime < b.datetime ) ) as m ON s.s_id = m.s_id";

        String where = null;
        String[] whereParam = null;
        if(onlyNew){
            where = "s." + DbContract.SlavesTable.IS_NEW + " = ?";
            whereParam = new String[]{"1"};
        }
        String sortOrder = "s." + DbContract.SlavesTable.S_ID + " ASC";

        String[][] selectResult = dbOperation.selectDataNotGroupAndHaving(
                false,
                DbContract.SlavesTable.TABLE_NAME,
                tableJoin,
                projection,
                where,
                whereParam,
                sortOrder,
                null
        );

        ArrayList<Slave> slavesData = new ArrayList<>();
        for(String[] rowData : selectResult){
            Slave slave = new Slave();
            slave.setSId( rowData[0] );
            slave.setName( rowData[1] );
            slave.setIsNew( Integer.valueOf(rowData[2]) );
            slave.setLastUpdate( Long.valueOf(rowData[3]) );
            slavesData.add(slave);
        }

        return slavesData;
    }

    /**
     * Get slave data form database.
     * @param sId Slave's SID
     * @return Slave data.
     */
    public Slave getSlaveDataFromSId(String sId) {
        String[] projection = {
                "s." + DbContract.SlavesTable.S_ID,
                "s." + DbContract.SlavesTable.NAME,
                "s." + DbContract.SlavesTable.NOTIFICATION_AMOUNT,
                "s." + DbContract.SlavesTable.AMOUNT_NOTIFICATION_ENABLE,
                "s." + DbContract.SlavesTable.EXCEPTION_FLAG,
                "s." + DbContract.SlavesTable.EXCEPTION_NOTIFICATION_ENABLE,
                "m." + DbContract.MeasurementDataTable.AMOUNT,
                "m." + DbContract.MeasurementDataTable.DATETIME
        };

        String selection = "s." + DbContract.SlavesTable.S_ID + " = ?";
        String[] selectionArgs = {sId};

        String tableJoin = " as s LEFT JOIN (" +
                "SELECT * FROM "+ DbContract.MeasurementDataTable.TABLE_NAME + " a " +
                "WHERE NOT EXISTS ( SELECT 1 FROM " + DbContract.MeasurementDataTable.TABLE_NAME + " b WHERE a.s_id = b.s_id AND a.datetime < b.datetime ) ) as m ON s.s_id = m.s_id";

        String[][] data = dbOperation.selectDataNotGroupAndHaving(
                false,
                DbContract.SlavesTable.TABLE_NAME,
                tableJoin,
                projection,
                selection,
                selectionArgs,
                null,
                null
        );

        Slave result = new Slave();
        result.setSId(sId);

        for(String[] value : data) {
            result.setName(value[1]);
            result.setAmount(Double.valueOf(value[6]));
            result.setNotificationAmount(Double.valueOf(value[2]));
            result.setAmountNotificationEnable(Integer.valueOf(value[3]));
            result.setExceptionFlag(Integer.valueOf(value[4]));
            result.setExceptionNotificationFlag(Integer.valueOf(value[5]));
            result.setLastUpdate(Long.valueOf(value[7]));
        }

        return result;
    }

    /**
     * Get Log data form database.
     */
    public String[][] getMeasurementData(String sId, int showingMonth, boolean isMonthly) {
        String[] projection = {
                DbContract.MeasurementDataTable.AMOUNT,
                DbContract.MeasurementDataTable.DATETIME,
                DbContract.MeasurementDataTable.MONTH_NUM
        };

        String selection;
        String[] selectionArgs = new String[(isMonthly ? 2 : 1)];

        // If monthly set month to where.
        if (isMonthly) {
            selection = DbContract.MeasurementDataTable.S_ID + " = ? AND " + DbContract.MeasurementDataTable.MONTH_NUM + " = ?";
            selectionArgs[0] = sId;
            selectionArgs[1] = String.valueOf(showingMonth);
        } else {
            selection = DbContract.MeasurementDataTable.S_ID + " = ?";
            selectionArgs[0] = sId;
        }

        // ORDER BY
        String sortOrder = DbContract.MeasurementDataTable.DATETIME + " ASC";

        String[][] result = dbOperation.selectDataNotGroupAndHaving(
                false,
                DbContract.MeasurementDataTable.TABLE_NAME,
                null,
                projection,
                selection,
                selectionArgs,
                sortOrder,
                null
        );

        return result;
    }

    /**
     * Get SID from Slaves Table.
     * @return ArrayList(String) : SIDs
     */
    public ArrayList<String> getSIdInSlavesTable(){
        String[][] sIds = dbOperation.selectDataNotWhereAndGroupHaving(
        false,
                DbContract.SlavesTable.TABLE_NAME,
                null,
                new String[]{DbContract.SlavesTable.S_ID},
                null,
                null
        );

        // Make a two-dimensional array a one-dimensional array.
        ArrayList<String> result = new ArrayList<>();
        for (String[] sId : sIds) {
            result.add(sId[0]);
        }

        return result;
    }

    /**
     * Get SID from Measurement Table.
     * @return ArrayList(String) : SIDs
     */
    public ArrayList<String> getSIdInMeasurementTable(){
        String[][] sIds = dbOperation.selectDataNotWhereAndGroupHaving(
                true,
                DbContract.MeasurementDataTable.TABLE_NAME,
                null, new String[]{DbContract.MeasurementDataTable.S_ID},
                null,
                null
        );

        // Make a two-dimensional array a one-dimensional array.
        ArrayList<String> result = new ArrayList<>();
        for (String[] sId : sIds) {
            result.add(sId[0]);
        }

        return result;
    }

    public String getDatetimeOfLastUpdate(){
        String[][] datetime = dbOperation.selectDataNotWhereAndGroupHaving(
                false,
                DbContract.MeasurementDataTable.TABLE_NAME,
                null,
                new String[]{"max(" + DbContract.MeasurementDataTable.DATETIME + ")"},
                null,
                null
        );

        return datetime[0][0];
    }

    // Inserting methods.

    public void putMeasurementData(ArrayList<String> updateData){
        for (String datum : updateData) {
            if (!datum.equals("1")) {
                String[] splitData = datum.split(",", 0);
                Log.d("Inserting new data", "splitData[0] + splitData[1] + splitData[2] + splitData[3]");
                dbOperation.insertData(
                        DbContract.MeasurementDataTable.TABLE_NAME,
                        new String[]{DbContract.MeasurementDataTable.S_ID, DbContract.MeasurementDataTable.AMOUNT, DbContract.MeasurementDataTable.DATETIME, DbContract.MeasurementDataTable.MONTH_NUM},
                        new String[]{splitData[0], splitData[1], splitData[2], splitData[3]},
                        new String[]{"string", "double", "long", "int"}
                );
            }
        }
    }

    public void putNewSlave(ArrayList<String> newSlaves){
        for(String sid : newSlaves){
            dbOperation.insertData(
                    DbContract.SlavesTable.TABLE_NAME,
                    new String[]{DbContract.SlavesTable.S_ID, DbContract.SlavesTable.NAME, DbContract.SlavesTable.NOTIFICATION_AMOUNT},
                    new String[]{sid, "子機" + sid, "0.01"},
                    new String[]{"string", "string", "double"}
            );
        }
    }

    // Updating methods.
    /**
     * Update slave.
     * @param slave Updating slave data.
     * @return Result (Number of updated rows)
     */
    public long updateSlave(Slave slave){

        return dbOperation.updateData(
                DbContract.SlavesTable.TABLE_NAME,
                new String[]{DbContract.SlavesTable.NAME, DbContract.SlavesTable.NOTIFICATION_AMOUNT, DbContract.SlavesTable.AMOUNT_NOTIFICATION_ENABLE, DbContract.SlavesTable.EXCEPTION_NOTIFICATION_ENABLE, DbContract.SlavesTable.IS_NEW},
                new String[]{slave.getName(), String.valueOf(slave.getNotificationAmount() / 1000), String.valueOf(slave.getAmountNotificationEnable()), String.valueOf(slave.getExceptionNotificationFlag()), "0"},
                new String[]{"string", "double", "int", "int", "int"},
                "S_ID = ?",
                new String[]{slave.getSId()}
        );
    }
}
