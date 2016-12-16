package com.bbk.gopay.View;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ListView;

/**
 * Created by hry on 2016/8/17.
 */
public class BounceScrollView extends FrameLayout {
    private View inner;// the view in scrollview

    private float y;

    private Rect normal = new Rect();// the location for inner view

    private boolean isCount = false;

    private float mLastDownY;
    private float mLastDownX;
    private float mLastItemY;


    public BounceScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onFinishInflate() {// after xml is made
        if (getChildCount() > 0) {
            inner = getChildAt(0);
        }
        super.onFinishInflate();
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (inner != null) {
            commOnTouchEvent(ev);
        }

        return super.onTouchEvent(ev);
    }

    /***
     * touch event
     *
     * @param ev enent
     */
    private void commOnTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                // go back the original loaction
                if (isNeedAnimation()) {
                    animation();
                    isCount = false;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                final float preY = y;
                float nowY = ev.getY();
                int deltaY = (int) (preY - nowY);
                if (!isCount) {
                    deltaY = 0;
                }
                y = nowY;

                if (isNeedMove()) {
                    if (normal.isEmpty()) {
                        normal.set(inner.getLeft(), inner.getTop(),
                                inner.getRight(), inner.getBottom());// save the original loaction
                    }
                    inner.layout(inner.getLeft(), inner.getTop() - deltaY / 2,
                            inner.getRight(), inner.getBottom() - deltaY / 2); // move layout
                }
                isCount = true;
                break;

            default:
                break;
        }
    }

    /***
     * go back original location
     */
    private void animation() {
        // start animation
        TranslateAnimation animation = new TranslateAnimation(0, 0, inner.getTop(),
                normal.top);
        animation.setDuration(200);
        inner.startAnimation(animation);
        inner.layout(normal.left, normal.top, normal.right, normal.bottom);
        normal.setEmpty();
    }


    /**
     * is need animation
     *
     * @return boolean
     */
    private boolean isNeedAnimation() {
        return !normal.isEmpty();
    }

    /***
     * is need move layout
     * inner.getMeasuredHeight():the inner view height
     * <p/>
     * getHeight()：the screen hight
     *
     * @return boolean
     */
    private boolean isNeedMove() {
        int offset = inner.getMeasuredHeight() - getHeight();
        int scrollY = getScrollY();
        return scrollY == 0 || scrollY == offset;
    }

    /**
     * solve the problem for not support little view to bounce
     *
     * @param ev event
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        float y = ev.getY();
        float x = ev.getX();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastDownY = y;
                mLastDownX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(x - mLastDownX) < Math.abs(y - mLastDownY))
                    if (inner instanceof ListView) {
                        if (((ListView) inner).getChildCount() > 0) {
                            int firstVisiblePos = ((ListView) inner).getFirstVisiblePosition();
                            if (y > mLastDownY) {
                                if (firstVisiblePos == 0 && ((ListView) inner).getChildAt(0).getTop() >= inner.getTop() + inner.getPaddingTop())
                                    return true;
                            } else if ((y < mLastDownY)) {
                                if (!inner.canScrollVertically(1))
                                    return true;
                            }
                        }
                    } else return true;
                break;
            case MotionEvent.ACTION_UP:
                mLastDownY = 0;
                break;
        }
        return false;
    }
}
