package com.cw;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cw.MyBanner.MyBannerListener;
import com.cw.http.HttpManager;
import com.cw.util.CpUtils;
import com.cw.util.Lg;
import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.BannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.comm.util.AdError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BannerManager {
	
	final static String gdt = "1";
	final static String api = "2";
	final static String baidu = "3";
	
	
	long lastrequesttime = 0;
	long gdtlastshowtime = 0;
	long baidulastshowtime = 0;
	
	
	
	int bannermargin = 0;
	
	static 
	{
		final Handler h = new Handler(Looper.getMainLooper());
		final int delay = 15 * 1000;
		h.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Activity act = CpUtils.getTopActivity();
				if(act !=null)
				{
					if(ins!=null) ins.notifycurrentactivity(act);
				}
				h.postDelayed(this, delay);
				
			}
		}, delay);
	}
	
	Context ctx;
	private String gdt_bannerid;
	private String gdt_appid;
	private String bd_bannerid;
	private String bd_appid;
	
	
	Handler handler = new Handler(Looper.getMainLooper());
	
	//1表示 gdt, 3表示 百度
	String requestorder = "2,1,3";
	List<String> requestqueue = new ArrayList<String>();
	
	int zouqi = 120*1000;
	
	HashMap<Activity, BannerTask> mapactbanner = new HashMap<Activity, BannerManager.BannerTask>();
	
	private BannerManager(Context ctx)
	{
		this.ctx = ctx;
		
	}
	
	static BannerManager ins;
	
	public static BannerManager getinstance(Context ctx)
	{
		if (ins==null)
		{
			ins = new BannerManager(ctx);
		}
		return ins;
	}
	
	public void setbannermargin(int margin)
	{
		bannermargin = margin;
		if(Lg.d)Lg.d("bannermargin>>" + bannermargin);
	}
	
	public void setrequestorder(String order)
	{
		if(TextUtils.isEmpty(order))return;
		requestorder = order;
		
//		if(Lg.d) requestorder = "2,1,3";
	}
	
	public void setgdtinfo(String appid, String bannerid)
	{
		this.gdt_appid = appid;
		this.gdt_bannerid = bannerid;
	}
	
	public void setbdinfo(String appid, String bannerid)
	{
		this.bd_appid = appid;
		this.bd_bannerid = bannerid;
	}
	
	public void start()
	{
		
		Activity topActivity = CpUtils.getTopActivity();
		if(topActivity==null)
		{
			return;
		}
		
		BannerTask task = mapactbanner.get(topActivity);
		if(task ==null)
		{
			task = new BannerTask(topActivity, requestorder);
			mapactbanner.put(topActivity, task);
		}
		task.start();
	}
	
	
	
	void notifycurrentactivity(Activity act)
	{
		if(Lg.d) Lg.d("notifycurrentactivity>>----"+act);
		if(mapactbanner.get(act)==null)
		{
			BannerTask t = new BannerTask(act, requestorder);
			mapactbanner.put(act, t);
			t.start();
		}
	}
	
	public BannerTask getCurrentTask()
	{
		Activity topActivity = CpUtils.getTopActivity();
		if(topActivity==null)
		{
			return null;
		}
		
		BannerTask task = mapactbanner.get(topActivity);
		return task;
	}
	

	
	class BannerTask implements MyBannerListener {
		
		Activity act;
		
		String requestorder = "1,3,2";
		List<String> requestqueue = new ArrayList<String>();
		BannerParent parent;

		private PopupWindow pw;
		
		boolean isshow;

		private int w;

		private int h;

		private FrameLayout fl;
		
		//默认底部
		int position = 1;
		
		int getposition()
		{
			try {
				position = Integer.valueOf(CpManager.banner_position);
			} catch (Exception e) {
			}
			return position;
		}
		

		private int sw;
		Runnable oneminutecheck = new Runnable() {
			
			@Override
			public void run() {
				resetqueue();
				requestbanner();
			}
		};		
		
		BannerTask(Activity act, String order) {
			this.act = act;
			this.requestorder = order;
			parent  = new BannerParent(act);
			
			fl = (FrameLayout) act.findViewById(android.R.id.content);
			sw = CpUtils.getscreenwidth(act);
			int sh = CpUtils.getscreenheight(act);
			
			w = Math.min(sw, sh);
			h = (int) (w * 2.9f/20);
			initpw(act);
//			pw.setContentView(parent);
//			pw.setFocusable(true);
//			pw.setOutsideTouchable(true);
			
			FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(w, h);
			int pos = getposition();
			if(pos==0)
			{
				lp.gravity = Gravity.BOTTOM |Gravity.LEFT;
			}
			else 
			{
				lp.gravity = Gravity.TOP |Gravity.LEFT;
			}
			lp.leftMargin = (sw-w)/2;
//			fl.addView(parent,lp);
		}


		private void initpw(Activity act) {
			pw = new PopupWindow(act);
			pw.setWidth(w);
			pw.setHeight(h);
			pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		}
		
		
		void handleonvisibilitychange(View v, int visibility)
		{
			if(visibility==View.VISIBLE)
			{
				if(Lg.d) Lg.d("可见" + v);
			}
			else 
			{
				boolean isDestroyed = false;
				try
				{
					 isDestroyed = (Boolean) act.getClass().getMethod("isDestroyed").invoke(act);
				}
				catch(Exception e)
				{
				}
				if(isDestroyed || act.isFinishing())
				{
					if(Lg.d) Lg.d("不可见"+ v);
					parent.removeAllViews();
					pw.dismiss();
				}
			}
		}
		
		
		void start()
		{
			if(Lg.d) Lg.d("banner start---------------");
			if(isshow) return;
			resetqueue();
			requestbanner();
		}
		
		void ongdtsuccess()
		{
			gdtlastshowtime = System.currentTimeMillis();
			onshowsuccess();
		}
		
		void onapifail(View v)
		{
			removeview(v);
			requestbanner();
		}
		
		void ongdtfail(View gdt)
		{
			if(gdt!=null)
			{
				removeview(gdt);
			}
			requestbanner();
		}
		
		public void onbaidusuccess()
		{
			baidulastshowtime = System.currentTimeMillis();
			onshowsuccess();
		}
		
		public void onbaidufail(View bd)
		{
			if(bd!=null)
				removeview(bd);
			requestbanner();
		}

		
		void clearCallback()
		{
			handler.removeCallbacks(next);
			handler.removeCallbacks(oneminutecheck);
		}
		
		void onshowsuccess()
		{
			if(Lg.d) System.out.println("banner show success");
			isshow = true;
			fl.requestLayout();
			fl.invalidate();
			
			clearCallback();
			
			/*
			fl.removeView(parent);
			pw.dismiss();
			initpw(act);
			pw.setContentView(parent);
			int x = (sw-w)/2;
			int pos = getposition();
			if(pos==0)
			{
				pw.showAtLocation(fl, Gravity.BOTTOM|Gravity.LEFT, x, 0);
			}
			else
			{
				int y = CpUtils.dip2px(act, 20);
				pw.showAtLocation(fl, Gravity.TOP|Gravity.LEFT, x, y);
			}
			*/
		}
		
		//点击关闭
		void removeview(View v)
		{
			parent.removeView(v);
			pw.dismiss();
		}
		
		
		void showbannerproxy(View v)
		{
//			fl.removeView(parent);
			System.out.println("showbannerproxy----------");
			
			if(pw!=null)
			{
				pw.dismiss();
			}

			initpw(act);
			
			int pos = getposition();
//			FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(w, h);
//			if(pos==0)
//			{
//				lp.gravity = Gravity.BOTTOM |Gravity.LEFT;
//			}
//			else 
//			{
//				lp.gravity = Gravity.TOP |Gravity.LEFT;
//			}
//			
//			lp.leftMargin = (sw-w)/2;
//			if(bannermargin>0)
//			{
//				lp.bottomMargin = CpUtils.dip2px(act, bannermargin);
//			}
			if(parent.getParent()!=null)
			{
				ViewGroup vp = (ViewGroup) parent.getParent();
				vp.removeView(parent);
				
			}
			if(Lg.d) System.out.println("parent >>" +  parent.getParent());
//			fl.addView(parent,lp);
			
			parent.removeAllViews();
			parent.addView(v, w, h);
			
			pw.update();
			pw.setContentView(parent);
			
			try
			{
				int x = (sw-w)/2;
				int y = 0;
				//pos == 0
				if(bannermargin<0)
				{
					y = CpUtils.dip2px(act, Math.abs(bannermargin));
					pw.showAtLocation(fl, Gravity.BOTTOM|Gravity.LEFT, x, y);
				}
				else
				{
					y = CpUtils.dip2px(act, 20 + Math.abs(bannermargin));
					pw.showAtLocation(fl, Gravity.TOP|Gravity.LEFT, x, y);
				}
			
			}catch(Exception e){}
			
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					fl.invalidate();
					fl.requestLayout();
				}
			});
			
			if(Lg.d) Lg.d("parent child size>>" + parent.getChildCount());
		}
		
		
		void doshowbanner(final View v)
		{
			handler.post(new Runnable() {
				public void run() {
					showbannerproxy(v);
				}
			});
		
		}
		
		void feedbackgdt(final int state)
		{
			new Thread(){
				public void run() {
					//id 为 8
					HttpManager.feedbackstate(8, state, 1);
				};
			}.start();
		}
		
		void feedbackbaidu(final int state)
		{
			new Thread(){
				public void run() {
					//id 为 9
					HttpManager.feedbackstate(9, state, 1);
				};
			}.start();
		}
		
		void requestapi()
		{
			MyBanner b = new MyBanner(act);
			b.listener = this;
			doshowbanner(b);
		}
		
		void requestgdt()
		{
			if(Lg.d) Lg.d("banner gdt->id" + gdt_appid);
			if(TextUtils.isEmpty(gdt_appid)){
				ongdtfail(null);
				return ;
			}
			
			final BannerView gdt = new BannerView(act, ADSize.BANNER, gdt_appid, gdt_bannerid);
			gdt.setShowClose(true);
			gdt.setRefresh(30);
			gdt.setADListener(new BannerADListener() {
				
				@Override
				public void onNoAD(AdError arg0) {
					if(Lg.d) Lg.d("banner noad>" +arg0.getErrorMsg());
					ongdtfail(gdt);
				}
				@Override
				public void onADReceiv() {
					if(Lg.d) Lg.d("gdt banner receive>>" + gdt);
					feedbackgdt(-1);
				}
				
				@Override
				public void onADOpenOverlay() {
				}
				
				@Override
				public void onADLeftApplication() {
				}
				
				@Override
				public void onADExposure() {
					//展示状态
					feedbackgdt(0);
					ongdtsuccess();
					if(Lg.d) Lg.d("onADExposure--------------");
				}
				
				@Override
				public void onADClosed() {
					removeview(gdt);
					isshow = false;
					fornext();
				}
				
				@Override
				public void onADCloseOverlay() {
				}
				
				@Override
				public void onADClicked() {
					feedbackgdt(1);
				}
			});
			
			doshowbanner(gdt);
			gdt.loadAD();
			feedbackgdt(-2);
		}
		
		void requestbaidu()
		{
			/*if(Lg.d) Lg.d("requestbaidu banner>" + bd_appid + "," + bd_bannerid);
			if(TextUtils.isEmpty(bd_appid))
			{
				onbaidufail(null);
				return;
			}
			
			AdView.setAppSid(act, bd_appid);
			
			final AdView baidu = new AdView(act, bd_bannerid);
			baidu.setListener(new AdViewListener() {
				
				@Override
				public void onAdSwitch() {
					
				}
				
				@Override
				public void onAdShow(JSONObject arg0) {
					if(Lg.d) Lg.d("baidu banner show>" + arg0);
					feedbackbaidu(0);
					onbaidusuccess();
					
				}
				
				@Override
				public void onAdReady(AdView arg0) {
					feedbackbaidu(-1);
					if(Lg.d) Lg.d("baidu banner ready");
				}
				
				@Override
				public void onAdFailed(String arg0) {
					onbaidufail(baidu);
					if(Lg.d) Lg.d("baidu banner fail>" + arg0);
				}
				
				@Override
				public void onAdClose(JSONObject arg0) {
					removeview(baidu);
					isshow = false;
					fornext();
				}
				
				@Override
				public void onAdClick(JSONObject arg0) {
					feedbackbaidu(1);
				}
			});*/
			
			if(TextUtils.isEmpty(bd_appid)||TextUtils.isEmpty(bd_bannerid))
			{
				onbaidufail(null);
				return;
			}
			
			System.out.println("请求穿山甲");
			FrameLayout baidu = new FrameLayout(act);
			TextView tv = new TextView(act);
			tv.setText(" ");
			baidu.addView(tv);
//			baidu.setBackgroundColor(Color.RED);
			API2CSJ.showCsjBanner(act, baidu, bd_appid, bd_bannerid);
			
			doshowbanner(baidu);
			//请求状态-2
			feedbackbaidu(-2);
		}
		
		
		void setoneminutecheck(){
			clearCallback();
			handler.postDelayed(oneminutecheck, 60*1000);
		}
		
		void requestbanner()
		{
			if(isshow)return;
			
			if(requestqueue.size()==0)
			{
				fornext();
				return ;
			}
			
			if(System.currentTimeMillis() - lastrequesttime <59 *1000)
			{
				if(requestqueue.size() == requestorder.split(",").length)return;
			}
			lastrequesttime = System.currentTimeMillis();
			
			if(Lg.d ) Lg.d("banner request>>" + requestqueue);
			String which = requestqueue.remove(0);
			if(Lg.d ) Lg.d("banner request afterremove>>" + requestqueue);
			if(gdt.equals(which))
			{
				requestgdt();
			}
			else if(baidu.equals(which))
			{
				requestbaidu();
			}
			else if(api.equals(which))
			{
				requestapi();
			}
			else 
			{
				requestbanner();
			}
			
		}
		
		void resetqueue()
		{
			requestqueue.clear();
			requestqueue.addAll(Arrays.asList(requestorder.split(",")));
			
			if(gdt_appid==null) requestqueue.remove(gdt);
			if(bd_appid==null) requestqueue.remove(baidu);
//			requestqueue.remove(api);
			
			//改变顺序
			if(lastrequesttime>0)
			{
				
				boolean change = false;
				String first = requestqueue.get(0);
				if(gdt.equals(first) && lastrequesttime > gdtlastshowtime)
				{
					change =true;
				}
				else if(baidu.equals(first) && lastrequesttime > baidulastshowtime)
				{
					change =true;
				}
				
				if(change)
				{
					requestqueue.remove(0);
					requestqueue.add(first);
					StringBuilder sb = new StringBuilder();
					for(String s:requestqueue)
					{
						sb.append(s).append(",");
					}
					String t = sb.toString();
					requestorder = t.substring(0, t.length()-1);
				}	
			}
		}
		
		
		
		Runnable next = new Runnable() {
			
			@Override
			public void run() {
				resetqueue();
				if(requestqueue.size()==0)return;
				requestbanner();
			}
		};
		
		public void fornext()
		{
			clearCallback();
			handler.postDelayed(next, zouqi);
		}
		
		
		class BannerParent extends RelativeLayout {
			public BannerParent(Context context) {
				super(context);
//				setBackgroundColor(Color.BLUE);
			}
			
			@Override
			protected void onWindowVisibilityChanged(int visibility) {
				try
				{
					handleonvisibilitychange(this, visibility);
				}
				catch(Exception e){}
				super.onWindowVisibilityChanged(visibility);
			}
			
		}


		@Override
		public void onshow() {
			//api banner显示
			if(Lg.d) Lg.d("api success");
			onshowsuccess();
		}


		@Override
		public void onclose(View v) {
			if(Lg.d) Lg.d("api banner close");
			removeview(v);
			isshow = false;
			fornext();
		}


		@Override
		public void onfail(final View v) {
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				
				@Override
				public void run() {
					onapifail(v);
				}
			});
		}
		
	}
	
	
	
	void test()
	{
	}
	
	
}
