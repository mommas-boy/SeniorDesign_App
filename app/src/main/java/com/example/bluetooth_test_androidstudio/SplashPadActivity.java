package com.example.bluetooth_test_androidstudio;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import static com.example.bluetooth_test_androidstudio.ScanActivity.BLUETOOTH_DEVICE_EXTRA;

public class SplashPadActivity extends Activity {

    private BluetoothDevice mDevice = null;
    private SplashPadComService mService = null;
    private boolean mBound = false;


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
        setContentView(R.layout.activity_splashpad);
        Intent intent = getIntent();
        mDevice = intent.getParcelableExtra(BLUETOOTH_DEVICE_EXTRA);

        intent = new Intent(this, SplashPadComService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        //mService.connectToDevice(mDevice);
    }

    @Override
    protected void onStart() {
        super.onStart();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id) {
            case R.id.action_settings:
                Toast.makeText(this, "Wat", Toast.LENGTH_LONG).show();
                return true;
            case R.id.menu_dev_buttons:
                Intent intent = new Intent(this, DevHelpActivity.class);
                intent.putExtra(BLUETOOTH_DEVICE_EXTRA, mDevice);
                startActivity(intent);
                return true;
            default:
        }

        return super.onOptionsItemSelected(item);
    }
}
