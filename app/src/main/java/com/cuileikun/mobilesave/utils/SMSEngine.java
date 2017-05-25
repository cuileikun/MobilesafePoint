package com.cuileikun.mobilesave.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SMSEngine {
	
	//1.创建刷子
	public interface Showprogress{
		public void setMax(int max);
		public void setProgress(int progress);
	}
	
	
	public static class SMSinfo{
		public String address;
		public String date;
		public String type;
		public String body;
		public SMSinfo(String address, String date, String type, String body) {
			super();
			this.address = address;
			this.date = date;
			this.type = type;
			this.body = body;
		}
		
	}
	/**
	 * Description:获取系统短信
	 */
	//2.将刷子放到一个地方
	public static void getSMS(Context context,Showprogress showprogress){
		List<SMSinfo> list = new ArrayList<SMSinfo>();
		//获取内容解析者
		ContentResolver contentResolver = context.getContentResolver();
		//获取uri地址
		Uri uri = Uri.parse("content://sms");
		Cursor cursor = contentResolver.query(uri, new String[]{"address","date","type","body"}, null, null, null);
		
		//设置最大进度
		showprogress.setMax(cursor.getCount());
		
		int progress = 0;
		
		if (cursor!= null) {
			while(cursor.moveToNext()){
				SystemClock.sleep(1000);
				String address = cursor.getString(0);
				String date = cursor.getString(1);
				String type = cursor.getString(2);
				String body = cursor.getString(3);
				//System.out.println("address:"+address+"   date:"+date+"  type:"+type+"  body:"+body);
				SMSinfo smsinfo = new SMSinfo(address, date, type, body);
				//将bean类保存到集合中
				list.add(smsinfo);
				progress++;
				showprogress.setProgress(progress);
			}
			try {
				Gson gson = new Gson();
				String json = gson.toJson(list);
				FileWriter fileWriter = new FileWriter(new File("/mnt/sdcard/sms.txt"));
				fileWriter.write(json);
				fileWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//短信备份:一般都是保存到数据库中,保存到文件中 : 比较常用;xml,json
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
