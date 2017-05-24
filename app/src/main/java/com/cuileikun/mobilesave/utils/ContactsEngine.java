package com.cuileikun.mobilesave.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

import com.cuileikun.mobilesave.bean.ContactsInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class ContactsEngine {
	/**
	 * Description:获取所有的系统联系人
	 */
	public static List<ContactsInfo> getAllContacts(Context context){
		List<ContactsInfo> list = new ArrayList<ContactsInfo>();
		//1.获取的内容解析者
		ContentResolver resolver = context.getContentResolver();
		//2.获取查询路径
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		//3.查询
		String[] projection = new String[]{
			ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
			ContactsContract.CommonDataKinds.Phone.NUMBER,
			ContactsContract.CommonDataKinds.Phone.CONTACT_ID
		};
		//uri : 查询地址
		//projection : 查询的字段
		//selection : 查询条件
		//selectionArgs : 查询条件的参数
		//sortOrder : 排序
		Cursor cursor = resolver.query(uri, projection, null, null, null);
		//4.遍历解析cursor
		if (cursor!= null) {
			while(cursor.moveToNext()){
				//5.获取相应的数据
				String name = cursor.getString(0);
				String number = cursor.getString(1);
				String contactid = cursor.getString(2);
				//6.保存到bean类中
				ContactsInfo contactsInfo = new ContactsInfo();
				contactsInfo.name = name;
				contactsInfo.number = number;
				contactsInfo.contactid = contactid;
				//7.添加到集合中
				list.add(contactsInfo);
			}
		}
		//8.关闭cursor
		cursor.close();
		
		return list;
	}
	
	
	/**
	 * Description:获取联系人的头像
	 */
	public static Bitmap getContactsPhoto(Context context,String contactid){
		//1.获取内容解析者
		ContentResolver resolver = context.getContentResolver();
		//2.获取联系人头像的uri
		//content://contacts/101
		Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactid);
		//3.获取头像,返回的流信息
		//参数1:内容解析者
		//参数2:头像路径
		InputStream in = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
		//4.将流转化成bitmap
		Bitmap bitmap = BitmapFactory.decodeStream(in);
		//5.关流操作
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return bitmap;
	}
	
	
	
	
	
	
	
	
	
	
}
