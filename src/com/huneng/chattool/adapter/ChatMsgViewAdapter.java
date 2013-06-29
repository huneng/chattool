package com.huneng.chattool.adapter;

import java.util.List;

import com.huneng.chattool.app.FileHelper;
import com.huneng.chattool.app.R;
import com.huneng.chattool.data.ChatMsgEntity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatMsgViewAdapter extends BaseAdapter {

	public static interface IMsgViewType {
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}

	private List<ChatMsgEntity> coll;

	private LayoutInflater mInflater;
	private MediaPlayer mMediaPlayer = new MediaPlayer();
	private Context context;

	public ChatMsgViewAdapter(Context context, List<ChatMsgEntity> coll) {
		this.coll = coll;
		mInflater = LayoutInflater.from(context);
		this.context = context;
	}

	public int getCount() {
		return coll.size();
	}

	public Object getItem(int position) {
		return coll.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public int getItemViewType(int position) {
		//
		ChatMsgEntity entity = coll.get(position);

		if (entity.getMsgType()) {
			return IMsgViewType.IMVT_COM_MSG;
		} else {
			return IMsgViewType.IMVT_TO_MSG;
		}

	}

	public int getViewTypeCount() {
		return 2;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		boolean isComMsg = coll.get(position).getMsgType();

		ViewHolder viewHolder = null;
		if (convertView == null) {
			if (isComMsg) {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_text_left, null);
			} else {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_text_right, null);
			}

			viewHolder = new ViewHolder();
			viewHolder.tvSendTime = (TextView) convertView
					.findViewById(R.id.tv_sendtime);
			viewHolder.tvUserName = (TextView) convertView
					.findViewById(R.id.tv_username);
			viewHolder.tvContent = (TextView) convertView
					.findViewById(R.id.tv_chatcontent);
			viewHolder.tvTime = (TextView) convertView
					.findViewById(R.id.tv_time);
			viewHolder.isComMsg = isComMsg;
			viewHolder.ivHead = (ImageView) convertView
					.findViewById(R.id.iv_userhead);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvSendTime.setText(coll.get(position).getDate());

		if (coll.get(position).getText().contains(".3gp")) {
			viewHolder.tvContent.setText("");
			viewHolder.tvContent.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.drawable.chatto_voice_playing, 0);

		} else {
			viewHolder.tvContent.setText(coll.get(position).getText());
			viewHolder.tvContent.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					0, 0);
			viewHolder.tvTime.setText("");
		}
		viewHolder.tvContent
				.setOnClickListener(new MyOnClickListener(position));
		viewHolder.tvUserName.setText(coll.get(position).getName());
		viewHolder.tvTime.setText(coll.get(position).getTime());
		Bitmap bitmap = FileHelper.getBitmap(coll.get(position).getPhoto());
		if (bitmap == null) {
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.app_icon);
		}
		viewHolder.ivHead.setImageBitmap(bitmap);
		return convertView;
	}

	class MyOnClickListener implements OnClickListener {
		int position;

		public MyOnClickListener(int index) {
			this.position = index;
		}

		@Override
		public void onClick(View v) {
			if (coll.get(position).getText().contains(".3gp")) {
				playMusic(android.os.Environment.getExternalStorageDirectory()
						+ "/chattool/audio/" + coll.get(position).getText());
				tv = (TextView) v;
			}
		}
	}

	TextView tv;

	static class ViewHolder {
		public TextView tvSendTime;
		public TextView tvUserName;
		public TextView tvContent;
		public TextView tvTime;
		public ImageView ivHead;
		public boolean isComMsg = true;
	}

	private void playMusic(String name) {
		try {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(name);
			mMediaPlayer.prepare();
			mMediaPlayer.start();

			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
