package com.huneng.chattool.app;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.huneng.chattool.adapter.FriendListAdapter;
import com.huneng.chattool.adapter.NewsAdapter;
import com.huneng.chattool.app.R;
import com.huneng.chattool.data.ChatMessage;
import com.huneng.chattool.data.ChatMsgEntity;
import com.huneng.chattool.data.User;
import com.huneng.chattool.net.MsgListenerThread;
import com.huneng.chattool.net.NetWork;
import com.huneng.chattool.widget.PullToRefreshListView;
import com.huneng.chattool.widget.SimpleInputDialog;
import com.huneng.chattool.widget.PullToRefreshListView.OnRefreshListener;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

/**
 * 主界面：包括三个列表，聊天记录、好友列表、新鲜事列表，一个普通界面，
 * */

public class MainActivity extends Activity {

	public static final String CHAT_RECORD_PREFER_NAME = "chat";
	public static final String FRIEND_PREFER_NAME = "friend";

	private LayoutInflater inflater;
	private ImageView mTab1, mTab2, mTab3, mTab4;
	private LinearLayout mCloseBtn;
	private View layout;

	private boolean menu_display = false;
	private PopupWindow menuWindow;

	private List<Map<String, String>> chatListData;
	private List<Map<String, String>> friendList;
	private List<Map<String, String>> newsList;

	private ArrayList<View> centerContent;
	private SharedPreferences chatPrefer;
	private SharedPreferences friendPrefer;
	private List<String> chatPersons;
	public String userId;
	public User mInfo;
	// NotificationManager notiManager;
	// public static int notiId = 0;
	private AppContext appcontext;

	public static MainActivity instance;
	public static MsgListenerThread msglistener;

