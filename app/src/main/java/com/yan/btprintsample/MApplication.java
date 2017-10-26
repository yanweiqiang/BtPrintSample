package com.yan.btprintsample;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Created by yanweiqiang on 2017/10/19.
 */

public class MApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        startPrintService();
        bindPrintService();
    }

    public void startPrintService() {
    }

    public void bindPrintService() {

    }
}
