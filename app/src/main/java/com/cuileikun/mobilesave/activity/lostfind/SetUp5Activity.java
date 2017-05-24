package com.cuileikun.mobilesave.activity.lostfind;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.cuileikun.mobilesave.R;
import com.cuileikun.mobilesave.utils.Contants;
import com.cuileikun.mobilesave.utils.SharedPreferencesUtil;

public class SetUp5Activity extends SetUpBaseActivity {
    private CheckBox cb_setup5_protected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up5);
        initView();
    }
    //初始化控件
    private void initView() {
        cb_setup5_protected = (CheckBox) findViewById(R.id.cb_setup5_protected);
        //回显操作
        boolean sp_protected = SharedPreferencesUtil.getBoolean(this, Contants.PROTECTED, false);
        //设置checkbox的状态
        cb_setup5_protected.setChecked(sp_protected);

        //监听checkbox选中状态操作
        cb_setup5_protected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            //当checkbox操作后调用
            //buttonView : checkbox
            //isChecked : 选中的状态
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //保存checkbox的状态
                SharedPreferencesUtil.saveBoolean(SetUp5Activity.this, Contants.PROTECTED, isChecked);
            }
        });
    }

    @Override
    public boolean next_activity() {
        //判断用户是否开启防盗保护
        //isChecked : 获取checkbox的状态
        if (!cb_setup5_protected.isChecked()) {
            Toast.makeText(getApplicationContext(), "请先开启防盗保护", Toast.LENGTH_SHORT).show();
            return true;
        }


        // 1.保存用户是否是第一次进入的状态
        SharedPreferencesUtil.saveBoolean(this, Contants.ISFIRSTENTER, true);

        // 2.跳转到手机防盗界面
        Intent intent = new Intent(this, LostFindActivity.class);
        startActivity(intent);
        return false;
    }

    @Override
    public boolean pre_activity() {
        Intent intent = new Intent(this, SetUp4Activity.class);
        startActivity(intent);
        return false;
    }
}