	/**
	 * Note: chatPrefer记录聊天记录，包括聊天对象列表chatList friendPrefer保存用户好友资料
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		setContentView(R.layout.main_weixin);

		Intent intent = getIntent();
		// userId = "1";
		userId = intent.getStringExtra("userId");

		mInfo = NetWork.getUser(userId);
		appcontext = (AppContext) getApplication();
		chatPrefer = getSharedPreferences(CHAT_RECORD_PREFER_NAME, MODE_PRIVATE);
		friendPrefer = getSharedPreferences(FRIEND_PREFER_NAME, MODE_PRIVATE);

		chatPersons = new ArrayList<String>();
		newsList = new LinkedList<Map<String, String>>();
		chatListData = new LinkedList<Map<String, String>>();
		friendList = new LinkedList<Map<String, String>>();

		initFriendInfo(userId);
		initChatList();

		// notiManager = (NotificationManager)
		// getSystemService(NOTIFICATION_SERVICE);

		// 注册广播接收机
		IntentFilter filter = new IntentFilter("com.huneng.chattool.msg");
		MyBroadCastReciver receiver = new MyBroadCastReciver();
		registerReceiver(receiver, filter);

		initTabs();
		initCenterContent();// 初始化中部界面

		registerMsgListener();
		appcontext.getOfflineMsg(userId);
		instance = this;
	}

	public void registerMsgListener() {
		Socket socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(NetWork.IP, 6000), 2000);
			msglistener = new MsgListenerThread(this, socket);

			String str = "{\"User_id\":" + '"' + mInfo.id + '"' + "}";
			msglistener.sendMsg(str);

		} catch (IOException e) {
			Toast.makeText(this, "Can't connect to net", Toast.LENGTH_LONG)
					.show();
			Log.v("Socket", e.getMessage());
			return;
		}
		msglistener.start();
	}

	void initChatList() {
		// 聊天用户列表
		String str = chatPrefer.getString("chatList", "");
		if (str != "") {
			String temp[] = str.split(";");
			for (int i = 0; i < temp.length; i++) {
				User user = appcontext.getUserInfo(temp[i], friendPrefer);
				if (user == null)
					continue;
				chatPersons.add(temp[i]);
				chatListData.add(user.toMap());
			}
		}
	}

	protected void onStop() {
		StringBuffer str = new StringBuffer();
		for (int i = 0; i < chatPersons.size(); i++) {
			String temp = chatPersons.get(i);
			str.append(temp);
			chatPrefer.edit().putString("chatList", temp);
		}
		super.onStop();
	};

	private final static int REMOVE_ALL_CHAT_RECORDS = -1;
	private final static int REMOVE_ONE_CHAT_RECORDS = 1;

	void updatePrefer(int flag, String key) {
		switch (flag) {
		case REMOVE_ALL_CHAT_RECORDS:
			chatPrefer.edit().clear().commit();
			break;
		case REMOVE_ONE_CHAT_RECORDS:
			int i;
			String str = "";
			for (i = 0; i < chatPersons.size() - 1; i++) {
				str = chatPersons.get(i) + ";";
			}
			str += chatPersons.get(i);
			chatPrefer.edit().putString("chatList", str).commit();
			chatPrefer.edit().remove(key).commit();
			break;
		}
	}

	void initTabs() {

		mTab1 = (ImageView) findViewById(R.id.img_weixin);
		mTab2 = (ImageView) findViewById(R.id.img_address);
		mTab3 = (ImageView) findViewById(R.id.img_friends);
		mTab4 = (ImageView) findViewById(R.id.img_settings);

		mTab1.setOnClickListener(new MyOnClickListener(0));
		mTab2.setOnClickListener(new MyOnClickListener(1));
		mTab3.setOnClickListener(new MyOnClickListener(2));
		mTab4.setOnClickListener(new MyOnClickListener(3));

	}

	class ItemClickListener implements OnItemClickListener {
		List<Map<String, String>> data;

		public ItemClickListener(List<Map<String, String>> data) {
			this.data = data;
		}

		@Override
		public void onItemClick(AdapterView<?> layout, View view, int position,
				long id) {
			Intent intent = new Intent();
			String str = data.get(position).get("userId");
			intent.putExtra("from_id", str);
			intent.putExtra("to_id", mInfo.id);
			String str2 = data.get(position).get("username");
			intent.putExtra("from_name", str2);
			intent.putExtra("to_name", mInfo.name);
			intent.putExtra("from_photo", data.get(position).get("img"));
			intent.putExtra("to_photo", mInfo.photo);

			intent.setClass(MainActivity.this, ChatActivity.class);
			startActivity(intent);
		}
	}

	private FriendListAdapter recent;
	private FriendListAdapter allFriends;
	private NewsAdapter newsAdapter;
	PullToRefreshListView news;

	void initCenterContent() {
		LayoutInflater inflater = LayoutInflater.from(this);

		View view1 = inflater.inflate(R.layout.main_tab_weixin, null);
		View view2 = inflater.inflate(R.layout.main_tab_friends, null);
		View view3 = inflater.inflate(R.layout.main_tab_news, null);
		View view4 = inflater.inflate(R.layout.main_tab_settings, null);

		recent = new FriendListAdapter(this, chatListData);
		allFriends = new FriendListAdapter(this, friendList);
		newsAdapter = new NewsAdapter(this, newsList);

		ListView chatlist = (ListView) view1.findViewById(R.id.chat_list);
		ListView friends = (ListView) view2.findViewById(R.id.friendlistview);
		news = (PullToRefreshListView) view3.findViewById(R.id.news_list);

		friends.setAdapter(allFriends);
		chatlist.setAdapter(recent);
		new Thread() {
			public void run() {
				appcontext.getNews(newsList);
				news.setAdapter(newsAdapter);
			};
		}.start();

		chatlist.setOnItemClickListener(new ItemClickListener(chatListData));
		friends.setOnItemClickListener(new ItemClickListener(friendList));
		news.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				newsList.clear();
				appcontext.getNews(newsList);
				newsAdapter.notifyDataSetChanged();
				news.onRefreshComplete();
			}
		});
		centerContent = new ArrayList<View>();
		centerContent.add(view1);
		centerContent.add(view2);
		centerContent.add(view3);
		centerContent.add(view4);

		LinearLayout mainCenter = (LinearLayout) findViewById(R.id.main_center);

		for (int i = 0; i < centerContent.size(); i++) {
			centerContent.get(i).setVisibility(View.GONE);
			mainCenter.addView(centerContent.get(i));
		}
		centerContent.get(0).setVisibility(View.VISIBLE);
		curIndex = 0;
	}

	private int curIndex;

	/**
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			if (curIndex == index)
				return;
			centerContent.get(index).setVisibility(View.VISIBLE);
			centerContent.get(curIndex).setVisibility(View.GONE);

			switch (index) {
			case 0:
				mTab1.setImageResource(R.drawable.tab_weixin_pressed);
				if (curIndex == 1)
					mTab2.setImageResource(R.drawable.tab_address_normal);
				else if (curIndex == 2)
					mTab3.setImageResource(R.drawable.tab_find_frd_normal);
				else if (curIndex == 3)
					mTab4.setImageResource(R.drawable.tab_settings_normal);
				break;
			case 1:
				mTab2.setImageResource(R.drawable.tab_address_pressed);
				if (curIndex == 0)
					mTab1.setImageResource(R.drawable.tab_weixin_normal);
				else if (curIndex == 2)
					mTab3.setImageResource(R.drawable.tab_find_frd_normal);
				else if (curIndex == 3)
					mTab4.setImageResource(R.drawable.tab_settings_normal);

				break;
			case 2:
				mTab3.setImageResource(R.drawable.tab_find_frd_pressed);
				if (curIndex == 0)
					mTab1.setImageResource(R.drawable.tab_weixin_normal);
				else if (curIndex == 1)
					mTab2.setImageResource(R.drawable.tab_address_normal);
				else if (curIndex == 3)
					mTab4.setImageResource(R.drawable.tab_settings_normal);
				break;
			case 3:
				mTab4.setImageResource(R.drawable.tab_settings_pressed);
				if (curIndex == 0)
					mTab1.setImageResource(R.drawable.tab_weixin_normal);
				else if (curIndex == 1)
					mTab2.setImageResource(R.drawable.tab_address_normal);
				else if (curIndex == 2)
					mTab3.setImageResource(R.drawable.tab_find_frd_normal);
				break;
			}
			curIndex = index;
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (menu_display) { // 如果 Menu已经打开 ，先关闭Menu
				menuWindow.dismiss();
				menu_display = false;
			} else {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, Exit.class);
				startActivity(intent);
			}
		}

		else if (keyCode == KeyEvent.KEYCODE_MENU) { // 获取 Menu键
			if (!menu_display) {
				inflater = (LayoutInflater) this
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				layout = inflater.inflate(R.layout.main_menu, null);

				menuWindow = new PopupWindow(layout, LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT);
				menuWindow.showAtLocation(this.findViewById(R.id.mainweixin),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
				mCloseBtn = (LinearLayout) layout
						.findViewById(R.id.menu_close_btn);

				mCloseBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent();
						intent.setClass(MainActivity.this, Exit.class);

						startActivity(intent);
						menuWindow.dismiss(); // 响应点击事件之后关闭Menu
					}
				});
				menu_display = true;
			} else {
				menuWindow.dismiss();
				menu_display = false;
			}

			return false;
		}
		return false;
	}

	private class MyBroadCastReciver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String str = intent.getStringExtra("msg");
			ChatMessage msg = new ChatMessage(str);

			if (!msg.to_id.equals(mInfo.id)) {
				return;
			}
			User user = appcontext.getUserInfo(msg.from_id, friendPrefer);

			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setDate(appcontext.getDate());
			entity.setText(msg.message);
			entity.setName(user.name);
			entity.setMsgType(true);
			entity.setTime("");
			entity.setPhoto(user.photo);
			if (msg.message.endsWith(".3gp")) {
				long a = SystemClock.currentThreadTimeMillis();
				// FileHelper.downloadAudio(msg.message);
				long b = SystemClock.currentThreadTimeMillis();
				int t = (int) ((b - a) / 100);
				entity.setTime("" + t);
			}

			String data = chatPrefer.getString(user.id, "[]");
			try {
				JSONArray array = new JSONArray(data);
				array.put(new JSONObject(entity.toString()));
				chatPrefer.edit().putString(user.id, array.toString()).commit();
			} catch (JSONException e) {
			}
			int flag = 0;
			for (int i = 0; i < chatPersons.size(); i++) {
				if (msg.from_id.equals(chatPersons.get(i)))
					flag = 1;
			}
			if (flag == 0) {
				chatPersons.add(user.id);
				chatListData.add(user.toMap());
				recent.notifyDataSetChanged();
			}
			Toast.makeText(MainActivity.this,
					user.name + ":" + entity.getText(), Toast.LENGTH_LONG)
					.show();
			// String nofiText = user.name + ":" + msg.message;
			//
			// Intent resultIntent = new Intent(MainActivity.this,
			// ChatActivity.class);
			// resultIntent.putExtra("from_id", user.id);
			// resultIntent.putExtra("to_id", mInfo.id);
			// resultIntent.putExtra("from_name", user.name);
			// resultIntent.putExtra("to_name", mInfo.name);
			// showNotify(nofiText, resultIntent);
		}

	}

	// void showNotify(String notifiText, Intent resultIntent) {
	// String notiTag = "message" + notiId;
	// resultIntent.putExtra("notifytag", notiTag);
	// resultIntent.putExtra("notiId", notiId);
	//
	// NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
	// MainActivity.this).setSmallIcon(R.drawable.ic_launcher)
	// .setContentTitle("Message notify").setContentText(notifiText);
	// PendingIntent resultPendingIntent = PendingIntent
	// .getActivity(MainActivity.this, 0, resultIntent,
	// PendingIntent.FLAG_ONE_SHOT);
	// mBuilder.setContentIntent(resultPendingIntent);
	//
	// //notiManager.notify(notiTag, notiId++, mBuilder.build());
	// }

	private void initFriendInfo(String user) {
		List<User> friends = NetWork.getFriends(user);
		if (friends == null || friends.size() == 0)
			return;

		StringBuffer buffer = new StringBuffer();
		String t;

		for (int i = 0; i < friends.size(); i++) {
			User friend = friends.get(i);
			friendList.add(friend.toMap());
			t = friend.id;
			friendPrefer.edit().putString(t, friend.toString()).commit();
			buffer.append(t + ";");
		}
		String str = buffer.toString();
		str = str.substring(0, str.lastIndexOf(';'));
		friendPrefer.edit().putString("friendList", str).commit();
	}

	public static final int CODE_ADD_FRIEND = 1;

	public void addFriend(View v) {
		new SimpleInputDialog(MainActivity.this,
				new SimpleInputDialog.OnInputListener() {
					@Override
					public void inputFinish(String str) {
						if (str.equals(mInfo.id)) {
							Toast.makeText(MainActivity.this, "自己不能被添加",
									Toast.LENGTH_LONG).show();
							return;
						}
						for (int i = 0; i < friendList.size(); i++) {
							Map<String, String> map = friendList.get(i);

							if (map.get("userId").equals(str)) {
								Toast.makeText(MainActivity.this, "该用户已经是好友了",
										Toast.LENGTH_LONG).show();
								return;
							}
						}
						Intent intent = new Intent();
						intent.putExtra("userId", str);
						intent.putExtra("myId", mInfo.id);

						intent.putExtra("flag", 1);
						intent.setClass(MainActivity.this, PersonInfo.class);
						startActivityForResult(intent, CODE_ADD_FRIEND);
					}
				}).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CODE_ADD_FRIEND
				&& resultCode== PersonInfo.ADD_FRIEND_OVER) {
			String str = data.getStringExtra("result");
			if (str.startsWith("true")) {
				str = "Success!";
				String friend = data.getStringExtra("friend");
				User user = null;
				try {
					user = new User(friend);
				} catch (JSONException e) {
				}
				if (user == null) {
					Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show();
					return;
				}
				friendList.add(user.toMap());
				allFriends.notifyDataSetChanged();
			} else {
				str = "Failed";
			}

			Toast.makeText(this, str, Toast.LENGTH_LONG).show();

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void exit_settings(View v) {
		this.finish();
	}

	public void clear_data(View v) {
		File file = new File(FileHelper.rootPath + "/audio");
		File[] files = file.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				files[i].delete();
			}
		}

		chatPrefer.edit().clear().commit();
		Toast.makeText(this, "Data has been delete", Toast.LENGTH_LONG).show();
		chatPersons.clear();
		chatListData.clear();
		recent.notifyDataSetChanged();
	}

	public void displayUserInfo(View v) {
		Intent intent = new Intent();
		intent.setClass(this, PersonInfo.class);
		intent.putExtra("userId", mInfo.id);
		startActivity(intent);

	}

	public void openPublishInput(View v) {
		Intent intent = new Intent();
		intent.setClass(this, PublishActivity.class);
		startActivity(intent);
	}
}
