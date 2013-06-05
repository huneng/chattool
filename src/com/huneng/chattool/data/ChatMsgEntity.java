
package com.huneng.chattool.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatMsgEntity {
    private String name;

    private String date;

    private String text;
    
    private String time;
    
    public String toString(){
    	JSONObject object = new JSONObject();
    	try {
			object.put("name", name);
			object.put("date", date);
			object.put("time", time);
			object.put("text", text);
		} catch (JSONException e) {
			return "";
		}
    	
    	return object.toString();
    }

    public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	private boolean isComMeg = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean getMsgType() {
        return isComMeg;
    }

    public void setMsgType(boolean isComMsg) {
    	isComMeg = isComMsg;
    }

    public ChatMsgEntity() {
    }

    public ChatMsgEntity(String name, String date, String text, boolean isComMsg) {
        super();
        this.name = name;
        this.date = date;
        this.text = text;
        this.isComMeg = isComMsg;
    }

}
