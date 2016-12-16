package com.bbk.gopay.mvp.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by gopaychan on 2016/12/15.
 */
public class BluetoothModel {

    public void loadData(BluetoothAdapter bluetoothAdapter,Context context){

        bluetoothAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);

        context.getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case BluetoothDevice.ACTION_FOUND:
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (mListener!=null){
                            mListener.onSuccess(device);
                        }
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                        if (mListener!=null){
                            mListener.onStart();
                        }
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        if (mListener!=null){
                            mListener.onFinish();
                        }
                        context.getApplicationContext().unregisterReceiver(this);
                        break;
                    default:
                        break;
                }
            }
        }, filter);
    }

    private onDiscoveryListener mListener;
    public void setOnDiscoveryListener(onDiscoveryListener listener){
        mListener = listener;
    }

    public interface onDiscoveryListener{
        void onSuccess(BluetoothDevice device);
        void onStart();
        void onFinish();
    }
}
