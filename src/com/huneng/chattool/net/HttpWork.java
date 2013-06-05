package com.huneng.chattool.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.annotation.SuppressLint;
import android.os.StrictMode;

public class HttpWork {
	private String site;
	private String user;
	private String password;
	private String result;
	public static HttpWork hw = null;
	public HttpWork() {
		new HttpWork("");
	}

	public HttpWork(String site) {
		new HttpWork(site, "", "");
	}
	
	public HttpWork(String site, String user, String password) {
		this.site = site;
		this.user = user;
		this.password = password;
	}

	public int submit() {
		return submit(user, password);
	}

	public int submit(String user, String password) {
		int t = -1;
		String suffix = "/android/submit/";
		String url = site + suffix + user + '/' + password;
		connect(url);
		return t;
	}

	public String getFriendList() {
		String suffix = "/android/friendlist/";
		String url = site + suffix;
		connect(url);
		return result;
	}
	public String getUserInfo(String id){
		String suffix = "/android/user/";
		String url = site + suffix + id;
		connect(url);
		return result;
	}
	public String getOnlineFriendList() {
		String suffix = "/android/onlinefriend/";
		String url = site + suffix;
		connect(url);
		return result;
	}

	public String getOnlineMsg(String id) {
		String suffix = "/android/onlinemessage/";
		String url = site + suffix + id;
		connect(url);
		return result;
	}

	public String getOfflineMsg() {
		String suffix = "/android/offlinemessage/";
		String url = site + suffix;
		connect(url);
		return result;
	}

	public String sendMsg(String data) {
		String suffix = "/android/sendmessage/";

		String url;
		try {
			url = site + suffix + URLEncoder.encode(data, "UTF-8");
			connect(url);
		} catch (UnsupportedEncodingException e) {
			result = "";
		}
		return result;
	}

	public void setResult(String str) {
		result = new String(str);
	}

	public String getResult() {
		return result;
	}
	public String getNews(){
		String suffix = "/android/news/";
		String url = site + suffix;
		connect(url);
		return result;
	}
	private void connect(String url) {
		try {
			connectServer(url);
		} catch (IOException e) {
			result = "";
		}
	}

	@SuppressLint("NewApi")
	public void connectServer(String myUrl) throws IOException {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());

		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
				.build());
		URL url = new URL(myUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		InputStream in = con.getInputStream();
		StringBuffer buffer = new StringBuffer();
		int length = con.getContentLength();
		for (int i = 0; i < length; i++) {
			buffer.append((char) in.read());
		}
		result = new String(buffer);
		in.close();
		con.disconnect();
	}

	public int downloadFile(String fileName) {
		return 0;
	}

}