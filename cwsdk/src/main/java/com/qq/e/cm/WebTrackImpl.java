package com.qq.e.cm;

import java.util.List;

import com.qq.e.cm.entity.AdBody;
import com.qq.e.cm.entity.AdReportTracker;
import com.qq.e.cm.http.HttpManager;
import com.qq.e.cm.http.TrackUtil;
import com.qq.e.cm.util.Constants;
import com.qq.e.cm.util.CpUtils.OnWebDismissListener;
import com.qq.e.cm.util.Lg;

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
