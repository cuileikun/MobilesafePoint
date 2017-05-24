package com.cuileikun.mobilesave.activity.lostfind;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cuileikun.mobilesave.R;
import com.cuileikun.mobilesave.utils.Contants;
import com.cuileikun.mobilesave.utils.SharedPreferencesUtil;

public class SetUp3Activity extends SetUpBaseActivity {
    private static final int SELECTCONTACTS = 10;
    private EditText et_setup3_safenumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up3);
        initView();
    }

    private void initView() {
        et_setup3_safenumber = (EditText) findViewById(R.id.et_setup3_safenumber);

        //回显安全号码操作
        String sp_safenumber = SharedPreferencesUtil.getString(this, Contants.SAFENUMBER, "");
        et_setup3_safenumber.setText(sp_safenumber);
        if (!TextUtils.isEmpty(sp_safenumber)) {
            //设置editttext光标的位置
            //index : 表示内容的最后一位的位置
            et_setup3_safenumber.setSelection(sp_safenumber.length());
        }
    }
    @Override
    public boolean next_activity() {
        //保存用户输入的安全号码
        //1.获取输入的安全号码
        String safenumber = et_setup3_safenumber.getText().toString().trim();
        //2.判断安全号码是否为空
        if (TextUtils.isEmpty(safenumber)) {
            Toast.makeText(this, "请输入安全号码...", Toast.LENGTH_SHORT).show();
            return true;
        }
        //3.保存安全号码
        SharedPreferencesUtil.saveString(this, Contants.SAFENUMBER, safenumber);


        Intent intent = new Intent(this,SetUp4Activity.class);
        startActivity(intent);
        return false;
    }

    @Override
    public boolean pre_activity() {
        Intent intent = new Intent(this,SetUp2Activity.class);
        startActivity(intent);
        return false;
    }
    /**
     *
     * Description:选择联系人按钮点击事件
     *
     * @author Administrator
     *
     * @date 2015-11-30
     *
     * @date 下午3:15:11
     */
    public void selectContacts(View v){
        //跳转到选择联系人界面
        Intent intent = new Intent(this,ContactsActivity.class);
        //startActivity(intent);
        startActivityForResult(intent, SELECTCONTACTS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data!=null) {
            //接受数据
            String number = data.getStringExtra("number");//空指针异常:1.null.方法     2.参数为null
            //设置给安全号码输入框
            et_setup3_safenumber.setText(number);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }





}
