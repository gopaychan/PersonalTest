package com.bbk.gopay.mvp.view;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

/**
 * Created by gopaychan on 2016/12/15.
 */
public interface BluetoothView {

    void showProgressDialog();

    void hideProgressDialog();

    void notifyDataSetChanged();

    void addData(BluetoothDevice device);

    void clearDatas();

    void showToast(String message);

    Context getContext();

}
