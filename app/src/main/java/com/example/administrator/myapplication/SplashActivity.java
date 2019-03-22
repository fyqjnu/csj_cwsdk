package com.example.administrator.myapplication;

import com.xdad.AActivity;

/**
 * Created by Administrator on 2019/3/22 0022.
 */

public class SplashActivity extends AActivity {

    @Override
    public String getMainActivityName() {
        //跳转到主页面
        return "com.example.administrator.myapplication.MainActivity";
    }
}
