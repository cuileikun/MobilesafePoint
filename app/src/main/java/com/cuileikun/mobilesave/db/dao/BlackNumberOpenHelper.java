package com.cuileikun.mobilesave.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Description:创建数据库
 */
public class BlackNumberOpenHelper extends SQLiteOpenHelper {

	// 面试题:单例模式,模板模式,装饰模式,工厂模式
	/*
	 * private BlackNumberOpenHelper(Context context){ super(context,
	 * BlackNumberContant.BLACKNUMBER_DBNAME, null,
	 * BlackNumberContant.BLACKNUMBER_DBVERSION); }
	 * 
	 * private BlackNumberOpenHelper blackNumberOpenHelper;
	 * 
	 * public BlackNumberOpenHelper getIntance(Context context){ synchronized
	 * (context) { if (blackNumberOpenHelper == null) { blackNumberOpenHelper =
	 * new BlackNumberOpenHelper(context); } return blackNumberOpenHelper; } }
	 */

	// 一般在构造函数中设置数据库的名称和版本号
	public BlackNumberOpenHelper(Context context) {
		super(context, BlackNumberContant.BLACKNUMBER_DBNAME, null,
				BlackNumberContant.BLACKNUMBER_DBVERSION);
	}

	// 创建表结构
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(BlackNumberContant.BLACKNUMBER_CREATETABLESQL);
	}

	// 更新数据库
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
