package com.cuileikun.mobilesave.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cuileikun.mobilesave.R;
import com.cuileikun.mobilesave.activity.appmanager.AppManagerActivity;
import com.cuileikun.mobilesave.activity.callsmssafe.CallSMSSafeActivity;
import com.cuileikun.mobilesave.activity.lostfind.LostFindActivity;
import com.cuileikun.mobilesave.activity.lostfind.SetUp1Activity;
import com.cuileikun.mobilesave.bean.HomeGridviewItemBeanInfo;
import com.cuileikun.mobilesave.utils.Contants;
import com.cuileikun.mobilesave.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends Activity implements AdapterView.OnItemClickListener {
	private ImageView iv_home_logo;
	private ImageView iv_home_setting;
	private GridView gv_home_gridview;

	private final static String[] TITLES = new String[] { "手机防盗", "骚扰拦截",
			"软件管家", "进程管理", "流量统计", "手机杀毒", "缓存清理", "常用工具" };
	private final static String[] DESCS = new String[] { "远程定位手机", "全面拦截骚扰",
			"管理您的软件", "管理运行进程", "流量一目了然", "病毒无处藏身", "系统快如火箭", "工具大全" };

	private final static int[] ICONS = new int[] { R.drawable.sjfd,
			R.drawable.srlj, R.drawable.rjgj, R.drawable.jcgl, R.drawable.lltj,
			R.drawable.sjsd, R.drawable.hcql, R.drawable.cygj };

	private List<HomeGridviewItemBeanInfo> list;
	private AlertDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//加载布局
		setContentView(R.layout.activity_home);
		initView();
		
	}

	/**
	 * Description:初始化控件
	 */
	private void initView() {
		iv_home_logo = (ImageView) findViewById(R.id.iv_home_logo);
		iv_home_setting = (ImageView) findViewById(R.id.iv_home_setting);
		gv_home_gridview = (GridView) findViewById(R.id.gv_home_gridview);

		setAnimation();
		// 将数组的数据保存到集合中
		list = new ArrayList<HomeGridviewItemBeanInfo>();
		for (int i = 0; i < ICONS.length; i++) {
			HomeGridviewItemBeanInfo info = new HomeGridviewItemBeanInfo();
			info.iconId = ICONS[i];
			info.title = TITLES[i];
			info.desc = DESCS[i];
			list.add(info);
		}
		gv_home_gridview.setAdapter(new Myadapter());
		iv_home_setting.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 跳转到设置中心界面
				Intent intent = new Intent(HomeActivity.this,
						SettingActivity.class);
				startActivity(intent);
			}
		});
		gv_home_gridview.setOnItemClickListener(HomeActivity.this);
	}

	private class Myadapter extends BaseAdapter {
		// 获取条目的个数
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		// 根据条目的位置获取相对应的数据
		// position : 条目的位置
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		// 获取条目的id
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		// 设置条目的样式
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			// 将布局文件转成view对象
			View view = View.inflate(getApplicationContext(),
					R.layout.home_item, null);
			// view.findViewById : 表示是从home_item的布局中获取数据
			// findViewById : 表示从activity_home的布局中获取数据
			ImageView iv_homeitem_icon = (ImageView) view
					.findViewById(R.id.iv_homeitem_icon);
			TextView tv_homeitem_title = (TextView) view
					.findViewById(R.id.tv_homeitem_title);
			TextView tv_homeitem_desc = (TextView) view
					.findViewById(R.id.tv_homeitem_desc);

			// 设置显示数据
			// 1.获取相应条目的bean对象
			HomeGridviewItemBeanInfo homeGridviewItemBeanInfo = list
					.get(position);
			// 2.设置显示
			iv_homeitem_icon.setImageResource(homeGridviewItemBeanInfo.iconId);
			tv_homeitem_title.setText(homeGridviewItemBeanInfo.title);
			tv_homeitem_desc.setText(homeGridviewItemBeanInfo.desc);
			return view;
		}

	}


	/**
	 * Description: logo属性动画的实现
	 */
	private void setAnimation() {
		// 参数1：执行属性动画的控件
		// 参数2：执行的操作
		// 参数3：一组动画执行的值
		ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(iv_home_logo,
				"RotationX", 0f, 90f, 270f, 360f);
		// 动画的持续时间
		objectAnimator.setDuration(2000);
		// 执行的次数吗，INFINITE ：代表一直执行
		objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
		// 旋转的模式
		// REVERSE : 动画执行完成，逆向执行动画
		// RESTART : 每次都初始化执行动画
		objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
		// 执行动画
		objectAnimator.start();
	}








	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
		switch (position) {
			case 0:
				//手机防盗
				//根据是否保存密码显示设置密码还是输入密码
				//获取保存密码
				String psw = SharedPreferencesUtil.getString(HomeActivity.this, Contants.SETPASSWORD, "");
				if (TextUtils.isEmpty(psw)) {//null    ""
					showSetPassWordDialog();
				}else{
					showEnterPasswordDialog();
				}
				break;
			case 1:
				//骚扰拦截
				Intent intent = new Intent(HomeActivity.this,CallSMSSafeActivity.class);
				startActivity(intent);
				break;
			case 2:
				//软件管家
				Intent intent2 = new Intent(HomeActivity.this,AppManagerActivity.class);
				startActivity(intent2);
				break;
			case 3:
				//进程管理
//				Intent intent3 = new Intent(HomeActivity.this,ProcessManagerActivity.class);
//				startActivity(intent3);
				break;
			case 4:
				//流量统计
//				Intent intent4 = new Intent(HomeActivity.this,TrafficManagerActivity.class);
//				startActivity(intent4);
				break;
			case 5:
				//手机杀毒
//				Intent intent5 = new Intent(HomeActivity.this,AntivirusActivity.class);
//				startActivity(intent5);
				break;
			case 6:
				//缓存清理
//				Intent intent6 = new Intent(HomeActivity.this,ClearCacheActivity.class);
//				startActivity(intent6);
				break;
			case 7://常用工具
//				Intent intent7 = new Intent(HomeActivity.this,CommonToolsActivity.class);
//				startActivity(intent7);
				break;
		}
	}

	/**
	 * Description:输入密码对话框
	 */
	private void showEnterPasswordDialog() {
		//复制步骤一
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		//将布局文件转化成view对象
		View view = View.inflate(this, R.layout.dialog_enterpassword, null);

		//复制步骤三
		//初始化控件
		final EditText et_setpassword_psw = (EditText) view.findViewById(R.id.et_setpassword_psw);
		Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
		Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

		//复制步骤四
		btn_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//1.获取输入的密码
				String psw = et_setpassword_psw.getText().toString().trim();
				//2.判断密码是否为空
				if (TextUtils.isEmpty(psw)) {
					Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				//3.获取保存的密码
				String sp_psw = SharedPreferencesUtil.getString(HomeActivity.this, Contants.SETPASSWORD, "");
				//4.判断输入的密码和保存的密码是否一致,一致,跳转到手机防盗页面,不一致,提醒用户密码错误
				if (psw.equals(sp_psw)) {
					//跳转到手机防盗页面
					Toast.makeText(HomeActivity.this, "密码正确", Toast.LENGTH_SHORT).show();
					//隐藏dialog
					dialog.dismiss();
					//跳转到手机防盗页面
					enterLostFind();
				}else{
					//提醒用户
					Toast.makeText(HomeActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
				}
			}
		});


		btn_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		//复制步骤二
		builder.setView(view);//添加view
		//builder.show();
		dialog = builder.create();
		dialog.show();
	}
	private void showSetPassWordDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		//将布局文件转化成view对象
		View view = View.inflate(this, R.layout.dialog_setpassword, null);
		//初始化控件
		final EditText et_setpassword_psw = (EditText) view.findViewById(R.id.et_setpassword_psw);
		final EditText et_setpassword_confirm = (EditText) view.findViewById(R.id.et_setpassword_confirm);
		Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
		Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

		btn_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//1.获取输入的密码
				String psw = et_setpassword_psw.getText().toString().trim();
				//2.判断密码是否为空，为空提醒用户
				if (TextUtils.isEmpty(psw)) {//null :没有内存    ""：有内存但是没有内容
					Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				//3.获取确认密码
				String confrim_psw = et_setpassword_confirm.getText().toString().trim();
				//4.判断两次密码是否一致，一致：保存密码，不一致：提醒用户，密码不一致
				if (psw.equals(confrim_psw)) {
					//保存密码
					SharedPreferencesUtil.saveString(HomeActivity.this, Contants.SETPASSWORD, psw);
					Toast.makeText(getApplicationContext(), "设置密码成功", Toast.LENGTH_SHORT).show();
					//隐藏对话框
					dialog.dismiss();
				}else{
					//不一致
					Toast.makeText(getApplicationContext(), "两次密码不一致", Toast.LENGTH_SHORT).show();
				}
			}
		});
		btn_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		builder.setView(view);//添加view
		//builder.show();
		dialog = builder.create();
		dialog.show();
	}

	/**
	 *
	 * Description:跳转手机防盗页面
	 *
	 * @author Administrator
	 *
	 * @date 2015-11-29
	 *
	 * @date 上午9:40:25
	 */
	protected void enterLostFind() {
		//如果用户是第一次进入手机防盗模块,跳转到设置引导界面,进行手机防盗功能设置,
		//如果设置过了,再次进入的时候就要跳转到手机防盗界面,进行防盗功能展示.

		boolean isFirstEnter = SharedPreferencesUtil.getBoolean(this, Contants.ISFIRSTENTER, false);

		if (isFirstEnter) {
			//不是第一次,跳转到手机防盗页面
			Intent intent = new Intent(this,LostFindActivity.class);
			startActivity(intent);
		}else{
			//是第一次,进入引导界面
			Intent intent = new Intent(this,SetUp1Activity.class);
			startActivity(intent);
		}
	}

}

