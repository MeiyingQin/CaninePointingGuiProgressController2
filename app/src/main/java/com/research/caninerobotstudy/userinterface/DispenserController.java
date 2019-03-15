package com.research.caninerobotstudy.userinterface;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import java.util.UUID;

public class DispenserController {
    private final static String TAG = "DispenserController";
    private String dispenserOneAddress = "D3:D5:19:80:C0:9E";
    private String dispenserTwoAddress = "FD:61:0E:2C:D5:12";
    private final UUID SERVICE_UUID = UUID.fromString("cba20d00-224d-11e6-9fb8-0002a5d5c51b");
    private final UUID CHARACTERISTIC_UUID = UUID.fromString("cba20002-224d-11e6-9fb8-0002a5d5c51b");
    private static BluetoothAdapter mBluetoothAdapterOne;
    private static BluetoothAdapter mBluetoothAdapterTwo;
    private static BluetoothGatt mBluetoothGattOne;
    private static BluetoothGatt mBluetoothGattTwo;
    private static BluetoothGattCharacteristic mCharacteristicOne;
    private static BluetoothGattCharacteristic mCharacteristicTwo;

    private final BluetoothGattCallback mGattCallbackOne = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            //Connection established
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {

                //Discover services
                boolean success = gatt.discoverServices();

            } else if (status == BluetoothGatt.GATT_SUCCESS
                    && newState == BluetoothProfile.STATE_DISCONNECTED) {

                //Handle a disconnect event

            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status != BluetoothGatt.GATT_SUCCESS) {
                return;
            }

            //Now we can start reading/writing characteristics
            mCharacteristicOne = gatt.getService(SERVICE_UUID).getCharacteristic(CHARACTERISTIC_UUID);
            mCharacteristicOne.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            boolean mInitialized = gatt.setCharacteristicNotification(mCharacteristicOne, true);
            if (mInitialized) {
                byte[] message = {0x57, 0x01, 0x00};
                mCharacteristicOne.setValue(message);
            }
        }

        @Override
        public void onCharacteristicWrite (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);

            if (status ==  BluetoothGatt.GATT_SUCCESS) {
//                gatt.disconnect();
//                gatt.close();
//                gatt = null;
            }
        }
    };

    private final BluetoothGattCallback mGattCallbackTwo = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            //Connection established
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {

                //Discover services
                boolean success = gatt.discoverServices();

            } else if (status == BluetoothGatt.GATT_SUCCESS
                    && newState == BluetoothProfile.STATE_DISCONNECTED) {

                //Handle a disconnect event

            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status != BluetoothGatt.GATT_SUCCESS) {
                return;
            }

            //Now we can start reading/writing characteristics
            mCharacteristicTwo = gatt.getService(SERVICE_UUID).getCharacteristic(CHARACTERISTIC_UUID);
            mCharacteristicTwo.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            boolean mInitialized = gatt.setCharacteristicNotification(mCharacteristicTwo, true);
            if (mInitialized) {
                byte[] message = {0x57, 0x01, 0x00};
                mCharacteristicTwo.setValue(message);
            }
        }

        @Override
        public void onCharacteristicWrite (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);

            if (status ==  BluetoothGatt.GATT_SUCCESS) {
//                gatt.disconnect();
//                gatt.close();
//                gatt = null;
            }
        }
    };

    public DispenserController(Context context) {

        if (mBluetoothAdapterOne == null) {
            mBluetoothAdapterOne = BluetoothAdapter.getDefaultAdapter();
            final BluetoothDevice device = mBluetoothAdapterOne.getRemoteDevice(dispenserOneAddress);

            if (device == null) {
                Log.w(TAG, "Device not found. Unable to connect");
            } else {
                mBluetoothGattOne = device.connectGatt(context, true, mGattCallbackOne);
            }
        }

        if (mBluetoothAdapterTwo == null) {
            mBluetoothAdapterTwo = BluetoothAdapter.getDefaultAdapter();
            final BluetoothDevice device = mBluetoothAdapterTwo.getRemoteDevice(dispenserTwoAddress);

            if (device == null) {
                Log.w(TAG, "Device not found. Unable to connect");
            } else {
                mBluetoothGattTwo = device.connectGatt(context, true, mGattCallbackTwo);
            }
        }
    }

    public void rotate() {
        mBluetoothGattOne.writeCharacteristic(mCharacteristicOne);
        mBluetoothGattTwo.writeCharacteristic(mCharacteristicTwo);
    }

    public void left_rotate() {
        mBluetoothGattOne.writeCharacteristic(mCharacteristicOne);
    }

    public void right_rotate() {
        mBluetoothGattTwo.writeCharacteristic(mCharacteristicTwo);
    }

}
