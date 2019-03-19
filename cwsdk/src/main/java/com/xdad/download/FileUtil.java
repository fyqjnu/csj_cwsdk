package com.xdad.download;

import java.io.File;

import android.os.Environment;

public class FileUtil {

	
	
	private static String dirname= "dd";
	
	
	public static boolean ismount()
	{
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}
	
	public static File getrootdir()
	{
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
