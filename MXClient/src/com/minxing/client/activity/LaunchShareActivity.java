package com.minxing.client.activity;

import java.util.ArrayList;
import java.util.List;

import com.minxing.client.AppConstants;
import com.minxing.client.LoginActivity;
import com.minxing.client.R;
import com.minxing.client.RootActivity;
import com.minxing.client.util.CacheManager;
import com.minxing.client.util.PreferenceUtils;
import com.minxing.client.util.Utils;
import com.minxing.kit.MXConstants;
import com.minxing.kit.api.MXAPI;
import com.minxing.kit.api.bean.MXCurrentUser;
import com.minxing.kit.api.bean.ShareLink;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class LaunchShareActivity extends RootActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!Utils.checkConnectState(this)) {
			Utils.toast(this, getString(R.string.error_no_network), Toast.LENGTH_SHORT);
			finish();
			return;
		}

		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		if (Intent.ACTION_SEND.equals(action) && type != null) {
			if ("text/plain".equals(type)) {
				handleSendText(intent);
			} else if (type.startsWith("image/")) {
				handleSendImage(intent);
			}else if("application/mx-share".equals(type)){
				handleMXShare(intent);
			}
		} else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
			if (type.startsWith("image/")) {
				handleSendMultipleImages(intent);
			}
		}else if((MXConstants.Share.MXKIT_INTENT_ACTION_SHARE.equals(action)  || MXConstants.Share.MXKIT_INTENT_ACTION_SHARE_CHAT.equals(action) )&& type != null){
			if("application/mx-share".equals(type)){
				handleMXShare(intent);
			}
		}
	}


	private void handleSendText(Intent intent) {
		String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
		if (sharedText != null) {
			CacheManager.getInstance().setHoldedShareContent(sharedText);
			startShare(AppConstants.SYSTEM_APP2APP_TYPE_SHARE_TEXT);
		} else {
			Utils.toast(this, getString(R.string.share_message_unsupport), Toast.LENGTH_SHORT);
		}
		finish();
	}

	private void handleSendImage(Intent intent) {
		Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
		if (imageUri != null) {
			CacheManager.getInstance().setHoldedShareContent(imageUri);
			startShare(AppConstants.SYSTEM_APP2APP_TYPE_SHARE_SINGLE_IMAGE);
		} else {
			Utils.toast(this, getString(R.string.share_message_unsupport), Toast.LENGTH_SHORT);
		}
		finish();
	}

	private void handleSendMultipleImages(Intent intent) {
		ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
		if (imageUris != null && !imageUris.isEmpty()) {
			CacheManager.getInstance().setHoldedShareContent(imageUris);
			startShare(AppConstants.SYSTEM_APP2APP_TYPE_SHARE_MULTI_IMAGE);
		} else {
			Utils.toast(this, getString(R.string.share_message_unsupport), Toast.LENGTH_SHORT);
		}
		finish();
	}

	@SuppressWarnings("unchecked")
	private void startShare(int shareType) {
		MXCurrentUser currentUser = MXAPI.getInstance(this).currentUser();
		Intent intent = null;
		if (currentUser != null) {
			// WBSysUtils.setHeader2ImageLoader(token);
			if (PreferenceUtils.isGesturePwdEnable(this, currentUser.getLoginName())) {
				intent = new Intent(this, GesturePasswordActivity.class);
				intent.putExtra(AppConstants.SYSTEM_START_TYPE_APP2APP, true);
				intent.putExtra(AppConstants.SYSTEM_APP2APP_TYPE, shareType);
				startActivity(intent);
			} else {
				switch (shareType) {
				case AppConstants.SYSTEM_APP2APP_TYPE_SHARE_TEXT:
					MXAPI.getInstance(this).shareTextToChat(this, (String) CacheManager.getInstance().getHoldedShareContent());
					break;

				case AppConstants.SYSTEM_APP2APP_TYPE_SHARE_SINGLE_IMAGE:
					MXAPI.getInstance(this).shareImageToChat(this, (Uri) CacheManager.getInstance().getHoldedShareContent());
					break;

				case AppConstants.SYSTEM_APP2APP_TYPE_SHARE_MULTI_IMAGE:
					MXAPI.getInstance(this).shareImagesToChat(this, (List<Uri>) CacheManager.getInstance().getHoldedShareContent());
					break;
				}
			}
		} else {
			intent = new Intent(this, LoginActivity.class);
			intent.putExtra(AppConstants.SYSTEM_START_TYPE_APP2APP, true);
			intent.putExtra(AppConstants.SYSTEM_APP2APP_TYPE, shareType);
			startActivity(intent);
		}
	}
	
	private void handleMXShare(Intent intent) {
		MXCurrentUser currentUser = MXAPI.getInstance(this).currentUser();
		if (currentUser != null) {
			String title = intent.getStringExtra(MXConstants.Share.MXKIT_INTENT_ACTION_DATA_SHARE_TITLE);
			String url = intent.getStringExtra(MXConstants.Share.MXKIT_INTENT_ACTION_DATA_SHARE_URL);
			String description = intent.getStringExtra(MXConstants.Share.MXKIT_INTENT_ACTION_DATA_SHARE_DESCRIPTION);
			String thumbnail_url = intent.getStringExtra(MXConstants.Share.MXKIT_INTENT_ACTION_DATA_SHARE_THUMBNAIL_URL);
			String appName = intent.getStringExtra(MXConstants.Share.MXKIT_INTENT_SHARE_APP_NAME);
			ShareLink link = new ShareLink();
			link.setTitle(title);
			link.setThumbnail(thumbnail_url);
			link.setUrl(url);
			link.setDesc(description);
			MXAPI.getInstance(LaunchShareActivity.this).shareToChat(LaunchShareActivity.this, link, appName, true);
		}else {
			intent = new Intent(this, LoginActivity.class);
			intent.putExtra(AppConstants.SYSTEM_START_TYPE_APP2APP, true);
//			intent.putExtra(AppConstants.SYSTEM_APP2APP_TYPE, shareType);
			startActivity(intent);
		}
		
		finish();
	}
}
