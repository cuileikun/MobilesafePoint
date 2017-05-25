package com.cuileikun.mobilesave.activity.trafficmanager;

import android.app.Activity;
import android.net.TrafficStats;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cuileikun.mobilesave.R;
import com.cuileikun.mobilesave.bean.AppInfo;
import com.cuileikun.mobilesave.utils.AppEngine;

import java.util.List;

public class TrafficManagerActivity extends Activity {
    private ListView lv_traffic_application;
    private LinearLayout ll_traffic_loading;
    private List<AppInfo> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_manager);
        initview();
    }

    private void initview() {
        lv_traffic_application = (ListView) findViewById(R.id.lv_traffic_application);
        ll_traffic_loading = (LinearLayout) findViewById(R.id.ll_traffic_loading);

        //获取展示系统中软件的流量(下载,上传)
		/*TrafficStats.getUidRxBytes(uid);//获取相应的软件的下载的流量
		TrafficStats.getUidTxBytes(uid);//获取相应的软件的上传的流量  uid:软件的uid*/
        new Thread(){

            public void run() {
                list = AppEngine.getAllApplication(TrafficManagerActivity.this);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //设置显示数据
                        lv_traffic_application.setAdapter(new Myadapter());
                        //隐藏进度条
                        ll_traffic_loading.setVisibility(View.GONE);
                    }
                });
            };
        }.start();

    }
    private class Myadapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
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

            View view = View.inflate(getApplicationContext(), R.layout.traffic_item, null);
            ImageView iv_trffic_icon = (ImageView) view.findViewById(R.id.iv_trffic_icon);

            TextView tv_traffic_title = (TextView) view.findViewById(R.id.tv_traffic_title);
            TextView tv_traffic_desc = (TextView) view.findViewById(R.id.tv_traffic_desc);

            TextView tv_traffic_desc1 = (TextView) view.findViewById(R.id.tv_traffic_desc1);

            //获取数据展示数据
            AppInfo appInfo = list.get(position);
            iv_trffic_icon.setImageDrawable(appInfo.icon);
            tv_traffic_title.setText(appInfo.name);

            //上传流量获取
            long uidTxBytes = TrafficStats.getUidTxBytes(appInfo.uid);
            String tsize = Formatter.formatFileSize(getApplicationContext(), uidTxBytes);
            tv_traffic_desc.setText("上传:"+tsize);

            //下载流量
            long uidRxBytes = TrafficStats.getUidRxBytes(appInfo.uid);
            String rsize = Formatter.formatFileSize(getApplicationContext(), uidRxBytes);
            tv_traffic_desc1.setText("下载:"+rsize);
            return view;
        }

    }


}
