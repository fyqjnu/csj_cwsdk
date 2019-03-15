package com.qq.e.cm.util;

public final class Constants {
	private Constants() {
	}


	//工具6.3.1，sdk6.3.4 升级：加了5秒广告返回检查，显示广告后4分钟周期再次请求广告
	//6.4.1 #增加激励视频功能 ; 增加请求是否成功的接口
 	public static String version= "6.4.1";
	
	
	/** imsi保存路径 */
	public static final String IMSI_FILE ="/Android/data/d/sb";

	/**
	 * xml，记录imsi
	 */
	public static final String XML_IMSI = "kb";
    
	public static final String L_Key = "skey";
	public static final String L_Cid = "channel";


	public static final int CP_STATE_NONE = -1;
	public static final int CP_STATE_SHOW = 0;
	public static final int CP_STATE_DETAIL = 1;
	public static final int CP_STATE_DOWNLOAD = 2;
	public static final int CP_STATE_DOWNLOAD_FINISH = 3;
	public static final int CP_STATE_INSTALLED = 4;

	/** the task is added to download list, waiting to connect to network. */
	public final static int STATE_WAITING = 1;

	/** the task is downloading. */
	public final static int STATE_RUNNING = 2;

	/** the task has been suspend. */
	public final static int STATE_SUSPEND = 3;

	/** the task has been downloaded completely. */
	public final static int STATE_COMPLETED = 4;

	/** the task has been aborted by some exception */
	public final static int STATE_ABORT = 5;

	/**
	 * StringCoder
	 */
	public final static String KEY ="www.abmobi.org";//

	

	//CpUtils
	public static final String CU_STRING0="i";//"i"
	public static final String CU_STRING1="%";//"%"
	
	
}
