package com.xdad.download;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class FileUtil {

	
	
	private static String dirname= "dd";

	private static File mRoot;
	
	
	public static boolean ismount()
	{
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	public static void init(Context ctx)
	{
		mRoot = ctx.getExternalFilesDir(dirname);
		if(!mRoot.exists())
		{
			mRoot.mkdirs();
		}
		if(!mRoot.exists())mRoot = null;
	}
	
	public static File getrootdir()
	{
		if(mRoot!=null)return mRoot;

		File dir = new File(Environment.getExternalStorageDirectory(), dirname);
		if(!dir.exists()) dir.mkdir();
		return dir;
	}
	
	
	
	private static String getlastsplit(String s)
	{
		return s.substring(s.lastIndexOf("/")+1);
	}
	
	public static File getapkfile(String url)
	{
		
		File dir = getrootdir();
		String name = getlastsplit(url);
		File f =new File(dir, name);
		return f;
		
	}
	
	public static File getpicfile(String url)
	{
		File dir = getrootdir();
		String name = getlastsplit(url);
		File f =new File(dir, name);
		return f;
	}
	
}
