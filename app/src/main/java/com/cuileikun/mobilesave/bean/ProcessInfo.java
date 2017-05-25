package com.cuileikun.mobilesave.bean;

import android.graphics.drawable.Drawable;

public class ProcessInfo {

	public String packagename;
	public String name;
	public Drawable icon;
	public long memorysize;
	public boolean isSystem;
	//条目是否选中,checkbox是否选中
	public boolean isChecked = false;
	
}
