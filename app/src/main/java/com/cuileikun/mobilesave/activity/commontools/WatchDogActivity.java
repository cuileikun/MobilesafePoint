package com.cuileikun.mobilesave.activity.commontools;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cuileikun.mobilesave.R;

public class WatchDogActivity extends Activity {
    private ImageView iv_watchdog_icon;
    private TextView tv_watchdog_name;
    private EditText et_watchdog_psw;
    private String packagename;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_dog);
        iv_watchdog_icon = (ImageView) findViewById(R.id.iv_watchdog_icon);
        tv_watchdog_name = (TextView) findViewById(R.id.tv_watchdog_name);
        et_watchdog_psw = (EditText) findViewById(R.id.et_watchdog_psw);


        Intent intent = getIntent();

        packagename = intent.getStringExtra("packagename");
        try {
            PackageManager pm = getPackageManager();
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packagename, 0);
            String name = applicationInfo.loadLabel(pm).toString();
            tv_watchdog_name.setText(name);
            Drawable icon = applicationInfo.loadIcon(pm);
            iv_watchdog_icon.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    //物理按钮的事件执行方法监听
    //keyCode : 按钮标示
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //返回到主界面
            Intent intent = new Intent();
            intent.setAction("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    public void lock(View v){
        //获取输入的内容
        String psw = et_watchdog_psw.getText().toString().trim();
        if (TextUtils.isEmpty(psw)) {
            Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if ("123".equals(psw)) {
            //告诉服务当前应用程序已经解锁,不需要在弹出加锁界面
            //广播,自定义发送一个广播,服务接受广播来进行操作
            Intent intent = new Intent();
            intent.setAction("com.itheima.mobliesafesh04.unlock");
            intent.putExtra("packagename", packagename);
            sendBroadcast(intent);
            finish();
        }

    }




}
