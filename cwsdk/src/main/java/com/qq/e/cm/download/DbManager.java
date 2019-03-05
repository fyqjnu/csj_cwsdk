package com.qq.e.cm.download;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbManager {

	
	
	
	public static DbManager dm;
	
	private Context ctx;
	
	
	private DatabaseHelper helper;
	private DbManager (Context ctx)
	{
		this.ctx = ctx.getApplicationContext();
		
		helper = new DatabaseHelper(this.ctx);
	}
	
	public static DbManager getinstance(Context ctx)
	{
		if(dm==null)
		{
			dm =new DbManager(ctx); 
		}
		return dm;
	}
	
	
	public void update(DownloadRecord record)
	{
		
		ContentValues values=new ContentValues();
		values.put("url", record.url);
		values.put("file", record.file);
		String whereClause= "where url = ?";
		String[] whereArgs={record.url};
		SQLiteDatabase db = helper.getWritableDatabase();
		db.update(name_db, values, whereClause, whereArgs);
		db.close();
	}
	
	public List<DownloadRecord> getallrecords()
	{
		
		/*
		try {
			Field f = helper.getClass().getSuperclass().getDeclaredField("mContext");
			f.setAccessible(true);
			Object object = f.get(helper);
			System.out.println(object);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		ArrayList<DownloadRecord> list =new ArrayList<DownloadRecord>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(name_table, null, null, null, null, null, null);
		if(cursor!=null)
		{
			while(!cursor.isAfterLast())
			{
				DownloadRecord record =new DownloadRecord();
				record.url = cursor.getString(cursor.getColumnIndex("url"));
				record.file = cursor.getString(cursor.getColumnIndex("file"));
				cursor.moveToNext();
			}
			
			cursor.close();
			
		}
		db.close();
		return list;
	}
	
	
	static String name_db = "db33";
	static int version_db = 1;
	static String name_table="tb33";


			
	
	static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, name_db, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = String.format("CREATE TABLE %s ( _id INTEGER PRIMARY KEY, url TEXT, file TEXT );", name_table );
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			String sql = "DROP TABLE IF EXISTS " + name_table;
			db.execSQL(sql);
			onCreate(db);
		}
		
	}
	
	
}
