package com.xdad;

import java.io.File;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xdad.download.FileUtil;
import com.xdad.entity.AdBody;
import com.xdad.entity.AdReportTracker;
import com.xdad.http.HttpManager;
import com.xdad.http.TrackUtil;
import com.xdad.util.Constants;
import com.xdad.util.CpUtils;
import com.xdad.util.Lg;
import com.xdad.util.SpUtil;

public class MyReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(final Context context, Intent intent) {
		
		
		String a = intent.getAction();
		
		
		if(Intent.ACTION_PACKAGE_ADDED.equals(a))
		{
			final String d = intent.getData().toString();
			final String pkg = d.substring(d.indexOf(":")+1);
			//package:com.wdsj.dhscw
			Lg.d(d);
			final AdBody info = SpUtil.getadinfobypkg(context, pkg);
			
			Lg.d(info);
			
			if(info!=null)
			{
				//判断是否下载完成
//				if(!SpUtil.isstateexist(context, info.advertId, Constants.CP_STATE_DOWNLOAD_FINISH))
//					return ;
				
				new Thread(){
					public void run() {
						int state = Constants.CP_STATE_INSTALLED;
						HttpManager.init(context);
						HttpManager.feedbackstate(info.advertId, state, 4);
						File f = FileUtil.getapkfile(info.url);
						if(f.exists())f.delete();
						
						//打开app
						CpUtils.openapp(context, pkg);
					};
				}.start();
				
				
				//玩咖
				if(info.reportVO!=null)
				{
					List<AdReportTracker> trackers = info.reportVO.getIntltrackers();
					TrackUtil.track(trackers);
					
					List<AdReportTracker> trackers2 = info.reportVO.getActvtrackers();
					TrackUtil.track(trackers2);
				}
				else
				{
					TrackUtil.track(info.installEndTrackUrl);
					TrackUtil.track(info.activeTrackUrl);
				}
				
			}
		}
		
	}

}
