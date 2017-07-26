package com.minxing.client.activity;

import com.minxing.client.R;
import com.minxing.client.RootActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

public class SystemFunctionIntrodutionActivity extends RootActivity {
	private WebView web;
	private ImageButton btnLeft;
	private TextView titleView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview_function_introdution);
		Intent intent = getIntent();
		String url = intent.getStringExtra("introdution_url");
		if (url == null || url.equals("")) {
			finish();
			return;
		}
		web = (WebView) findViewById(R.id.web_function_introdution);
		titleView = (TextView) findViewById(R.id.title_name);
		titleView.setText(R.string.web_title_instrodution);
		btnLeft = (ImageButton) findViewById(R.id.title_left_button);
		//btnLeft.setImageDrawable(getResources().getDrawable(R.drawable.btn_close));
		btnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		web.getSettings().setJavaScriptEnabled(true);
		web.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});

		web.setWebChromeClient(new WebChromeClient() {
			public void onReceivedTitle(WebView view, String title) {
				titleView.setText(title);
				super.onReceivedTitle(view, title);
			}
		});

		web.loadUrl(url);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack()) {
			web.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
