package com.minxing.client.demo;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.minxing.client.R;
import com.minxing.client.util.Utils;
import com.minxing.kit.api.MXAPI;
import com.minxing.kit.api.bean.MXCurrentUser;
import com.minxing.kit.api.bean.MXError;
import com.minxing.kit.api.bean.MXUser;
import com.minxing.kit.api.callback.MXJsonCallBack;
import com.minxing.kit.api.callback.UserCallback;

public class SystemAppDemoPersonCenterActivity extends Activity {
	private ImageButton leftBackButton;
	private TextView title;
	private LinearLayout viewCurrentUser;
	private LinearLayout starCurrentUserActivity;
	private LinearLayout starNetworkList;
	private LinearLayout starPersonInfo;
	private LinearLayout viewPersonInfoJson;
	private LinearLayout viewPersonInfo;
	private LinearLayout changePassWord;
	private MXCurrentUser currentUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_app_demo_person_center);
		currentUser = MXAPI.getInstance(SystemAppDemoPersonCenterActivity.this).currentUser();
		title = (TextView) findViewById(R.id.title_name);
		title.setText("me Demo");
		leftBackButton = (ImageButton) findViewById(R.id.title_left_button);
		leftBackButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		viewCurrentUser = (LinearLayout) findViewById(R.id.appdemo_view_crrent_user);
		viewCurrentUser.setVisibility(View.GONE);
		viewCurrentUser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MXAPI.getInstance(SystemAppDemoPersonCenterActivity.this).currentUser();
			}
		});
		starCurrentUserActivity = (LinearLayout) findViewById(R.id.appdemo_star_crrent_user_activity);
		starCurrentUserActivity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MXAPI.getInstance(SystemAppDemoPersonCenterActivity.this).viewCurrentUser();
			}
		});
		starNetworkList = (LinearLayout) findViewById(R.id.appdemo_star_network_list);
		starNetworkList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MXAPI.getInstance(SystemAppDemoPersonCenterActivity.this).viewNetworkList();
			}
		});
		starPersonInfo = (LinearLayout) findViewById(R.id.appdemo_star_personinfo_activity);
		starPersonInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MXAPI.getInstance(SystemAppDemoPersonCenterActivity.this).viewPersonInfo(currentUser.getLoginName());

			}
		});
		viewPersonInfoJson = (LinearLayout) findViewById(R.id.appdemo_view_personinfo_json);
		// viewPersonInfoJson.setVisibility(View.GONE);
		viewPersonInfoJson.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MXAPI.getInstance(SystemAppDemoPersonCenterActivity.this).personInfo(currentUser.getLoginName(), new MXJsonCallBack() {

					@Override
					public void onSuccess() {
						Utils.toast(SystemAppDemoPersonCenterActivity.this, "success", Toast.LENGTH_SHORT);
					}

					@Override
					public void onLoading() {

					}

					@Override
					public void onFail(MXError error) {
						Utils.toast(SystemAppDemoPersonCenterActivity.this, "fail", Toast.LENGTH_SHORT);
					}

					@Override
					public void onResult(String result) {
						Utils.toast(SystemAppDemoPersonCenterActivity.this, result, Toast.LENGTH_SHORT);
					}
				});
			}
		});
		viewPersonInfo = (LinearLayout) findViewById(R.id.appdemo_view_personinfo);
		// viewPersonInfo.setVisibility(View.GONE);
		viewPersonInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MXAPI.getInstance(SystemAppDemoPersonCenterActivity.this).personInfo(currentUser.getLoginName(), new UserCallback() {

					@Override
					public void onSuccess() {
						Utils.toast(SystemAppDemoPersonCenterActivity.this, "success", Toast.LENGTH_SHORT);
					}

					@Override
					public void onLoading() {

					}

					@Override
					public void onFail(MXError error) {
						Utils.toast(SystemAppDemoPersonCenterActivity.this, "fail", Toast.LENGTH_SHORT);
					}

					@Override
					public void onResult(List<MXUser> userList) {
						Utils.toast(SystemAppDemoPersonCenterActivity.this, "success", Toast.LENGTH_SHORT);
					}

					@Override
					public void onResult(MXUser user) {
						Utils.toast(SystemAppDemoPersonCenterActivity.this, "success", Toast.LENGTH_SHORT);
					}
				});
			}
		});
		changePassWord = (LinearLayout) findViewById(R.id.appdemo_change_password);
		changePassWord.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MXAPI.getInstance(SystemAppDemoPersonCenterActivity.this).changePassword(SystemAppDemoPersonCenterActivity.this);
			}
		});
	}
}
