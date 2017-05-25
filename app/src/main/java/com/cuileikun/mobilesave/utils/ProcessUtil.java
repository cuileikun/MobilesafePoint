package com.cuileikun.mobilesave.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.os.Build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.List;

@SuppressLint("NewApi")
public class ProcessUtil {
	
	/**
	 * 获取正在运行的进程的个数
	 */
	public static int getRunningProcessCount(Context context){
		//获取进程的管理者
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//获取正在运行的进程
		List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
		return list.size();
	}
	/**
	 * 获取系统总进程
	 */
	public static int getAllProcessCount(Context context){
		
		//安装成功一个软件,软件在系统中就有一个进程,软件运行:进程变成正在运行的进程;软件不运行:进程变成空闲进程     总进程=空闲进程+运行进程
		
		//一个软件对应一个进程,显示一个软件可以对应多个进程, com.qq   com.qq.activityA   com.qq.activityB,认为这些进程是属于一个软件的,进程名称com.qq
		
		//一个软件com.qq  另一个软件   com.qq       升级条件   包名一致   签名一致
		
		//获取包的管理者
		PackageManager pm = context.getPackageManager();
		//获取系统中安装所有应用程序信息
		List<PackageInfo> list = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES | PackageManager.GET_PROVIDERS | PackageManager.GET_RECEIVERS);
		//一个软件占用一个进程,一个软件占用了多个进程
		
		int count=0;
		for (PackageInfo packageInfo : list) {
			
			//去重
			HashSet<String> set  = new HashSet<String>();
			set.add(packageInfo.applicationInfo.processName);
			
			//activity  service   provider  receiver
			//activity
			ActivityInfo[] activities = packageInfo.activities;
			if (activities != null) {
				for (ActivityInfo activityInfo : activities) {
					set.add(activityInfo.processName);
				}
			}
			//service  
			ServiceInfo[] services = packageInfo.services;
			if (services != null) {
				for (ServiceInfo serviceInfo : services) {
					set.add(serviceInfo.processName);
				}
			}
			//receiver
			ActivityInfo[] receivers = packageInfo.receivers;
			if (receivers != null) {
				for (ActivityInfo activityInfo : receivers) {
					set.add(activityInfo.processName);
				}
			}
			//provider
			ProviderInfo[] providers = packageInfo.providers;
			if (providers != null) {
				for (ProviderInfo providerInfo : providers) {
					set.add(providerInfo.processName);
				}
			}
			
			count+=set.size();
		}
		return count;
	}
	
	/**
	 * 获取空闲内存
	 */
	public static long getFreeMemory(Context context){
		
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		
		MemoryInfo outInfo = new MemoryInfo();//创建白纸
		am.getMemoryInfo(outInfo);//把内存的信息写到白纸中
		long avaimem = outInfo.availMem;//获取可用内存
		//outInfo.totalMem;//获取总内存
		return avaimem;
	}
	
	/**
	 * 获取总内存
	 */
	public static long getTotalMemory(Context context){
		long totalMem = 0;
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		
		MemoryInfo outInfo = new MemoryInfo();//创建白纸
		am.getMemoryInfo(outInfo);//把内存的信息写到白纸中
		
		//判断系统api版本,根据版本实现不同的操作
		//Build.VERSION.SDK_INT : 获取当前系统的sdk版本
		if (Build.VERSION.SDK_INT >= 16) {
			totalMem = outInfo.totalMem;//版本16
		}else{
			//采用其他方式获取总内存,通过proc\meminfo
			totalMem = getProcTotalMemory();
		}
		return totalMem;
	}
	/**
	 * Description: 解析proc\meminfo文件获取内存信息
	 */
	private static long getProcTotalMemory() {
		File file = new File("proc/meminfo");
		try {
			BufferedReader  br = new BufferedReader(new FileReader(file));
			String readLine = br.readLine();
			readLine.replace("MemTotal:", "");
			readLine.replace("kB", "");
			String trim = readLine.trim();
			long memory = Long.parseLong(trim);
			// 1lb = 1024   1*1024  kb -> b
			return memory*1024;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	
	
	
	
	
	
	
	
	
	
	
	
}
