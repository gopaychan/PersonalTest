package com.bbk.gopay.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.ListView;

import com.bbk.gopay.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewPropertyAnimator;

/**
 * Created by cgp on 2016/8/4.
 * <p/>
 * 使用该控件 隐藏的view id必须为right_view 正常显示的view id必须为left_view
 * <p/>
 * 如果滑动后抬起手指不要点击事件 可通过getIsSlipUp 判断是否是滑动后的action_up
 */
public class SwipeListView extends SlideOverCheckBoxListView {

    /**
     * 当前打开的item
     */
    private View mItemView;
    private View mLeftView;
    private View mRightView;
    /**
     * 当前按下的position
     */
    private int mDownPosition;
    private float mRightDownX;
    private float mLastDownY;
    private float mLeftDownX;
    private int mSlopDistance;
    private VelocityTracker mVelocityTracker;
    private boolean isSliding;
    private boolean isCloseOther;
    private boolean isDownOnDisableRect;
    private Rect mDisableScrollRect;
    private int mLastSlideDirection;
    private final int SLIDE_LEFT = 0x1;
    private final int SLIDE_RIGHT = 0x2;
    private final int ACTION_DOWN = 0x3;
    /**
     * item是否打开
     */
    private boolean isOpened;
    /**
     * item是否允许滑动
     */
    private boolean isItemScrollable = true;
    /**
     * 是否沿y轴移动
     */
    private boolean isSlidingY;
    /**
     * 是否是滑动后抬起手指
     */
    private boolean isSlidingUp = false;
    private final int DEFAULT_SCROLL_WIDTH = 200;
    private int mScrollWidth;
    private OnDeleteBtnClickListener mListener;

    public SwipeListView(Context context) {
        this(context, null);
    }

