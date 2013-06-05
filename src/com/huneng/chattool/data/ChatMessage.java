package com.huneng.chattool.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatMessage {
	public String id;
	public String message;
	public String state;

	public ChatMessage(String data) {
		try {
			JSONObject object = new JSONObject(data);
			id = object.getString("userId");
			message = object.getString("message");
			state = object.getString("state");
		} catch (JSONException e) {
		}

	}

	public String toString() {
		JSONObject object = new JSONObject();
		try {
			object.put("userId", id);
			object.put("message", message);
			object.put("state", state);
		} catch (JSONException e) {
		}
		return object.toString();
	}
}
