package com.minxing.client.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.minxing.client.R;
import com.minxing.client.util.ResourceUtil;
import com.minxing.kit.MXKit;
import com.minxing.kit.MXUIEngine;
import com.minxing.kit.ui.chat.ChatManager;
import com.minxing.kit.ui.circle.CircleManager;
import com.minxing.kit.ui.contacts.ContactManager;

public class SystemMainTopRightPopMenu extends PopupWindow {

	private Context mContext;
	private View contentView = null;

	private LinearLayout btn_new_conversation;
	private LinearLayout btn_add_contact;
	private LinearLayout btn_scan;
	private LinearLayout btn_new_circle_message;
	private ChatManager chatManager;

	@SuppressWarnings("deprecation")
	public SystemMainTopRightPopMenu(Context context) {
		super(context);
		mContext = context;
		chatManager = MXUIEngine.getInstance().getChatManager();
		contentView = LayoutInflater.from(mContext).inflate(R.layout.system_top_right_menu, null);

		this.setContentView(contentView);
		this.setWidth(LayoutParams.WRAP_CONTENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		this.setTouchable(true);
		this.setBackgroundDrawable(new BitmapDrawable());

		btn_new_conversation = (LinearLayout) contentView.findViewById(R.id.btn_new_conversation);
		if (ResourceUtil.getConfBoolean(mContext, "client_show_new_conversation", true)) {
			btn_new_conversation.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					chatManager.startNewChat((Activity) mContext);
					((Activity) mContext).overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
					dismiss();
				}
			});
		} else {
			btn_new_conversation.setVisibility(View.GONE);
		}

		btn_add_contact = (LinearLayout) contentView.findViewById(R.id.btn_add_contact);
		btn_add_contact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ContactManager contactManager = MXUIEngine.getInstance().getContactManager();
				contactManager.addContacts((Activity) mContext);
				((Activity) mContext).overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
				dismiss();
			}
		});

		btn_scan = (LinearLayout) contentView.findViewById(R.id.btn_scan);
		if(ResourceUtil.getConfBoolean(mContext, "client_show_scan", true)){
			btn_scan.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					MXKit.getInstance().startScan((Activity) mContext);
					((Activity) mContext).overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
					dismiss();
				}
			});
		} else {
			btn_scan.setVisibility(View.GONE);
		}

		btn_new_circle_message = (LinearLayout) contentView.findViewById(R.id.btn_new_circle_message);
		String tabSortHide = ResourceUtil.getConfString(context, "client_sort_hide");
		if (TextUtils.isEmpty(tabSortHide) || (!TextUtils.isEmpty(tabSortHide) && tabSortHide.contains("circle"))) {
			btn_new_circle_message.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					CircleManager circleManager = MXUIEngine.getInstance().getCircleManager();
					circleManager.shareToCircle((Activity) mContext);
					((Activity) mContext).overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
					dismiss();
				}
			});
		} else {
			btn_new_circle_message.setVisibility(View.GONE);
		}
	}

	public void isAddContactEnabled(boolean isEnable) {
		btn_add_contact.setVisibility(isEnable ? View.VISIBLE : View.GONE);
	}
}
