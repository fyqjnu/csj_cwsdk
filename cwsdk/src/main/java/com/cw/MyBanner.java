package com.cw;

import java.io.File;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.ViewFlipper;

import com.cw.download.DownListener;
import com.cw.download.DownloadManager;
import com.cw.download.DownloadTask;
import com.cw.download.FileUtil;
import com.cw.download.SimpleDownApkListenerImpl;
import com.cw.entity.AdBody;
import com.cw.entity.AdReportTracker;
import com.cw.http.HttpManager;
import com.cw.http.TrackUtil;
import com.cw.util.Constants;
import com.cw.util.CpUtils;
import com.cw.util.Lg;

/**
 * banner控件 显示掌乐广告
 * @author Administrator
 *
 */
public class MyBanner extends FrameLayout implements DownListener, View.OnClickListener {
	
	public interface MyBannerListener{
		void onshow();
		void onclose(View v);
		void onfail(View v);
	}
	
	MyBannerListener listener;
	
	AdBody[] ads;
	
	public MyBanner(Context context) {
		super(context);
		getad()	;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		TrackUtil.getclicklocation(MyBanner.this, event);
		return super.onTouchEvent(event);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		TrackUtil.getclicklocation(MyBanner.this, ev);
		return super.onInterceptTouchEvent(ev);
	}
	
	private void getad() {
		new Thread(){
			public void run() {
				AdBody[] infos = HttpManager.getadbody(1);
				if(Lg.d) Lg.d("ad>>" + infos);
				if(infos!=null&&infos.length>0)
				{
					ads = infos;
					boolean needdown = false;
					for(AdBody ad:ads)
					{
						needdown = true;
						downloadpic(ad);
					}
//					if(!needdown)
//					{
//						showbanner();
//					}
				}
				else
				{
					listener.onfail(MyBanner.this);
				}
			};
		}.start();
	}
	
	int currentshow = 0;

	private ViewFlipper vf;
	
	void showbannerproxy()
	{
		if(Lg.d) Lg.d("showbannerproxy");
		vf = new ViewFlipper(getContext());
		View item = null;
		for(AdBody ad:ads)
		{
				//下载app
				ImageView iv = new ImageView(getContext());
				iv.setScaleType(ScaleType.FIT_XY);
				String url = ad.screenUrls.split(";")[0];
				File f = FileUtil.getpicfile(url);
				Bitmap bm = BitmapFactory.decodeFile(f.getPath());
				iv.setImageBitmap(bm);
				item = iv;
			/*else if(ad.type==2)
			{
				//wap类型
				WebView wv = new WebView(getContext());
				wv.setWebChromeClient(new WebChromeClient());
				wv.setWebViewClient(new WebViewClient());
				wv.loadUrl(ad.url);
				item = wv;
			}*/
			
			item.setTag(ad);
			item.setOnClickListener(this);
			LayoutParams lp = new LayoutParams(-1, -1);
			vf.addView(item,lp);
			
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
		
		
		//添加广告父
		addView(vf, -1, -1);
		
		ImageView close = new ImageView(getContext());
		//添加关闭按钮
		int w = CpUtils.dip2px(getContext(), 20);
		LayoutParams lpclose = new LayoutParams(w, w);
		addView(close, lpclose);
		close.setImageBitmap(ImgRes.bmClose);
		close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				listener.onclose(MyBanner.this);
			}
		});
		
		ImageView logo = new ImageView(getContext());
		logo.setImageBitmap(ImgRes.bmLogo);
		LayoutParams lplogo = new LayoutParams(-2, -2);
		lplogo.gravity= Gravity.RIGHT|Gravity.BOTTOM;
		addView(logo, lplogo);
		
		currentshow = 0;
		showwhich();
		
		if(listener!=null)
		{
			listener.onshow();
		}
		
		
		
	}
	
	void showbanner()
	{
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			
			@Override
			public void run() {
				showbannerproxy();
			}
		});
	}
	
	void showwhich()
	{
		if(CpUtils.getTopActivity()==null)return ;
		if(vf.getChildCount()<2)return;
		int which = currentshow%vf.getChildCount();
		for(int i=0;i<vf.getChildCount();i++)
		{
			View v = vf.getChildAt(i);
			if(i==which)
			{
				v.setVisibility(View.VISIBLE);
			}
			else
			{
				v.setVisibility(View.INVISIBLE);
			}
		}
		
		currentshow++;
		
		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
			
			@Override
			public void run() {
				showwhich();
			}
		}, 4000);
	}
	
	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		if(visibility!=View.VISIBLE)
		{
		}
		super.onWindowVisibilityChanged(visibility);
	}
	
	void downloadpic(AdBody ad)
	{
		String url = ad.screenUrls.split(";")[0];
		File f = FileUtil.getpicfile(url);
		DownloadManager.downloadpic(url, f, this);
		if(Lg.d) System.out.println("download pic>>" + url);
	}
	
	
	void feedbackstate(final int id, final int state)
	{
		new Thread(){
			public void run() {
				//banner类型为1
				HttpManager.feedbackstate(id, state, 1);
			};
		}.start();
	}


	@Override
	public void ondownloading(DownloadTask dt, int progress) {
		
	}

	
	boolean hasshow;
	
	@Override
	public void onstatechanged(DownloadTask dt, int state) {
		if(Lg.d) System.out.println("banner pic download>>" + state);
		if(hasshow) return;
		if(state==DownloadTask.state_complete)
		{
			for(AdBody ad:ads)
			{
				if(ad.type!=1)continue;
				
				String url = ad.screenUrls.split(";")[0];
				File f = FileUtil.getpicfile(url);
				if(!f.exists())return ;
			}
			
			//图片全部下载完成
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				
				@Override
				public void run() {
					showbanner();
				}
			});
			
			hasshow = true;
		}
		else if(state==DownloadTask.state_failed)
		{
			listener.onfail(MyBanner.this);
		}
	}

	@Override
	public void onClick(View v) {
		//点击广告
		AdBody ad = (AdBody) v.getTag();
		if(ad.type == 1)
		{
			if(CpUtils.ispackageinstalled(getContext(), ad.apkPackage))
			{
				CpUtils.openapp(getContext(), ad.apkPackage);
				
				//返回状态
				int state = Constants.CP_STATE_DETAIL;
				feedbackstate(ad.advertId, state);
			}
			else
			{
				//下载
				File f = FileUtil.getapkfile(ad.url);
				if(f.exists())
				{
					CpUtils.installapk(getContext(), f);
				}
				else 
				{
					Toast.makeText(getContext(), "开始下载" + ad.apkName, 0).show();
					//下载
					DownListener dl = new SimpleDownApkListenerImpl(getContext(), ad, 1);
					DownloadManager.downloadapk(ad.url, f, dl);
				}
			}
			
			//玩咖
			if(ad.reportVO!=null)
			{
				List<AdReportTracker> trackers = ad.reportVO.getClktrackers();
				TrackUtil.track(trackers);
			}
			else
			{
				//track
				TrackUtil.track(ad.clickTrackingUrl);
			}
			
		}
		else
		{
			//web
			/*feedbackstate(ad.advertId, Constants.CP_STATE_DETAIL);
			CpUtils.openweb(getContext(), ad.url);*/
			
			CpUtils.openwebwithwebview(getContext(), ad.url, new WebTrackImpl(ad, 1), ad.closeRate1, ad.closeRate2);
		}
		
		
	}

}
