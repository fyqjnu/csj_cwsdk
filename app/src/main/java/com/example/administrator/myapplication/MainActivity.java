package com.example.administrator.myapplication;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdad.XDAPI;


public class MainActivity extends Activity {

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout container = new FrameLayout(this);
        setContentView(container);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(1);
        ll.setBackgroundColor(Color.GRAY);

        TextView ver = new TextView(this);
        ver.setText("版本："+"6.5.6");
        ver.setTextSize(20);
        ll.addView(ver);

        container.addView(ll, -1, -1);
        Button tv = new Button(this);
        tv.setText("点击展示插屏");
        ll.addView(tv, -1, 100);

        final EditText et = new EditText(this);
        et.setHint("输入广告id");
        et.setHintTextColor(Color.GRAY);
        et.setBackgroundColor(Color.WHITE);
        et.setTextColor(Color.BLACK);
        et.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        ll.addView(et, -1, -2);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et.getText()!=null)
                {
                    try {
                        Integer val = Integer.valueOf(et.getText().toString());
                        if(val>0)
                        {
                            getSharedPreferences("testid", 0).edit().putInt("advertId", val).commit();
                        }
                    }catch ( Exception e){}
                }
                XDAPI.display(false);
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


//        XDAPI.banner();





//        FrameLayout banner = new FrameLayout(this);
//        container.addView(banner, -1, 150);
//        CsjBanner.showbanner(this, banner, CsjConstant.appId, CsjConstant.codeIdBanner);
    }
}
