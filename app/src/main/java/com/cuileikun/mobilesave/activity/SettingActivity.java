package com.cuileikun.mobilesave.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.cuileikun.mobilesave.R;
import com.cuileikun.mobilesave.utils.Contants;
import com.cuileikun.mobilesave.utils.SharedPreferencesUtil;
import com.cuileikun.mobilesave.view.SettingView;


public class SettingActivity extends Activity {

	private SettingView sv_setting_update;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		initView();
	}
	/**
	 * 
	 * Description:初始化控件
	 *
	 * @author Administrator
	 *
	 * @date 2015-11-28
	 *
	 * @date 下午3:52:28
	 */
	private void initView() {
		sv_setting_update = (SettingView) findViewById(R.id.sv_setting_update);
		//回显按钮打开关闭状态操作
		boolean istoggle = SharedPreferencesUtil.getBoolean(this, Contants.TOGGLE, true);
		if (istoggle) {
			//打开
			sv_setting_update.setToggle(true);
		}else{
			//关闭
			sv_setting_update.setToggle(false);
		}
		
		sv_setting_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//打开 -> 关闭   关闭 -> 打开
				//获取开关是打开还是关闭的状态
				/*if (sv_setting_update.getToggle()) {
					//关闭
					//设置打开关闭开关
					sv_setting_update.setToggle(false);
				}else{
					//打开
					//设置打开关闭开关
					sv_setting_update.setToggle(true);
				}*/
				sv_setting_update.toggle();
				//保存按钮的打开关闭状态
				SharedPreferencesUtil.saveBoolean(SettingActivity.this, Contants.TOGGLE, sv_setting_update.getToggle());
			}
		});
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
