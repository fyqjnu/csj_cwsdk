package com.qq.e.cm.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdReportTracker implements Serializable{
	private int template_type;//  int  0  required  监播处理模板类型 0：不处理监播响应信息，1：广点通监播模板 
	private int http_method;//  int  0  required  监播上报方式：0：get；1：post 
	private String url;//  string    required  监播上报的url 
	private String content;//  string    optional  若监播上报方式为get，此项为空，若上报方式为post，此为上报内容 
	
	
	public static List<AdReportTracker> parsejsonarray(JSONArray ary) throws JSONException
	{
		ArrayList<AdReportTracker> list = new ArrayList<AdReportTracker>();
		int len = ary.length();
		for(int i=0;i<len;i++)
		{
			JSONObject jo = ary.getJSONObject(i);
			AdReportTracker item = new AdReportTracker();
			item.template_type = jo.optInt("template_type");
			item.http_method = jo.optInt("http_method");
			item.url = jo.optString("url");
			item.content = jo.optString("content");
			list.add(item);
		}
		return list;
	}
	
	public static JSONArray buildjsonarray(List<AdReportTracker> list)
	{
		JSONArray ary = new JSONArray();
		int size=list.size();
		for(int i=0;i<size;i++)
		{
			try {
				AdReportTracker item = list.get(i);
				JSONObject jo = new JSONObject();
				jo.put("template_type", item.template_type);
				jo.put("http_method", item.http_method);
				jo.put("url", item.url);
				jo.put("content", item.content);
				ary.put(i, item);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		return ary;
	}
	
	public int getTemplate_type() {
		return template_type;
	}
	public void setTemplate_type(int template_type) {
		this.template_type = template_type;
	}
	public int getHttp_method() {
		return http_method;
	}
	public void setHttp_method(int http_method) {
		this.http_method = http_method;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
