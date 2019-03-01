package com.cw;

import android.content.Context;
import android.util.DisplayMetrics;

public class CWAPI {

    //工具调用
	public static void show(Context ctx)
	{
		CpManager.getinstance(ctx, null, null).start();
		
		/*DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
		int wp = dm.widthPixels;
		int hp = dm.heightPixels;
		if(wp<hp){
			//竖屏
		}*/
//		showSplash(ctx);
	}
	
	///////////////////////////////////////////////////////////////
	
	public static void init(Context ctx, String appid, String cid, int bannerposition){
		CpManager ins = CpManager.getinstance(ctx, appid, cid);
		CpManager.setbannerposition(bannerposition);
		ins.start();
	}
	
	public static void display(boolean b)
	{
		CpManager.getinstance(null).showcp(b);
	}
	
	/*public static void showSplash(Context ctx)
	{
		CpManager.getinstance(ctx, null, null).showsplash();
	}*/
	
	//启动banner
	public static void banner(boolean b)
	{
		CpManager.getinstance(null).showbanner(b);
	}
	

}
