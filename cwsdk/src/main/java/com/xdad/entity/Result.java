package com.xdad.entity;

import org.json.JSONObject;

public class Result implements JsonInterface {


	/**
	 * 1.0.0 a:结果返回码
	 */
	public int resultCode = -1;
	/**
	 * 1.0.0 b:返回结果描述
	 */
	public String description;


	public void parseJson(JSONObject json) {
		try {
			resultCode = json.isNull("a") ? -1 : json.getInt("a");
			description = json.isNull("b") ? null : json.getString("b");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = new JSONObject();
			json.put("a", resultCode);
			json.put("b", description);
			return json;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String getShortName() {
		return "c";
	}


}
