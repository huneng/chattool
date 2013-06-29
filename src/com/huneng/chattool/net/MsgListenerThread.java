package com.huneng.chattool.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MsgListenerThread extends Thread {
	private Context context;
	private Socket socket;
	private InputStream is;
	private OutputStream os;

	public MsgListenerThread(Context context, Socket s) {
		this.context = context;
		this.socket = s;
		try {
			is = socket.getInputStream();
		} catch (IOException e) {
		}
		try {
			os = socket.getOutputStream();
		} catch (IOException e) {
		}
	}

	@Override
	public void run() {
		while (true) {
			if (context == null || socket == null)
				break;
			try {
				StringBuffer buffer = new StringBuffer();
				byte b[] = new byte[1000];
				int t = is.read(b, 0, 1000);
				for (int i = 0; i < t; i++) {
					buffer.append((char) b[i]);
				}
				String str = buffer.toString();
				Intent intent = new Intent("com.huneng.chattool.msg");
				intent.putExtra("msg", str);
				context.sendBroadcast(intent);

			} catch (IOException e) {
				Log.v("Socket", e.getMessage());
				break;
			}

		}
	}

	public void sendMsg(String msg) {
		try {
			os.write(msg.getBytes());
			os.flush();
			Log.v("Socket", "Send Success!");
		} catch (IOException e) {
			Log.v("Socket", "Socket Send:" + e.getMessage());
		}
	}
}
