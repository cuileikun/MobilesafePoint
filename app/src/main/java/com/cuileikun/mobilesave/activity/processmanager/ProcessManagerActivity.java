package com.cuileikun.mobilesave.activity.processmanager;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.cuileikun.mobilesave.R;
import com.cuileikun.mobilesave.bean.ProcessInfo;
import com.cuileikun.mobilesave.service.LockScreenService;
import com.cuileikun.mobilesave.utils.Contants;
import com.cuileikun.mobilesave.utils.ProcessEngine;
import com.cuileikun.mobilesave.utils.ProcessUtil;
import com.cuileikun.mobilesave.utils.Serviceutil;
import com.cuileikun.mobilesave.utils.SharedPreferencesUtil;
import com.cuileikun.mobilesave.view.ProgressbarView;
import com.cuileikun.mobilesave.view.SettingView;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ProcessManagerActivity extends Activity {
    private ProgressbarView pv_processsmanager_processcount;
    private ProgressbarView pv_processsmanager_mermory;
    private List<ProcessInfo> list;
    private StickyListHeadersListView lv_processsmanager_process;
    private LinearLayout ll_processsmanager_loading;
    private List<ProcessInfo> userprocessInfos;
    private List<ProcessInfo> systemprocessInfos;
    private ProcessInfo processInfo;
    private Myadapter myadapter;
    private int runningProcessCount;
    private int allProcessCount;
    private int freeProcessCount;
    private ImageView iv_processmanager_drawerarrowup1;
    private ImageView iv_processmanager_drawerarrowup2;
    private SlidingDrawer sd_processmanager_slidingdrawer;
    private SettingView sv_processmanager_isshowsystem;
    private SettingView sv_processmanager_lockscreenclear;
    //系统进程是否显示标示
    private boolean isShowSystem = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);
        initView();
        fillData();
        setListViewOnItemClickListener();
    }
    // listview条目点击事件
    private void setListViewOnItemClickListener() {
        lv_processsmanager_process
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        // 1.获取点击条目对应的数据
                        if (position <= userprocessInfos.size() - 1) {
                            processInfo = userprocessInfos.get(position);
                        } else {
                            processInfo = systemprocessInfos.get(position
                                    - userprocessInfos.size());
                        }
                        // 2.修改点击条目的checkbox的状态
                        if (processInfo.isChecked) {
                            processInfo.isChecked = false;
                        } else {
                            // 判断如果是当前应用,不可以选中
                            if (!processInfo.packagename
                                    .equals(getPackageName())) {
                                processInfo.isChecked = true;
                            }
                        }
                        // 3.更新界面
                        // myadapter.notifyDataSetChanged();
                        // 更新单个条目
                        ViewHolder viewHolder = (ViewHolder) view.getTag();
                        viewHolder.cb_processmanageritem_isselect
                                .setChecked(processInfo.isChecked);
                    }
                });
    }

    /*
     * 获取正在运行的进程信息
     */
    private void fillData() {
        new Thread() {

            public void run() {
                list = ProcessEngine
                        .getRunningProcess(ProcessManagerActivity.this);

                // 拆分用户进程和系统进程
                userprocessInfos = new ArrayList<ProcessInfo>();
                systemprocessInfos = new ArrayList<ProcessInfo>();
                for (ProcessInfo processInfo : list) {
                    if (processInfo.isSystem) {
                        systemprocessInfos.add(processInfo);
                    } else {
                        userprocessInfos.add(processInfo);
                    }
                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        myadapter = new Myadapter();
                        lv_processsmanager_process.setAdapter(myadapter);
                        // 数据显示完成,隐藏进度条
                        ll_processsmanager_loading.setVisibility(View.GONE);
                    }
                });
            };
        }.start();
    }
    // 全选点击事件
    public void all(View v) {
        // 将所有的应用进程的标示设置true
        // 用户进程
        for (ProcessInfo processInfo : userprocessInfos) {
            // 将用户程序的标示改为true,将当前的应用排除
            if (!processInfo.packagename.equals(getPackageName())) {
                processInfo.isChecked = true;
            }
        }
        //根据系统进程是否显示更改系统进程是否选择
        if (isShowSystem) {
            // 系统进程
            for (ProcessInfo processInfo : systemprocessInfos) {
                processInfo.isChecked = true;
            }
        }
        // 更新界面
        myadapter.notifyDataSetChanged();
    }

    // 反选点击事件
    public void cancel(View v) {
        // 如果是true改为false,如果是false改为true
        // 用户进程
        for (ProcessInfo processInfo : userprocessInfos) {
            // 将用户程序的标示改为true,将当前的应用排除
            if (!processInfo.packagename.equals(getPackageName())) {
                processInfo.isChecked = !processInfo.isChecked;
				/*
				 * if (processInfo.isChecked) { processInfo.isChecked = false;
				 * }else{ processInfo.isChecked = true; }
				 */
            }
        }
        //根据系统进程是否显示更改系统进程是否反选
        // 系统进程
        if (isShowSystem) {
            for (ProcessInfo processInfo : systemprocessInfos) {
                processInfo.isChecked = !processInfo.isChecked;
            }
        }
        // 更新界面
        myadapter.notifyDataSetChanged();
    }

    // 清理点击事件
    public void clear(View v) {
        // 清理进程
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        // 保存的杀死进程的信息
        List<ProcessInfo> deleteInfos = new ArrayList<ProcessInfo>();

        // 用户进程
        for (ProcessInfo processInfo : userprocessInfos) {
            // 选中才能被清理
            if (processInfo.isChecked) {
                // 清理
                // packageName : 进程的名称
                am.killBackgroundProcesses(processInfo.packagename);// 杀死后台进程和空进程,前台进程,服务进程,可见进程
                // 将清理掉的应用的bean信息从集合中删除
                deleteInfos.add(processInfo);
            }
        }
        //根据系统进程是否显示更改系统进程是否进行清理
        if (isShowSystem) {
            // 系统进程
            for (ProcessInfo processInfo : systemprocessInfos) {
                if (processInfo.isChecked) {
                    am.killBackgroundProcesses(processInfo.packagename);
                    deleteInfos.add(processInfo);
                }
            }
        }
        // 更新界面
        long memorysize = 0;
        for (ProcessInfo processInfo : deleteInfos) {
            if (processInfo.isSystem) {
                systemprocessInfos.remove(processInfo);
            } else {
                userprocessInfos.remove(processInfo);
            }
            memorysize += processInfo.memorysize;
        }
        // 展示toast
        String memSize = Formatter.formatFileSize(getApplicationContext(),
                memorysize);
        Toast.makeText(getApplicationContext(),
                "清理" + deleteInfos.size() + "个进程,释放" + memSize + "内存", Toast.LENGTH_SHORT)
                .show();

        // 更新进程个数以及内存信息
        // 进程计算,内存重新获取
        // 之前运行的进程数 - 杀死的进程数
        runningProcessCount = runningProcessCount - deleteInfos.size();
        // 重新计算进度条进度,以及显示操作
        // 获取进度条的进度
        int runallProcess = (int) (runningProcessCount * 100f / allProcessCount + 0.5f);
        // 设置显示
        pv_processsmanager_processcount.setTitle("进程数:");
        pv_processsmanager_processcount.setUsed("正在运行" + runningProcessCount
                + "个");
        pv_processsmanager_processcount.setFree("可有进程:" + freeProcessCount
                + "个");
        pv_processsmanager_processcount.setProgress(runallProcess);

        // 内存操作
        // 获取可用内存
        long freeMemory = ProcessUtil
                .getFreeMemory(ProcessManagerActivity.this);
        // 获取总内存
        long totalMemory = ProcessUtil
                .getTotalMemory(ProcessManagerActivity.this);
        // 获取占用的内存
        long useMemory = totalMemory - freeMemory;
        // 获取进度条进度
        int useTotalMemory = (int) (useMemory * 100f / totalMemory + 0.5f);
        // 显示数据
        pv_processsmanager_mermory.setTitle("内存:    ");

        String useSize = Formatter.formatFileSize(ProcessManagerActivity.this,
                useMemory);
        String freeSize = Formatter.formatFileSize(ProcessManagerActivity.this,
                freeMemory);

        pv_processsmanager_mermory.setUsed("占用内存:" + useSize);
        pv_processsmanager_mermory.setFree("可用内存:" + freeSize);
        pv_processsmanager_mermory.setProgress(useTotalMemory);


        myadapter.notifyDataSetChanged();
    }
    private void initView() {
        pv_processsmanager_processcount = (ProgressbarView) findViewById(R.id.pv_processsmanager_processcount);
        pv_processsmanager_mermory = (ProgressbarView) findViewById(R.id.pv_processsmanager_mermory);
        lv_processsmanager_process = (StickyListHeadersListView) findViewById(R.id.lv_processsmanager_process);
        ll_processsmanager_loading = (LinearLayout) findViewById(R.id.ll_processsmanager_loading);

        iv_processmanager_drawerarrowup1 = (ImageView) findViewById(R.id.iv_processmanager_drawerarrowup1);
        iv_processmanager_drawerarrowup2 = (ImageView) findViewById(R.id.iv_processmanager_drawerarrowup2);

        sd_processmanager_slidingdrawer = (SlidingDrawer) findViewById(R.id.sd_processmanager_slidingdrawer);

        sv_processmanager_isshowsystem = (SettingView) findViewById(R.id.sv_processmanager_isshowsystem);
        sv_processmanager_lockscreenclear = (SettingView) findViewById(R.id.sv_processmanager_lockscreenclear);
        setProcessMemory();

//实现动画操作
        openAnimation();

        //打开的时候停止动画,同时更改图片
        //当抽屉关闭的监听
        sd_processmanager_slidingdrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {

            @Override
            public void onDrawerClosed() {
                //开启动画
                openAnimation();
            }
        });
        //当抽屉打开的监听
        sd_processmanager_slidingdrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {

            @Override
            public void onDrawerOpened() {
                //停止动画
                closeAnimation();
            }
        });

        boolean b = SharedPreferencesUtil.getBoolean(ProcessManagerActivity.this, Contants.PROCESSISSHOWSYSTEM, true);
        sv_processmanager_isshowsystem.setToggle(b);

        //根据标示设置系统进程是否显示
        isShowSystem = b;

        //设置显示系统进程条目的点击事件
        sv_processmanager_isshowsystem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 打开 -> 关闭 关闭 -> 打开
                // 获取开关是打开还是关闭的状态
				/*
				 * if (sv_setting_update.getToggle()) { //关闭 //设置打开关闭开关
				 * sv_setting_update.setToggle(false); }else{ //打开 //设置打开关闭开关
				 * sv_setting_update.setToggle(true); }
				 */
                sv_processmanager_isshowsystem.toggle();

                //根据标示隐藏显示系统进程
                //获取标示的状态
                boolean toggle = sv_processmanager_isshowsystem.getToggle();
                isShowSystem = toggle;
                //更新界面
                myadapter.notifyDataSetChanged();

                // 保存按钮的打开关闭状态
                SharedPreferencesUtil.saveBoolean(ProcessManagerActivity.this,
                        Contants.PROCESSISSHOWSYSTEM, sv_processmanager_isshowsystem.getToggle());
            }
        });
        //锁屏清理进程点击事件
        sv_processmanager_lockscreenclear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProcessManagerActivity.this,
                        LockScreenService.class);
                // 开启服务 再次点击 -> 关闭服务 关闭服务 再次点击 -> 开启服务
                // 问题?:怎么知道服务是否开启
                // 获取服务是否开启
                if (Serviceutil
                        .isServiceRunning(getApplicationContext(),
                                "com.itheima.mobliesafesh04.service.LockScreenService")) {
                    // 开启,再次点击,关闭
                    stopService(intent);
                    // 改变图标
                    sv_processmanager_lockscreenclear.setToggle(false);
                } else {
                    // 关闭,再次点击,开启
                    startService(intent);
                    sv_processmanager_lockscreenclear.setToggle(true);
                }
            }
        });
    }

    private void setProcessMemory() {
        // 获取正在运行的进程个数
        runningProcessCount = ProcessUtil
                .getRunningProcessCount(ProcessManagerActivity.this);
        // 获取总进程的个数
        allProcessCount = ProcessUtil
                .getAllProcessCount(ProcessManagerActivity.this);
        // 获取空闲进程
        freeProcessCount = allProcessCount - runningProcessCount;
        // 获取进度条的进度
        int runallProcess = (int) (runningProcessCount * 100f / allProcessCount + 0.5f);
        // 设置显示
        pv_processsmanager_processcount.setTitle("进程数:");
        pv_processsmanager_processcount.setUsed("正在运行" + runningProcessCount
                + "个");
        pv_processsmanager_processcount.setFree("可有进程:" + freeProcessCount
                + "个");
        pv_processsmanager_processcount.setProgress(runallProcess);

        // 设置显示内存信息
        // 获取可用内存
        long freeMemory = ProcessUtil
                .getFreeMemory(ProcessManagerActivity.this);
        // 获取总内存
        long totalMemory = ProcessUtil
                .getTotalMemory(ProcessManagerActivity.this);
        // 获取占用的内存
        long useMemory = totalMemory - freeMemory;
        // 获取进度条进度
        int useTotalMemory = (int) (useMemory * 100f / totalMemory + 0.5f);
        // 显示数据
        pv_processsmanager_mermory.setTitle("内存:    ");

        String useSize = Formatter.formatFileSize(ProcessManagerActivity.this,
                useMemory);
        String freeSize = Formatter.formatFileSize(ProcessManagerActivity.this,
                freeMemory);

        pv_processsmanager_mermory.setUsed("占用内存:" + useSize);
        pv_processsmanager_mermory.setFree("可用内存:" + freeSize);
        pv_processsmanager_mermory.setProgress(useTotalMemory);
    }

    private class Myadapter extends BaseAdapter implements
            StickyListHeadersAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            //return userprocessInfos.size() + systemprocessInfos.size();
            return isShowSystem ? userprocessInfos.size() + systemprocessInfos.size() : userprocessInfos.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = View.inflate(ProcessManagerActivity.this,
                        R.layout.processmanager_item, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_processmanageritem_icon = (ImageView) view
                        .findViewById(R.id.iv_processmanageritem_icon);

                viewHolder.tv_processmanageritem_name = (TextView) view
                        .findViewById(R.id.tv_processmanageritem_name);
                viewHolder.tv_processmanageritem_memory = (TextView) view
                        .findViewById(R.id.tv_processmanageritem_memory);

                viewHolder.cb_processmanageritem_isselect = (CheckBox) view
                        .findViewById(R.id.cb_processmanageritem_isselect);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            // 设置显示数据
            // 获取数据
            ProcessInfo processInfo;
            if (position <= userprocessInfos.size() - 1) {
                processInfo = userprocessInfos.get(position);
            } else {
                processInfo = systemprocessInfos.get(position
                        - userprocessInfos.size());
            }

            viewHolder.iv_processmanageritem_icon
                    .setImageDrawable(processInfo.icon);
            viewHolder.tv_processmanageritem_name.setText(processInfo.name);

            long memorysize = processInfo.memorysize;
            // b -> MB
            String memSize = Formatter.formatFileSize(
                    ProcessManagerActivity.this, memorysize);

            viewHolder.tv_processmanageritem_memory.setText("内存占用:" + memSize);

            // 在listview中实时改变的操作,一般不会去复用缓存的,因为checkbox状态是要实时改变的,所以checkbox状态是不能跟着去复用缓存的
            // 解决:一般会把操作状态的标示存放bean类,根据bean类来进行改变checkbox的状态
            viewHolder.cb_processmanageritem_isselect
                    .setChecked(processInfo.isChecked);
            // 如果是当前应用程序,隐藏checkbox
            // 在adapter中有if必须有else,避免缓存复用
            if (processInfo.packagename.equals(getPackageName())) {
                // 隐藏
                viewHolder.cb_processmanageritem_isselect
                        .setVisibility(View.GONE);
            } else {
                // 显示
                viewHolder.cb_processmanageritem_isselect
                        .setVisibility(View.VISIBLE);
            }

            return view;
        }

        // 设置头部textview的样式
        @Override
        public View getHeaderView(int position, View convertView,
                                  ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                textView = new TextView(ProcessManagerActivity.this);
                textView.setTextSize(15);
                textView.setBackgroundColor(Color.parseColor("#D6D3CE"));
                textView.setTextColor(Color.BLACK);
                textView.setPadding(8, 8, 8, 8);
            } else {
                textView = (TextView) convertView;
            }

            // 设置显示用户进程和系统进程的个数
            // 获取数据
            ProcessInfo processInfo;
            if (position <= userprocessInfos.size() - 1) {
                processInfo = userprocessInfos.get(position);
            } else {
                processInfo = systemprocessInfos.get(position
                        - userprocessInfos.size());
            }
            // 根据进程是否是系统进程设置显示样式文本
            textView.setText(processInfo.isSystem ? "系统进程("
                    + systemprocessInfos.size() + "个)" : "用户进程("
                    + userprocessInfos.size() + "个)");

            return textView;
        }

        // 设置头部textview的位置
        @Override
        public long getHeaderId(int position) {
            // 获取数据
            ProcessInfo processInfo;
            if (position <= userprocessInfos.size() - 1) {
                processInfo = userprocessInfos.get(position);
            } else {
                processInfo = systemprocessInfos.get(position
                        - userprocessInfos.size());
            }
            return processInfo.isSystem ? 0 : 1;
        }

    }

    static class ViewHolder {
        ImageView iv_processmanageritem_icon;
        TextView tv_processmanageritem_name, tv_processmanageritem_memory;
        CheckBox cb_processmanageritem_isselect;
    }

    //执行动画
    private void openAnimation(){

        //将图片更改回来
        iv_processmanager_drawerarrowup1.setImageResource(R.drawable.drawer_arrow_up);
        iv_processmanager_drawerarrowup2.setImageResource(R.drawable.drawer_arrow_up);

        AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.2f, 1.0f);//表示半透明到不透明
        alphaAnimation1.setRepeatCount(Animation.INFINITE);//一直运行
        alphaAnimation1.setRepeatMode(Animation.REVERSE);//设置动画的模式
        alphaAnimation1.setDuration(300);
        iv_processmanager_drawerarrowup1.startAnimation(alphaAnimation1);

        AlphaAnimation alphaAnimation2 = new AlphaAnimation(1.0f,0.2f);//表示不透明到半透明
        alphaAnimation2.setRepeatCount(Animation.INFINITE);//一直运行
        alphaAnimation2.setRepeatMode(Animation.REVERSE);//设置动画的模式
        alphaAnimation2.setDuration(300);
        iv_processmanager_drawerarrowup2.startAnimation(alphaAnimation2);
    }
    //停止动画
    private void closeAnimation(){
        iv_processmanager_drawerarrowup1.clearAnimation();//清除动画
        iv_processmanager_drawerarrowup2.clearAnimation();

        //更改图片
        iv_processmanager_drawerarrowup1.setImageResource(R.drawable.drawer_arrow_down);
        iv_processmanager_drawerarrowup2.setImageResource(R.drawable.drawer_arrow_down);
    }



}
