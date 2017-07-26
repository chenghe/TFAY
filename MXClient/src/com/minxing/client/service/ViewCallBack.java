package com.minxing.client.service;

import android.content.Context;
import android.widget.Toast;

import com.minxing.client.core.BaseCallBack;
import com.minxing.client.core.ServiceError;
import com.minxing.client.util.Utils;

public class ViewCallBack extends BaseCallBack {
	public Context context;

	public ViewCallBack(Context context, boolean isShowProgressDialog,
			String title, String content) {
		super(context, isShowProgressDialog, title, content);
		this.context = context;
	}

	public ViewCallBack(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public void success(Object obj) {
	}
	
	@Override
	public void failure(ServiceError error) {
		if (error.getMessage() != null
				&& error.getMessage().trim().length() > 0) {
			Utils.toast(context, String.valueOf(error.getMessage()),
					Toast.LENGTH_SHORT);
		}
	}
}
