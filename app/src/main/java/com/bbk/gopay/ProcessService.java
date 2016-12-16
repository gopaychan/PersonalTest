package com.bbk.gopay;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by gopaychan on 2016/11/21.
 */
public class ProcessService extends Service {
    private String mPackageName = "";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//        Toast.makeText(this, "onStartCommand", Toast.LENGTH_LONG).show();
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                    String packageName = cn.getPackageName();
                    if (packageName != null && !packageName.equals(mPackageName)) {
                        mPackageName = packageName;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ProcessService.this, mPackageName, Toast.LENGTH_SHORT).show();
                            }
                        });
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
//        startService(new Intent(this, ProcessService1.class));

        List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(Integer.MAX_VALUE);
        boolean isLife = false;
        for (ActivityManager.RunningServiceInfo info : services) {
            if (info.service.equals(new ComponentName(this, ProcessService1.class))) {
                isLife = true;
            }
        }
        Log.e("isLife", isLife + "");
        if (!isLife) startService(new Intent(this, ProcessService1.class));
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startService(new Intent(this, ProcessService1.class));
        Toast.makeText(this, "onDestroy", Toast.LENGTH_LONG).show();
    }
}
