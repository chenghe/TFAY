package com.minxing.client.core;

import android.content.Context;

public abstract class BaseCallBack {
	public Context mContext; // 上下文
	public String dialogTitle; // ProgressDialog的标题
	public String dialogContent; // ProgressDialog的提示内容
	public boolean isShowProgressDialog; // 是否显示ProgressDialog

	public BaseCallBack(Context context, boolean isShowProgressDialog, String title, String content) {
		this.mContext = context;
		this.dialogContent = content;
		this.dialogTitle = title;
		this.isShowProgressDialog = isShowProgressDialog;
	}

	public BaseCallBack(Context context) {
		this.mContext = context;
	}

	public BaseCallBack() {
	}

	public abstract void success(Object obj);

	public abstract void failure(ServiceError error);

	public Context getContext() {
		return mContext;
	}
}
