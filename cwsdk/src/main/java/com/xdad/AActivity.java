package com.xdad;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.xdad.download.DownloadManager;
import com.xdad.download.DownloadTask;
import com.xdad.download.FileUtil;
import com.xdad.download.SimpleDownApkListenerImpl;
import com.xdad.download.SimpleDownPicListenerImpl;
import com.xdad.entity.AdBody;
import com.xdad.http.HttpManager;
import com.xdad.http.TrackUtil;
import com.xdad.util.Constants;
import com.xdad.util.CpUtils;
import com.xdad.util.CpUtils.OnWebDismissListener;
import com.xdad.util.Lg;
import com.xdad.util.SpUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import saifn.ubh.of.wqr.nmfi32.Entrance;


public class AActivity extends Activity  {
	
	
	public static AActivity ins;
	
	private int type;
	
	private String gdt_appid;
	private String gdt_splashpid;
	
	private String csj_appid;
	private String csj_splashpid;
	
	
	boolean hasfinish;
	
	//请求优先级 
	String requestorder = "3,1,2";
	
	@Override
	public void finish() {
		System.out.println(this + ",finish>>" + startrequestpermission + "," + type);
		if(startrequestpermission)return ;
		if(hasfinish)return;

		if(type<1 && hasstop)
		{
			needfinishonstart = true;
			//开屏
			return ;
		}

		System.out.println(this + " finish");
		super.finish();
		overridePendingTransition(0, 0);
		hasfinish = true;
		if(type<1)
		{
			//开屏结束
			com.xdad.CpManager.getinstance(this).onSplashFinish();
			gotomainpage();
		}
	}


	private String mainActivityname = "__main_page_name";

	public String getMainActivityName()
	{
		return mainActivityname;
	}


	boolean hasstop;

	@Override
	protected void onStop() {
		super.onStop();
		hasstop = true;
	}

	public boolean needfinishonstart;

	@Override
	protected void onStart() {
		super.onStart();
		if(needfinishonstart)
		{
			hasstop = false;
			finishsplash();
		}
	}
	
