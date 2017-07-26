package com.minxing.client.demo;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.minxing.client.R;
import com.minxing.client.RootActivity;
import com.minxing.kit.MXUIEngine;
import com.minxing.kit.ui.appcenter.AppCenterManager;
import com.minxing.kit.ui.appcenter.AppCenterManager.OnEditModeListener;
import com.minxing.kit.ui.appcenter.MXAppCenterView;

public class SystemAppDemoAppCenterViewActivity extends RootActivity {
	private MXAppCenterView appView = null;
	private ImageButton leftBackButton = null;
	private ImageButton rightAddButton = null;
	private Button rightTextButton = null;

	private AppCenterManager appCenterManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo_app_center_view);
		appCenterManager = MXUIEngine.getInstance().getAppCenterManager();
		
		((TextView)findViewById(R.id.title_name)).setText("app view demo");

		leftBackButton = (ImageButton) findViewById(R.id.title_left_button);
		leftBackButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finishWithAnimation();
			}
		});

		rightAddButton = (ImageButton) findViewById(R.id.title_right_image_button);
		rightAddButton.setVisibility(View.VISIBLE);
		rightAddButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				appCenterManager.addApp(SystemAppDemoAppCenterViewActivity.this);
				overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
			}
		});

		rightTextButton = (Button) findViewById(R.id.title_right_button);
		rightTextButton.setText(R.string.complete);

		appView = (MXAppCenterView) findViewById(R.id.app_center_view);
		appView.enableAppChangeMonitor();

		appCenterManager.setEditModeListener(new OnEditModeListener() {

			@Override
			public void onStartEditMode() {
				rightAddButton.setVisibility(View.GONE);
				rightTextButton.setVisibility(View.VISIBLE);
				rightTextButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						appCenterManager.endEdit();
					}
				});
			}

			@Override
			public void onEndEditMode() {
				rightAddButton.setVisibility(View.VISIBLE);
				rightTextButton.setVisibility(View.GONE);
			}
		});
		
//		MXAPI.getInstance(this).getAppCenterOcus(new OcuInfoCallback() {
//			
//			@Override
//			public void onSuccess() {
//			}
//			
//			@Override
//			public void onLoading() {
//			}
//			
//			@Override
//			public void onFail(MXError error) {
//			}
//			
//			@Override
//			public void onResult(final List<OcuInfo> ocuList) {
//				Utils.toast(DemoAppCenterViewActivity.this, JSON.toJSONString(ocuList), Toast.LENGTH_LONG);
//				rightAddButton.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						 MXAPI.getInstance(DemoAppCenterViewActivity.this).launchAppCenterOcu(ocuList.get(0));
//					}
//				});
//			}
//		});
	}

	@Override
	protected void onResume() {
		if (appCenterManager != null) {
			appCenterManager.setAppView(appView);
		}
		appView.loadData(false);
		super.onResume();
	}

	@Override
	protected void onPause() {
		appView.endEdit();
		if (appCenterManager != null) {
			appCenterManager.clearAppView();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		appView.disableAppChangeMonitor();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finishWithAnimation();
		}
		return false;
	}
}