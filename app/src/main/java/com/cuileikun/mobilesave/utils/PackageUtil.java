package com.cuileikun.mobilesave.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * 
 * Description: 获取版本号和版本名称
 *
 * @author Administrator
 *
 * @date 2015-11-25
 *
 * @date 上午10:33:48
 */
public class PackageUtil {
	/**
	 * 
	 * Description: 获取当前应用程序的版本号
	 *
	 * @author Administrator
	 *
	 * @date 2015-11-25
	 *
	 * @date 上午10:35:55
	 */
	public static int getVersionCode(Context context){
		//包的管理者,通过他是可以获取清单文件中的所有信息
		PackageManager pm = context.getPackageManager();
		try {
			//通过包名获取应用程序的信息
			//packageName : 应用程序的包名
			//flags :指定信息的标签，如果你设置了指定信息的标签，你就可以获取相应标签的对应的信息，设置GET_SIGNATURES,额外获取应用程序签名信息
			//0 : 基本的信息
			//context.getPackageName() : 获取当前的应用程序的包名
			PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			//找不到包名的异常
			e.printStackTrace();
		}
		return 0;
	}
	
	
	/**
	 * 
	 * Description: 获取当前应用程序的版本号名称
	 *
	 * @author Administrator
	 *
	 * @date 2015-11-25
	 *
	 * @date 上午10:35:55
	 */
	public static String getVersionName(Context context){
		//包的管理者,通过他是可以获取清单文件中的所有信息
		PackageManager pm = context.getPackageManager();
		try {
			//通过包名获取应用程序的信息
			//packageName : 应用程序的包名
			//flags :指定信息的标签，如果你设置了指定信息的标签，你就可以获取相应标签的对应的信息，设置GET_SIGNATURES,额外获取应用程序签名信息
			//0 : 基本的信息
			//context.getPackageName() : 获取当前的应用程序的包名
			PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
			
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			//找不到包名的异常
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
