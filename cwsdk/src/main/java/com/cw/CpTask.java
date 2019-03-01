package com.cw;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.cw.download.DownListener;
import com.cw.download.DownloadManager;
import com.cw.download.DownloadTask;
import com.cw.download.FileUtil;
import com.cw.download.SimpleDownApkListenerImpl;
import com.cw.download.SimpleDownPicListenerImpl;
import com.cw.entity.AdBody;
import com.cw.entity.AdReportTracker;
import com.cw.http.HttpManager;
import com.cw.http.TrackUtil;
import com.cw.ui.CpView;
import com.cw.ui.CpView.CpEventListener;
import com.cw.util.BitmapHelper;
import com.cw.util.Constants;
import com.cw.util.CpUtils;
import com.cw.util.Lg;
import com.cw.util.SpUtil;

public class CpTask implements Runnable, CpEventListener 
{

	
	private Context ctx;


	private boolean isrequestingcp;
	
	
	private static Handler h =new Handler(Looper.getMainLooper());
	
	private static Executor threadcache = Executors.newCachedThreadPool();
	private Dialog dialog;
	
	private static boolean showingcp;
	
	
	private long period;
	
	
	private static boolean onlyshortcut;
	
	private List<AdBody> currentshowcp;
	
	String getcpname()
	{
		return "";
	}
	
	private ClickWebNotificationReceiver clickWebNotificationReceiver;
	
