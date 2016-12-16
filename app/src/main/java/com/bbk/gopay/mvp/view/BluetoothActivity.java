package com.bbk.gopay.mvp.view;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bbk.gopay.BaseActivity;
import com.bbk.gopay.BluetoothUtil;
import com.bbk.gopay.R;
import com.bbk.gopay.mvp.presenter.BluetoothPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gopaychan on 2016/12/13.
 */
public class BluetoothActivity extends BaseActivity implements BluetoothView {
    private static final String TAG = "BluetoothActivity";
    private ListView mBluetoothLv;
    private ArrayAdapter<String> mAdapter;
    private List<String> mList;
    private List<BluetoothDevice> mDeviceList;

    private BluetoothPresenter mPresenter;
    private ProgressBar mPb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        mBluetoothLv = findView(R.id.lv);
        mPb = findView(R.id.pb);
        Button startDiscoveryBtn = findView(R.id.start_discovery_btn);
        startDiscoveryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onStartDiscoveryClick();
            }
        });
        mList = new ArrayList<>();
        mDeviceList = new ArrayList<>();
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, mList);
        mBluetoothLv.setAdapter(mAdapter);

        mBluetoothLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.onItemClick(mDeviceList.get(position));
            }
        });
        mPresenter = new BluetoothPresenter(this);
    }

    @Override
    public void showProgressDialog() {
        if (mPb.getVisibility() == View.GONE) {
            mPb.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideProgressDialog() {
        if (mPb.getVisibility() == View.VISIBLE) {
            mPb.setVisibility(View.GONE);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void addData(BluetoothDevice device) {
        String name = device.getName();
        mList.add(name == null ? device.getAddress() : name + "  " + device.getAddress());
        mDeviceList.add(device);
    }

    @Override
    public void clearDatas() {
        mList.clear();
        mDeviceList.clear();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothUtil.ENABLE_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    mPresenter.loadData();
                    break;
                case RESULT_CANCELED:
                    break;
                default:
                    break;
            }
        }
    }
}
