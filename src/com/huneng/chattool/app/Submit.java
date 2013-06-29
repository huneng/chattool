package com.huneng.chattool.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.huneng.chattool.app.R;
import com.huneng.chattool.net.NetWork;

public class Submit extends Activity {
	private String user;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_login);
	}



	public void login_mainweixin(View v) {
		EditText userEd = (EditText) findViewById(R.id.et_login);
		EditText passEd = (EditText) findViewById(R.id.et_password);
		user = userEd.getText().toString();
		password = passEd.getText().toString();

		String str = NetWork.submit(user, password);
		if (str.startsWith("true")) {
			Intent intent = new Intent();
			intent.putExtra("userId", user);
			intent.setClass(Submit.this, MainActivity.class);
			startActivity(intent);

			this.finish();
			return;
		}
		Toast.makeText(this, "ÓÃ»§Ãû´íÎó£¡", Toast.LENGTH_LONG).show();
	}
}
