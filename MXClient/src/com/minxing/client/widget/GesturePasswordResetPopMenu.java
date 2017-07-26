package com.minxing.client.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.minxing.client.R;
import com.minxing.client.activity.GesturePasswordActivity;


public class GesturePasswordResetPopMenu extends PopupWindow {

	private Context mContext;
	private View contentView = null;

	private TextView relogin_btn = null;
	private TextView cancel_btn = null;

	@SuppressWarnings("deprecation")
	public GesturePasswordResetPopMenu(Context context) {
		super(context);
		mContext = context;
		contentView = LayoutInflater.from(mContext).inflate(
				R.layout.gesture_password_reset_menu_layout, null);

		relogin_btn = (TextView) contentView.findViewById(R.id.relogin_btn);
		cancel_btn = (TextView) contentView.findViewById(R.id.cancel_btn);
		this.setContentView(contentView);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setAnimationStyle(R.style.popup_animation);
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		this.setTouchable(true);
		this.setBackgroundDrawable(new BitmapDrawable());

		contentView.setClickable(true);
		contentView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		contentView.setFocusableInTouchMode(true);
		contentView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((keyCode == KeyEvent.KEYCODE_MENU) && (isShowing())) {
					dismiss();
					return true;
				}
				return false;
			}
		});

		relogin_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((GesturePasswordActivity) mContext).reset(false, true);
				dismiss();
			}
		});

		cancel_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
}
