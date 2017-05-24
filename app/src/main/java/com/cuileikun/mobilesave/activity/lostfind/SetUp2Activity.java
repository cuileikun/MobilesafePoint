package com.cuileikun.mobilesave.activity.lostfind;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cuileikun.mobilesave.R;
import com.cuileikun.mobilesave.utils.Contants;
import com.cuileikun.mobilesave.utils.SharedPreferencesUtil;

public class SetUp2Activity extends SetUpBaseActivity {
    private RelativeLayout rel_setup2_sim;
    private ImageView iv_setup2_islock;
    private TelephonyManager telephonyManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up2);
        initView();
    }

    private void initView() {
        rel_setup2_sim = (RelativeLayout) findViewById(R.id.rel_setup2_sim);
        iv_setup2_islock = (ImageView) findViewById(R.id.iv_setup2_islock);

        //回显操作
        String sp_sim = SharedPreferencesUtil.getString(SetUp2Activity.this, Contants.SIM, "");
        if (TextUtils.isEmpty(sp_sim)) {
            iv_setup2_islock.setImageResource(R.drawable.unlock);
        }else{
            iv_setup2_islock.setImageResource(R.drawable.lock);
        }
        //电话管理者
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        rel_setup2_sim.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //1.获取保存的SIM卡,有的话显示加锁的图标,没有的话显示解锁的图片
                String sp_sim = SharedPreferencesUtil.getString(SetUp2Activity.this, Contants.SIM, "");
                //2.判断是否为空
                if (TextUtils.isEmpty(sp_sim)) {//null   ""
                    //没有保存,再次点击保存,图标改为加锁
                    //绑定
                    //保存SIM卡
                    //1.获取SIM卡
                    //telephonyManager.getLine1Number();//获取SIM卡绑定的手机号码,注意:在中国不太好用,运营商一般不会将sim卡和手机号绑定
                    String sim = telephonyManager.getSimSerialNumber();//获取SIM卡序列号.SIM卡的唯一标示
                    //2.保存SIM卡
                    SharedPreferencesUtil.saveString(SetUp2Activity.this, Contants.SIM, sim);

                    iv_setup2_islock.setImageResource(R.drawable.lock);
                }else{
                    //保存,表示绑定SIM卡,再次点击解绑操作,图标改为解锁
                    //解绑
                    SharedPreferencesUtil.saveString(SetUp2Activity.this, Contants.SIM, "");
                    iv_setup2_islock.setImageResource(R.drawable.unlock);
                }
            }
        });
    }

    @Override
    public boolean next_activity() {
        //判断用户是否绑定SIM卡
        String sim = SharedPreferencesUtil.getString(this, Contants.SIM, "");
        if (TextUtils.isEmpty(sim)) {
            Toast.makeText(this, "请先绑定SIM卡...", Toast.LENGTH_SHORT).show();
            return true;
        }
        Intent intent = new Intent(this,SetUp3Activity.class);
        startActivity(intent);
        return false;
    }
    @Override
    public boolean pre_activity() {
        Intent intent = new Intent(this,SetUp1Activity.class);
        startActivity(intent);
        return false;
    }



}
