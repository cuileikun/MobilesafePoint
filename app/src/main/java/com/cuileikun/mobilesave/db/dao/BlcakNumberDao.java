package com.cuileikun.mobilesave.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import com.cuileikun.mobilesave.bean.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Description:数据库操作类
 * 
 * @author Administrator
 * 
 * @date 2015-12-3
 * 
 * @date 上午9:19:34
 */
public class BlcakNumberDao {
	private BlackNumberOpenHelper blackNumberOpenHelper;
	//面试题:在同一时刻,我即去读数据也去写数据库,怎么解决这个问题,1.同步锁,2.将BlackNumberOpenHelper设置为单例模式
	private byte[] b = new byte[1024];
	
	// 增删改查
	// 1.获取数据库BlackNumberOpenHelper
	public BlcakNumberDao(Context context) {
		blackNumberOpenHelper = new BlackNumberOpenHelper(context);
	}

	/**
	 * Description:添加黑名单
	 * @date 上午9:21:46 BlackNumberContant : 要添加的黑名单号码 mode : 拦截模式
	 * @return true:表示添加成功,false:表示添加失败
	 */
	public boolean addBlackNumber(String blackNumber, int mode) {
		//同步锁
//		synchronized (b) {
			// 1.获取数据库
			SQLiteDatabase database = blackNumberOpenHelper.getWritableDatabase();
			// 2.添加操作
			// table : 表名
			// nullColumnHack : 因为sqlite数据库中不允许存入null,当有null的值,数据库直接将相应的字段设置成null
			// ContentValues : 要添加的字段数据
			ContentValues contentValues = new ContentValues();
			contentValues.put(BlackNumberContant.BLACKNUMBER_NUMBER, blackNumber);
			contentValues.put(BlackNumberContant.BLACKNUMBER_MODE, mode);
			long insert = database.insert(BlackNumberContant.BLACKNUMBER_TABLENAME,
					null, contentValues);
			// 关闭数据库
			database.close();
			// 判断是否添加成功
			return insert != -1;
//		}
	}

	/**
	 * Description:删除数据库数据
	 * @date 上午9:35:10 根据黑名单号码删除数据库中相应的数据
	 * @return : true:删除成功 false:表示删除失败
	 */
	public boolean deleteBlackNumber(String blackNumber) {
		// 获取数据库
		SQLiteDatabase database = blackNumberOpenHelper.getWritableDatabase();
		// table : 表名
		// whereClause : 查询条件 "blacknumber=?"
		// whereArgs:查询条件的参数
		int delete = database.delete(BlackNumberContant.BLACKNUMBER_TABLENAME,
				BlackNumberContant.BLACKNUMBER_NUMBER + "=?",
				new String[] { blackNumber });
		// 关闭数据库
		database.close();
		return delete != 0;
	}

	/**
	 * Description:更新黑名单数据库,根据黑名单号码,更新它的拦截模式
	 * @return true:更新成功 false:表示更新失败
	 */
	public boolean updateBlackNumber(String blackNumber, int mode) {
		// 获取数据库
		SQLiteDatabase database = blackNumberOpenHelper.getWritableDatabase();
		// table : 表名
		// ContentValues : 要更新的字段数据
		// whereClause : 查询条件
		// whereArgs : 查询条件的参数
		ContentValues contentValues = new ContentValues();
		contentValues.put(BlackNumberContant.BLACKNUMBER_MODE, mode);
		int update = database.update(BlackNumberContant.BLACKNUMBER_TABLENAME,
				contentValues, BlackNumberContant.BLACKNUMBER_NUMBER + "=?",
				new String[] { blackNumber });
		// 关闭数据库
		database.close();
		return update != 0;
	}

	/**
	 * Description:根据黑名单号码查询它的拦截模式
	 */
	public int queryBlackNumberMode(String blacknumber) {
		int mode = -1;
		// 获取数据库
		SQLiteDatabase database = blackNumberOpenHelper.getReadableDatabase();
		// 查询数据库
		// table : 表名
		// columns : 要查询的字段名
		// selection : 查询条件
		// selectionArgs : 查询条件的参数
		// groupBy : group by 分组
		// having : 去重
		// orderBy : order by 排序
		Cursor cursor = database.query(
				BlackNumberContant.BLACKNUMBER_TABLENAME,
				new String[] { BlackNumberContant.BLACKNUMBER_MODE },
				BlackNumberContant.BLACKNUMBER_NUMBER + "=?",
				new String[] { blacknumber }, null, null, null);
		// 解析cursor,如果cursor中就只有一条数据,不用while
		if (cursor != null) {
			if (cursor.moveToNext()) {
				mode = cursor.getInt(0);
			}
			cursor.close();
		}
		database.close();
		return mode;
	}

	/**
	 * Description:查询全部数据
	 */
	public List<BlackNumberInfo> queryAllBlackNumber() {
		SystemClock.sleep(2000);
		List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
		// 获取数据
		SQLiteDatabase database = blackNumberOpenHelper.getReadableDatabase();
		//查询全部数据
		Cursor cursor = database.query(BlackNumberContant.BLACKNUMBER_TABLENAME, new String[] {
				BlackNumberContant.BLACKNUMBER_NUMBER,
				BlackNumberContant.BLACKNUMBER_MODE }, null, null, null, null,
				null);
		//遍历cursor
		if (cursor != null) {
			while(cursor.moveToNext()){
				//获取数据
				String number = cursor.getString(0);
				int mode = cursor.getInt(1);
				//将数据保存到bean类中
				BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
				blackNumberInfo.number = number;
				blackNumberInfo.mode = mode;
				//将bean类添加到集合
				list.add(blackNumberInfo);
			}
			cursor.close();
		}
		database.close();
		return list;
	}
	
	
	/**
	 * Description:查询部分数据
	 * maxNum : 表示查询多少条数据
	 * startindex : 查询的起始位置
	 */
	public List<BlackNumberInfo> queryPartBlackNumber(int maxNum,int startindex) {
		SystemClock.sleep(2000);
		List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
		// 获取数据
		SQLiteDatabase database = blackNumberOpenHelper.getReadableDatabase();
		//sql : sql语句
		//参数2:SQL语句中需要的参数
		Cursor cursor = database.rawQuery("select blacknumber,mode from info limit ? offset ?", new String[]{maxNum+"",startindex+""});
		if (cursor != null) {
			while(cursor.moveToNext()){
				//获取数据
				String number = cursor.getString(0);
				int mode = cursor.getInt(1);
				//将数据保存到bean类中
				BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
				blackNumberInfo.number = number;
				blackNumberInfo.mode = mode;
				//将bean类添加到集合
				list.add(blackNumberInfo);
			}
			cursor.close();
		}
		database.close();
		return list;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
