package com.bbk.gopay;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        TextView content = (TextView) findViewById(R.id.drawer_content);
        content.append("hhh");
//        DrawerLayout dl = (DrawerLayout) findViewById(R.id.dl);

//        Toolbar tl = (Toolbar) findViewById(R.id.toolbar);
//        tl.setTitle("Toolbar");//设置Toolbar标题
//        tl.setTitleTextColor(Color.WHITE);
//        tl.setLogo(R.mipmap.ic_launcher);
//        setSupportActionBar(tl);
//        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, dl, tl, R.string.open, R.string.open);
//        dl.setDrawerListener(mDrawerToggle);
//        mDrawerToggle.syncState();
//        ActionBar bar = getSupportActionBar();
//        if (bar != null)
//            getSupportActionBar().setHomeButtonEnabled(true);
//
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle("Toolbar");
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://www.68idc.cn/").build();
//
        INetService service = retrofit.create(INetService.class);
        Call<ResponseBody> call = service.loginNuomi();

//
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.e("body", response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("message", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                text.setText(t.getMessage());
            }
        });

//        ContentValues value = new ContentValues();
//        value.put("name", "xipai");
//        value.put("parent", "gopaychan");
//        getContentResolver().insert(Uri.parse("content://com.bbk.gopay.plugin.provider/name"), value);
//
//        Cursor cursor = getContentResolver().query(Uri.parse("content://com.bbk.gopay.plugin.provider/name"), null, null, null, null);
//        if (cursor != null) {
//            while (cursor.moveToNext()) {
//                String text = cursor.getString(cursor.getColumnIndex("name")) + " parent: " + cursor.getString(cursor.getColumnIndex("parent"));
//                content.append("\n" + text);
//            }
//            cursor.close();
//        }
//        final ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//        List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(Integer.MAX_VALUE);
//        boolean isLife = false;
//        for (ActivityManager.RunningServiceInfo info : services) {
//            if (info.service.equals(new ComponentName(this, ProcessService1.class))) {
////                isLife = true;
//            }
//        }
//        if (!isLife) startService(new Intent(this, ProcessService.class));
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        NestedScrollView sv =  ((NestedScrollView)findViewById(R.id.scroll_view));
        sv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.i(TAG, "onCreate: " + ((CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams()).getBehavior().toString());
            }
        });
        Log.i(TAG, "onCreate: " + ((CoordinatorLayout.LayoutParams)sv.getLayoutParams()).getBehavior().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onCreate: " + appBarLayout.getLayoutParams());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onCreate: " + appBarLayout.getLayoutParams());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        startService(new Intent(this, ProcessService.class));
//
//        System.out.printf("");
//        Log.i(TAG, "onDestroy:");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE){
            Log.i(TAG, "onCreate: " + ((CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams()).getBehavior().toString());
        }
        return super.onTouchEvent(event);
    }
}
