package com.huneng.chattool.adapter;

import java.util.List;
import java.util.Map;

import com.huneng.chattool.app.FileHelper;
import com.huneng.chattool.app.MainActivity;
import com.huneng.chattool.app.R;
import com.huneng.chattool.data.News;

import com.huneng.chattool.net.NetWork;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsAdapter extends BaseAdapter {
	private MainActivity parent;
	private List<Map<String, String>> data;

	LayoutInflater inflater;

	public NewsAdapter(MainActivity parent, List<Map<String, String>> data) {
		this.parent = parent;
		this.data = data;
		inflater = LayoutInflater.from(parent);
		Log.v("NewsAdapter", data.toString());
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		public ImageView userPhoto;
		public TextView userName;
		public TextView follows_list;
		public TextView publishTime;
		public TextView content;
		public List<TextView> joins;
		public Button joinBtn;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup p) {
		ViewHolder holder = new ViewHolder();
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.news_item, null);
			holder.userPhoto = (ImageView) convertView
					.findViewById(R.id.publish_user_photo);
			holder.userName = (TextView) convertView
					.findViewById(R.id.publish_user_name);
			holder.publishTime = (TextView) convertView
					.findViewById(R.id.publish_time);
			holder.joinBtn = (Button) convertView.findViewById(R.id.join_btn);
			holder.follows_list = (TextView) convertView
					.findViewById(R.id.follow_person_id);
			holder.content = (TextView) convertView
					.findViewById(R.id.news_content);

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		Map<String, String> map = data.get(position);

		//Log.v("NewsAdapter", user.toString());

		Bitmap img = FileHelper.getBitmap(map.get("Creat_photo"));
		img = img == null ? BitmapFactory.decodeResource(parent.getResources(),
				R.drawable.abaose) : img;
		holder.userPhoto.setImageBitmap(img);
		holder.userName.setText(map.get("Creat_name"));
		holder.publishTime.setText(map.get("Time"));
		holder.content.setText(map.get("Content"));
		holder.follows_list.setText(map.get("Follow_id"));
		//Log.v("NewsAdapter", map.toString());
		holder.joinBtn.setOnClickListener(new MyOnClickListener(this, position, map.get("id")));
		
		return convertView;
	}

	class MyOnClickListener implements OnClickListener {
		int position;
		private NewsAdapter adapter;
		String newsId;
		public MyOnClickListener(NewsAdapter adapter, int position, String newsId) {
			this.position = position;
			this.adapter = adapter;
			this.newsId = newsId;
		}

		@Override
		public void onClick(View v) {
			NetWork.followNews(parent.mInfo.id,
					data.get(position).get("id"));
			Log.v("NewsAdapter",newsId+":"+ position);
			NetWork.followNews(parent.mInfo.id, newsId);
			News news = NetWork.getNew(newsId);
			data.set(position, news.toMap());
			adapter.notifyDataSetChanged();
		}
	}
}
