package com.huneng.chattool.widget;

import com.huneng.chattool.app.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SimpleInputDialog extends Dialog {
	public String input;
	private EditText ed;
	OnInputListener mListener;

	public SimpleInputDialog(Context context, OnInputListener listener) {
		super(context);
		setContentView(R.layout.text_dialog);
		Button btn1 = (Button) findViewById(R.id.ok_btn);
		Button btn2 = (Button) findViewById(R.id.can_btn);
		ed = (EditText) findViewById(R.id.text_edit);
		btn1.setOnClickListener(l);
		btn2.setOnClickListener(l);
		mListener = listener;
	}

	public SimpleInputDialog(Context context, OnInputListener listener,
			String hint) {
		super(context);
		setContentView(R.layout.text_dialog);
		Button btn1 = (Button) findViewById(R.id.ok_btn);
		Button btn2 = (Button) findViewById(R.id.can_btn);
		ed = (EditText) findViewById(R.id.text_edit);
		ed.setHint(hint);
		btn1.setOnClickListener(l);
		btn2.setOnClickListener(l);
		mListener = listener;
	}

	android.view.View.OnClickListener l = new android.view.View.OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ok_btn:
				String str = ed.getText().toString();
				mListener.inputFinish(str);
				dismiss();
				break;
			case R.id.can_btn:
				dismiss();
				break;
			case R.id.text_edit:
				break;
			}
		}
	};

	public interface OnInputListener {
		public void inputFinish(String str);

	}
}
