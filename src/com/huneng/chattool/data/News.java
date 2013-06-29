package com.huneng.chattool.data;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.huneng.chattool.net.NetWork;

public class News {
	public String id;
	public String userId;
	public String followId;

	public String message;
	public String time;
	public String creat_img;
	public String creat_name;
	public News() {

		userId = "";
		followId = null;
		message = "";
		time = "";
	}

	public News(String data) {

		try {
			JSONObject object = new JSONObject(data);
			id = object.getString("id");
			userId = object.getString("Creat_id");
			followId = object.getString("Follow_id");
			message = object.getString("Content");
			time = object.getString("Time");
			
		} catch (JSONException e) {
		}
		creat_img="";
		creat_name="";

	}

	public void setTime(String time) {
		this.time = new String(time);
	}

	public String toString() {
		JSONObject object = new JSONObject();
		try {
			object.put("id", id);
			object.put("Content", message);
			object.put("Creat_id", userId);
			object.put("Follow_id", followId);
			object.put("Time", time);
		} catch (JSONException e) {
		}

		return object.toString();
	}

	public Map<String, String> toMap() {
		Map<String, String> object = new HashMap<String, String>();
		User user = NetWork.getUser(userId);
		if (user == null)
			return null;
		object.put("id", id);
		object.put("Content", message);
		object.put("Creat_id", userId);
		object.put("Follow_id", followId);
		object.put("Time", time);
		object.put("Creat_name", user.name);
		object.put("Creat_photo", user.photo);
		return object;
	}


}
