package com.huneng.chattool.adapter;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.huneng.chattool.app.MainWeiXin;
import com.huneng.chattool.app.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ExAdapter extends BaseExpandableListAdapter {
	Context context;
	List<Map<String, String>> groupData;
	List<List<Map<String, String>>> itemData;
	MainWeiXin parent;
	public ExAdapter(MainWeiXin parent) {
		new ExAdapter(parent, null, null);
	}

	public ExAdapter(MainWeiXin parent, List<Map<String, String>> group,
			List<List<Map<String, String>>> items) {
		this.parent = parent;
		this.context = parent.getApplicationContext();
		this.groupData = group;
		this.itemData = items;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return itemData.get(groupPosition).get(childPosition).get("userId");
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@SuppressLint("NewApi")
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup p) {
		if(convertView==null){
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.friend_item, null);
		}
		TextView name = (TextView)convertView.findViewById(R.id.friend_name);
		TextView lable =  (TextView)convertView.findViewById(R.id.friend_lable);
		ImageView img = (ImageView)convertView.findViewById(R.id.friend_photo);
		Map<String, String> item = itemData.get(groupPosition).get(childPosition);
		name.setText(item.get("name"));
		lable.setText(item.get("lable"));
		
		String photo = (String) item.get("img");
		photo = parent.path + "/img/" + photo;
		File file = new File(photo);
		if (!file.isFile()) {
			img.setImageResource(R.drawable.abaose);
		} else {
			img.setBackground(Drawable.createFromPath(photo));
		}
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {

		return itemData.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {

		return groupData.get(groupPosition).get("name");
	}

	@Override
	public int getGroupCount() {

		return groupData.size();
	}

	@Override
	public long getGroupId(int groupPosition) {

		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.group, null);

		}
		TextView tv = (TextView) convertView.findViewById(R.id.group_name);
		Map<String, String> group = groupData.get(groupPosition);
		tv.setText(group.get("name"));
		ImageButton btn = (ImageButton) convertView
				.findViewById(R.id.expand_switch_btn);
		if (isExpanded)
			btn.setBackgroundResource(R.drawable.pack_up_btn);
		else
			btn.setBackgroundResource(R.drawable.expand_btn);

		return convertView;
	}

	@Override
	public boolean hasStableIds() {

		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {

		return true;
	}

}
