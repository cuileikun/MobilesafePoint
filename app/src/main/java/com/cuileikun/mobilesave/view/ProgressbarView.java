package com.cuileikun.mobilesave.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cuileikun.mobilesave.R;

public class ProgressbarView extends LinearLayout {

	private TextView tv_appmanaget_title;
	private TextView tv_appmanager_used;
	private TextView tv_appmanager_free;
	private ProgressBar pb_appmanager_porgress;

	public ProgressbarView(Context context) {
		super(context);
		initView();
	}

	public ProgressbarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public ProgressbarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	// 添加布局文件到自定义组合中,初始化控件
	public void initView() {
		View view = View.inflate(getContext(), R.layout.progressbarview, this);

		tv_appmanaget_title = (TextView) view
				.findViewById(R.id.tv_appmanaget_title);
		tv_appmanager_used = (TextView) view
				.findViewById(R.id.tv_appmanager_used);
		tv_appmanager_free = (TextView) view
				.findViewById(R.id.tv_appmanager_free);
		pb_appmanager_porgress = (ProgressBar) view
				.findViewById(R.id.pb_appmanager_porgress);
	}

	// 创建设置控件内容的方法

	// 设置title方法
	public void setTitle(String title) {
		tv_appmanaget_title.setText(title);
	}

	// 设置已用内存方法
	public void setUsed(String used) {
		tv_appmanager_used.setText(used);
	}

	// 设置空闲内存的方法
	public void setFree(String free) {
		tv_appmanager_free.setText(free);
	}
	
	//设置progressbar的进度操作
	public void setProgress(int progress){
		pb_appmanager_porgress.setProgress(progress);
	}

}
