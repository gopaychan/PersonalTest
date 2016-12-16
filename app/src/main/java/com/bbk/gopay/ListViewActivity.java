package com.bbk.gopay;

import android.app.ActivityManager;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.internal.util.AppendOnlyLinkedArrayList;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by gopaychan on 2016/11/11.
 */
public class ListViewActivity extends ListActivity {

    private List<AppInfo> items;
    private List<AppInfo> itemsTemp;
    private BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        startService(new Intent(this, ProcessService.class));
        items = new ArrayList<>();
        itemsTemp = new ArrayList<>();
        adapter = new MyAdapter();
        setListAdapter(adapter);
        final PackageManager pm = getPackageManager();
        loadData();
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    startActivity(new Intent(ListViewActivity.this, MainActivity.class));
                } else {
                    startActivity(pm.getLaunchIntentForPackage(items.get(position).getPackageName()));
                }
            }
        });

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                am.killBackgroundProcesses(items.get(position).getPackageName());
                loadData();
                return false;
            }
        });
    }

    private void loadData() {
        final PackageManager pm = getPackageManager();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) {
                Runtime rt = Runtime.getRuntime();
                Process proc = null;
                try {
                    proc = rt.exec("ps");
                    printRunTimeResult(e, proc.getInputStream());
                    printRunTimeResult(e, proc.getErrorStream());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .filter(new AppendOnlyLinkedArrayList.NonThrowingPredicate<String>() {
                    @Override
                    public boolean test(String string) {
                        return !string.contains("root") && !string.contains("system") && string.contains(".");
//                                && !string.contains("meizu") && !string.contains("android");
                    }
                })
                .map(new Function<String, AppInfo>() {
                    @Override
                    public AppInfo apply(String s) {
                        String str;
                        int startIndex = s.lastIndexOf(" ");
                        int endIndex = s.lastIndexOf(":");
                        if (startIndex > 0) {
                            if (endIndex > startIndex + 1)
                                str = s.substring(startIndex + 1, endIndex);
                            else str = s.substring(startIndex);
                        } else str = s;
                        return new AppInfo(str.trim());
                    }
                })

                .map(new Function<AppInfo, AppInfo>() {
                    @Override
                    public AppInfo apply(AppInfo info) {
                        ApplicationInfo appInfo = null;
                        try {
                            appInfo = pm.getApplicationInfo(info.getPackageName(), PackageManager.GET_SHARED_LIBRARY_FILES);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                            info.setAppName("fail");
                            return info;
                        }
                        if (appInfo == null)
                            info.setAppName("fail");
                        CharSequence labelName = pm.getApplicationLabel(appInfo);
                        if (labelName == null)
                            info.setAppName("fail");
                        else info.setAppName(labelName.toString());
                        return info;
                    }
                })
                .filter(new AppendOnlyLinkedArrayList.NonThrowingPredicate<AppInfo>() {
                    @Override
                    public boolean test(AppInfo s) {
                        return !s.getAppName().equals("fail") && pm.getLaunchIntentForPackage(s.getPackageName()) != null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AppInfo>() {
                    @Override
                    public void onNext(AppInfo value) {
                        if (!itemsTemp.contains(value))
                            itemsTemp.add(value);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public final void onComplete() {
                        items.clear();
                        items.addAll(itemsTemp);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }
                });
    }

    private void printRunTimeResult(ObservableEmitter<String> e, InputStream in) {
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        try {
            String line;
            while ((line = r.readLine()) != null) {
                e.onNext(line);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (items == null) return 0;
            else return items.size();
        }

        @Override
        public AppInfo getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ListViewActivity.this).inflate(R.layout.item, parent, false);
            }
            TextView tv = ViewHolder.get(convertView, R.id.text);
            tv.setText(getItem(position).getAppName());
            return convertView;
        }
    }
}
