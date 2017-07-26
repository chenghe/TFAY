package com.minxing.client;

import android.os.Environment;

import com.minxing.client.util.ImageFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class AppConstants {
	public static final String IMAGE_ENGINE_CACHE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/minxing/image";
	public static final int IMAGE_ENGINE_CORETHREAD = 5;
	public static final int IMAGE_ENGINE_FREQ_LIMITED_MEMECACHE = 2 * 1024 * 1024;
	public static final ImageLoadingListener animateFirstListener = new ImageFirstDisplayListener();

	public static DisplayImageOptions USER_AVATAR_OPTIONS = new DisplayImageOptions.Builder() // 圆角边处理的头像
			.cacheInMemory(true) // 缓存到内存，设置则缓存到内存
			.cacheOnDisk(true) // 缓存到本地磁盘,设置则缓存到磁盘
			.showImageForEmptyUri(R.drawable.default_icon_avatar) // 设置尚未加载，或者无图片的默认图片
			.showImageOnFail(R.drawable.default_icon_avatar) // 设置加载图片失败时的默认图片
			.build();

	public static DisplayImageOptions DEFAULT_IMAGE_OPTIONS = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();

	public static final int SYSTEM_SETTING_DISTURB_NONE = 0;
	public static final int SYSTEM_SETTING_DISTURB_NIGHT = 1;
	public static final int SYSTEM_SETTING_DISTURB_ALLDAY = 2;

	public static final String SYSTEM_START_TYPE_APP2APP = "start_type_app2app";
	public static final String SYSTEM_APP2APP_TYPE = "app2app_data_type";

	public static final int SYSTEM_APP2APP_TYPE_SHARE_TEXT = 0;
	public static final int SYSTEM_APP2APP_TYPE_SHARE_SINGLE_IMAGE = 1;
	public static final int SYSTEM_APP2APP_TYPE_SHARE_MULTI_IMAGE = 2;
	public static final int SYSTEM_APP2APP_TYPE_MAILTO = 3;
	public static final int SYSTEM_APP2APP_TYPE_SHARE_TEXT_CIRCLE = 4;
	public static final int SYSTEM_APP2APP_TYPE_SHARE_SINGLE_IMAGE_CIRCLE = 5;
	public static final int SYSTEM_APP2APP_TYPE_SHARE_MULTI_IMAGE_CIRCLE = 6;
	public static final int SYSTEM_APP2APP_TYPE_START_CHAT = 7;
	public static final int SYSTEM_APP2APP_TYPE_TAB_SHEET = 8;
	
	public static final String SYSTEM_APP2APP_TYPE_TAB_SHEET_VALUE = "app2app_tab_sheet";

	public static final String SAVE_UPGRADE_FILENAME = "patcher.patch";
	public static final int UPGRADE_NOTIFICATION_ID = 998877;
	public static final String MXCLIENT_REFRESH_NEW_VERSION = "com.minxing.client.refrsh.new.version";
	public static final String MXCLIENT_UPGRADE_INFO = "com.minxing.client.upgrade.info";
	public static final String MXCLIENT_HAVE_UNREAD = "com.minxing.client.unread.info";

	public static final String MXCLIENT_MODE_VIDEO_ACCEPT = "com.minxing.client.push.video";
	public static final String CALL_RECEIVE_DATA = "call_receive_data";

	public static DisplayImageOptions NO_CACHE_OPTIONS = new DisplayImageOptions.Builder().cacheInMemory(false).cacheOnDisk(false).build();
}
