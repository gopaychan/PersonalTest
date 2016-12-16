package com.bbk.gopay.mvp.presenter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.bbk.gopay.BluetoothUtil;
import com.bbk.gopay.mvp.model.BluetoothModel;
import com.bbk.gopay.mvp.view.BluetoothView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by gopaychan on 2016/12/15.
 */
public class BluetoothPresenter {

    private BluetoothModel mModel;
    private BluetoothView mView;
    private BluetoothAdapter mBluetoothAdapter;
    private static final String TAG = "BluetoothPresenter";


    public BluetoothPresenter(BluetoothView view) {
        mView = view;
        mModel = new BluetoothModel();
        mModel.setOnDiscoveryListener(new BluetoothModel.onDiscoveryListener() {
            @Override
            public void onSuccess(BluetoothDevice device) {
                mView.showProgressDialog();
                mView.addData(device);
                mView.notifyDataSetChanged();
            }

            @Override
            public void onStart() {
//                mView.showProgressDialog();
//                mView.clearDatas();
//                mView.notifyDataSetChanged();
            }

            @Override
            public void onFinish() {
                mView.hideProgressDialog();
            }
        });
        initBluetoothAdapter();
    }

    private void initBluetoothAdapter() {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 18) {
            BluetoothManager bm = (BluetoothManager) mView.getContext().getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bm.getAdapter();
        } else {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (!mBluetoothAdapter.isEnabled()) {
            BluetoothUtil.openBluetooth((Activity) mView.getContext());
        } else {
            loadData();
        }
    }

    public void loadData() {
        mView.showProgressDialog();
        mView.clearDatas();
        mView.notifyDataSetChanged();
        mModel.loadData(mBluetoothAdapter, mView.getContext());
    }

    public void onItemClick(BluetoothDevice device) {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        new ConnectThread(device).start();
    }

    public void onStartDiscoveryClick() {
        if (mBluetoothAdapter.isEnabled()) {
            loadData();
        } else {
            mView.showToast("请同意打开蓝牙设备");
            BluetoothUtil.openBluetooth((Activity) mView.getContext());
        }
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            mmDevice = device;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            int bondState = mmDevice.getBondState();
            if (bondState == BluetoothDevice.BOND_NONE) {
                Method createBondMethod = null;
                try {
                    createBondMethod = BluetoothDevice.class.getMethod("createBond");
                    createBondMethod.invoke(mmDevice);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                BluetoothSocket tmp = null;
                try {
                    tmp = mmDevice.createRfcommSocketToServiceRecord(BluetoothUtil.getUuid(mView.getContext()));
                } catch (IOException e) {
                    Log.e(TAG, "error");
                }
                mmSocket = tmp;
                try {
                    if (mmSocket != null) {
                        if (!mmSocket.isConnected()) {
                            mmSocket.connect();
                        }
                        if (mmSocket.isConnected()) {
                            OutputStream ops = mmSocket.getOutputStream();
                            File f = new File("D:\\Document", "startStudentModel.bat");
                            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
                            BufferedOutputStream bfs = new BufferedOutputStream(ops);
                            byte[] buffer = new byte[1024];
                            int length = -1;
                            while ((length = bis.read(buffer)) != -1) {
                                bfs.write(buffer, 0, length);
                            }
                        }
                    }
                } catch (IOException connectException) {
                    try {
                        mmSocket.close();
                    } catch (IOException closeException) {
                        Log.e(TAG, "error");
                    }
                    return;
                }
            }
        }

        /**
         * Will cancel an in-progress connection, and close the socket
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }
}
