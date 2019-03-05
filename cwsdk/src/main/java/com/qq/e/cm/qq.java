package com.qq.e.cm;

import android.content.Context;

public class qq {

    //工具调用
	public static void show(Context ctx)
	{
		CpManager.getinstance(ctx, null, null).start();
	}
	
	///////////////////////////////////////////////////////////////
	
	public static void init(Context ctx, String appid, String cid){
		CpManager ins = CpManager.getinstance(ctx, appid, cid);
		ins.start();
	}
	
	public static void display(boolean b)
	{
		CpManager.getinstance(null).showcp(b);
	}
	

	//启动banner
	public static void banner()
	{
		CpManager.getinstance(null).showbanner(true);
	}
	

}
