package com.example.bluetooth_test_androidstudio;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class LeDeviceListAdapter extends ArrayAdapter {

    @androidx.annotation.NonNull
    @Override
    public View getView(int position, @androidx.annotation.Nullable View convertView, @androidx.annotation.NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    public LeDeviceListAdapter(@androidx.annotation.NonNull Context context, int resource, @androidx.annotation.NonNull Object[] objects) {
        super(context, resource, objects);
    }

    public void addDevice(BluetoothDevice device) {

    }
}
