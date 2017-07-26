package com.minxing.client.notification;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.minxing.client.R;
import com.minxing.kit.api.bean.ChatMessage;

public class NotificationHolder {
	private static NotificationHolder instance;

	private NotificationHolder() {
	}

	private Map<Integer, Integer> notificationMap = new HashMap<Integer, Integer>();
	private Map<Integer, String> notificationSenderNameMap = new HashMap<Integer, String>();

	public static NotificationHolder getInstance() {
		if (instance == null) {
			instance = new NotificationHolder();
		}
		return instance;
	}

	public void addMessage(ChatMessage message) {
		Integer messageCount = notificationMap.get(message.getChatID());
		if (messageCount != null) {
			notificationMap.put(message.getChatID(), messageCount + 1);
		} else {
			notificationMap.put(message.getChatID(), 1);
		}

		if (message.isMultiUser()) {
			notificationSenderNameMap.put(message.getChatID(), message.getSender() + ":");
		}
	}

	public int getMessageCount(ChatMessage message) {
		Integer count = notificationMap.get(message.getChatID());
		if (count != null) {
			return count;
		}
		return 1;
	}

	public void clear() {
		notificationMap.clear();
		notificationSenderNameMap.clear();
	}

	public boolean checkChatMessage(int chatID) {
		return notificationMap.containsKey(chatID);
	}

	public String getContent(Context context, ChatMessage message) {
		String senderName = notificationSenderNameMap.get(message.getChatID());
		int count = getMessageCount(message);
		if (count > 1) {
			return context.getString(R.string.chat_notification_unit_start) + count + context.getString(R.string.chat_notification_unit_end)
					+ (senderName != null && !"".equals(senderName) ? senderName : "") + message.getContent();
		} else {
			return (senderName != null && !"".equals(senderName) ? senderName : "") + message.getContent();
		}
	}
}
