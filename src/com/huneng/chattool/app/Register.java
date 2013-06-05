package com.huneng.chattool.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
	}

	public void register(View v) {
		EditText ed = (EditText) findViewById(R.id.register_id_edit);
		String id = ed.getText().toString();
		ed = (EditText) findViewById(R.id.register_passwd_edit);
		String password = ed.getText().toString();
		ed = (EditText) findViewById(R.id.register_passwd_ack_edit);
		String temp = ed.getText().toString();
		if (password != temp) {
			Toast.makeText(this, "Password input wrong", Toast.LENGTH_LONG)
					.show();
			return;
		}
		ed = (EditText) findViewById(R.id.register_name_edit);
		String name = ed.getText().toString();
		ed = (EditText) findViewById(R.id.register_birth_edit);
		String birth = ed.getText().toString();
		ed = (EditText) findViewById(R.id.register_address_edit);
		String addr = ed.getText().toString();
		ed = (EditText) findViewById(R.id.register_phone_edit);
		String phone = ed.getText().toString();
	}
}
