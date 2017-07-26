package com.minxing.client.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.minxing.client.R;
import com.minxing.kit.internal.common.DebugActivity;

public class SystemAppDemoActivity extends Activity {
	private LinearLayout layoutPersonCenter;
	private LinearLayout layoutShare;
	private LinearLayout layoutCircle;
	private LinearLayout layoutChat;
	private LinearLayout layoutAppInfo;
	private ImageButton leftBackButton;
	private TextView title;
	private LinearLayout appdemo_app_debug;
	
	private int secretCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_app_demo);
		layoutChat = (LinearLayout) findViewById(R.id.appdemo_chat);
		layoutChat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(SystemAppDemoActivity.this, SystemAppDemoChatActivity.class);
				startActivity(it);
			}
		});
		layoutShare = (LinearLayout) findViewById(R.id.appdemo_share);
		layoutShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(SystemAppDemoActivity.this, SystemAppDemoShareActivity.class);
				startActivity(it);
			}
		});
		layoutCircle = (LinearLayout) findViewById(R.id.appdemo_circle);
		layoutCircle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(SystemAppDemoActivity.this, SystemAppDemoCircle.class);
				startActivity(it);
			}
		});
		layoutPersonCenter = (LinearLayout) findViewById(R.id.appdemo_personcenter);
		layoutPersonCenter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(SystemAppDemoActivity.this, SystemAppDemoPersonCenterActivity.class);
				startActivity(it);
			}
		});
		layoutAppInfo = (LinearLayout) findViewById(R.id.appdemo_app_info);
		layoutAppInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(SystemAppDemoActivity.this, SystemAppDemoAppInfo.class);
				startActivity(it);
			}
		});
		appdemo_app_debug = (LinearLayout) findViewById(R.id.appdemo_app_debug);
		appdemo_app_debug.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it = new Intent(SystemAppDemoActivity.this, DebugActivity.class);
				startActivity(it);
				
			}
		});
		title = (TextView) findViewById(R.id.title_name);
		title.setText("Demo分类");
		leftBackButton = (ImageButton) findViewById(R.id.title_left_button);
		leftBackButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		title.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				secretCount++;
				
				if(secretCount >= 5){
					appdemo_app_debug.setVisibility(View.VISIBLE);
				}
			}
		});
	}
}
