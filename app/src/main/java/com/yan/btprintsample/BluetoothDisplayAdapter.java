package com.yan.btprintsample;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yan.btprint.bt.BtConnector;
import com.yan.btprint.bt.IBtConnector;
import com.yan.btprint.bt.SimpleCallback;
import com.yan.btprint.print.PrintCmd;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanweiqiang on 2017/10/18.
 */

public class BluetoothDisplayAdapter extends RecyclerView.Adapter<BluetoothDisplayAdapter.ViewHolder> {
    private ProgressDialog dialog;
    private List<BluetoothDevice> deviceList;

    public BluetoothDisplayAdapter() {
        super();
        deviceList = new ArrayList<>();
    }

    public void refresh(List<BluetoothDevice> deviceList) {
        this.deviceList.clear();
        if (deviceList != null) {
            this.deviceList.addAll(deviceList);
        }
        notifyDataSetChanged();
    }

    public void add(BluetoothDevice device) {
        this.deviceList.add(device);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (dialog == null) {
            dialog = new ProgressDialog(parent.getContext());
        }
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bluetooth_display, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Context context = holder.itemView.getContext();
        final BluetoothDevice device = deviceList.get(position);

        holder.name.setText(device.getName() + "|" + device.getAddress());

        if (PrintCenter.getConnectedState() == IBtConnector.State.CONNECTED && device.getAddress().equals(PrintCenter.getConnectedDevice().getAddress())) {
            holder.operate.setText("Disconnect");
        } else {
            holder.operate.setText("Connect");
        }

        holder.operate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {
                if (holder.operate.getText().equals("Disconnect")) {
                    PrintCenter.disconnect(new SimpleCallback() {
                        @Override
                        public void onStart() {
                            super.onStart();
                            dialog.setMessage("Disconnecting...");
                            dialog.show();
                        }

                        @Override
                        public void onStop() {
                            super.onStop();
                            dialog.cancel();
                            notifyDataSetChanged();
                        }
                    });
                } else {
                    PrintCenter.connect(context, device, new BtConnector.Callback() {
                        @Override
                        public void onStart() {
                            dialog.setMessage("Connecting...");
                            dialog.show();
                        }

                        @Override
                        public void onSuccess() {
                            Toast.makeText(holder.itemView.getContext(), "Connected...", Toast.LENGTH_SHORT).show();
                            context.getSharedPreferences("config", Context.MODE_PRIVATE).edit().putString("print_device", device.getAddress()).apply();
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(holder.itemView.getContext(), "Connect failure...", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onStop() {
                            dialog.cancel();
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.deviceList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        Button operate;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.bd_name);
            operate = itemView.findViewById(R.id.bd_operate);
        }
    }
}
