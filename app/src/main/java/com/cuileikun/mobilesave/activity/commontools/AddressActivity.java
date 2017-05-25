package com.cuileikun.mobilesave.activity.commontools;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cuileikun.mobilesave.R;
import com.cuileikun.mobilesave.db.dao.AddressDao;

public class AddressActivity extends Activity {
    private EditText et_address_phone;
    private TextView tv_address_phoneaddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        initView();
    }
    //初始化控件
    private void initView() {
        et_address_phone = (EditText) findViewById(R.id.et_address_phone);
        tv_address_phoneaddress = (TextView) findViewById(R.id.tv_address_phoneaddress);

        //当EditText文本变化的监听
        et_address_phone.addTextChangedListener(new TextWatcher() {
            //当文本内容变化的时候调用
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String number = s.toString();
                String address = AddressDao.getAddress(AddressActivity.this, number);
                if (!TextUtils.isEmpty(address)) {
                    tv_address_phoneaddress.setText("归属地:"+address);
                }
            }
            //变化之前调用
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }
            //变化之后调用
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
    }
    //查询按钮的点击事件
    public void address(View v){
        //查询显示号码归属地操作
        //1.获取出输入框中输入的号码
        String number = et_address_phone.getText().toString().trim();
        //2.判断号码是否为空
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(getApplicationContext(), "请输入要查询的号码", Toast.LENGTH_SHORT).show();
            //执行抖动动画
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			/* shake.setInterpolator(new Interpolator() {

				@Override
				public float getInterpolation(float x) {
					return 0;//根据x的值获取y的值
				}
			});*/
            et_address_phone.startAnimation(shake);
            //振动
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            //设置振动的时间
            //milliseconds : 振动的时间
            vibrator.vibrate(100);//国产定制手机,执行默认的振动的时间  比如小米,单位毫秒
            return;
        }
        //3.查询号码归属地
        String address = AddressDao.getAddress(this, number);
        if (!TextUtils.isEmpty(address)) {
            //4.设置显示号码归属地
            tv_address_phoneaddress.setText("归属地:"+address);
        }
    }


}
