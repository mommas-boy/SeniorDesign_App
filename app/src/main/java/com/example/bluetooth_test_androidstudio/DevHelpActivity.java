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
    private Button rainbowButton;
    private Button toggleCenterButton;
    private Button allWhiteButton;
    private Button allColorsOffButton;
    private Button jiggleBluetoothButton;

    public Button getCenterOnlyButton() {
        return centerOnlyButton;
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SplashPadComService.LocalBinder binder = (SplashPadComService.LocalBinder) service;
            mService = binder.getService();
            mService.connectToDevice(mDevice);
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

        //mService.connectToDevice(mDevice);

        initializeViews();
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
        rainbowButton = findViewById(R.id.rainbow_button);
        toggleCenterButton = findViewById(R.id.toggle_center_button);
        allWhiteButton = findViewById(R.id.all_white_button);
        allColorsOffButton = findViewById(R.id.colors_off_button);
        jiggleBluetoothButton = findViewById(R.id.jiggle_bluetooth_button);


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

        rainbowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mService != null) {
                    mService.setColor(0, 0x0FFF, 0x0000, 0x0000);
                    mService.setColor(1, 0x0FFF, 0x0FFF, 0x0000);
                    mService.setColor(2, 0x0000, 0x0FFF, 0x0000);
                    mService.setColor(3, 0x0000, 0x0FFF, 0x0FFF);
                    mService.setColor(4, 0x0000, 0x0000, 0x0FFF);
                    mService.setColor(5, 0x0FFF, 0x0000, 0x0FFF);
                    mService.setColor(6, 0x0FFF, 0x0FFF, 0x0FFF);
                }
            }
        });

        toggleCenterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mService != null) {
                    int centerMode = mService.getNozzleMode(3);
                    centerMode = centerMode ^ 0x0001;
                    mService.setNozzleMode(3, centerMode);
                }
            }
        });

        allWhiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mService != null) {
                    mService.setColor(0, 0x0FFF, 0x0FFF, 0x0FFF);
                    mService.setColor(1, 0x0FFF, 0x0FFF, 0x0FFF);
                    mService.setColor(2, 0x0FFF, 0x0FFF, 0x0FFF);
                    mService.setColor(3, 0x0FFF, 0x0FFF, 0x0FFF);
                    mService.setColor(4, 0x0FFF, 0x0FFF, 0x0FFF);
                    mService.setColor(5, 0x0FFF, 0x0FFF, 0x0FFF);
                    mService.setColor(6, 0x0FFF, 0x0FFF, 0x0FFF);
                }
            }
        });

        allColorsOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mService != null) {
                    mService.setColor(0, 0x0000, 0x0000, 0x0000);
                    mService.setColor(1, 0x0000, 0x0000, 0x0000);
                    mService.setColor(2, 0x0000, 0x0000, 0x0000);
                    mService.setColor(3, 0x0000, 0x0000, 0x0000);
                    mService.setColor(4, 0x0000, 0x0000, 0x0000);
                    mService.setColor(5, 0x0000, 0x0000, 0x0000);
                    mService.setColor(6, 0x0000, 0x0000, 0x0000);
                }

            }
        });

        jiggleBluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mService.setColor(3, 0x0000, 0x0000, 0x0000);
                mService.setColor(3, 0x0FFF, 0x0FFF, 0x0FFF);
            }
        });

    }
}
