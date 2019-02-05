package com.jeg.te.justenoughgoods.slave_configuration;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jeg.te.justenoughgoods.database.DbOperationForSlaveData;
import com.jeg.te.justenoughgoods.list_item_data_class.Slave;
import com.jeg.te.justenoughgoods.main.ActivityMain;
import com.jeg.te.justenoughgoods.R;

import java.math.BigDecimal;

/**
 * Slave configuration fragment.
 */
public class FragmentSlaveConfiguration extends Fragment {

    // DB operator.
    private DbOperationForSlaveData dbOperationForSlaveData;

    // Slave data.
    private Slave slave;

    private String callFrom;

    // Constructor
    public FragmentSlaveConfiguration() {}

    // Called to do initial creation of the fragment.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create slave to config.
        slave = new Slave();

        // Get Bundle values and set to Slave.
        Bundle args = getArguments();
        if(args != null){
            slave.setSId(args.getString("sid"));
            callFrom = args.getString("callFrom");
        }

        // Get instance.
        dbOperationForSlaveData = new DbOperationForSlaveData();
    }

    // Creates and returns the view hierarchy associated with the fragment.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_slave_configuration, container, false);
    }

    // Called when view generation is complete.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get slave data and display it.
        slave = dbOperationForSlaveData.getSlaveDataFromSId(slave.getSId());

        TextView textViewSlaveCId = view.findViewById(R.id.textView_slaveConfigTitleCIdValue);
        textViewSlaveCId.setText(slave.getSId());

        EditText editTextSlaveName = view.findViewById(R.id.editText_slaveNameEdit);
        editTextSlaveName.setText(slave.getName());
        editTextSlaveName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                slave.setName(editable.toString());
            }
        });

        TextView textViewAmount = view.findViewById( R.id.textView_slaveConfigAmountValue);
        textViewAmount.setText( String.valueOf(new BigDecimal(slave.getAmount()  * 1000.0 ).setScale(2, BigDecimal.ROUND_HALF_UP)) );

        Switch switchAmountNotificationToggle = view.findViewById( R.id.switch_amountNotificationToggle );
        if(slave.getAmountNotificationEnable() == 1) switchAmountNotificationToggle.setChecked(true);
        else switchAmountNotificationToggle.setChecked(false);
        switchAmountNotificationToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    slave.setAmountNotificationEnable(1);
                }
                else {
                    slave.setAmountNotificationEnable(0);
                }
            }
        });

        EditText editTextNotificationAmountValue = view.findViewById( R.id.editText_slaveNotificationAmountEdit );
        editTextNotificationAmountValue.setText( String.valueOf( new BigDecimal(slave.getNotificationAmount()  * 1000.0 ).setScale(2, BigDecimal.ROUND_HALF_UP)) );
        editTextNotificationAmountValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String amount = editable.toString();
                if(amount.equals("")){
                    amount = "0";
                }
                slave.setNotificationAmount(Double.valueOf(amount));
            }
        });

        TextView textViewExceptionFlag = view.findViewById( R.id.textView_slaveConfigExceptionNowStatus);
        if(slave.getExceptionFlag() == 1) textViewExceptionFlag.setText( getString( R.string.slave_config_exception_now_status_ng) );
        else textViewExceptionFlag.setText( getString( R.string.slave_config_exception_now_status_ok) );

        Switch switchExceptionNotificationToggle = view.findViewById( R.id.switch_exceptionNotificationToggle );
        if(slave.getExceptionNotificationFlag() == 1) switchExceptionNotificationToggle.setChecked(true);
        else switchExceptionNotificationToggle.setChecked(false);
        switchExceptionNotificationToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    slave.setExceptionNotificationFlag(1);
                }
                else {
                    slave.setExceptionNotificationFlag(0);
                }
            }
        });

        view.findViewById(R.id.bt_slaveConfigBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityMain activityMain = (ActivityMain) getActivity();
                if(callFrom.equals("remaining")){
                    activityMain.createFragmentRemainingAmount();
                }
                else if(callFrom.equals("slaveList")){
                    activityMain.createFragmentSlaveList();
                }
            }
        });

        view.findViewById(R.id.bt_slaveConfigConfirm).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                long result = dbOperationForSlaveData.updateSlave(slave);

                if(result > 0)
                    Toast.makeText(getContext(), R.string.slave_config_done, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), R.string.slave_config_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
