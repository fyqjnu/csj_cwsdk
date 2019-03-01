package com.cw;

import java.util.List;

import com.cw.entity.AdBody;
import com.cw.entity.AdReportTracker;
import com.cw.http.HttpManager;
import com.cw.http.TrackUtil;
import com.cw.util.Constants;
import com.cw.util.CpUtils.OnWebDismissListener;
import com.cw.util.Lg;

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
