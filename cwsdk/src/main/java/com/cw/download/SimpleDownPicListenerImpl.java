package com.cw.download;

import java.io.File;

import android.os.Handler;
import android.os.Looper;

public abstract class SimpleDownPicListenerImpl implements DownListener{

	
	static Handler handler =new Handler(Looper.getMainLooper());
	
	
	public abstract void onfinish(DownloadTask task, File file);


	@Override
	public void ondownloading(DownloadTask dt, int progress) {
		
	}


	@Override
	public void onstatechanged(final DownloadTask dt, int state) {
		if(state==DownloadTask.state_complete)
		{
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					onfinish(dt, dt.getfile());
				}
			});
		}
	}

	
	
}
