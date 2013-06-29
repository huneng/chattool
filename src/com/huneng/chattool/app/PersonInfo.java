package com.huneng.chattool.app;

import com.huneng.chattool.data.User;
import com.huneng.chattool.net.NetWork;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PersonInfo extends Activity {
	User user;
	String myId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_info);
		Intent intent = getIntent();
		String userId = intent.getStringExtra("userId");
		myId = intent.getStringExtra("myId");
		int flag = intent.getIntExtra("flag", 0);

		user = NetWork.getUser(userId);
		if (user == null) {
			user = new User();
			Toast.makeText(this, "该用户不存在", Toast.LENGTH_LONG).show();
			flag = 0;
		}
		TextView tv = (TextView) findViewById(R.id.info_userid);

		tv.setText(user.id);
		tv = (TextView) findViewById(R.id.info_username);
		tv.setText(user.name);
		tv = (TextView) findViewById(R.id.info_motto);
		tv.setText(user.lable);

		ImageView img = (ImageView) findViewById(R.id.info_user_photo);
		img.setImageBitmap(FileHelper.getBitmap(user.photo));
		Button btn = (Button) findViewById(R.id.add_friend_btn);
		if (flag == 1) {
			btn.setVisibility(View.VISIBLE);
		}
	}

	public void btn_back(View v) {
		this.finish();
	}

	public static final int ADD_FRIEND_OVER = 2;

	public void addFriend(View v) {
		String str = NetWork.addFriends(user, myId);
		if (!str.equals("")) {
			str = "true";
			
		}
		else{
			str = "false";
		}
		Intent intent = new Intent();
		intent.putExtra("result", str);
		intent.putExtra("friend", user.toString());
		setResult(ADD_FRIEND_OVER, intent);
		this.finish();

	}

	@Override
	protected void onPause() {
		finish();
		super.onPause();
	}
}
