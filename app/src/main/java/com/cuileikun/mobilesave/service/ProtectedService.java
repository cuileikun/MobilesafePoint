package com.cuileikun.mobilesave.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.cuileikun.mobilesave.R;
import com.cuileikun.mobilesave.SplashActivity;

public class ProtectedService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		//获取一个消息
		Notification notification = new Notification();
		//设置消息的图标
		notification.icon = R.drawable.ic_launcher;
		notification.tickerText = "notificationhhhhhhhhhhhhhhhhhhnotification";
		
		//设置消息的样式
		//参数1:包名
		//参数2:布局文件的id
		notification.contentView = new RemoteViews(getPackageName(), R.layout.protected_item);
		
		//PendingIntent : 延迟意图,点击消息,才会执行意图
		Intent intent = new Intent(this,SplashActivity.class);
		//FLAG_ACTIVITY_CLEAR_TASK : 避免出现重复返回统一个界面的问题
		intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);//给要跳转activity设置一个任务栈,在服务中跳转到一个activity的是会用,服务不在任务栈
		//context : 上下文件
		//requestCode :请求，码
		//intent : 跳转到activity的intent
		//flags : 指定操作的标签信息
		notification.contentIntent = PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		startForeground(100, notification);
	}
	
}