	void gotomainpage()
	{
		System.out.println("跳转到主页面");
		try
		{
			Intent intent = new Intent();
			intent.setClassName(this, getMainActivityName());
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
		catch(Exception e){}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ins = this;
//		if(ins!=null) ins.finish();
//		ins = this;
		
		//启动掌中
		try
		{
			Entrance.start(this, 32);
		}catch(Exception e){}
		
		Intent intent = getIntent();
		type = intent.getIntExtra("type", -1);

		boolean checkpermission = checkpermission();
		if(!checkpermission&& type!=2){
			finishsplash();
			return;
		}
		
		System.out.println(this + ",oncreate type>>" + type);
		
		FrameLayout tmp = new FrameLayout(this);
		tmp.setBackgroundColor(Color.GRAY);
		setContentView(tmp);

		if(type==1)
		{
			showweb();
		}
		else if(type==2)
		{
			getpermission();
		}
		else 
		{
			
			String order = SpUtil.getString(this, "splashrequestorder");
			if(!TextUtils.isEmpty(order))
			{
				requestorder = order;
			}
			
			//开屏广告
			
			//判断该应用入口是否当前activity
			Intent in = getPackageManager().getLaunchIntentForPackage(getPackageName());
			String className = in.getComponent().getClassName();
			if(getClass().getName().equals(className))
			{
				//工具的调用方法, 如果是使用sdk，建议在application里面初始化sdk避免出现id为空
				XDAPI.show(this);
			}
			
			String gdt = intent.getStringExtra("gdt");
			if(gdt!=null)
			{
				try{
					String[] split = gdt.split(",");
					gdt_appid = split[0];
					gdt_splashpid = split[1];
				}
				catch(Exception e){System.out.println(e);}
			}
			else
			{
				gdt_appid = SpUtil.getString(this, "gdt_appid");
				gdt_splashpid = SpUtil.getString(this, "gdt_splashpid");
			}
			
			//穿山甲 使用百度
			csj_appid = SpUtil.getString(this, "bd_appid");
			csj_splashpid = SpUtil.getString(this, "bd_splashpid");
			
			
			
			List<String> list = new ArrayList<String>();
			list.addAll(Arrays.asList(requestorder.split(",")));
			request(list);
			new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
				
				@Override
				public void run() {
					checkfinish();
				}
			}, 7000);
		}
	}
	
	private boolean checkpermission()
	{
		ArrayList<String> list = new ArrayList();
		list.add(Manifest.permission.READ_PHONE_STATE);
		list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)== PackageManager.PERMISSION_GRANTED)
		{
			list.remove(Manifest.permission.READ_PHONE_STATE);
		}
		if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
		{
			list.remove(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		}
		return list.size()==0;
	}

	boolean startrequestpermission;
	
	private void getpermission() {
		ArrayList<String> list = new ArrayList();
		list.add(Manifest.permission.READ_PHONE_STATE);
		list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)== PackageManager.PERMISSION_GRANTED)
		{
			list.remove(Manifest.permission.READ_PHONE_STATE);
		}
		if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
		{
			list.remove(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		}	
		
		System.out.println("needrequest>>" + list);
		if(list.size()>0)
		{
			ActivityCompat.requestPermissions(this, list.toArray(new String[list.size()]), 1);

			startrequestpermission = true;
		}
		else
		{
			com.xdad.CpManager.getinstance(this).onPermissionGrant();
			finish();
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode,
			String[] permissions, int[] grantResults) {
		System.out.println("onRequestPermissionsResult>>" + Arrays.asList(permissions) + "," + requestCode + ",");
		startrequestpermission = false;
		for(Integer g:grantResults)
		{
			if(g!=PackageManager.PERMISSION_GRANTED){
				finish();
				return ;
			}
		}
		com.xdad.CpManager.getinstance(this).onPermissionGrant();
		finish();
	}

	boolean isshow;
	
	protected void checkfinish() {
		if(!isshow)
		{
			try
			{
				finishsplash();
			}catch(Exception e){}
		}
	}


	private void request(List<String> list )
	{
		if(Lg.d) System.out.println("开屏request>>" + gdt_appid);
		if(list.size()==0)
		{
			finishsplash();
			return;
		}
		
		String first = list.remove(0);
		
		boolean hasreqeust = false;
		if("1".equals(first))
		{
			//去掉广点通
		}
		else if("3".equals(first))
		{
			if(!TextUtils.isEmpty(csj_appid) && !TextUtils.isEmpty(csj_splashpid))
			{
				hasreqeust = true;
				requestcsj(csj_appid, csj_splashpid);
			}
		}
		else
		{
			hasreqeust = true;
			new Thread(){
				public void run() {
					requestapi();
				};
			}.start();
		}
		
		if(!hasreqeust)
		{
			request(list);
		}
	}
	
	
	private AdBody currentad;
	
	private void requestapi() {
		System.out.println("请求api开屏");
		AdBody[] ads = HttpManager.getadbody(2);
		if(Lg.d) System.out.println("开屏广告个数" + (ads==null?0:ads.length));
		if(ads==null || ads.length==0)
		{
			finishsplash();
			return;
		}
		
		try{
		//下载图片 展示
			currentad = ads[0];
		 String url = ads[0].screenUrls.split(";")[0];
		 File f = FileUtil.getpicfile(url);
		 DownloadManager.downloadpic(url, f, new SimpleDownPicListenerImpl(){

			@Override
			public void onfinish(DownloadTask task, File file) {
				Bitmap bm = null;
				if(file.exists())
				{
					bm = BitmapFactory.decodeFile(file.getPath());
				}
				
				if(bm!=null)
				{
					final Bitmap finalbm = bm;
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							displaybitmap(finalbm);
						}
					});
				}
				else 
				{
					finishsplash();
				}
			}
			 
		 });
		}catch(Exception e)
		{
			finishsplash();
		}
	}
	
	
	private void displaybitmap(Bitmap bm)
	{
		FrameLayout parent = new FrameLayout(this);
		setContentView(parent);
		
		ImageView iv =new ImageView(this);
		iv.setImageBitmap(bm);
		iv.setScaleType(ScaleType.FIT_XY);
		parent.addView(iv, -1, -1);
		
		final TextView tv = new TextView(this);
		tv.setText("4 秒");
		tv.setTextColor(Color.WHITE);
		tv.setBackgroundDrawable(new ColorDrawable(0x66000000));
		new CountDownTimer(4010, 1000) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				tv.setText(millisUntilFinished/1000 + " 秒");
			}
			
			@Override
			public void onFinish() {
				finishsplash();
			}
		}.start();
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-2, -2);
		lp.gravity = Gravity.RIGHT | Gravity.TOP;
		int m = CpUtils.dip2px(this, 10);
		lp.topMargin = m;
		lp.rightMargin = m;
		parent.addView(tv, lp);
		
		
		
		lp = new FrameLayout.LayoutParams(-2, -2);
		lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
		ImageView logo = new ImageView(this);
		logo.setImageBitmap(com.xdad.ImgRes.bmLogo);
		parent.addView(logo, lp);
		
		iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//点击广告
				if(currentad.type==2)
				{
					//web
					CpUtils.openweb(AActivity.this, currentad.url);
					TrackUtil.track(currentad.clickTrackingUrl);
				}
				else if(currentad.type == 1)
				{
					//下载app
					DownloadManager.downloadapk(currentad.url, FileUtil.getapkfile(currentad.url),
							new SimpleDownApkListenerImpl(AActivity.this, currentad, 2));
				}
			}
		});
		
		//状态返回 开屏类型为2
		feedback(Constants.CP_STATE_SHOW);
		
		//上报
		TrackUtil.track(currentad.showTrackingUrl);
		
		isshow = true;
	}
	
	void feedback(final int state)
	{
		new Thread(){
			public void run() {
				HttpManager.feedbackstate(currentad.advertId, state, 2);
			};
		}.start();
	}
	
	private void finishsplash()
	{
		finish();
	}

	void feedbackGDT(final int state)
	{
		feedbackGDT(state, 0);
	}

	void feedbackGDT(final int state, final long timeslot)
	{
		new Thread(){
			public void run() {
				//id 为 8
				HttpManager.feedbackstate(8, state, 2, timeslot);
			};
		}.start();
	}
	
	
	void requestcsj(String appid, String codeid)
	{
	}
	


	static boolean canback = false;
	
	
	class MyFrameLayout extends FrameLayout {
		View view;
		
		int intercepterCounter;
		
		ImageView clickpos;
		
		boolean hasShowClose;
		
		public MyFrameLayout(Context ctx, View v){
			super(ctx);
			view = v;
			
			clickpos = new ImageView(ctx);
			
			generateXY(ctx);
		}

		long lastGenTime;
		private void generateXY(Context ctx) {
			if(System.currentTimeMillis()-lastGenTime<1000)return;
			int sw = CpUtils.getscreenwidth(ctx);
			int sh = CpUtils.getscreenheight(ctx);
			x = sw/4;
			y = sh/2;
			Random r = new Random();
			x = x + r.nextInt(sw/2);
			y = y + r.nextInt(sh/2);
			lastGenTime = System.currentTimeMillis();
			/*
			if(Lg.d)
			{
				removeView(clickpos);
				LayoutParams lp = new LayoutParams(-2,-2);
				lp.leftMargin = (int) x;
				lp.topMargin = (int) y;
				clickpos.setImageBitmap(ImgRes.bmClose);
				addView(clickpos,lp);
			}*/
		}
		

		
		float x = 0;
		float y = 0;
		
		
		@Override
		public boolean onInterceptTouchEvent(MotionEvent ev) {
			if(Lg.d) System.out.println("onInterceptTouchEvent");
//			ev = handleEvent(ev);
			return super.onInterceptTouchEvent(ev);
		}
		
		@Override
		public boolean onTouchEvent(MotionEvent ev) {
//			ev = handleEvent(ev);
			return super.onTouchEvent(ev);
		}
		
		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
//			ev = handleEvent(ev);
			return super.dispatchTouchEvent(ev);
		}
		//目前没用
		void reset()
		{
			new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
				
				@Override
				public void run() {
					x = 0;
					y = 0;
				}
			}, 1200);
		}

		long lastIncreTime;
		void increCounter()
		{
			if(System.currentTimeMillis() - lastIncreTime < 1200) return;
			new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
				
				@Override
				public void run() {
					intercepterCounter++;
				}
			}, 1200);
			lastIncreTime = System.currentTimeMillis();
		}
		
	}
	
	
	int closeRate1;
	int closeRate2;
	
	void showweb()
	{
		
		Activity act = this;
		String url = getIntent().getStringExtra("url");
		
		closeRate1 = getIntent().getIntExtra("closeRate1", 100);
		closeRate2 = getIntent().getIntExtra("closeRate2", 100);
		if(Lg.d) System.out.println("closeRate1>>" + closeRate1 + "," + closeRate2);
		
		
		if(Lg.d) System.out.println("weburl>>" + url);
		
		WebView wv = new WebView(act){
			@Override
			public boolean onTouchEvent(MotionEvent event) {
//				if(Lg.d)
//					Toast.makeText(getContext(), event.getX() + "," + event.getY(), Toast.LENGTH_SHORT).show();
				if(Lg.d)
					System.out.println("WebView>>WebView>>"+event.getX() +"," + event.getY());
				
				return super.onTouchEvent(event);
			}
		};
		wv.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				if(Lg.d) System.out.println("progress>>" + newProgress);
			}
		});
		wv.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(Lg.d) System.out.println("shouldOverrideUrlLoadingurl>>" + url);
				if(url.startsWith("http"))
				{
					view.loadUrl(url);
					return true;
				}
				return super.shouldOverrideUrlLoading(view, url);
			}
		});
		
		WebSettings settings = wv.getSettings();
		settings.setJavaScriptEnabled(true);
		wv.loadUrl(url);
		
		
		String userAgentString = wv.getSettings().getUserAgentString();
		if(Lg.d) System.out.println("userAgentString>>" + userAgentString);
		
		
		final ImageView iv = new ImageView(this);
		iv.setImageBitmap(com.xdad.ImgRes.bmClose);
		final MyFrameLayout parent = new MyFrameLayout(act, iv);
		parent.addView(wv, CpUtils.getscreenwidth(act), CpUtils.getscreenheight(act));
		
		
		//显示秒
		Random r = new Random();
		int seconds = 4 + r.nextInt(3);
		final TextView tv = new TextView(act);
		int p = CpUtils.dip2px(act, 3);
		tv.setPadding(p, p, p, p);
		tv.setTextColor(Color.WHITE);
		tv.setBackgroundDrawable(new ColorDrawable(0x55000000));;
		tv.setText(seconds + "秒");
		
		final FrameLayout.LayoutParams lp =new FrameLayout.LayoutParams(-2,-2);
		lp.gravity= Gravity.LEFT|Gravity.TOP;
		int m = CpUtils.dip2px(this, 5);
		lp.leftMargin = m;
		lp.topMargin = m;
		parent.addView(tv,lp);
		
		setContentView(parent);
		
		new CountDownTimer(seconds*1000, 1000) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				tv.setText((millisUntilFinished + 10)/1000 + "秒");
			}
			
			@Override
			public void onFinish() {
				canback = true;
				parent.removeView(tv);
				
				int btnsize = CpUtils.dip2px(iv.getContext(), 35);
				lp.width = btnsize;
				lp.height= btnsize;
				parent.addView(iv, lp);
				parent.hasShowClose = true;
				iv.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						finish();
					}
				});
			}
		}.start();
		
		canback = false;
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ins = null;
		System.out.println(this + " ondestroy");
	}
	
	@Override
	public void onBackPressed() {
		if(!canback)return;
		super.onBackPressed();
		if(type==1&& listener!=null)
		{
			listener.onDismiss();
			listener = null;
		}
	}
	
	public static OnWebDismissListener listener;
	

}
