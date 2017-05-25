package com.cuileikun.mobilesave.activity.commontools;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cuileikun.mobilesave.R;
import com.cuileikun.mobilesave.bean.AppInfo;
import com.cuileikun.mobilesave.db.dao.WatchDogDao;
import com.cuileikun.mobilesave.utils.AppEngine;

import java.util.ArrayList;
import java.util.List;

public class AppLockActivity extends Activity implements View.OnClickListener {
    private Button btn_applock_unlock;
    private Button btn_applock_lock;
    private LinearLayout ll_applock_unlock;
    private LinearLayout ll_applock_lock;
    private List<AppInfo> list;
    private List<AppInfo> locklist;
    private List<AppInfo> unlocklist;
    private ListView lv_applock_lock;
    private ListView lv_applock_unlock;
    private WatchDogDao watchDogDao;
    private Myadapter lockadapter;
    private Myadapter unlockadapter;
    private TextView tv_applock_lock;
    private TextView tv_applock_unlock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        initView();
    }
    // 初始化控件
    private void initView() {

        watchDogDao = new WatchDogDao(AppLockActivity.this);

        btn_applock_unlock = (Button) findViewById(R.id.btn_applock_unlock);
        btn_applock_lock = (Button) findViewById(R.id.btn_applock_lock);

        ll_applock_unlock = (LinearLayout) findViewById(R.id.ll_applock_unlock);
        ll_applock_lock = (LinearLayout) findViewById(R.id.ll_applock_lock);

        lv_applock_lock = (ListView) findViewById(R.id.lv_applock_lock);
        lv_applock_unlock = (ListView) findViewById(R.id.lv_applock_unlock);

        tv_applock_lock = (TextView) findViewById(R.id.tv_applock_lock);
        tv_applock_unlock = (TextView) findViewById(R.id.tv_applock_unlock);


        btn_applock_unlock.setOnClickListener(this);
        btn_applock_lock.setOnClickListener(this);

        // 获取系统中安装所有应用程序
        new Thread() {

            public void run() {
                list = AppEngine.getAllApplication(AppLockActivity.this);
                locklist = new ArrayList<AppInfo>();
                unlocklist = new ArrayList<AppInfo>();
                // 将list集合加锁的应用程序存放到加锁的集合中,将未加锁的应用程序存放到未加锁的集合
                // 查询数据库,查看数据库中是否有包名,有表示加锁,保存到加锁的集合,没有表示未加锁,保存到未加锁的集合中
                for (AppInfo appinfo : list) {
                    if (watchDogDao.queryLockAPP(appinfo.packagename)) {
                        // 加锁
                        locklist.add(appinfo);
                    } else {
                        // 未加锁
                        unlocklist.add(appinfo);
                    }
                }

                // 设置显示列表数据
                runOnUiThread(new Runnable() {

                    public void run() {
                        // 给listview设置adapter

                        lockadapter = new Myadapter(true);
                        lv_applock_lock.setAdapter(lockadapter);

                        unlockadapter = new Myadapter(false);
                        lv_applock_unlock.setAdapter(unlockadapter);

                    }
                });
            };
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_applock_unlock:
                // 未加锁
                // 更改图片的背景
//                btn_applock_unlock
//                        .setBackgroundResource(R.drawable.dg_btn_confirm_normal);
                btn_applock_unlock.setBackgroundColor(Color.RED);
                btn_applock_lock.setBackgroundColor(Color.WHITE);
//                btn_applock_lock
//                        .setBackgroundResource(R.drawable.dg_button_cancel_normal);
                // 更改字体颜色
                btn_applock_unlock.setTextColor(Color.parseColor("#ffffff"));
                btn_applock_lock.setTextColor(Color.parseColor("#429ED6"));

                // 更改列表界面的展示
                ll_applock_unlock.setVisibility(View.VISIBLE);
                ll_applock_lock.setVisibility(View.GONE);
                break;
            case R.id.btn_applock_lock:
                // 加锁
                // 更改图片的背景
                btn_applock_unlock.setBackgroundColor(Color.WHITE);
                btn_applock_lock.setBackgroundColor(Color.RED);

                // 更改字体颜色
                btn_applock_unlock.setTextColor(Color.parseColor("#429ED6"));
                btn_applock_lock.setTextColor(Color.parseColor("#ffffff"));

                // 更改列表界面的展示
                ll_applock_lock.setVisibility(View.VISIBLE);
                ll_applock_unlock.setVisibility(View.GONE);
                break;
        }
    }

    private class Myadapter extends BaseAdapter {
        // 判断是加锁列表还是未加锁列表
        // true : 加锁列表 false:未加锁的列表
        private boolean islock;
        private TranslateAnimation right;
        private TranslateAnimation left;

        public Myadapter(boolean islock) {
            this.islock = islock;

            // 创建动画
            // Animation.ABSOLUTE : 绝对位置,没有定位坐标
            // Animation.RELATIVE_TO_SELF : 相对于自身来说
            // Animation.RELATIVE_TO_PARENT : 相对于父控件来说
            // romXType, fromXValue, toXType, toXValue x轴移动效果
            // fromYType, fromYValue, toYType, toYValue y轴移动效果

            right = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                    1, Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0);
            right.setDuration(500);

            left = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                    -1, Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0);
            left.setDuration(500);
        }

        @Override
        public int getCount() {
            showCount();
            // 如果是加锁的列表,加载locklist长度,如果是未加锁的列表,加载unlocklist长度
            if (islock) {
                return locklist.size();
            } else {
                return unlocklist.size();
            }
        }

        @Override
        public Object getItem(int position) {
            if (islock) {
                return locklist.get(position);
            } else {
                return unlocklist.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = View.inflate(getApplicationContext(),
                        R.layout.applock_item, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_applockitem_icon = (ImageView) view
                        .findViewById(R.id.iv_applockitem_icon);
                viewHolder.iv_applockitem_islock = (ImageView) view
                        .findViewById(R.id.iv_applockitem_islock);
                viewHolder.tv_applockitem_name = (TextView) view
                        .findViewById(R.id.tv_applockitem_name);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            final AppInfo appInfo = (AppInfo) getItem(position);
            viewHolder.iv_applockitem_icon.setImageDrawable(appInfo.icon);
            viewHolder.tv_applockitem_name.setText(appInfo.name);

            // 加锁列表显示解锁图标,解锁列表显示加锁图标
            if (islock) {
                viewHolder.iv_applockitem_islock
                        .setBackgroundResource(R.drawable.applock_unlock_selector);
            } else {
                viewHolder.iv_applockitem_islock
                        .setBackgroundResource(R.drawable.applock_lock_selector);
            }

            viewHolder.iv_applockitem_islock
                    .setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            //判断应用是否是当前应用
                            if (appInfo.packagename.equals(getPackageName())) {
                                Toast.makeText(getApplicationContext(), "自己不能给自己加锁", Toast.LENGTH_SHORT).show();
                            }else{
                                // 区分是加锁还是解锁
                                if (islock) {
                                    view.startAnimation(left);
                                    left.setAnimationListener(new Animation.AnimationListener() {
                                        //动画开始的时候调用
                                        @Override
                                        public void onAnimationStart(Animation animation) {
                                            // TODO Auto-generated method stub

                                        }
                                        //动画的执行次数
                                        @Override
                                        public void onAnimationRepeat(Animation animation) {
                                            // TODO Auto-generated method stub

                                        }
                                        //动画结束调用
                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            // TODO Auto-generated method stub
                                            // 解锁操作
                                            // 1.从数据库中数据删除
                                            // 2.从加锁的集合中将数据删除
                                            // 3.添加未加锁的集合中
                                            // 4.更新界面,两个界面
                                            watchDogDao.deleteLockApp(appInfo.packagename);
                                            locklist.remove(appInfo);
                                            unlocklist.add(appInfo);
                                            lockadapter.notifyDataSetChanged();
                                            unlockadapter.notifyDataSetChanged();
                                        }
                                    });
                                } else {
                                    view.startAnimation(right);
                                    right.setAnimationListener(new Animation.AnimationListener() {
                                        //动画开始的时候调用
                                        @Override
                                        public void onAnimationStart(Animation animation) {
                                            // TODO Auto-generated method stub

                                        }
                                        //动画的执行次数
                                        @Override
                                        public void onAnimationRepeat(Animation animation) {
                                            // TODO Auto-generated method stub

                                        }
                                        //动画结束调用
                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            // TODO Auto-generated method stub
                                            // 加锁
                                            // 1.数据库添加数据
                                            // 2.未加锁的集合中删除数据
                                            // 3.添加到加锁的集合中
                                            // 4.更新界面,两个界面
                                            watchDogDao.addLockAPP(appInfo.packagename);
                                            unlocklist.remove(appInfo);
                                            locklist.add(appInfo);
                                            unlockadapter.notifyDataSetChanged();
                                            lockadapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        }
                    });

            return view;
        }

    }

    static class ViewHolder {
        ImageView iv_applockitem_icon, iv_applockitem_islock;
        TextView tv_applockitem_name;
    }
    //设置显示加锁和未加锁个数
    public void showCount(){
        tv_applock_lock.setText("已加锁("+locklist.size()+")");
        tv_applock_unlock.setText("未加锁("+unlocklist.size()+")");
    }





}
