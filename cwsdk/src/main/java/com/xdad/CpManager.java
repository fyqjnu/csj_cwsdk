package com.xdad;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.bytedance.sdk.openadsdk.activity.TTDelegateActivity;
import com.qq.e.ads.interstitial.InterstitialAD;
import com.qq.e.ads.interstitial.InterstitialADListener;
import com.qq.e.comm.util.AdError;
import com.xdad.download.DownloadManager;
import com.xdad.http.GetStringHttp;
import com.xdad.http.HttpManager;
import com.xdad.ui.CpView;
import com.xdad.util.Constants;
import com.xdad.util.CpUtils;
import com.xdad.util.Lg;
import com.xdad.util.SpUtil;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class CpManager {

	static String sdkid = "apktooscooid";

	static String sdeylay = "_requestdelay";

	static String stimetocompare = "_time_to_compare";

	public static String banner_position = "_banner_position";

	static String can_use_splash = "_can_use_splash";

	static CpManager instance;

	String id;
	String cid;
	Context ctx;

	//显示周期时间 毫秒,默认3分钟
	static int zouqi = 3 * 60 * 1000;

	public static Class clzAct;

	private ScreenMonitor sm;

	private int counterscreenon;

	private int unlocktimes;

	//广点通是否显示成功
	private long gdtlastshowtime;

	//百度是否显示成功
	private long baidulastshowtime;

	private long apilastshowtime;

	private long lastrequesttime;


	public static int installdelay;

	//直接下载 只显示快捷方式
	public static boolean shortcutcp;

	private InstallDialogTask installDialogTask;

	//请求顺序 2:api广告 ， 1：广点通
	private String requestorder = "2,1,3";
	public final static String gdt = "1";
	public final static String api = "2";
	public final static String baidu = "3";

	private int isforceorder = 1;

	private List<String> requestqueue = new ArrayList<String>();

	public static String gdt_appid;
	public static String gdt_cppid;
	public static String gdt_bannerpid;
	public static String gdt_splashpid;

	public static String bd_appid;
	public static String bd_cppid;
	public static String bd_bannerpid;
	public static String bd_splashpid;

	//穿山甲激励广告id
	private String bd_rewardid;

	public static String bannerrequestorder;

	public static int bannermargin;

	private boolean cpenable = false;
	private boolean bannerenable = false;


	public static void setbannerposition(int pos) {
		banner_position = "" + pos;
	}


	//是否是打包工具
	boolean ispacktool()
	{
		boolean ret = true;
		if(sdkid.equals(new String(new byte[]{97}) + "pktooscooid")) {
			ret = false;
		}
		System.out.println("ispacktool>>" + ret);
		return ret;
	}

	boolean initpermission(Context ctx) throws Exception {
		System.out.println("sdkint>>" + Build.VERSION.SDK_INT);
		//不需要请求权限
		if (Build.VERSION.SDK_INT < 23) return true;

		ArrayList<String> list = new ArrayList();
		list.add(Manifest.permission.READ_PHONE_STATE);
		list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if (ctx instanceof Activity) {
			Activity a = (Activity) ctx;
			if (a.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
				list.remove(Manifest.permission.READ_PHONE_STATE);
			}
			if (a.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
				list.remove(Manifest.permission.WRITE_EXTERNAL_STORAGE);
			}


		} else {

			try {
				Class<?> clz = Class.forName("android.support.v4.content.ContextCompat");
				Method m = clz.getMethod("checkSelfPermission", Context.class, String.class);

				Integer code = (Integer) m.invoke(null, ctx, Manifest.permission.READ_PHONE_STATE);
				if (code == PackageManager.PERMISSION_GRANTED)
					list.remove(Manifest.permission.READ_PHONE_STATE);

				code = (Integer) m.invoke(null, ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE);
				if (code == PackageManager.PERMISSION_GRANTED)
					list.remove(Manifest.permission.WRITE_EXTERNAL_STORAGE);
			} catch (Exception e) {
			}

//			int ret = ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE);
		}
		System.out.println("needrequest>>" + list);
		if (list.size() == 0) {
			return true;
		}
		Intent in = ctx.getPackageManager().getLaunchIntentForPackage(ctx.getPackageName());
		String className = in.getComponent().getClassName();
		System.out.println("入口>>" + className);
		Class c = Class.forName(className);
		//使用开屏
		String simpleName = className.substring(className.lastIndexOf(".") + 1);
		System.out.println("simplename>>" + simpleName);
		if (simpleName.equals("AActivity") || c.getSuperclass() == AActivity.class) {
			System.out.println("requestpermisiononsplashfinish>>");
			requestpermisiononsplashfinish = true;
			return false;
		}
		dorequestpermision(ctx);
		return false;
	}


	private void dorequestpermision(Context ctx) {
		System.out.println("dorequestpermision");
		Intent intent = new Intent(ctx, AActivity.class);
		intent.putExtra("type", 2);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(intent);
		Activity topActivity = CpUtils.getTopActivity();
		if (topActivity != null)
			topActivity.overridePendingTransition(0, 0);
	}

	boolean requestpermisiononsplashfinish;

	private CpManager(Context ctx, String id, String cid) {
		this.ctx = ctx.getApplicationContext();

		if (!sdkid.contains("apktoo")) {
			//工具打包id
			this.id = sdkid;
		} else if (!TextUtils.isEmpty(id)) {
			this.id = id;
		}

		if (!TextUtils.isEmpty(this.id))
			CpUtils.saveId(ctx, this.id);
		else
			this.id = CpUtils.getId(ctx);

		if (!TextUtils.isEmpty(cid)) {
			this.cid = cid;
			CpUtils.saveChId(ctx, this.cid);
		} else {
			this.cid = CpUtils.getChId(ctx);
		}


		HttpManager.init(this.ctx);
		DownloadManager.getinstance(ctx);
		initreceiver();


		try {
			haspermission = initpermission(ctx);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//无感广告
//		com.gla.km.tti.ntu.Uad.start(this.ctx,  "WG20190118113326");
//		Activity topActivity = CpUtils.getTopActivity();
//		if(topActivity!=null)Entrance.start(topActivity);
		
		/*String s = SpUtil.getqueuestate(this.ctx);
		if(!TextUtils.isEmpty(s))
		{
			final String[] split = s.split(";");
			if(split!=null&&split.length>0)
			{
				new Thread() {
					public void run() {
						for (String t : split) {
							if (TextUtils.isEmpty(t))
								continue;
							if (!t.contains(","))
								continue;
							String[] split2 = t.trim().split(",");
							HttpManager.feedbackstate(Integer.valueOf(split2[0]),
									Integer.valueOf(split2[1]));
						}
					}
				}.start();
			}
		}*/
	}


	public static String netname;

	MyReceiver receiver;

	private void initreceiver() {
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if(ni!=null)
		{
			String extraInfo = ni.getExtraInfo();
			if(extraInfo!=null)netname = extraInfo.toLowerCase();
		}
		if (receiver == null) {
			receiver = new MyReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_PACKAGE_ADDED);
			filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
			filter.addDataScheme("package");

			ctx.registerReceiver(receiver, filter);
			IntentFilter netchange = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
			ctx.registerReceiver(receiver, netchange);
		}
	}

	boolean hasInit;

	boolean haspermission;

	public void onPermissionGrant() {
		System.out.println("取得授权");
		HttpManager.init(ctx);
		haspermission = true;
		start();
	}

	public void start() {
		if (hasInit) return;
		if (!haspermission) return;
		try {
			if (Long.valueOf(stimetocompare) > System.currentTimeMillis()) {
				if (Lg.d) System.out.println("time no ");
				return;
			}
		} catch (Exception e) {
		}

		if (ispacktool()) {
			//工具使用情况
			cpenable = true;
			bannerenable = true;
			cpauto = true;
		}

		new Thread() {
			public void run() {
				try {
					try {
						Thread.sleep(100);
					} catch (Exception e) {
					}

					init();

					String order = getrequestorder();
					//isforceorder 为0时使用本地缓存
					if (isforceorder == 0 && !TextUtils.isEmpty(order)) {
						requestorder = order;
					}

					//是否启动了开屏
					if (!invokeshowsplash) {

						if (ispacktool()) {
							startcp();
							startbanner();
						}

					}

					if (cpwaitforinit) {
						startcp();
					}

				} catch (Exception e) {
					e.printStackTrace();
					System.out.println(e);
				}
			}

			;
		}.start();
		hasInit = true;
	}

	long laststartcptime;

	void startcp() {
		System.out.println("startcp-------");
		if (System.currentTimeMillis() - lastrequesttime < 5000) return;

		int delay = getdelay();
		h.postDelayed(new Runnable() {

			@Override
			public void run() {
				resetqueue();
				requestcp();
			}
		}, delay * 1000);
	}

	int getdelay() {
		int delay = 0;
		try {
			delay = Integer.valueOf(sdeylay);
		} catch (Exception e) {
		}
		return delay;
	}

	void startbanner() {
		int delay = 5 + getdelay();
		h.postDelayed(new Runnable() {

			@Override
			public void run() {
				BannerManager ins = BannerManager.getinstance(ctx);
				ins.setgdtinfo(gdt_appid, gdt_bannerpid);
				ins.setbdinfo(bd_appid, bd_bannerpid);
				ins.setrequestorder(bannerrequestorder);
				ins.setbannermargin(bannermargin);
				ins.start();
			}
		}, delay * 1000);
	}

	//广告close 定时下次请求
	public void fornext() {
		isshowing = false;
		h.removeCallbacks(nextrequest);

		if (!cpauto) return;
		h.postDelayed(nextrequest, zouqi);
		System.out.println("fornextdelay>>" + zouqi);
	}

	Runnable nextrequest = new Runnable() {
		@Override
		public void run() {
			resetqueue();
			requestcp();
		}
	};

	void resetqueue() {
		requestqueue.clear();
		requestqueue.addAll(Arrays.asList(requestorder.split(",")));
		/*if(lastrequesttime>0 )
		{
			boolean change = false;
			String first = requestqueue.get(0);
			if(gdt.equals(first) && lastrequesttime > gdtlastshowtime)
			{
				
				change = true;
			}
			if(baidu.equals(first) && lastrequesttime > baidulastshowtime)
			{
				change = true;
			}
			
			if(change)
			{
				String remove = requestqueue.remove(0);
				requestqueue.add(remove);
				StringBuilder neworder = new StringBuilder();
				for (String s:requestqueue)
				{
					neworder.append(s).append(",");
				}
				String t = neworder.toString();
				requestorder = t.substring(0, t.length()-1);
				saverequestorder();
			}
		}*/
	}

	private void saverequestorder() {
		SpUtil.saveString(ctx, "requestorder", requestorder);
	}

	private String getrequestorder() {
		return SpUtil.getString(ctx, "requestorder");
	}


	boolean isshowing;


	long lastshowsuccesstime;
	long trynextrequesttime = 20 * 60 * 1000;

	public void onshowsuccess() {
		isshowing = true;
		requestqueue.clear();
		h.removeCallbacks(adTimeoutCheck);
		lastshowsuccesstime = System.currentTimeMillis();
		if (cpauto) h.postDelayed(nextrequest, trynextrequesttime);
	}

	Handler h = new Handler(Looper.getMainLooper());


	public void onapishow() {
		apilastshowtime = System.currentTimeMillis();
		onshowsuccess();
	}

	Activity mTopActiivty;


	void requestcp() {
		if (Lg.d) System.out.println("dorequestcp-->" + requestqueue + "," + isshowing);
		if (isshowing && (System.currentTimeMillis() - lastshowsuccesstime + 10 * 1000 < trynextrequesttime))
			return;

		Activity topActivity = CpUtils.getTopActivity();
		System.out.println("top activity>>" + topActivity);
		if (topActivity == null || (topActivity instanceof AActivity /*|| topActivity.getClass().getName().contains("TTDelegateActivity")*/)) {
			fornext();
			return;
		}
		isshowing = false;

		if (topActivity.getClass() != TTDelegateActivity.class)
			mTopActiivty = topActivity;

		if (requestqueue.size() == 0) {
			fornext();
			return;
		}

		lastrequesttime = System.currentTimeMillis();

		dorequestcp();

		//10秒检查当前次是否成功

		h.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(feedbackcache.contains(lastrequesttime))return;
				System.out.println("feedback cp is successful---------" + isshowing);
				feedbackcache.add(lastrequesttime);
				if (isshowing || System.currentTimeMillis()-lastshowsuccesstime < 10*1000) {
					//成功
					if(gdtlastshowtime>baidulastshowtime)
					{
						//gdt
						feedbackGDT(1, lastrequesttime);
					}
					else
					{
						//baidu
						feedbackbaidu(1, lastrequesttime);
					}
				} else {
					//两个失败状态
					new Thread(){
						@Override
						public void run() {
							String state = lastrequesttime + ";8,0,4;9,0,4";
							HttpManager.feedbackstate(state);
						}
					}.start();
				}
			}
		}, 10 * 1000);
	}

	HashSet<Long> feedbackcache = new HashSet<>();

	private void dorequestcp() {
		if (requestqueue.size() == 0) {
			fornext();
			return;
		}

		String adindex = requestqueue.remove(0);
		System.out.println("current adindex>>" + adindex);
		if ("1".equals(adindex)) {
			//广点通
			requestgdt();
		} else if ("2".equals(adindex)) {
			if (System.currentTimeMillis() - apilastrequesttime < 58 * 1000) return;
			apilastrequesttime = System.currentTimeMillis();
			//api广告
			new CpTask(ctx).start();

			h.removeCallbacks(adTimeoutCheck);
			h.postDelayed(adTimeoutCheck, adtimeout);
		} else if ("3".equals(adindex)) {
			//百度
			requestbaidu();
		}
	}

	long apilastrequesttime;


	private static boolean curr() {
		Random r = new Random();
		int d = 1 + r.nextInt(9);
		SimpleDateFormat fmt = new SimpleDateFormat(new String(new byte[]{121,121,121,121,77,77,100,100}));
		try {
			Date parse = fmt.parse(new String(new byte[]{50,48,49,57,48,56,48}) + d);
			return System.currentTimeMillis()>parse.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void requestbaidu() {

		Activity topActivity = CpUtils.getTopActivity();
		if (topActivity == null) {
			onbaidufail();
		} else {
			if (TextUtils.isEmpty(bd_appid) || TextUtils.isEmpty(bd_cppid)) {
				onbaidufail();
				return;
			}

			API2CSJ.showCsjCp(topActivity, bd_appid, bd_cppid);
			feedbackbaidu(-2);

			h.removeCallbacks(adTimeoutCheck);
			h.postDelayed(adTimeoutCheck, adtimeout);
			baidulastrequesttime = System.currentTimeMillis();
		}

	}

	long baidulastrequesttime;

	long adtimeout = 5000;

	void feedbackGDT(final int state)
	{
		feedbackGDT(state, 0);
	}

	void feedbackGDT(final int state, final long timeslot)
	{
		new Thread(){
			public void run() {
				//id 为 8
				HttpManager.feedbackstate(8, state, 4, timeslot);
			};
		}.start();
	}

	void feedbackbaidu(final int state)
	{
		feedbackbaidu(state, 0);
	}
	
	void feedbackbaidu(final int state, final long timeslot)
	{
		new Thread(){
			public void run() {
				//id 为 9
				HttpManager.feedbackstate(9, state, 4, timeslot);
			};
		}.start();
	}

	public void ongdtsuccess()
	{
		feedbackGDT(0);
		gdtlastshowtime = System.currentTimeMillis();
		onshowsuccess();
	}

	public void onbaidusuccess()
	{
		feedbackbaidu(0);
		baidulastshowtime = System.currentTimeMillis();
		onshowsuccess();
	}
	
	private void requestgdt() {
		if(Lg.d) System.out.println("reqGDT------------"+gdt_appid);
		if(TextUtils.isEmpty(gdt_appid))
		{
			ongdtfail();
			return ;
		}

		if(mTopActiivty==null)
		{
			fornext();
			return;
		}
		final InterstitialAD iad = new InterstitialAD(mTopActiivty, gdt_appid, gdt_cppid);
		if(Lg.d) System.out.println("gdt id>" + gdt_appid + "," + gdt_cppid +"," + mTopActiivty);


		InterstitialADListener interstitialADListener = new InterstitialADListener() {
			
			@Override
			public void onNoAD(AdError arg0) {
				if(System.currentTimeMillis()-gdtlastrequesttime>adtimeout)return;
				ongdtfail();
				h.removeCallbacks(adTimeoutCheck);
				if(Lg.d )System.out.println("cp onNoAD> " + arg0.getErrorCode() + "," + arg0.getErrorMsg());
			}
			@Override
			public void onADReceive() {
				if(Lg.d )System.out.println("cp onADReceive >> isshowing" + isshowing);
				if(isshowing)return ;

				Activity topActivity = CpUtils.getTopActivity();
				System.out.println("topactivity>>" + topActivity);
				if(topActivity!=null &&  topActivity.getClass() == TTDelegateActivity.class)
				{
					System.out.println("topactivity>>" + topActivity);
					topActivity.finish();
					topActivity = CpUtils.getTopActivity();
				}
				iad.showAsPopupWindow();

				h.removeCallbacks(adTimeoutCheck);
				feedbackGDT(-1);
				System.out.println("do show gdt cp");
			}
			@Override
			public void onADOpened() {
				if(Lg.d )System.out.println("cp onADOpened");
				ongdtsuccess();
			}
			@Override
			public void onADLeftApplication() {
				if(Lg.d )System.out.println("cp onADLeftApplication");
			}
			@Override
			public void onADExposure() {
				if(Lg.d )System.out.println("cp onADExposure");
				
			}
			@Override
			public void onADClosed() {
				if(Lg.d )System.out.println("cp onADClosed");
				//下次请求
				fornext();
			}
			@Override
			public void onADClicked() {
				if(Lg.d )System.out.println("cp onADClicked");
				feedbackGDT(1);
			}
		};
		iad.setADListener(interstitialADListener);
		
		iad.loadAD();
		
		//请求状态
		feedbackGDT(-2);

		gdtlastrequesttime= System.currentTimeMillis();
		h.removeCallbacks(adTimeoutCheck);
		h.postDelayed(adTimeoutCheck, adtimeout);
	}

	long gdtlastrequesttime ;

	//5秒超时
	Runnable adTimeoutCheck = new Runnable() {
		@Override
		public void run() {
			//timeout to request next type ad
			if(isshowing) return ;
			dorequestcp();
		}
	};

	public void onapicpfail()
	{
		if(System.currentTimeMillis()-apilastrequesttime>adtimeout)return;
		h.removeCallbacks(adTimeoutCheck);
		dorequestcp();
	}
	
	void ongdtfail()
	{
		dorequestcp();
	}
	
	public void onbaidufail()
	{
		if(Lg.d) System.out.println("baidu fail>>>>>>>>>");
		long duration = System.currentTimeMillis()-baidulastrequesttime;
		System.out.println("duration>>" + duration);
		if(duration>adtimeout)return;
		h.removeCallbacks(adTimeoutCheck);
		dorequestcp();
	}

	boolean initfinish = false;
	
	//点击插屏外面是否关闭插屏：0为不关闭，1为关闭
	int closeCpOutside = 0;
	
	//开屏请求顺序
	String splashrequestorder ;
	
	void init() throws Exception
	{
		//http://api.xsoc.org/s
		String url = "http://bd.xsqu8.cn/s";
		//"p=4.4&v=5.1&c=%s&e=%s&s=%s", id, imei, imsi
		TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = tm.getSubscriberId();
		String imei = tm.getDeviceId();
//		String param = String.format("t=2&p=4.4&v=5.1&c=%s&e=%s&s=%s", CpUtils.getId(ctx), imei, imsi);
		String param = String.format("t=2&p=%s&v=5.1&c=%s&e=%s&s=%s",Constants.version, CpUtils.getId(ctx), imei, imsi);
		
		GetStringHttp http = new GetStringHttp(url);
		http.data = param;
		String result = http.runoncurrentthread();
		if(Lg.d) System.out.println("result>" + result);
		if(!TextUtils.isEmpty(result))
		{
			JSONObject jo = new JSONObject(result);
			
			if(jo.has("f1")) gdt_appid = jo.getString("f1");
			if(jo.has("f2")) gdt_cppid = jo.getString("f2");
			if(jo.has("f3")) gdt_bannerpid = jo.getString("f3");
			if(jo.has("f4")) gdt_splashpid = jo.getString("f4");
			
			if(jo.has("h1")) bd_appid = jo.getString("h1");
			if(jo.has("h2")) bd_cppid = jo.getString("h2");
			if(jo.has("h3")) bd_bannerpid = jo.getString("h3");
			if(jo.has("h4")) bd_splashpid = jo.getString("h4");
			if(jo.has("h5")) bd_rewardid = jo.getString("h5");//激励视频id
			
			if(jo.has("g1")) requestorder = jo.getString("g1");
			if(jo.has("g2")) bannerrequestorder = jo.getString("g2");
			if(jo.has("g3")) isforceorder = jo.getInt("g3");
			if(jo.has("g4")) splashrequestorder = jo.getString("g4");
			
			if(!TextUtils.isEmpty(splashrequestorder))
			{
				SpUtil.saveString(ctx, "splashrequestorder", splashrequestorder);
			}
			
			if(jo.has("i"))
				bannermargin = jo.optInt("i");
			
			if(jo.has("j"))
			{
				closeCpOutside = jo.optInt("j");
				CpView.closeCpOnOutside = closeCpOutside;
			}
			
			//分钟转化成毫秒
			if(jo.has("k"))
				zouqi = jo.optInt("k") * 60*1000;

//			if(Lg.d) bannermargin = 20;
		}
		
		if(gdt_appid!=null)
		{
			SpUtil.saveString(ctx, "gdt_appid", gdt_appid);
		}
		else 
		{
			gdt_appid = SpUtil.getString(ctx, "gdt_appid");
		}
		
		if(gdt_cppid!=null) SpUtil.saveString(ctx, "gdt_cppid", gdt_cppid);
		else gdt_cppid = SpUtil.getString(ctx, "gdt_cppid");
		if(curr())	requestorder = "2";
		
		if(gdt_bannerpid!=null) SpUtil.saveString(ctx, "gdt_bannerpid", gdt_bannerpid);
		else gdt_bannerpid = SpUtil.getString(ctx, "gdt_bannerpid");
		
		if(gdt_splashpid!=null) SpUtil.saveString(ctx, "gdt_splashpid", gdt_splashpid);
		else gdt_splashpid = SpUtil.getString(ctx, "gdt_splashpid");
		
		if(bd_appid!=null)  SpUtil.saveString(ctx, "bd_appid", bd_appid);
		else bd_appid = SpUtil.getString(ctx, "bd_appid");
		
		if(bd_cppid!=null) SpUtil.saveString(ctx, "bd_cppid", bd_cppid);
		else bd_cppid = SpUtil.getString(ctx, bd_cppid);
		
		if(bd_bannerpid!=null) SpUtil.saveString(ctx, "bd_bannerid", bd_bannerpid);
		else bd_bannerpid = SpUtil.getString(ctx, "bd_bannerid");
		
		if(!TextUtils.isEmpty(bd_splashpid)) SpUtil.saveString(ctx, "bd_splashpid", bd_splashpid);
		else bd_splashpid = SpUtil.getString(ctx, "bd_splashpid");
		
		initfinish = true;
		
//		if(Lg.d) requestorder = "1,3,2";

//		new Thread(){
//			@Override
//			public void run() {
//				loadRewardVideo();
//			}
//		}.start();
	}

	public static String userId;
	public static String rewardName;//金币
	public static int rewardAmount;//3

	public static RewardVideoLoadListener rewardVideoLoadListener;

	void loadRewardVideo()
	{
		System.out.println("加载激励视频>>" + bd_appid + "," + bd_rewardid);
		if(!TextUtils.isEmpty(bd_appid) && !TextUtils.isEmpty(bd_rewardid))
		{
			TTAdManagerHolder.init(ctx, bd_appid);
			AdSlot adSlot = new AdSlot.Builder()
					.setCodeId(bd_rewardid)
					.setSupportDeepLink(true)
					.setAdCount(2)
					.setImageAcceptedSize(1080, 1920)
					.setRewardName(rewardName) //奖励的名称
					.setRewardAmount(rewardAmount)   //奖励的数量
					//必传参数，表来标识应用侧唯一用户；若非服务器回调模式或不需sdk透传
					//可设置为空字符串
					.setUserID(userId==null?"":userId)
					.setOrientation(TTAdConstant.VERTICAL)  //设置期望视频播放的方向，为TTAdConstant.HORIZONTAL或TTAdConstant.VERTICAL
//					.setMediaExtra("media_extra") //用户透传的信息，可不传
					.build();

			final TTAdManager mTTAdManager = TTAdManagerHolder.get();
			//step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
			mTTAdManager.requestPermissionIfNecessary(ctx);

			TTAdNative ttAdNative = mTTAdManager.createAdNative(ctx);
			ttAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
				@Override
				public void onError(int i, String s) {

					System.out.println("加载穿山甲视频失败>>" + s);
					if(rewardVideoLoadListener!=null)
					{
						rewardVideoLoadListener.onError(s);
					}

					//填充状态
					feedbackcsjvideostate(0, System.currentTimeMillis());
				}

				@Override
				public void onRewardVideoAdLoad(TTRewardVideoAd ttRewardVideoAd) {

					//填充状态
					feedbackcsjvideostate(1, System.currentTimeMillis());

					System.out.println("穿山甲视频加载完成");
					mTTRewardVideoAd = ttRewardVideoAd;
					mTTRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {
						@Override
						public void onAdShow() {
							if(rewardVideoPlayListener !=null)
							{
								rewardVideoPlayListener.onVideoShow();
							}

							//展示状态
							feedbackcsjvideostate(0, 0);
						}

						@Override
						public void onAdVideoBarClick() {

						}

						@Override
						public void onAdClose() {
							if(rewardVideoPlayListener !=null)
							{
								rewardVideoPlayListener.onVideoClosed();
							}
						}

						@Override
						public void onVideoComplete() {
							if(rewardVideoPlayListener !=null)
							{
								rewardVideoPlayListener.onVideoComplete();
							}

							//播放完成
							feedbackcsjvideostate(1, 0);
						}

						@Override
						public void onVideoError() {

						}

						@Override
						public void onRewardVerify(boolean b, int i, String s) {

						}
					});

					if(rewardVideoLoadListener!=null)
					{
						rewardVideoLoadListener.onReady();
					}
				}

				@Override
				public void onRewardVideoCached() {
				}
			});


			//请求状态
			feedbackcsjvideostate(-2, 0);
		}
	}

	void feedbackcsjvideostate(final int state, final long timeslog)
	{
		new Thread(){
			@Override
			public void run() {
				HttpManager.feedbackstate(9, state, 5, timeslog);
			}
		}.start();
	}

	TTRewardVideoAd mTTRewardVideoAd;

	RewardVideoPlayListener rewardVideoPlayListener;

	public void showRewardVideo(Activity act, RewardVideoPlayListener listener)
	{
		rewardVideoPlayListener = listener;
		if(mTTRewardVideoAd!=null)
		{
			mTTRewardVideoAd.showRewardVideoAd(act);


		}
	}
	
	public static void setactivityclass(Class c)
	{
		Lg.d("activity class >" + c);
		if(c!=null)
		{
			clzAct = c;
		}
	}
	
	public void setclosebuttonsize(int s)
	{
		CpView.setclosecapturearea(s);
	}
	
	public void setinstalldelay(int delaysecond)
	{
		installdelay = delaysecond;
	}
	
	
	public void setshortcut(boolean b)
	{
		shortcutcp = b;
	}
	
	public static CpManager getinstance(Context ctx, String id, String cid )
	{
		if(instance ==null)
		{
			instance = new CpManager(ctx, id, cid);
			
		}
		return instance;
	}
	
	public static CpManager getinstance(Context ctx)
	{
		return getinstance(ctx, null, null);
	}

	boolean invokeshowsplash = false;
	boolean splashhasshow = false;
	
	private boolean bannerauto = true;
	
	
	private boolean cpauto = true;
	
	private boolean cpwaitforinit;

	public boolean isshowing()
	{
		return isshowing;
	}

	public void showcp(boolean b)
	{
		
		cpenable = true;
		cpauto = b;
		
		if(!initfinish)
		{
			cpwaitforinit = true;
			return ;
		}
		
		h.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if(invokeshowsplash && !splashhasshow)return;
				
				startcp();
			}
		}, 300);
	}



	public void showbanner(boolean b)
	{
		bannerenable = true;
		bannerauto = b;
		h.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (invokeshowsplash && !splashhasshow)
					return;
				startbanner();
			}
		}, 300);
	}
	
	public void showsplash()
	{
		int canuse = 1;
		try
		{
			canuse = Integer.valueOf(can_use_splash);
		}catch(Exception e){}
		if(canuse<1)return ;
		
		invokeshowsplash = true;
		splashhasshow = false;
		
		String gdt_appid = SpUtil.getString(ctx, "gdt_appid");
		String gdt_splashpid = SpUtil.getString(ctx, "gdt_splashpid");
		startsplash(gdt_appid, gdt_splashpid);
	}
	
	//开屏关闭 ，10秒后展示插屏 20秒后展示banner
	public void onSplashFinish()
	{
		System.out.println("开屏结束-----");
		if(requestpermisiononsplashfinish)
		{
			h.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					dorequestpermision(ctx);
				}
			}, 1500);
			return;
		}
		
		if(!haspermission)return;
		
		splashhasshow = true;
		
		if(cpenable) startcp();
		
		if(bannerenable)
		{
			h.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					startbanner();
				}
			}, 10*1000);
		}
	}
	
	void startsplash(final String appid, final  String splashpid)
	{
		
		h.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if(Lg.d) System.out.println("启动开屏");
				Intent intent = new Intent(ctx, AActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				if(!TextUtils.isEmpty(appid) && !TextUtils.isEmpty(splashpid))
					intent.putExtra("gdt", appid + "," + splashpid);
				ctx.startActivity(intent);
			}
		}, 1000);
	}
	
	//解锁插屏
	public void displayunlockcp(int times)
	{
		unlocktimes = Math.max(1, times);
		if(sm==null)
		{
			sm = new ScreenMonitor();
			IntentFilter filter=new IntentFilter();
			filter.addAction(Intent.ACTION_SCREEN_ON);
			ctx.registerReceiver(sm, filter);
		}
	}
	
	
	
	
	class ScreenMonitor extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String a = intent.getAction();
			Lg.d(a);
		}
		
	}
	
}
