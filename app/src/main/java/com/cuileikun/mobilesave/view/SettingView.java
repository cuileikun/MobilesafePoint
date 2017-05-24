package com.cuileikun.mobilesave.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cuileikun.mobilesave.R;

public class SettingView extends RelativeLayout {

	private TextView tv_setting_title;
	private ImageView iv_setting_islock;
	private View view;
	private boolean isToggle;

	public SettingView(Context context) {
		super(context);
		initView();
	}

	public SettingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public SettingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
		// 1.获取设置自定义属性
		// 获取应用程序自定义相应的属性集合
		TypedArray tya = context.obtainStyledAttributes(attrs,
				R.styleable.SettingView);
		// SettingView_title : 表示的名称是Settingview的控件定义的title属性
		String title = tya.getString(R.styleable.SettingView_title);
		// 获取自定义背景属性的值
		int bkg = tya.getInt(R.styleable.SettingView_setBackground, 0);
		//获取是否显示开关的标示
		boolean isShow = tya.getBoolean(R.styleable.SettingView_isToggle, true);
		System.out.println("title" + title);

		tya.recycle();

		// 根据命名空间和属性名称获取相应的属性的值
		// String title =
		// attrs.getAttributeValue("http://schemas.android.com/apk/res/com.itheima.mobliesafesh04",
		// "title");

		// 3.将获取的属性的值，设置给相应的控件进行展示
		tv_setting_title.setText(title);

		// 根据属性的值设置相应的背景
		switch (bkg) {
		case 0:
			view.setBackgroundResource(R.drawable.setting_first_selector);
			break;
		case 1:
			view.setBackgroundResource(R.drawable.setting_middle_selector);
			break;
		case 2:
			view.setBackgroundResource(R.drawable.setting_last_selector);
			break;
		default:
			view.setBackgroundResource(R.drawable.setting_first_selector);
			break;
		}
		//根据标示设置开关是否显示
		iv_setting_islock.setVisibility(isShow ? View.VISIBLE : View.GONE);
	}

	/**
	 * 
	 * Description:加载自定义控件的布局
	 * 
	 * @author Administrator
	 * 
	 * @date 2015-11-28
	 * 
	 * @date 上午11:51:02
	 */
	private void initView() {
		// 将布局文件转化成view对象
		// 第一种方式
		/*
		 * View view = View.inflate(getContext(), R.layout.settingview,
		 * null);//爹有了，直接找孩子，亲生的 this.addView(view);
		 */
		// 第二种方式
		// root : 将布局文件转化的view对象，挂载在那个控件中
		view = View.inflate(getContext(), R.layout.settingview, this);
		// 2.初始化控件
		tv_setting_title = (TextView) view.findViewById(R.id.tv_setting_title);
		iv_setting_islock = (ImageView) view
				.findViewById(R.id.iv_setting_islock);
	}
	/**
	 * 
	 * Description:设置开关的打开和关闭
	 *
	 * @author Administrator
	 *
	 * @date 2015-11-28
	 *
	 * @date 下午3:54:59
	 */
	public void setToggle(boolean toggle){
		this.isToggle = toggle;
		if (toggle) {
			iv_setting_islock.setImageResource(R.drawable.on);
		}else{
			iv_setting_islock.setImageResource(R.drawable.off);
		}
	}
	/**
	 * 
	 * Description: 获取开关状态
	 *
	 * @author Administrator
	 *
	 * @date 2015-11-28
	 *
	 * @date 下午3:57:27
	 */
	public boolean getToggle(){
		return isToggle;
	}
	/**
	 * 
	 * Description:抽取设置打开关闭的操作
	 *
	 * @author Administrator
	 *
	 * @date 2015-11-28
	 *
	 * @date 下午4:03:54
	 */
	public void toggle(){
		setToggle(!isToggle);
	}
	
	
	
	
	
	
	
	
	

}
