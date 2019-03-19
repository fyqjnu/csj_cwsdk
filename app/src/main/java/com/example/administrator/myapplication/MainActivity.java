package com.example.administrator.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.xdad.BannerAdView;
import com.xdad.CWAPI;
import com.xdad.RewardVideoLoadListener;
import com.xdad.RewardVideoPlayListener;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout container = new FrameLayout(this);
        Button tv = new Button(this);
        tv.setText("hello");
        setContentView(container);
        container.addView(tv, -1, 100);



        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CWAPI.loadRewardVideo("x", "金币", 100, new RewardVideoLoadListener() {
                    @Override
                    public void onReady() {
                        System.out.println("激励视频加载成功");
                        CWAPI.showRewardVideo(MainActivity.this, new RewardVideoPlayListener() {
                            @Override
                            public void onVideoComplete() {
                                System.out.println("激励视频播放完成");
                            }

                            @Override
                            public void onVideoClosed() {
                                System.out.println("激励视频关闭");
                            }

                            @Override
                            public void onVideoShow() {
                                System.out.println("激励视频开始播放");
                            }
                        });
                    }


                    @Override
                    public void onError(String msg) {
                        System.out.println("激励视频加载失败>>" + msg);
                    }
                });
            }
        });
/*

        System.out.println("goname>>" + name);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        int result = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result== PackageManager.PERMISSION_GRANTED)
        {
            System.out.println("有写权限");
        }
        else {
            System.out.println("没有写权限");
        }*/

//        Util.addcp(this, CsjConstant.appId, CsjConstant.codeIdCp);

        CWAPI.display(false);
//        CWAPI.banner();

        BannerAdView ad = new BannerAdView(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-2, -2);
        lp.gravity= Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        lp.bottomMargin = 100;
        container.addView(ad, lp);



//        FrameLayout banner = new FrameLayout(this);
//        container.addView(banner, -1, 150);
//        CsjBanner.showbanner(this, banner, CsjConstant.appId, CsjConstant.codeIdBanner);
    }
}
