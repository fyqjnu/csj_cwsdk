package com.qq.e.cm.download;

public interface DownListener {

	
	void ondownloading(DownloadTask dt, int progress);
	
	
	void onstatechanged(DownloadTask dt,int state);
	
	
}
