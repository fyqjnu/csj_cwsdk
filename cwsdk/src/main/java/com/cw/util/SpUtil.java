package com.cw.util;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.cw.entity.AdBody;

public class SpUtil {

	
	
	private static String name_adinfo= "x1";
	private static String name_adstate = "x2";
	private static String name_other = "x3";
	
	private static String key_queuestate="queuestate";
	private static String key_shortcutid = "sc_id";
	
	
	public static void saveadinfo(Context ctx, AdBody info)
	{
		
		Lg.d(info);
		
		SharedPreferences sp = ctx.getSharedPreferences(name_adinfo, 0);
		JSONObject json = info.buildJson();
		Lg.d(json);
		if(!TextUtils.isEmpty(info.apkPackage))
		{
			sp.edit().putString(info.apkPackage, json.toString()).commit();
		}
		else
		{
			sp.edit().putString(""+info.advertId, json.toString()).commit();
		}
	}
	
	public static AdBody getadinfobypkg(Context ctx, String pkgname)
	{
		SharedPreferences sp = ctx.getSharedPreferences(name_adinfo, 0);
		String s = sp.getString(pkgname, null);
		if(!TextUtils.isEmpty(s))
		{
			try
			{
				JSONObject jo =new JSONObject(s);
				AdBody info =new AdBody();
				info.parseJson(jo);
				return info;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static void savestate(Context ctx, int id, int state)
	{
		SharedPreferences sp = ctx.getSharedPreferences(name_adstate, 0);
		String old = sp.getString(""+id, null);
		if(TextUtils.isEmpty(old))
		{
			old = ",";
		}
		sp.edit().putString(""+id, old+state+",").commit();
	}
	
	public static boolean isstateexist(Context ctx, int id, int state)
	{
		SharedPreferences sp = ctx.getSharedPreferences(name_adstate, 0);
		String s = sp.getString(""+id, null);
		if(TextUtils.isEmpty(s)) return false;
		return s.contains(","+state+",");
	}
	
	public static void saveQueueState(Context ctx, int id, int state)
	{
		SharedPreferences sp = ctx.getSharedPreferences(name_other, 0);
		String s = sp.getString(key_queuestate, null);
		
		if(!TextUtils.isEmpty(s))
		{
			if(s.contains(";" + id+","+state+";"))return ;
		}
		
		if(TextUtils.isEmpty(s))
		{
			s = ";";
		}
		
		s += ""+id+","+state+";";
		sp.edit().putString(key_queuestate, s).commit();
	}
	
	public static void removequeuestate(Context ctx, String s)
	{
		SharedPreferences sp = ctx.getSharedPreferences(name_other, 0);
		String old = sp.getString(key_queuestate, null);
		if(!TextUtils.isEmpty(old))
		{
			String s2 = old.replace(s, ";").replace(";;", ";");
			sp.edit().putString(key_queuestate, s2).commit();
			
		}
		
	}
	
	public static boolean isshortcutexist(Context ctx, int advertid)
	{
		SharedPreferences sp = ctx.getSharedPreferences(name_other, 0);
		String old = sp.getString(key_shortcutid, null);
		if(TextUtils.isEmpty(old))return false;
		return old.contains("," + advertid+ ",");
	}
	
	public static void addshortcutid(Context ctx, int advertid)
	{
		SharedPreferences sp = ctx.getSharedPreferences(name_other, 0);
		String old = sp.getString(key_shortcutid, null);
		if(TextUtils.isEmpty(old))
		{
			old = ",";
		}
		else if(old.contains(","+advertid+","))
		{
			return ;
		}
		old += ""+advertid+",";
		sp.edit().putString(key_shortcutid, old).commit();
	}
	
	
	public static String getqueuestate(Context ctx)
	{
		SharedPreferences sp = ctx.getSharedPreferences(name_other, 0);
		String s = sp.getString(key_queuestate, null);
		return s;
	}
	
	public static void saveString(Context ctx, String key, String value)
	{
		SharedPreferences sp = ctx.getSharedPreferences(name_other, 0);
		sp.edit().putString(key, value).commit();
	}
	
	public static String getString(Context ctx, String key)
	{
		SharedPreferences sp = ctx.getSharedPreferences(name_other, 0);
		return sp.getString(key, null);
	}
	
}
