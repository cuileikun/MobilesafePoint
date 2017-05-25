package com.cuileikun.mobilesave.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;

import java.util.List;

public class Serviceutil {
	/**
	 * 
	 * Description:获取服务是否开启
	 *
	 * @author Administrator
	 *
	 * @date 2015-12-4
	 *
	 * @date 上午9:52:37
	 */
	public static boolean isServiceRunning(Context context,String serviceClassName){
		//获取服务是否开启
		//进程的管理者,活动的管理者
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//获取系统中正在运行所有的服务
		//maxNum : 获取服务的上限个数,最多获取多少个
		List<RunningServiceInfo> runningServices = activityManager.getRunningServices(1000);
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			//获取正在运行的服务的组件表示
			ComponentName componentName = runningServiceInfo.service;
			String className = componentName.getClassName();
			//判断获取的正在运行的服务的全类名和我们传递过来的服务的全类名是否一致,一致,表示运行,不一致,表示没有运行
			if (serviceClassName.equals(className)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	
	
	
	
	
	
	
	
}
