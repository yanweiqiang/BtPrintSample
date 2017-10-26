package com.yan.btprint.bt;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.Serializable;

/**
 * Created by yanweiqiang on 2017/10/19.
 */

public interface IBtConnector extends Serializable {

    void connect(BluetoothDevice device, Callback callback);

    void disconnect(Callback callback);

    void addListenCallback(ListenCallback listenCallback);

    void removeListenCallback(ListenCallback listenCallback);

    State getState();

    BluetoothDevice getDevice();

    BluetoothSocket getSocket();

    void write(byte[] bytes);

    public enum State {
        IDLE, CONNECTING, DISCONNECTING, CONNECTED
    }

    public interface Callback {
        void onStart();

        void onSuccess();

        void onFailure();

        void onStop();
    }

    public interface ListenCallback {
        void onDisconnected();
    }
}