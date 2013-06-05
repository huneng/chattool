package com.huneng.chattool.app;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import com.huneng.chattool.adapter.FriendListAdapter;
import com.huneng.chattool.adapter.NewsAdapter;
import com.huneng.chattool.app.R;
import com.huneng.chattool.data.ChatMsgEntity;
import com.huneng.chattool.data.News;
import com.huneng.chattool.data.UserInformation;
import com.huneng.chattool.net.HttpWork;
import com.huneng.chattool.net.MsgListenerThread;
import com.huneng.chattool.widget.PullToRefreshListView;
import com.huneng.chattool.widget.PullToRefreshListView.OnRefreshListener;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

public class MainWeiXin extends Activity {

	protected static final int CHAT_CONTENT = 0;

	public static MainWeiXin instance = null;

	private ViewPager mTabPager;
	private ImageView mTabImg;// 动画图片
	private ImageView mTab1, mTab2, mTab3, mTab4;
	private int zero = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int one;// 单个水平动画位移
	private int two;
	private int three;
	private LinearLayout mCloseBtn;
	private View layout;
	private boolean menu_display = false;
	private PopupWindow menuWindow;
	private LayoutInflater inflater;
	private ArrayList<View> centerContent;
	private List<Map<String, String>> chatListData;
	private List<Map<String, String>> friendList;
	public String path;
	SharedPreferences chatPrefer; //
	SharedPreferences friendPrefer;
	private List<String> chatPersons;
	public String userId;
	UserInformation mInfo;
	NotificationManager notiManager;

