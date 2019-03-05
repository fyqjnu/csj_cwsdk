package com.qq.e.cm.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapHelper {

	
	
	private static Map<String, Bitmap> bitmaps = new HashMap<String, Bitmap>();
	
	
	public static Bitmap getbitmap(File file)
	{
		Bitmap b = bitmaps.get(file.getPath());
		if(b==null)
		{
			b = BitmapFactory.decodeFile(file.getPath());
			bitmaps.put(file.getPath(), b);
		}
		return b;
	}
	
}
