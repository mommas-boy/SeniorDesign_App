package com.example.bluetooth_test_androidstudio;


import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

// A service that interacts with the BLE device via the Android BLE API.
public class SplashPadComService extends Service {

    private final static String TAG = SplashPadComService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothGatt mBluetoothGatt;
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    private int modeArray[] = new int[7];
    private int durationArray[] = new int[7];
    private Color colorArray[] = new Color[7];

    private int connectionState = STATE_DISCONNECTED;

    private  BluetoothGattService nozzleService;
    private  BluetoothGattCharacteristic nozzleCharacteristics[] = new BluetoothGattCharacteristic[7];

    //private BluetoothGattCharacteristic nozzle0Characteristic;
    //private BluetoothGattCharacteristic nozzle1Characteristic;
    //private BluetoothGattCharacteristic nozzle2Characteristic;
    //private BluetoothGattCharacteristic nozzle3Characteristic;
    //private BluetoothGattCharacteristic nozzle4Characteristic;
    //private BluetoothGattCharacteristic nozzle5Characteristic;
    //private BluetoothGattCharacteristic nozzle6Characteristic;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";


    public final static String NOZZLE_SERVICE_UUID = "0000babe-0000-1000-8000-00805f9b34fb";

    public final static String NOZZLE_0_CHARACTERISTIC_UUID = "f000a0e0-0451-4000-b000-000000000000";
    public final static String NOZZLE_1_CHARACTERISTIC_UUID = "f000a0e1-0451-4000-b000-000000000000";
    public final static String NOZZLE_2_CHARACTERISTIC_UUID = "f000a0e2-0451-4000-b000-000000000000";
    public final static String NOZZLE_3_CHARACTERISTIC_UUID = "f000a0e3-0451-4000-b000-000000000000";
    public final static String NOZZLE_4_CHARACTERISTIC_UUID = "f000a0e4-0451-4000-b000-000000000000";
    public final static String NOZZLE_5_CHARACTERISTIC_UUID = "f000a0e5-0451-4000-b000-000000000000";
    public final static String NOZZLE_6_CHARACTERISTIC_UUID = "f000a0e6-0451-4000-b000-000000000000";

    //public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);

    public class Color {
        public short red;
        public short green;
        public short blue;
    }


    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        SplashPadComService getService() {
            // Return this instance of LocalService so clients can call public methods
            return SplashPadComService.this;
        }
    }

    // Various callback methods defined by the BLE API.
    private final BluetoothGattCallback mGattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    String intentAction;
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        intentAction = ACTION_GATT_CONNECTED;
                        connectionState = STATE_CONNECTED;
                        broadcastUpdate(intentAction);
                        Log.i(TAG, "Connected to GATT server.");
                        Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());

                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        intentAction = ACTION_GATT_DISCONNECTED;
                        connectionState = STATE_DISCONNECTED;
                        Log.i(TAG, "Disconnected from GATT server.");
                        broadcastUpdate(intentAction);
                    }
                }

                @Override
                // New services discovered
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                        initServices();
                    } else {
                        Log.w(TAG, "onServicesDiscovered received: " + status);
                    }
                }

                @Override
                // Result of a characteristic read operation
                public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                    }
                }
            };

    @Override
    public void onCreate() {
        super.onCreate();
        mBluetoothManager = null; //TODO: get this done right.
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public  void onDestroy() {
        close();
    }


    @Override
    public IBinder onBind(Intent intent) {


        return mBinder;
    }

    public boolean connectToDevice(BluetoothDevice device) {
        mBluetoothDevice = device;
        mBluetoothGatt = device.connectGatt(this, true, mGattCallback);


        return mBluetoothGatt != null;
    }

    public void turnOnNozzle(int pos) {
        modeArray[pos] = 0x00000001;
        writeNozzle(pos);
    }

    public void turnOffNozzle(int pos) {

    }

    private void writeNozzle(int pos) {
        //nozzleCharacteristics[pos];
        ByteBuffer bb = ByteBuffer.allocate(14);
        bb.putInt(modeArray[pos]);
        bb.putInt(durationArray[pos]);
        bb.putShort(colorArray[pos].red);
        bb.putShort(colorArray[pos].green);
        bb.putShort(colorArray[pos].blue);

        byte[] bytes = bb.array();

        for (int i = 0; i < 14; i++) {
            Log.d("TEST ", "Byte " + i + " is " + bytes[i] );
        }

        nozzleCharacteristics[pos].setValue(bytes);
        mBluetoothGatt.writeCharacteristic(nozzleCharacteristics[pos]);
    }


    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    private void initServices() {
        List<BluetoothGattService> services =  mBluetoothGatt.getServices();
        if(services == null){
            return;
        }
        for (BluetoothGattService service : services) {
            if(service.getUuid().equals(UUID.fromString(NOZZLE_SERVICE_UUID))) {
                nozzleService = service;
            }
        }
        initNozzleCharacteristics();
    }

    private void initNozzleCharacteristics() {
        if(nozzleService == null) {
            return;
        }
        List<BluetoothGattCharacteristic> characteristics = nozzleService.getCharacteristics();
        if(characteristics == null) {
            return;
        }
        for(BluetoothGattCharacteristic characteristic : characteristics) {
            if(        characteristic.getUuid().equals(UUID.fromString(NOZZLE_0_CHARACTERISTIC_UUID))) {
                nozzleCharacteristics[0] = characteristic;
            } else if (characteristic.getUuid().equals(UUID.fromString(NOZZLE_1_CHARACTERISTIC_UUID))) {
                nozzleCharacteristics[1] = characteristic;
            } else if (characteristic.getUuid().equals(UUID.fromString(NOZZLE_2_CHARACTERISTIC_UUID))) {
                nozzleCharacteristics[2] = characteristic;
            } else if (characteristic.getUuid().equals(UUID.fromString(NOZZLE_3_CHARACTERISTIC_UUID))) {
                nozzleCharacteristics[3] = characteristic;
            } else if (characteristic.getUuid().equals(UUID.fromString(NOZZLE_4_CHARACTERISTIC_UUID))) {
                nozzleCharacteristics[4] = characteristic;
            } else if (characteristic.getUuid().equals(UUID.fromString(NOZZLE_5_CHARACTERISTIC_UUID))) {
                nozzleCharacteristics[5] = characteristic;
            } else if (characteristic.getUuid().equals(UUID.fromString(NOZZLE_6_CHARACTERISTIC_UUID))) {
                nozzleCharacteristics[6] = characteristic;
            }
        }
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is special handling for the Heart Rate Measurement profile. Data
        // parsing is carried out as per profile specifications.
        if (false/*UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())*/) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" +
                        stringBuilder.toString());
            }
        }
        sendBroadcast(intent);
    }

}