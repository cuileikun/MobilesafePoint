package com.cuileikun.mobilesave.activity.callsmssafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cuileikun.mobilesave.R;
import com.cuileikun.mobilesave.db.dao.BlackNumberContant;
import com.cuileikun.mobilesave.db.dao.BlcakNumberDao;

public class BlackNumberAddAndEditActivity extends Activity {
    private EditText et_blacknumberaddandedit_phone;
    private RadioGroup rg_blacknumberaddandedit_radiogroup;
    private BlcakNumberDao blcakNumberDao;
    private String action;
    private Button btn_blacknumberaddandedit_ok;
    private TextView tv_blacknumaddandedit_title;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_number_add_and_edit);
        blcakNumberDao = new BlcakNumberDao(this);
        initView();
    }
    //初始化控件
    private void initView() {
        et_blacknumberaddandedit_phone = (EditText) findViewById(R.id.et_blacknumberaddandedit_phone);
        rg_blacknumberaddandedit_radiogroup = (RadioGroup) findViewById(R.id.rg_blacknumberaddandedit_radiogroup);
        btn_blacknumberaddandedit_ok = (Button) findViewById(R.id.btn_blacknumberaddandedit_ok);
        tv_blacknumaddandedit_title = (TextView) findViewById(R.id.tv_blacknumaddandedit_title);


        //获取CallSMSSafeActivity传递过来的数据
        //1.获取intent
        Intent intent = getIntent();
        //获取intent的动作
        action = intent.getAction();
        //2.判断是否是更新操作
        if ("update".equals(action)) {
            //更新操作
            String number = intent.getStringExtra("number");
            int mode = intent.getIntExtra("mode", -1);
            position = intent.getIntExtra("position", -1);

            //3.更改标题和button的文本
            tv_blacknumaddandedit_title.setText("更新黑名单");
            btn_blacknumberaddandedit_ok.setText("更新");

            //4.回显数据
            et_blacknumberaddandedit_phone.setText(number);
            //设置输入框不能进行编辑操作
            //设置输入框是否可用  true:可用    fasle:不可用
            et_blacknumberaddandedit_phone.setEnabled(false);
            //回显拦截模式,也就是单选按钮组
            int checkedId = -1;
            //根据传递过来的拦截模式,设置应该选中那个radiobutton
            switch (mode) {
                case BlackNumberContant.BLACKNUMBER_CALL:
                    //电话拦截
                    checkedId = R.id.rb_blacknumberaddandedit_call;
                    break;
                case BlackNumberContant.BLACKNUMBER_SMS:
                    //短信拦截
                    checkedId = R.id.rb_blacknumberaddandedit_sms;
                    break;
                case BlackNumberContant.BLACKNUMBER_ALL:
                    //全部拦截
                    checkedId = R.id.rb_blacknumberaddandedit_all;
                    break;
                default:
                    //严谨性判断
                    checkedId = R.id.rb_blacknumberaddandedit_call;
                    break;
            }
            //设置选中相应radiobutton
            rg_blacknumberaddandedit_radiogroup.check(checkedId);//设置选中的radiobutton
        }else{
            //添加操作
        }

    }
    //保存按钮的点击事件
    public void addORedit(View view){
        //将号码+拦截模式传递给黑名单管理界面
        //1.获取号码
        String number = et_blacknumberaddandedit_phone.getText().toString().trim();
        //2.判断号码是否为空
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(BlackNumberAddAndEditActivity.this, "号码不能为空", 0).show();
            return;
        }
        //3.获取拦截模式
        int mode = -1;
        //获取被选中的Radiobutton的id
        int checkedRadioButtonId = rg_blacknumberaddandedit_radiogroup.getCheckedRadioButtonId();
        //根据被选中的Radiobutton的id设置相应的拦截模式
        switch (checkedRadioButtonId) {
            case R.id.rb_blacknumberaddandedit_call:
                //电话拦截
                mode = BlackNumberContant.BLACKNUMBER_CALL;
                break;
            case R.id.rb_blacknumberaddandedit_sms:
                //短信拦截
                mode = BlackNumberContant.BLACKNUMBER_SMS;
                break;
            case R.id.rb_blacknumberaddandedit_all:
                //全部拦截
                mode = BlackNumberContant.BLACKNUMBER_ALL;
                break;
            default:
                Toast.makeText(getApplicationContext(), "请选择拦截模式", 0).show();
                return;
        }
        //判断是更新还是添加,更新做更新操作,添加做添加操作
        //判断传递过来的intent的动作是更新还是添加
        if ("update".equals(action)) {
            //更新
            //更新操作
            //1.更新数据库
            boolean isupdate = blcakNumberDao.updateBlackNumber(number, mode);
            //根据是否更新的状态设置是否传递数据
            if (isupdate) {
                //成功,传递数据
                Intent intent = new Intent();
                intent.putExtra("mode", mode);
                //将原来的被更新的条目位置在回传回去
                intent.putExtra("position", position);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }else{
                //失败
                Toast.makeText(getApplicationContext(), "系统繁忙,请稍候再试..", 0).show();
            }

        }else{
            //添加
            //4.添加操作
            //查询数据库,查看数据库中是否有号码,没有添加,有不添加
            //1.添加到数据库中
            boolean isadd = blcakNumberDao.addBlackNumber(number, mode);
            if (isadd) {
                //2.将号码+拦截模式回传给黑名单管理界面进行列表展示
                Intent intent = new Intent();
                intent.putExtra("number", number);
                intent.putExtra("mode", mode);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }else{
                Toast.makeText(getApplicationContext(), "系统繁忙,请稍候再试..", 0).show();
            }
        }
    }

    //取消按钮的点击事件
    public void cancle(View v){
        //消除界面
        finish();
    }






}
