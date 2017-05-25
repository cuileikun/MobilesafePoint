package com.cuileikun.mobilesave.activity.commontools;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.cuileikun.mobilesave.R;
import com.cuileikun.mobilesave.db.dao.CommonNumDao;

import java.util.List;

public class CommonNumberActivity extends Activity {
    private ExpandableListView elv_commonnumber_commonnumbers;
    private List<CommonNumDao.GroupInfo> groups;
    private int currentExpandGroup = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_number);
        initView();
    }

    // 初始化控件
    private void initView() {
        elv_commonnumber_commonnumbers = (ExpandableListView) findViewById(R.id.elv_commonnumber_commonnumbers);

        // 获取所有的数据
        new Thread() {

            public void run() {
                groups = CommonNumDao.getGroups(CommonNumberActivity.this);

                // 设置显示数据
                runOnUiThread(new Runnable() {
                    public void run() {
                        // 设置显示数据
                        elv_commonnumber_commonnumbers.setAdapter(new Myadapter());
                    }
                });
            };

        }.start();
        //组的点击事件
        elv_commonnumber_commonnumbers.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            //v : 条目的view对象
            //groupPosition :条目的位置
            //id : 条目的id
            //return : true:表示执行完成
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                //打开关闭条目,打开条目的时候关闭其他条目,同时让当前打开条目置顶
                if (currentExpandGroup == -1) {
                    //打开的自己
                    elv_commonnumber_commonnumbers.expandGroup(groupPosition);//打开点击的组条目
                    currentExpandGroup = groupPosition;
                    elv_commonnumber_commonnumbers.setSelectedGroup(groupPosition);
                }else{
                    //关闭组,打开其他组
                    //1.打开的是自己,又点击了自己,关闭自己
                    //2.打开的是自己,又点击其他组,关闭自己,打开其他组,通将其他组置顶
                    if (currentExpandGroup == groupPosition) {
                        //关闭自己
                        elv_commonnumber_commonnumbers.collapseGroup(groupPosition);
                        currentExpandGroup = -1;
                    }else{
                        //关闭之前打开的组,打开点击的组
                        elv_commonnumber_commonnumbers.collapseGroup(currentExpandGroup);
                        //打开点击的组
                        elv_commonnumber_commonnumbers.expandGroup(groupPosition);

                        elv_commonnumber_commonnumbers.setSelectedGroup(groupPosition);
                        currentExpandGroup = groupPosition;
                    }
                }

                return true;
            }
        });
        //孩子的点击事件
        elv_commonnumber_commonnumbers.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            //childPosition : 孩子的位置
            //id : 孩子的id
            //return : 表示执行完成
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                //获取孩子的电话
                String number = groups.get(groupPosition).child.get(childPosition).number;
                //打电话
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+number));
                startActivity(intent);
                return true;
            }
        });
		/*//关闭组的监听
		elv_commonnumber_commonnumbers.setOnGroupCollapseListener(onGroupCollapseListener)
		//展开组的监听
		elv_commonnumber_commonnumbers.setOnGroupExpandListener(onGroupExpandListener)*/
    }

    private class Myadapter extends BaseExpandableListAdapter {
        // 设置组的个数
        @Override
        public int getGroupCount() {
            return groups.size();
        }

        // 设置孩子的个数
        @Override
        public int getChildrenCount(int groupPosition) {
            // TODO Auto-generated method stub
            return groups.get(groupPosition).child.size();
        }

        // 根据组的位置获取的组的数据
        @Override
        public Object getGroup(int groupPosition) {
            // TODO Auto-generated method stub
            return groups.get(groupPosition);
        }

        // 根据组的位置和孩子的位置获取孩子的数据
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return groups.get(groupPosition).child.get(childPosition);
        }

        // 获取组的id
        @Override
        public long getGroupId(int groupPosition) {
            // TODO Auto-generated method stub
            return groupPosition;
        }

        // 获取孩子的id
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return childPosition;
        }

        // 判断id是否稳定,如果你返回id,返回false,没有返回id,返回true
        @Override
        public boolean hasStableIds() {
            // TODO Auto-generated method stub
            return false;
        }

        // 设置组的样式
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            TextView textView = new TextView(CommonNumberActivity.this);
            textView.setText(groups.get(groupPosition).name);
            textView.setTextSize(20);
            textView.setTextColor(Color.BLACK);
            textView.setBackgroundColor(Color.parseColor("#33000000"));
            textView.setPadding(8, 8, 8, 8);
            return textView;
        }

        // 设置孩子的样式
        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            TextView textView = new TextView(CommonNumberActivity.this);
            textView.setText(groups.get(groupPosition).child.get(childPosition).name
                    + "\n"
                    + groups.get(groupPosition).child.get(childPosition).number);
            textView.setTextSize(18);
            textView.setTextColor(Color.BLACK);
            textView.setPadding(8, 8, 8, 8);
            return textView;
        }

        // 设置孩子是否可以点击,false:表示不可点击,true:表示可以点击
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return true;
        }

    }



}
