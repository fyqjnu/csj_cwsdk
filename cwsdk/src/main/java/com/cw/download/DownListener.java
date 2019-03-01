package com.cw.download;

public interface DownListener {

	
	void ondownloading(DownloadTask dt, int progress);
	
	
	void onstatechanged(DownloadTask dt,int state);
	
	
}
