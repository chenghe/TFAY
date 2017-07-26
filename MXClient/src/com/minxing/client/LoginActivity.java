package com.minxing.client;

import java.util.List;

import com.minxing.client.activity.SystemDiagnosisActivity;
import com.minxing.client.service.UpgradeService;
import com.minxing.client.service.ViewCallBack;
import com.minxing.client.util.CacheManager;
import com.minxing.client.util.NotificationUtil;
import com.minxing.client.util.PreferenceUtils;
import com.minxing.client.util.ResourceUtil;
import com.minxing.client.util.Utils;
import com.minxing.kit.MXKit;
import com.minxing.kit.MXKit.MXKitLoginListener;
import com.minxing.kit.api.MXAPI;
import com.minxing.kit.api.bean.MXError;
import com.minxing.kit.api.callback.MXApiCallback;
import com.minxing.kit.ui.widget.MXVariableTextView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;


public class LoginActivity extends RootActivity implements View.OnLayoutChangeListener{

	private LinearLayout username_password_login_container = null;
	private EditText usernameEditView = null;
	private EditText passwordEditView = null;

	private LinearLayout phone_sms_login_container = null;
	private EditText phone_number = null;
	private EditText sms_password = null;
	private Button request_sms_btn = null;

	private ProgressDialog progressDialog = null;
	private Button loginBtn = null;
	private TextView reset_password_btn = null;
	private View contentView = null;

	private boolean isCounting = false;
	private int countSecond = 0;
	private Handler handler = null;

	private static final int COUNTING_MAX_DURATION = 120;

