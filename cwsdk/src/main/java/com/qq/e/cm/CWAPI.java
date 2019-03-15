package com.qq.e.cm;

import android.app.Activity;
import android.content.Context;

public class CWAPI {
    //工具调用
	static void show(Context ctx)
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

	//激励视频初始化
	public static void loadRewardVideo(String userid, String rewardName, int rewardAmount, RewardVideoLoadListener listener) {
		CpManager.userId = userid;
		CpManager.rewardName = rewardName;
		CpManager.rewardAmount = rewardAmount;

		CpManager.getinstance(null).loadRewardVideo();
	}

	public static void showRewardVideo(Activity a, RewardVideoPlayListener listener)
	{
			CpManager.getinstance(a).showRewardVideo(a, listener);
	}

}