	public CpTask(Context ctx)
	{
		this.ctx = ctx;
	}
	
	
	private void shownotificationforweb2(final AdBody info, File f)
	{
		 int icon = 0;
         try {
             PackageInfo pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
             icon = pi.applicationInfo.icon;
         } catch (NameNotFoundException e) {
         }
         
         
         if(clickWebNotificationReceiver==null)
         {
        	 clickWebNotificationReceiver = new ClickWebNotificationReceiver();
        	 IntentFilter filter=new IntentFilter();
        	 filter.addAction(ClickWebNotificationReceiver.action_clickweb);
			ctx.registerReceiver(clickWebNotificationReceiver, filter);
         }
         
         Intent intent = new Intent(ClickWebNotificationReceiver.action_clickweb);
         intent.putExtra(AdBody.class.getName(), info);
         PendingIntent pi = PendingIntent.getBroadcast(ctx, info.advertId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
         Notification n = new Notification.Builder(ctx)
         		.setContentTitle(info.title)
         		.setContentText(info.body)
         		.setSmallIcon(icon)
         		.setContentIntent(pi)
         		.build();
         
         n.flags |= Notification.FLAG_AUTO_CANCEL;
         NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
         
         Bitmap bm=null;
         if(f!=null)
         {
			bm = BitmapHelper.getbitmap(f);
	         if(bm!=null){
	             CpUtils.setNotificationIcon(ctx, n, bm);
	         }
         }
         
         nm.notify(info. advertId+400000, n);
         
         
         if(!SpUtil.isshortcutexist(ctx, info.advertId))
         {
        	 CpUtils.installShortcutForWeb(ctx, info.title, bm, info.url);
        	 SpUtil.addshortcutid(ctx, info.advertId);
         }
	}
	
	//显示通知栏
	private void shownotificationforweb(final AdBody info)
	{
		if(info==null)return;
		if(TextUtils.isEmpty(info.iconUrl))
		{
			shownotificationforweb2(info, null);
		}
		else
		{
		final File f=FileUtil.getpicfile(info.iconUrl);
		DownListener dl = new SimpleDownPicListenerImpl(){
			@Override
			public void onfinish(DownloadTask task, File file) {
                shownotificationforweb2(info, f);
               
			}
		};
		//先下载图标
		DownloadManager.downloadpic(info.iconUrl, f, dl);
		}
	}
	
	@Override
	public void run() {
		
		AdBody[] getadbody = HttpManager.getadbody();
		isrequestingcp= false;
		if(getadbody ==null||getadbody.length==0)
		{
			Lg.d("no message..");
			CpManager.getinstance(ctx).onapicpfail();
			return ;
		}
		
		
		List<AdBody> cpbody = new ArrayList<AdBody>();
		for(AdBody ab :getadbody)
		{
			if(TextUtils.isEmpty(ab.screenUrls)){
				shownotificationforweb(ab);
			}
			else
			{
				cpbody.add(ab);
			}
		}
		if(cpbody.size() == 0)
		{
			Lg.d("no cp message..");
			return ;
		}
		
		
		currentshowcp = cpbody;
		
		for(AdBody info:currentshowcp)
		{
			final File f = FileUtil.getpicfile(geturl(info));
			Lg.d(f);
			if(f.exists())
			{
				h.post(new Runnable() {
					
					@Override
					public void run() {
						onpicprepare(f);
					}
				});
			}
			else 
			{
				downloadpic(info);
			}
		}
	}
	
	void onpicprepare(File f2)
	{
		File[] fs = new File[currentshowcp.size()];
		int i = 0;
		for(AdBody info:currentshowcp)
		{
			final File f = FileUtil.getpicfile(geturl(info));
			if(!f.exists())return;
			fs[i++] = f;
		}
		showcp(fs);
	}
	
	
	
	public void start()
	{
		Lg.d("start");
		if(showingcp) return ;
		
		Activity topActivity = CpUtils.getTopActivity();
		if(topActivity==null || topActivity instanceof AActivity)
		{
			//5秒再检测
			h.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					start();
				}
			}, 3);
			return ;
		}
		if(isrequestingcp) return;
		isrequestingcp = true;
		
		
		threadcache.execute(this);
	}
	
	private String geturl(AdBody info)
	{
		String[] split = info.screenUrls.split(";");
		String url = split[0];
		return url;
	}
	
	private void downloadpic(AdBody info)
	{
		String[] split = info.screenUrls.split(";");
		String url = split[0];
		File f = FileUtil.getpicfile(url);
		DownListener dl =new SimpleDownPicListenerImpl(){
			@Override
			public void onfinish(DownloadTask task, File file) {
				onpicprepare(file);
			}
		};
		
		DownloadManager.downloadpic(url, f, dl);
	}
	
	
	private void showcp(final File[] picfiles)
	{
		Lg.d("showcp--------");
		
		if(showingcp&&dialog!=null)return ;
		
		Activity topActivity = CpUtils.getTopActivity();
		Lg.d(topActivity);
		if(topActivity==null)
		{
			return ;
		}
		
		Bitmap[] bms = new Bitmap[picfiles.length];
		int i = 0;
		for(File picfile:picfiles)
		{
			Bitmap bm = BitmapHelper.getbitmap(picfile);
			bms[i++] = bm;
		}
		CpView cp =new CpView(ctx);
		cp.setBitmap(bms);
		dialog = new Dialog(topActivity){
			@Override
			public void onBackPressed() {
			}
		};
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOwnerActivity(topActivity);
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		ViewGroup.LayoutParams lp=new ViewGroup.LayoutParams(CpUtils.getscreenwidth(topActivity),
				CpUtils.getscreenheight(ctx));
		dialog.setContentView(cp, lp);
		try
		{
			dialog.show();
		}catch(Exception e){}
		
		showingcp = true;
		
		for(AdBody info:currentshowcp){
			//状态
			feedbackstate(info.advertId, Constants.CP_STATE_SHOW);
		}
		cp.setCpEventListener(this);
		
		
		CpManager.getinstance(ctx).onapishow();
		
		//track 展示
		for(AdBody ad:currentshowcp)
		{
			//玩咖
			if(ad.reportVO!=null)
			{
				List<AdReportTracker> trackers = ad.reportVO.getImptrackers();
				TrackUtil.track(trackers);
			}
			else
			{
				TrackUtil.track(ad.showTrackingUrl);
			}
		}
	}

	@Override
	public void onclickcp(int index) {
		if(Lg.d) System.out.println("clickcp>" + index);
		AdBody info = currentshowcp.get(index);
		if(info.type==1)
		{
			
			//判断是否已经安装
			if(CpUtils.ispackageinstalled(ctx, info.apkPackage))
			{
				CpUtils.openapp(ctx, info.apkPackage);
				//返回状态
				int state = Constants.CP_STATE_DETAIL;
				feedbackstate(info.advertId, state);
			}
			else 
			{
				Toast.makeText(ctx, "开始下载" + info.apkName, 0).show();
				downloadapk(info);
			}
			
			
			if(info.reportVO!=null)
			{
				List<AdReportTracker> trackers = info.reportVO.getClktrackers();
				TrackUtil.track(trackers);
			}
			else
			{
				TrackUtil.track(info.clickTrackingUrl);
			}
			
		}
		else if(info.type==2)
		{
			//wap
			/*CpUtils.openweb(ctx, info.url);
			
			//返回状态
			int state = Constants.CP_STATE_DETAIL;
			feedbackstate(info.advertId, state);*/
			
			CpUtils.openwebwithwebview(ctx, info.url, new WebTrackImpl(info, 4), info.closeRate1, info.closeRate2);
			
		}
		removecp();
		
		/*
		//玩咖
		if(info.reportVO!=null)
		{
			List<AdReportTracker> trackers = info.reportVO.getClktrackers();
			TrackUtil.track(trackers);
		}
		else
		{
			TrackUtil.track(info.clickTrackingUrl);
		}*/
		
		
	}
	

	
	private void installapk(AdBody info, final File f)
	{
		if(CpManager.installdelay>0)
		{
			h.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					CpUtils.installapk(ctx, f);
				}
			}, CpManager.installdelay*1000);
		}
		else 
		{
			CpUtils.installapk(ctx, f);
		}
		
		//快捷方式
