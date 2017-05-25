package com.cuileikun.mobilesave.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

public class AntivirusDao {
	//查询是否是病毒
	public static boolean isAntivirus(String md5,Context context){
		boolean isAntivirus = false;
		
		File file = new File(context.getFilesDir(), "antivirus.db");
		SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
		Cursor cursor = database.query("datable", null, "md5=?", new String[]{md5}, null, null, null);
		if (cursor.moveToNext()) {
			isAntivirus = true;
		}
		cursor.close();
		database.close();
		return isAntivirus;
	}  
	
}
