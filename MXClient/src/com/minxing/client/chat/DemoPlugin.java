package com.minxing.client.chat;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.minxing.client.R;
import com.minxing.client.util.Utils;
import com.minxing.kit.ui.chat.MXChatPlugin;
import com.minxing.kit.ui.chat.MXChatPluginMessageSender;
import com.minxing.kit.ui.chat.MXChatPluginMessge;

public class DemoPlugin extends MXChatPlugin {

	@Override
	public View getPluginView(final Context context, String message) {
		JSONObject json = null;
		try {
			json = JSONObject.parseObject(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (json != null) {
			View view = LayoutInflater.from(context).inflate(R.layout.chat_plugin_demo, null);
			TextView titleTV = (TextView) view.findViewById(R.id.title);
			TextView contentTV = (TextView) view.findViewById(R.id.content);

			final String title = json.getString("title");
			final String content = json.getString("content");

			titleTV.setText(title);
			contentTV.setText(content);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new Builder(context);
					builder.setItems(new String[] { "show Title", "show Content" }, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == 0) {
								Utils.toast(context, title, Toast.LENGTH_SHORT);
								dialog.dismiss();
							} else {
								Utils.toast(context, content, Toast.LENGTH_SHORT);
								dialog.dismiss();
							}
						}
					});
					builder.show();
				}
			});
			return view;
		}
		return null;
	}

	@Override
	public void createPluginMessage(final Context context, final MXChatPluginMessageSender sender) {
		List<MXChatPluginMessge> messages = new ArrayList<MXChatPluginMessge>();
		JSONObject demo = new JSONObject();
		demo.put("title", "this is Title");
		demo.put("content", "this is Content");
		MXChatPluginMessge message = new MXChatPluginMessge(demo.toJSONString(), getKey());
		messages.add(message);
		sender.sendMultiMessage(messages);
	}
}
