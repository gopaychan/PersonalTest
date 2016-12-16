package com.bbk.gopay;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import java.util.List;

/**
 * Created by gopaychan on 2016/11/21.
 */
public class ProcessService1 extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        Toast.makeText(this, "ProcessService1 onStartCommand", Toast.LENGTH_LONG).show();
        List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(Integer.MAX_VALUE);
        boolean isLife = false;
        for (ActivityManager.RunningServiceInfo info : services) {
            if (info.service.equals(new ComponentName(this, ProcessService1.class))) {
                isLife = true;
            }
        }
        if (!isLife) startService(new Intent(this, ProcessService.class));
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "ProcessService1 onDestroy", Toast.LENGTH_LONG).show();
        startService(new Intent(this,ProcessService.class));
    }
}
