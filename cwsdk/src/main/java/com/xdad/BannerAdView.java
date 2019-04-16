package com.xdad;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.BannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.comm.util.AdError;
import com.xdad.http.HttpManager;
import com.xdad.util.CpUtils;

/**
 * Created by Administrator on 2019/3/19 0019.
 */

public class BannerAdView extends FrameLayout {
    public BannerAdView(@NonNull Context context) {
        super(context);
        oncreate();
    }

    public BannerAdView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        oncreate();
    }

    public BannerAdView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        oncreate();

    }

    void oncreate()
    {
//        setBackgroundColor(Color.CYAN);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                start();
            }
        }, 5000);
    }

    /*public BannerAdView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }*/

    String requestorder = "3,1,2";

    String currentorder;

    private String gdt_bannerid;
    private String gdt_appid;
    private String bd_bannerid;
    private String bd_appid;


    void start() {
        init();

        currentorder = requestorder.replace(",", "");
        request();
    }


    int w;
    int h;

    private void init() {
        int sw = CpUtils.getscreenwidth(getContext());
        int sh = CpUtils.getscreenheight(getContext());

        w = Math.min(sw, sh);
        h = (int) (w * 2.9f / 20);

        if (!TextUtils.isEmpty(com.xdad.CpManager.bd_appid))
            bd_appid = com.xdad.CpManager.bd_appid;
        if (!TextUtils.isEmpty(com.xdad.CpManager.bd_bannerpid))
            bd_bannerid = com.xdad.CpManager.bd_bannerpid;
        if (!TextUtils.isEmpty(com.xdad.CpManager.gdt_appid))
            gdt_appid = com.xdad.CpManager.gdt_appid;
        if (!TextUtils.isEmpty(com.xdad.CpManager.gdt_bannerpid))
            gdt_bannerid = com.xdad.CpManager.gdt_bannerpid;

        if (!TextUtils.isEmpty(com.xdad.CpManager.bannerrequestorder))
            requestorder = com.xdad.CpManager.bannerrequestorder;

//        requestorder = "2,3,1";
    }

    static Handler handler = new Handler(Looper.getMainLooper());

    Runnable nextrequest = new Runnable() {
        @Override
        public void run() {
            start();
        }
    };

    void fornext() {

        handler.postDelayed(nextrequest, 60 * 1000);
    }

    private void request() {
        if (TextUtils.isEmpty(currentorder)) {
            fornext();
            return;
        }

        String top = currentorder.substring(0, 1);
        if (currentorder.length() > 1) {
            currentorder = currentorder.substring(1);
        } else {
            currentorder = "";
        }

        if ("1".equals(top)) {
            //
            if (TextUtils.isEmpty(gdt_appid) || TextUtils.isEmpty(gdt_bannerid)) {
                request();
            } else {
                requestgdt((Activity) getContext());
            }
        } else if ("2".equals(top)) {
            requestapi();
        } else if ("3".equals(top)) {
            if (TextUtils.isEmpty(bd_appid) || TextUtils.isEmpty(bd_bannerid)) {
                request();
            } else {
                requestcsj((Activity) getContext(), this, bd_appid, bd_bannerid);
            }
        }

    }

    void oncsjfail() {
        System.out.println("穿山甲banner失败");
        handler.post(new Runnable() {
            @Override
            public void run() {
                removeAllViews();
            }
        });
        request();
    }

    void ongdtfail() {
        System.out.println("广点通banner失败");
        handler.post(new Runnable() {
            @Override
            public void run() {
                removeAllViews();
            }
        });
        request();
    }



    void feedbackgdt(final int state, final long timeslot) {
    new Thread(){
        @Override
        public void run() {
            HttpManager.feedbackstate(8, state, 1, timeslot);
        }
    }.start();
    }

    void feedbackcsj(final  int state, final long timeslot)
    {
        new Thread(){
            @Override
            public void run() {
                HttpManager.feedbackstate(9, state, 1, timeslot);
            }
        }.start();
    }

    void onapifail() {
        System.out.println("api banner失败");
        handler.post(new Runnable() {
            @Override
            public void run() {
                removeAllViews();
            }
        });
        request();
    }

    void requestcsj(Activity act, final ViewGroup mBannerContainer, String appId, String codeId) {
        oncsjfail();
    }

    void requestgdt(Activity act) {
        final BannerView gdt = new BannerView(act, ADSize.BANNER, gdt_appid, gdt_bannerid);
        gdt.setShowClose(true);
        gdt.setRefresh(30);
        gdt.setADListener(new BannerADListener() {

            @Override
            public void onNoAD(AdError arg0) {
                ongdtfail();

                //填充
                feedbackgdt(0, System.currentTimeMillis());
            }

            @Override
            public void onADReceiv() {
            }

            @Override
            public void onADOpenOverlay() {
            }

            @Override
            public void onADLeftApplication() {
            }

            @Override
            public void onADExposure() {
                feedbackgdt(0, 0);

                feedbackgdt(1, System.currentTimeMillis());
            }

            @Override
            public void onADClosed() {
                fornext();
            }

            @Override
            public void onADCloseOverlay() {
            }

            @Override
            public void onADClicked() {
                System.out.println("gdt banner点击");
                feedbackgdt(1, 0);
            }
        });

        addView(gdt, w, h);
        gdt.loadAD();

        feedbackgdt(-2, 0);
    }

    void requestapi() {
        MyBanner mb = new MyBanner(getContext());
        mb.listener = new MyBanner.MyBannerListener() {
            @Override
            public void onshow() {

            }

            @Override
            public void onclose(View v) {
                removeAllViews();
                fornext();
            }

            @Override
            public void onfail(View v) {
                onapifail();
            }
        };
        addView(mb, w, h);
    }



}
