package com.huneng.chattool.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.huneng.chattool.app.R;
import com.huneng.chattool.net.HttpWork;

public class Submit extends Activity {
	private String user;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null)
			savedInstanceState = new Bundle();

		setContentView(R.layout.login);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void login_back(View v) {
		finish();
	}

	public void login_mainweixin(View v) {
		EditText userEd = (EditText) findViewById(R.id.login_user_edit);
		EditText passEd = (EditText) findViewById(R.id.login_passwd_edit);
		user = userEd.getText().toString();
		password = passEd.getText().toString();
		if (user == "" || password == "") {
			userEd.setText("");
			passEd.setText("");
			return;
		}
		HttpWork.hw = new HttpWork("http://10.21.241.214:8080", user, password);
		HttpWork.hw.submit();
		if (HttpWork.hw.getResult() != "") {
			getInitData();
			Intent intent = new Intent();
			intent.putExtra("userId", user);
			intent.setClass(Submit.this, MainWeiXin.class);
			startActivity(intent);

			finish();
		}
		
//		if (connectDB(user, password)) {
//			Intent intent = new Intent();
//			intent.putExtra("userId", "136816548");
//			intent.setClass(Submit.this, MainWeiXin.class);
//			startActivity(intent);
//			finish();
//		}
	}

	private void getInitData() {
		String str = HttpWork.hw.getFriendList();

		if (str == "")
			return;

		SharedPreferences friendPrefer = getSharedPreferences("friend",
				MODE_PRIVATE);
		StringBuffer buffer = new StringBuffer();
		try {
			JSONArray array = new JSONArray(str);
			String t;
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = (JSONObject) array.get(i);
				t = object.getString("userId");
				friendPrefer.edit().putString(t, object.toString()).commit();
				buffer.append(t+";");
			}
		} catch (JSONException e) {
		}
		friendPrefer.edit().putString("friendList", buffer.toString()).commit();
		
	}
	

	boolean connectDB(String user, String password) {
		return true;
	}
}
