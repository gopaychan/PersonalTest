package com.bbk.gopay.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.CheckBox;
import android.widget.ListView;

import com.bbk.gopay.R;

/**
 * Created by cgp on 2016/8/17.
 *
 * 当手指划过checkbox是会回调onSlide，checkbox的id必须为 check_box
 */
public class SlideOverCheckBoxListView extends ListView {

    private OnSlideOverCheckBoxListener mListener;
    private CheckBox mLastCheckBox;
    private int mLastSlideDirection;
    private final int SLIDE_UP = 0x1;
    private final int SLIDE_DOWN = 0x2;
    private final int ACTION_DOWN = 0x3;
    private float mLastDownY;

    public SlideOverCheckBoxListView(Context context) {
        this(context, null);
    }

    public SlideOverCheckBoxListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideOverCheckBoxListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x = (int) ev.getRawX();
        int y = (int) ev.getRawY();
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                dispatchCheckBoxSlide((int) ev.getX(), (int) ev.getY(), x, y);
                if (mLastCheckBox != null) {
                    mLastDownY = y;
                    mLastSlideDirection = ACTION_DOWN;
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_MOVE:
                if (mLastCheckBox != null) {
                    dispatchCheckBoxSlide((int) ev.getX(), (int) ev.getY(), x, y);
                    if (mLastCheckBox != null)
                        return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mLastCheckBox != null) {
                    mLastCheckBox = null;
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastCheckBox != null) return true;
        return super.onTouchEvent(ev);
    }

    private void dispatchCheckBoxSlide(int x, int y, int rawX, int rawY) {
        int position = pointToPosition(x, y);
        if (position != ListView.INVALID_POSITION) {
            View v = getChildAt(position - getFirstVisiblePosition());
            CheckBox checkBox = (CheckBox) v.findViewById(R.id.check_box);
            if (checkBox != null && checkBox.getVisibility() == VISIBLE && isPointInView(checkBox, rawX, rawY)) {
                int slideDirection = mLastDownY > rawY ? SLIDE_UP : SLIDE_DOWN;
                if (mLastCheckBox != checkBox) {
                    mLastCheckBox = checkBox;
                    if (mListener != null) {
                        ViewParent parent = getParent();
                        if (parent != null)
                            parent.requestDisallowInterceptTouchEvent(true);
                        mListener.onSlide(position, v, checkBox);
                    }
                    mLastSlideDirection = slideDirection;
                } else {
                    if (mLastSlideDirection != ACTION_DOWN) {//当滑动方向改变是 回调改变反向的那个checkbox的方法
                        if (mLastSlideDirection != slideDirection) {
                            if (mListener != null) {
                                ViewParent parent = getParent();
                                if (parent != null)
                                    parent.requestDisallowInterceptTouchEvent(true);
                                mListener.onSlide(position, v, checkBox);
                            }
                            mLastSlideDirection = slideDirection;
                        }
                    }
                }
                mLastDownY = rawY;
            }
        }
    }

    private boolean isPointInView(View v, int x, int y) {
        if (v != null) {
            int[] location = new int[2];
            v.getLocationOnScreen(location);
            return x > location[0] - 10 && x < location[0] + v.getWidth() + 20 && y > location[1] - 10 && y < location[1] + v.getHeight() + 20;
        }
        return false;
    }

    public void setOnSlideOverCheckBoxListener(OnSlideOverCheckBoxListener listener) {
        mListener = listener;
    }

    public interface OnSlideOverCheckBoxListener {
        public void onSlide(int position, View v, CheckBox checkBox);
    }
}
