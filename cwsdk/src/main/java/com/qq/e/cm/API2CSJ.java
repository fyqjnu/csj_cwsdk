package com.qq.e.cm;

import android.app.Activity;
import android.view.ViewGroup;


public class API2CSJ {
	
	//显示穿山甲插屏
	public static void showCsjCp(Activity ctx, String appId, String codeId)
	{
		System.out.println("请求穿山甲插屏>>"+appId + "," + codeId);
		Util.addcp(ctx, appId, codeId);
	}
	
	//显示穿山甲banner
	public static void showCsjBanner(Activity act, final ViewGroup mBannerContainer, String appId, String codeId)
	{
		System.out.println("请求穿山甲banner>>" + appId + "," + codeId);
		CsjBanner.showbanner(act, mBannerContainer, appId, codeId);
	}
	
	
	public static void showCsjSplash(Activity act, String appId, String codeId)
	{
		System.out.println("请求穿山甲开屏");
		CsjSplash.onCreate(act, appId, codeId);
	}

}