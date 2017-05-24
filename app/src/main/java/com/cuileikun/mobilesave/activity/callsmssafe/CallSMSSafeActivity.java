package com.cuileikun.mobilesave.activity.callsmssafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cuileikun.mobilesave.R;
import com.cuileikun.mobilesave.bean.BlackNumberInfo;
import com.cuileikun.mobilesave.db.dao.BlackNumberContant;
import com.cuileikun.mobilesave.db.dao.BlcakNumberDao;

import java.util.List;

public class CallSMSSafeActivity extends Activity {
    protected static final int REQUEST_CODE_ADD = 100;
    protected static final int REQUEST_CODE_UPDATE = 101;
    private ListView lv_callsmssafe_blacknumbers;
    private ImageView iv_callsmsafe_empty;
    private BlcakNumberDao blcakNumberDao;
    private List<BlackNumberInfo> list;
    private ImageView iv_callsmssafe_add;
    private Myadapter myadapter;
    private LinearLayout ll_callsmssafe_loading;

    //查询多少条数据
    private final int MAX_NUM=10;
    private int startIndex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_smssafe);
        initView();
    }
    //初始化控件
    private void initView() {
//获取全部数据,耗时操作,放到子线程中执行
        blcakNumberDao = new BlcakNumberDao(this);

        lv_callsmssafe_blacknumbers = (ListView) findViewById(R.id.lv_callsmssafe_blacknumbers);
        iv_callsmsafe_empty = (ImageView) findViewById(R.id.iv_callsmsafe_empty);
        iv_callsmssafe_add = (ImageView) findViewById(R.id.iv_callsmssafe_add);
        ll_callsmssafe_loading = (LinearLayout) findViewById(R.id.ll_callsmssafe_loading);


        filldata();
        //添加黑名单按钮点击事件
        iv_callsmssafe_add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //跳转到添加黑名单界面
                Intent intent = new Intent(CallSMSSafeActivity.this,BlackNumberAddAndEditActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD);
            }
        });
        //listview的条目点击事件
        lv_callsmssafe_blacknumbers.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //跳转到更新界面,要把点击条目的号码+拦截模式传递给更新界面展示
                Intent intent = new Intent(CallSMSSafeActivity.this,BlackNumberAddAndEditActivity.class);
                intent.setAction("update");//设置intent表示的动作
                intent.putExtra("position", position);
                intent.putExtra("number", list.get(position).number);
                intent.putExtra("mode", list.get(position).mode);
                startActivityForResult(intent, REQUEST_CODE_UPDATE);
            }
        });

        //当listview滑动到底部并且底部的最后一条可见条目是我们查询的list集合中最后一条数据,加载下一波数据,否则让用户执行滑动listview操作
        //监听listview滑动状态的操作
        lv_callsmssafe_blacknumbers.setOnScrollListener(new AbsListView.OnScrollListener() {
            //当listview滚动状态改变的时候调用
            //scrollState : listview的状态
            //SCROLL_STATE_FLING : 惯性滑动的状态
            //SCROLL_STATE_IDLE : 空闲状态
            //SCROLL_STATE_TOUCH_SCROLL : 缓慢滑动的状态
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //当listview是空闲状态的时候判断组合一条可见条目是否是list集合中的最后一条数据,是加载数据,不是不管了
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    //获取界面可见的最后一个条目
                    int position = lv_callsmssafe_blacknumbers.getLastVisiblePosition();
                    //判断可见条目是否是list集合中的最后一个条目
                    if (position == list.size()-1) {
                        //更改查询的起始位置
                        startIndex+=MAX_NUM;
                        //加载下一波数据
                        filldata();
                    }
                }
            }
            //当listview滚动的时候调用
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub

            }
        });

    }

    /**
     * Description:加载数据
     */
    private void filldata() {
        new Thread(){
            public void run() {
                //list = blcakNumberDao.queryAllBlackNumber();
                if (list == null) {
                    list = blcakNumberDao.queryPartBlackNumber(MAX_NUM, startIndex);
                }else{
                    //addAll : 将一个集合整合到另一个集合
                    //a(1,2,3)   b(4,5,6)  a.addAll(b)   a(1,2,3,4,5,6)
                    list.addAll(blcakNumberDao.queryPartBlackNumber(MAX_NUM, startIndex));
                }
                //获取完数据,设置显示数据
                runOnUiThread(new Runnable() {//其实是运行在主线程的,内存封装了handler
                    public void run() {
                        if (myadapter == null) {
                            myadapter = new Myadapter();
                            lv_callsmssafe_blacknumbers.setAdapter(myadapter);//设置显示数据界面
                        }else{
                            //更新操作
                            myadapter.notifyDataSetChanged();//更新界面
                        }
                        //当listview没有数据展示的时候,要先的view控件,要是在布局文件中的view控件才行
                        lv_callsmssafe_blacknumbers.setEmptyView(iv_callsmsafe_empty);
                        //数据显示完成之后,隐藏进度条
                        ll_callsmssafe_loading.setVisibility(View.GONE);
                    }
                });
            };
        }.start();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //initView();
        if (requestCode == REQUEST_CODE_ADD) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    //表示数据传递成功
                    if (data != null) {
                        //获取数据
                        String number = data.getStringExtra("number");
                        int mode = data.getIntExtra("mode", -1);
                        //添加到列表中显示,其实就是添加集合中,刷新列表
                        BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                        blackNumberInfo.number = number;
                        blackNumberInfo.mode = mode;
                        list.add(blackNumberInfo);
                        //list.add(0, blackNumberInfo);//0:list集合的索引
                        //刷新列表
                        myadapter.notifyDataSetChanged();//刷新列表
                    }
                    break;

                default:
                    break;
            }
        }else if(requestCode == REQUEST_CODE_UPDATE){
            switch (resultCode) {
                case Activity.RESULT_OK:
                    //获取传递的数据
                    if (data != null) {
                        int mode = data.getIntExtra("mode", -1);
                        //获取被更新的条目的位置
                        int position = data.getIntExtra("position", -1);
                        //更新相应的条目数据了
                        //取出更新的条目,将更新的条目的拦截模式跟换成传递过来的新的拦截模式
                        //问题?我们怎么取到条目
                        list.get(position).mode = mode;
                        //更新界面
                        myadapter.notifyDataSetChanged();
                    }
                    break;

                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        public View getView(final int position, View convertView, ViewGroup parent) {
			/*TextView textView = new TextView(CallSMSSafeActivity.this);
			textView.setText("11111");*/
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = View.inflate(getApplicationContext(), R.layout.callsmssafe_item, null);
                //将findviewbyid放到盒子中
                viewHolder = new ViewHolder();
                viewHolder.tv_callsmssafe_number = (TextView) view.findViewById(R.id.tv_callsmssafe_number);
                viewHolder.tv_callsmssafe_mode = (TextView) view.findViewById(R.id.tv_callsmssafe_mode);
                viewHolder.iv_callsmssafe_delete = (ImageView) view.findViewById(R.id.iv_callsmssafe_delete);
                //将盒子绑定view对象
                view.setTag(viewHolder);
            }else{
                view = convertView;
                //从复用的view对象中将盒子取出来
                viewHolder = (ViewHolder) view.getTag();
            }
            //使用盒子中的控件了
            //获取对应的数据
            final BlackNumberInfo blackNumberInfo = list.get(position);
            viewHolder.tv_callsmssafe_number.setText(blackNumberInfo.number);
            //获取出拦截模式,判断拦截模式设置显示相应的文字
            int mode = blackNumberInfo.mode;
            switch (mode) {
                case BlackNumberContant.BLACKNUMBER_CALL:
                    //电话拦截
                    viewHolder.tv_callsmssafe_mode.setText("电话拦截");
                    break;
                case BlackNumberContant.BLACKNUMBER_SMS:
                    //短信拦截
                    viewHolder.tv_callsmssafe_mode.setText("短信拦截");
                    break;
                case BlackNumberContant.BLACKNUMBER_ALL:
                    //全部拦截
                    viewHolder.tv_callsmssafe_mode.setText("全部拦截");
                    break;
            }
            //设置删除图片的点击事件
            viewHolder.iv_callsmssafe_delete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //删除条目的操作
                    //弹出对话框提醒用户是否删除
                    AlertDialog.Builder builder = new AlertDialog.Builder(CallSMSSafeActivity.this);
                    builder.setMessage("您是否删除黑名单:"+blackNumberInfo.number);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //设置删除数据
                            //1.删除数据库中的数据
                            boolean isdelete = blcakNumberDao.deleteBlackNumber(blackNumberInfo.number);
                            //2.根据是否删除成功判断是否删除界面中的数据
                            if (isdelete) {
                                //删除界面数据
                                list.remove(position);
                                //更新界面
                                myadapter.notifyDataSetChanged();
                            }
                        }
                    });
                    builder.setNegativeButton("取消", null);//当取消按钮只有隐藏对话框的操作的时候,可以使用null来进行操作
                    builder.show();
                }
            });
            return view;
        }

    }
    //1.创建存放控件的盒子
    static class ViewHolder{
        //TextView tv_callsmssafe_number,tv_callsmssafe_mode;
        TextView tv_callsmssafe_number;
        TextView tv_callsmssafe_mode;
        ImageView iv_callsmssafe_delete;
    }




}
