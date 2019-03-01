package com.cw.download;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.cw.entity.AdBody;
import com.cw.entity.AdReportTracker;
import com.cw.http.HttpManager;
import com.cw.http.TrackUtil;
import com.cw.util.Constants;
import com.cw.util.CpUtils;
import com.cw.util.Lg;

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
