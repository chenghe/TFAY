package com.minxing.client.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.minxing.client.R;
import com.minxing.client.util.ResourceUtil;
import com.minxing.kit.MXUIEngine;
import com.minxing.kit.ui.circle.CircleManager;

public class CircleTopRightPopMenu extends PopupWindow {

	private Context mContext;
	private View contentView = null;

	private LinearLayout btn_send_text;
	private LinearLayout btn_create_task;
	private LinearLayout btn_activity;
	private LinearLayout btn_poll;
	private CircleManager circleManager;

	@SuppressWarnings("deprecation")
	public CircleTopRightPopMenu(Context context) {
		super(context);
		mContext = context;
		circleManager = MXUIEngine.getInstance().getCircleManager();
		contentView = LayoutInflater.from(mContext).inflate(R.layout.circle_top_right_menu, null);

		this.setContentView(contentView);
		this.setWidth(LayoutParams.WRAP_CONTENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		this.setTouchable(true);
		this.setBackgroundDrawable(new BitmapDrawable());

		btn_send_text = (LinearLayout) contentView.findViewById(R.id.btn_send_text);
		if (ResourceUtil.getConfBoolean(mContext, "client_circle_disable_text")) {
			btn_send_text.setVisibility(View.GONE);
		} else {
			btn_send_text.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 参数activity:当前Activity
					// 参数name:新消息界面标题
					circleManager.startSendText((Activity) mContext, v.getContext().getResources().getString(R.string.circle_menu_send_text));
					dismiss();
				}
			});
		}
		
		btn_create_task = (LinearLayout) contentView.findViewById(R.id.btn_create_task);
		if (ResourceUtil.getConfBoolean(mContext, "client_circle_disable_task")) {
			btn_create_task.setVisibility(View.GONE);
		} else {
			btn_create_task.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					circleManager.createTask((Activity) mContext, v.getContext().getResources().getString(R.string.circle_menu_create_task));
					dismiss();
				}
			});
		}

		btn_activity = (LinearLayout) contentView.findViewById(R.id.btn_activity);
		if (ResourceUtil.getConfBoolean(mContext, "client_circle_disable_action")) {
			btn_activity.setVisibility(View.GONE);
		} else {
			btn_activity.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					circleManager.setupActivity((Activity) mContext, v.getContext().getResources().getString(R.string.circle_menu_activity));
					dismiss();
				}
			});
		}

		btn_poll = (LinearLayout) contentView.findViewById(R.id.btn_poll);
		if (ResourceUtil.getConfBoolean(mContext, "client_circle_disable_poll")) {
			btn_poll.setVisibility(View.GONE);
		} else {
			btn_poll.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					circleManager.poll((Activity) mContext, v.getContext().getResources().getString(R.string.circle_menu_poll));
					dismiss();
				}
			});
		}
	}
}
