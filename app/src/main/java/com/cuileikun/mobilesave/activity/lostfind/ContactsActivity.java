package com.cuileikun.mobilesave.activity.lostfind;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cuileikun.mobilesave.R;
import com.cuileikun.mobilesave.bean.ContactsInfo;
import com.cuileikun.mobilesave.utils.ContactsEngine;

import java.util.List;

public class ContactsActivity extends AppCompatActivity {
    private ListView lv_contact_contacts;
    private List<ContactsInfo> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        initView();
    }

    //初始化控件
    private void initView() {

        //获取系统所有的联系人
        list = ContactsEngine.getAllContacts(this);

        lv_contact_contacts = (ListView) findViewById(R.id.lv_contact_contacts);

        lv_contact_contacts.setAdapter(new Myadapter());

        //设置listview的条目点击事件
        lv_contact_contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //1.返回数据
                Intent data = new Intent();
                data.putExtra("number", list.get(position).number);
                //设置结果的方法,可以将结果返回给调用当前activity的activity
                setResult(Activity.RESULT_OK, data);
                //2.销毁界面
                finish();
            }
        });
    }
    private class Myadapter extends BaseAdapter {
        //获取条目的个数
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }
        //根据条目的位置获取相应的数据
        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }
        //获取条目的id
        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }
        //设置条目显示的样式
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //2.复用缓存
            View view = null;
            ViewHoler viewHoler = null;
            if (convertView == null) {
                view = View.inflate(ContactsActivity.this, R.layout.contacts_item, null);
                //3.创建一个存放控件的盒子
                viewHoler = new ViewHoler();
                //4.将控件放到盒子中
                viewHoler.iv_homeitem_icon = (ImageView) view.findViewById(R.id.iv_homeitem_icon);
                viewHoler.tv_homeitem_title = (TextView) view.findViewById(R.id.tv_homeitem_title);
                viewHoler.tv_homeitem_desc = (TextView) view.findViewById(R.id.tv_homeitem_desc);
                //5.将盒子放到view对象
                view.setTag(viewHoler);
            }else{
                view = convertView;//获取复用的view对象
                //6.获取和view对象绑定的存放控件的盒子
                viewHoler = (ViewHoler) view.getTag();
            }

            //设置显示数据
            //获取联系人的bean类
            ContactsInfo contactsInfo = list.get(position);
            viewHoler.tv_homeitem_title.setText(contactsInfo.name);
            viewHoler.tv_homeitem_desc.setText(contactsInfo.number);

            //根据联系人的id获取头像
            Bitmap bitmap = ContactsEngine.getContactsPhoto(getApplicationContext(), contactsInfo.contactid);
            //判断bitmap是否为空,不为空设置相应的头像,为空,设置默认头像
            if (bitmap != null) {
                viewHoler.iv_homeitem_icon.setImageBitmap(bitmap);
            }else{
                viewHoler.iv_homeitem_icon.setImageResource(R.drawable.ic_contact);
            }

            return view;
        }

    }

    //1.创建一个存放控件的盒子
    static class ViewHoler{
        ImageView iv_homeitem_icon;
        TextView tv_homeitem_title;
        TextView tv_homeitem_desc;
    }


}