//		CpUtils.installShortcut(ctx, info.apkName, CpUtils.getIconFromApk(ctx, f.getPath()), f.getPath());
	}
	
	private void downloadapk(final AdBody info)
	{
		
		File f = FileUtil.getapkfile(info.url);
		Lg.d("file exist>" +f +"," + f.exists());
		if(f.exists())
		{
			installapk(info, f);
		}
		else
		{
		DownListener dl= null;
		dl = new SimpleDownApkListenerImpl(ctx, info, 4);
		
		DownloadManager.downloadapk(info.url, f, dl);
		}
	}

	@Override
	public void onclosecp() {
		removecp();
	}
	
	private void feedbackstate(final int id, final int state)
	{
		threadcache.execute(new Runnable() {
			
			@Override
			public void run() {
				HttpManager.feedbackstate(id, state, 4);
			}
		});
	}
	
	private void trynext()
	{
		if(period>0)
		{
			h.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					start();
				}
			}, period);
		}
	}
	
	private void removecp()
	{
		currentshowcp = null;
		if(dialog!=null)
		{
			if(dialog.isShowing())
			{
				dialog.cancel();
			}
			
		}
		dialog=null;
		showingcp = false;
		
		CpManager.getinstance(ctx).fornext();
	}
	
	class ClickWebNotificationReceiver extends BroadcastReceiver {
		
		
		public static final String action_clickweb = "action_click_web_notification";

		@Override
		public void onReceive(final Context context, final Intent intent) {
			threadcache.execute(new Runnable() {
				
				@Override
				public void run() {
					AdBody info = (AdBody) intent.getSerializableExtra(AdBody.class.getName());
					Lg.d(info);
					int state = Constants.CP_STATE_DETAIL;
					HttpManager.feedbackstate(info.advertId, state, 4);
					CpUtils.openweb(context, info.url);
				}
			});
		}

	}	
	
}
