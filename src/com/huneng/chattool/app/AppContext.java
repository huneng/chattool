package com.huneng.chattool.app;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.huneng.chattool.data.ChatMessage;
import com.huneng.chattool.data.News;
import com.huneng.chattool.data.User;
import com.huneng.chattool.net.MsgListenerThread;
import com.huneng.chattool.net.NetWork;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class AppContext extends Application{
	/**
	 * 注册消息监听器
	 * 
	 * @param msglistener
	 *            监听器
	 * @param id
	 *            用户id
	 */


	/**
	 * 获取用户信息，查看本地数据存储，或者服务器
	 * 
	 * @param id
	 *            用户id
	 * @param friendPrefer
	 *            保存用户数据的Preference
	 * @return User对象
	 */
	public User getUserInfo(String id, SharedPreferences friendPrefer) {
		String str = friendPrefer.getString(id, "");
		if (str == "") {
			return NetWork.getUser(id);
		}
		User user = null;
		try {
			user = new User(str);
		} catch (JSONException e) {
		}
		return user;
	}

	/**
	 * 获取公告/
	 * 
	 * @param newsList
	 *            保存公告的列表
	 */
	void getNews(List<Map<String, String>> newsList) {
		List<News> news = NetWork.getNews();
		if (news == null)
			return;
		for (int i = 0; i < news.size(); i++) {
			Map<String, String> map = news.get(i).toMap();
			newsList.add(map);
		}
	}

	/**
	 * 获取离线信息/
	 * 
	 * @param id
	 *            用户id
	 */
	public void getOfflineMsg(String id) {
		List<ChatMessage> msgs = NetWork.getOfflineMsg(id);
		for (int i = 0; i < msgs.size(); i++) {
			Intent intent = new Intent("com.huneng.chattool.msg");
			intent.putExtra("msg", msgs.get(i).toString());
			this.sendBroadcast(intent);
		}
	}
	public String getDate() {
		Calendar c = Calendar.getInstance();

		String year = String.valueOf(c.get(Calendar.YEAR));
		String month = String.valueOf(c.get(Calendar.MONTH));
		String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
		String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		String mins = String.valueOf(c.get(Calendar.MINUTE));

		StringBuffer sbBuffer = new StringBuffer();
		sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":"
				+ mins);

		return sbBuffer.toString();
	}
}
