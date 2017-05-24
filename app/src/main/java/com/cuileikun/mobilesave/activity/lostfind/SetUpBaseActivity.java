package com.cuileikun.mobilesave.activity.lostfind;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.cuileikun.mobilesave.R;

public abstract class SetUpBaseActivity extends Activity {

    private GestureDetector gestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //手势识别器
        //手势识别器,必须注册到界面的触摸事件中才有效果
        gestureDetector = new GestureDetector(this, new MyOnGestureListener());
        super.onCreate(savedInstanceState);
    }
    //界面的触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //将手势识别器,注册到触摸事件中
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        //滑动的操作
        //e1 : 按下的事件,保存有按下的坐标
        //e2 : 抬起的事件,保存有抬起的坐标
        //velocity : 速度,滑动速率
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {

            //获取按下和抬起的坐标
            int startX = (int) e1.getRawX();//按下的x坐标
            int endX = (int) e2.getRawX();//抬起的x坐标

            //获取Y坐标
            int startY = (int) e1.getRawY();
            int endY = (int) e2.getRawY();


            if ((Math.abs(startY-endY)) > 50) {
                Toast.makeText(SetUpBaseActivity.this, "你小子又乱滑了.....", Toast.LENGTH_SHORT).show();
                return true;
            }

            //下一步操作
            if ((startX - endX) > 100) {
                doNext();
            }
            //上一步操作,100像素
            if ((endX-startX) > 100) {
                doPre();
            }
            //true if the event is consumed, else false
            //true : 执行事件,false:不执行
            return true;
        }

    }

    //父类是不是知道子类应该跳转到那个activity,所以父类可以创建两个抽象方法,让子类根据自己的特性去实现响应的方法进行操作
    //下一步按钮的点击事件
    public void next(View v){
        doNext();
    }
    //上一步按钮的点击事件
    public void pre(View v){
        doPre();
    }


    //下一步执行具体操作
    private void doNext() {
        if (next_activity()) {
            return;
        }
        finish();
        overridePendingTransition(R.anim.setup_next_enter, R.anim.setup_next_exit);
    }
    //上一步执行的具体操作
    private void doPre() {
        if (pre_activity()) {
            return;
        }
        finish();
        overridePendingTransition(R.anim.setup_pre_enter, R.anim.setup_pre_exit);
    }

    /**
     * Description:下一步的执行操作方法
     * @return  boolean : true:表示不执行切换界面操作,false : 执行界面切换的操作
     */
    public abstract boolean next_activity();
    /**
     * Description:上一步的执行操作方法,子类实现操作
     * @return  boolean : true:表示不执行切换界面操作,false : 执行界面切换的操作
     */
    public abstract boolean pre_activity();

}
