package com.cuileikun.mobilesave;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cuileikun.mobilesave.activity.HomeActivity;
import com.cuileikun.mobilesave.service.ProtectedService;
import com.cuileikun.mobilesave.utils.Contants;
import com.cuileikun.mobilesave.utils.PackageUtil;
import com.cuileikun.mobilesave.utils.Serviceutil;
import com.cuileikun.mobilesave.utils.SharedPreferencesUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.IOUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SplashActivity extends Activity {
    private static final int INSTALL_REQUESTCODE = 100;
    private final String CONNECTIONURL="http://169.254.87.155:8080/updateinfo.html";
    private String reponescode;
    private String apkurl;
    private String desc;
    private String versionName;
    private ProgressDialog progressDialog;
    private RelativeLayout splash_rl;
    /*private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			update();
		};
	};*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //拷贝数据库
        copyDB("address.db");
        copyDB("commonnum.db");
        copyDB("antivirus.db");
        //开启前台进程服务
        if (!Serviceutil.isServiceRunning(this, "com.cuileikun.mobilesave.service.ProtectedService")) {
            startService(new Intent(this,ProtectedService.class));
        }
        initView();
    }

    /**
     * Description: 初始化控件
     */
    private void initView() {

        splash_rl = (RelativeLayout) findViewById(R.id.splash_rl);
        splash_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SplashActivity.this,"联网失败 跳到主界面",Toast.LENGTH_SHORT).show();
                enterHome();
            }
        });
        TextView tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
        versionName = PackageUtil.getVersionName(this);
        tv_splash_version.setText("版本:"+versionName);

        //1.连接服务器,查看是否有最新版本
        //update();
        //发送一个延迟消息,延迟几秒中发送消息
        //msg : 消息
        //delayMillis : 延迟的时间
        //handler.sendEmptyMessageDelayed(0, 2000);
        //发送一个消息,发送一个消息到消息队列中,通知消息队列,几秒后在执行任务
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                //实际上是在主线程执行的
                if (SharedPreferencesUtil.getBoolean(SplashActivity.this, Contants.TOGGLE, true)) {
                    update();
                }else{
                    //跳转到主界面
                    enterHome();
                }
            }
        }, 2000);





    }
    /**
     *
     * Description:拷贝数据库
     *
     * @author Administrator
     *
     * @date 2015-12-4
     *
     * @date 下午3:47:06
     */
    private void copyDB(String name) {
        File file = new File(getFilesDir(), name);
        //判断文件是否存在,存在不去拷贝
        if (!file.exists()) {
            //1.获取assets管理者
            AssetManager assetManager = getAssets();
            InputStream in= null;
            FileOutputStream out = null;
            try {
                //2.读取数据库
                in = assetManager.open(name);
                //getCacheDir() : 获取缓存目录
                //getFilesDir() : 获取文件的目录
                out = new FileOutputStream(file);
                //3.读写操作
                byte[] b = new byte[1024];
                int len = -1;
                while((len = in.read(b)) != -1){
                    out.write(b, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                //4.关流
				/*in.close();
				out.close();*/
                //xutils中关流操作
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(out);
            }
        }
    }
    /**
     * Description:1.连接服务器,查看是否有最新版本
     */
    private void update() {
        //1.1连接服务器
        //联网操作,耗时操作,子线程,4.0之后不允许耗时操作在主线程执行
        //httpClient(android 6.0废弃了),httpURLConnection
        //xutils
        //connTimeout : 连接超时时间
        HttpUtils httpUtils = new HttpUtils(2000);
        //method : 请求方式
        //url : 请求的路径
        //callBack : RequestCallBack请求回调函数
        httpUtils.send(HttpMethod.GET, CONNECTIONURL, new RequestCallBack<String>() {
            //请求成功调用的方法
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                //1.2获取服务器返回的数据,服务器返回的数据:code : 新版本版本号   apkurl : 新版本下载地址    desc:更新的描述信息
                //接受数据,首选需要知道服务器是如何封装数据  xml json(json:String类型json串)
                //获取服务器返回的数据
                String result = responseInfo.result;
                //{code:"2.0",apkurl:"xxxxxxxx",desc:"新版本上线了,快来下载吧!!!"} 就是json串
                try {
                    //1.3解析json串,有封装就有解析
                    JSONObject jsonObject = new JSONObject(result);
                    //name : 就是服务器返回的接口字段
                    reponescode = jsonObject.getString("code");
                    apkurl = jsonObject.getString("apkurl");
                    desc = jsonObject.getString("desc");

                    //2.查看是否有最新版本,拿着服务返回的新版本版本号和当前的版本进行比较
                    if (reponescode.equals(versionName)) {
                        //一致,没有最新版本
                        System.out.println("连接成功,没有最新版本");
                        //跳转到主界面
                        enterHome();
                    }else{
                        //不一致,有最新版本
                        System.out.println("连接成功,有最新版本");
                        //弹出对话框,提醒用户更新版本
                        showUpdateDialog();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //请求失败调用的方法
            @Override
            public void onFailure(HttpException arg0, String arg1) {
                System.out.println("连接失败");
            }
        });
    }
    /**
     * Description: 2.弹出对话框提示用户更新版本
     */
    protected void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //设置对话框点击不能消失
        //builder.setCancelable(false);
        //设置标题
        builder.setTitle("发现新版本:"+reponescode);
        //设置图标
        builder.setIcon(R.drawable.ic_launcher);
        //设置描述信息
        builder.setMessage(desc);
        //监听对话框消失的操作
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            //当对话框消失的时候调用
            @Override
            public void onCancel(DialogInterface dialog) {
                //跳转主界面
                enterHome();
            }
        });
        //设置升级和取消按钮
        //升级按钮
        builder.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //升级操作,下载apk
                downloadApk();
            }
        });
        //取消按钮
        builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //取消升级
                //销毁对话框
                dialog.dismiss();
                //跳转到主界面
                enterHome();
            }
        });
        //显示对话框
        builder.show();
        //builder.create().show();
    }
    /**
     * Description:3.下载最新版本
     */
    protected void downloadApk() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);//设置对话框不能消失
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();

        //联网操作
        HttpUtils httpUtils = new HttpUtils();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //下载操作
            //url : 下载最新版本地址
            //target : 最新版本存放路径
            //callback : RequestCallBack
            httpUtils.download(apkurl, "/mnt/sdcard/mobliesafeSH04_02.apk", new RequestCallBack() {
                //下载成功调用的方法
                @Override
                public void onSuccess(ResponseInfo arg0) {
                    System.out.println("下载成功");

                    //消除进度条对话框
                    progressDialog.dismiss();
                    //安装最新版本
                    installerApk();
                }
                //下载失败调用的方法
                @Override
                public void onFailure(HttpException arg0, String arg1) {
                    System.out.println("下载失败");
                    //消除进度条对话框
                    progressDialog.dismiss();
                    //下载失败,跳转到主界面
                    enterHome();
                }
                //显示加载进度
                //total : 下载总进度
                //current : 当前的进度
                //isUploading : 是否是上传操作
                @Override
                public void onLoading(long total, long current,
                                      boolean isUploading) {
                    super.onLoading(total, current, isUploading);

                    //设置最大进度
                    progressDialog.setMax((int) total);
                    //设置当前的进度
                    progressDialog.setProgress((int)current);
                }
            });
        }else{
            Toast.makeText(this, "SD卡异常", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Description:4.安装最新版本
     */
    protected void installerApk() {
        /**
         * <intent-filter>
         <action android:name="android.intent.action.VIEW" />
         <category android:name="android.intent.category.DEFAULT" />
         <data android:scheme="content" />  content:// : 内容提供者协议
         <data android:scheme="file" /> file : 从文件中获取数据
         <data android:mimeType="application/vnd.android.package-archive" />
         </intent-filter>
         */
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        //intent.setType("application/vnd.android.package-archive");
        //intent.setData(Uri.fromFile(new File("/mnt/sdcard/mobliesafeSH04_02.apk")));
        intent.setDataAndType(Uri.fromFile(new File("/mnt/sdcard/mobliesafeSH04_02.apk")), "application/vnd.android.package-archive");
        //startActivity(intent);
        //当当前的activity的退出的时候,会调用之前的activity的OnActivityResult方法
        startActivityForResult(intent, INSTALL_REQUESTCODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }
    /**
     * Description: 跳转到主界面操作
     */
    protected void enterHome() {
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
        //注意:一般情况,跳转到主界面,主界面点击返回键,退出应用,不会回到splash界面
        finish();
    }






}
