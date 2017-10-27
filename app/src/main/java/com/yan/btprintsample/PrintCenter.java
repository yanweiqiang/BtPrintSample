package com.yan.btprintsample;

import android.app.IntentService;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.yan.btprint.bt.BtConnector;
import com.yan.btprint.bt.IBtConnector;
import com.yan.btprint.print.PrintQueue;

import java.util.List;

/**
 * Created by yanweiqiang on 2017/10/20.
 */

public class PrintCenter extends IntentService {
    private final String tag = PrintCenter.class.getSimpleName();
    private static BtConnector btConnector = new BtConnector();
    private static PrintQueue printQueue = new PrintQueue();

    public PrintCenter() {
        super("PrintCenter");
        Log.i(tag, this.toString());
    }

    public static void connect(final Context context, BluetoothDevice device, final IBtConnector.Callback callback) {
        btConnector.connect(device, new IBtConnector.Callback() {
            @Override
            public void onStart() {
                callback.onStart();
            }

            @Override
            public void onStop() {
                callback.onStop();
            }

            @Override
            public void onSuccess() {
                callback.onSuccess();
                printQueue.addConnector(btConnector);
                context.startService(new Intent(context, PrintCenter.class));
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });
    }

    public static void disconnect(IBtConnector.Callback callback) {
        btConnector.disconnect(callback);
    }

    public static IBtConnector.State getConnectedState() {
        return btConnector.getState();
    }

    public static BluetoothDevice getConnectedDevice() {
        return btConnector.getDevice();
    }

    public static void print(List<byte[]> bytesList) {
        printQueue.addBytesList(bytesList);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.i(tag, "start print service");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(tag, this.toString());
        btConnector.addListenCallback(new IBtConnector.ListenCallback() {
            @Override
            public void onDisconnected() {
                btConnector.removeListenCallback(this);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Observer.trigger("disconnected");
                        Toast.makeText(getApplicationContext(), "Printer disconnected...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        btConnector.listen();
    }
}
