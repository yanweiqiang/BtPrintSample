package com.yan.btprint.bt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

/**
 * Created by yanweiqiang on 2017/10/18.
 */

public class BtManager {
    public static final String DISCOVERY_STARTED = BluetoothAdapter.ACTION_DISCOVERY_STARTED;
    public static final String DISCOVERY_FINISHED = BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
    public static final String STATE_CHANGED = BluetoothAdapter.ACTION_STATE_CHANGED;
    public static final String FOUND = BluetoothDevice.ACTION_FOUND;
    public static final String BOND_STATE_CHANGED = BluetoothDevice.ACTION_BOND_STATE_CHANGED;
    public static final String PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";

    private static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private DiscoveryCallback discoveryCallback;

    public BtManager() {
        super();
    }

    public static boolean hasBluetoothModule() {
        return bluetoothAdapter != null;
    }

    public static boolean isBluetoothEnable() {
        if (!hasBluetoothModule()) {
            return false;
        }
        try {
            return bluetoothAdapter.isEnabled();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean enableBluetooth(Activity activity) {
        activity.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 0xa1);
        return true;
    }

    public static void onEnableResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0xa1 && resultCode == Activity.RESULT_OK) {
            startDiscoveryBluetooth();
        }
    }

    public static boolean disableBluetooth() {
        bluetoothAdapter.cancelDiscovery();
        return bluetoothAdapter.disable();
    }

    public static boolean startDiscoveryBluetooth() {
        return bluetoothAdapter.startDiscovery();
    }

    public static boolean cancelDiscoveryBluetooth() {
        return bluetoothAdapter.cancelDiscovery();
    }

    public static BluetoothDevice getBtDevice(String btAddress) {
        return bluetoothAdapter.getRemoteDevice(btAddress);
    }

    public static BluetoothDevice getBtDevice(Intent intent) {
        return intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
    }

    /**
     * register bluetooth receiver
     *
     * @param activity activity
     */
    public void registerBluetoothReceiver(Activity activity) {
        if (null == mBtReceiver || null == activity) {
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        //start discovery
        intentFilter.addAction(DISCOVERY_STARTED);
        //finish discovery
        intentFilter.addAction(DISCOVERY_FINISHED);
        //bluetooth status change
        intentFilter.addAction(STATE_CHANGED);
        //found device
        intentFilter.addAction(FOUND);
        //bond status change
        intentFilter.addAction(BOND_STATE_CHANGED);
        //pairing device
        intentFilter.addAction(PAIRING_REQUEST);
        activity.registerReceiver(mBtReceiver, intentFilter);
    }

    /**
     * unregister bluetooth receiver
     *
     * @param activity activity
     */
    public void unregisterBluetoothReceiver(Activity activity) {
        if (null == mBtReceiver || null == activity) {
            return;
        }
        activity.unregisterReceiver(mBtReceiver);
    }

    public void setDiscoveryCallback(DiscoveryCallback discoveryCallback) {
        this.discoveryCallback = discoveryCallback;
    }

    /**
     * blue tooth broadcast receiver
     */
    private BroadcastReceiver mBtReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent) {
                return;
            }

            String action = intent.getAction();

            if (TextUtils.isEmpty(action)) {
                return;
            }

            discoveryCallback.call(action, intent);
        }
    };

    public interface DiscoveryCallback {
        void call(String action, Intent intent);
    }
}
