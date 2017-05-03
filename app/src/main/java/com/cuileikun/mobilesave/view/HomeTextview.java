package com.cuileikun.mobilesave.view;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

public class HomeTextview extends TextView {

	//在代码中使用的时候调用
	public HomeTextview(Context context) {
		super(context);
	}
	//在布局文件中使用的时候调用
	//AttributeSet : 保存的就是控件的所有属性
	public HomeTextview(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		/*android:singleLine="true"
        android:ellipsize="marquee"
        android:focusableInTouchMode="true"
        android:focusable="true"
        android:marqueeRepeatLimit="marquee_forever"*/
		setSingleLine();
		setEllipsize(TruncateAt.MARQUEE);
		setFocusableInTouchMode(true);
		setFocusable(true);
		setMarqueeRepeatLimit(-1);
	}
	//在布局文件中使用的调用
	//defStyle : 默认样式
	public HomeTextview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	//是否获取焦点
	@Override
	public boolean isFocused() {
		return true;
	}
	//参数1：控件是否有焦点
	//参数2：焦点执行的焦点方向
	//参数3：焦点从哪里获取出来
	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
		if (focused) {
			super.onFocusChanged(focused, direction, previouslyFocusedRect);
		}
	}
	//hasWindowFocus : 当 前窗口是否有当前的视图的焦点s
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if (hasWindowFocus) {
			super.onWindowFocusChanged(hasWindowFocus);
		}
	}
	
}
