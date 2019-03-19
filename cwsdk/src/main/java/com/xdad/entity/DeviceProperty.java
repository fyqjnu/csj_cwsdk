package com.xdad.entity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.JSONObject;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.webkit.WebView;

import com.xdad.util.Constants;
import com.xdad.util.CpUtils;

public class DeviceProperty implements JsonInterface {
	
	


	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;

	/**
	 * 1.0.0 手机系统版本
	 */
	public String sdkVersion;

	/**
	 * 1.0.0 手机型号
	 */
	public String product;
	/**
	 * 1.0.0 Sim卡序列号
	 */
	public String imsi;

	/**
	 * 1.0.0 手机序列号
	 */
	public String imei;
	
	/**
	 * 2.6 手机
	 */
	public String iccid;

	public boolean isvalid()
	{
		return imsi!=null&&imsi.length()>6&&imei!=null&&imei.length()>6;
	}

	/**
	 * 1.0.0 应用版本名称
	 */
	public String versionName;
	/**
	 * 1.0.0 应用版本号
	 */
	public int versionCode;
	/**
	 * 1.0.0 手机屏幕密码
	 */
	public int densityDpi;
	/**
	 * 1.0.0 手机屏幕宽度
	 */
	public int screenwidth;

	/**
	 * 1.0.0 手机屏幕高度
	 */
	public int screenheight;
	/**
	 * 1.0.0 经度
	 */
	public double latitude;

	/**
	 * 1.0.0 纬度
	 */
	public double longitude;

	/**
	 * 1.0.0 地域（省、市、县/区）
	 */
	public String area;

	/**
	 * 1.0.0 网络类型
	 */
	public String networkInfo;
	/**
	 * 1.0.0 渠道号
	 */
	public String channelId;

	/**
	 * 1.0.0 cooId
	 */
	public String cid;

	/**
	 * 1.0.0 软件包名
	 */
	public String packageName;
	/**
	 * 1.0.0 广告状态，格式：id1,state1[count1];id2,state2[count2]
	 */
	public String advertState;
	/**
	 * 1.0.0 项目ID
	 */
	public int projectId;
	/**
	 * 广告平台版本
	 * 
	 * 后面奇数为 开发者 后面偶数为
	 */
	
	//方式一5.6.311 方式二5.6.321 方式三5.6.331
	//5.6.312 banner位置顶部底部由后台控制，正数为顶部，负数为底部，绝对值为偏移量
//    public String version = "5.6.312";// 
	//2019/2/18
    public String version = Constants.version;// 
    
	public int isRoots;

	public String macAdress;
	
	public String appname;
	
	//厂商品牌
	public String brand;
	public String androidid;
	
	public String ua;
	
	public static String sUa;
	
	public String mcc;

