package com.cuileikun.mobilesave.objectanimator;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.cuileikun.mobilesave.R;

public class ObjectAnimatorDemoActivity extends AppCompatActivity {

    private ImageView iv_saishi;

    private ImageView iv_yuezhan;
    private ImageView iv_quidui;
    private ImageView iv_quidui1;
    private ImageView iv_faxian;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_animator_demo);
        iv_saishi = (ImageView) findViewById(R.id.iv_saishi);
        iv_yuezhan = (ImageView) findViewById(R.id.iv_yuezhan);
        iv_quidui = (ImageView) findViewById(R.id.iv_quidui);
        iv_quidui1 = (ImageView) findViewById(R.id.iv_quidui1);
        iv_faxian = (ImageView) findViewById(R.id.iv_faxian);
        saiShiAnimator();
        yueZhanAnimator();
        quiDuiAnimator();
        faXianAnimator();
    }


    /**
     * 赛事动画
     */
    private void saiShiAnimator() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(iv_saishi, "translationY", 0, 20, -10,0);

        // 动画的持续时间
        objectAnimator.setDuration(2000);
        // 执行的次数吗，INFINITE ：代表一直执行
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        // 旋转的模式
        // REVERSE : 动画执行完成，逆向执行动画
        // RESTART : 每次都初始化执行动画
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
        // 执行动画
        objectAnimator.start();
    }

    /**
     * 约战动画
     */
    private void yueZhanAnimator() {
    }

    /**
     * 球队动画
     */
    private void quiDuiAnimator() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(iv_quidui, "translationY", 0, 10, -5,0);

        // 动画的持续时间
        objectAnimator.setDuration(2000);
        // 执行的次数吗，INFINITE ：代表一直执行
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        // 旋转的模式
        // REVERSE : 动画执行完成，逆向执行动画
        // RESTART : 每次都初始化执行动画
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
        // 执行动画
        objectAnimator.start();

        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(iv_quidui1, "scaleX", 0, 10, -5,0);

        // 动画的持续时间
        objectAnimator1.setDuration(2000);
        // 执行的次数吗，INFINITE ：代表一直执行
        objectAnimator1.setRepeatCount(ObjectAnimator.INFINITE);
        // 旋转的模式
        // REVERSE : 动画执行完成，逆向执行动画
        // RESTART : 每次都初始化执行动画
        objectAnimator1.setRepeatMode(ObjectAnimator.RESTART);
        // 执行动画
        objectAnimator1.start();
    }

    /**
     * 发现动画
     */
    private void faXianAnimator() {
    }
}
