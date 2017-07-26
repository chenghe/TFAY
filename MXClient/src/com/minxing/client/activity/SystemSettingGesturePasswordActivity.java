package com.minxing.client.activity;

import com.minxing.client.R;
import com.minxing.client.RootActivity;
import com.minxing.client.util.PreferenceUtils;
import com.minxing.kit.api.MXAPI;
import com.minxing.kit.api.bean.MXCurrentUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

public class SystemSettingGesturePasswordActivity extends RootActivity {

//	private RelativeLayout gesture_password_setting_on = null;//手势开父控件

	private Switch setting_gesture_password_switch;
	
	private RelativeLayout change_gesture_password = null;//修改手势父控件

	private ImageButton backButton = null;
	private String loginName = "";

	private static final int REQUEST_SETTING_GESTURE_PASSWORD = 9901;
	private static final int REQUEST_CANCEL_GESTURE_PASSWORD = 9902;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_setting_gesture_password);

		MXCurrentUser currentUser = MXAPI.getInstance(this).currentUser();
		if(currentUser != null){
			loginName = currentUser.getLoginName();
		}
		
		((TextView) findViewById(R.id.title_name)).setText(R.string.setting_gesture_password);
		backButton = (ImageButton) findViewById(R.id.title_left_button);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finishWithAnimation();
			}
		});

//		gesture_password_setting_on = (RelativeLayout) findViewById(R.id.gesture_password_setting_on);
		change_gesture_password = (RelativeLayout) findViewById(R.id.change_gesture_password);
		setting_gesture_password_switch = (Switch) findViewById(R.id.setting_gesture_password_switch);
		

		if (PreferenceUtils.isInitGesturePwd(this, loginName) && PreferenceUtils.isGesturePwdEnable(this, loginName)) {
			setting_gesture_password_switch.setChecked(true);
			change_gesture_password.setVisibility(View.VISIBLE);
		} else {
			setting_gesture_password_switch.setChecked(false);
			change_gesture_password.setVisibility(View.GONE);
		}

		setting_gesture_password_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					//打开
					if (PreferenceUtils.isInitGesturePwd(SystemSettingGesturePasswordActivity.this, loginName)) {
						PreferenceUtils.resetGesturePwd(SystemSettingGesturePasswordActivity.this, loginName);
					}
					Intent i = new Intent(SystemSettingGesturePasswordActivity.this, GesturePasswordActivity.class);
					startActivityForResult(i, REQUEST_SETTING_GESTURE_PASSWORD);
				}else{
					//关闭
					Intent i = new Intent(SystemSettingGesturePasswordActivity.this, GesturePasswordActivity.class);
					i.putExtra("is_cancel_password", true);
					startActivityForResult(i, REQUEST_CANCEL_GESTURE_PASSWORD);
				}
			}
		});
		
		
//		gesture_password_on.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent i = new Intent(SystemSettingGesturePasswordActivity.this, GesturePasswordActivity.class);
//				i.putExtra("is_cancel_password", true);
//				startActivityForResult(i, REQUEST_CANCEL_GESTURE_PASSWORD);
//			}
//		});

		change_gesture_password.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SystemSettingGesturePasswordActivity.this, GesturePasswordActivity.class);
				i.putExtra("is_reset_password", true);
				startActivity(i);
			}
		});

//		gesture_password_off.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (PreferenceUtils.isInitGesturePwd(SystemSettingGesturePasswordActivity.this, loginName)) {
//					PreferenceUtils.resetGesturePwd(SystemSettingGesturePasswordActivity.this, loginName);
//				}
//				Intent i = new Intent(SystemSettingGesturePasswordActivity.this, GesturePasswordActivity.class);
//				startActivityForResult(i, REQUEST_SETTING_GESTURE_PASSWORD);
//			}
//		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finishWithAnimation();
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}

		switch (requestCode) {
		case REQUEST_SETTING_GESTURE_PASSWORD:
			PreferenceUtils.enableGesturePwd(this, loginName);
			change_gesture_password.setVisibility(View.VISIBLE);
//			gesture_password_setting_off.setVisibility(View.GONE);
//			gesture_password_setting_on.setVisibility(View.VISIBLE);
			break;

		case REQUEST_CANCEL_GESTURE_PASSWORD:
			PreferenceUtils.disableGesturePwd(this, loginName);
			change_gesture_password.setVisibility(View.GONE);
//			gesture_password_setting_off.setVisibility(View.VISIBLE);
//			gesture_password_setting_on.setVisibility(View.GONE);
			break;
		}
	}

}