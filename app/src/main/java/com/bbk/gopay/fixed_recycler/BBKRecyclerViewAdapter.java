package com.bbk.gopay.fixed_recycler;

import android.support.v7.widget.RecyclerView;

/**
 * Created by cgp on 2016/7/29.
 * <p/>
 * 要实现固定在顶端的滑动RecyclerView必须继承这个Adapter
 */
public abstract class BBKRecyclerViewAdapter<H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<H> {
    /**
     * 判断是否是固定在顶端的类型
     *
     * @param type
     * @return
     */
    public abstract boolean isItemViewTypeFixed(int type);

    /**
     * 根据所给的position 返回对应固定在顶端的position
     *
     * @param position
     * @return parentPosition
     */
    public abstract int findParentPosByChildPos(int position);

    /**
     * 固定View的点击事件
     * @param holder
     * @param position
     */
    public void onFixedViewClickListener(RecyclerView.ViewHolder holder, int position) {

    }
}
