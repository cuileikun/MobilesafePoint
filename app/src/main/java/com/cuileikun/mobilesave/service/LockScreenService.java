package com.cuileikun.mobilesave.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.util.List;

public class LockScreenService extends Service {

	private LockScreenReceiver lockScreenReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private class LockScreenReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//清理进程的操作
			killProcess();
		}
		
	}
	//清理进程
	public void killProcess() {
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
		for (RunningAppProcessInfo runningAppProcessInfo : list) {
			//屏蔽当前的应用程序
			if (!getPackageName().equals(runningAppProcessInfo.processName)) {
				am.killBackgroundProcesses(runningAppProcessInfo.processName);
			}
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		//代码注册锁屏的广播接受者
		//1.广播接受者
		lockScreenReceiver = new LockScreenReceiver();
		//2.设置过滤器
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);//锁屏的广播事件,锁屏的广播只能用代码进行注册
		//3.注册广播接受者
		registerReceiver(lockScreenReceiver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//注销广播接受者
		unregisterReceiver(lockScreenReceiver);
	}
	
	
	
	
	
	
	
	
	
}
