package com.cuileikun.mobilesave.activity.antivirus;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cuileikun.mobilesave.R;
import com.cuileikun.mobilesave.bean.AntivirusInfo;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.lidroid.xutils.util.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AntivirusActivity extends Activity {
    private ArcProgress arc_progress;
    private PackageManager pm;
    private List<PackageInfo> packages;
    private TextView tv_antivirus_packagename;
    private List<AntivirusInfo> list;
    private ListView lv_antivirus_applications;
    private Myadapter myadapter;
    private int totalCount;
    private RelativeLayout rel_antivirus_acrprogress;
    private RelativeLayout rel_antivirus_result;
    private TextView tv_antivirus_antivirustext;
    private ImageView iv_antivirus_left;
    private ImageView iv_antivirus_right;
    private Button btn_antivirus_scan;
    private LinearLayout ll_antivirus_canvas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antivirus);
        initview();
    }

    private void initview() {
        copyDB("antivirus.db");
        arc_progress = (ArcProgress) findViewById(R.id.arc_progress);
        tv_antivirus_packagename = (TextView) findViewById(R.id.tv_antivirus_packagename);
        lv_antivirus_applications = (ListView) findViewById(R.id.lv_antivirus_applications);
        rel_antivirus_acrprogress = (RelativeLayout) findViewById(R.id.rel_antivirus_acrprogress);
        rel_antivirus_result = (RelativeLayout) findViewById(R.id.rel_antivirus_result);
        tv_antivirus_antivirustext = (TextView) findViewById(R.id.tv_antivirus_antivirustext);
        iv_antivirus_left = (ImageView) findViewById(R.id.iv_antivirus_left);
        iv_antivirus_right = (ImageView) findViewById(R.id.iv_antivirus_right);
        btn_antivirus_scan = (Button) findViewById(R.id.btn_antivirus_scan);
        ll_antivirus_canvas = (LinearLayout) findViewById(R.id.ll_antivirus_canvas);

        scan();
    }
    /**
     * Description:拷贝数据库
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
    //扫描操作
    private void scan() {

        list = new ArrayList<AntivirusInfo>();
        list.clear();


        pm = getPackageManager();
        new Thread(){

            public void run() {

                packages = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
                int total = packages.size();
                runOnUiThread(new Runnable() {
                    public void run() {
                        arc_progress.setMax(100);
                    }
                });
                //设置进度条最大进度
                //设置当前进度
                int progress=0;
                for (final PackageInfo packageInfo : packages) {
                    SystemClock.sleep(100);
                    progress++;
                    final int progerssMax = (int) (progress * 100f/total + 0.5f);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            arc_progress.setProgress(progerssMax);
                            tv_antivirus_packagename.setText(packageInfo.packageName);
                        }
                    });

                    //循环的时候获取每个应用的特征码，查询数据库
                    //获取特征码
                    Signature[] singtures = packageInfo.signatures;//得到应用程序的特征码
                    String charsString = singtures[0].toCharsString();
//                    boolean antivirus = AntivirusDao.isAntivirus(MD5Util.md5(charsString), getApplicationContext());
                    boolean antivirus = false;

                    AntivirusInfo antivirusInfo = new AntivirusInfo();
                    antivirusInfo.name = packageInfo.applicationInfo.loadLabel(pm).toString();
                    antivirusInfo.icon = packageInfo.applicationInfo.loadIcon(pm);
                    antivirusInfo.packageName = packageInfo.packageName;
                    if (antivirus) {
                        antivirusInfo.isAntivirus = true;
                    }else{
                        antivirusInfo.isAntivirus = false;
                    }
                    if (antivirus) {
                        totalCount++;
                        list.add(0,antivirusInfo);
                    }else{
                        list.add(antivirusInfo);
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            //设置显示数据
                            if (myadapter == null) {
                                myadapter = new Myadapter();
                                lv_antivirus_applications.setAdapter(myadapter);
                            }else{
                                myadapter.notifyDataSetChanged();
                            }
                            lv_antivirus_applications.smoothScrollToPosition(myadapter.getCount());
                        }
                    });
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (packages.size() == list.size()) {
                                lv_antivirus_applications.smoothScrollToPosition(0);
                                if (totalCount > 0) {
                                    //有病毒
                                    rel_antivirus_result.setVisibility(View.VISIBLE);
                                    rel_antivirus_acrprogress.setVisibility(View.GONE);
                                    tv_antivirus_antivirustext.setText("您的手机很不安全");
                                }else{
                                    //没有病毒
                                    rel_antivirus_result.setVisibility(View.VISIBLE);
                                    rel_antivirus_acrprogress.setVisibility(View.GONE);
                                    tv_antivirus_antivirustext.setText("您的手机很安全");
                                }
                                ll_antivirus_canvas.setVisibility(View.VISIBLE);
                                //获取进度条的缓存图片
                                rel_antivirus_acrprogress.setDrawingCacheEnabled(true);
                                rel_antivirus_acrprogress.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                                Bitmap bitmap = rel_antivirus_acrprogress.getDrawingCache();
                                //拆分缓存图片

                                Bitmap leftBitmap = getLeftBitmap(bitmap);

                                Bitmap rightBitmap = getRihtBitmap(bitmap);

                                iv_antivirus_left.setImageBitmap(leftBitmap);
                                iv_antivirus_right.setImageBitmap(rightBitmap);

                                showAnimation();

                                btn_antivirus_scan.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        closeAnimation();
                                    }

                                });
                            }

                        }


                    });
                }
            }
        }.start();

    }

    private void closeAnimation() {
        AnimatorSet animatorSet = new AnimatorSet();
        //iv_antivirus_left.setTranslationX(translationX)
        //iv_antivirus_left.setAlpha(alpha)
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(iv_antivirus_left, "translationX", -iv_antivirus_left.getWidth(),0),
                ObjectAnimator.ofFloat(iv_antivirus_right, "translationX", iv_antivirus_right.getWidth(),0),
                ObjectAnimator.ofFloat(iv_antivirus_left, "alpha", 0,1),
                ObjectAnimator.ofFloat(iv_antivirus_right, "alpha", 0,1),
                ObjectAnimator.ofFloat(rel_antivirus_result, "alpha", 1,0)
        );
        animatorSet.setDuration(3000);
        animatorSet.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ll_antivirus_canvas.setVisibility(View.GONE);
                rel_antivirus_acrprogress.setVisibility(View.VISIBLE);
                scan();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub

            }
        });
        animatorSet.start();
    }

    private void showAnimation() {
        AnimatorSet animatorSet = new AnimatorSet();
        //iv_antivirus_left.setTranslationX(translationX)
        //iv_antivirus_left.setAlpha(alpha)
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(iv_antivirus_left, "translationX", 0,-iv_antivirus_left.getMeasuredWidth()),
                ObjectAnimator.ofFloat(iv_antivirus_right, "translationX", 0,iv_antivirus_right.getMeasuredWidth()),
                ObjectAnimator.ofFloat(iv_antivirus_left, "alpha", 1,0),
                ObjectAnimator.ofFloat(iv_antivirus_right, "alpha", 1,0),
                ObjectAnimator.ofFloat(rel_antivirus_result, "alpha", 0,1)
        );
        animatorSet.setDuration(3000);
        animatorSet.start();

    }

    private Bitmap getRihtBitmap(Bitmap bitmap) {
        int width = (int) (bitmap.getWidth()/2 + 0.5f);
        int height = bitmap.getHeight();

        Bitmap canvasbitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());

        Canvas canvas = new Canvas(canvasbitmap);

        Paint paint = new Paint();

        Matrix matrix = new Matrix();

        matrix.setTranslate(-width, 0);

        canvas.drawBitmap(bitmap, matrix, paint);

        return canvasbitmap;
    }


    private Bitmap getLeftBitmap(Bitmap bitmap) {

        int width = (int) (bitmap.getWidth()/2 + 0.5f);
        int height = bitmap.getHeight();

        Bitmap canvasbitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());

        Canvas canvas = new Canvas(canvasbitmap);

        Paint paint = new Paint();

        Matrix matrix = new Matrix();

        //matrix.setTranslate(dx, dy)

        canvas.drawBitmap(bitmap, matrix, paint);

        return canvasbitmap;
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
            View view;
            ViewHolder viewHolder;
            if (convertView==null) {
                view = View.inflate(getApplicationContext(), R.layout.antivirus_item, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_anitvirus_icon = (ImageView) view.findViewById(R.id.iv_anitvirus_icon);
                viewHolder.tv_anitvirus_title = (TextView) view.findViewById(R.id.tv_anitvirus_title);
                viewHolder.tv_anitvirus_desc = (TextView) view.findViewById(R.id.tv_anitvirus_desc);
                view.setTag(viewHolder);
            }else{
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            AntivirusInfo antivirusInfo = list.get(position);
            viewHolder.iv_anitvirus_icon.setImageDrawable(antivirusInfo.icon);
            viewHolder.tv_anitvirus_title.setText(antivirusInfo.name);

            if (antivirusInfo.isAntivirus) {
                viewHolder.tv_anitvirus_desc.setText("病毒");
                viewHolder.tv_anitvirus_desc.setTextColor(Color.RED);
            }else{
                viewHolder.tv_anitvirus_desc.setText("安全");
                viewHolder.tv_anitvirus_desc.setTextColor(Color.GREEN);
            }

            return view;
        }

    }
    static class ViewHolder{
        ImageView iv_anitvirus_icon;
        TextView tv_anitvirus_title,tv_anitvirus_desc;
    }



}
