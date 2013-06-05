package com.huneng.chattool.adapter;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.huneng.chattool.app.MainWeiXin;
import com.huneng.chattool.app.R;
import com.huneng.chattool.data.UserInformation;
import com.huneng.chattool.net.HttpWork;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NewsAdapter extends BaseAdapter {
	List<Map<String, Object>> maps;
	Context context;
	MainWeiXin parent;

	public NewsAdapter(MainWeiXin parent, List<Map<String, Object>> map) {
		this.maps = map;
		this.parent = parent;
		this.context = parent.getApplicationContext();
	}

	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	class ViewHolder {
		public ImageView photo;
		public TextView name;
		public TextView time;
		public Button add;
		public TextView content;
		public LinearLayout layout;
		public List<TextView> list;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup p) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.news_item, null);
			holder.photo = (ImageView) convertView
					.findViewById(R.id.publish_user_photo);
			holder.name = (TextView) convertView
					.findViewById(R.id.publish_user_name);
			holder.time = (TextView) convertView
					.findViewById(R.id.publish_time);
			holder.layout = (LinearLayout) convertView
					.findViewById(R.id.join_person_list_layout);
			holder.content = (TextView) convertView
					.findViewById(R.id.news_content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Map<String, Object> map = maps.get(position);
		
		String photo = (String) map.get("img");
		photo = parent.path + "/image/" + photo;
		
		File file = new File(photo);
		if (!file.isFile()) {
			holder.photo.setImageResource(R.drawable.abaose);
		} else {
			holder.photo.setBackground(Drawable.createFromPath(photo));
		}
		
		holder.name.setText(map.get("name").toString());
		holder.content.setText(map.get("message").toString());
		String[] follows = (String[]) map.get("followId");
		for (int i = 0; i < follows.length; i++) {
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			JoinTextView join = new JoinTextView(context, follows[i]);
			holder.layout.addView(join, params);
		}
		return convertView;
	}

	public class JoinTextView extends TextView {
		String id;
		UserInformation info;

		public JoinTextView(Context context, String id) {
			super(context);
			this.id = id;
			info = null;
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				String str = HttpWork.hw.getUserInfo(id);
				try {
					info = new UserInformation(str);
				} catch (JSONException e) {
					info = new UserInformation();
				}
			}
			return true;
		}
	}
}
