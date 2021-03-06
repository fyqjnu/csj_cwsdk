package com.xdad.download;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.xdad.entity.AdBody;
import com.xdad.entity.AdReportTracker;
import com.xdad.http.HttpManager;
import com.xdad.http.TrackUtil;
import com.xdad.util.Constants;
import com.xdad.util.CpUtils;
import com.xdad.util.Lg;

public class SimpleDownApkListenerImpl implements DownListener {
	
	private AdBody info;
	private Context ctx;
	
	private int type;

	public SimpleDownApkListenerImpl(Context ctx, AdBody info, int type){
		this.ctx = ctx;
		this.info = info;
		this.type = type;
	}

	@Override
	public void onstatechanged(final DownloadTask dt, int state) {
		Lg.d("state>" + state);
		if(state==DownloadTask.state_complete)
		{
			//下载完成
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				
				@Override
				public void run() {
					CpUtils.installapk(ctx, dt.getfile());
				}
			});
			
			//状态
			feedbackstate(info.advertId, Constants.CP_STATE_DOWNLOAD_FINISH);
			
			//玩咖
			if(info.reportVO!=null)
			{
				List<AdReportTracker> trackers = info.reportVO.getDwnltrackers();
				TrackUtil.track(trackers);
			}
			else
			{
				TrackUtil.track(info.downloadEndTrackUrl);
				TrackUtil.track(info.installStartTrackUrl);
			}
			
		}
		else if(state==DownloadTask.state_start)
		{
			//状态:开始下载
			feedbackstate(info.advertId, Constants.CP_STATE_DOWNLOAD);
			//玩咖
			if(info.reportVO!=null)
			{
				List<AdReportTracker> trackers = info.reportVO.getDwnlsts();
				TrackUtil.track(trackers);
			}
			else
			{
				TrackUtil.track(info.downloadStartTrackUrl);
			}
		}
	}
	
	@Override
	public void ondownloading(DownloadTask dt, int progress) {
//		Lg.d("progress>" + progress);
	}

	
	private void feedbackstate(final int id, final int state)
	{
		new Thread() {
			
			@Override
			public void run() {
				//banner 类型为1 
				HttpManager.feedbackstate(id, state, type);
			}
		}.start();
	}
	
}
