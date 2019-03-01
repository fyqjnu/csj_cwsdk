package com.example.administrator.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cw.Go;
import com.cw.Run;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout container = new FrameLayout(this);
        TextView tv = new TextView(this);
        tv.setText("hello");
        setContentView(container);
        container.addView(tv);


        String name = Go.getname();
        System.out.println("name>>" + name);
        String mcc = Run.getmcc(this);
        System.out.println("mcc>>"+mcc);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        int result = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result== PackageManager.PERMISSION_GRANTED)
        {
            System.out.println("有写权限");
        }
        else {
            System.out.println("没有写权限");
        }


        File f = new File(Environment.getExternalStorageDirectory(), "test25");
        try {
            FileOutputStream fos = new FileOutputStream(f);
            fos.write("helloworld".getBytes());
            fos.flush();
            fos.close();
        }catch (Exception e){
            System.out.println("exception>>" + e);
            e.printStackTrace();
        }


        Util.addcp(this, CsjConstant.appId, CsjConstant.codeIdCp);

        FrameLayout banner = new FrameLayout(this);
        container.addView(banner, -1, 150);
        CsjBanner.showbanner(this, banner, CsjConstant.appId, CsjConstant.codeIdBanner);
    }
}
