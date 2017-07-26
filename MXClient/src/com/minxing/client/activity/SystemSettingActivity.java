package com.minxing.client.activity;

import com.minxing.client.ClientTabActivity;
import com.minxing.client.R;
import com.minxing.client.RootActivity;
import com.minxing.client.service.UpgradeService;
import com.minxing.client.service.ViewCallBack;
import com.minxing.client.upgrade.AppUpgradeInfo;
import com.minxing.client.util.ResourceUtil;
import com.minxing.client.util.Utils;
import com.minxing.kit.MXKit;
import com.minxing.kit.MXKit.MXKitLogoutListener;
import com.minxing.kit.api.MXAPI;
import com.minxing.kit.internal.common.UICustomActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SystemSettingActivity extends RootActivity implements OnClickListener{

	private TextView newflag = null;
	private TextView upgrade_desc = null;
	private String upgrade_url = null;

	private LinearLayout mx_ui_custom;
	private LinearLayout open_horizontal_screen = null;
	private LinearLayout message_notification = null;
	private LinearLayout gesture_password = null;
	private LinearLayout change_password = null;
	private LinearLayout upgrade = null;
	private LinearLayout exit_btn_layout = null;
	private LinearLayout mobil_contacts = null;
	private LinearLayout function_introdution = null;

	private ImageButton backButton = null;
	private AppUpgradeInfo upgradeInfo = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		init();

		if (ResourceUtil.getConfBoolean(this, "client_sms_password_login_enable")
				|| !ResourceUtil.getConfBoolean(this, "client_enable_reset_password", true)) {
			change_password.setVisibility(View.GONE);
		} else {
			change_password.setVisibility(View.VISIBLE);
			change_password.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					MXAPI.getInstance(SystemSettingActivity.this).changePassword(SystemSettingActivity.this);
				}
			});
		}

		if (Utils.checkConnectState(this)) {
			String clientId = ResourceUtil.getConfString(SystemSettingActivity.this, "client_app_client_id");
			new UpgradeService().checkUpgrade(SystemSettingActivity.this, clientId, ResourceUtil.getVerCode(this), false, new ViewCallBack(this) {
				@Override
				public void success(Object obj) {
					upgradeInfo = (AppUpgradeInfo) obj;
					if (!upgradeInfo.isNeedUpgrade()) {
						return;
					}
					upgrade_url = upgradeInfo.getUpgrade_url();
					if (upgrade_url == null || "".equals(upgrade_url)) {
						return;
					}
					newflag.setTag(upgrade_url);
					newflag.setVisibility(View.VISIBLE);
				}
			});
		}

		// 链接地址为 baseUrl+/introduces/index.html?v=5.0.0 最后拼当前版本号。
		if (ResourceUtil.getConfBoolean(getApplicationContext(), "client_show_introduction")) {
			function_introdution.setVisibility(View.VISIBLE);
			function_introdution.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String url = ResourceUtil.getConfString(SystemSettingActivity.this, "client_conf_http_host") + "/introduces/index.html?v="
							+ ResourceUtil.getVerCode(SystemSettingActivity.this);
					Intent intent = new Intent(SystemSettingActivity.this, SystemFunctionIntrodutionActivity.class);
					intent.putExtra("introdution_url", url);
					startActivity(intent);
				}
			});
		}

	}
	private void init(){
		setContentView(R.layout.system_setting);
		((TextView) findViewById(R.id.title_name)).setText(R.string.title_setting);
		backButton = (ImageButton) findViewById(R.id.title_left_button);

		mx_ui_custom = (LinearLayout) findViewById(R.id.mx_ui_custom);
		exit_btn_layout = (LinearLayout) findViewById(R.id.exit_btn_layout);
		upgrade_desc = (TextView) this.findViewById(R.id.upgrade_desc);
		upgrade_desc.setText(String.format(getString(R.string.version_upgrade), ResourceUtil.getVerName(this)));

		newflag = (TextView) this.findViewById(R.id.newflag);

		open_horizontal_screen = (LinearLayout) findViewById(R.id.open_horizontal_screen);
		message_notification = (LinearLayout) findViewById(R.id.message_notification);

		gesture_password = (LinearLayout) findViewById(R.id.gesture_password);

		upgrade = (LinearLayout) findViewById(R.id.upgrade);
		mobil_contacts = (LinearLayout) findViewById(R.id.mobil_contacts);

		function_introdution = (LinearLayout) findViewById(R.id.function_introdution);
		change_password = (LinearLayout) findViewById(R.id.change_password);

		backButton.setOnClickListener(this);
		exit_btn_layout.setOnClickListener(this);
		gesture_password.setOnClickListener(this);
		message_notification.setOnClickListener(this);
		upgrade.setOnClickListener(this);
		mobil_contacts.setOnClickListener(this);
		mx_ui_custom.setOnClickListener(this);
		open_horizontal_screen.setOnClickListener(this);

	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finishWithAnimation();
		}
		return false;
	}

	public void upgrade() {
		if (!Utils.checkConnectState(this)) {
			Utils.toast(this, R.string.upgrade_network_disable, Toast.LENGTH_SHORT);
			return;
		}

		if (newflag.getVisibility() == View.GONE) {
			Utils.toast(this, R.string.upgrade_no_new_version, Toast.LENGTH_SHORT);
			return;
		}
		Utils.showUpgradeDialog(this, upgradeInfo);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()){
			case R.id.title_left_button:
				finishWithAnimation();
				break;
			case R.id.exit_btn_layout:
				Utils.showSystemDialog(SystemSettingActivity.this, getResources().getString(R.string.system_tip), SystemSettingActivity.this
						.getResources().getString(R.string.warning_logout), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						MXKit.getInstance().logout(SystemSettingActivity.this, new MXKitLogoutListener() {
							@Override
							public void onLogout() {
								Intent finishIntent = new Intent(SystemSettingActivity.this, ClientTabActivity.class);
								finishIntent.setAction("finish");
								finishIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(finishIntent);
							}
						});
					}
				}, null, true);
				break;
			case R.id.gesture_password:
				intent = new Intent(SystemSettingActivity.this, SystemSettingGesturePasswordActivity.class);
				startActivity(intent);
				break;
			case R.id.message_notification:
				intent = new Intent(SystemSettingActivity.this, SystemSettingMessageNotificationActivity.class);
				startActivity(intent);
				break;
			case R.id.upgrade:
				upgrade();
				break;
			case R.id.mobil_contacts:
				MXAPI.getInstance(SystemSettingActivity.this).contactsConfig(SystemSettingActivity.this);
				break;
			case R.id.mx_ui_custom:
				intent = new Intent(SystemSettingActivity.this, UICustomActivity.class);
				startActivity(intent);
				break;
			case R.id.open_horizontal_screen:
				intent = new Intent(SystemSettingActivity.this,OpenHorizontalScreenActivity.class);
				startActivity(intent);
				break;

		}
	}
}