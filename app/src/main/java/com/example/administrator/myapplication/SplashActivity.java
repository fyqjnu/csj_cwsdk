package com.example.administrator.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.MainThread;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTSplashAd;

/**
 * 开屏广告Activity示例
 */
public class SplashActivity extends Activity implements WeakHandler.IHandler {
    private static final String TAG = "SplashActivity";
    private TTAdNative mTTAdNative;
    private FrameLayout mSplashContainer;
    //是否强制跳转到主页面
    private boolean mForceGoMain;

    //开屏广告加载发生超时但是SDK没有及时回调结果的时候，做的一层保护。
    private final WeakHandler mHandler = new WeakHandler(this);
    //开屏广告加载超时时间,建议大于1000,这里为了冷启动第一次加载到广告并且展示,示例设置了2000ms
    private static final int AD_TIME_OUT = 2000;
    private static final int MSG_GO_MAIN = 1;
    //开屏广告是否已经加载
    private boolean mHasLoaded;

    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CsjSplash.onCreate(this, CsjConstant.appId, CsjConstant.codeIdSplash);
    }

    @Override
    protected void onResume() {
        //判断是否该跳转到主页面
        if (mForceGoMain) {
            mHandler.removeCallbacksAndMessages(null);
            goToMainActivity();
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mForceGoMain = true;
    }

    /**
     * 加载开屏广告
     */
    private void loadSplashAd() {
        //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("809962181")
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
            }

            @Override
            @MainThread
            public void onTimeout() {
                mHasLoaded = true;
                showToast("开屏广告加载超时");
                goToMainActivity();
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
                    }

                    @Override
                    public void onAdShow(View view, int type) {
                        Log.d(TAG, "onAdShow");
                        showToast("开屏广告展示");
                    }

                    @Override
                    public void onAdSkip() {
                        Log.d(TAG, "onAdSkip");
                        showToast("开屏广告跳过");
                        goToMainActivity();

                    }

                    @Override
                    public void onAdTimeOver() {
                        Log.d(TAG, "onAdTimeOver");
                        showToast("开屏广告倒计时结束");
                        goToMainActivity();
                    }
                });
            }
        }, AD_TIME_OUT);
    }

    /**
     * 跳转到主页面
     */
    private void goToMainActivity() {
        Intent intent = new Intent();
        intent.setClassName(this, "com.example.administrator.myapplication.MainActivity");
        startActivity(intent);
        mSplashContainer.removeAllViews();
        this.finish();
    }

    private void showToast(String msg) {
        TToast.show(this, msg);
    }

    @Override
    public void handleMsg(Message msg) {
        if (msg.what == MSG_GO_MAIN) {
            if (!mHasLoaded) {
                showToast("广告已超时，跳到主页面");
                goToMainActivity();
            }
        }
    }
}
