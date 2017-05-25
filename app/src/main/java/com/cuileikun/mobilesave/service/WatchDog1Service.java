package com.cuileikun.mobilesave.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.cuileikun.mobilesave.activity.commontools.WatchDogActivity;
import com.cuileikun.mobilesave.db.dao.WatchDogDao;

import java.util.List;

public class WatchDog1Service extends Service {
    private boolean isWatch = false;
    private WatchDogDao watchDogDao;
    private CurrentUnlockAppReceiver currentUnlockAppReceiver;
    private String currentUnlockAppPackageName;
    private LockScreenOffReceiver lockScreenOffReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    private class CurrentUnlockAppReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            currentUnlockAppPackageName = intent.getStringExtra("packagename");
        }

    }
    public class LockScreenOffReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            currentUnlockAppPackageName = null;
        }

    }
    @Override
    public void onCreate() {
        super.onCreate();
        watchDogDao = new WatchDogDao(this);
        isWatch = true;


        currentUnlockAppReceiver = new CurrentUnlockAppReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.itheima.mobliesafesh04.unlock");
        registerReceiver(currentUnlockAppReceiver, intentFilter);


        lockScreenOffReceiver = new LockScreenOffReceiver();
        IntentFilter filter =new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(lockScreenOffReceiver, filter);





        new Thread(){
            public void run() {
                ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                //时时刻刻监听用户打开的程序
                //获取正在的运行的任务栈
                //maxNum : 获取正在运行的任务栈的个数
                while(isWatch){
                    //当前用户打开的应用,就是运行的任务栈的第一个应用
                    List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
                    for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTasks) {
                        ComponentName baseactivity = runningTaskInfo.baseActivity;//获取栈底的activity,包含main的actiivty
                        //runningTaskInfo.topActivity;//获取栈顶的activity
                        //获取应用程序的包名
                        String packageName = baseactivity.getPackageName();
                        //如果是加锁应用,弹出解锁界面
                        if (watchDogDao.queryLockAPP(packageName)) {
                            if (!packageName.equals(currentUnlockAppPackageName)) {
                                //弹出解锁界面
                                Intent intent = new Intent(WatchDog1Service.this,WatchDogActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//给跳转的activity设置一个任务栈
                                intent.putExtra("packagename", packageName);
                                startActivity(intent);
                            }
                        }
                    }
                    try {
                        sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
        }.start();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //停止监听,停止while循环
        isWatch = false;
        unregisterReceiver(currentUnlockAppReceiver);
        unregisterReceiver(lockScreenOffReceiver);
    }
}
