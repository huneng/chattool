package com.huneng.chattool.data;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class News {
	public String message;
	public UserInformation user;
	public String[] follows;

	public News() {
		message = "";
		follows = null;
		user = null;
	}

	public News(String data) {
		try {
			JSONObject object = new JSONObject(data);
			message = object.getString("message");
			user = new UserInformation(object.getString("user"));
			JSONArray array = object.getJSONArray("followId");
			int length = array.length();
			follows = new String[length];
			for (int i = 0; i < length; i++) {
				follows[i] = (String) array.get(i);
			}
		} catch (JSONException e) {
		}

	}

	public String toString() {
		
		JSONObject object = new JSONObject();
		try {
			object.put("message", message);
			object.put("user", user.toString());
			JSONArray array = new JSONArray();
			for (int i = 0; i < follows.length; i++) {
				array.put(follows[i]);
			}
			object.put("followId", array);
		} catch (JSONException e) {
		}
		return object.toString();
	}
	public Map<String, Object> toMap(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", user.name);
		map.put("img", user.photo);
		map.put("message", message);
		map.put("followId", follows);
		return map;
	} 
}
