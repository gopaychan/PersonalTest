package com.bbk.gopay.fixed_recycler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by cgp on 2016/7/27.
 * 如果要用paddingTop,改为marginTop paddingTop会有点奇怪
 */
public class BBKRecyclerView extends RecyclerView {
    private Context mContext;
    /**
     * 固定View的position
     */
    private int mFixedViewPosition = -1;
    /**
     * 固定的View
     */
    public View mFixedView;
    /**
     * canvas的平移距离
     */
    private float mTranslateY = 0;
    private SectionStore mSectionStore;
    private PointF mTouchPoint;
    private int mTouchSlop;

    private View mTouchTarget;
    private MotionEvent mDownEvent;
    private final Rect mTouchRect = new Rect();

    public BBKRecyclerView(Context context) {
        this(context, null);
    }

    public BBKRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BBKRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        if (getAdapter() instanceof BBKRecyclerViewAdapter) {
            int firstVisibleItemPosition = getFirstVisiblePosition();
            if (isSection(firstVisibleItemPosition)) {
                View sectionView = getChildAt(0);
                if (sectionView.getTop() != getPaddingTop()) {
                    calculateTranslate(firstVisibleItemPosition, firstVisibleItemPosition);
                } else {
                    mFixedView = null;
                    mFixedViewPosition = -1;
                }
            } else {
                int sectionPosition = findCurrentSectionPos(firstVisibleItemPosition);
                if (sectionPosition > -1) {
                    calculateTranslate(sectionPosition, firstVisibleItemPosition);
                } else {
                    mFixedView = null;
                    mFixedViewPosition = -1;
                }
            }
        }
    }

    /**
     * 计算canvas的平移距离
     *
     * @param sectionPosition
     * @param firstVisiblePosition
     */
    private void calculateTranslate(int sectionPosition, int firstVisiblePosition) {
        if (mFixedView != null && sectionPosition != mFixedViewPosition) {
            mFixedView = null;
            mFixedViewPosition = -1;
        }
        if (mFixedView == null){
            mFixedView = mSectionStore.getViewByPosition(sectionPosition);
            mFixedViewPosition = sectionPosition;
        }
        int nextPosition = sectionPosition + 1;
        if (nextPosition < getAdapter().getItemCount()) {
            int nextSectionPosition = findFirstVisibleSectionPos(nextPosition,
                    getChildCount() - (nextPosition - firstVisiblePosition));
            if (nextSectionPosition > -1) {
                View nextSectionView = getChildAt(nextSectionPosition - firstVisiblePosition);
                final int bottom = mFixedView.getBottom() + getPaddingTop();
                float sectionsDistanceY = nextSectionView.getTop() - bottom;
                if (sectionsDistanceY < 0 && isSection(nextSectionView)) {
                    mTranslateY = sectionsDistanceY;
                } else {
                    mTranslateY = 0;
                }
            } else {
                mTranslateY = 0;
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mFixedView != null) {
//                Log.i("rect....", mFixedView.toString());
            int pLeft = getPaddingLeft();
            int pTop = getPaddingTop();
            canvas.save();

            canvas.clipRect(pLeft, pTop, pLeft + mFixedView.getWidth(), pTop + mFixedView.getHeight());
            canvas.translate(pLeft, pTop + mTranslateY);
            drawChild(canvas, mFixedView, getDrawingTime());
            canvas.restore();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        final float x = ev.getX();
        final float y = ev.getY();
        final int action = ev.getAction();

        if (action == MotionEvent.ACTION_DOWN
                && mTouchTarget == null
                && mFixedView != null
                && isFixedViewTouched(mFixedView, x, y)) {

            mTouchTarget = mFixedView;
            mTouchPoint.x = x;
            mTouchPoint.y = y;

            mDownEvent = MotionEvent.obtain(ev);
        }

        if (mTouchTarget != null) {
            if (isFixedViewTouched(mTouchTarget, x, y)) {
                mTouchTarget.dispatchTouchEvent(ev);
            }

            if (action == MotionEvent.ACTION_UP) {
                super.dispatchTouchEvent(ev);
                performPinnedItemClick();
                clearTouchTarget();
            } else if (action == MotionEvent.ACTION_CANCEL) {
                clearTouchTarget();
            } else if (action == MotionEvent.ACTION_MOVE) {
                if (Math.abs(y - mTouchPoint.y) > mTouchSlop) {

                    MotionEvent event = MotionEvent.obtain(ev);
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    mTouchTarget.dispatchTouchEvent(event);
                    event.recycle();

                    super.dispatchTouchEvent(mDownEvent);
                    super.dispatchTouchEvent(ev);
                    clearTouchTarget();
                }
            }

            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void clearTouchTarget() {
        mTouchTarget = null;
        if (mDownEvent != null) {
            mDownEvent.recycle();
            mDownEvent = null;
        }
    }

    int findFirstVisibleSectionPos(int firstVisibleItem, int visibleItemCount) {
        RecyclerView.Adapter adapter = getAdapter();

        int adapterDataCount = adapter.getItemCount();
        if (getLastVisiblePosition() >= adapterDataCount) return -1;

        if (firstVisibleItem + visibleItemCount >= adapterDataCount) {
            visibleItemCount = adapterDataCount - firstVisibleItem;
        }

        for (int childIndex = 0; childIndex < visibleItemCount; childIndex++) {
            int position = firstVisibleItem + childIndex;
            if (isSection(position)) return position;
        }
        return -1;
    }

    int findCurrentSectionPos(int position) {
        Adapter adapter = getAdapter();
        if (adapter instanceof BBKRecyclerViewAdapter) {
            return ((BBKRecyclerViewAdapter) adapter).findParentPosByChildPos(position);
        } else {
            return -1;
        }
    }

    private int getFirstVisiblePosition() {
        return ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
    }

    private int getLastVisiblePosition() {
        return ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
    }

    private boolean isSection(int position) {
        return isItemViewTypeFixed(getAdapter().getItemViewType(position));
    }

    private boolean isSection(View child) {
        return isItemViewTypeFixed(getChildViewHolder(child).getItemViewType());
    }

    private boolean isItemViewTypeFixed(int type) {
        RecyclerView.Adapter adapter = getAdapter();
        if (adapter instanceof BBKRecyclerViewAdapter) {
            return ((BBKRecyclerViewAdapter) adapter).isItemViewTypeFixed(type);
        } else {
            return false;
        }
    }

    private boolean isFixedViewTouched(View view, float x, float y) {
        view.getHitRect(mTouchRect);
        return mTouchRect.contains((int) x, (int) y);
    }

    private void performPinnedItemClick() {
        View view = mFixedView;
        playSoundEffect(SoundEffectConstants.CLICK);
        if (view != null) {
            view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
        }
        Adapter adapter = getAdapter();
        if (adapter instanceof BBKRecyclerViewAdapter) {
            if (mFixedViewPosition >= 0)
                ((BBKRecyclerViewAdapter) adapter).onFixedViewClickListener(mSectionStore.getHolderByPosition(mFixedViewPosition), mFixedViewPosition);
        }
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        if (top != 0) {
            throw new IllegalArgumentException("BBKRecyclerView is unable to setPaddingTop");
        }
        super.setPadding(left, top, right, bottom);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter instanceof BBKRecyclerViewAdapter) {
            mTouchPoint = new PointF();
            mSectionStore = new SectionStore(this);
            mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
            setPadding(getPaddingLeft(), 0, getPaddingRight(), getPaddingBottom());
        }
        super.setAdapter(adapter);
    }
}