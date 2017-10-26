package com.yan.btprint.print;

import android.util.Log;

import com.yan.btprint.bt.BtConnector;
import com.yan.btprint.bt.IBtConnector;
import com.yan.btprint.bt.SimpleCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanweiqiang on 2017/10/18.
 */

public class PrintQueue {
    private final String tag = PrintQueue.class.getSimpleName();
    private List<IBtConnector> btConnectorList;
    private ArrayList<byte[]> bytesList;

    public PrintQueue() {
        btConnectorList = new ArrayList<>();
        bytesList = new ArrayList<>();
    }

    public void addConnector(final IBtConnector connector) {
        connector.addListenCallback(new IBtConnector.ListenCallback() {
            @Override
            public void onDisconnected() {
                connector.removeListenCallback(this);
                btConnectorList.remove(connector);
            }
        });
        btConnectorList.add(connector);
    }

    public void removeConnector(IBtConnector connector, IBtConnector.Callback callback) {
        connector.disconnect(callback);
    }

    public List<IBtConnector> getBtConnectorList() {
        return btConnectorList;
    }

    public void clearConnector(final IBtConnector.Callback callback) {
        final List<IBtConnector> tempList = new ArrayList<>();
        for (int i = 0; i < btConnectorList.size(); i++) {
            final int j = i;
            final IBtConnector connector = btConnectorList.get(i);
            connector.disconnect(new SimpleCallback() {
                @Override
                public void onSuccess() {
                    super.onSuccess();
                    tempList.add(connector);
                    if (j == btConnectorList.size() - 1) {
                        if (btConnectorList.size() == tempList.size()) {
                            btConnectorList.clear();
                            callback.onSuccess();
                            callback.onStop();
                        } else {
                            for (IBtConnector btConnector : tempList) {
                                btConnectorList.remove(btConnector);
                            }
                            callback.onFailure();
                            callback.onStop();
                        }
                    }
                }
            });
        }
    }

    public synchronized void addBytes(byte[] bytes) {
        bytesList.add(bytes);
        printData();
    }

    public synchronized void addBytesList(List<byte[]> bytesList) {
        this.bytesList.addAll(bytesList);
        printData();
    }

    public synchronized void printData() {
        Log.i(tag, "connector count:" + btConnectorList.size());
        while (bytesList.size() > 0) {
            for (IBtConnector connector : btConnectorList) {
                connector.write(bytesList.get(0));
            }
            bytesList.remove(0);
        }
    }
}
