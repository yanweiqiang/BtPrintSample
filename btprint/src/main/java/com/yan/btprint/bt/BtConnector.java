package com.yan.btprint.bt;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by yanweiqiang on 2017/10/18.
 */

public class BtConnector implements IBtConnector {
    private static final String tag = BtConnector.class.getSimpleName();
    private static final UUID CONST_UUID = UUID.fromString("0001101-0000-1000-8000-00805F9B34FB");

    private Handler handler;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private volatile State state;
    private List<ListenCallback> listenCallbackList;
    private Callback disconnectCallback;
    private ReentrantLock lock;

    public BtConnector() {
        super();
        handler = new Handler(Looper.getMainLooper());
        listenCallbackList = new ArrayList<>();
        state = State.IDLE;
        lock = new ReentrantLock();
    }

    public void addListenCallback(ListenCallback callback) {
        listenCallbackList.add(callback);
    }

    public void removeListenCallback(ListenCallback callback) {
        listenCallbackList.remove(callback);
    }

    private void changeState(State state) {
        lock.lock();
        this.state = state;
        lock.unlock();
    }

    @Override
    public void connect(final BluetoothDevice currentDevice, final Callback callback) {
        Log.i(tag, "connect");
        if (!BtManager.hasBluetoothModule()) {
            return;
        }
        Log.i(tag, "connect start");

        new Thread(new Runnable() {
            Timer timer;
            BluetoothSocket currentSocket;

            @Override
            public void run() {
                Log.i(tag, "connect 0");

                if (state != State.IDLE) {
                    return;
                }

                triggerStart();
                timer = new Timer();
                BtManager.cancelDiscoveryBluetooth();

                try {
                    currentSocket = currentDevice.createRfcommSocketToServiceRecord(CONST_UUID);

                    if (currentSocket == null) {
                        triggerFailure();
                        return;
                    }

                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                Log.i(tag, "timeout");
                                currentSocket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }, 30000);

                    currentSocket.connect();
                    triggerSuccess();
                } catch (IOException e) {
                    Log.i(tag, e.getMessage());
                    triggerFailure();
                    e.printStackTrace();
                } finally {
                    timer.cancel();
                    triggerStop();
                }
            }

            private void triggerStart() {
                Log.i(tag, "triggerStart");
                changeState(State.CONNECTING);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onStart();
                        }
                    }
                });
            }

            private void triggerSuccess() {
                Log.i(tag, "triggerSuccess");
                changeState(State.CONNECTED);
                device = currentDevice;
                socket = currentSocket;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    }
                });
            }

            private void triggerFailure() {
                Log.i(tag, "triggerFailure");
                changeState(State.IDLE);
                device = null;
                socket = null;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onFailure();
                        }
                    }
                });
            }

            private void triggerStop() {
                Log.i(tag, "triggerStop");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onStop();
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void disconnect(Callback callback) {
        Log.i(tag, "disconnect");

        if (state != State.CONNECTED) {
            return;
        }

        changeState(State.DISCONNECTING);

        disconnectCallback = callback;
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (disconnectCallback != null) {
                    disconnectCallback.onStart();
                }
            }
        });

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (disconnectCallback != null) {
                        disconnectCallback.onFailure();
                        disconnectCallback.onStart();
                    }
                }
            });
        }
    }

    private void disconnected() {
        Log.i(tag, "disconnected");
        device = null;
        socket = null;
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (disconnectCallback != null) {
                    disconnectCallback.onSuccess();
                    disconnectCallback.onStop();
                }
            }
        });
        changeState(State.IDLE);
    }

    @Override
    public void write(byte[] bytes) {
        if (socket == null) {
            return;
        }

        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(bytes);
        } catch (IOException e) {
            disconnected();
            e.printStackTrace();
        }
    }

    public void listen() {
        Log.i(tag, "start listen");

        if (state != State.CONNECTED) {
            Log.i(tag, "start listen error, socket not connected!");
            return;
        }

        byte[] bytes = new byte[1024];

        try {
            InputStream inputStream = socket.getInputStream();
            while (true) {
                int total;
                total = inputStream.read(bytes);
                Log.i(tag, new String(bytes, "gbk"));
                if (total == -1) {
                    Log.i(tag, "read -1");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            disconnected();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    List<ListenCallback> tempList = new ArrayList<>(listenCallbackList);
                    for (ListenCallback listenCallback : tempList) {
                        if (listenCallback != null) {
                            listenCallback.onDisconnected();
                        }
                    }
                }
            });
        }
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public BluetoothDevice getDevice() {
        return device;
    }

    @Override
    public BluetoothSocket getSocket() {
        return socket;
    }

}