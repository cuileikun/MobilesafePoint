package com.cuileikun.mobilesave.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {

	
	private static SharedPreferences sp;

	/**
	 * 
	 * Description:保存状态
	 *
	 * @author Administrator
	 *
	 * @date 2015-11-28
	 *
	 * @date 下午4:12:58
	 */
	public static void saveBoolean(Context context,String key,boolean value){
		if (sp==null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().putBoolean(key, value).commit();
	}
	
	
	/**
	 * 
	 * Description:获取状态
	 *
	 * @author Administrator
	 *
	 * @date 2015-11-28
	 *
	 * @date 下午4:13:09
	 */
	public static boolean getBoolean(Context context,String key,boolean defValue){
		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return sp.getBoolean(key, defValue);
	}
	
	/**
	 * 
	 * Description:保存信息
	 *
	 * @author Administrator
	 *
	 * @date 2015-11-28
	 *
	 * @date 下午4:12:58
	 */
	public static void saveString(Context context,String key,String value){
		if (sp==null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().putString(key, value).commit();
	}
	
	
	/**
	 * 
	 * Description:获取信息
	 *
	 * @author Administrator
	 *
	 * @date 2015-11-28
	 *
	 * @date 下午4:13:09
	 */
	public static String getString(Context context,String key,String defValue){
		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return sp.getString(key, defValue);
	}
	
	
	
	
	
	
	
	
	
	
}
