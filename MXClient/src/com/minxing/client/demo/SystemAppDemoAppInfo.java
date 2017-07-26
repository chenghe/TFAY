package com.minxing.client.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.content.Intent;
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
import com.minxing.kit.api.bean.MXAppInfo;
import com.minxing.kit.api.bean.MXError;
import com.minxing.kit.api.callback.AppInfoCallback;
import com.minxing.kit.api.callback.MXRequestCallBack;

@SuppressWarnings("deprecation")
public class SystemAppDemoAppInfo extends Activity {
	private ImageButton leftBackButton;
	private TextView title;
	private LinearLayout getAppCenterInfos;
	private LinearLayout launchAppInfo;
	private LinearLayout launchAppInfoEXT;
	private LinearLayout viewTopics;
	private LinearLayout invokeRequest;
	private LinearLayout layoutAppCenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_app_demo_app_info);
		title = (TextView) findViewById(R.id.title_name);
		title.setText("appcenter demo");
		leftBackButton = (ImageButton) findViewById(R.id.title_left_button);
		leftBackButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		getAppCenterInfos = (LinearLayout) findViewById(R.id.appdemo_get_app_center_infos);
		getAppCenterInfos.setVisibility(View.GONE);
		getAppCenterInfos.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		launchAppInfo = (LinearLayout) findViewById(R.id.appdemo_launch_app_info);
		launchAppInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getAppInfo(false);
			}
		});
		launchAppInfoEXT = (LinearLayout) findViewById(R.id.appdemo_launch_app_info_ext);
		launchAppInfoEXT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getAppInfo(true);
			}
		});
		viewTopics = (LinearLayout) findViewById(R.id.appdemo_view_topics);
		viewTopics.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MXAPI.getInstance(SystemAppDemoAppInfo.this).viewTopics();
			}
		});
		invokeRequest = (LinearLayout) findViewById(R.id.appdemo_invoke_request);
		invokeRequest.setVisibility(View.GONE);
		invokeRequest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				invokeNewRequest();

			}
		});
		layoutAppCenter = (LinearLayout) findViewById(R.id.appdemo_appcenter);
		layoutAppCenter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(SystemAppDemoAppInfo.this, SystemAppDemoAppCenterViewActivity.class);
				startActivity(it);
			}
		});
	}

	private void getAppInfo(final boolean isEXT) {
		MXAPI.getInstance(SystemAppDemoAppInfo.this).getAppCenterInfos(new AppInfoCallback() {

			@Override
			public void onSuccess() {
				Utils.toast(SystemAppDemoAppInfo.this, "success", Toast.LENGTH_SHORT);
			}

			@Override
			public void onLoading() {
				Utils.toast(SystemAppDemoAppInfo.this, "loading...", Toast.LENGTH_SHORT);
			}

			@Override
			public void onFail(MXError error) {
				Utils.toast(SystemAppDemoAppInfo.this, "fail", Toast.LENGTH_SHORT);
			}

			@Override
			public void onResult(List<MXAppInfo> appList) {
				Utils.toast(SystemAppDemoAppInfo.this, "get app info", Toast.LENGTH_SHORT);
				if (appList != null) {
					MXAppInfo info = appList.get(0);
					if (isEXT) {
						MXAPI.getInstance(SystemAppDemoAppInfo.this).launchAppInfo(info, "with ext params", null);
					} else {
						MXAPI.getInstance(SystemAppDemoAppInfo.this).launchAppInfo(info, null);
					}
				}
			}
		});
	}

	private void invokeNewRequest() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		TreeMap<String, String> map = new TreeMap<String, String>();
		List<String> list = new ArrayList<String>();
		MXAPI.getInstance(SystemAppDemoAppInfo.this).invokeRequest("POST", "", params, map, list, new MXRequestCallBack(SystemAppDemoAppInfo.this) {

			@Override
			public void onSuccess(String result) {
				Utils.toast(SystemAppDemoAppInfo.this, "request server interface success", Toast.LENGTH_SHORT);
			}

			@Override
			public void onFailure(MXError error) {
				Utils.toast(SystemAppDemoAppInfo.this, "request server interface fail", Toast.LENGTH_SHORT);
			}
		});
	}
}
