package com.minxing.kit.plugin.android.mail;

import android.content.Context;

import com.minxing.kit.api.bean.MXAppInfo;
import com.minxing.kit.mail.MXMail;
import com.minxing.kit.plugin.android.MXPlugin;

public class Mail extends MXPlugin {

	@Override
	public void start(Context context, MXAppInfo app, String params, String extParams) {
//		app.setChatDisplayOrder(99);
		boolean isFtt = MXMail.getInstance().isFtt(context);
		if (isFtt) {
			MXMail.getInstance().loadApp(context, app);
		} else {
			MXMail.getInstance().loadApp(context, app);
		}
	}

	@Override
	public void uninstall(Context context, MXAppInfo app) {
		MXMail.getInstance().uninstall(context);
	}
}