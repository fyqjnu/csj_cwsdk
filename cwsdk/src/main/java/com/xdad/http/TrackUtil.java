package com.xdad.http;

import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

import com.xdad.entity.AdReportTracker;
import com.xdad.entity.DeviceProperty;
import com.xdad.util.Lg;

import java.util.List;
import java.util.Random;

public class TrackUtil {
    
	
	 public static void track(List<AdReportTracker> list)
	 {
		 if(list==null)return;
		 
		 for(AdReportTracker item:list)
		 {
			 track(item.getUrl(), item.getContent());
		 }
	 }
	 
	 public static void track(String urls){
		 track(urls, null);
	 }
    
    public static void track(String urls, String content)
    {
    	
        if(TextUtils.isEmpty(urls))
        {
            return ;
        }
        String[] split = urls.split(";");
        for(String url:split)
        {
        	
         	//玩咖
        	url = url.replace("${down_x}", "" + cl.downx);
        	url = url.replace("${down_y}", "" + cl.downy);
        	url = url.replace("${up_x}", "" + cl.upx);
        	url = url.replace("${up_y}", "" + cl.upy);
        	 
        	url = url.replace("${relative_down_x}", "" + cl.reldownx);
        	url = url.replace("${relative_down_y}", "" + cl.reldowny);
        	url = url.replace("${relative_up_x}", "" + cl.relupx);
        	url = url.replace("${relative_up_y}", "" + cl.relupy);
        	
        	url = url.replace("relative_down_x", "" + cl.reldownx);
        	url = url.replace("relative_down_y", "" + cl.reldowny);
        	url = url.replace("relative_up_x", "" + cl.relupx);
        	url = url.replace("relative_up_y", "" + cl.relupy);
        	
        	//替换宏
        	url = url.replace("down_x", "" + cl.downx);
        	url = url.replace("down_y", "" + cl.downy);
        	url = url.replace("up_x", "" + cl.upx);
        	url = url.replace("up_y", "" + cl.upy);
        	
        	//替换x=-999&y=-999&start=-999&end=-999
        	url = url.replace("x=-999", "x=" + cl.downx);
        	url = url.replace("y=-999", "y=" + cl.downy);
        	url = url.replace("start=-999", "start=" + System.currentTimeMillis());
        	Random r = new Random();
        	long end = System.currentTimeMillis() + 50 + r.nextInt(50);
        	url = url.replace("end=-999", "end=" + end);
        	
        	
        	
        	System.out.println("trackurl>>" + url);
            GetStringHttp hp = new GetStringHttp(url);
            if(DeviceProperty.sUa!=null)
            {
                hp.ua= DeviceProperty.sUa;
            }
            
            
            hp.execute();
        }
        
    }
    
  
    
    public static class ClickLocation {
    	public int downx;
    	public int downy;
    	public int upx;
    	public int upy;
    	
    	public int reldownx;
    	public int reldowny;
    	public int relupx;
    	public int relupy;
		@Override
		public String toString() {
			return "ClickLocation [downx=" + downx + ", downy=" + downy
					+ ", upx=" + upx + ", upy=" + upy + ", reldownx="
					+ reldownx + ", reldowny=" + reldowny + ", relupx="
					+ relupx + ", relupy=" + relupy + "]";
		}
    	
    	
    }
    
    static ClickLocation cl = new ClickLocation();
    
    public static ClickLocation getclicklocation(View v, MotionEvent me)
    {
    	switch (me.getAction()) {
		case MotionEvent.ACTION_DOWN:
			cl.downx = (int) me.getX();
			cl.downy = (int) me.getY();
			
			cl.reldownx = (int) (cl.downx * 1000f / v.getWidth());
			cl.reldowny = (int) (cl.downy * 1000f / v.getHeight());
			
			break;
			
		case MotionEvent.ACTION_UP:
			cl.upx = (int) me.getX();
			cl.upy = (int) me.getY();
			
			cl.relupx = (int) (cl.upx * 1000f / v.getWidth());
			cl.relupy = (int) (cl.upy * 1000f / v.getHeight());
			break;
		default:
			break;
		}
    	
    	if(Lg.d) Lg.d("clicklocation>>" + cl);
    	
    	return cl;
    }

}
