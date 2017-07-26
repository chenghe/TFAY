package com.minxing.client;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.minxing.client.location.callback.ClientLocationProvider;
import com.minxing.client.notification.NotificationHolder;
import com.minxing.client.util.BackgroundDetector;
import com.minxing.client.util.ImageUtil;
import com.minxing.client.util.NotificationUtil;
import com.minxing.client.util.PreferenceUtils;
import com.minxing.client.util.ResourceUtil;
import com.minxing.client.util.Utils;
import com.minxing.kit.MXConstants;
import com.minxing.kit.MXKit;
import com.minxing.kit.MXKit.MXChatNotificationListener;
import com.minxing.kit.MXKit.MXKitLifecycleCallbacks;
import com.minxing.kit.MXKit.MXKitMasterClearListener;
import com.minxing.kit.MXKit.MXKitUserConflictListener;
import com.minxing.kit.MXKit.MXUIListener;
import com.minxing.kit.MXKitConfiguration;
import com.minxing.kit.MXUIEngine.ViewSwitchListener;
import com.minxing.kit.api.bean.ChatMessage;
import com.minxing.kit.api.bean.MXError;
import com.minxing.kit.mail.MXMail;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.https.HttpsUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class AppApplication extends MXApplication {

	public static String MX_LOGTAG = "MXLOG";
	private int mUserID;	//本地用户Id
	private static AppApplication sInstance;

	@Override
	public void onCreate() {
		Log.i(MX_LOGTAG, "[AppApplication][onCreate]");
		super.onCreate();
		MultiDex.install(this);
		//初始化Crash自动捕获错误日志
//		CrashHandler crashHandler=CrashHandler.getInstance();
//		crashHandler.init(this);

		//广告页 OKHttp初始化
		HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor("hcb"))
				.connectTimeout(10000L, TimeUnit.MILLISECONDS)
				.readTimeout(10000L, TimeUnit.MILLISECONDS)
				//https 配置 通用全部证书(https://github.com/hongyangAndroid/okhttputils)
//				.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
				//其他配置
				.build();

		OkHttpUtils.initClient(okHttpClient);


		NotificationUtil.clearAllNotification(getApplicationContext());



		MXKitConfiguration config = new MXKitConfiguration.Builder(getApplicationContext())
				// http server地址// 推送server地址
				.hostOptions(ResourceUtil.getConfString(getApplicationContext(), "client_conf_http_host"),
						ResourceUtil.getConfString(getApplicationContext(), "client_conf_push_host"))
				// SD卡目录
				.sdCardCacheFolder(ResourceUtil.getConfString(getApplicationContext(), "client_conf_sdcard_root"))
				// 设置是否在通讯录中显示公众号操作
				.contactOcu(ResourceUtil.getConfBoolean(getApplicationContext(), "client_show_contact_ocu"))
				// 设置是否在通讯录中显示公司通讯录操作。
				.contactCompany(ResourceUtil.getConfBoolean(getApplicationContext(), "client_show_contact_company"))
				// 设置是否在通讯录中显示群聊操作
				.contactMultiChat(ResourceUtil.getConfBoolean(getApplicationContext(), "client_show_multi_chat"))
				// 设置是否在通讯录中显示特别关注操作
				.contactVip(ResourceUtil.getConfBoolean(getApplicationContext(), "client_show_contact_vip"))
				// 设置手机号码隐藏规则
				.encryptCellphone(ResourceUtil.getConfString(getApplicationContext(), "client_encrypt_cellphone"))
				.appForceRefresh(ResourceUtil.getConfBoolean(getApplicationContext(), "client_app_center_force_refresh"))
				.appClientId(ResourceUtil.getConfString(getApplicationContext(), "client_app_client_id"))
				.waterMarkEnable(ResourceUtil.getConfBoolean(getApplicationContext(), "client_water_mark_enable"))
				.fileDownloadForbidden(ResourceUtil.getConfBoolean(getApplicationContext(), "file_download_forbidden"))
				.callShowDefalut(ResourceUtil.getConfBoolean(getApplicationContext(), "client_call_show_on"))
				.appCenterAddButton(ResourceUtil.getConfBoolean(getApplicationContext(), "app_center_add_button"))
				.syncPersonalContactAll(ResourceUtil.getConfBoolean(getApplicationContext(),"sync_personal_contact_all_from_server"))
				.hiddenPhoneNumber(ResourceUtil.getConfBoolean(getApplicationContext(), "cient_phone_hidden"))
				.circleShowAllLikePerson(ResourceUtil.getConfBoolean(getApplicationContext(),"client_show_circle_like_by_someone"))
				.setBannerShow(ResourceUtil.getConfBoolean(getApplicationContext(),"client_app_center_banner_show"))
				.setAppCenterAutoDownMaxSize(ResourceUtil.getConfLong(getApplicationContext(),"client_app_center_auto_down_max_size"))
				.setGalleryImageCompress(ResourceUtil.getConfInt(getApplicationContext(),"client_gallery_img_compress",0))
				.build();
		MXKit.getInstance().init(getApplicationContext(), config);
		MXKit.getInstance().setAvatarRoundPixels(0);//头像
		MXKit.getInstance().setAppIconRoundPixels(20);//应用中心

		ImageUtil.initImageEngine(getApplicationContext());
		MXKit.getInstance().setConflictListener(new MXKitUserConflictListener() {

			@Override
			public void onUserConflict(MXError error) {
				// 用户被踢出后处理逻辑
				Intent finishIntent = new Intent(getApplicationContext(), ClientTabActivity.class);
				finishIntent.putExtra("error_message", error.getMessage());
				finishIntent.setAction("finish");
				finishIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				getApplicationContext().startActivity(finishIntent);
			}
		});

		MXKit.getInstance().setMasterClearListener(new MXKitMasterClearListener() {

			@Override
			public void onKitMasterClear() {
				Utils.toast(getApplicationContext(), R.string.master_clear_alert, Toast.LENGTH_SHORT);

				PreferenceUtils.clearAllPreference(getApplicationContext());

				Intent finishIntent = new Intent(getApplicationContext(), ClientTabActivity.class);
				finishIntent.setAction("master_clear");
				finishIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				getApplicationContext().startActivity(finishIntent);
			}
		});

		MXKit.getInstance().setupNotification(new MXChatNotificationListener() {
			@Override
			public void onChatNotify(Context context, ChatMessage message, boolean conversationIsExist) {
				NotificationUtil.handleMessageNotification(context, message, false, conversationIsExist);
			}

			@Override
			public void dismissNotification(Context context, int chatID) {
				if (NotificationHolder.getInstance().checkChatMessage(chatID)) {
					NotificationUtil.clearAllNotification(context);
				}
			}

			@Override
			public void onMessageRevoked(Context context, ChatMessage revokedMessage) {
				if (NotificationHolder.getInstance().checkChatMessage(revokedMessage.getChatID())) {
					NotificationUtil.handleMessageNotification(context, revokedMessage, true, true);
				}
			}
		});

		// 用以返回主界面
		MXKit.getInstance().setMXUIListener(new MXUIListener() {

			@Override
			public void switchToMainScreen(Context context) {
				Intent intent = new Intent(context, ClientTabActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(intent);
			}

			@Override
			public void reLaunchApp(Context context) {
				Intent intent = new Intent(context, ClientTabActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				context.startActivity(intent);
			}
		});

		MXKit.getInstance().setViewSwitchListener(new ViewSwitchListener() {

			@Override
			public void switchToCircle(Context context, int groupID) {
				Intent intent = new Intent(context, ClientTabActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra(MXConstants.IntentKey.SHOW_CURRENT_GROUP_WORK_CIRCLE, groupID);
				context.startActivity(intent);
			}
		});
		MXKit.getInstance().setLifecycleCallbacks(new MXKitLifecycleCallbacks() {

			@Override
			public void onActivityStop(Activity activity) {
				
			}

			@Override
			public void onActivityStart(Activity activity) {
			}

			@Override
			public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
			}

			@Override
			public void onActivityResume(Activity activity) {
				// 友盟统计回调
				MobclickAgent.onResume(activity);
				BackgroundDetector.getInstance().setDetectorStop(false);
				//注意自定义事件不能写在onCreate中
				MXKit.getInstance().setCountEventsListener(new MXKit.MXKitCountEventsListener() {
					@Override
					public void getSendDuration(Context context, String eventId, Map<String, String> map_value, int duration) {
						MobclickAgent.onEventValue(context, eventId, map_value, duration);
					}

					@Override
					public void getClickNumber(Context context, String eventId, HashMap map) {
						MobclickAgent.onEvent(context, eventId,map);
					}
				});
			}

			@Override
			public void onActivityPause(Activity activity) {
				// 友盟统计回调
				MobclickAgent.onPause(activity);
			}

			@Override
			public void onActivityDestroy(Activity activity) {
//				UmsAgent.postClientData(activity);
			}

			@Override
			public void onActivityCreate(Activity activity, Bundle savedInstanceState) {
				Log.i("hcb","onActivityCreate");
				handleStatusBarColor(activity);
			}

			@Override
			public void onStartActivityForResult(Activity activity) {
				BackgroundDetector.getInstance().setDetectorStop(true);
				if (activity != null) {
					if (activity.getParent() != null) {
						activity.getParent().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
					} else {
						activity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
					}
				}
			}

			@Override
			public void onStartActivity(Activity activity) {
				if (activity != null) {
					if (activity.getParent() != null) {
						activity.getParent().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
					} else {
						activity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
					}
				}
			}

			@Override
			public void onActivityFinish(Activity activity) {
				if (activity != null) {
					activity.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
				}
			}
		});
		//初始化前后台探测器
		MXKit.getInstance().initForeBackgroundDetector(this);
		
		MXMail.getInstance().init(this);

		MXKit.getInstance().setLocationProvider(new ClientLocationProvider(this));
	}
//	public static AppApplication getInstance() {
//		return sInstance;
//	}
	
	@TargetApi(21/*Build.VERSION_CODES.LOLLIPOP*/)
	private void handleStatusBarColor(Activity activity) {
		Log.i("hcb","handleStatusBarColor");
		Log.i("hcb","Build.VERSION.SDK_INT" +Build.VERSION.SDK_INT+"");
		if (Build.VERSION.SDK_INT >= 21/* Build.VERSION_CODES.LOLLIPOP */){
			Log.i("hcb","Build.VERSION.SDK_INT >= 21");
			Window window = activity.getWindow();
			// clear FLAG_TRANSLUCENT_STATUS flag:
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			// finally change the color
			window.setStatusBarColor(activity.getResources().getColor(R.color.status_bar_color));
		}
	}

	public void setUserID(int sUserID)
	{
		mUserID = sUserID;
	}

	public int getUserID()
	{
		return mUserID;
	}

}
