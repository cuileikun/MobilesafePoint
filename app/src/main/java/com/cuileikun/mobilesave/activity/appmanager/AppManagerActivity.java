package com.cuileikun.mobilesave.activity.appmanager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.cuileikun.mobilesave.R;
import com.cuileikun.mobilesave.bean.AppInfo;
import com.cuileikun.mobilesave.utils.AppEngine;
import com.cuileikun.mobilesave.view.ProgressbarView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends Activity implements View.OnClickListener {
    private ProgressbarView pv_appmanager_memory;
    private ProgressbarView pv_appmanager_sd;
    private List<AppInfo> list;
    private List<AppInfo> userAppInfos;
    private List<AppInfo> systemAppInfos;
    private ListView lv_appmanager_applications;
    private LinearLayout ll_appmanager_loading;
    private TextView tv_appmanager_count;
    private AppInfo appInfo;
    private PopupWindow popupWindow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        initView();
        setMemory();
        fillData();
        setListViewOnScrollListener();
        setListViewOnItemClickListener();
    }

    // 初始化控件
    private void initView() {
        pv_appmanager_memory = (ProgressbarView) findViewById(R.id.pv_appmanager_memory);
        pv_appmanager_sd = (ProgressbarView) findViewById(R.id.pv_appmanager_sd);
        lv_appmanager_applications = (ListView) findViewById(R.id.lv_appmanager_applications);
        ll_appmanager_loading = (LinearLayout) findViewById(R.id.ll_appmanager_loading);
        tv_appmanager_count = (TextView) findViewById(R.id.tv_appmanager_count);
    }
    /**
     * Description:设置内存空间大小
     */
    private void setMemory() {
        // 获取内存空间大小
        // 获取内存的路径
        File dataDirectory = Environment.getDataDirectory();
        // 获取可用的内存空间
        long freeSpace = dataDirectory.getFreeSpace();
        // 获取总内存的空间
        long totalSpace = dataDirectory.getTotalSpace();
        // 获取已用的内存,获取的B
        long usedSpace = totalSpace - freeSpace;
        // 获取已用内存占用比例
        // + 0.5f 数学3.7 4 程序3.7 3 3.7+0.5 = 4.2 4
        int usedtotle = (int) (usedSpace * 100f / totalSpace + 0.5f);
        // 转化成MB
        String usedSize = Formatter.formatFileSize(AppManagerActivity.this,
                usedSpace);// number : 内存大小
        String freeSize = Formatter.formatFileSize(AppManagerActivity.this,
                freeSpace);// number : 内存大小
        pv_appmanager_memory.setTitle("内存:");
        pv_appmanager_memory.setUsed(usedSize + "已用");
        pv_appmanager_memory.setFree(freeSize + "可用");
        pv_appmanager_memory.setProgress(usedtotle);

        // 获取SD卡
        File externalStorageDirectory = Environment
                .getExternalStorageDirectory();
        long sdFreeSpace = externalStorageDirectory.getFreeSpace();
        long sdtotalSpace = externalStorageDirectory.getTotalSpace();
        long sdUsedSpace = sdtotalSpace - sdFreeSpace;
        int sdusedtotle = (int) (sdUsedSpace * 100f / sdtotalSpace + 0.5f);

        String sdusedSize = Formatter.formatFileSize(AppManagerActivity.this,
                sdUsedSpace);// number : 内存大小
        String sdfreeSize = Formatter.formatFileSize(AppManagerActivity.this,
                sdFreeSpace);// number : 内存大小
        pv_appmanager_sd.setTitle("SD:   ");
        pv_appmanager_sd.setUsed(sdusedSize + "已用");
        pv_appmanager_sd.setFree(sdfreeSize + "可用");
        pv_appmanager_sd.setProgress(sdusedtotle);

    }
    // 获取数据
    private void fillData() {
        new Thread() {
            public void run() {
                list = AppEngine.getAllApplication(AppManagerActivity.this);
                userAppInfos = new ArrayList<AppInfo>();
                systemAppInfos = new ArrayList<AppInfo>();
                for (AppInfo appinfo : list) {
                    if (appinfo.isSystem) {
                        // 系统程序
                        systemAppInfos.add(appinfo);
                    } else {
                        // 用户程序
                        userAppInfos.add(appinfo);
                    }
                }
                // 设置显示数据
                runOnUiThread(new Runnable() {
                    public void run() {

                        tv_appmanager_count.setText("用户程序("
                                + userAppInfos.size() + "个)");

                        lv_appmanager_applications.setAdapter(new Myadapter());
                        ll_appmanager_loading.setVisibility(View.GONE);// 数据显示完成,隐藏进度条
                    }
                });
            };
        }.start();
    }



    private class Myadapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return userAppInfos.size() + systemAppInfos.size() + 2;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (position == 0) {
                // 添加用户程序多少个
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setText("用户程序(" + userAppInfos.size() + "个)");
                textView.setTextSize(15);
                textView.setBackgroundColor(Color.parseColor("#D6D3CE"));
                textView.setTextColor(Color.BLACK);
                textView.setPadding(8, 8, 8, 8);
                return textView;
            } else if (position == userAppInfos.size() + 1) {
                // 添加系统程序多少个
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setText("系统程序(" + systemAppInfos.size() + "个)");
                textView.setTextSize(15);
                textView.setBackgroundColor(Color.parseColor("#D6D3CE"));
                textView.setTextColor(Color.BLACK);
                textView.setPadding(8, 8, 8, 8);
                return textView;
            }

            View view;
            ViewHolder viewHolder;
            if (convertView == null || convertView instanceof TextView) {
                view = View.inflate(AppManagerActivity.this,
                        R.layout.appmanager_item, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_appmanageritem_icon = (ImageView) view
                        .findViewById(R.id.iv_appmanageritem_icon);

                viewHolder.tv_appmanageritem_name = (TextView) view
                        .findViewById(R.id.tv_appmanageritem_name);
                viewHolder.tv_appmanageritem_issd = (TextView) view
                        .findViewById(R.id.tv_appmanageritem_issd);
                viewHolder.tv_appmanageritem_memory = (TextView) view
                        .findViewById(R.id.tv_appmanageritem_memory);

                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            // 获取相应的bean数据
            AppInfo appInfo;
            // 从用户程序集合和系统程序集合中获取
            if (position <= userAppInfos.size()) {
                appInfo = userAppInfos.get(position - 1);
            } else {
                appInfo = systemAppInfos
                        .get(position - userAppInfos.size() - 2);
            }

            viewHolder.iv_appmanageritem_icon.setImageDrawable(appInfo.icon);// null.方法,参数为null
            viewHolder.tv_appmanageritem_name.setText(appInfo.name);
            viewHolder.tv_appmanageritem_issd.setText(appInfo.isSD ? "SD卡"
                    : "手机内存");

            viewHolder.tv_appmanageritem_memory
                    .setText(Formatter.formatFileSize(AppManagerActivity.this,
                            appInfo.memorysize));

            return view;
        }

    }

    static class ViewHolder {
        ImageView iv_appmanageritem_icon;
        TextView tv_appmanageritem_name, tv_appmanageritem_issd,
                tv_appmanageritem_memory;
    }

    // listview滑动监听事件
    private void setListViewOnScrollListener() {
        lv_appmanager_applications.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub

            }

            // firstVisibleItem : 当前界面可见的条目
            // visibleItemCount : 当前界面可见条目的总数
            // totalItemCount : listview总共的条目个数
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                popuwindowHide();
                // 空指针的原因:在listview加载的时候就会调用onScroll
                if (userAppInfos != null && systemAppInfos != null) {
                    if (firstVisibleItem >= userAppInfos.size() + 1) {
                        tv_appmanager_count.setText("系统程序("
                                + systemAppInfos.size() + "个)");
                    } else {
                        tv_appmanager_count.setText("用户程序("
                                + userAppInfos.size() + "个)");
                    }
                }
            }
        });
    }

    // listview的条目点击事件
    private void setListViewOnItemClickListener() {
        lv_appmanager_applications
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        // 显示气泡
                        // 1.屏蔽textview操作
                        if (position == 0
                                || position == userAppInfos.size() + 1) {
                            return;
                        }
                        // 2.获取相应的数据
                        // 从用户程序集合和系统程序集合中获取
                        if (position <= userAppInfos.size()) {
                            appInfo = userAppInfos.get(position - 1);
                        } else {
                            appInfo = systemAppInfos.get(position
                                    - userAppInfos.size() - 2);
                        }

                        popuwindowHide();

                        // 3.显示气泡
                        View contentView = View.inflate(
                                AppManagerActivity.this,
                                R.layout.popuwindow_item, null);

                        // 初始化控件
                        contentView.findViewById(R.id.ll_pop_uninstall)
                                .setOnClickListener(AppManagerActivity.this);
                        contentView.findViewById(R.id.ll_pop_open)
                                .setOnClickListener(AppManagerActivity.this);
                        contentView.findViewById(R.id.ll_pop_share)
                                .setOnClickListener(AppManagerActivity.this);
                        contentView.findViewById(R.id.ll_pop_info)
                                .setOnClickListener(AppManagerActivity.this);

                        // contentView : 设置填充的veiw对象
                        // width : view对象宽度
                        // height : View对象的高度
                        popupWindow = new PopupWindow(contentView,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        popupWindow.setAnimationStyle(R.style.popuwindow);

						/*int[] location = new int[2];
						view.getLocationInWindow(location);
						int x = location[0];
						int y = location[1];*/

                        // 显示
                        // parent : 挂载哪里
                        // gravity, x, y : 设置popuwindow的位置
                        // popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP, x+60, y);//显示在布局那个位置
                        // popupWindow.showAsDropDown(view);//显示在listview条目的底部
                        popupWindow.showAsDropDown(view, 0 + 60,
                                -view.getHeight());// 设置显示在以listview条目为基准的那个位置
                    }

                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_pop_uninstall:
                uninstall();
                break;
            case R.id.ll_pop_open:
                open();
                break;
            case R.id.ll_pop_share:
                share();
                break;
            case R.id.ll_pop_info:
                info();
                break;
        }
        popuwindowHide();
    }
    // 隐藏popuwindow
    private void popuwindowHide() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }
    //分享  shareSDK
    private void share() {
        /**
         *  <intent-filter>
         <action android:name="android.intent.action.SEND" />
         <category android:name="android.intent.category.DEFAULT" />
         <data android:mimeType="text/plain" />
         </intent-filter>
         */
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "发现一个很牛X软件:"+appInfo.name+"  下载地址:www.baidu.com,自己去搜..");
        startActivity(intent);
    }

    //详细信息
    private void info() {
        //跳转到系统详情界面
        /**
         * act=android.settings.APPLICATION_DETAILS_SETTINGS   action
         cat=[android.intent.category.DEFAULT]   category
         dat=package:com.example.android.apis   data
         cmp=com.android.settings/.applications.InstalledAppDetails u=0  跳转的界面
         */
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:"+appInfo.packagename));
        startActivity(intent);
    }

    //打开操作
    private void open() {
        PackageManager pm = getPackageManager();
        //获取相应应用程序的启动意图
        Intent intent = pm.getLaunchIntentForPackage(appInfo.packagename);
        if (intent != null) {
            startActivity(intent);
        }else{
            Toast.makeText(getApplicationContext(), "系统核心程序,无法启动", Toast.LENGTH_SHORT).show();
        }
    }

    //卸载操作
    private void uninstall() {
        /**
         *  <intent-filter>
         <action android:name="android.intent.action.VIEW" />
         <action android:name="android.intent.action.DELETE" />
         <category android:name="android.intent.category.DEFAULT" />
         <data android:scheme="package" />
         </intent-filter>
         */
        if (!appInfo.packagename.equals(getPackageName())) {
            if (!appInfo.isSystem) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.DELETE");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:"+appInfo.packagename));
                startActivityForResult(intent, 0);
            }else{
                Toast.makeText(getApplicationContext(), "系统程序,要想卸载,请root先", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "文明社会,杜绝自杀", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fillData();
    }



}
