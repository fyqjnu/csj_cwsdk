package com.xdad;

import java.util.List;

import com.xdad.entity.AdBody;
import com.xdad.entity.AdReportTracker;
import com.xdad.http.HttpManager;
import com.xdad.http.TrackUtil;
import com.xdad.util.Constants;
import com.xdad.util.CpUtils.OnWebDismissListener;
import com.xdad.util.Lg;

public class WebTrackImpl implements OnWebDismissListener {

	
	public long startime;
	private AdBody info;

	private int type;
	public WebTrackImpl(AdBody info, int type)
	{
		this.info = info;
		startime = System.currentTimeMillis();
		this.type = type;
	}
	
	private void feedbackstate(final int id, final int state)
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpManager.feedbackstate(id, state, type);
			}
		}).start();
	}
	
	@Override
	public void onDismiss() {
		long remaintime = System.currentTimeMillis() - startime;
		if(Lg.d) System.out.println("remaintime>>" + remaintime);
		if(remaintime> info.remainTimeOnWeb)
		{
			//玩咖
			if(info.reportVO!=null)
			{
				List<AdReportTracker> trackers = info.reportVO.getClktrackers();
				TrackUtil.track(trackers);
			}
			else
			{
				TrackUtil.track(info.clickTrackingUrl);
			}
			
			int state = Constants.CP_STATE_DETAIL;
			feedbackstate(info.advertId, state);
			
		}
	}

}
