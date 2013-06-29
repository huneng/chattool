package com.huneng.chattool.app;

import com.huneng.chattool.data.News;
import com.huneng.chattool.data.User;
import com.huneng.chattool.net.NetWork;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.EditText;

public class PublishActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.publish_news);
	}

	public void publishNews(View v) {
		EditText ed = (EditText) findViewById(R.id.publish_content_ed);
		String text = ed.getText().toString();
		News news = new News();
		Time time = new Time("GMT+8");
		time.setToNow();
		news.time = ""+time.hour+":"+time.minute+":"+time.second;
		news.message = text;
		news.followId="";
		User user = MainActivity.instance.mInfo;
		news.userId = user.id;
		news.creat_img=user.photo;
		news.creat_name = user.name;
		NetWork.publishNews(news);
		this.finish();
	}

	public void publish_back(View v) {
		this.finish();
	}
}
