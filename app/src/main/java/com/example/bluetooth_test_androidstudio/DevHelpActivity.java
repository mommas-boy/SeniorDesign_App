package com.example.bluetooth_test_androidstudio;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;


import static com.example.bluetooth_test_androidstudio.ScanActivity.BLUETOOTH_DEVICE_EXTRA;

public class DevHelpActivity extends Activity {

    private BluetoothDevice mDevice = null;
    private SplashPadComService mService = null;
    private boolean mBound = false;

    private Button allOnButton;
    private Button centerOnlyButton;


    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SplashPadComService.LocalBinder binder = (SplashPadComService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_buttons);
        Intent intent = getIntent();
        mDevice = intent.getParcelableExtra(BLUETOOTH_DEVICE_EXTRA);

        intent = new Intent(this, SplashPadComService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mService != null) {
            mService.connectToDevice(mDevice);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initializeViews() {
        allOnButton = findViewById(R.id.all_on_button);
        centerOnlyButton = findViewById(R.id.only_center_button);

        allOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mService != null) {
                    for(int i = 0; i < 7; i++) {
                        mService.turnOnNozzle(i);
                    }
                }
            }
        });

        centerOnlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mService != null) {
                    for(int i = 0; i < 7; i++) {
                        if (i == 3) {
                            mService.turnOnNozzle(3);
                        } else {
                            mService.turnOffNozzle(i);
                        }
                    }
                }
            }
        });


    }
}
