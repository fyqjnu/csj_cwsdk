package com.xdad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


public class Run extends Activity {
	
	
	public static String getmcc(Context ctx)
	{
		String mcc = "460";
		try
		{
			TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
			mcc = tm.getNetworkOperator().substring(0,3);
		}
		catch(Exception e)
		{
		}
		return mcc;
	}
	


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
//		DisplayMetrics dm = getResources().getDisplayMetrics();
//		int wp = dm.widthPixels;
//		int hp = dm.heightPixels;
//		System.out.println("wp>>" + wp + ",hp>>" + hp); 
        
        try {
        	getPackageManager().getApplicationInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
        
        
        System.out.println("mcc>" + getmcc(this));
        
        System.out.println("agent>>" + System.getProperty("http.agent"));
        
//		GetStringHttp g = new GetStringHttp("https://www.bturl.net//search/%E6%B5%B7%E7%8E%8B_ctime_2.html");
//		g.execute();
        
        
		LinearLayout layout =new LinearLayout(this);
		layout.setOrientation(1);
		
		tv = new TextView(this);
		
		Button b = new Button(this);
		b.setText("内插");
		//db69044e47fb488ea46203fb
		//10492637eaf149ec802f8155
		CWAPI.init(this, "Z", null);
		
//		CWAPI.showSplash(this);
		CWAPI.display(true);
		CWAPI.banner();
		
		Intent in = getPackageManager().getLaunchIntentForPackage(getPackageName());
		String className = in.getComponent().getClassName();
		System.out.println("入口>>" + className);
		//使用开屏
				String simpleName = getClass().getSimpleName();
				System.out.println("simplename>>" + simpleName);
		
//		Intent in = new Intent(this, AActivity.class);
//		in.putExtra("type", 1);
//		in.putExtra("url", "https://iadapi.mobjz.com/iad/entryfirst?appkey=BF85BEEBB56305DC4212AE9C2979A56C");
//		startActivity(in);
		
		
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				startActivity(new Intent(Run.this, AActivity.class));
//				test();
				
			}
		});
		
//		layout.addView(b);
		
		
		setContentView(layout);
//		banner();
		
          
		tv.setText("hell");
		pw = new PopupWindow(this);
//		pw.dismiss();
		pw.setWidth(200);
		pw.setHeight(200);
		pw.setContentView(tv);
		
		
    }
    
    
    PopupWindow pw;
	private TextView tv;
    
    void test()
    {
    	pw.setWidth((int) (pw.getWidth() * 1.2f));
//    	pw.dismiss();
    	tv.setText("" + counter++);
    	pw.setContentView(tv);
    	pw.setBackgroundDrawable(new ColorDrawable(Color.RED));
    	pw.showAtLocation(findViewById(android.R.id.content), Gravity.TOP, 100, 100);
    	
    }
    
    int counter = 1;
     

}
