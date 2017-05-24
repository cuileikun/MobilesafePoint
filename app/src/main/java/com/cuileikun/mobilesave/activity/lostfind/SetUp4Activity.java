package com.cuileikun.mobilesave.activity.lostfind;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cuileikun.mobilesave.R;
import com.cuileikun.mobilesave.receiver.Admin;

public class SetUp4Activity extends SetUpBaseActivity {
    protected static final int REQUEST_CODE_ENABLE_ADMIN = 20;
    private RelativeLayout rel_setup4_devicepolicy;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName componentName;
    private ImageView iv_setup4_isactive;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up4);
        initView();
    }
    //初始化控件
    private void initView() {

        rel_setup4_devicepolicy = (RelativeLayout) findViewById(R.id.rel_setup4_devicepolicy);
        iv_setup4_isactive = (ImageView) findViewById(R.id.iv_setup4_isactive);

        //1.获取设备管理者
        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        //2.获取超级管理员组件表示
        componentName = new ComponentName(this, Admin.class);

        //回显操作
        //根据超级管理员是否激活进行图片设置
        if (devicePolicyManager.isAdminActive(componentName)) {
            iv_setup4_isactive.setImageResource(R.drawable.admin_activated);
        }else{
            iv_setup4_isactive.setImageResource(R.drawable.admin_inactivated);
        }

        rel_setup4_devicepolicy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //激活 点击 -> 取消激活       没有激活  点击 -> 激活超级管理员
                //3.判断超级管理员是否激活
                if (devicePolicyManager.isAdminActive(componentName)) {
                    //取消激活
                    //取消设置密码
                    devicePolicyManager.resetPassword("", 0);
                    devicePolicyManager.removeActiveAdmin(componentName);//设置取消激活超级管理员
                    //修改图片
                    iv_setup4_isactive.setImageResource(R.drawable.admin_inactivated);
                }else{
                    //激活超级管理员
                    //跳转到系统的激活超级管理员界面
                    //表示激活一个超级管理员
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    //标示要激活那个超级管理员
                    //mDeviceAdminSample : 超级管理员的标示
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
                    //设置描述信息
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"黑马手机卫士");
                    //跳转操作
                    //REQUEST_CODE_ENABLE_ADMIN
                    startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //判断一个请求吗是否一致
        if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
            //根据是否激活超级管理员设置相应的图片
            if (devicePolicyManager.isAdminActive(componentName)) {
                //设置为激活超级管理员的图标
                iv_setup4_isactive.setImageResource(R.drawable.admin_activated);
            }else{
                //设置取消激活超级管理员图标
                iv_setup4_isactive.setImageResource(R.drawable.admin_inactivated);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean next_activity() {

        //判断是否激活超级管理员
        if (!devicePolicyManager.isAdminActive(componentName)) {
            Toast.makeText(getApplicationContext(), "请激活超级管理员", Toast.LENGTH_SHORT).show();
            return true;
        }

        Intent intent = new Intent(this,SetUp5Activity.class);
        startActivity(intent);
        return false;
    }
    @Override
    public boolean pre_activity() {
        Intent intent = new Intent(this,SetUp3Activity.class);
        startActivity(intent);
        return false;
    }
}
