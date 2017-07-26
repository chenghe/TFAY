package com.minxing.client.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

import com.minxing.client.AppConstants;
import com.minxing.client.ClientTabActivity;
import com.minxing.client.R;
import com.minxing.client.notification.NotificationHolder;
import com.minxing.kit.api.MXAPI;
import com.minxing.kit.api.bean.ChatMessage;
import com.minxing.kit.api.bean.MXCurrentUser;

public class NotificationUtil {

	private static Timer notificationTimer = null;

	@SuppressWarnings("deprecation")
	public static void handleMessageNotification(final Context context, final ChatMessage message, boolean isRevokeMessage, boolean conversationIsExist) {
		MXCurrentUser currentUser = MXAPI.getInstance(context).currentUser();
		if (currentUser == null) {
			return;
		}

		if (!PreferenceUtils.isMessageNotification(context, currentUser.getLoginName())) {
			return;
		}
//		if (!conversationIsExist){
//			NotificationHolder.getInstance().clear();
//		}
		if(!isRevokeMessage){
			NotificationHolder.getInstance().addMessage(message);
		}
		String content = NotificationHolder.getInstance().getContent(context, message);
		
		boolean isNeedSoundOrShakeNotify = false;
		int currentDisturb = PreferenceUtils.getCurrentDisturbType(context, currentUser.getLoginName());
		switch (currentDisturb) {
		case AppConstants.SYSTEM_SETTING_DISTURB_NONE:
			isNeedSoundOrShakeNotify = true;
			break;
		case AppConstants.SYSTEM_SETTING_DISTURB_NIGHT:
			Calendar start = Calendar.getInstance();
			start.set(Calendar.HOUR_OF_DAY, 8);
			start.set(Calendar.MINUTE, 0);
			start.set(Calendar.SECOND, 0);

			Calendar end = Calendar.getInstance();
			end.set(Calendar.HOUR_OF_DAY, 22);
			end.set(Calendar.MINUTE, 0);
			end.set(Calendar.SECOND, 0);

			Calendar now = Calendar.getInstance();
			now.setTime(new Date());
			if (now.before(end) && now.after(start)) {
				isNeedSoundOrShakeNotify = true;
			}
			break;
		}

		Intent notificationIntent = new Intent(context, ClientTabActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		notificationIntent.putExtra(ClientTabActivity.SHOW_CHAT_LIST_FLAG, true);
		notificationIntent.putExtra(ClientTabActivity.AUTO_ENTER_CHAT_ID, message.getChatID());
		
		long createdTime = message.getCreatedTime();
		if(createdTime == 0){
			createdTime = System.currentTimeMillis();
		}
		
		final Notification notification = new Notification(R.drawable.ic_launcher, content, createdTime);

		notification.defaults = Notification.DEFAULT_LIGHTS;
//		notification.ledARGB = Color.BLUE;
//		notification.ledOnMS = 5000;
		
		boolean isNotifySound = PreferenceUtils.isNotificationSound(context, currentUser.getLoginName()) && isNeedSoundOrShakeNotify;
		final boolean isNotifyShake = PreferenceUtils.isNotificationShake(context, currentUser.getLoginName()) && isNeedSoundOrShakeNotify;
		
		if (isNotifySound) {
			notification.defaults |= Notification.DEFAULT_SOUND;
//			notification.sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
//				    + "://" + context.getPackageName() + "/raw/mailreceived");
		}

		PendingIntent contentItent = PendingIntent.getActivity(context, message.getChatID(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		notification.setLatestEventInfo(context, message.getName(), content, contentItent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		if (notificationTimer != null) {
			notificationTimer.cancel();
			notificationTimer = null;
		}
		notificationTimer = new Timer(true);
		notificationTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				NotificationManager notificationManager = (NotificationManager) context
						.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
				notificationManager.notify(message.getChatID(), notification);
				if (isNotifyShake) {
					Vibrator vib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
					vib.vibrate(new long[] { 300, 300, 300, 300 }, -1);
				}
			}
		}, 500);
	}

	/** 清除通知 */
	public static void clearAllNotification(Context context) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();
		NotificationHolder.getInstance().clear();
	}
}
