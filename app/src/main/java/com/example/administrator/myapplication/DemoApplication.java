package com.example.administrator.myapplication;

import android.app.Application;

import com.xdad.CWAPI;

/**
 * Create by hanweiwei on 11/07/2018
 */
@SuppressWarnings("unused")
public class DemoApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        //b1b1679cbe4345aab5850e84  俊澎
        //830de89e28fb4c7b8d2112e3  测试
        CWAPI.init(this, "a", null);
        //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现content为null的异常
//        TTAdManagerHolder.init(this);

        //如果明确某个进程不会使用到广告SDK，可以只针对特定进程初始化广告SDK的content
        //if (PROCESS_NAME_XXXX.equals(processName)) {
        //   TTAdManagerHolder.init(this)
        //}


    }

}