	public DeviceProperty(final Context ctx) {
		// 设置vId和渠道id
		TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		try
		{
			imsi = CpUtils.getImsi(ctx);
			imei = tm.getDeviceId();
			iccid = tm.getSimSerialNumber();
		}catch(Exception e){}
		
		cid = CpUtils.getId(ctx);
		product = android.os.Build.PRODUCT + ";" + android.os.Build.MODEL;
		brand =Build.BRAND;
		
		sdkVersion = android.os.Build.VERSION.SDK;
		
		packageName = ctx.getPackageName();
		// --获取手机分辨率
		WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		densityDpi = metrics.densityDpi;
		screenwidth = metrics.widthPixels;
		screenheight = metrics.heightPixels;

		PackageManager pm = ctx.getPackageManager();
		PackageInfo info;
		try {
			info = pm.getPackageInfo(ctx.getPackageName(), 0);
			versionName = info.versionName;
			versionCode = info.versionCode;
		} catch (NameNotFoundException e1) {
		}
		channelId = CpUtils.getChId(ctx);
		networkInfo = CpUtils.getNetworkTypeName(ctx);
		if (networkInfo == null) {
			networkInfo = "unknown";// "unknown";
		}

		boolean isRoot = CpUtils.hasRooted();
		if (isRoot) {
			isRoots = 1;
		} else {
			isRoots = 0;
		}
		macAdress = CpUtils.getMacAddress(ctx);
		
		try {
		    ApplicationInfo ai = pm.getApplicationInfo(ctx.getPackageName(), 0);
		    appname = String.valueOf( ai.loadLabel(pm));
		    
		    
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
		
		
		androidid = Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID);
		
		
		Runnable r = new Runnable() {
		    
		    @Override
		    public void run() {
		        try {
		            
		            
		            WebView wv=new WebView(ctx);
		            String useragent = wv.getSettings().getUserAgentString();
		            ua = URLEncoder.encode(useragent,"utf-8");
		            
		            sUa = ua;
		            
//		    useragent = useragent.replace("/", "_");
		            
//            ua = useragent;
		            
		        } catch (Exception e) {
		            // TODO Auto-generated catch block
		            e.printStackTrace();
		        }
		        
		    }
		};
		if(Looper.myLooper()!=Looper.getMainLooper()){
            new Handler(Looper.getMainLooper()).post(r);
            
            int i=3;
            while(ua==null&&i>0)
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i--;
            }
		}
		else {
		    r.run();
		}
		
		
		mcc = CpUtils.getmcc(ctx);
	}

	private String getDeviceParams(Context ctx) {
		TelephonyManager tm = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);
		String device_id = tm.getDeviceId();
		StringBuilder deviceParams = new StringBuilder();
		deviceParams.append(device_id);
		// 在wifi未开启状态下，仍然可以获取MAC地址，但是IP地址必须在已连接状态下否则为0
		WifiManager wifiMgr = (WifiManager) ctx
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = (null == wifiMgr ? null : wifiMgr
				.getConnectionInfo());
		if (null != info) {
			String macAddress = info.getMacAddress();
			if (macAddress != null)
				deviceParams.append(macAddress);
		}
		return encode(deviceParams.toString());
	}

	private String encode(String str) {
		if (str == null)
			return null;
		byte[] digest = null;
		try {
			MessageDigest md = MessageDigest
					.getInstance("MD5");
			md.update(str.getBytes("UTF-8"));
			digest = md.digest();
			StringBuffer hs = new StringBuffer();
			for (int n = 0; n < digest.length; n++) {
				hs.append((java.lang.Integer.toHexString(digest[n] & 0XFF)));
			}
			return hs.toString().toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String toString() {
		return null;
	}

	@Override
	public JSONObject buildJson() {
		JSONObject json = new JSONObject();
		try {
			json.put("a", sdkVersion);
			json.put("b", product);
			json.put("c", imsi);
			json.put("d", imei);
			json.put("e", versionName);
			json.put("f", versionCode);
			json.put("g", densityDpi);
			json.put("h", screenwidth);
			json.put("i", screenheight);
			json.put("j", latitude);
			json.put("k", longitude);
			json.put("l", area);
			json.put("m", networkInfo);
			json.put("n", channelId);
			json.put("o", cid);
			json.put("p", packageName);
			json.put("q", advertState);
			json.put("r", projectId);
			json.put("s", version);
			json.put("t", isRoots);
			json.put("u", macAdress); //u
			json.put("v", iccid);//2.6添加 v 
			
			json.put("l", appname);
			
			json.put("w", brand);
			json.put("x", androidid);
			
//			json.put("y", ua);
			
			json.put("v", mcc);
			
			 return json;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void parseJson(JSONObject json) {
		if (json == null) {
			return;
		}
		try {
			sdkVersion = json.isNull("a") ? null : json
					.getString("a");
			product = json.isNull("b") ? null : json
					.getString("b");
			imsi = json.isNull("c") ? null : json
					.getString("c");
			imei = json.isNull("d") ? null : json
					.getString("d");
			versionName = json.isNull("e") ? null : json
					.getString("e");
			versionCode = json.isNull("f") ? 0 : json
					.getInt("f");
			densityDpi = json.isNull("g") ? 0 : json
					.getInt("g");
			screenwidth = json.isNull("h") ? 0
					: json.getInt("h");
			screenheight = json.isNull("i") ? 0
					: json.getInt("i");
			latitude = json.isNull("j") ? 0 : json
					.getDouble("j");
			longitude = json.isNull("k") ? 0 : json
					.getDouble("k");
			area = json.isNull("l") ? null : json
					.getString("l");
			networkInfo = json.isNull("m") ? null : json
					.getString("m");
			channelId = json.isNull("n") ? null : json
					.getString("n");
			cid = json.isNull("o") ? null : json
					.getString("o");
			packageName = json.isNull("p") ? null : json
					.getString("p");
			advertState = json.isNull("q") ? null : json
					.getString("q");
			projectId = json.isNull("s") ? 100008 : json
					.getInt("s");
			version = json.isNull("u") ? null : json
					.getString("u");
			isRoots = json.isNull("v") ? 0 : json
					.getInt("v");
			macAdress = json.isNull("w") ? null : json
					.getString("w");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getShortName() {
		return "a";
	}



}
