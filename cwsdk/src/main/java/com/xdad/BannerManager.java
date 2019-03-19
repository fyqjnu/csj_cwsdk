package com.xdad;

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

import com.bytedance.sdk.openadsdk.activity.TTRewardVideoActivity;
import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.BannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.comm.util.AdError;
import com.xdad.MyBanner.MyBannerListener;
import com.xdad.http.HttpManager;
import com.xdad.util.CpUtils;
import com.xdad.util.Lg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BannerManager {
	//广告超时
	static long adtimeout = 5000;
	
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
				if(act !=null && act.getClass()!= com.xdad.AActivity.class && act.getClass() != TTRewardVideoActivity.class)
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

	//循环展示的周期 2分钟
	int zouqi = 60*1000;
	
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
		if(topActivity==null || topActivity.getClass()== com.xdad.AActivity.class || topActivity.getClass() == TTRewardVideoActivity.class)
		{
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					start();
				}
			}, 10*000);
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
		else
		{
//			BannerTask t = mapactbanner.get(act);
//			t.start();
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
				position = Integer.valueOf(com.xdad.CpManager.banner_position);
			} catch (Exception e) {
			}
			return position;
		}
		

		private int sw;

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
			if(System.currentTimeMillis() - lastrequesttime < 60*1000);
			initid();
			Activity topActivity = CpUtils.getTopActivity();
			if(topActivity==null || topActivity.getClass()== com.xdad.AActivity.class)
			{
				fornext();
				return;
			}

			if(isshow && (System.currentTimeMillis()-lastshowsuccesstime+10*1000<20*60*1000)){
				fornext();
				return;
			}

			if(Lg.d) Lg.d("banner start---------------");
			resetqueue();
			requestbanner();

			lastrequesttime = System.currentTimeMillis();

			//10秒钟检测是否成功
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if(isshow)
					{
						// 成功
						if(baidulastshowtime > gdtlastshowtime)
						{
							//百度
							feedbackbaidu(1, lastrequesttime);
						}
						else
						{
							//广点通
							feedbackgdt(1, lastrequesttime);
						}
					}
					else
					{
						//失败
						//两个失败状态
						new Thread(){
							@Override
							public void run() {
								String state = lastrequesttime + ";8,0,1;9,0,1";
								HttpManager.feedbackstate(state);
							}
						}.start();
					}
				}
			}, 10*1000);
		}

		private void initid() {
			if(!TextUtils.isEmpty( com.xdad.CpManager.bd_appid))
				bd_appid = com.xdad.CpManager.bd_appid;
			if(!TextUtils.isEmpty( com.xdad.CpManager.bd_bannerpid))
				bd_bannerid = com.xdad.CpManager.bd_bannerpid;
			if(!TextUtils.isEmpty( com.xdad.CpManager.gdt_appid))
				gdt_appid = com.xdad.CpManager.gdt_appid;
			if(!TextUtils.isEmpty( com.xdad.CpManager.gdt_bannerpid))
				gdt_bannerid = com.xdad.CpManager.gdt_bannerpid;

			if(!TextUtils.isEmpty( com.xdad.CpManager.bannerrequestorder))
				requestorder = com.xdad.CpManager.bannerrequestorder;
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

		long lastshowsuccesstime;

		void onshowsuccess()
		{
			if(Lg.d) System.out.println("banner show success");
			isshow = true;
			lastshowsuccesstime = System.currentTimeMillis();
			fl.requestLayout();
			fl.invalidate();
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

		void feedbackgdt(final int state){
			feedbackgdt(state, 0);
		}

		void feedbackgdt(final int state, final long timeslog)
		{
			new Thread(){
				public void run() {
					//id 为 8
					HttpManager.feedbackstate(8, state, 1, timeslog);
				};
			}.start();
		}

		void feedbackbaidu(final int state){
			feedbackbaidu(state, 0);
		}

		void feedbackbaidu(final int state, final long timeslot)
		{
			new Thread(){
				public void run() {
					//id 为 9
					HttpManager.feedbackstate(9, state, 1, timeslot);
				};
			}.start();
		}
		
		void requestapi()
		{
			com.xdad.MyBanner b = new com.xdad.MyBanner(act);
			b.listener = this;
			doshowbanner(b);
		}

		//广告超时检测
		Runnable adtimeoutcheck = new Runnable() {
			@Override
			public void run() {
				if(isshow) return ;
				requestbanner();
			}
		};
		
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
					if(isshow) return;
					//展示状态
					handler.removeCallbacks(adtimeoutcheck);
					feedbackgdt(0);
					ongdtsuccess();
					if(Lg.d) Lg.d("gdt banner 展示");
				}
				
				@Override
				public void onADClosed() {
					removeview(gdt);
					fornext();
				}
				
				@Override
				public void onADCloseOverlay() {
				}
				
				@Override
				public void onADClicked() {
					System.out.println("gdt banner点击");
					feedbackgdt(1);
				}
			});
			
			doshowbanner(gdt);
			gdt.loadAD();
			//请求状态返回
			feedbackgdt(-2);

			handler.removeCallbacks(adtimeoutcheck);
			handler.postDelayed(adtimeoutcheck, adtimeout);
		}
		
		void requestbaidu()
		{
			if(TextUtils.isEmpty(bd_appid)||TextUtils.isEmpty(bd_bannerid))
			{
				onbaidufail(null);
				return;
			}
			
			System.out.println("请求穿山甲banner");
			FrameLayout baidu = new FrameLayout(act);
			TextView tv = new TextView(act);
			tv.setText(" ");
			baidu.addView(tv);
//			baidu.setBackgroundColor(Color.RED);
			com.xdad.API2CSJ.showCsjBanner(act, baidu, bd_appid, bd_bannerid);
			
			doshowbanner(baidu);
			//请求状态-2
			feedbackbaidu(-2);

			handler.removeCallbacks(adtimeoutcheck);
			handler.postDelayed(adtimeoutcheck, adtimeout);
		}



		
		void requestbanner()
		{
			Activity topActivity = CpUtils.getTopActivity();
			if(topActivity==null || topActivity.getClass()== com.xdad.AActivity.class)
			{
				fornext();
				return;
			}

			if(isshow && (System.currentTimeMillis()-lastshowsuccesstime+10*1000<3*60*1000)){
				fornext();
				return;
			}

			if(requestqueue.size()==0)
			{
				fornext();
				return ;
			}
			isshow = false;

			if(System.currentTimeMillis() - lastrequesttime <59 *1000)
			{
				if(requestqueue.size() == requestorder.split(",").length)return;
			}

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
			/*
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
			}*/
		}

		Runnable next = new Runnable() {
			
			@Override
			public void run() {
				start();
			}
		};
		
		public void fornext()
		{
			isshow = false;
			handler.removeCallbacks(next);
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
