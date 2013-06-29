package com.huneng.chattool.app;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;

public class AppStart extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appstart);
		makeDataStoreDir();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(AppStart.this, Welcome.class);
				startActivity(intent);
				AppStart.this.finish();
			}
		}, 1000);

	}

	void makeDataStoreDir() {
		String path = Environment.getExternalStorageDirectory().getPath()
				+ "/chattool";
		File file = new File(path + "/img");
		file.mkdirs();
		file = new File(path + "/audio");
		file.mkdirs();
	}

}
