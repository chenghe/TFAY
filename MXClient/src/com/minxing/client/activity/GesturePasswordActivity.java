package com.minxing.client.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.minxing.client.AppConstants;
import com.minxing.client.ClientTabActivity;
import com.minxing.client.R;
import com.minxing.client.RootActivity;
import com.minxing.client.upgrade.AppUpgradeInfo;
import com.minxing.client.util.BackgroundDetector;
import com.minxing.client.util.CacheManager;
import com.minxing.client.util.PreferenceUtils;
import com.minxing.client.util.Utils;
import com.minxing.client.widget.GesturePasswordResetPopMenu;
import com.minxing.client.widget.NinePointLineView;
import com.minxing.client.widget.RoundImageView;
import com.minxing.kit.MXConstants;
import com.minxing.kit.MXKit;
import com.minxing.kit.MXKit.MXKitLogoutListener;
import com.minxing.kit.api.MXAPI;
import com.minxing.kit.api.bean.MXCurrentUser;
import com.minxing.kit.api.bean.MXError;
import com.minxing.kit.api.callback.MXApiCallback;
import com.minxing.kit.internal.common.bean.appstore.AppInfo;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class GesturePasswordActivity extends RootActivity {

	private LinearLayout nine_icon;
	private NinePointLineView nv;
	private TextView showInfo;

	private TextView left_control;
	private TextView right_control;
	private LinearLayout userinfo_layout;
	private RoundImageView my_avatar;

	private GesturePwd_Type currentType = GesturePwd_Type.CHECK_PWD;

	public static String PWD_SCREEN_MODE_KEY = "PWD_SCREEN_MODE_KEY";
	public static int PWD_SCREEN_MODE_FORCE = 1;
	public static int PWD_SCREEN_MODE_FREE = 0;
	public static int PWD_SCREEN_MODE_BACKGROUND = 2;
	private int pwdScreenMode = 0;
	
	private static String TAG = "GesturePasswordActivity";
	private BroadcastReceiver receiver;

	public static enum GesturePwd_Type {
		CHECK_PWD, SET_FIRST, SET_SECOND, SET_SECOND_ERROR, CHECK_PWD_END, SET_PWD_END
	}

	private String firstPwd = "";
	private GesturePasswordResetPopMenu popMenu = null;
	private MXCurrentUser currentUser = null;
	private boolean isResetPassword = false;
	private boolean isCancelPassword = false;
	private AppUpgradeInfo upgradeInfo;
	private boolean haveUnread = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setHandleStatusColor(false);
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.system_gesture_pwd);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		pwdScreenMode = getIntent().getIntExtra(PWD_SCREEN_MODE_KEY, PWD_SCREEN_MODE_FREE);
		isResetPassword = getIntent().getBooleanExtra("is_reset_password", false);
		isCancelPassword = getIntent().getBooleanExtra("is_cancel_password", false);

		nv = new NinePointLineView(GesturePasswordActivity.this);
		nine_icon = (LinearLayout) findViewById(R.id.nine_icon);
		nine_icon.addView(nv);
		showInfo = (TextView) findViewById(R.id.textTip);

		left_control = (TextView) findViewById(R.id.left_control);
		right_control = (TextView) findViewById(R.id.right_control);
		userinfo_layout = (LinearLayout) findViewById(R.id.userinfo_layout);
		my_avatar = (RoundImageView) findViewById(R.id.my_avatar);
		initView();
		checkUpgradeInfo();
	}

	private void checkUpgradeInfo() {
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(AppConstants.MXCLIENT_REFRESH_NEW_VERSION)) {
					if (PreferenceUtils.checkUpgradeMark(GesturePasswordActivity.this)) {

						upgradeInfo = (AppUpgradeInfo) intent.getSerializableExtra(AppConstants.MXCLIENT_UPGRADE_INFO);
						
					} 
				}else if(intent.getAction().equals(MXConstants.BroadcastAction.MXKIT_REVOKE_DISPATCH_UNSEEN)){
					haveUnread = true;
				}
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(MXConstants.BroadcastAction.MXKIT_REVOKE_DISPATCH_UNSEEN);
		filter.addAction(AppConstants.MXCLIENT_REFRESH_NEW_VERSION);
		registerReceiver(receiver, filter, MXKit.getInstance().getAppSignaturePermission(), null);
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		nv.invalidate();

	}

	private void initView() {
		currentUser = MXAPI.getInstance(this).currentUser();
		if (currentUser == null) {
			finish();
			return;
		}
		if (PreferenceUtils.isInitGesturePwd(this, currentUser.getLoginName())) {
			this.currentType = GesturePwd_Type.CHECK_PWD;
		} else {
			this.currentType = GesturePwd_Type.SET_FIRST;
		}
		refreshView();
	}

	private void refreshView() {
		left_control.setVisibility(View.GONE);
		right_control.setVisibility(View.GONE);
//		showInfo.setVisibility(View.INVISIBLE);
		userinfo_layout.setVisibility(View.VISIBLE);
		if (currentUser != null) {
			// WBSysUtils.initImageEngine(this);
			ImageLoader.getInstance().displayImage(currentUser.getAvatarUrl(), my_avatar, AppConstants.USER_AVATAR_OPTIONS,
					AppConstants.animateFirstListener);
		}
		switch (currentType) {
		case CHECK_PWD:
			left_control.setVisibility(View.VISIBLE);
			left_control.setText(R.string.gesture_password_setting_forget);
			showInfo.setText(R.string.gesture_password_check);
			left_control.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					popMenu = new GesturePasswordResetPopMenu(GesturePasswordActivity.this);
					if (!popMenu.isShowing()) {
						popMenu.showAtLocation(findViewById(R.id.operation_layout), Gravity.BOTTOM, 0, 0);
					}
				}
			});
			
			if (isCancelPassword) {
				break;
			}
			
			if (pwdScreenMode != PWD_SCREEN_MODE_FORCE && pwdScreenMode != PWD_SCREEN_MODE_BACKGROUND) {
				right_control.setVisibility(View.GONE);
			} else {
				right_control.setVisibility(View.VISIBLE);
				right_control.setText(R.string.gesture_password_setting_login_changeuser);
				right_control.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						reset(true, false);
					}
				});
			}

			break;

		case SET_FIRST:
			showInfo.setVisibility(View.VISIBLE);
			showInfo.setText(R.string.gesture_password_setting_first);
			break;

		case SET_SECOND:
			showInfo.setVisibility(View.VISIBLE);
			showInfo.setText(R.string.gesture_password_setting_second);
			break;

		case SET_SECOND_ERROR:
			showInfo.setVisibility(View.VISIBLE);
			showInfo.setText(R.string.gesture_password_setting_secondwrong);
			left_control.setVisibility(View.VISIBLE);
			left_control.setText(R.string.gesture_password_setting_reset);
			left_control.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					firstPwd = "";
					nv.finishDraw();
					currentType = GesturePwd_Type.SET_FIRST;
					refreshView();
				}
			});
			break;

		case CHECK_PWD_END:
			if (isCancelPassword) {
				sendResultBack();
			} else {
				userinfo_layout.setVisibility(View.VISIBLE);
				startMainView();
			}
			break;

		case SET_PWD_END:
			showInfo.setVisibility(View.VISIBLE);
			showInfo.setText(R.string.gesture_password_setting_success);
			sendResultBack();
			Utils.toast(this, getString(R.string.gesture_password_setting_success), Toast.LENGTH_SHORT);
			break;
		}
	}
	
	private void sendResultBack() {
		Intent resultIntent = new Intent();
		setResult(RESULT_OK, resultIntent);
		finish();
	}

	@SuppressWarnings("unchecked")
	private void startMainView() {
		BackgroundDetector.getInstance().setPasswordCheckActive(false);
		MXKit.getInstance().setStartGesturePsd(false);
		
		boolean isApp2App = getIntent().getBooleanExtra(AppConstants.SYSTEM_START_TYPE_APP2APP, false);
		int app2appType = getIntent().getIntExtra(AppConstants.SYSTEM_APP2APP_TYPE, -1);

		if (isApp2App && app2appType != -1) {
			switch (app2appType) {
			case AppConstants.SYSTEM_APP2APP_TYPE_SHARE_TEXT:
				MXAPI.getInstance(this).shareTextToChat(this, (String) CacheManager.getInstance().getHoldedShareContent());
				break;

			case AppConstants.SYSTEM_APP2APP_TYPE_SHARE_SINGLE_IMAGE:
				MXAPI.getInstance(this).shareImageToChat(this, (Uri) CacheManager.getInstance().getHoldedShareContent());
				break;

			case AppConstants.SYSTEM_APP2APP_TYPE_SHARE_MULTI_IMAGE:
				MXAPI.getInstance(this).shareImagesToChat(this, (List<Uri>) CacheManager.getInstance().getHoldedShareContent());
				break;
			case AppConstants.SYSTEM_APP2APP_TYPE_MAILTO:
				MXAPI.getInstance(this).shareToMail(this, (Uri) CacheManager.getInstance().getHoldedShareContent());
				break;
			case AppConstants.SYSTEM_APP2APP_TYPE_SHARE_TEXT_CIRCLE:
				MXAPI.getInstance(this).shareTextToCircle(this, (String) CacheManager.getInstance().getHoldedShareContent());
				break;

			case AppConstants.SYSTEM_APP2APP_TYPE_SHARE_SINGLE_IMAGE_CIRCLE:
				MXAPI.getInstance(this).shareImageToCircle(this, (Uri) CacheManager.getInstance().getHoldedShareContent());
				break;

			case AppConstants.SYSTEM_APP2APP_TYPE_SHARE_MULTI_IMAGE_CIRCLE:
				MXAPI.getInstance(this).shareImagesToCircle(this, (List<Uri>) CacheManager.getInstance().getHoldedShareContent());
				break;
				
			case AppConstants.SYSTEM_APP2APP_TYPE_START_CHAT:
				String[] loginNames = (String[]) CacheManager.getInstance().getHoldedShareContent();
				
				MXAPI.getInstance(this).chat(loginNames, new MXApiCallback() {

					@Override
					public void onLoading() {
					}

					@Override
					public void onFail(MXError error) {
						Utils.toast(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT);
						finish();
					}

					@Override
					public void onSuccess() {
						finish();
					}
				});
				break;
				
			case AppConstants.SYSTEM_APP2APP_TYPE_TAB_SHEET:
				String tabSheet = (String) CacheManager.getInstance().getHoldedShareContent();
				Intent intent = new Intent(this, ClientTabActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra(AppConstants.SYSTEM_APP2APP_TYPE_TAB_SHEET_VALUE, tabSheet);
				startActivity(intent);
				break;
			}
			
			if(app2appType != AppConstants.SYSTEM_APP2APP_TYPE_START_CHAT){
				finish();
			}
		} else {
			Intent i  = new Intent(this, ClientTabActivity.class);
			if (upgradeInfo != null){
				i.putExtra(AppConstants.MXCLIENT_UPGRADE_INFO, upgradeInfo);
				i.putExtra(AppConstants.MXCLIENT_HAVE_UNREAD, haveUnread);
				
			}
			startActivity(i);
			finish();
		}
	}

	public void handleGesturePwd(String pwd) {
		switch (currentType) {
		case CHECK_PWD:
			if (PreferenceUtils.checkGesturePwd(this, currentUser.getLoginName(), pwd)) {
				if (isResetPassword) {
					currentType = GesturePwd_Type.SET_FIRST;
					firstPwd = "";
				} else {
					currentType = GesturePwd_Type.CHECK_PWD_END;
				}
			} else {
				Utils.toast(this, R.string.gesture_password_check_error, Toast.LENGTH_SHORT);
			}
			nv.finishDraw();
			refreshView();
			break;

		case SET_FIRST:
			if (pwd.length() < 3) {
				Utils.toast(this, R.string.gesture_password_setting_error, Toast.LENGTH_SHORT);
			} else {
				firstPwd = pwd;
				currentType = GesturePwd_Type.SET_SECOND;
				refreshView();
			}
			nv.finishDraw();
			break;

		case SET_SECOND:
			if (pwd.length() < 3) {
				Utils.toast(this, R.string.gesture_password_setting_error, Toast.LENGTH_SHORT);
			} else {
				if (pwd.equals(firstPwd)) {
					PreferenceUtils.saveGesturePwd(this, currentUser.getLoginName(), firstPwd);
					currentType = GesturePwd_Type.SET_PWD_END;
				} else {
					currentType = GesturePwd_Type.SET_SECOND_ERROR;
				}
			}
			nv.finishDraw();
			refreshView();
			break;

		case SET_SECOND_ERROR:
			if (pwd.length() < 3) {
				Utils.toast(this, R.string.gesture_password_setting_error, Toast.LENGTH_SHORT);
			} else {
				if (pwd.equals(firstPwd)) {
					PreferenceUtils.saveGesturePwd(this, currentUser.getLoginName(), firstPwd);
					currentType = GesturePwd_Type.SET_PWD_END;
				} else {
				}
			}
			nv.finishDraw();
			refreshView();
			break;
		default:
			Log.e(TAG, "unknown currentType:" + currentType);
			break;
		}
	}

	public void reset(boolean isChangeUser, boolean resetGesturePwd) {
		if (isChangeUser) {
			PreferenceUtils.resetLoginName(this);
		}
		if (resetGesturePwd) {
			PreferenceUtils.resetGesturePwd(this, currentUser.getLoginName());
			PreferenceUtils.disableGesturePwd(this, currentUser.getLoginName());
		}

		MXKit.getInstance().logout(this, new MXKitLogoutListener() {
			@Override
			public void onLogout() {
				Intent finishIntent = new Intent(GesturePasswordActivity.this, ClientTabActivity.class);
				finishIntent.setAction("finish");
				finishIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				finishIntent.putExtra(AppConstants.SYSTEM_START_TYPE_APP2APP,
						getIntent().getBooleanExtra(AppConstants.SYSTEM_START_TYPE_APP2APP, false));
				finishIntent.putExtra(AppConstants.SYSTEM_APP2APP_TYPE, getIntent().getIntExtra(AppConstants.SYSTEM_APP2APP_TYPE, -1));
				startActivity(finishIntent);
				finish();
			}
		});
	}


	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		super.onNewIntent(intent);
	}

	@Override
	public void onBackPressed() {
		if (pwdScreenMode == PWD_SCREEN_MODE_FORCE || pwdScreenMode == PWD_SCREEN_MODE_BACKGROUND) {
			return;
		}
		super.onBackPressed();
	}

	
	@Override
	protected void onDestroy() {
		if(receiver != null){
			unregisterReceiver(receiver);
		}
		super.onDestroy();
	}


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

	}
}
