package com.xdad.entity;

import java.io.Serializable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class AdReport implements Serializable, JsonInterface {
	private List<AdReportTracker> imptrackers;// 曝光追踪地址，允许有多个追踪地址 
	private List<AdReportTracker> clktrackers;// 点击追踪地址，允许有多个追踪地址 
	private List<AdReportTracker> dwnlsts;// 下载开始监播 
	private List<AdReportTracker> dwnltrackers;// 下载完成监播 
	private List<AdReportTracker> intltrackers;// 安装监播 
	private List<AdReportTracker> actvtrackers;// 激活监播 
	private List<AdReportTracker> dplktrackers;// deeplink 成功唤起监播 
	
	
	public List<AdReportTracker> getImptrackers() {
		return imptrackers;
	}
	public void setImptrackers(List<AdReportTracker> imptrackers) {
		this.imptrackers = imptrackers;
	}
	public List<AdReportTracker> getClktrackers() {
		return clktrackers;
	}
	public void setClktrackers(List<AdReportTracker> clktrackers) {
		this.clktrackers = clktrackers;
	}
	public List<AdReportTracker> getDwnlsts() {
		return dwnlsts;
	}
	public void setDwnlsts(List<AdReportTracker> dwnlsts) {
		this.dwnlsts = dwnlsts;
	}
	public List<AdReportTracker> getDwnltrackers() {
		return dwnltrackers;
	}
	public void setDwnltrackers(List<AdReportTracker> dwnltrackers) {
		this.dwnltrackers = dwnltrackers;
	}
	public List<AdReportTracker> getIntltrackers() {
		return intltrackers;
	}
	public void setIntltrackers(List<AdReportTracker> intltrackers) {
		this.intltrackers = intltrackers;
	}
	public List<AdReportTracker> getActvtrackers() {
		return actvtrackers;
	}
	public void setActvtrackers(List<AdReportTracker> actvtrackers) {
		this.actvtrackers = actvtrackers;
	}
	public List<AdReportTracker> getDplktrackers() {
		return dplktrackers;
	}
	public void setDplktrackers(List<AdReportTracker> dplktrackers) {
		this.dplktrackers = dplktrackers;
	}
	
	
	@Override
	public JSONObject buildJson() {
		try
		{
			JSONObject jo = new JSONObject();
			jo.put("imptrackers", AdReportTracker.buildjsonarray(imptrackers));
			jo.put("clktrackers", AdReportTracker.buildjsonarray(clktrackers));
			jo.put("dwnlsts", AdReportTracker.buildjsonarray(dwnlsts));
			jo.put("dwnltrackers", AdReportTracker.buildjsonarray(dwnltrackers));
			jo.put("intltrackers", AdReportTracker.buildjsonarray(intltrackers));
			jo.put("actvtrackers", AdReportTracker.buildjsonarray(actvtrackers));
			jo.put("dplktrackers", AdReportTracker.buildjsonarray(dplktrackers));
			return jo;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public void parseJson(JSONObject json) {
		try {
			//private List<AdReportTracker> imptrackers;// 曝光追踪地址，允许有多个追踪地址 
			//private List<AdReportTracker> clktrackers;// 点击追踪地址，允许有多个追踪地址 
			//private List<AdReportTracker> dwnlsts;// 下载开始监播 
			//private List<AdReportTracker> dwnltrackers;// 下载完成监播 
			//private List<AdReportTracker> intltrackers;// 安装监播 
			//private List<AdReportTracker> actvtrackers;// 激活监播 
			//private List<AdReportTracker> dplktrackers;// deeplink 成功唤起监播 
			imptrackers = AdReportTracker.parsejsonarray(json.getJSONArray("imptrackers"));
			clktrackers = AdReportTracker.parsejsonarray(json.getJSONArray("clktrackers"));
			dwnlsts = AdReportTracker.parsejsonarray(json.getJSONArray("dwnlsts"));
			dwnltrackers = AdReportTracker.parsejsonarray(json.getJSONArray("dwnltrackers"));
			intltrackers = AdReportTracker.parsejsonarray(json.getJSONArray("intltrackers"));
			actvtrackers = AdReportTracker.parsejsonarray(json.getJSONArray("actvtrackers"));
			dplktrackers = AdReportTracker.parsejsonarray(json.getJSONArray("dplktrackers"));
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	@Override
	public String getShortName() {
		return "ac";
	}
}
