package com.minxing.client;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.minxing.client.util.BackgroundDetector;
import com.minxing.kit.MXKit;
import com.minxing.kit.api.MXAPI;
import com.minxing.kit.api.bean.MXCurrentUser;
import com.minxing.kit.internal.common.bean.UserAccount;
import com.umeng.analytics.MobclickAgent;

public class RootActivity extends Activity {
	private ComponentName componentName = getComponentName();
	private long startActivityTime = 0;
	private final long DELAYTIME = 1000; // 1 SECOND
	private boolean mHandleStatusColor = true;

	@Override
	public void startActivity(Intent intent) {
		if (componentName == null || intent.getComponent() == null || SystemClock.uptimeMillis() - startActivityTime > DELAYTIME
				|| !intent.getComponent().equals(componentName)) {
			super.startActivity(intent);

			if (getParent() != null) {
				getParent().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
			} else {
				overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
			}

			startActivityTime = SystemClock.uptimeMillis();
			componentName = intent.getComponent();
		}
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		if (componentName == null || intent.getComponent() == null || SystemClock.uptimeMillis() - startActivityTime > DELAYTIME
				|| !intent.getComponent().equals(componentName)) {
			BackgroundDetector.getInstance().setDetectorStop(true);
			super.startActivityForResult(intent, requestCode);

			if (getParent() != null) {
				getParent().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
			} else {
				overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
			}

			startActivityTime = SystemClock.uptimeMillis();
			componentName = intent.getComponent();
		}
	}

	protected void finishWithAnimation() {
		super.finish();
		overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		BackgroundDetector.getInstance().setDetectorStop(false);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}
	
	

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setScreenOrientation();
		if (mHandleStatusColor) {
			handleStatusBarColor();
		}
	}
	public void setScreenOrientation(){
		int currentAccountID = 0;
		MXCurrentUser currentuser = MXAPI.getInstance(RootActivity.this).currentUser();
		if (currentuser != null){
			currentAccountID = currentuser.getAccountId();
		}

		Boolean isSensor = MXAPI.getInstance(this).getScreenOrientation(this,currentAccountID);
		if(isSensor){
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		}else{
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}


	@TargetApi(21/* Build.VERSION_CODES.LOLLIPOP */)
	private void handleStatusBarColor() {
		if (Build.VERSION.SDK_INT >= 21/* Build.VERSION_CODES.LOLLIPOP */) {
			Window window = getWindow();
			// clear FLAG_TRANSLUCENT_STATUS flag:
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			// finally change the color
			window.setStatusBarColor(getResources().getColor(R.color.status_bar_color));
		}
	}

	protected void setHandleStatusColor(boolean isEnable) {
		mHandleStatusColor = isEnable;
	}

}
