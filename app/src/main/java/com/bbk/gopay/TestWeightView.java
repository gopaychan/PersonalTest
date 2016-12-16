package com.bbk.gopay;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by gopaychan on 2016/11/8.
 */
public class TestWeightView extends View {


    public TestWeightView(Context context) {
        this(context, null);
    }

    public TestWeightView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestWeightView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.ViewGroup_Layout);
//        int height = a.getLayoutDimension(R.styleable.ViewGroup_Layout_layout_height);
//        Log.e("height:",height + "");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e("height:", MeasureSpec.getSize(heightMeasureSpec) + "");
    }
}
