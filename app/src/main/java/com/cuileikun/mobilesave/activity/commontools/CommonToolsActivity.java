package com.cuileikun.mobilesave.activity.commontools;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.cuileikun.mobilesave.R;
import com.cuileikun.mobilesave.service.WatchDog1Service;
import com.cuileikun.mobilesave.utils.SMSEngine;
import com.cuileikun.mobilesave.utils.Serviceutil;
import com.cuileikun.mobilesave.view.SettingView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class CommonToolsActivity extends Activity {
    private SettingView sv_commontools_address;
    private SettingView sv_setting_callsmssafe;
    private SettingView sv_commontool_sms;
    private SettingView sv_commontools_writesms;
    private SettingView sv_commontools_watchdog;
    private SettingView sv_commontools_watchdog1;
    private SettingView sv_commontools_watchdog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_tools);
        initView();
    }
    // 初始化控件
    private void initView() {
        sv_commontools_address = (SettingView) findViewById(R.id.sv_commontools_address);
        sv_setting_callsmssafe = (SettingView) findViewById(R.id.sv_setting_callsmssafe);
        sv_commontool_sms = (SettingView) findViewById(R.id.sv_commontool_sms);
        sv_commontools_writesms = (SettingView) findViewById(R.id.sv_commontools_writesms);
        sv_commontools_watchdog = (SettingView) findViewById(R.id.sv_commontools_watchdog);
        sv_commontools_watchdog1 = (SettingView) findViewById(R.id.sv_commontools_watchdog1);
        sv_commontools_watchdog2 = (SettingView) findViewById(R.id.sv_commontools_watchdog2);

        sv_commontools_address.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 跳转到号码归属地查询界面
                Intent intent = new Intent(CommonToolsActivity.this,
                        AddressActivity.class);
                startActivity(intent);
            }
        });

        sv_setting_callsmssafe.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 跳转到常用号码界面
                Intent intent = new Intent(CommonToolsActivity.this,
                        CommonNumberActivity.class);
                startActivity(intent);

            }
        });
        // 短信备份的点击事件
        sv_commontool_sms.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // progressbdialog是可以在子线程中更新ui
                final ProgressDialog progressDialog = new ProgressDialog(
                        CommonToolsActivity.this);
                progressDialog.setCancelable(false);// 禁止对话框取消
                progressDialog
                        .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				/*
				 * progressDialog.setMax(max);//设置最大进度
				 * progressDialog.setProgress(value);//设置当前进度
				 */
                progressDialog.show();

                new Thread() {
                    public void run() {
                        // 3.调用刷子,实现自己的操作
                        SMSEngine.getSMS(CommonToolsActivity.this,
                                new SMSEngine.Showprogress() {

                                    public void setProgress(int progress) {
                                        progressDialog.setProgress(progress);
                                    }

                                    public void setMax(int max) {
                                        progressDialog.setMax(max);
                                    }
                                });
                        progressDialog.dismiss();
                    };
                }.start();
                // 回调函数 :界面操作需要业务去修改一些操作进行支持
                // 回调函数:暴露一个接口,干什么不知道,我们去调用这个接口,在我们的操作中实现想要的操作
            }
        });
        // 短信还原的点击事件
        sv_commontools_writesms.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    // 读取文件中的数据信息
                    BufferedReader br = new BufferedReader(new FileReader(
                            new File("/mnt/sdcard/sms.txt")));
                    String json = br.readLine();
                    // 解析json数据,将json数据转化成list集合
                    Gson gson = new Gson();
                    // 参数1:json数据 new TypeToken<List<SMSinfo>>(){}.getType()
                    // 参数2:转化成的类型
                    List<SMSEngine.SMSinfo> list = gson.fromJson(json,
                            new TypeToken<List<SMSEngine.SMSinfo>>() {
                            }.getType());
                    for (SMSEngine.SMSinfo smSinfo : list) {
                        // 还原短信,给数据的短信的表中插入数据
                        ContentResolver resolver = getContentResolver();
                        Uri uri = Uri.parse("content://sms");
                        ContentValues values = new ContentValues();
                        values.put("address", smSinfo.address);
                        values.put("date", smSinfo.date);
                        values.put("type", smSinfo.type);
                        values.put("body", smSinfo.body);
                        resolver.insert(uri, values);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // 程序锁的点击事件
        sv_commontools_watchdog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 跳转到程序锁的界面
                Intent intent = new Intent(CommonToolsActivity.this,
                        AppLockActivity.class);
                startActivity(intent);
            }
        });

        // 开启电子狗1
        sv_commontools_watchdog1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommonToolsActivity.this,
                        WatchDog1Service.class);
                // 打开关闭骚扰拦截操作
                // 打开骚扰拦截:开启服务
                // 关闭骚扰拦截:关闭服务
                // 开启服务 再次点击 -> 关闭服务 关闭服务 再次点击 -> 开启服务
                // 问题?:怎么知道服务是否开启
                // 获取服务是否开启
                if (Serviceutil.isServiceRunning(getApplicationContext(),
                        "com.cuileikun.mobilesave.service.WatchDog1Service")) {
                    // 开启,再次点击,关闭
                    stopService(intent);
                    // 改变图标
                    sv_commontools_watchdog1.setToggle(false);
                } else {
                    // 关闭,再次点击,开启
                    startService(intent);
                    sv_commontools_watchdog1.setToggle(true);
                }
            }
        });

        sv_commontools_watchdog2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //跳转到辅助功能界面
                /**
                 * START
                 * {
                 * act=android.settings.ACCESSIBILITY_SETTINGS
                 * cmp=com.android.settings/.Settings$AccessibilitySettingsActivity u=0
                 * } from pid 1255
                 */
                Intent intent = new Intent();
                intent.setAction("android.settings.ACCESSIBILITY_SETTINGS");
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        // 在界面可见的时候,去进行回显操作
        if (Serviceutil.isServiceRunning(getApplicationContext(),
                "com.itheima.mobliesafesh04.service.WatchDog1Service")) {
            // 设置开启的图标
            sv_commontools_watchdog1.setToggle(true);
        } else {
            // 设置关闭的图标
            sv_commontools_watchdog1.setToggle(false);
        }

        if (Serviceutil.isServiceRunning(getApplicationContext(),
                "com.itheima.mobliesafesh04.service.WatchDog2Service")) {
            // 设置开启的图标
            sv_commontools_watchdog2.setToggle(true);
        } else {
            // 设置关闭的图标
            sv_commontools_watchdog2.setToggle(false);
        }
    }



}

