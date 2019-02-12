package com.example.bluetooth_test_androidstudio;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class LeDeviceListAdapter extends ArrayAdapter {

    private ArrayList<BluetoothDevice> deviceArray = new ArrayList<BluetoothDevice>();

    public LeDeviceListAdapter(@androidx.annotation.NonNull Context context, int resource) {
        super(context, resource);
    }

    @androidx.annotation.NonNull
    @Override
    public View getView(int position, @androidx.annotation.Nullable View convertView, @androidx.annotation.NonNull ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.listview_item_bluetooth_device, null);
        }
        TextView textViewTitle = (TextView) convertView.findViewById(R.id.item_title);
        TextView textViewDescription = (TextView) convertView.findViewById(R.id.item_description);
        textViewTitle.setText(deviceArray.get(position).getName());
        textViewDescription.setText(deviceArray.get(position).getAddress());


        return convertView;
    }

    @Override
    public int getCount() {
        return deviceArray.size();
    }


    public void addDevice(BluetoothDevice device) {
        deviceArray.add(device);
        notifyDataSetChanged();
    }
}
