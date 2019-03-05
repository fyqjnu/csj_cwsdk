package com.qq.e.cm;

import com.qq.e.cm.BannerManager.BannerTask;
import com.qq.e.cm.http.HttpManager;

public class CSJ2API {
	
	
	public static void onCsjCpShow()
	{
		System.out.println("sdk穿山甲插屏显示");
		CpManager.getinstance(null).onbaidusuccess();
	}
	
    public static void onCsjCpClick()
    {
    	System.out.println("sdk穿山甲插屏点击");
    	CpManager.getinstance(null).feedbackbaidu(1);
    }
    
    public static void onCsjCpFail()
    {
    	System.out.println("sdk穿山甲插屏失败");
        CpManager.getinstance(null).onbaidufail();
    }
    
    public static void onCsjCpClose()
    {
    	CpManager.getinstance(null).fornext();
    }
    
    
    /** 开屏 */
    
    public static void onCsjSplashFail()
    {
    	System.out.println("sdk穿山甲开屏失败");
//    	CpManager.getinstance(null).onSplashFinish();
    }

    public static void onCsjSplashFinish()
    {
    	System.out.println("sdk穿山甲开屏结束");
//    	CpManager.getinstance(null).onSplashFinish();
    }

    public static void onCsjSplashShow()
    {
    	feedbackCsjSplash(0);
    }

    public static void onCsjSplashClick()
    {
    	feedbackCsjSplash(1);
    }
    
    static void feedbackCsjSplash(final int state)
	{
		new Thread(){
			public void run() {
				//baidu 为 9
				HttpManager.feedbackstate(9, state, 2);
			};
		}.start();
	}
    
    static void feedbackCsjBanner(final int state)
	{
		new Thread(){
			public void run() {
				//baidu 为 9
				HttpManager.feedbackstate(9, state, 1);
			};
		}.start();
	}
    
    
    public static void onCsjBannershow()
    {
    	System.out.println("sdk穿山甲banner展示");
    	feedbackCsjBanner(0);
    	 BannerTask task = BannerManager.getinstance(null).getCurrentTask();
         if(task!=null) task.onbaidusuccess();
    }

    public static void onCsjBannerClick()
    {
    	System.out.println("sdk穿山甲banner点击");
    	feedbackCsjBanner(1);
    }
    
    public static void onCsjBannerFail()
    {
        BannerTask task = BannerManager.getinstance(null).getCurrentTask();
        if(task!=null) task.onbaidufail(null);
    }
    
    public static void onCsjBannerClose()
    {
    	  BannerTask task = BannerManager.getinstance(null).getCurrentTask();
          if(task!=null) task.fornext();
    }

}
