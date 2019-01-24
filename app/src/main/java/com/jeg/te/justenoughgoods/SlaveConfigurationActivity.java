package com.jeg.te.justenoughgoods;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.jeg.te.justenoughgoods.database.DbContract.MeasurementDataTable;
import com.jeg.te.justenoughgoods.database.DbContract.SlavesTable;
import com.jeg.te.justenoughgoods.database.DbOpenHelper;
import com.jeg.te.justenoughgoods.database.DbOperation;

import org.w3c.dom.Text;

import java.math.BigDecimal;

import static com.jeg.te.justenoughgoods.Utilities.convertLongToDateFormatDefault;

public class SlaveConfigurationActivity extends Activity {

    // 子機データベース
    // 子機データベース
    private DbOperation dbOperation = null;

    // 子機データ
    private String sId;
    private String name;
    private double amount;
    private double notificationAmount;
    private int amountNotificationEnable;
    private int slaveExceptionFlag;
    private int slaveExceptionNotificationEnable;
    private String lastUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // タイトル設定
        setTitle(R.string.app_name_slave_configuration);

        Intent intent = getIntent();
        sId = intent.getStringExtra("sid");

        dbOperation = DbOperation.getDbOperation();

        // 設定画面表示
        displaySlavesLabelNameConfig();
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
        }
        return false;
    }

    @Override
    protected void onPause()
    {
        dbOperation.closeConnection();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        dbOperation.closeConnection();
        super.onDestroy();
    }

    // 画面表示
    private void displaySlavesLabelNameConfig(){
        setContentView(R.layout.slave_configuration);

        setActionBar((Toolbar) findViewById(R.id.toolbar_slaveConfig));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // データ取得
        getSlaveData();

        // データをもとに表示
        TextView textViewSId = findViewById( R.id.textView_slaveConfigTitleCIdValue);
        textViewSId.setText( sId );

        EditText editTextName = findViewById( R.id.editText_slaveNameEdit );
        editTextName.setText( name );

        TextView textViewAmount = findViewById( R.id.textView_slaveConfigAmountValue);
        textViewAmount.setText( String.valueOf(new BigDecimal(amount  * 1000.0 ).setScale(3, BigDecimal.ROUND_HALF_UP)) );

        Switch switchAmountNotificationToggle = findViewById( R.id.switch_amountNotificationToggle );
        if(amountNotificationEnable == 1) switchAmountNotificationToggle.setChecked(true);
        else switchAmountNotificationToggle.setChecked(false);
        switchAmountNotificationToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    amountNotificationEnable = 1;
                    Toast.makeText( getApplication(), R.string.slave_config_amount_notification_on, Toast.LENGTH_SHORT ).show();
                }
                else {
                    amountNotificationEnable = 0;
                    Toast.makeText( getApplication(), R.string.slave_config_amount_notification_off, Toast.LENGTH_SHORT ).show();
                }
            }
        });

        EditText editTextNotificationAmountValue = findViewById( R.id.editText_slaveNotificationAmountEdit );
        editTextNotificationAmountValue.setText( String.valueOf( new BigDecimal(notificationAmount  * 1000.0 ).setScale(3, BigDecimal.ROUND_HALF_UP)) );

        TextView textViewExceptionFlag = findViewById( R.id.textView_slaveConfigExceptionNowStatus);
        if(slaveExceptionFlag == 1) textViewExceptionFlag.setText( MyApplication.getContext().getResources().getString( R.string.slave_config_exception_now_status_ng) );
        else textViewExceptionFlag.setText( MyApplication.getContext().getResources().getString( R.string.slave_config_exception_now_status_ok) );

        Switch switchExceptionNotificationToggle = findViewById( R.id.switch_exceptionNotificationToggle );
        if(slaveExceptionNotificationEnable == 1) switchExceptionNotificationToggle.setChecked(true);
        else switchExceptionNotificationToggle.setChecked(false);
        switchExceptionNotificationToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    slaveExceptionNotificationEnable = 1;
                    Toast.makeText( getApplication(), R.string.slave_config_exception_notification_on, Toast.LENGTH_SHORT ).show();
                }
                else {
                    slaveExceptionNotificationEnable = 0;
                    Toast.makeText( getApplication(), R.string.slave_config_exception_notification_off, Toast.LENGTH_SHORT ).show();
                }
            }
        });

        findViewById(R.id.bt_slaveConfigConfirm).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateSlave();
            }
        });
    }

    // 子機データの取得
    private void getSlaveData() {
        // データベースから取得
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
        String selection = "s." + SlavesTable.S_ID + " = ?"; // WHERE
        String[] selectionArgs = {sId};

        // JOIN
        String tableJoin = " as s LEFT JOIN (" +
                "SELECT * FROM "+ MeasurementDataTable.TABLE_NAME + " a " +
                "WHERE NOT EXISTS ( SELECT 1 FROM " + MeasurementDataTable.TABLE_NAME + " b WHERE a.s_id = b.s_id AND a.datetime < b.datetime ) ) as m ON s.s_id = m.s_id";

        String[][] data = dbOperation.selectData(
                false,
                SlavesTable.TABLE_NAME, // The table to query
                tableJoin,
                projection,         // The columns to return
                selection,          // The columns for the WHERE clause
                selectionArgs,      // The values for the WHERE clause
                null,               // don't group the rows
                null,               // don't filter by row groups
                null,
                null
        );

        for(String[] value : data) {
            name = value[1];
            amount = Double.valueOf(value[6]);
            notificationAmount = Double.valueOf(value[6]);
            amountNotificationEnable = Integer.valueOf(value[3]);
            slaveExceptionFlag = Integer.valueOf(value[4]);
            slaveExceptionNotificationEnable = Integer.valueOf(value[5]);
            lastUpdate = convertLongToDateFormatDefault(Long.valueOf(value[7])) ;
        }
    }

    // 子機のデータ更新
    private void updateSlave(){
        // データの取得
        EditText editTextName = findViewById(R.id.editText_slaveNameEdit);
        EditText editTextNotificationAmount = findViewById(R.id.editText_slaveNotificationAmountEdit);
        Switch switchAmountNotification = findViewById(R.id.switch_amountNotificationToggle);
        Switch switchExceptionNotification = findViewById(R.id.switch_exceptionNotificationToggle);

        long result = dbOperation.updateData(
                SlavesTable.TABLE_NAME,
                new String[]{SlavesTable.NAME, SlavesTable.NOTIFICATION_AMOUNT, SlavesTable.AMOUNT_NOTIFICATION_ENABLE, SlavesTable.EXCEPTION_NOTIFICATION_ENABLE},
                new String[]{String.valueOf(editTextName.getText()), String.valueOf(editTextNotificationAmount.getText()), (switchAmountNotification.isChecked() ? "1" : "0"), (switchExceptionNotification.isChecked() ? "1" : "0")},
                new String[]{"string", "double", "int", "int"},
                "S_ID = ?",
                new String[]{sId}
        );

        if(result > 0)
            Toast.makeText( getApplication(), R.string.slave_config_done, Toast.LENGTH_SHORT ).show();
    }
}