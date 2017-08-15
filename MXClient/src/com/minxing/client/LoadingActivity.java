package com.minxing.client;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.minxing.client.activity.AdActivity;
import com.minxing.client.activity.GesturePasswordActivity;
import com.minxing.client.activity.SystemNewsActivity;
import com.minxing.client.service.UpgradeService;
import com.minxing.client.service.ViewCallBack;
import com.minxing.client.util.BackgroundDetector;
import com.minxing.client.util.BadgeUtil;
import com.minxing.client.util.PreferenceUtils;
import com.minxing.client.util.ResourceUtil;
import com.minxing.client.util.Utils;
import com.minxing.kit.MXKit;
import com.minxing.kit.MXKit.MXKitLoginListener;
import com.minxing.kit.MXKit.MXKitLogoutListener;
import com.minxing.kit.MXKit.MXKitPrepareResourceListener;
import com.minxing.kit.api.MXAPI;
import com.minxing.kit.api.bean.MXCurrentUser;
import com.minxing.kit.api.bean.MXError;
import com.umeng.analytics.MobclickAgent;

public class LoadingActivity extends RootActivity {

	public static final String LAUNCH_TYPE_SSO_LOGIN = "sso_login";
	public static final String SSO_LOGIN_USERNAME_KEY = "sso_username";
	public static final String SSO_LOGIN_PASSWORD_KEY = "sso_password";

	private int resultCode = RESULT_CANCELED;
	private ProgressDialog mProgressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		if(!isTaskRoot()) {
			super.onCreate(savedInstanceState);
		    finish();
		    return;
		} 
		setHandleStatusColor(false);
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.system_loading);
		BackgroundDetector.getInstance().init(this);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				MobclickAgent.updateOnlineConfig(LoadingActivity.this);
				boolean sdcardAvailable = Utils.sdcardAvailable();
				if (!sdcardAvailable) {
					Utils.toast(LoadingActivity.this, R.string.init_error_no_sdcard, Toast.LENGTH_SHORT);
					return;
				}

				MXKit.getInstance().prepareResource(LoadingActivity.this, PreferenceUtils.isAPPFTT(getApplicationContext()),
						new MXKitPrepareResourceListener() {

							@Override
							public void onComplete() {
								runOnUiThread(new Runnable() {
									public void run() {
										if (mProgressDialog != null && mProgressDialog.isShowing()) {
											mProgressDialog.dismiss();
											mProgressDialog = null;
										}
									}
								});
								new Handler(LoadingActivity.this.getMainLooper()).post(new Runnable() {

									@Override
									public void run() {
										loginMXKit();
									}
								});
							}

							@Override
							public void onProcessing(final String message) {
								runOnUiThread(new Runnable() {
									public void run() {
										mProgressDialog = ProgressDialog.show(LoadingActivity.this, getString(R.string.migrate_db_alert_title),
												message);
									}
								});
							}

							@Override
							public void onFail() {
								runOnUiThread(new Runnable() {
									public void run() {
										if (mProgressDialog != null && mProgressDialog.isShowing()) {
											mProgressDialog.dismiss();
											mProgressDialog = null;
										}
									}
								});
								new Handler(LoadingActivity.this.getMainLooper()).post(new Runnable() {
									@Override
									public void run() {
										loginMXKit();
									}
								});
								// 拷贝资源失败
							}
						});
			}
		}, 1000);
	}

	private void loginMXKit() {
		boolean isSSOLogin = getIntent().getBooleanExtra(LAUNCH_TYPE_SSO_LOGIN, false);
		if (isSSOLogin) {
			MXCurrentUser currentUser = MXAPI.getInstance(this).currentUser();
			if (currentUser != null) {
				MXKit.getInstance().logout(this, new MXKitLogoutListener() {
					@Override
					public void onLogout() {
						// Intent finishIntent = new
						// Intent(LoadingActivity.this,
						// ClientTabActivity.class);
						// finishIntent.setAction("finish");
						// finishIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						// finishIntent.putExtra("need_login_page", false);
						// startActivity(finishIntent);
						loginWithSSO();
						BadgeUtil.resetBadgeCount(LoadingActivity.this);
					}
				});
			} else {
				loginWithSSO();
			}
		} else {
			MXKit.getInstance().loginMXKitWithToken(this, new MXKitLoginListener() {

				@Override
				public void onSuccess() {
					launchApp();
					String clientId = ResourceUtil.getConfString(LoadingActivity.this, "client_app_client_id");
					new UpgradeService().checkUpgrade(LoadingActivity.this, clientId, ResourceUtil.getVerCode(LoadingActivity.this), true, new ViewCallBack(
							LoadingActivity.this) {
					});
				}

				@Override
				public void onFail(MXError error) {
					Intent intent = null;
					if (PreferenceUtils.isAPPFTT(LoadingActivity.this)) {
						intent = new Intent(LoadingActivity.this, SystemNewsActivity.class);
					} else {
						intent = new Intent(LoadingActivity.this, LoginActivity.class);
					}
					startActivity(intent);
					BadgeUtil.resetBadgeCount(LoadingActivity.this);
					finish();
				}

				@Override
				public void onSMSVerify() {
				}
			});
		}
	}

	private void loginWithSSO() {
		String ssoUsername = getIntent().getStringExtra(SSO_LOGIN_USERNAME_KEY);
		String ssoPassword = getIntent().getStringExtra(SSO_LOGIN_PASSWORD_KEY);

		MXKit.getInstance().loginMXKit(this, ssoUsername, ssoPassword, new MXKitLoginListener() {
			@Override
			public void onSuccess() {
				// 登陆成功后逻辑
				resultCode = RESULT_OK;
				setResult(resultCode);
				finish();
				// new
				// UpgradeService().checkUpgrade(ResourceUtil.getVerCode(LoadingActivity.this),
				// true, new ViewCallBack(LoadingActivity.this) {
				// });
			}

			@Override
			public void onFail(MXError error) {
				// 登陆失败逻辑
				if (error != null) {
					Utils.toast(LoadingActivity.this, error.getMessage(), Toast.LENGTH_SHORT);
				}
			}

			@Override
			public void onSMSVerify() {
			}
		});
	}

	private void launchApp() {
		MXCurrentUser currentUser = MXAPI.getInstance(this).currentUser();
		if (currentUser != null) {
			String tabSortHide = ResourceUtil.getConfString(this, "client_sort_hide");
			if (TextUtils.isEmpty(tabSortHide) || (!TextUtils.isEmpty(tabSortHide) && tabSortHide.contains("circle"))) {
				Utils.setShareFromExternalBrowserEnable(this, true);
			} else {
				Utils.setShareFromExternalBrowserEnable(this, false);
			}
			Intent intent = null;
			if (PreferenceUtils.isGesturePwdEnable(this, currentUser.getLoginName())
					&& PreferenceUtils.isInitGesturePwd(this, currentUser.getLoginName())) {
				intent = new Intent(this, GesturePasswordActivity.class);
				intent.putExtra(GesturePasswordActivity.PWD_SCREEN_MODE_KEY, GesturePasswordActivity.PWD_SCREEN_MODE_FORCE);
				MXKit.getInstance().setStartGesturePsd(true);
			} else {
				intent = new Intent(this, ClientTabActivity.class);
			}
			startActivity(intent);
			finish();
		}
//		Intent intent = new Intent(this, AdActivity.class);
//		startActivity(intent);
	}
}
