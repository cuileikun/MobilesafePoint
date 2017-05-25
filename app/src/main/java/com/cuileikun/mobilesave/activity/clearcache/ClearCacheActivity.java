package com.cuileikun.mobilesave.activity.clearcache;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cuileikun.mobilesave.R;
import com.cuileikun.mobilesave.bean.CacheInfo;

import java.util.ArrayList;
import java.util.List;

public class ClearCacheActivity extends Activity {
    private ImageView iv_clearcache_scanline;
    private Button btn_clearcache_clear;
    private ProgressBar pb_clearcache_porgress;
    private ImageView iv_clearcache_icon;
    private TextView tv_clearcache_name;
    private List<CacheInfo> list;
    private List<PackageInfo> packages;
    private TextView tv_clearcache_clearsize;
    private PackageManager pm;
    private ListView lv_clearcache_applications;
    private LinearLayout ll_clearcache_progressbar;
    private RelativeLayout rel_clearcache_scan;
    private TextView tv_clearcache_cleartext;
    private Button btn_clearcache_scan;
    private Myadapter myadapter;
    //缓存软件的个数
    private int totalCount = 0;
    //缓存软件的缓存总大小
    private long cachesizecount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear_cache);
        initView();
    }
    private void initView() {
        iv_clearcache_scanline = (ImageView) findViewById(R.id.iv_clearcache_scanline);
        btn_clearcache_clear = (Button) findViewById(R.id.btn_clearcache_clear);
        pb_clearcache_porgress = (ProgressBar) findViewById(R.id.pb_clearcache_porgress);
        iv_clearcache_icon = (ImageView) findViewById(R.id.iv_clearcache_icon);
        tv_clearcache_name = (TextView) findViewById(R.id.tv_clearcache_name);
        tv_clearcache_clearsize = (TextView) findViewById(R.id.tv_clearcache_clearsize);
        lv_clearcache_applications = (ListView) findViewById(R.id.lv_clearcache_applications);

        ll_clearcache_progressbar = (LinearLayout) findViewById(R.id.ll_clearcache_progressbar);
        rel_clearcache_scan = (RelativeLayout) findViewById(R.id.rel_clearcache_scan);
        tv_clearcache_cleartext = (TextView) findViewById(R.id.tv_clearcache_cleartext);
        btn_clearcache_scan = (Button) findViewById(R.id.btn_clearcache_scan);

        scan();
    }
    // 扫描操作
    private void scan() {

        ll_clearcache_progressbar.setVisibility(View.VISIBLE);
        rel_clearcache_scan.setVisibility(View.GONE);

        list = new ArrayList<CacheInfo>();
        list.clear();
        totalCount = 0;
        cachesizecount=0;

        // 1.实现扫描动画
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT,
                0, Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 1);
        translateAnimation.setDuration(500);
        translateAnimation.setRepeatCount(Animation.INFINITE);
        translateAnimation.setRepeatMode(Animation.REVERSE);

        iv_clearcache_scanline.startAnimation(translateAnimation);

        //2.屏蔽一键清理按钮点击事件
        btn_clearcache_clear.setEnabled(false);//设置按钮是否可用,true:可用,false:不可用
        pm = getPackageManager();
        new Thread(){
            public void run() {

                packages = pm.getInstalledPackages(0);
                //设置进度条最大进度
                pb_clearcache_porgress.setMax(packages.size());
                //设置当前进度
                int progress=0;
                for (final PackageInfo packageInfo : packages) {
                    SystemClock.sleep(100);
                    progress++;
                    pb_clearcache_porgress.setProgress(progress);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            //4.显示应用程序的图标和名称
                            try {
                                ApplicationInfo applicationInfo = pm.getApplicationInfo(packageInfo.packageName, 0);
                                String name = applicationInfo.loadLabel(pm).toString();
                                Drawable icon = applicationInfo.loadIcon(pm);

                                iv_clearcache_icon.setImageDrawable(icon);
                                tv_clearcache_name.setText(name);
                            } catch (PackageManager.NameNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    });
                    //5.获取缓存大小
                    // pm.getPackageSizeInfo(packageInfo.packageName, mStatsObserver);
                    //反射
//                    try {
//                        Method method = pm.getClass().getDeclaredMethod("getPackageSizeInfo", String.class,IPackageStatsObserver.class);
//                        method.invoke(pm, packageInfo.packageName,mStatsObserver);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }.start();
    }


//    IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
//        public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
//            final long cachesize = stats.cacheSize;
//            String packagename = stats.packageName;
//            System.out.println(packagename+"   cachesize:"+cachesize);
//            //6.设置显示缓存信息
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    tv_clearcache_clearsize.setText("缓存大小:"+ Formatter.formatFileSize(getApplicationContext(), cachesize));
//                }
//            });
//            //7.设置显示扫描的软件信息
//            //获取扫描的软件的详细信息
//            try {
//                ApplicationInfo applicationInfo = pm.getApplicationInfo(packagename, 0);
//                String name = applicationInfo.loadLabel(pm).toString();
//                Drawable icon = applicationInfo.loadIcon(pm);
//                CacheInfo cacheInfo = new CacheInfo();
//                cacheInfo.name = name;
//                cacheInfo.icon = icon;
//                cacheInfo.cachesize = cachesize;
//                cacheInfo.packagename = packagename;
//                //保存到集合中
//                //8.显示缓存软件的位置
//                if (cachesize > 0) {
//                    list.add(0, cacheInfo);
//                    totalCount++;
//                    cachesizecount+=cachesize;
//                }else{
//                    list.add(cacheInfo);
//                }
//            } catch (PackageManager.NameNotFoundException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            //listview设置adapter
//            runOnUiThread(new Runnable() {
//                public void run() {
//
//                    if (myadapter == null) {
//                        myadapter = new Myadapter();
//                        lv_clearcache_applications.setAdapter(myadapter);
//                    }else{
//                        myadapter.notifyDataSetChanged();
//                    }
//                    lv_clearcache_applications.smoothScrollToPosition(myadapter.getCount());
//                }
//            });
//            //扫描完成,滚动到第一个
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    if (packages.size() == list.size()) {
//                        lv_clearcache_applications.smoothScrollToPosition(0);
//                        //9.设置扫描完成界面展示
//                        //停止动画
//                        iv_clearcache_scanline.clearAnimation();
//                        //隐藏进度条界面,显示快速扫描界面
//                        ll_clearcache_progressbar.setVisibility(View.GONE);
//                        rel_clearcache_scan.setVisibility(View.VISIBLE);
//                        //显示缓存软件个数和缓存总大小
//                        tv_clearcache_cleartext.setText("总共有"+totalCount+"个缓存软件,共"+Formatter.formatFileSize(getApplicationContext(), cachesizecount));
//                        btn_clearcache_scan.setOnClickListener(new View.OnClickListener() {
//
//                            @Override
//                            public void onClick(View v) {
//                                //重新扫描
//                                scan();
//                            }
//                        });
//                        //10.清理
//                        btn_clearcache_clear.setEnabled(true);
//                        btn_clearcache_clear.setOnClickListener(new View.OnClickListener() {
//
//                            @Override
//                            public void onClick(View v) {
//                                //public abstract void freeStorageAndNotify(long freeStorageSize, IPackageDataObserver observer);
//                                try {
//                                    Method method = pm.getClass().getDeclaredMethod("freeStorageAndNotify",Long.TYPE,IPackageDataObserver.class);
//                                    method.invoke(pm, Long.MAX_VALUE,new MyIPackageDataObserver());
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                                scan();
//                            }
//                        });
//
//                    }
//                }
//            });
//        }
//    };
//
//    private class MyIPackageDataObserver extends IPackageDataObserver.Stub{
//
//        //删除缓存之后调用的方法
//        @Override
//        public void onRemoveCompleted(String packageName, boolean succeeded)
//                throws RemoteException {
//        }
//
//    }

    private class Myadapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = View.inflate(getApplicationContext(), R.layout.clearcache_item, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_clearcache_icon = (ImageView) view.findViewById(R.id.iv_clearcache_icon);

                viewHolder.iv_clearcache_clear = (ImageView) view.findViewById(R.id.iv_clearcache_clear);
                viewHolder.tv_clearcache_title = (TextView) view.findViewById(R.id.tv_clearcache_title);
                viewHolder.tv_clearcache_desc = (TextView) view.findViewById(R.id.tv_clearcache_desc);

                view.setTag(viewHolder);
            }else{
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            final CacheInfo cacheInfo = list.get(position);
            viewHolder.iv_clearcache_icon.setImageDrawable(cacheInfo.icon);
            viewHolder.tv_clearcache_title.setText(cacheInfo.name);
            viewHolder.tv_clearcache_desc.setText("缓存大小:"+Formatter.formatFileSize(getApplicationContext(), cacheInfo.cachesize));

            if (cacheInfo.cachesize > 0) {
                viewHolder.iv_clearcache_clear.setVisibility(View.VISIBLE);
            }else{
                viewHolder.iv_clearcache_clear.setVisibility(View.GONE);
            }
			/*
			 *
			 * START {
			 * act=android.settings.APPLICATION_DETAILS_SETTINGS
			 * dat=package:com.android.browser
			 * cmp=com.android.settings/.applications.InstalledAppDetails u=0
			 * } from pid 1268
			 */
            viewHolder.iv_clearcache_clear.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent =new Intent();
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.setData(Uri.parse("package:"+cacheInfo.packagename));
                    startActivityForResult(intent, 0);
                }
            });

            return view;
        }

    }
    static class ViewHolder{
        ImageView iv_clearcache_icon,iv_clearcache_clear;
        TextView tv_clearcache_title,tv_clearcache_desc;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        scan();
        super.onActivityResult(requestCode, resultCode, data);
    }



}
