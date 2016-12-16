package com.bbk.gopay;

import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by gopaychan on 2016/12/13.
 */
public class BaseActivity extends AppCompatActivity {

    public <T extends View> T findView(@IdRes int resId){
        return (T)findViewById(resId);
    }
}
