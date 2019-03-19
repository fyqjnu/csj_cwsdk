package com.xdad;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTBannerAd;

/**
 * Created by Administrator on 2019/2/14 0014.
 */

public class CsjBanner {

    public static void showbanner(Activity act, final ViewGroup mBannerContainer, String appId, String codeId)
    {
        System.out.println("穿山甲showbanner>>" + mBannerContainer);
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId) //广告位id
                .setSupportDeepLink(true)
                .setImageAcceptedSize(600, 90)
                .build();
        TTAdManagerHolder.init(act.getApplicationContext(),appId);
        //step2:创建TTAdNative对象
        TTAdNative mTTAdNative = TTAdManagerHolder.get().createAdNative(act);
        //step5:请求广告，对请求回调的广告作渲染处理
        mTTAdNative.loadBannerAd(adSlot, new TTAdNative.BannerAdListener() {

            @Override
            public void onError(int code, String message) {

//                TToast.show(LaiyueActivity.this, "load error : " + code + ", " + message);
                System.out.println("穿山甲banner加载失败 : " + code + ", " + message);
                mBannerContainer.removeAllViews();
                CSJ2API.onCsjBannerFail();
            }

            @Override
            public void onBannerAdLoad(final TTBannerAd ad) {
                System.out.println("穿山甲加载成功TTBannerAd" + ad);
                if (ad == null) {
                    return;
                }
                View bannerView = ad.getBannerView();
                System.out.println("穿山甲加载成功bannerView" + bannerView);
                if (bannerView == null) {
                    return;
                }
                //设置轮播的时间间隔  间隔在30s到120秒之间的值，不设置默认不轮播
                ad.setSlideIntervalTime(30 * 1000);
                mBannerContainer.removeAllViews();
                mBannerContainer.addView(bannerView);
                System.out.println("width>>" + mBannerContainer.getWidth() + "," + mBannerContainer.getHeight());
                //设置广告互动监听回调
                CSJ2API.onCsjBannershow();
                ad.setBannerInteractionListener(new TTBannerAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
//                        TToast.show(mContext, "广告被点击");
                        System.out.println("穿山甲banner点击");
                        CSJ2API.onCsjBannerClick();
                    }

                    @Override
                    public void onAdShow(View view, int type) {
//                        TToast.show(mContext, "广告展示");
                        System.out.println("穿山甲banner展示成功");
                        CSJ2API.onCsjBannershow();
                    }
                });
                //（可选）设置下载类广告的下载监听
//                bindDownloadListener(ad);
                //在banner中显示网盟提供的dislike icon，有助于广告投放精准度提升
                ad.setShowDislikeIcon(new TTAdDislike.DislikeInteractionCallback() {
                    @Override
                    public void onSelected(int position, String value) {
//                        TToast.show(mContext, "点击 " + value);
                        //用户选择不喜欢原因后，移除广告展示
                        mBannerContainer.removeAllViews();
                    }

                    @Override
                    public void onCancel() {
//                        TToast.show(mContext, "点击取消 ");
                        System.out.println("穿山甲banner取消");
                        CSJ2API.onCsjBannerClose();
                    }
                });

                //获取网盟dislike dialog，您可以在您应用中本身自定义的dislike icon 按钮中设置 mTTAdDislike.showDislikeDialog();
                /*mTTAdDislike = ad.getDislikeDialog(new TTAdDislike.DislikeInteractionCallback() {
                        @Override
                        public void onSelected(int position, String value) {
                            TToast.show(mContext, "点击 " + value);
                        }

                        @Override
                        public void onCancel() {
                            TToast.show(mContext, "点击取消 ");
                        }
                    });
                if (mTTAdDislike != null) {
                    XXX.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTTAdDislike.showDislikeDialog();
                        }
                    });
                } */

            }
        });
    }


}
