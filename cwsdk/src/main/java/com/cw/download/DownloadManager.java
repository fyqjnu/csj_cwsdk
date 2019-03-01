package com.cw.download;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.content.Context;
import android.text.TextUtils;

public class DownloadManager {

	
	
	static DownloadManager dm ;
	
	
	private Context ctx ;
	
	
	private ConcurrentHashMap<String, DownloadTask> apktasks = new ConcurrentHashMap<String, DownloadTask>();
	private ConcurrentHashMap<String, DownloadTask> pictasks = new ConcurrentHashMap<String, DownloadTask>();
	
	private Executor threadcache = Executors.newCachedThreadPool();
	
	DbManager dbmgr ;
	
	private DownloadManager(Context ctx)
	{
		
		this.ctx = ctx.getApplicationContext();
		dbmgr = DbManager.getinstance(this.ctx);
		List<DownloadRecord> records = dbmgr.getallrecords();
		for(DownloadRecord record :records)
		{
			File file = new File(record.file);
			if(file.exists())continue;
			DownloadTask task = new DownloadTask(record.url, file);
			task.start();
		}
		
	}
	
	public static DownloadManager getinstance(Context ctx) 
	{
		if(dm==null)
		{
			dm =new DownloadManager(ctx);
		}
		return dm;
	}
	
	
	public void addtask(DownloadTask dt)
	{
		int type = dt.getfiletype();
		if(type==DownloadTask.filetype_apk)
		{
			apktasks.put(dt.geturl(), dt);
		}
		else 
		{
			pictasks.put(dt.geturl(), dt);
		}
		
		threadcache.execute(dt);
	}
	
	public static void downloadapk(String url, File f, DownListener dl )
	{
		if(TextUtils.isEmpty(url))return ;
		if(dm.apktasks.containsKey(url)){
			DownloadTask dt = dm.apktasks.get(url);
			dt.setdownlistener(dl);
			return;
		}
		
		DownloadTask task =new DownloadTask(url, f);
		task.setdownlistener(dl);
		task.start();
	}
	
	public static void downloadpic(String url, File f, DownListener dl)
	{
		if(TextUtils.isEmpty(url))return ;
		if(dm.pictasks.containsKey(url)){
			dm.pictasks.get(url).setdownlistener(dl);
			return;
		}
		
		DownloadTask task =new DownloadTask(url, f);
		task.setfiletype(DownloadTask.filetype_pic);
		task.setdownlistener(dl);
		task.start();
	}
	
	
}
