package com.huneng.chattool.net;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.os.StrictMode;

import com.huneng.chattool.app.FileHelper;
import com.huneng.chattool.data.ChatMessage;
import com.huneng.chattool.data.News;
import com.huneng.chattool.data.User;

public class NetWork {
	private static final String httpUrl = "http://" + NetWork.IP + ":3000";
	private static final String httpUrl2 = "http://" + NetWork.IP + ":8000";
	// public static final String IP = "192.168.0.101";
	public static final String IP = "10.21.241.214";

	/**
	 * 登陆，返回值true/false
	 */
	public static String submit(String user, String password) {
		String result;
		if (user == "" || password == "") {
			return "false";
		}

		try {
			result = connectServer(httpUrl + "/login?Username=" + user
					+ "&Password=" + password);
		} catch (IOException e) {
			return "false";
		}

		if (result.startsWith("true"))
			return "true";

		try {
			result = connectServer(httpUrl + "/login?id=" + user + "&Password="
					+ password);
		} catch (IOException e) {
			result = "false";
		}
		if (result.startsWith("true"))
			return "true";
		return "false";
	}

	/**
	 * 注册，返回值true/false
	 */
	public static String register(String name, String password) {
		String result;
		try {
			result = connectServer(httpUrl + "/adduser?Username=" + name
					+ "&Password=" + password);
		} catch (IOException e) {
			result = "false";
		}
		if (result.startsWith("true"))
			return "true";
		return "false";
	}

	/**
	 * 发布公告，返回值true/false
	 */
	public static String publishNews(News news) {
		String result;
		String suffix = "/addnews?";
		suffix += "Content=" + news.message;
		suffix += "&Creat_id=" + news.userId;
		suffix += "&Time=" + news.time;
		suffix += "&Creat_name=" + news.creat_name;
		suffix += "&Creat_img=" + news.creat_img;
		try {
			result = connectServer(httpUrl + suffix);
		} catch (IOException e) {
			result = "false";
		}
		if (result.startsWith("true"))
			return "true";
		return "false";
	}

	/**
	 * 参与，返回值true/false
	 */
	public static String followNews(String userId, String newsId) {
		String result = "false";
		try {
			result = connectServer(httpUrl + "/follow?id=" + newsId
					+ "&follow=" + userId);
		} catch (IOException e) {
			result = "false";
		}
		if (result.startsWith("true"))
			return "true";
		return "false";
	}

	/**
	 * 获取所有公告，返回公告列表
	 */
	public static List<News> getNews() {
		String result;
		try {
			result = connectServer(httpUrl + "/news");
		} catch (IOException e) {
			result = "";
		}
		if (result == "")
			return null;
		List<News> news = new LinkedList<News>();
		JSONArray array;
		try {
			array = new JSONArray(result);
			for (int i = 0; i < array.length(); i++) {
				News n = new News(array.getJSONObject(i).toString());
				news.add(n);
			}
		} catch (JSONException e) {
		}
		return news;
	}

	/**
	 * 获取单个公告，返回公告
	 */
	public static News getNew(String id) {
		String result;
		try {
			result = connectServer(httpUrl + "/getnews?id=" + id);
		} catch (IOException e) {
			result = "";
		}
		if (result == "")
			return null;
		News n;
		JSONArray array;
		try {
			array = new JSONArray(result);
			n = new News(array.get(0).toString());
		} catch (JSONException e) {
			n = null;
		}
		return n;
	}

	public static String addFriends(User user, String userId) {
		String str;
		try {
			String suffix = "User_id=" + userId;
			suffix += "&id=" + user.id;
			suffix += "&Username=" + user.name;
			suffix += "&Photo=" + user.photo;
			suffix += "&tag=" + user.lable;
			str = connectServer(httpUrl + "/addfriend?" + suffix);
		} catch (IOException e) {
			str = "";
		}
		if (str.startsWith("true"))
			return "true";
		return "false";
	}

	/**
	 * 获取好友列表
	 * */
	public static List<User> getFriends(String id) {
		String str;
		try {
			str = connectServer(httpUrl + "/getfriend?User_id=" + id);
		} catch (IOException e) {
			str = "";
		}
		if (str == "")
			return null;
		List<User> friends = new LinkedList<User>();

		try {
			JSONArray array = new JSONArray(str);
			for (int i = 0; i < array.length(); i++) {
				User user = new User(array.getJSONObject(i).toString());
				friends.add(user);
			}
		} catch (JSONException e) {
		}
		return friends;
	}

	/**
	 * 获取单个用户
	 */
	public static User getUser(String id) {
		String str;
		try {
			str = connectServer(httpUrl + "/getuser?id=" + id);
		} catch (IOException e) {
			return null;
		}
		User user = null;
		try {
			user = new User(str.substring(1, str.length() - 1));
		} catch (JSONException e) {
		}
		return user;
	}

	/**
	 * 获取离线信息
	 */
	public static List<ChatMessage> getOfflineMsg(String id) {
		String result;
		try {
			result = connectServer(httpUrl + "/getcomment?To_id=" + id);
		} catch (IOException e) {
			result = "";
		}
		if (result == "")
			return null;
		List<ChatMessage> msgs = new LinkedList<ChatMessage>();
		try {
			JSONArray array = new JSONArray(result);
			for (int i = 0; i < array.length(); i++) {
				ChatMessage msg = new ChatMessage(array.getJSONObject(i)
						.toString());
				msgs.add(msg);
			}

		} catch (JSONException e) {
		}

		return msgs;
	}

	public static boolean downloadAudio(String fileName) {
		String myUrl = httpUrl2 + "/audio/" + fileName;
		String filePath = FileHelper.rootPath + "/audio/" + fileName;
		return downloadFile(myUrl, filePath);
	}

	/**
	 * 下载图片
	 * */
	public static boolean downloadImg(String fileName) {
		String myUrl = httpUrl2 + "/img/" + fileName;
		String filePath = FileHelper.rootPath + "/img/" + fileName;
		return downloadFile(myUrl, filePath);
	}

	/**
	 * 下载图片
	 * */
	public static boolean downloadFile(String myUrl, String fileName) {
		try {
			URL url = new URL(myUrl);
			URLConnection con = url.openConnection();
			con.setConnectTimeout(4000);
			InputStream is = con.getInputStream();
			byte[] bs = new byte[1000];
			int len;
			FileOutputStream os = new FileOutputStream(fileName);

			while ((len = is.read(bs)) != -1) {
				os.write(bs, 0, len);
			}
		} catch (MalformedURLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return false;
	}

	/**
	 * 上传文件
	 * */
	public static void uploadFile(String fileName, byte[] data) {

	}

	@SuppressLint("NewApi")
	public static String connectServer(String myUrl) throws IOException {
		// StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		// .detectDiskReads().detectDiskWrites().detectNetwork()
		// .penaltyLog().build());
		//
		// StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		// .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
		// .build());

		URL url = new URL(myUrl);

		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setConnectTimeout(4000);
		InputStream in = con.getInputStream();
		if (in == null)
			return "";
		int t = 0;
		StringBuffer buffer = new StringBuffer();
		while (t != -1) {
			t = in.read();
			buffer.append((char) t);
		}

		String result = new String(buffer);
		result = result.substring(0, result.length() - 1);
		in.close();
		con.disconnect();
		return result;
	}

}
