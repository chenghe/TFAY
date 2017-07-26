package com.minxing.client.demo;

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

public class SystemAppDemoCircle extends Activity {
	private ImageButton leftBackButton;
	private TextView title;
	private MXCurrentUser currentUser;
	private LinearLayout forceRefreshCircle;
	private LinearLayout setCircleAutoRefresh;
	private LinearLayout checkNetworkUnread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_app_demo_circle);
		title = (TextView) findViewById(R.id.title_name);
		title.setText("circle Demo");
		currentUser = MXAPI.getInstance(SystemAppDemoCircle.this).currentUser();
		leftBackButton = (ImageButton) findViewById(R.id.title_left_button);
		leftBackButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		forceRefreshCircle = (LinearLayout) findViewById(R.id.appdemo_force_refresh_circle);
		forceRefreshCircle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MXAPI.getInstance(SystemAppDemoCircle.this).forceRefreshCircle();
				Utils.toast(SystemAppDemoCircle.this, R.string.interface_force_refresh_circle, Toast.LENGTH_SHORT);
			}
		});
		setCircleAutoRefresh = (LinearLayout) findViewById(R.id.appdemo_set_autorefresh_circle);
		setCircleAutoRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MXAPI.getInstance(SystemAppDemoCircle.this).setCircleAutoRefresh();
				Utils.toast(SystemAppDemoCircle.this, R.string.interface_set_auto_refresh_circle, Toast.LENGTH_SHORT);
			}
		});
		checkNetworkUnread = (LinearLayout) findViewById(R.id.appdemo_check_network_unread);
		checkNetworkUnread.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean check = MXAPI.getInstance(SystemAppDemoCircle.this).checkNetworkCircleUnread(currentUser.getNetworkID());
				Utils.toast(SystemAppDemoCircle.this, getString(R.string.interface_check_network_unread) + "：" + check, Toast.LENGTH_SHORT);
			}
		});
	}
}
