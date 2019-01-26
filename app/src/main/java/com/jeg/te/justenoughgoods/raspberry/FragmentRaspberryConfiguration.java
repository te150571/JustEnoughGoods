package com.jeg.te.justenoughgoods.raspberry;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jeg.te.justenoughgoods.main.ActivityMain;
import com.jeg.te.justenoughgoods.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Raspberry Pi configuration fragment.
 */
public class FragmentRaspberryConfiguration extends Fragment {

    // Constructor
    public FragmentRaspberryConfiguration(){}

    //GUIs
    private TextView textViewDeviceName;
    private TextView textViewDeviceAddress;

    // Called to do initial creation of the fragment.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get instances.
    }

    // Creates and returns the view hierarchy associated with the fragment.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_raspberry_configuration, container, false);
    }

    // Called when view generation is complete.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewDeviceName = view.findViewById(R.id.textView_deviceName);
        textViewDeviceAddress = view.findViewById(R.id.textView_deviceAddress);

        // Get Raspberry Pi information from application data.
        SharedPreferences data = getContext().getSharedPreferences("jegAppData", MODE_PRIVATE);
        textViewDeviceName.setText(data.getString("raspberryName", ""));
        textViewDeviceAddress.setText(data.getString("raspberryAddress", ""));

        view.findViewById(R.id.bt_raspberryDeviceSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityMain activityMain = (ActivityMain) getActivity();
                activityMain.transitionBluetoothDeviceListActivity();
            }
        });

        view.findViewById(R.id.bt_raspberryDeviceDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder( getContext() )
                        .setTitle( R.string.raspberry_device_delete_title )
                        .setMessage( R.string.raspberry_device_delete_text )
                        .setPositiveButton( R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                clearDevice();
                            }
                        })
                        .setNegativeButton( R.string.no, null)
                        .show();
            }
        });

        view.findViewById(R.id.bt_raspberryBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityMain activityMain = (ActivityMain) getActivity();
                activityMain.createFragmentMainView();
            }
        });
    }

    // Called when just before the user can operate.
    @Override
    public void onResume() {
        super.onResume();
    }

    public void clearDevice() {
        // Initialize Raspberry Pi data in application.
        SharedPreferences data = getContext().getSharedPreferences("jegAppData", MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putString("raspberryName", "");
        editor.putString("raspberryAddress", "");
        editor.apply();

        // Initialize TextView.
        textViewDeviceName.setText("");
        textViewDeviceAddress.setText("");
    }
}
