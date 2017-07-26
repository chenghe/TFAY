package com.minxing.client.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.minxing.client.AppConstants;
import com.minxing.client.R;
import com.minxing.client.RootActivity;
import com.minxing.client.util.PreferenceUtils;
import com.minxing.kit.api.MXAPI;
import com.minxing.kit.api.bean.MXCurrentUser;

public class SystemSettingDisturbActivity extends RootActivity {
	private LinearLayout disturb_all_day = null;
	private LinearLayout disturb_night = null;
	private LinearLayout disturb_none = null;
	private TextView disturb_all_day_check = null;
	private TextView disturb_night_check = null;
	private TextView disturb_none_check = null;
	private ImageButton backButton = null;

	private String loginName = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_setting_message_disturb);
		
		MXCurrentUser currentUser = MXAPI.getInstance(this).currentUser();
		if(currentUser != null){
			loginName = currentUser.getLoginName();
		}
		((TextView) findViewById(R.id.title_name)).setText(R.string.setting_message_disturb);

		disturb_all_day = (LinearLayout) findViewById(R.id.disturb_all_day);
		disturb_night = (LinearLayout) findViewById(R.id.disturb_night);
		disturb_none = (LinearLayout) findViewById(R.id.disturb_none);

		disturb_all_day_check = (TextView) findViewById(R.id.disturb_all_day_check);
		disturb_night_check = (TextView) findViewById(R.id.disturb_night_check);
		disturb_none_check = (TextView) findViewById(R.id.disturb_none_check);

		backButton = (ImageButton) findViewById(R.id.title_left_button);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		disturb_all_day.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				disturb_all_day_check.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sns_shoot_select_checked, 0);
				disturb_night_check.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sns_shoot_select_normal, 0);
				disturb_none_check.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sns_shoot_select_normal, 0);
				PreferenceUtils.saveCurrentDisturbType(SystemSettingDisturbActivity.this, loginName, AppConstants.SYSTEM_SETTING_DISTURB_ALLDAY);
			}
		});

		disturb_night.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				disturb_all_day_check.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sns_shoot_select_normal, 0);
				disturb_night_check.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sns_shoot_select_checked, 0);
				disturb_none_check.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sns_shoot_select_normal, 0);
				PreferenceUtils.saveCurrentDisturbType(SystemSettingDisturbActivity.this, loginName, AppConstants.SYSTEM_SETTING_DISTURB_NIGHT);
			}
		});

		disturb_none.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				disturb_all_day_check.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sns_shoot_select_normal, 0);
				disturb_night_check.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sns_shoot_select_normal, 0);
				disturb_none_check.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sns_shoot_select_checked, 0);
				PreferenceUtils.saveCurrentDisturbType(SystemSettingDisturbActivity.this, loginName, AppConstants.SYSTEM_SETTING_DISTURB_NONE);
			}
		});

		int currentDisturb = PreferenceUtils.getCurrentDisturbType(this, loginName);
		switch (currentDisturb) {
		case AppConstants.SYSTEM_SETTING_DISTURB_NONE:
			disturb_all_day_check.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sns_shoot_select_normal, 0);
			disturb_night_check.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sns_shoot_select_normal, 0);
			disturb_none_check.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sns_shoot_select_checked, 0);
			break;

		case AppConstants.SYSTEM_SETTING_DISTURB_NIGHT:
			disturb_all_day_check.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sns_shoot_select_normal, 0);
			disturb_night_check.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sns_shoot_select_checked, 0);
			disturb_none_check.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sns_shoot_select_normal, 0);
			break;

		case AppConstants.SYSTEM_SETTING_DISTURB_ALLDAY:
			disturb_all_day_check.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sns_shoot_select_checked, 0);
			disturb_night_check.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sns_shoot_select_normal, 0);
			disturb_none_check.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sns_shoot_select_normal, 0);
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return false;
	}
}