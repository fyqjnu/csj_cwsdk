package com.example.administrator.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.xdad.XDAPI;



public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout container = new FrameLayout(this);
        Button tv = new Button(this);
        tv.setText("hello");
        setContentView(container);
        container.addView(tv, -1, 100);



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

        XDAPI.display(false);
//        XDAPI.banner();





//        FrameLayout banner = new FrameLayout(this);
//        container.addView(banner, -1, 150);
//        CsjBanner.showbanner(this, banner, CsjConstant.appId, CsjConstant.codeIdBanner);
    }
}
