package com.cw.util;

import java.util.regex.Pattern;

public final class Constants {
	private Constants() {
	}

	
	public static String version= "6.2.1";
	
	
	/** imsi保存路径 */
	public static final String IMSI_FILE ="/Android/data/c/si";

	/**
	 * xml，记录imsi
	 */
	public static final String XML_IMSI = "ka";
    
	public static final String L_Key = "kkey";
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
	public final static String KEY ="www.cwmobi.com";//
	public final static Pattern PATTERN = Pattern.compile("%(\\d*)");

	

	//CpUtils
	public static final String CU_STRING0="i";//"i"
	public static final String CU_STRING1="%";//"%"
	
	
}