    public SwipeListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getResources().obtainAttributes(attrs, R.styleable.SwipeListView);
        mScrollWidth = (int) a.getDimension(R.styleable.SwipeListView_switchWidth, DEFAULT_SCROLL_WIDTH);
        a.recycle();
        mSlopDistance = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        addMovement(ev);
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mRightDownX = x;
                mLastDownY = y;
                mLeftDownX = x;
                mLastSlideDirection = ACTION_DOWN;
                int position = pointToPosition((int) mRightDownX, (int) mLastDownY);
                if (position == ListView.INVALID_POSITION)
                    return super.dispatchTouchEvent(ev);
                if (position != mDownPosition && mItemView != null && isOpened) {
                    smoothClose();
                    isCloseOther = true;
                } else isCloseOther = false;
                mDownPosition = position;
                mItemView = getChildAt(mDownPosition - getFirstVisiblePosition());
                //判断是否点击在不可滑动区域
                if (mItemView != null) {
                    if (!isOpened)
                        isDownOnDisableRect = mDisableScrollRect != null && mDisableScrollRect.contains((int) x, (int) y - mItemView.getTop());
                    else
                        isDownOnDisableRect = mDisableScrollRect != null && mDisableScrollRect.contains((int) x + mScrollWidth, (int) y - mItemView.getTop());
                }
                break;
            case MotionEvent.ACTION_MOVE:
//                int slideDirection = mLeftDownX > x ? SLIDE_LEFT : SLIDE_RIGHT;
                int slideDirection;
                if (mLeftDownX > x) slideDirection = SLIDE_LEFT;
                else if (mLeftDownX < x) slideDirection = SLIDE_RIGHT;
                else slideDirection = mLastSlideDirection;
                if (mItemView != null)
                    if (!isSlidingY) {//确保快速滑动y时 会识别成x轴的移动
                        if (!canMovingInX(x, y) || !isItemScrollable || isDownOnDisableRect) {
                            break;
                        }
                        if (mDownPosition >= getFirstVisiblePosition() && mDownPosition <= getLastVisiblePosition()) {
                            mLeftView = mItemView.findViewById(R.id.left_view);
                            mRightView = mItemView.findViewById(R.id.right_view);
                            ViewParent parent = getParent();
                            if (parent != null)
                                parent.requestDisallowInterceptTouchEvent(true);
                            if (mLeftView != null) {
                                mRightView.setEnabled(false);
                                if (mLastSlideDirection == ACTION_DOWN)
                                    mLastSlideDirection = slideDirection;
                                else if (mLastSlideDirection != slideDirection) {
                                    mLastSlideDirection = slideDirection;
                                }
                                float deltaRightX = x - mRightDownX;
                                float deltaLeftX = x - mLeftDownX;
                                mLeftDownX = x;
                                mRightDownX = x;
                                mLastDownY = y;
                                if (slideDirection == SLIDE_LEFT) {
                                    if (-mRightView.getTranslationX() < mScrollWidth) {
                                        deltaRightX = Math.max(-mScrollWidth - mRightView.getTranslationX(), deltaRightX);
                                    } else {
                                        deltaRightX = 0;
                                    }
                                } else if (slideDirection == SLIDE_RIGHT) {
                                    if (mLeftView.getTranslationX() >= 0) {//关闭时不能再往右滑
                                        deltaLeftX = 0;
                                    }
                                    if (-mLeftView.getTranslationX() > mScrollWidth) {//左边的view离开右边的view时，右滑回到原来的位置 右边的view才可以滑动
                                        deltaRightX = 0;
                                    } else if (mRightView.getTranslationX() < 0) {
                                        deltaRightX = Math.min(-mRightView.getTranslationX(), deltaRightX);
                                    } else {
                                        deltaRightX = 0;
                                    }
                                }
                                if (-mLeftView.getTranslationX() > mScrollWidth)
                                    deltaLeftX = -mLeftView.getTranslationX() / mLeftView.getWidth() * deltaLeftX;
                                mLeftView.setTranslationX(mLeftView.getTranslationX() + deltaLeftX);
                                mRightView.setTranslationX(mRightView.getTranslationX() + deltaRightX);
                                isSliding = true;
//                                return true;
                            }
                        }
                    }
                break;
            case MotionEvent.ACTION_UP:
                mRightDownX = 0;
                mLastDownY = 0;
                mLeftDownX = 0;
                isSlidingY = false;
                if (mItemView != null) {
                    if (isSliding) {
                        mVelocityTracker.computeCurrentVelocity(100);
                        float velocityX = mVelocityTracker.getXVelocity();
                        if (mLeftView != null) {
                            float translationX = -mLeftView.getTranslationX();
                            if (velocityX > 0) {
                                if (velocityX > 500 || (translationX < mScrollWidth / 2 && translationX != 0))
                                    smoothClose();
                                else if (translationX == 0) isOpened = false;
                                else
                                    smoothOpen();
                            } else {
                                if (-velocityX > 500 || (translationX > mScrollWidth / 2 && translationX != mScrollWidth))
                                    smoothOpen();
                                else if (translationX == mScrollWidth) isOpened = true;
                                else smoothClose();
                            }
                        }
                        recycleVelocityTracker();
                        isSliding = false;
                        isSlidingUp = true;
                    } else if (isOpened) {//打开删除按钮后，点击该item 关闭删除按钮 或 点击删除按钮回调方法
                        if (isPointInView(mRightView, (int) ev.getRawX(), (int) ev.getRawY())) {
                            if (mListener != null) {
                                mListener.onClick(mRightView, mDownPosition);
                            }
                        }
                        smoothClose();
                        isSlidingUp = true;
                    } else if (isCloseOther) {//如果是因为点击其他按钮关闭删除按钮，先不跳入编辑闹钟页面
                        isCloseOther = false;
                        isSlidingUp = true;
                    } else isSlidingUp = false;
                } else isSliding = false;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getActionMasked() == MotionEvent.ACTION_MOVE) {
            if (isSliding)
                return true;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 检测是否是横向滑动
     *
     * @param x Position X
     * @param y Position Y
     */
    private boolean canMovingInX(float x, float y) {
        final int xDiff = (int) Math.abs(x - mRightDownX);
        final int yDiff = (int) Math.abs(y - mLastDownY);
        //不判断沿X轴滑动多长距离算是滑动
        // 不然当一个item open时再打开另一个item会有一瞬间沿Y轴移动
//        final int touchSlop = mSlopDistance;
//        return xDiff > touchSlop &&
        if (xDiff > yDiff || isSliding) {
            return true;
        } else {
            isSlidingY = true;
            return false;
        }
    }

    private boolean isPointInView(View v, int x, int y) {
        if (v != null) {
            int[] location = new int[2];
            v.getLocationOnScreen(location);
            return x > location[0] && x < location[0] + v.getWidth() && y > location[1] && y < location[1] + v.getHeight();
        }
        return false;
    }

    /**
     * 打开删除按钮动画
     */
    public void smoothOpen() {
        if (mLeftView != null) {
            ViewPropertyAnimator.animate(mLeftView)
                    .translationX(-mScrollWidth)
                    .setDuration(200);
            ViewPropertyAnimator animator = ViewPropertyAnimator.animate(mRightView)
                    .translationX(-mScrollWidth)
                    .setDuration(200);
            animator.setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (mRightView != null)
                        mRightView.setEnabled(true);//动画结束才可以点击按钮
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            isOpened = true;
        }
    }

    public void setItemScrollable(boolean enable) {
        isItemScrollable = enable;
    }

    public void setClosedDisableScrollRect(Rect rect) {
        mDisableScrollRect = rect;
    }

    /**
     * 关闭删除按钮动画
     */
    public void smoothClose() {
        if (mLeftView != null) {
            ViewPropertyAnimator.animate(mLeftView)
                    .translationX(0)
                    .setDuration(200);
            ViewPropertyAnimator.animate(mRightView)
                    .translationX(0)
                    .setDuration(200);
            isOpened = false;
        }
        mItemView = null;
        mLeftView = null;
        mRightView = null;
    }

    public boolean getIsOpened() {
        return isOpened;
    }

    public boolean getIsSlipUp() {
        return isSlidingUp;
    }

    private void addMovement(MotionEvent event) {
        if (mVelocityTracker == null)
            mVelocityTracker = VelocityTracker.obtain();
        mVelocityTracker.addMovement(event);
    }

    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    public void setOnDeleteBtnClickListener(OnDeleteBtnClickListener listener) {
        mListener = listener;
    }

    /**
     * 左滑显示的删除按钮点击监听接口
     */
    public interface OnDeleteBtnClickListener {
        public void onClick(View view, int position);
    }
}