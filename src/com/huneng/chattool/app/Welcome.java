package com.huneng.chattool.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Welcome extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
	}

	public void register(View v) {
		Intent intent = new Intent();
		intent.setClass(Welcome.this, Register.class);
		startActivity(intent);
	}

	public void login(View v) {
		Intent intent = new Intent();
		intent.setClass(Welcome.this, Submit.class);
		startActivity(intent);
		this.finish();
	}
}