    private MXVariableTextView ip_config_change;
    private int adjustPan;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHandleStatusColor(false);
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		contentView = LayoutInflater.from(this).inflate(R.layout.system_login, null);
		NotificationUtil.clearAllNotification(getApplicationContext());
		setContentView(contentView);
		handler = new CountingHandler();
		username_password_login_container = (LinearLayout) findViewById(R.id.username_password_login_container);
		phone_sms_login_container = (LinearLayout) findViewById(R.id.phone_sms_login_container);
		ip_config_change = (MXVariableTextView) findViewById(R.id.ip_config_change);
        ip_config_change.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG);
		loginBtn = (Button) findViewById(R.id.login_btn);
		loginBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				login();
			}
		});

		if (ResourceUtil.getConfBoolean(this, "client_sms_password_login_enable")) {
			username_password_login_container.setVisibility(View.GONE);
			phone_sms_login_container.setVisibility(View.VISIBLE);

			phone_number = (EditText) findViewById(R.id.phone_number);
			sms_password = (EditText) findViewById(R.id.sms_password);
			request_sms_btn = (Button) findViewById(R.id.request_sms_btn);

			String preLoginName = PreferenceUtils.getLoginName(this);
			if (preLoginName != null && !"".equals(preLoginName)) {
				phone_number.setText(preLoginName);
				sms_password.requestFocus();
			}

			phone_number.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence value, int start, int before, int count) {
					if (value.toString().trim().length() == 0) {
						request_sms_btn.setEnabled(false);
						loginBtn.setEnabled(false);
					} else {
						if (!isCounting) {
							request_sms_btn.setEnabled(true);
						}

						if (sms_password.getText().toString().trim().length() > 0) {
							loginBtn.setEnabled(true);
						}
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
				}
			});

			sms_password.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence value, int start, int before, int count) {
					if (value.toString().trim().length() == 0) {
						loginBtn.setEnabled(false);
					} else {
						if (phone_number.getText().toString().trim().length() > 0) {
							loginBtn.setEnabled(true);
						}
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
				}
			});

			request_sms_btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String check = phone_number.getText().toString();

					if (Utils.checkMobileNumber(check)) {
						requestSMSCode();
					} else {
						Toast.makeText(LoginActivity.this, R.string.sms_login_wrong_cell, Toast.LENGTH_SHORT).show();
					}
				}
			});

		} else {
			username_password_login_container.setVisibility(View.VISIBLE);
			phone_sms_login_container.setVisibility(View.GONE);

			usernameEditView = (EditText) findViewById(R.id.username);
			passwordEditView = (EditText) findViewById(R.id.password);

			String preLoginName = PreferenceUtils.getLoginName(this);
			if (preLoginName != null && !"".equals(preLoginName)) {
				usernameEditView.setText(preLoginName);
				passwordEditView.requestFocus();
			}

			usernameEditView.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence value, int start, int before, int count) {
					if (value.toString().trim().length() == 0) {
						loginBtn.setEnabled(false);
					} else {
						if (passwordEditView.getText().toString().trim().length() > 0) {
							loginBtn.setEnabled(true);
						}
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
				}
			});

			passwordEditView.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence value, int start, int before, int count) {
					if (value.toString().trim().length() == 0) {
						loginBtn.setEnabled(false);
					} else {
						if (usernameEditView.getText().toString().trim().length() > 0) {
							loginBtn.setEnabled(true);
						}
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
				}
			});

			passwordEditView.setOnEditorActionListener(new OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_DONE) {
						login();
						return true;
					}
					return false;
				}
			});

			reset_password_btn = (TextView) findViewById(R.id.reset_password_btn);
			final String resrtUrl = ResourceUtil.getConfString(getApplicationContext(), "client_login_reset_password_url");
			if (!TextUtils.isEmpty(resrtUrl)) {
				reset_password_btn.setVisibility(View.VISIBLE);
				reset_password_btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						MXAPI.getInstance(LoginActivity.this).resetPassword(LoginActivity.this, resrtUrl);
					}
				});
			} else {
				reset_password_btn.setVisibility(View.GONE);
			}
		}

		if (ResourceUtil.getConfBoolean(this, "mx_config_changeip", false)){
            ip_config_change.setVisibility(View.VISIBLE);
            ip_config_change.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, SystemDiagnosisActivity.class);
                    startActivity(intent);
                }
            });
		} else {
            ip_config_change.setVisibility(View.GONE);
		}
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        adjustPan = 1 / 3 * (dm.heightPixels);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
        contentView.addOnLayoutChangeListener(this);
		String errorMessage = getIntent().getStringExtra("error_message");
		if (errorMessage != null && !"".equals(errorMessage)) {
			Utils.toast(this, errorMessage, Toast.LENGTH_SHORT);
			getIntent().removeExtra("error_message");
		}
		/*int windowHeight = WindowUtils.getScreenHeight(LoginActivity.this);
		int windowWidth = WindowUtils.getScreenWidth(LoginActivity.this);*/
		super.onResume();
	}


	@Override
	protected void onStop() {
		super.onStop();
	}

	private void requestSMSCode() {
		if (getCurrentFocus() != null) {
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}

		final String mobile = phone_number.getText().toString();

		if (mobile.trim().length() == 0) {
			return;
		}

		progressDialog = ProgressDialog.show(LoginActivity.this, getString(R.string.alert), getString(R.string.login_get_autocode), true, false);
		MXAPI.getInstance(this).requestSMSVerifyCode(mobile, new MXApiCallback() {

			@Override
			public void onSuccess() {
				if (!isCounting) {
					countSecond = 0;
					isCounting = true;
					handler.sendEmptyMessage(0);
				}
				progressDialog.dismiss();
			}

			@Override
			public void onLoading() {

			}

			@Override
			public void onFail(MXError error) {
				progressDialog.dismiss();
			}
		});
	}

	private void login() {
		if (getCurrentFocus() != null) {
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}

		if (ResourceUtil.getConfBoolean(this, "client_sms_password_login_enable")) {
			final String phoneString = phone_number.getText().toString();
			final String smsString = sms_password.getText().toString();

			if (handleDiagnosisEvent(phoneString)) {
				return;
			}

			if (phoneString.trim().length() == 0 || smsString.trim().length() == 0) {
				return;
			}

			progressDialog = ProgressDialog.show(LoginActivity.this, getString(R.string.alert), getString(R.string.login_loading), true, false);
			MXKit.getInstance().loginMXKitWithSMSCode(this, phoneString, smsString, new MXKitLoginListener() {
				@Override
				public void onSuccess() {
					// 登陆成功后逻辑
					PreferenceUtils.saveLoginName(LoginActivity.this, phoneString);
					launchApp();
					String clientId = ResourceUtil.getConfString(LoginActivity.this, "client_app_client_id");
					new UpgradeService().checkUpgrade(LoginActivity.this, clientId, ResourceUtil.getVerCode(LoginActivity.this), true, new ViewCallBack(
							LoginActivity.this) {
					});
					progressDialog.dismiss();
				}

				@Override
				public void onFail(MXError error) {
					// 登陆失败逻辑
					if (error != null) {
						Utils.toast(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT);
					}
					progressDialog.dismiss();
				}

				// 显示短信验证页
				@Override
				public void onSMSVerify() {
					progressDialog.dismiss();
				}
			});
		} else {
			final String usernameString = usernameEditView.getText().toString();
			final String passwordString = passwordEditView.getText().toString();

			if (handleDiagnosisEvent(usernameString)) {
				return;
			}

			if (usernameString.trim().length() == 0 || passwordString.trim().length() == 0) {
				return;
			}

			progressDialog = ProgressDialog.show(LoginActivity.this, getString(R.string.alert), getString(R.string.login_loading), true, false);

			MXKit.getInstance().loginMXKit(this, usernameString, passwordString, new MXKitLoginListener() {
				@Override
				public void onSuccess() {
					// 登陆成功后逻辑
					PreferenceUtils.saveLoginName(LoginActivity.this, usernameString);
					launchApp();
					String clientId = ResourceUtil.getConfString(LoginActivity.this, "client_app_client_id");
					new UpgradeService().checkUpgrade(LoginActivity.this, clientId, ResourceUtil.getVerCode(LoginActivity.this), true, new ViewCallBack(
							LoginActivity.this) {
					});
					progressDialog.dismiss();
				}

				@Override
				public void onFail(MXError error) {
					// 登陆失败逻辑
					if (error != null) {
						Utils.toast(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT);
					}
					progressDialog.dismiss();
				}

				// 显示短信验证页
				@Override
				public void onSMSVerify() {
					progressDialog.dismiss();
				}
			});
		}
	}

	private boolean handleDiagnosisEvent(String eventString) {
		if (eventString != null && !"".equals(eventString.trim()) && "getconfig".equals(eventString.trim())) {
			Intent intent = new Intent(this, SystemDiagnosisActivity.class);
			startActivity(intent);
			return true;
		}
		return false;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		super.onNewIntent(intent);
	}

	@SuppressWarnings("unchecked")
	private void launchApp() {
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

			if (app2appType != AppConstants.SYSTEM_APP2APP_TYPE_START_CHAT) {
				finish();
			}
		} else {
			Intent intent = new Intent(this, ClientTabActivity.class);
			startActivity(intent);
			finish();
		}
	}

	private void refreshPageData() {
		int leftSecond = COUNTING_MAX_DURATION - countSecond;
		if (leftSecond >= 0) {
			if (leftSecond == 0) {
				isCounting = false;
				countSecond = 0;
				request_sms_btn.setEnabled(true);
				request_sms_btn.setText(R.string.request_sms_password);
			} else {
				request_sms_btn.setEnabled(false);
				request_sms_btn.setText(String.format(getString(R.string.wait_for_next_sms_password_request), leftSecond));
			}
		}
	}

	private class CountingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {

			if (msg.what == 0) {
				if (isCounting) {
					countSecond++;
					refreshPageData();
					if (handler != null) {
						handler.sendEmptyMessageDelayed(0, 1000);
					}
				}
			}
		}
	}

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > adjustPan)) {
            ip_config_change.setVisibility(View.GONE);
        } else {
            if (ResourceUtil.getConfBoolean(this, "mx_config_changeip", false)) {
                ip_config_change.setVisibility(View.VISIBLE);
            }
        }
    }
}
