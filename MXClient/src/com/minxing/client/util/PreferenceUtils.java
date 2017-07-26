package com.minxing.client.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.minxing.client.AppConstants;

public class PreferenceUtils {
	
	private static final String PREFERENCE_SYSTEM = "system_preference";
	
	private static final String PREFERENCE_ITEM_IS_FTT = "isAPPFTT";
	private static final String PREFERENCE_ITEM_LOGIN_NAME = "login_name";
	private static final String PREFERENCE_ITEM_GESTURE_PWD = "gesture_pwd";
	private static final String PREFERENCE_ITEM_GESTURE_PWD_ENABLE = "gesture_pwd_enable";
	private static final String PREFERENCE_ITEM_SYSTEM_NOTIFICATION = "preference_notification";
	private static final String PREFERENCE_ITEM_MAIL_NOTIFICATION = "preference_mail_notification";
	private static final String PREFERENCE_ITEM_NOTIFICATION_SOUND = "preference_notification_sound";
	private static final String PREFERENCE_ITEM_NOTIFICATION_SHAKE = "preference_notification_shake";
	private static final String PREFERENCE_ITEM_DISTURB = "preference_disturb";
	private static final String PREFERENCE_ITEM_UPGRADE_MARK = "client_upgrade";
	private static final String PREFERENCE_ITEM_GESTURE_PWD_VIEW = "preference_gesture_pwd_view_enable";
	private static final String PREFERENCE_ITEM_IS_AT_MEETING = "preference_is_at_meeting";
	private static final String PREFERENCE_ITEM_TIME_START_VIDEO_REQUEST = "time_start_video_request";
	
