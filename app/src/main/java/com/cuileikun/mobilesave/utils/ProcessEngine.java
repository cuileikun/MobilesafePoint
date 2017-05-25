package com.cuileikun.mobilesave.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;
import com.cuileikun.mobilesave.R;
import com.cuileikun.mobilesave.bean.ProcessInfo;
import java.util.ArrayList;
import java.util.List;

public class ProcessEngine {
	/**
	 * Description:获取正在运行的进程信息
	 */
	public static List<ProcessInfo> getRunningProcess(Context context){
		
		List<ProcessInfo> list = new ArrayList<ProcessInfo>();
		
		//获取进程的管理者
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		//获取正在运行的进程的信息
		List<RunningAppProcessInfo> process = am.getRunningAppProcesses();
		for (RunningAppProcessInfo runningAppProcessInfo : process) {
			
				ProcessInfo processInfo = new ProcessInfo();
			
				//包名
				String packagename = runningAppProcessInfo.processName;
				
				processInfo.packagename = packagename;
				
				//占用内存
				//int[] : 存放进程的pid,数组 : 你存放几个进程的pid,最终会获取几个进程的内存信息
				MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
				long memorysize = memoryInfo[0].getTotalPss()*1024;
				
				processInfo.memorysize = memorysize;
				
				try {
					ApplicationInfo applicationInfo = pm.getApplicationInfo(packagename, 0);
					//名称
					String name = applicationInfo.loadLabel(pm).toString();
					
					processInfo.name = name;
					
					//图标
					Drawable icon = applicationInfo.loadIcon(pm);
					
					processInfo.icon = icon;
					
					//是否是系统进程
					boolean isSystem;
					//获取应用程序的所有标签信息
					int flags = applicationInfo.flags;
					if ((ApplicationInfo.FLAG_SYSTEM & flags) == ApplicationInfo.FLAG_SYSTEM) {
						//系统进程
						isSystem = true;
					}else{
						//用户进程
						isSystem = false;
					}
					
					processInfo.isSystem = isSystem;
					
				} catch (NameNotFoundException e) {
					//找不到applicationinfo,表示进程是系统进程,c进程
					//设置默认数据
					processInfo.name = packagename;
					processInfo.icon = context.getResources().getDrawable(R.drawable.ic_default);
					processInfo.isSystem = true;
				}
				list.add(processInfo);
		}
		return list;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
