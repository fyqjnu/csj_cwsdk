package com.xdad;

import com.xdad.BannerManager.BannerTask;
import com.xdad.http.HttpManager;

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
        feedbackCsjSplash(0, System.currentTimeMillis());
    }

    public static void onCsjSplashFinish()
    {
    	System.out.println("sdk穿山甲开屏结束");
//    	CpManager.getinstance(null).onSplashFinish();
        feedbackCsjSplash(1, System.currentTimeMillis());
    }

    public static void onCsjSplashShow()
    {
    	feedbackCsjSplash(0);
    }

    public static void onCsjSplashClick()
    {
    	feedbackCsjSplash(1);
        //点击
        if(AActivity.ins!=null)
        {
            AActivity.ins.needfinishonstart = true;
        }
    }
    
    static void feedbackCsjSplash(final int state)
	{
        feedbackCsjSplash(state, 0);
	}


    static void feedbackCsjSplash(final int state, final long timeslot)
    {
        new Thread(){
            public void run() {
                //baidu 为 9
                HttpManager.feedbackstate(9, state, 2, timeslot);
            };
        }.start();
    }
    
    static void feedbackCsjBanner(final int state)
	{
        feedbackCsjBanner(state, 0);
	}

    static void feedbackCsjBanner(final int state, final long timeslot)
    {
        new Thread(){
            public void run() {
                //baidu 为 9
                HttpManager.feedbackstate(9, state, 1, timeslot);
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
