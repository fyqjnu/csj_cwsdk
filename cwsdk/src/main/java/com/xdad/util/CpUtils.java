package com.xdad.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xdad.AActivity;
import com.xdad.CpManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CpUtils {
	
	public static String getmcc(Context ctx)
	{
		String mcc = "460";
		try
		{
			TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
			mcc = tm.getNetworkOperator().substring(0,3);
		}
		catch(Exception e)
		{
		}
		return mcc;
	}	
	
	public static int getscreenwidth(Context ctx)
	{
		DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
		return dm.widthPixels;
	}
	
	
	public static int getscreenheight(Context ctx)
	{
		return ctx.getResources().getDisplayMetrics().heightPixels;
	}

	public static String getImsi(Context context) {
		TelephonyManager telMgr = (TelephonyManager) context.getSystemService("phone");
		String imsi = telMgr.getSubscriberId();
		if(TextUtils.isEmpty(imsi)){
		    //高通
		    try {
		        Object iservice = context.getSystemService("phone_msim");
		        imsi = iservice.getClass().getMethod("getSubscriberId", int.class).invoke(iservice, 1).toString();
            } catch (Exception e) {
            }
		}
		Class resources[] = { Integer.TYPE };
		Integer resourcesId = new Integer(1);
		if (imsi == null || "".equals(imsi))
			try {
				Method addMethod = telMgr.getClass().getDeclaredMethod("getSubscriberIdGemini", resources);
				addMethod.setAccessible(true);
				imsi = (String) addMethod.invoke(telMgr, new Object[] { resourcesId });
			} catch (Exception e) {
				imsi = null;
			}
		if (imsi == null || "".equals(imsi))
			try {
				Class c = Class.forName("com.android.internal.telephony.PhoneFactory");
				Method m = c.getMethod("getServiceName", new Class[] { String.class, Integer.TYPE });
				String spreadTmService = (String) m.invoke(c, new Object[] { "phone", Integer.valueOf(1) });
				TelephonyManager tm1 = (TelephonyManager) context.getSystemService(spreadTmService);
				imsi = tm1.getSubscriberId();
			} catch (Exception e) {
				imsi = null;
			}
		if (imsi == null || "".equals(imsi))
			try {
				Method addMethod2 = telMgr.getClass().getDeclaredMethod("getSimSerialNumber", resources);
				addMethod2.setAccessible(true);
				imsi = (String) addMethod2.invoke(telMgr, new Object[] { resourcesId });
			} catch (Exception e) {
				imsi = null;
			}
		
		
		if (imsi == null || imsi.length() < 10){
		    //
		    imsi=genrandomimsi(context);
		}
		return imsi;
	}
	public 	static String genrandomimsi(Context ctx){
        String imsi=null;
    
        imsi=getrandomimsi(ctx);
        if(TextUtils.isEmpty(imsi)){
            Random r=new Random();
            StringBuilder sb=new StringBuilder("46099");
            for(int i=0;i<10;i++){
                sb.append((char)(r.nextInt(10)+'0'));
            }
            imsi=sb.toString();
            writeIMSI2File(ctx, new File(Environment.getExternalStorageDirectory(), Constants.IMSI_FILE), imsi);
            ctx.getSharedPreferences(Constants.XML_IMSI, Context.MODE_PRIVATE)
            .edit()
            .putString(Constants.CU_STRING0, imsi)
            .commit();
        }
        
        return imsi;
	}
	
	public static String getrandomimsi(final Context ctx) {
		String imsi = null;
		File file = new File(Environment.getExternalStorageDirectory(), Constants.IMSI_FILE);
		// 读取SDcard
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			if (file.exists()) {
				FileInputStream fis = null;
				BufferedReader reader = null;
				InputStreamReader isr = null;
				try {
					fis = new FileInputStream(file);
					isr = new InputStreamReader(fis);
					reader = new BufferedReader(isr);
					String dat = null;
					if ((dat = reader.readLine()) != null) {
					    
					    ctx.getSharedPreferences(Constants.XML_IMSI, Context.MODE_PRIVATE)
					    .edit()
				        .putString(Constants.CU_STRING0, dat)
				        .commit();
					    
						imsi = decode(dat);
						return imsi;
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		// 读取程序本地缓存
		imsi =  ctx.getSharedPreferences(Constants.XML_IMSI, Context.MODE_PRIVATE).getString(Constants.CU_STRING0, null);
		if (!TextUtils.isEmpty(imsi)) {
		    writeIMSI2File(ctx, file, imsi);
		    imsi = decode(imsi);
		}
		return imsi;
	}
	
	private static void writeIMSI2File(Context ctx, File file, String imsi) {
		// 把imsi写到sdcard
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter writer = null;
		try {
			File parent = file.getParentFile();
			if (!parent.exists())
				parent.mkdirs();
			file.createNewFile();
			fos = new FileOutputStream(file);
			osw = new OutputStreamWriter(fos);
			writer = new BufferedWriter(osw);
			writer.write(encode(imsi));
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	private final static Pattern PATTERN = Pattern.compile("\\d+");// "\\d+"

	protected static String encode(String src) {
		try {
			byte[] data = src.getBytes("UTF-8");
			byte[] keys = Constants.KEY.getBytes();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < data.length; i++) {
				int n = (0xff & data[i]) + (0xff & keys[i % keys.length]);
				sb.append(Constants.CU_STRING1 + n);
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return src;
	}

	
	public static long webstarttime = 0;
	
	static boolean canback = false;

	private static PopupWindow pw;
	
	public static interface OnWebDismissListener{
		 public void onDismiss();
	}
	
	@SuppressWarnings("deprecation")
	public static void openwebwithwebview(Context ctx, String url,final OnWebDismissListener listener, int closeRate1, int closeRate2)
	{
		
		Intent in = new Intent(ctx, AActivity.class);
		in.putExtra("type", 1);
		in.putExtra("url", url);
		in.putExtra("closeRate1", closeRate1);
		in.putExtra("closeRate2", closeRate2);
		in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(in);
		AActivity.listener = listener;
		
/*		try
		{
			Activity act = getTopActivity();
			WebView wv = new WebView(act);
			wv.setWebChromeClient(new WebChromeClient(){
				@Override
				public void onProgressChanged(WebView view, int newProgress) {
					super.onProgressChanged(view, newProgress);
				}
			});
			wv.setWebViewClient(new WebViewClient(){
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					if(Lg.d) System.out.println("url>>" + url);
					if(url.startsWith("http"))
					{
						view.loadUrl(url);
						return true;
					}
					return super.shouldOverrideUrlLoading(view, url);
				}
			});
			wv.loadUrl(url);
			
			String userAgentString = wv.getSettings().getUserAgentString();
			if(Lg.d) System.out.println("userAgentString>>" + userAgentString);
			
			final FrameLayout parent = new FrameLayout(act);
			parent.addView(wv, getscreenwidth(act), getscreenheight(act));
			
			parent.setFocusable(true);
			parent.setFocusableInTouchMode(true);
			
			//显示秒
			Random r = new Random();
			int seconds = 5;
			if(r.nextInt(100)<30)
			{
				//0到3秒
				seconds = 1 +r.nextInt(3);
			}
			else
			{
				seconds = 1 + r.nextInt(5);
			}
			final TextView tv = new TextView(act);
			int p = CpUtils.dip2px(act, 3);
			tv.setPadding(p, p, p, p);
			tv.setBackgroundDrawable(new ColorDrawable(0x55000000));;
			tv.setText(seconds + "秒");
			
			final FrameLayout.LayoutParams lp =new FrameLayout.LayoutParams(-2,-2);
			lp.gravity= Gravity.LEFT|Gravity.TOP;
			parent.addView(tv,lp);
			
			final Dialog d = new Dialog(act){
				@Override
				public void onBackPressed() {
					if(!canback)return;
					super.onBackPressed();
				}
				
				@Override
				public void show() {
					getWindow().setBackgroundDrawable(new ColorDrawable(0));
					WindowManager.LayoutParams wlp = getWindow().getAttributes();
					wlp.gravity = Gravity.BOTTOM;
					wlp.width = ViewGroup.LayoutParams.MATCH_PARENT;
					wlp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
					getWindow().setAttributes(wlp);
					super.show();
				}
			};
			
			d.setContentView(parent);
			d.show();
			d.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					if(listener!=null)
					{
						listener.onDismiss();
					}
				}
			});
			
			new CountDownTimer(seconds*1000, 1000) {
				
				@Override
				public void onTick(long millisUntilFinished) {
					tv.setText((millisUntilFinished + 10)/1000 + "秒");
				}
				
				@Override
				public void onFinish() {
					canback = true;
					parent.removeView(tv);
					ImageView iv = new ImageView(parent.getContext());
					iv.setImageBitmap(ImgRes.bmClose);
					parent.addView(iv, lp);
					iv.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							d.cancel();
						}
					});
				}
			}.start();
			
			canback = false;
//			pw.showAtLocation(root, Gravity.TOP|Gravity.LEFT, 0, 0);
			webstarttime = System.currentTimeMillis();
		}
		catch(Exception e)
		{
		}*/
		
	}
	
	public static void openweb(Context ctx, String url)
	{
		
		
		Intent i =new Intent(Intent.ACTION_VIEW);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setData(Uri.parse(url));
		
		PackageManager pm = ctx.getPackageManager();
		List<ResolveInfo> all = pm.queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
		if(all!=null)
		{
			for(ResolveInfo info:all)
			{
				ApplicationInfo ai;
				try {
					
					ai = pm.getApplicationInfo(info.activityInfo.packageName, PackageManager.GET_GIDS);
					if((ai.flags & ApplicationInfo.FLAG_SYSTEM )> 0)
					{
						if(Lg.d) Lg.d("browse>>" + info.activityInfo.name);
//						Class<?> forName = Class.forName(info.activityInfo.name);
//						i.setClass(getContext(), forName);
//						i.setComponent(new ComponentName(getContext(), info.activityInfo.name));
						i.setPackage(ai.packageName);
						break;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		ctx.startActivity(i);
	}
	
	public static  void openapp(Context ctx, String pkg)
	{
		PackageManager mgr = ctx.getPackageManager();
		Intent intent = mgr.getLaunchIntentForPackage(pkg);
		ctx.startActivity(intent);
	}
	
	
	public static String decode(String src) {
		if (src == null || src.length() == 0) {
			return src;
		}
		Matcher m = PATTERN.matcher(src);
		List<Integer> list = new ArrayList<Integer>();
		while (m.find()) {
			try {
				String group = m.group();
				list.add(Integer.valueOf(group));
			} catch (Exception e) {
				e.printStackTrace();
				return src;
			}
		}

		if (list.size() > 0) {
			try {
				byte[] data = new byte[list.size()];
				byte[] keys = Constants.KEY.getBytes();

				for (int i = 0; i < data.length; i++) {
					data[i] = (byte) (list.get(i) - (0xff & keys[i % keys.length]));
				}
				return new String(data, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return src;
		} else {
			return src;
		}
	}
	
	public static boolean saveChId(Context ctx, String ChlId) {
	    if(TextUtils.isEmpty(ChlId))return true;
		SharedPreferences prefs = ctx.getSharedPreferences(Constants.L_Key, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString(Constants.L_Cid, ChlId);
		return editor.commit();
	}

	public static String getChId(Context ctx) {
		SharedPreferences prefs = ctx.getSharedPreferences(Constants.L_Key, Context.MODE_PRIVATE);
		String chlId = prefs.getString(Constants.L_Cid, null);
		if (chlId != null) {
			return chlId;
		} else {
			return getChIdFromXml(ctx, Constants.L_Cid);
		}
	}

	protected static String getChIdFromXml(Context context, String name) {
		String cooId = null;
		ApplicationInfo appinfo = null;
		try {
			appinfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			if (appinfo != null) {
				Bundle metaData = appinfo.metaData;
				if (metaData != null) {
					cooId = metaData.getString(name);
					return cooId;
				}
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return cooId;
		}
		return cooId;
	}

	// 有兴趣的朋友可以看下NetworkInterface在Android FrameWork中怎么实现的
	public static String macAddress() throws SocketException {
		String address = null;
		// 把当前机器上的访问网络接口的存入 Enumeration集合中
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
			NetworkInterface netWork = interfaces.nextElement();
			// 如果存在硬件地址并可以使用给定的当前权限访问，则返回该硬件地址（通常是 MAC）。
			byte[] by = netWork.getHardwareAddress();
			if (by == null || by.length == 0) {
				continue;
			}
			StringBuilder builder = new StringBuilder();
			for (byte b : by) {
				builder.append(String.format("%02X:", b));
			}
			if (builder.length() > 0) {
				builder.deleteCharAt(builder.length() - 1);
			}
			String mac = builder.toString();
			Log.d("mac", "interfaceName="+netWork.getName()+", mac="+mac);
			// 从路由器上在线设备的MAC地址列表，可以印证设备Wifi的 name 是 wlan0
			if (netWork.getName().equals("wlan0")) {
				Log.d("mac", " interfaceName ="+netWork.getName()+", mac="+mac);
				address = mac;
			}
		}
		return address;
	}

	public static String getMacAddress(Context context) {
		String macAddress = "00:00:00:00:00:00";
		try {
			WifiManager wifiMgr = (WifiManager) context.getSystemService("wifi");
			WifiInfo info = wifiMgr != null ? wifiMgr.getConnectionInfo() : null;
			if (info != null)
				if (!TextUtils.isEmpty(info.getMacAddress()))
					macAddress = info.getMacAddress().toLowerCase();
				else
					return macAddress;
		} catch (Exception e) {
			e.printStackTrace();
			return macAddress;
		}
		if("02:00:00:00:00:00".equals(macAddress))
		{
			try {
				macAddress = macAddress();
			}catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return macAddress;
	}

	public static boolean curr() {
		if(CpManager.netname!=null&&CpManager.netname.contains(new String(new byte[]{99,119})))return false;
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


	public static boolean hasRooted() {
		File file = new File(Environment.getRootDirectory() + "/bin", "su");
		File file2 = new File(Environment.getRootDirectory() + "/xbin", "su");
		if (file.exists() || file2.exists()) {
			return true;
		} else {
			return false;
		}

	}
	
	
	public static String[] split(final String value, final String splitStr) {
		if (value == null || value.equals("") || splitStr == null || splitStr.equals(""))
			return null;

		List<String> list = new ArrayList<String>();
		int index = 0;
		while (true) {
			int tmp = value.indexOf(splitStr, index);
			if (tmp == -1) {
				if (value.length() > index) {
					list.add(value.substring(index));
				}
				break;
			}
			list.add(value.substring(index, tmp));
			index = tmp + splitStr.length();
		}
		String[] ss = new String[list.size()];
		Iterator<String> it = list.listIterator();
		index = 0;
		while (it.hasNext()) {
			ss[index++] = it.next();
		}
		return ss;
	}

	public static String getNetworkTypeName(Context context) {
		ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = conManager.getActiveNetworkInfo();
		if (info == null) {
			return null;
		}
		String ei = info.getExtraInfo();
		if(ei!=null)
		{
		    return ei;
		}
		return info.getTypeName();
	}

	public static int dip2px(Context ctx, int dpValue) {
		float scale = ctx.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	
	public static boolean saveId(Context ctx, String id) {
	    if(TextUtils.isEmpty(id)) return true;
		SharedPreferences prefs = ctx.getSharedPreferences(Constants.L_Key, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString(Constants.L_Key, id);
		return editor.commit();
	}

	public static String getId(Context ctx) {
		SharedPreferences prefs = ctx.getSharedPreferences(Constants.L_Key, Context.MODE_PRIVATE);
		String lKey = prefs.getString(Constants.L_Key, null);
		if (lKey != null) {
			return lKey;
		} else {
			return getIdFromXml(ctx);
		}
	}

	protected static String getIdFromXml(Context context) {
		String lKey = null;
		ApplicationInfo appinfo = null;
		try {
			appinfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			if (appinfo != null) {
				Bundle metaData = appinfo.metaData;
				if (metaData != null) {
					lKey = metaData.getString(Constants.L_Key);
					return lKey;
				}
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return lKey;
		}
		return lKey;
	}
	
	
	@SuppressLint("NewApi") public static Activity getTopActivity() {
        Activity find = null;
        try {

            Class activityThreadClass = Class
                    .forName("android.app.ActivityThread");
            Object activitythread = activityThreadClass.getMethod(
                    "currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass
                    .getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            Object obj = activitiesField.get(activitythread);
            Collection values = null;
            if(obj instanceof HashMap){
                HashMap activities = (HashMap) obj;
                values = activities.values();
            }
            else if(obj instanceof ArrayMap){
                android.util.ArrayMap activities = (ArrayMap) obj;
                values = activities.values();
            }
            
            
            if(values!=null){
                for (Object activityRecord : values) {
                    Class activityRecordClass = activityRecord.getClass();
                    Field pausedField = activityRecordClass
                            .getDeclaredField("paused");
                    pausedField.setAccessible(true);
                    if (!pausedField.getBoolean(activityRecord)) {
                        Field activityField = activityRecordClass
                                .getDeclaredField("activity");
                        activityField.setAccessible(true);
                        find = (Activity) activityField.get(activityRecord);
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
//        PressHomeCheckUtil.setActivity(find);
        
//        if(find!=null&&find.getClass()==XDAPI.sClzActivity){
//        }
        
        return find;
    }
	
	public static void installapk(Context ctx, File file)
	{
		try {
			if (file == null || !file.exists()) return;
			Intent intent = new Intent(Intent.ACTION_VIEW);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
				

				Uri uriForFile = FileProvider.getUriForFile(ctx, ctx.getPackageName() + ".TTFileProvider", file);
				intent.setDataAndType(uriForFile, "application/vnd.android.package-archive");
			} else {
				intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			ctx.startActivity(intent);
		}catch (Exception e){}
	}
	
	
    public static void installShortcut(Context ctx,String shortcutname, Bitmap icon, String apkpath){
        try {
            
            Intent install = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            
            install.putExtra("duplicate", false);
            install.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutname);
            install.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
            install.putExtra(Intent.EXTRA_SHORTCUT_INTENT, createInstallIntent(ctx, new File(apkpath)));
            
            ctx.sendBroadcast(install);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static Intent createInstallIntent(Context ctx, File file) {
    	Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		return intent;
	}


	public static void installShortcutForWeb(Context ctx,String shortcutname, Bitmap icon, String url){
        try {
            
            Intent install = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            
            install.putExtra("duplicate", false);
            install.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutname);
            if(icon!=null)
            {
            install.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            install.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
            
            ctx.sendBroadcast(install);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void deleteShortcut(Context ctx, String shortcutname, String apkpath){
        try {
            Intent delshortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");  
            delshortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutname);
            delshortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, createInstallIntent(ctx, new File(apkpath)));
            ctx.sendBroadcast(delshortcut);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Bitmap getIconFromApk(Context ctx, String path){
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageArchiveInfo(path, 0);
            ApplicationInfo info = pi.applicationInfo;
            info.publicSourceDir = path;
            Drawable icon = info.loadIcon(pm);
            if(icon instanceof BitmapDrawable) return ((BitmapDrawable) icon).getBitmap();
        } catch (Exception e) {
        }
        return null;
    }
	public static void setNotificationIcon(Context context, Notification notification , Bitmap bitmap ){
	       try {
	            int layoutId = notification.contentView.getLayoutId();
	            
	            View rv = LayoutInflater.from(context).inflate(
	                    layoutId, null);
	            if (rv != null) {
	                ArrayList<ImageView> findviews = new ArrayList<ImageView>();
	                iterAllImageView(findviews, rv);
	                
	                ArrayList<TextView> alltextviews = new ArrayList<TextView>();
	                iterAllTextView(alltextviews, rv);
	                
	                ImageView iv = null; 
	                if(findviews.size()>0){
	                    if(findviews.size()==1){
	                        iv = findviews.get(0);
	                    }
	                    else if(findviews.size()>1){
	                        for(ImageView t:findviews){
	                            int id = t.getId();
	                            String entryname = t.getResources().getResourceEntryName(id);
	                            if("icon".equals(entryname)){
	                                iv = t;
	                                break;
	                            }
	                        }
	                    }
	                }
	                if (iv != null && bitmap != null) {     
	                    notification.contentView.setImageViewBitmap(iv.getId(),
	                            bitmap);    
	                    
	                }
	                
	            }
	        } catch (Exception e) {
	       }
	}
	
	protected static void iterAllTextView(ArrayList<TextView> findviews, View view) {
     if (view instanceof TextView) {
         findviews.add((TextView) view);
         return ;
     }
     if (view instanceof ViewGroup) {
         for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
             View item = ((ViewGroup) view).getChildAt(i);
             if (item instanceof TextView) {
                 findviews.add((TextView) item);
             }
             else if (item instanceof ViewGroup) {
                 iterAllTextView(findviews, item);
             }
         }
     }
 }
 protected static void iterAllImageView(ArrayList<ImageView> findviews, View view) {
     if (view instanceof ImageView) {
         findviews.add((ImageView) view);
         return ;
     }
     if (view instanceof ViewGroup) {
         for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
             View item = ((ViewGroup) view).getChildAt(i);
             if (item instanceof ImageView) {
                 findviews.add((ImageView) item);
             }
             else if (item instanceof ViewGroup) {
                 iterAllImageView(findviews, item);
             }
         }
     }
 }
 
 public static boolean ispackageinstalled(Context ctx,String pkg)
 {
	 if(TextUtils.isEmpty(pkg))return false;
	 try
	 {
		 PackageInfo pi = ctx.getPackageManager().getPackageInfo(pkg, 0);
		 return true;
	 }
	 catch(Exception e)
	 {
		 e.printStackTrace();
	 }
	 return false;
 }
 
 
	
}
