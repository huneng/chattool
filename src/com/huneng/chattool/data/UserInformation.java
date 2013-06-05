package com.huneng.chattool.data;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class UserInformation {
	public String id;
	public String name;
	public int age;
	public String addr;
	public String photo;
	public String lable;

	public UserInformation() {
		id = name = addr = photo = lable = "";
		age = 0;
	}

	public UserInformation(String str) throws JSONException {
		JSONObject object = new JSONObject(str);
		id = object.getString("userId");
		name = object.getString("username");
		age = object.getInt("age");
		lable = object.getString("lable");
	}

	public String toString() {
		JSONObject object = new JSONObject();
		try {
			object.put("userId", id);
			object.put("username", name);
			object.put("age", age);
			object.put("photo", photo);
			object.put("lable", lable);
		} catch (JSONException e) {
			return "";
		}
		return object.toString();
	}
	public Map<String, String> toMap(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("userId", id);
		map.put("username", name);
		map.put("img", photo);
		map.put("lable", lable);
		return map;
	}
}
