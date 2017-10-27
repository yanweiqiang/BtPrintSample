package com.yan.btprintsample;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.yan.btprint.bt.BtManager;
import com.yan.btprint.bt.BtPermissionFragment;
import com.yan.btprint.print.PrintBuilder;

public class MainActivity extends AppCompatActivity {
    private BluetoothDisplayAdapter adapter;

    BtPermissionFragment btPermissionFragment;
    private BtManager btManager;

    private Observable observable = new Observable() {
        @Override
        public void action(String event) {
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BluetoothDisplayAdapter();
        recyclerView.setAdapter(adapter);

        btManager = new BtManager();
        btManager.setDiscoveryCallback(new BtManager.DiscoveryCallback() {
            @Override
            public void call(String action, Intent intent) {
                switch (action) {
                    case BtManager.DISCOVERY_STARTED:
                        Log.i("temp", "start discover");
                        break;
                    case BtManager.DISCOVERY_FINISHED:
                        Log.i("temp", "finish discover");
                        break;
                    case BtManager.FOUND:
                        BluetoothDevice device = BtManager.getBtDevice(intent);
                        adapter.add(device);
                        Log.i("temp", "find device:" + device.getName());
                        break;
                }
            }
        });
        btManager.registerBluetoothReceiver(MainActivity.this);
        Observer.subscript(observable);
        btPermissionFragment = new BtPermissionFragment();
        getSupportFragmentManager().beginTransaction().add(btPermissionFragment, null).commitAllowingStateLoss();
        openBt();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_open_bt:
                openBt();
                break;
            case R.id.main_close_bt:
                closeBt();
                break;
            case R.id.main_scan_device:
                scanDevice();
                break;
            case R.id.main_test_print:
                testPrint();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BtManager.cancelDiscoveryBluetooth();
        btManager.unregisterBluetoothReceiver(MainActivity.this);
        Observer.remove(observable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BtManager.REQUEST_CODE && resultCode == RESULT_OK) {
            scanDevice();
        }
    }

    private void openBt() {
        adapter.refresh(null);
        if (!BtManager.hasBluetoothModule()) {
            Toast.makeText(MainActivity.this, "do not have bluetooth module!", Toast.LENGTH_SHORT).show();
            return;
        }
        BtManager.enableBluetooth(MainActivity.this);
    }

    private void closeBt() {
        adapter.refresh(null);
        BtManager.disableBluetooth();
    }

    private void scanDevice() {
        adapter.refresh(null);
        if (btPermissionFragment.hasPermission(this)) {
            BtManager.cancelDiscoveryBluetooth();
            BtManager.startDiscoveryBluetooth();
        } else {
            btPermissionFragment.requestPermission(new BtPermissionFragment.Callback() {
                @Override
                public void onSuccess() {
                    BtManager.cancelDiscoveryBluetooth();
                    BtManager.startDiscoveryBluetooth();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(getBaseContext(), "No permission!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void testPrint() {
        PrintCenter.print(new PrintBuilder().text("Hello world!!").print().print().print().print().build());
    }
}
