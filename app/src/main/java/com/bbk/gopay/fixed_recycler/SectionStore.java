package com.bbk.gopay.fixed_recycler;

import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;


/**
 * Created by cgp on 2016/7/27.
 * 保存，生成固定View的类
 */
public class SectionStore {
    private SparseArrayCompat<View> mSectionViewMap;
    private SparseArrayCompat<RecyclerView.ViewHolder> mHolderMap;
    private RecyclerView mRecyclerView;

    public SectionStore(RecyclerView recyclerView) {
        mSectionViewMap = new SparseArrayCompat<>();
        mHolderMap = new SparseArrayCompat<>();
        this.mRecyclerView = recyclerView;
    }

    public View getViewByPosition(final int position) {
        if (mSectionViewMap.get(position) == null) {
            final RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
            int type = adapter.getItemViewType(position);
            final RecyclerView.ViewHolder holder = adapter.onCreateViewHolder(mRecyclerView, type);
            adapter.onBindViewHolder(holder, position);
            layoutView(holder.itemView);
            mSectionViewMap.put(position, holder.itemView);
            mHolderMap.put(position,holder);
        }
        return mSectionViewMap.get(position);
    }

    public RecyclerView.ViewHolder getHolderByPosition(int position){
        return mHolderMap.get(position);
    }

    private void layoutView(View view) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(mRecyclerView.getWidth() - mRecyclerView.getPaddingLeft() - mRecyclerView.getPaddingRight(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthSpec, heightSpec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }
}
