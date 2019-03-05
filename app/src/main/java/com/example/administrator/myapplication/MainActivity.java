package com.example.administrator.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.qq.e.cm.CWAPI;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout container = new FrameLayout(this);
        Button tv = new Button(this);
        tv.setText("hello");
        setContentView(container);
        container.addView(tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("xxxxxxxxxx");
            }
        });
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
