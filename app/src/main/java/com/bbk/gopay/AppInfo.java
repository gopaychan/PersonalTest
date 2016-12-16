package com.bbk.gopay;

/**
 * Created by gopaychan on 2016/11/21.
 */
public class AppInfo {
    private String appName;
    private String packageName;

    public AppInfo(String packageName) {
        this.packageName = packageName;
    }

    public AppInfo() {

    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AppInfo) {
            AppInfo info = (AppInfo) o;
            return appName.equals(info.appName) && packageName.equals(info.packageName);
        } else return false;
    }
}
