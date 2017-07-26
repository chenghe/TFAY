package com.minxing.client.http;

import android.content.Context;

import com.minxing.client.core.BaseCallBack;

public abstract class HttpCallBack extends BaseCallBack {
	
	public BaseCallBack mCallBack;

	public HttpCallBack() {}

	public HttpCallBack(Context context) {
		super(context);
	}
	
	public HttpCallBack(BaseCallBack viweCallBack) {
		this.setViewCallBack(viweCallBack);
	}
	
	public void setViewCallBack(BaseCallBack viewCallBack) {
		if(viewCallBack == null) {
			return ;
		}
		this.mContext = viewCallBack.mContext;
		this.mCallBack = viewCallBack;
		this.isShowProgressDialog = viewCallBack.isShowProgressDialog;
		this.dialogTitle = viewCallBack.dialogTitle;
		this.dialogContent = viewCallBack.dialogContent;
	}
}
