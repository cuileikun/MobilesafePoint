package com.cuileikun.mobilesave.bean;

import android.graphics.drawable.Drawable;

public class AppInfo {
	//包名
	public String packagename;
	//软件名称
	public String name;
	//软件的图标
	public Drawable icon;
	//是否保存到sd
	public boolean isSD;
	//软件占用的大小
	public long memorysize;
	//表示是用户程序还是系统程序
	public boolean isSystem;
	
	public int uid;
	
	public AppInfo(String packagename, String name, Drawable icon,
			boolean isSD, long memorysize, boolean isSystem,int uid) {
		super();
		this.packagename = packagename;
		this.name = name;
		this.icon = icon;
		this.isSD = isSD;
		this.memorysize = memorysize;
		this.isSystem = isSystem;
		this.uid = uid;
	}
	
	
	
}
