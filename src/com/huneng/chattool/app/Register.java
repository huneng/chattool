package com.huneng.chattool.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.huneng.chattool.data.User;
import com.huneng.chattool.net.NetWork;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends Activity {
	private final static int RESULT_LOAD_IMAGE = 1;
	ImageView register_photo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		register_photo = (ImageView) findViewById(R.id.register_photo);
	}

	public void register(View v) {
		LinearLayout layout = (LinearLayout) findViewById(R.id.register_layout);
		EditText ed = (EditText) findViewById(R.id.register_id_edit);
		String name = ed.getText().toString();
		ed = (EditText) findViewById(R.id.register_passwd_edit);
		String password = ed.getText().toString();
		ed = (EditText) findViewById(R.id.register_passwd_ack_edit);
		String temp = ed.getText().toString();
		if (!password.equals(temp)) {
			Toast.makeText(this, "密码输入不一致", Toast.LENGTH_LONG)
					.show();
			return;
		}
		User user = NetWork.getUser("name");
		if(user!=null){
			Toast.makeText(this, "用户已存在", Toast.LENGTH_LONG)
			.show();
			return;
		}
		String result = NetWork.register(name, password);
		if (result.startsWith("true")) {
			TextView tv = (TextView) findViewById(R.id.display_id_tv);
			tv.setText("注册成功");
			layout.setVisibility(View.GONE);
			tv.setVisibility(View.VISIBLE);
		}

	}

	public void register_back(View v) {
		this.finish();
	}

	int width = 50;
	int height = 50;

	public void choicePhoto(View v) {
		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		width = v.getWidth();
		height = v.getHeight();
		startActivityForResult(i, RESULT_LOAD_IMAGE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			File file = new File(picturePath);
			String name = file.getName();
			String path = Environment.getExternalStorageDirectory()
					+ "/chattool/img/";
			file = new File(path);
			file.mkdirs();
			path += name;
			file = new File(path);

			FileOutputStream fos;
			try {
				fos = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				fos = null;
			}
			Bitmap photo = BitmapFactory.decodeFile(picturePath);
			photo = Bitmap.createScaledBitmap(photo, width, height, true);
			photo.compress(CompressFormat.PNG, 100, fos);
			register_photo.setImageBitmap(photo);
		}
	}
}
