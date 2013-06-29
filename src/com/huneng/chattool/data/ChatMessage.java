package com.huneng.chattool.data;

import org.json.JSONException;
import org.json.JSONObject;

import com.huneng.chattool.app.ChatActivity;
import com.huneng.chattool.net.NetWork;

public class ChatMessage {
	public String from_id;
	public String to_id;
	public String message;

	public ChatMessage() {
		from_id = to_id = message = "";
	}

	public ChatMessage(String data) {
		try {
			JSONObject object = new JSONObject(data);
			from_id = object.getString("From_id");
			message = object.getString("Content");
			to_id = object.getString("To_id");
		} catch (JSONException e) {
		}
	}

	public String toString() {
		JSONObject object = new JSONObject();
		try {
			object.put("From_id", from_id);
			object.put("Content", message);
			object.put("To_id", to_id);
		} catch (JSONException e) {
		}
		return object.toString();
	}
}
