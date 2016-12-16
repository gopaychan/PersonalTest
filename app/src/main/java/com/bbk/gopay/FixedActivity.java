package com.bbk.gopay;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bbk.gopay.fixed_recycler.BBKRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gopaychan on 2016/12/16.
 */
public class FixedActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private static List<String> mDataList;
    public static final int FIXED_ITEM = 0;
    public static final int ORIGIN_ITEM = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixed);
        mRecyclerView = findView(R.id.fixed_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new BBKRecyclerViewAdapter<ViewHolder>() {
            @Override
            public boolean isItemViewTypeFixed(int type) {
                return type == FIXED_ITEM;
            }

            @Override
            public int findParentPosByChildPos(int position) {
                if (position >= 0 && position <= 4) {
                    return 0;
                } else if (position >= 5 && position <= 9) {
                    return 5;
                } else if (position >= 10 && position <= 14) {
                    return 10;
                } else {
                    return 15;
                }
            }

            @Override
            public int getItemViewType(int position) {
                if (position % 5 == 0) return FIXED_ITEM;
                else return ORIGIN_ITEM;
            }

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fixed,parent,false);
                if (viewType == FIXED_ITEM) {
                    itemView.setBackgroundColor(Color.GREEN);
                }
                return new ViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                holder.tv.setText(mDataList.get(position));
            }

            @Override
            public int getItemCount() {
                return mDataList.size();
            }
        });
    }

    static {
        mDataList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mDataList.add(i + "");
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv;

        public ViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv);
        }
    }
}
