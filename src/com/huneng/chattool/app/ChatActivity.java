package com.huneng.chattool.app;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.huneng.chattool.adapter.ChatMsgViewAdapter;
import com.huneng.chattool.data.ChatMessage;
import com.huneng.chattool.data.ChatMsgEntity;
import com.huneng.chattool.net.NetWork;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChatActivity extends Activity {

	private TextView mRecordBtn;
	private TextView mChatTitle;
	private EditText mMsgEdit;
	private RelativeLayout mBottom;
	private ListView mChatListView;
	private ChatMsgViewAdapter mAdapter;
	List<ChatMsgEntity> mDatas = new LinkedList<ChatMsgEntity>();;
	private boolean isShort = false;
	private LinearLayout voice_rcd_hint_loading;
	private LinearLayout voice_rcd_hint_rcding;
	private LinearLayout voice_rcd_hint_tooshort;
	private SoundMeter mSensor;
	private View rcChat_popup;
	private ImageView chatting_mode_btn, volume;
	private boolean mode_voice = false;
	private int flag = 1;
	private Handler mHandler = new Handler();
	private String voiceName;
	private long startVoiceT, endVoiceT;
	private String userId;
	private SharedPreferences chatPrefer;
	private String name1;
	private String myname;
	private String path;
	private String myId;
	private String from_photo;
	private String to_photo;
	private AppContext appcontext;

	/**
	 * chatPrefer chatList:chatData chatData:userid, content,time, date
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		myId = intent.getStringExtra("to_id");
		userId = intent.getStringExtra("from_id");
		name1 = intent.getStringExtra("from_name");
		myname = intent.getStringExtra("to_name");
		from_photo = intent.getStringExtra("from_photo");
		to_photo = intent.getStringExtra("to_photo");
		Log.v("huneng", "Talk budy ids:" + userId + "," + myId);

		// int notiId = intent.getIntExtra("notiId", -1);
		// Log.v("huneng", "Notification Id:" + notiId);
		// if (notiId != -1) {
		// NotificationManager notiMgr = (NotificationManager)
		// getSystemService(NOTIFICATION_SERVICE);
		// notiMgr.cancel("message" + notiId, notiId);
		// }
		setContentView(R.layout.chat);
		// 启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		initView();

		path = FileHelper.rootPath + "/audio";
		try {
			initData();
		} catch (JSONException e) {
		}
		IntentFilter filter = new IntentFilter("com.huneng.chattool.msg");
		MyBroadCastReciver receiver = new MyBroadCastReciver();
		registerReceiver(receiver, filter);
		appcontext = (AppContext) getApplication();

	}

	public void getPersonInfo(View v) {
		Intent intent = new Intent();
		intent.setClass(this, PersonInfo.class);
		intent.putExtra("userId", userId);
		startActivity(intent);
	}

	void initData() throws JSONException {
		chatPrefer = getSharedPreferences(MainActivity.CHAT_RECORD_PREFER_NAME,
				MODE_PRIVATE);
		mDatas.clear();
		String str = chatPrefer.getString(userId, "[]");
		JSONArray array = new JSONArray(str);
		JSONObject object;
		for (int i = 0; i < array.length(); i++) {
			ChatMsgEntity entity = new ChatMsgEntity();
			object = array.getJSONObject(i);
			entity.setTime(object.getString("time"));
			entity.setDate(object.getString("date") + entity.getTime());
			if (object.getString("name").equals(name1)) {
				entity.setMsgType(true);
				entity.setName(name1);
				entity.setPhoto(from_photo);
			} else {
				entity.setMsgType(false);
				entity.setName(myname);
				entity.setPhoto(to_photo);
			}
			entity.setText(object.getString("text"));
			mDatas.add(entity);
		}
		mAdapter = new ChatMsgViewAdapter(this, mDatas);
		mChatListView.setAdapter(mAdapter);
		int t = mChatListView.getCount() - 1;
		if (t >= 0)
			mChatListView.setSelection(t);
	}

	public void initView() {
		mChatTitle = (TextView) findViewById(R.id.chat_title);
		mChatTitle.setText(name1);
		mChatListView = (ListView) findViewById(R.id.listview);
		mRecordBtn = (TextView) findViewById(R.id.btn_rcd);
		chatting_mode_btn = (ImageView) this.findViewById(R.id.ivPopUp);
		volume = (ImageView) this.findViewById(R.id.volume);
		rcChat_popup = this.findViewById(R.id.rcChat_popup);
		voice_rcd_hint_rcding = (LinearLayout) this
				.findViewById(R.id.voice_rcd_hint_rcding);
		voice_rcd_hint_loading = (LinearLayout) this
				.findViewById(R.id.voice_rcd_hint_loading);
		voice_rcd_hint_tooshort = (LinearLayout) this
				.findViewById(R.id.voice_rcd_hint_tooshort);
		mSensor = new SoundMeter();
		mMsgEdit = (EditText) findViewById(R.id.et_sendmessage);
		mBottom = (RelativeLayout) findViewById(R.id.btn_bottom);
		// 语音文字切换按钮
		chatting_mode_btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (mode_voice) {
					mRecordBtn.setVisibility(View.GONE);
					mBottom.setVisibility(View.VISIBLE);
					mode_voice = false;
					chatting_mode_btn
							.setImageResource(R.drawable.chatting_setmode_msg_btn);

				} else {
					mRecordBtn.setVisibility(View.VISIBLE);
					mBottom.setVisibility(View.GONE);
					chatting_mode_btn
							.setImageResource(R.drawable.chatting_setmode_voice_btn);
					mode_voice = true;
				}
			}
		});
		mRecordBtn.setOnTouchListener(touch_listener);
	}

	OnTouchListener touch_listener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN && flag == 1) {
				System.out.println("begin record");
				mRecordBtn
						.setBackgroundResource(R.drawable.voice_rcd_btn_pressed);
				rcChat_popup.setVisibility(View.VISIBLE);
				voice_rcd_hint_loading.setVisibility(View.VISIBLE);
				voice_rcd_hint_rcding.setVisibility(View.GONE);
				mHandler.postDelayed(new Runnable() {
					public void run() {
						if (!isShort) {
							voice_rcd_hint_loading.setVisibility(View.GONE);
							voice_rcd_hint_rcding.setVisibility(View.VISIBLE);
						}
					}
				}, 300);
				startVoiceT = System.currentTimeMillis();

				voiceName = "/audio" + startVoiceT + ".3gp";
				start(path + voiceName);
				flag = 2;
			} else if (event.getAction() == MotionEvent.ACTION_UP && flag == 2) {
				System.out.println("Record over");
				mRecordBtn.setBackgroundResource(R.drawable.voice_rcd_btn_nor);
				voice_rcd_hint_rcding.setVisibility(View.GONE);

				stop();
				flag = 1;
				endVoiceT = System.currentTimeMillis();
				int time = (int) ((endVoiceT - startVoiceT) / 1000);
				if (time < 1) {
					isShort = true;
					voice_rcd_hint_loading.setVisibility(View.GONE);
					voice_rcd_hint_rcding.setVisibility(View.GONE);
					voice_rcd_hint_tooshort.setVisibility(View.VISIBLE);
					mHandler.postDelayed(new Runnable() {
						public void run() {
							voice_rcd_hint_tooshort.setVisibility(View.GONE);
							rcChat_popup.setVisibility(View.GONE);
							isShort = false;
						}
					}, 500);
					return false;
				}
				ChatMsgEntity entity = new ChatMsgEntity();
				entity.setDate(appcontext.getDate());
				entity.setName(myname);
				entity.setMsgType(false);
				entity.setTime(time + "\"");
				entity.setText(voiceName);
				entity.setPhoto(to_photo);
				mDatas.add(entity);
				mAdapter.notifyDataSetChanged();
				mChatListView.setSelection(mChatListView.getCount() - 1);
				rcChat_popup.setVisibility(View.GONE);
			}
			return true;
		}
	};

	class MyBroadCastReciver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String str = intent.getStringExtra("msg");
			ChatMessage msg = new ChatMessage(str);
			if (!(msg.from_id.equals(userId))) {
				return;
			}
			handleReceiveData(msg);
		}
	}

	void handleReceiveData(ChatMessage msg) {
		ChatMsgEntity entity = new ChatMsgEntity();
		entity.setDate(appcontext.getDate());
		entity.setText(msg.message);
		entity.setName(name1);
		entity.setMsgType(true);
		entity.setPhoto(from_photo);
		if (msg.message.endsWith(".3gp")) {
			long a = SystemClock.currentThreadTimeMillis();
			NetWork.downloadAudio(msg.message);
			long b = SystemClock.currentThreadTimeMillis();
			int t = (int) ((b - a) / 100);
			entity.setTime("" + t);
		} else {
			entity.setTime("");
		}
		mDatas.add(entity);
		mAdapter.notifyDataSetChanged();
		mChatListView.setSelection(mChatListView.getCount() - 1);
	}

	private void send() {
		String contString = mMsgEdit.getText().toString();
		if (contString.length() > 0) {
			ChatMsgEntity entity = new ChatMsgEntity();

			entity.setDate(appcontext.getDate());
			entity.setTime("");
			entity.setName(myname);
			entity.setMsgType(false);
			entity.setText(contString);
			entity.setPhoto(to_photo);
			mDatas.add(entity);
			mAdapter.notifyDataSetChanged();

			mChatListView.setSelection(mChatListView.getCount() - 1);
			mMsgEdit.setText("");
			ChatMessage msg = new ChatMessage();
			msg.from_id = myId;
			msg.to_id = userId;
			msg.message = entity.getText();
			if (MainActivity.msglistener != null)
				MainActivity.msglistener.sendMsg(msg.toString());
			if (msg.message.endsWith(".3gp")) {
			}
		}
	}

	private static final int POLL_INTERVAL = 300;

	private Runnable mSleepTask = new Runnable() {
		public void run() {
			stop();
		}
	};
	private Runnable mPollTask = new Runnable() {
		public void run() {
			double amp = mSensor.getAmplitude();
			updateDisplay(amp);
			mHandler.postDelayed(mPollTask, POLL_INTERVAL);

		}
	};

	private void start(String name) {
		mSensor.start(name);
		mHandler.postDelayed(mPollTask, POLL_INTERVAL);
	}

	private void stop() {
		mHandler.removeCallbacks(mSleepTask);
		mHandler.removeCallbacks(mPollTask);
		mSensor.stop();
		volume.setImageResource(R.drawable.amp1);
	}

	private void updateDisplay(double signalEMA) {

		switch ((int) signalEMA) {
		case 0:
		case 1:
			volume.setImageResource(R.drawable.amp1);
			break;
		case 2:
		case 3:
			volume.setImageResource(R.drawable.amp2);
			break;
		case 4:
		case 5:
			volume.setImageResource(R.drawable.amp3);
			break;
		case 6:
		case 7:
			volume.setImageResource(R.drawable.amp4);
			break;
		case 8:
		case 9:
			volume.setImageResource(R.drawable.amp5);
			break;
		case 10:
		case 11:
			volume.setImageResource(R.drawable.amp6);
			break;
		default:
			volume.setImageResource(R.drawable.amp7);
			break;
		}
	}

	public void btnBack(View v) {
		finish();
	}

	public void btnSend(View v) {
		send();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
		}
		return true;
	}

	@Override
	protected void onPause() {
		String str = "";
		int i;

		for (i = 0; i < mDatas.size() - 1; i++) {
			str += mDatas.get(i).toString() + ',';

		}
		if (mDatas.size() > i)
			str = "[" + str + mDatas.get(i) + "]";
		SharedPreferences.Editor editor = chatPrefer.edit();
		editor.putString(userId, str).commit();
		super.onPause();
	}

}