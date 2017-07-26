package com.minxing.client.util;

import android.content.Context;
import android.util.DisplayMetrics;

public class WindowUtils {

	public static DisplayMetrics getDisplayMetrics(Context ctx) {
		return ctx.getResources().getDisplayMetrics();
	}

	public static int getScreenWidth(Context ctx) {
		return getDisplayMetrics(ctx).widthPixels;
	}
	
	public static int getScreenHeight(Context ctx) {
		return getDisplayMetrics(ctx).heightPixels;
	}
}
