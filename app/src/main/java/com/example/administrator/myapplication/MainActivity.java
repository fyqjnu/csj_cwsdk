package com.example.administrator.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.qq.e.cm.CWAPI;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout container = new FrameLayout(this);
        TextView tv = new TextView(this);
        tv.setText("hello");
        setContentView(container);
        container.addView(tv);
/*

        String name = Go.getname();
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
        CWAPI.init(this, "db69044e47fb488ea46203fb", null);
        CWAPI.display(true);


//        FrameLayout banner = new FrameLayout(this);
//        container.addView(banner, -1, 150);
//        CsjBanner.showbanner(this, banner, CsjConstant.appId, CsjConstant.codeIdBanner);
    }
}
