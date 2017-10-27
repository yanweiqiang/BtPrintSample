package com.yan.btprintsample;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;
import android.widget.Toast;

import com.yan.btprint.bt.BtManager;
import com.yan.btprint.bt.IBtConnector;

/**
 * Created by yanweiqiang on 2017/10/19.
 */

public class MApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        startPrintService();
    }

    public void startPrintService() {
        String printerAddress = getApplicationContext().getSharedPreferences("cache", MODE_PRIVATE).getString("printer_address", null);
        if (TextUtils.isEmpty(printerAddress)) {
            return;
        }
        BluetoothDevice device = BtManager.getBtDevice(printerAddress);
        if (device == null) {
            return;
        }
        PrintCenter.connect(getApplicationContext(), device, new IBtConnector.Callback() {
            @Override
            public void onStart() {
                Toast.makeText(getApplicationContext(), "Printer connecting...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "Printer connected...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(getApplicationContext(), "Printer connect fail...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStop() {

            }
        });
    }
}
