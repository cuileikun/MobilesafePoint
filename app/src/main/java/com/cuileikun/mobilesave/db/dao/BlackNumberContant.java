package com.cuileikun.mobilesave.db.dao;

public interface BlackNumberContant {

	// 数据库名称
	public static String BLACKNUMBER_DBNAME = "blackNum.db";
	// 版本号
	public static int BLACKNUMBER_DBVERSION = 1;
	//表名
	public static String BLACKNUMBER_TABLENAME = "info";
	//id
	public static String BLACKNUMBER_ID = "_id";
	//黑名单号码字段
	public static String BLACKNUMBER_NUMBER = "blacknumber";
	//拦截模式
	public static String BLACKNUMBER_MODE = "mode";
	// 创建表的sql语句
	public static String BLACKNUMBER_CREATETABLESQL = "create table "
			+ BLACKNUMBER_TABLENAME + "(" + BLACKNUMBER_ID
			+ " integer primary key autoincrement," + BLACKNUMBER_NUMBER
			+ " varchar(20)," + BLACKNUMBER_MODE + " varchar(2))";
	/**
	 * 电话拦截
	 */
	public static int BLACKNUMBER_CALL=0;
	/**
	 * 短信拦截
	 */
	public static int BLACKNUMBER_SMS=1;
	/**
	 * 全部拦截
	 */
	public static int BLACKNUMBER_ALL=2;

}