	/*
	 * Note: chatPrefer记录聊天记录，包括聊天对象列表chatList friendPrefer保存用户好友资料
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_weixin);

		// 启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		Intent intent = getIntent();
		userId = intent.getStringExtra("userId");
		chatPrefer = getSharedPreferences("chat", MODE_PRIVATE);
		friendPrefer = getSharedPreferences("friend", MODE_PRIVATE);
		chatPersons = new ArrayList<String>();
		try {
			mInfo = new UserInformation(HttpWork.hw.getUserInfo(userId));
		} catch (JSONException e) {
		}
		// mInfo = new UserInformation();
		// mInfo.id = userId;
		// mInfo.name = "huneng";
		// mInfo.age = 22;
		// mInfo.lable = "I am good man";

		notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		IntentFilter filter = new IntentFilter("chattool.net.thread");

		MyBroadCastReciver receiver = new MyBroadCastReciver();
		registerReceiver(receiver, filter);

		newsList = new LinkedList<Map<String, Object>>();
		chatListData = new LinkedList<Map<String, String>>();
		friendList = new LinkedList<Map<String, String>>();

		initTabs();
		initChatAndFriendList(); // 初始化本地聊天记录和好友数据
		initOnlineFriend();// 在线好友监听器设置
		initCenterContent();// 初始化中部界面
	}

	void makeDataStoreDir() {
		path = Environment.getExternalStorageDirectory().getPath()
				+ "/chattool";
		File file = new File(path + "/img");
		file.mkdirs();
		file = new File(path + "/audio");
		file.mkdirs();
	}

	void initChatAndFriendList() {
		// 聊天数据
		Map<String, String> map;
		String str = chatPrefer.getString("chatList", "");
		if (str != "") {
			String temp[] = str.split(";");
			for (int i = 0; i < temp.length; i++) {
				UserInformation user = getUserInfo(temp[i]);
				if (user == null)
					continue;
				map = new HashMap<String, String>();
				map.put("userId", temp[i]);
				map.put("name", user.name);
				map.put("img", user.photo);
				map.put("lable", user.lable);
				chatPersons.add(temp[i]);
				chatListData.add(map);
			}

		}
		str = friendPrefer.getString("friendList", "");
		if (str != "") {
			String[] friends = str.split(";");
			for (int i = 0; i < friends.length; i++) {
				UserInformation user = getUserInfo(friends[i]);
				if (user == null)
					continue;
				map = user.toMap();
				friendList.add(map);
			}
		}
	}

	private void initOnlineFriend() {
		String str = HttpWork.hw.getOnlineFriendList();
		if (str != null) {
			try {
				JSONArray array = new JSONArray(str);
				for (int i = 0; i < array.length(); i++) {
					String t = array.getString(i);
					MsgListenerThread thread = new MsgListenerThread(this, t);
					thread.start();
				}
			} catch (JSONException e) {
			}
		}
	}

	protected void onStop() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < chatPersons.size(); i++) {
			buffer.append(chatPersons.get(i));
		}
		chatPrefer.edit().putString("chatList", buffer.toString());

	};

	UserInformation getUserInfo(String id) {
		String str = friendPrefer.getString(id, "");
		if (str == "") {
			str = HttpWork.hw.getUserInfo(id);
		}
		UserInformation user = null;
		try {
			user = new UserInformation(str);
		} catch (JSONException e) {
		}
		return user;

	}

	private class MyBroadCastReciver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String id = intent.getStringExtra("userId");
			String msg = intent.getStringExtra("message");
			if (msg == "")
				return;

			UserInformation user = getUserInfo(id);

			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setDate(ChatActivity.getDate());
			entity.setText(msg);
			entity.setName(user.name);
			entity.setMsgType(true);

			String data = chatPrefer.getString(user.id, "[]");
			if (msg.endsWith(".3gp")) {
				int t = HttpWork.hw.downloadFile(msg);
				entity.setTime("" + t);
			} else {
				entity.setTime("");
			}

			JSONArray array;
			try {
				array = new JSONArray(data);
				array.put(entity.toString());
				chatPrefer.edit().putString(user.id, array.toString()).commit();
			} catch (JSONException e) {
			}

			int flag = 0;
			for (int i = 0; i < chatPersons.size(); i++) {
				if (id == chatPersons.get(i))
					flag = 1;
			}
			if (flag == 0) {
				chatPersons.add(user.id);
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", user.id);
				map.put("name", user.name);
				map.put("img", user.photo);
				map.put("lable", user.lable);
				chatListData.add(map);
				recent.notifyDataSetChanged();
			}

			String nofiText = user.name + ":" + msg;

			Intent resultIntent = new Intent(MainWeiXin.this,
					ChatActivity.class);
			intent.putExtra("userId", id);
			intent.putExtra("name1", user.name);
			intent.putExtra("name2", mInfo.name);
			showNotify(nofiText, resultIntent);
		}

	}

	public static int notiId = 0;

	void showNotify(String notifiText, Intent resultIntent) {
		String notiTag = "message" + notiId;
		resultIntent.putExtra("notifytag", notiTag);
		resultIntent.putExtra("notiId", notiId);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				MainWeiXin.this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Message notify").setContentText(notifiText);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(
				MainWeiXin.this, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT);
		mBuilder.setContentIntent(resultPendingIntent);

		NotificationManager notiMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notiMgr.notify(notiTag, notiId++, mBuilder.build());
	}

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
		Display display = getWindowManager().getDefaultDisplay();// 获取屏幕当前分辨率
		@SuppressWarnings("deprecation")
		int displayWidth = display.getWidth();
		one = displayWidth / 4; // 设置水平动画平移大小
		two = one * 2;
		three = one * 3;

		mTabPager = (ViewPager) findViewById(R.id.tabpager);
		mTabPager.setOnPageChangeListener(new MyOnPageChangeListener());

		mTab1 = (ImageView) findViewById(R.id.img_weixin);
		mTab2 = (ImageView) findViewById(R.id.img_address);
		mTab3 = (ImageView) findViewById(R.id.img_friends);
		mTab4 = (ImageView) findViewById(R.id.img_settings);
		mTabImg = (ImageView) findViewById(R.id.img_tab_now);
		mTabImg.setLayoutParams(new LayoutParams(displayWidth / 4,
				LayoutParams.WRAP_CONTENT));
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
			intent.putExtra("userId", str);
			intent.putExtra("name1", data.get(position).get("name"));
			intent.putExtra("name2", mInfo.name);
			intent.setClass(MainWeiXin.this, ChatActivity.class);
			startActivity(intent);
		}
	}

	private FriendListAdapter recent;
	private FriendListAdapter allFriends;
	private NewsAdapter newsAdapter;

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
		PullToRefreshListView news = (PullToRefreshListView) view3
				.findViewById(R.id.news_list);
		friends.setAdapter(allFriends);
		chatlist.setAdapter(recent);
		news.setAdapter(newsAdapter);
		news.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				getNews();
				newsAdapter.notifyDataSetChanged();
			}
		});
		chatlist.setOnItemClickListener(new ItemClickListener(chatListData));
		friends.setOnItemClickListener(new ItemClickListener(friendList));

		centerContent = new ArrayList<View>();
		centerContent.add(view1);
		centerContent.add(view2);
		centerContent.add(view3);
		centerContent.add(view4);

		PagerAdapter mPagerAdapter = new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return centerContent.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(centerContent.get(position));
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(centerContent.get(position));
				return centerContent.get(position);
			}
		};
		mTabPager.setAdapter(mPagerAdapter);
	}

	private List<Map<String, Object>> newsList;

	void getNews() {
		String str = HttpWork.hw.getNews();
		newsList.clear();
		if (str == "")
			return;
		JSONArray array = null;
		try {
			array = new JSONArray(str);
		} catch (JSONException e) {

		}
		if (array == null)
			return;
		for (int i = 0; i < array.length(); i++) {
			News news;
			try {
				news = new News(array.get(i).toString());
			} catch (JSONException e) {
				news = null;
			}
			if (news != null) {
				Map<String, Object> map = news.toMap();
				newsList.add(map);
			}
		}
	}

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
			mTabPager.setCurrentItem(index);
		}
	};

	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				mTab1.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_weixin_pressed));
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_address_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_find_frd_normal));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, 0, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_settings_normal));
				}
				break;
			case 1:
				mTab2.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_address_pressed));
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, one, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_weixin_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_find_frd_normal));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, one, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_settings_normal));
				}
				break;
			case 2:
				mTab3.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_find_frd_pressed));
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, two, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_weixin_normal));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_address_normal));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, two, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_settings_normal));
				}
				break;
			case 3:
				mTab4.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_settings_pressed));
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, three, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_weixin_normal));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, three, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_address_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, three, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_find_frd_normal));
				}
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(150);
			mTabImg.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (menu_display) { // 如果 Menu已经打开 ，先关闭Menu
				menuWindow.dismiss();
				menu_display = false;
			} else {
				Intent intent = new Intent();
				intent.setClass(MainWeiXin.this, Exit.class);
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

				// 比如单击某个MenuItem的时候，他的背景色改变
				// 事先准备好一些背景图片或者颜色
				mCloseBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent();
						intent.setClass(MainWeiXin.this, Exit.class);

						startActivity(intent);
						menuWindow.dismiss(); // 响应点击事件之后关闭Menu
					}
				});
				menu_display = true;
			} else {
				// 如果当前已经为显示状态，则隐藏起来
				menuWindow.dismiss();
				menu_display = false;
			}

			return false;
		}
		return false;
	}

	public void btnmainright(View v) {

	}

	public void exit_settings(View v) {

	}

	public void btn_shake(View v) {

	}

	public void displayUserInfo(View v) {

	}
}
