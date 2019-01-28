package com.jeg.te.justenoughgoods.slave_configuration;

import com.jeg.te.justenoughgoods.list_item_data_class.Slave;
import com.jeg.te.justenoughgoods.database.DbContract;
import com.jeg.te.justenoughgoods.database.DbOperation;

/**
 * DbOperator class for Slave Configuration.
 */
public class SlaveConfigurationDbOperator {

    // DbOperation instance.
    private DbOperation dbOperation = null;

    public SlaveConfigurationDbOperator(){
        dbOperation = DbOperation.getDbOperation();
    }

    /**
     * Get slave data form database.
     * @param sId Slave's SID
     * @return Slave data.
     */
    public Slave getSlaveDataFromSId(String sId) {
        // SELECT
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

        // WHERE
        String selection = "s." + DbContract.SlavesTable.S_ID + " = ?";
        String[] selectionArgs = {sId};

        // JOIN
        String tableJoin = " as s LEFT JOIN (" +
                "SELECT * FROM "+ DbContract.MeasurementDataTable.TABLE_NAME + " a " +
                "WHERE NOT EXISTS ( SELECT 1 FROM " + DbContract.MeasurementDataTable.TABLE_NAME + " b WHERE a.s_id = b.s_id AND a.datetime < b.datetime ) ) as m ON s.s_id = m.s_id";

        String[][] data = dbOperation.selectData(
                false,
                DbContract.SlavesTable.TABLE_NAME, // The table to query
                tableJoin,
                projection,         // The columns to return
                selection,          // The columns for the WHERE clause
                selectionArgs,      // The values for the WHERE clause
                null,               // don't group the rows
                null,               // don't filter by row groups
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
