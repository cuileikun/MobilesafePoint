package com.cuileikun.mobilesave.activity.lostfind;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cuileikun.mobilesave.R;
import com.cuileikun.mobilesave.utils.Contants;
import com.cuileikun.mobilesave.utils.SharedPreferencesUtil;

public class LostFindActivity extends Activity {

    private TextView tv_lostfind_setup;
    private TextView tv_lostfind_safenumber;
    private ImageView iv_lostfind_protected;
    private RelativeLayout rel_lostfind_protected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_find);
        initView();
    }

    private void initView() {
        tv_lostfind_setup = (TextView) findViewById(R.id.tv_lostfind_setup);
        tv_lostfind_safenumber = (TextView) findViewById(R.id.tv_lostfind_safenumber);
        iv_lostfind_protected = (ImageView) findViewById(R.id.iv_lostfind_protected);
        rel_lostfind_protected = (RelativeLayout) findViewById(R.id.rel_lostfind_protected);

        //根据保存的安全号码和防盗保护是否开启的状态设置相应的显示效果
        tv_lostfind_safenumber.setText(SharedPreferencesUtil.getString(this, Contants.SAFENUMBER, ""));
        //获取保存的状态
        boolean b = SharedPreferencesUtil.getBoolean(this, Contants.PROTECTED, false);
        //根据保存的状态设置相应的图片
        if (b) {
            iv_lostfind_protected.setImageResource(R.drawable.lock);
        }else{
            iv_lostfind_protected.setImageResource(R.drawable.unlock);
        }


        rel_lostfind_protected.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //开启 -> 关闭   关闭 -> 开启
                //1.获取保存的防盗保护是否开启的状态
                boolean protecte = SharedPreferencesUtil.getBoolean(LostFindActivity.this, Contants.PROTECTED, false);
                if (protecte) {
                    //关闭操作
                    //重新保存状态
                    SharedPreferencesUtil.saveBoolean(LostFindActivity.this, Contants.PROTECTED, false);
                    //更改小锁的图标
                    iv_lostfind_protected.setImageResource(R.drawable.unlock);
                }else{
                    //开启操作
                    //重新保存状态
                    SharedPreferencesUtil.saveBoolean(LostFindActivity.this, Contants.PROTECTED, true);
                    //更改小锁的图标
                    iv_lostfind_protected.setImageResource(R.drawable.lock);
                }
            }
        });


        tv_lostfind_setup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //跳转到设置向导第一个界面
//                Intent intent = new Intent(LostFindActivity.this,SetUp1Activity.class);
//                startActivity(intent);
                finish();
            }
        });
    }
}
