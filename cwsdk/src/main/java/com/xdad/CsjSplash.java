package com.xdad;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.MainThread;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTSplashAd;

import java.lang.ref.WeakReference;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2019/2/14 0014.
 */

public class CsjSplash {


    private static final int AD_TIME_OUT = 2000;
    private static final int MSG_GO_MAIN = 1;
    //开屏广告是否已经加载
    static boolean mHasLoaded;

    static TTAdNative mTTAdNative;

    static Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case MSG_GO_MAIN:
                    if (!mHasLoaded) {
                        showToast("广告已超时，跳到主页面");
                        goToMainActivity();
                    }
                    break;
            }
        }
    };

    static WeakReference<Activity> actRef;


    static FrameLayout mSplashContainer;

    public static void onCreate(Activity act, String appId, String codeId)
    {
        actRef = new WeakReference<Activity>(act);
        FrameLayout container = new FrameLayout(act);
        act.setContentView(container);
        mSplashContainer = container;

        TTAdManagerHolder.init(act.getApplicationContext(),appId);
        //step2:创建TTAdNative对象
        mTTAdNative = TTAdManagerHolder.get().createAdNative(act);
        //在合适的时机申请权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题
        //在开屏时候申请不太合适，因为该页面倒计时结束或者请求超时会跳转，在该页面申请权限，体验不好
        // TTAdManagerHolder.getInstance(this).requestPermissionIfNecessary(this);
        //定时，AD_TIME_OUT时间到时执行，如果开屏广告没有加载则跳转到主页面
        mHandler.sendEmptyMessageDelayed(MSG_GO_MAIN, AD_TIME_OUT);
        //加载开屏广告
        loadSplashAd(codeId);
    }

    /**
     * 加载开屏广告
     */
    private static void loadSplashAd(String codeId) {
        //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .build();
        //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
        mTTAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
            @Override
            @MainThread
            public void onError(int code, String message) {
                Log.d(TAG, message);
                mHasLoaded = true;
                showToast(message);
                goToMainActivity();

                CSJ2API.onCsjSplashFail();
            }

            @Override
            @MainThread
            public void onTimeout() {
                mHasLoaded = true;
                showToast("开屏广告加载超时");
                goToMainActivity();

                CSJ2API.onCsjSplashFail();
            }

            @Override
            @MainThread
            public void onSplashAdLoad(TTSplashAd ad) {
                Log.d(TAG, "开屏广告请求成功");
                mHasLoaded = true;
                mHandler.removeCallbacksAndMessages(null);
                if (ad == null) {
                    return;
                }
                //获取SplashView
                View view = ad.getSplashView();
                mSplashContainer.removeAllViews();
                //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕宽
                mSplashContainer.addView(view);
                //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
                //ad.setNotAllowSdkCountdown();

                //设置SplashView的交互监听器
                ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                        Log.d(TAG, "onAdClicked");
                        showToast("开屏广告点击");


                        CSJ2API.onCsjSplashClick();
                    }

                    @Override
                    public void onAdShow(View view, int type) {
                        Log.d(TAG, "onAdShow");
                        showToast("开屏广告展示");

                        CSJ2API.onCsjSplashShow();
                    }

                    @Override
                    public void onAdSkip() {
                        Log.d(TAG, "onAdSkip");
                        showToast("开屏广告跳过");
                        goToMainActivity();

                        CSJ2API.onCsjSplashFinish();

                    }

                    @Override
                    public void onAdTimeOver() {
                        Log.d(TAG, "onAdTimeOver");
                        showToast("开屏广告倒计时结束");
                        goToMainActivity();


                        CSJ2API.onCsjSplashFinish();
                    }
                });
            }
        }, AD_TIME_OUT);
    }

    /**
     * 跳转到主页面
     */
    private static void goToMainActivity() {
        System.out.println();
        if(actRef.get()==null)return;
        try {
            Class<?> c = Class.forName("com.example.administrator.myapplication.MainActivity");
            Intent intent = new Intent();
            intent.setClassName(actRef.get(), "com.example.administrator.myapplication.MainActivity");
            actRef.get().startActivity(intent);
        }catch (Exception e){
        }
        actRef.get().finish();
    }

    private static void showToast(String msg) {
        if(actRef.get()!=null)
            TToast.show(actRef.get(), msg);
    }


}
