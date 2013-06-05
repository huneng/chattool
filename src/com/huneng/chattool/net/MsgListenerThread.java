package com.huneng.chattool.net;

import com.huneng.chattool.data.ChatMessage;

import android.content.Context;
import android.content.Intent;

public class MsgListenerThread extends Thread {
	Context context;
	int sleepTime;
	String id;
	String exit;

	public MsgListenerThread(Context context, String id) {
		this.context = context;
		this.id = id;
		exit = "online";
	}

	@Override
	public void run() {
		while (exit != "offline") {
			String result = HttpWork.hw.getOnlineMsg(id);
			if (result != "") {
				ChatMessage msg = new ChatMessage(result);
				if (msg.id == id) {
					Intent intent = new Intent("chattool.net.thread");
					intent.putExtra("userId", msg.id);
					intent.putExtra("message", msg.message);
					exit = msg.state;
				}
			}
			try {
				sleep(2000);
			} catch (InterruptedException e) {
			}

		}
	}
}
