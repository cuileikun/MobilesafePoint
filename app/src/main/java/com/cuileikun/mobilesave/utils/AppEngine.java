package com.cuileikun.mobilesave.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.cuileikun.mobilesave.bean.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppEngine {

	/**
	 * Description:获取系统中所有的软件的信息
	 */
	public static List<AppInfo> getAllApplication(Context context){
		
		List<AppInfo> list = new ArrayList<AppInfo>();
		
		//获取包的管理者
		PackageManager packageManager = context.getPackageManager();
		//获取系统中所有安装的应用程序的清单文件中的信息
		List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
		
		for (PackageInfo packageInfo : installedPackages) {
			int uid = packageInfo.applicationInfo.uid;
			//包名
			String packageName = packageInfo.packageName;
			//图标
			Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
			//名称
			String name = packageInfo.applicationInfo.loadLabel(packageManager).toString();
			//占用空间大小
			//sourceDir : data\app
			String sourceDir = packageInfo.applicationInfo.sourceDir;
			long memorysize = new File(sourceDir).length();
			//获取是否是系统程序
			boolean isSystem;
			//1.获取应用程序的标签信息
			int flags = packageInfo.applicationInfo.flags;
			if ((ApplicationInfo.FLAG_SYSTEM & flags) == ApplicationInfo.FLAG_SYSTEM) {
				//是系统程序
				isSystem = true;
			}else{
				//用户程序
				isSystem = false;
			}
			//获取是否安装到SD卡
			boolean isSD;
			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
				//安装到SD卡中
				isSD = true;
			}else{
				//系统内存中
				isSD = false;
			}
			//将数据保存到bean类中
			AppInfo appInfo = new AppInfo(packageName, name, icon, isSD, memorysize, isSystem,uid);
			//将bean类,添加到集合中
			list.add(appInfo);
		}
		return list;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