	public static void saveAPPFTTStatus(Context context) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		sp.edit().putBoolean(PREFERENCE_ITEM_IS_FTT, Boolean.FALSE).commit();
	}

	public static boolean isAPPFTT(Context context) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		return sp.getBoolean(PREFERENCE_ITEM_IS_FTT, Boolean.TRUE);
	}
	
	public static void saveLoginName(Context context, String name) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		sp.edit().putString(PREFERENCE_ITEM_LOGIN_NAME, name).commit();
	}

	public static void resetLoginName(Context context) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		sp.edit().remove(PREFERENCE_ITEM_LOGIN_NAME).commit();
	}

	public static String getLoginName(Context context) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		return sp.getString(PREFERENCE_ITEM_LOGIN_NAME, null);
	}
	
	public static void saveGesturePwd(Context context, String loginName, String gesturePwd) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		sp.edit().putString(PREFERENCE_ITEM_GESTURE_PWD + loginName, gesturePwd).commit();
	}

	public static boolean isInitGesturePwd(Context context, String loginName) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		return !"".equals(sp.getString(PREFERENCE_ITEM_GESTURE_PWD + loginName, ""));
	}

	public static boolean checkGesturePwd(Context context, String loginName, String gesturePwd) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		if (gesturePwd != null && !"".equals(gesturePwd)) {
			return gesturePwd.equals(sp.getString(PREFERENCE_ITEM_GESTURE_PWD + loginName, ""));
		}
		return false;
	}

	public static void resetGesturePwd(Context context, String loginName) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		sp.edit().remove(PREFERENCE_ITEM_GESTURE_PWD + loginName).commit();
	}

	public static void enableGesturePwd(Context context, String loginName) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		sp.edit().putBoolean(PREFERENCE_ITEM_GESTURE_PWD_ENABLE + loginName, true).commit();
	}
	
	public static void disableGesturePwd(Context context, String loginName) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		sp.edit().putBoolean(PREFERENCE_ITEM_GESTURE_PWD_ENABLE + loginName, false).commit();
	}

	public static boolean isGesturePwdEnable(Context context, String loginName) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		return sp.getBoolean(PREFERENCE_ITEM_GESTURE_PWD_ENABLE + loginName, false);
	}
	
	public static void enableMessageNotification(Context context, String loginName) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		sp.edit().putBoolean(PREFERENCE_ITEM_SYSTEM_NOTIFICATION + loginName, true).commit();
	}
	
	public static void disableMessageNotification(Context context, String loginName) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		sp.edit().putBoolean(PREFERENCE_ITEM_SYSTEM_NOTIFICATION + loginName, false).commit();
	}

	public static boolean isMessageNotification(Context context, String loginName) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		return sp.getBoolean(PREFERENCE_ITEM_SYSTEM_NOTIFICATION + loginName, true);
	}

	public static void enableNotificationSound(Context context, String loginName) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		sp.edit().putBoolean(PREFERENCE_ITEM_NOTIFICATION_SOUND + loginName, true).commit();
	}
	
	public static void disableNotificationSound(Context context, String loginName) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		sp.edit().putBoolean(PREFERENCE_ITEM_NOTIFICATION_SOUND + loginName, false).commit();
	}

	public static boolean isNotificationSound(Context context, String loginName) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		return sp.getBoolean(PREFERENCE_ITEM_NOTIFICATION_SOUND + loginName, true);
	}

	public static void enableNotificationShake(Context context, String loginName) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		sp.edit().putBoolean(PREFERENCE_ITEM_NOTIFICATION_SHAKE + loginName, true).commit();
	}
	
	public static void disableNotificationShake(Context context, String loginName) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		sp.edit().putBoolean(PREFERENCE_ITEM_NOTIFICATION_SHAKE + loginName, false).commit();
	}

	public static boolean isNotificationShake(Context context, String loginName) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		return sp.getBoolean(PREFERENCE_ITEM_NOTIFICATION_SHAKE + loginName, true);
	}

	public static void enableMailNotification(Context context, String loginName) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		sp.edit().putBoolean(PREFERENCE_ITEM_MAIL_NOTIFICATION + loginName, true).commit();
	}
	
	public static void disableMailNotification(Context context, String loginName) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		sp.edit().putBoolean(PREFERENCE_ITEM_MAIL_NOTIFICATION + loginName, false).commit();
	}

	public static boolean isMailNotification(Context context, String loginName) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		return sp.getBoolean(PREFERENCE_ITEM_MAIL_NOTIFICATION + loginName, true);
	}
	
	public static void saveCurrentDisturbType(Context context, String loginName, int disturb) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		sp.edit().putInt(PREFERENCE_ITEM_DISTURB + loginName, disturb).commit();
	}

	public static int getCurrentDisturbType(Context context, String loginName) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		return sp.getInt(PREFERENCE_ITEM_DISTURB + loginName, AppConstants.SYSTEM_SETTING_DISTURB_NONE);
	}
	
	public static void saveUpgradeMark(Context context) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		sp.edit().putBoolean(PREFERENCE_ITEM_UPGRADE_MARK, true).commit();
	}

	public static void clearUpgradeMark(Context context) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		sp.edit().remove(PREFERENCE_ITEM_UPGRADE_MARK).commit();
	}

	public static boolean checkUpgradeMark(Context context) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		return sp.getBoolean(PREFERENCE_ITEM_UPGRADE_MARK, false);
	}
	
	public static void clearAllPreference(Context context){
		context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE).edit().clear().commit();
	}
	
	public static void  setGesturePwdViewEnable(Context context, boolean enableView) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		sp.edit().putBoolean(PREFERENCE_ITEM_GESTURE_PWD_VIEW, enableView).commit();
	}

	public static boolean isgesturePwdViewEnabled(Context context) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		return sp.getBoolean(PREFERENCE_ITEM_GESTURE_PWD_VIEW , false);
	}

	public static void  setIsAtMeetingRoom(Context context, boolean isAtMeetingRoom) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		sp.edit().putBoolean(PREFERENCE_ITEM_IS_AT_MEETING, isAtMeetingRoom).commit();
	}

	public static boolean isAtMeetingRoom(Context context) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		return sp.getBoolean(PREFERENCE_ITEM_IS_AT_MEETING, false);
	}

	public static void setStartVideoTime(Context context, int time) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		sp.edit().putInt(PREFERENCE_ITEM_TIME_START_VIDEO_REQUEST, time).commit();
	}

	public static int getStartVideoTime(Context context) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SYSTEM, Context.MODE_PRIVATE);
		return sp.getInt(PREFERENCE_ITEM_TIME_START_VIDEO_REQUEST,-1);
	}
}
