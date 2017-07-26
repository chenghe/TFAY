package com.minxing.client.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.minxing.client.AppConstants;
import com.minxing.client.LoginActivity;
import com.minxing.client.R;
import com.minxing.client.RootActivity;
import com.minxing.client.util.CacheManager;
import com.minxing.client.util.PreferenceUtils;
import com.minxing.client.util.Utils;
import com.minxing.kit.api.MXAPI;
import com.minxing.kit.api.bean.MXCurrentUser;

public class LaunchComposeMailActivity extends RootActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		String action = intent.getAction();
		Uri localUri = intent.getData();
		if (Intent.ACTION_VIEW.equals(action) || Intent.ACTION_SENDTO.equals(action)) {
			if ("mailto".equals(localUri.getScheme())) {
				handleSendMail(intent);
			}
		}
	}

	private void handleSendMail(Intent intent) {
		Uri mailUri = intent.getData();
		if (mailUri != null) {
			CacheManager.getInstance().setHoldedShareContent(mailUri);
			sendMail();
		} else {
			Utils.toast(this, getString(R.string.share_message_unsupport), Toast.LENGTH_SHORT);
		}
		finish();

	}

	private void sendMail() {
		MXCurrentUser currentUser = MXAPI.getInstance(this).currentUser();
		Intent intent = null;
		if (currentUser != null) {
//			WBSysUtils.setHeader2ImageLoader(token);
			if (PreferenceUtils.isGesturePwdEnable(this, currentUser.getLoginName())) {
				intent = new Intent(this, GesturePasswordActivity.class);
				intent.putExtra(AppConstants.SYSTEM_START_TYPE_APP2APP, true);
				intent.putExtra(AppConstants.SYSTEM_APP2APP_TYPE, AppConstants.SYSTEM_APP2APP_TYPE_MAILTO);
				startActivity(intent);
			} else {
				MXAPI.getInstance(this).shareToMail(this, (Uri)CacheManager.getInstance().getHoldedShareContent());
			}
		} else {
			intent = new Intent(this, LoginActivity.class);
			intent.putExtra(AppConstants.SYSTEM_START_TYPE_APP2APP, true);
			intent.putExtra(AppConstants.SYSTEM_APP2APP_TYPE, AppConstants.SYSTEM_APP2APP_TYPE_MAILTO);
			startActivity(intent);
		}
	}
}
