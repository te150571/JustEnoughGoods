package com.jeg.te.justenoughgoods.utilities;

import com.jeg.te.justenoughgoods.database.DbContract;
import com.jeg.te.justenoughgoods.database.DbOperation;
import com.jeg.te.justenoughgoods.list_item_data_class.Slave;

import java.util.ArrayList;

public class DbOperationForSlaveData {

    private DbOperation dbOperation;

    public DbOperationForSlaveData(){
        dbOperation = DbOperation.getDbOperation();
    }

    public ArrayList<Slave> getSlaveListWithRemainingAmountData(boolean onlyLack){
        // Get slaves data from database.
        // SELECT
        String[] projection = {
                "s." + DbContract.SlavesTable.S_ID,
                "s." + DbContract.SlavesTable.NAME,
                "s." + DbContract.SlavesTable.NOTIFICATION_AMOUNT,
                "m." + DbContract.MeasurementDataTable.AMOUNT,
                "m." + DbContract.MeasurementDataTable.DATETIME
        };
        // JOIN
        String tableJoin = "as s LEFT JOIN (" +
                "SELECT * FROM "+ DbContract.MeasurementDataTable.TABLE_NAME + " a " +
                "WHERE NOT EXISTS ( SELECT 1 FROM " + DbContract.MeasurementDataTable.TABLE_NAME + " b WHERE a.s_id = b.s_id AND a.datetime < b.datetime ) ) as m ON s.s_id = m.s_id";

        // ORDER
        String sortOrder = "s." + DbContract.SlavesTable.S_ID + " ASC";

        String[][] selectResult = dbOperation.selectData(
                false,
                DbContract.SlavesTable.TABLE_NAME,
                tableJoin,
                projection,
                null,
                null,
                null,
                null,
                sortOrder,
                null
        );

        ArrayList<Slave> slaves = new ArrayList<>();
        for(String[] rowData : selectResult){
            Slave slave = new Slave();
            slave.setSId( rowData[0] );
            slave.setName( rowData[1] );
            slave.setAmount( Double.valueOf(rowData[3]) );
            slave.setNotificationAmount( Double.valueOf(rowData[2]) );
            slave.setLastUpdate( Long.valueOf(rowData[4]) );
            slaves.add(slave);
        }

        if(onlyLack){
            ArrayList<Slave> lackSlaves = new ArrayList<>();
            for(Slave slave : slaves){
                if(slave.getAmount() - slave.getNotificationAmount() <= 0)
                    lackSlaves.add(slave);
            }
            return lackSlaves;
        }

        return slaves;
    }

    public ArrayList<Slave> getSlaveListWithIsNewParam(boolean onlyNew){
        // Get slaves data from database.
        // SELECT
        String[] projection = {
                "s." + DbContract.SlavesTable.S_ID,
                "s." + DbContract.SlavesTable.NAME,
                "s." + DbContract.SlavesTable.IS_NEW,
                "m." + DbContract.MeasurementDataTable.DATETIME
        };
        // JOIN
        String tableJoin = "as s LEFT JOIN (" +
                "SELECT * FROM "+ DbContract.MeasurementDataTable.TABLE_NAME + " a " +
                "WHERE NOT EXISTS ( SELECT 1 FROM " + DbContract.MeasurementDataTable.TABLE_NAME + " b WHERE a.s_id = b.s_id AND a.datetime < b.datetime ) ) as m ON s.s_id = m.s_id";

        String where = null;
        String[] whereParam = null;
        if(onlyNew){
            where = "s.is_new = ?";
            whereParam = new String[]{"1"};
        }
        // ORDER
        String sortOrder = "s." + DbContract.SlavesTable.S_ID + " ASC";

        String[][] selectResult = dbOperation.selectData(
                false,
                DbContract.SlavesTable.TABLE_NAME,
                tableJoin,
                projection,
                where,
                whereParam,
                null,
                null,
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
}
