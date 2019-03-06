package com.example.administrator.myapplication;

import android.app.Activity;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTInteractionAd;
import com.qq.e.cm.CSJ2API;
import com.qq.e.cm.CpManager;

/**
 * Created by Administrator on 2019/1/23 0023.
 */

public class Util {


    public static void addcp(final Activity ctx, String appId, String codeIdCp)
    {

        TTAdManagerHolder.init(ctx, appId);

        TTAdManager mTTAdManager = TTAdManagerHolder.get();
        //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        mTTAdManager.requestPermissionIfNecessary(ctx);

        TTAdNative ttAdNative = mTTAdManager.createAdNative(ctx);

        //step4:创建插屏广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeIdCp)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(600, 600) //根据广告平台选择的尺寸，传入同比例尺寸
                .build();
        //step5:请求广告，调用插屏广告异步请求接口

        ttAdNative.loadInteractionAd(adSlot, new TTAdNative.InteractionAdListener() {
            @Override
            public void onError(int code, String message) {
                TToast.show(ctx.getApplicationContext(), "code: " + code + "  message: " + message);
                System.out.println("穿山甲插屏失败>>code: " + code + "  message: " + message);
                CSJ2API.onCsjCpFail();
            }

            @Override
            public void onInteractionAdLoad(TTInteractionAd ttInteractionAd) {
                try {
                    if (CpManager.getinstance(null).isshowing()) return;
                }catch (Exception e){}

                ttInteractionAd.setAdInteractionListener(new TTInteractionAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked() {
                        System.out.println("点击");
                        CSJ2API.onCsjCpClick();
                    }

                    @Override
                    public void onAdShow() {
                        System.out.println("展示");
                        CSJ2API.onCsjCpShow();
                    }

                    @Override
                    public void onAdDismiss() {
                        System.out.println("消失");
                        CSJ2API.onCsjCpClose();
                    }
                });
                //如果是下载类型的广告，可以注册下载状态回调监听
                if (ttInteractionAd.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
                    ttInteractionAd.setDownloadListener(new TTAppDownloadListener() {
                        @Override
                        public void onIdle() {
                        }

                        @Override
                        public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                        }

                        @Override
                        public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                        }

                        @Override
                        public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                        }

                        @Override
                        public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                        }

                        @Override
                        public void onInstalled(String fileName, String appName) {
                        }
                    });
                }
                //弹出插屏广告
                ttInteractionAd.showInteractionAd(ctx);
            }
        });
    }

}
