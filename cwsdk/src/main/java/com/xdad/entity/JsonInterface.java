package com.xdad.entity;

import org.json.JSONObject;

interface JsonInterface {
	JSONObject buildJson();

	void parseJson(JSONObject json);

	String getShortName();
}