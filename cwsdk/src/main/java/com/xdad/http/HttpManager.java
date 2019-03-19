package com.xdad.http;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;

import com.xdad.entity.AdBody;
import com.xdad.entity.DeviceProperty;
import com.xdad.entity.Result;
import com.xdad.util.Kode;
import com.xdad.util.Lg;
import com.xdad.util.SpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class HttpManager {

	
	
	static boolean ablezip = true;
	
	//http://api.xsoc.org/n
	static String host = "http://bd.xsqu8.cn/n";
	
	//c=5加密版本
	//请求广告信息
	private static String url_adbody=host+"?requestId=0&g=1&c=4&t=%d";//&r=1
	//返回广告状态
	private static String url_state = host+"?requestId=1&g=1&c=4";//&r=2

	//填充状态
	private static String url_fill = host+"?requestId=2&g=1&c=4";//&r=2
	
	private static  DeviceProperty deviceinfo;
	
	private static Context ctx;
	
	
	//玩咖6
	public static int advertId = -1;
	
	public static void init(Context ctx)
	{
		HttpManager.ctx = ctx.getApplicationContext();
		deviceinfo =new DeviceProperty(ctx);
	}
	
	
	private static String request(String url, String content)
	{
		if(Lg.d) System.out.println("url>" + url);
		if(Lg.d) System.out.println("body>" + content);
		//重复请求一次
		int retry = 2;
		while (retry>0)
		{
			try
			{
				
				Lg.d(url);
				Lg.d(content);
				
				HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
				
				conn.setDoInput(true);
				conn.setDoOutput(true);
				
				//连接超时
				conn.setConnectTimeout(10*1000);
				
				conn.addRequestProperty("Content-Type", "application/octet-stream; charset=utf-8" );
				
				conn.setRequestMethod("POST");
				conn.connect();
				
				OutputStream os = conn.getOutputStream();
				
				byte[] param = zip(encode(content).getBytes());
				
				os.write(param);
				os.flush();
				os.close();
				
				InputStream is = conn.getInputStream();
				byte[] buf = new byte[1024*4];
				int len = 0;
				ByteArrayOutputStream ary = new ByteArrayOutputStream();
				while((len=is.read(buf))>0)
				{
					ary.write(buf,0, len);
				}
				is.close();
				conn.disconnect();
				
				String s = new String(unzip(ary.toByteArray()));
				String json = decode(s);
				Lg.d(json);
				return json;
			}
			catch(Exception e)
			{
				Lg.d(e);
				e.printStackTrace();
				retry--;
			}
		}
		
		return null;
	}
	
	
	public static AdBody[] getadbody()
	{
		return getadbody(4);
	}
	
	
	//adtype >1:banner 4:插屏 2:开屏
	public static synchronized AdBody[] getadbody(int adtype)
	{

		if(!deviceinfo.isvalid())return null;

		try
		{
			String url = String.format(url_adbody, adtype);
			
			
			if(advertId>0)
			{
				url += "&advertId=" + advertId;
				advertId = -1;
			}
			
			if(DeviceProperty.sUa!=null)
			{
				url += "&ua=" + DeviceProperty.sUa;
			}
			
			JSONObject data =new JSONObject();
			deviceinfo.advertState = null;
			data.put(deviceinfo.getShortName(), deviceinfo.buildJson());
			String ss = data.toString();
			
			String json = request(url, ss);
			
			if(Lg.d) Lg.d("adbody>>" + json);
			
			
			if(!TextUtils.isEmpty(json))
			{
				JSONObject jo =new JSONObject(json);
				AdBody t = new AdBody();
				JSONArray ja = jo.getJSONArray(t.getShortName());
				int size = ja.length();
				AdBody[] ad = new AdBody[size];
				for(int i=0;i<size;i++)
				{
					t = new AdBody();
					JSONObject obj = ja.getJSONObject(i);
					t.parseJson(obj);
					
					
					//判断是否激活类型
					if(t.url!=null && (!t.url.startsWith("http://")) && (!t.url.startsWith("https://")))
					{
						boolean success = false;
						try
						{
							Intent intent =new Intent(Intent.ACTION_VIEW);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.setData(Uri.parse(url));
							
							List<ResolveInfo> queryIntentActivities = ctx.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
							if(queryIntentActivities!=null && queryIntentActivities.size()>0)
							{
								ctx.startActivity(intent);
								success = true;
							}
						}
						catch(Exception e)
						{
						}
						
						if(success)
						{
							//上报
							if(t.reportVO!=null)
							{
								TrackUtil.track(t.reportVO.getDplktrackers());
							}
						}
					}
					else
					{
						if(Lg.d)
						{
//							t.type = 2;
//							t.url = "http://www.qq.com";
						}
						//普通广告
						ad[i] = t;
						
					}
					
					//保存信息到xml
					SpUtil.saveadinfo(ctx, t);
					
				}
				return ad;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	
	private static Set<String> stateing = new HashSet<String>();
	
	private static HashSet<String> datas = new HashSet<String>();



	public static void feedbackstate(int advertId, int state, int type) {
		feedbackstate(advertId, state, type, 0);
	}



	
	/*
	 * type:banner1 插屏4,开屏2
	 */
	public static synchronized void feedbackstate(int advertId, int state, int type, long timeSlot)
	{
		Lg.d("feedbackstate");
		//8是广点通 9是百度
		if(advertId!=8 && advertId!=9)
		{
			if(SpUtil.isstateexist(ctx, advertId, state))return ;
			if(stateing.contains("" + advertId+"_" + state))return ;
			stateing.add(""+advertId+"_"+state);
		}

		deviceinfo.advertState = String.format("%d,%d,%d;", advertId, state, type);
		if(timeSlot > 0)
		{
			deviceinfo.advertState = String.valueOf(timeSlot) + ";" + deviceinfo.advertState;
		}
		
		if(advertId!=8 && advertId!=9)
			SpUtil.saveQueueState(ctx, advertId, state);
		
		try
		{
			JSONObject jo =new JSONObject();
			jo.put(deviceinfo.getShortName(), deviceinfo.buildJson());
			
			String data = jo.toString();
			String url = null;
			if(timeSlot>0)
			{
				url = url_fill;
			}
			else {
				url = url_state;
			}
			String s = request(url, data);
			JSONObject obj =new JSONObject(s);
			Result r =new Result();
			r.parseJson(obj.getJSONObject(r.getShortName()));
			if(r.resultCode>=0)
			{
				SpUtil.savestate(ctx, advertId, state);
				SpUtil.removequeuestate(ctx, ";"+advertId +","+state+";");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stateing.remove(""+advertId+"_" + state);
		}
		
		
	}

	public static synchronized void feedbackstate(String fillstate)
	{
		Lg.d("feedbackstate");
		deviceinfo.advertState = fillstate;
		try
		{
			JSONObject jo =new JSONObject();
			jo.put(deviceinfo.getShortName(), deviceinfo.buildJson());

			String data = jo.toString();
			String url = url_fill;
			String s = request(url, data);
			JSONObject obj =new JSONObject(s);
			Result r =new Result();
			r.parseJson(obj.getJSONObject(r.getShortName()));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
		}
	}


	private static byte[] zip(byte[] data)
	{
		
		if(!ablezip) return data;
		
		try
		{
			ByteArrayOutputStream os =new ByteArrayOutputStream();
			GZIPOutputStream zo = new GZIPOutputStream(os);
			zo.write(data);
			zo.flush();
			zo.finish();
			zo.close();
			return os.toByteArray();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	private static byte[] unzip(byte[] data)
	{
		
		if(!ablezip) return data;
		
		try
		{
			ByteArrayOutputStream os =new ByteArrayOutputStream();
			GZIPInputStream zi =new GZIPInputStream(new ByteArrayInputStream(data));
			
			byte[] buf =new byte[1024*4];
			int len= 0;
			while((len=zi.read(buf))>0)
			{
				os.write(buf, 0, len);
			}
			
			zi.close();
			os.flush();
			os.close();
			return os.toByteArray();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	private static String encode(String s)
	{
		return Kode.a(s);
	}
	
	private static String decode(String s)
	{
		return Kode.e(s);
	}
	
	
}
