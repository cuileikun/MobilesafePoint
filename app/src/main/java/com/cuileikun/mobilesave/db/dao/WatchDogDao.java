package com.cuileikun.mobilesave.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:数据库操作类
 */
public class WatchDogDao {
	private WatchDogOpenHelper watchDogOpenHelper;
	//面试题:在同一时刻,我即去读数据也去写数据库,怎么解决这个问题,1.同步锁,2.将BlackNumberOpenHelper设置为单例模式
	private byte[] b = new byte[1024];
	
	// 增删改查
	// 1.获取数据库BlackNumberOpenHelper
	public WatchDogDao(Context context) {
		watchDogOpenHelper = new WatchDogOpenHelper(context);
	}

	/**
	 * Description:添加程序
	 * packageName : 表示添加的应用程序的包名
	 * @return true:表示添加成功,false:表示添加失败
	 */
	public boolean addLockAPP(String packageName) {
		//同步锁
//		synchronized (b) {
			// 1.获取数据库
			SQLiteDatabase database = watchDogOpenHelper.getWritableDatabase();
			// 2.添加操作
			// table : 表名
			// nullColumnHack : 因为sqlite数据库中不允许存入null,当有null的值,数据库直接将相应的字段设置成null
			// ContentValues : 要添加的字段数据
			ContentValues contentValues = new ContentValues();
			contentValues.put(WatchDogContant.WATCHDOG_PACKAGENAME, packageName);
			long insert = database.insert(WatchDogContant.WATCHDOG_TABLENAME,
					null, contentValues);
			// 关闭数据库
			database.close();
			// 判断是否添加成功
			return insert != -1;
//		}
	}

	/**
	 * Description:删除包名
	 * @return : true:删除成功 false:表示删除失败
	 */
	public boolean deleteLockApp(String packageName) {
		// 获取数据库
		SQLiteDatabase database = watchDogOpenHelper.getWritableDatabase();
		// table : 表名
		// whereClause : 查询条件 "blacknumber=?"
		// whereArgs:查询条件的参数
		int delete = database.delete(WatchDogContant.WATCHDOG_TABLENAME,
				WatchDogContant.WATCHDOG_PACKAGENAME + "=?",
				new String[] { packageName });
		// 关闭数据库
		database.close();
		return delete != 0;
	}
	/**
	 * Description:查询数据库中是否有应用程序包名
	 */
	public boolean queryLockAPP(String packageName) {
		boolean islock = false;
		// 获取数据库
		SQLiteDatabase database = watchDogOpenHelper.getReadableDatabase();
		// 查询数据库
		// table : 表名
		// columns : 要查询的字段名
		// selection : 查询条件
		// selectionArgs : 查询条件的参数
		// groupBy : group by 分组
		// having : 去重
		// orderBy : order by 排序
		Cursor cursor = database.query(
				WatchDogContant.WATCHDOG_TABLENAME,
				null,
				WatchDogContant.WATCHDOG_PACKAGENAME + "=?",
				new String[] { packageName }, null, null, null);
		// 解析cursor,如果cursor中就只有一条数据,不用while
		if (cursor != null) {
			if (cursor.moveToNext()) {
				islock = true;
			}
			cursor.close();
		}
		database.close();
		return islock;
	}

	/**
	 * Description:查询所有应用程序的包名
	 */
	public List<String> queryAllLockApp() {
		SystemClock.sleep(2000);
		List<String> list = new ArrayList<String>();
		// 获取数据
		SQLiteDatabase database = watchDogOpenHelper.getReadableDatabase();
		//查询全部数据
		Cursor cursor = database.query(WatchDogContant.WATCHDOG_TABLENAME, new String[] {
				WatchDogContant.WATCHDOG_PACKAGENAME}, null, null, null, null,
				null);
		//遍历cursor
		if (cursor != null) {
			while(cursor.moveToNext()){
				//获取数据
				String packageName = cursor.getString(0);
				list.add(packageName);
			}
			cursor.close();
		}
		database.close();
		return list;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
