package com.huneng.chattool.data;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
	public String id;
	public String name;
	public String photo;
	public String lable;

	public User() {
		id = name = photo = lable = "";
	}

	public User(String str) throws JSONException {
		JSONObject object = new JSONObject(str);
		id = object.getString("id");
		name = object.getString("Username");
		lable = object.getString("tag");
		photo = object.getString("Photo");
	}

	public String toString() {
		JSONObject object = new JSONObject();
		try {
			object.put("id", id);
			object.put("Username", name);

			object.put("Photo", photo);
			object.put("tag", lable);
		} catch (JSONException e) {
			return "";
		}
		return object.toString();
	}

	public Map<String, String> toMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("userId", id);
		map.put("username", name);
		map.put("img", photo);
		map.put("lable", lable);
		return map;
	}
}
