package com.minxing.client.demo;

import android.content.Context;
import android.content.Intent;

import com.minxing.kit.api.bean.MXAppInfo;
import com.minxing.kit.plugin.android.MXPlugin;

public class DemoSamplePlugin extends MXPlugin {

	@Override
	public void start(Context context, MXAppInfo app, String params, String extParams) {
		Intent intent = new Intent(context, SystemAppDemoActivity.class);
		context.startActivity(intent);
	}
}
