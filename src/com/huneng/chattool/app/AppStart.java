package com.huneng.chattool.app;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

public class AppStart extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appstart);
		Intent intent = new Intent(AppStart.this, Submit.class);
		startActivity(intent);
		AppStart.this.finish();
//		new Handler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				Intent intent = new Intent(AppStart.this, Submit.class);
//				startActivity(intent);
//				AppStart.this.finish();
//			}
//		}, 1000);
	}
}
