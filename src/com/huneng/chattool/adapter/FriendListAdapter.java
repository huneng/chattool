package com.huneng.chattool.adapter;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.huneng.chattool.app.MainWeiXin;
import com.huneng.chattool.app.R;
import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendListAdapter extends BaseAdapter {
	LayoutInflater inflater;
	List<Map<String, String>> data;
	MainWeiXin parent;
	public FriendListAdapter(MainWeiXin parent, List<Map<String, String>> data) {
		this.parent = parent;
		inflater = LayoutInflater.from( parent.getApplicationContext());
		this.data = data;
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

	public final class ViewHolder {
		public ImageView img;
		public TextView name;
		public TextView lable;
	}


 	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup p) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.friend_item, null);
			holder = new ViewHolder();
			holder.img = (ImageView) convertView
					.findViewById(R.id.friend_photo);
			holder.name = (TextView) convertView.findViewById(R.id.friend_name);
			holder.lable = (TextView) convertView
					.findViewById(R.id.friend_lable);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String photo = (String) data.get(position).get("img");
		photo = parent.path +"/image/" + photo;
		File file = new File(photo);
		if (!file.isFile()) {
			holder.img.setImageResource(R.drawable.abaose);
		} else {
			holder.img.setBackground(Drawable.createFromPath((String) data.get(
					position).get("img")));
		}
		holder.name.setText(data.get(position).get("username").toString());
		holder.lable.setText(data.get(position).get("lable").toString());
		return convertView;
	}

}
