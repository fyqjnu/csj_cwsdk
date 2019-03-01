package com.cw.entity;

import java.io.Serializable;

import org.json.JSONObject;

public class AdBody implements JsonInterface,Serializable{
	


	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	/**
	 * 1.0.0 a:广告内容
	 */
	public String body;
	/**
	 * 1.0.0 b:网址
	 */
	public String url;
	/**
	 * 1.0.0 c:广告类型
	 * 1:app
	 * 2:wap
	 * 3:聚告广告
	 * 4:旺脉apk
	 * 5：旺脉wap
	 */
	public int type;//c
	/**
	 * 1.0.0 d:下载程序类中，程序的名称
	 */
	public String apkName;
	/**
	 * 1.0.0 e:广告id
	 */
	public int advertId;
	/**
	 * 1.0.0 f:下载程序类中，程序的包名
	 */
	public String apkPackage;
	/**
	 * 1.0.0 g:广告标题
	 */
	public String title;
	/**
	 * 1.0.0 h:软件类型（如：应用软件、休闲游戏）
	 */
	public String appType;
	/**
	 * 1.0.0 i:软件大小（单位：byte）
	 */
	public int appSize;
	/**
	 * 1.0.0 j:软件版本名称
	 */
	public String appVersion;
	/**
	 * 1.0.0 k:图标的下载url
	 */
	public String iconUrl;
	/**
	 * 1.0.0 l:截图的下载url,每个url用;分开
	 */
	public String captureUrls;
	/**
	 * 1.0.0 r:提供商
	 */
	public String provider;

	public String uploadTime;

	public String language;

	public int adMode;

	public int leastShowTime;

	//每个url用;分开
	public String screenUrls;

	
	//用于旺脉的状态反馈概率控制
	public int clickDownload = 0;

	public boolean hasInstalled = false;

	public boolean downloaded = false;

	public boolean downloading = false;

	public int appState = -1;

	public int isCpAd = 0; // 1 表示插屏广告， 0表示推荐位广告, 2 表示列表

	public int isCrazy = 0; // 1表示疯狂模式，0则不是
	
	public int isOpenA = 1; // 1是打开， 0 则不打开\
	
	
	//第三方 聚告 旺脉
	public String showTrackingUrl;//展示监控地址，多个用逗号分开
	public String clickTrackingUrl;//点击监控地址，多个用逗号分开
	
	
	public String downloadStartTrackUrl;
	public String downloadEndTrackUrl;
	public String installStartTrackUrl;
	public String installEndTrackUrl;
	public String activeTrackUrl;
	
	//SSP广告对应的广告位ID
	public String sspSlotId;
	
	//SSP广告类型， 6-DAP-玩咖, 7-掌上乐游(API)
	public int sspType;
	
	//玩咖广告监控实体
	public AdReport reportVO;
	
	public long remainTimeOnWeb;
	
	
	public int closeRate1 = 100;
	public int closeRate2 = 100;

	@Override
	public void parseJson(JSONObject json) {
		// TODO Auto-generated method stub
		if (json == null)
			return;
		try {
			body = json.isNull("a") ? null : json.getString("a");
			//b
			url = json.isNull("b") ? null : json.getString("b");
			type = json.isNull("c") ? -1 : json.getInt("c");
			apkName = json.isNull("d") ? null : json.getString("d");
			advertId = json.isNull("e") ? -1 : json.getInt("e");
			//f
			apkPackage = json.isNull("f") ? null : json.getString("f");
			title = json.isNull("g") ? null : json.getString("g");
			appType = json.isNull("h") ? null : json.getString("h");
			appSize = json.isNull("i") ? -1 : json.getInt("i");
			appVersion = json.isNull("j") ? null : json.getString("j");
			iconUrl = json.isNull("k") ? null : json.getString("k");
			captureUrls = json.isNull("l") ? null : json.getString("l");
			provider = json.isNull("m") ? null : json.getString("m");
			uploadTime = json.isNull("n") ? "" : json.getString("n");
			language = json.isNull("o") ? "" : json.getString("o");
			adMode = json.isNull("p") ? -1 : json.getInt("p");
			leastShowTime = json.isNull("q") ? -1 : json.getInt("q");
			//r
			screenUrls = json.isNull("r") ? "" : json.getString("r");
			clickDownload = json.isNull("s") ? 0 : json.getInt("s");//s
			isCrazy = json.isNull("t") ? 0 : json.getInt("t");
			isOpenA = json.isNull("u") ? 1 : json.getInt("u");
			
			
			showTrackingUrl=json.isNull("x")?showTrackingUrl:json.getString("x");
			clickTrackingUrl=json.isNull("y")?clickTrackingUrl:json.getString("y");
			
			downloadStartTrackUrl = json.optString("z3");
			downloadEndTrackUrl = json.optString("z4");
			installStartTrackUrl = json.optString("z5");
			installEndTrackUrl = json.optString("z6");
			activeTrackUrl = json.optString("z7");
			
			sspSlotId = json.optString("aa");
			sspType = json.optInt("ab");
			
			if(json.has("ac"))
			{
				JSONObject acJSONObject = json.optJSONObject("ac");
				reportVO = new AdReport();
				reportVO.parseJson(acJSONObject);
			}
			
			remainTimeOnWeb = json.optLong("ad");
			
			if(json.has("closeRate1"))
				closeRate1 = json.optInt("closeRate1");
			if(json.has("closeRate2"))
				closeRate2 = json.optInt("closeRate2");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = new JSONObject();
			json.put("a", body);
			json.put("b", url);
			json.put("c", type);
			json.put("d", apkName);
			json.put("e", advertId);
			json.put("f", apkPackage);
			json.put("g", title);
			json.put("h", appType);
			json.put("i", appSize);
			json.put("j", appVersion);
			json.put("k", iconUrl);
			json.put("l", captureUrls);
			json.put("m", provider);
			json.put("n", uploadTime);
			json.put("o", language);
			json.put("p", adMode);
			json.put("q", leastShowTime);
			json.put("r", screenUrls);
			json.put("s", clickDownload);
			json.put("t", isCrazy);
			json.put("u", isOpenA);
			
			json.put("x", showTrackingUrl);
			json.put("y", clickTrackingUrl);
			
			 json.put("z3",downloadStartTrackUrl);
			 json.put("z4",downloadEndTrackUrl);
			 json.put("z5",installStartTrackUrl);
			  json.put("z6",installEndTrackUrl);
			 json.put("z7",activeTrackUrl);			
			
			 //sspSlotId = json.optString("aa");
				//sspType = json.optInt("ab");
			 json.put("aa", sspSlotId);
			 json.put("ab", sspType);
			
			 if(reportVO !=null)
			 {
				 json.put(reportVO.getShortName(), reportVO.buildJson());
			 }
			
			 json.put("ad", remainTimeOnWeb);
			 
			 json.put("closeRate1", closeRate1);
			 json.put("closeRate2", closeRate2);
			 
			return json;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}


	@Override
	public String getShortName() {
	    //b
		return "b";
	}


}
