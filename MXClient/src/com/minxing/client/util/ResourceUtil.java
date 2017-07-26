package com.minxing.client.util;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

public class ResourceUtil {
	public static String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.getStackTrace();
		}
		return verName;
	}
	
	public static int getVerCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.getStackTrace();
		}
		return verCode;
	}
	
	public static String getConfString(Context context, String key) {
		int id = context.getResources().getIdentifier(key, "string", context.getPackageName());
		if(id > 0){
			return context.getResources().getString(id);
		}
		return null;
	}
	
	public static boolean getConfBoolean(Context context, String key) {
		int id = context.getResources().getIdentifier(key, "string", context.getPackageName());
		if(id > 0){
			return Boolean.parseBoolean(context.getResources().getString(id));
		}
		return false;
	}
	
	public static boolean getConfBoolean(Context context, String key, boolean defaultValue) {
		int id = context.getResources().getIdentifier(key, "string", context.getPackageName());
		if(id > 0){
			return Boolean.parseBoolean(context.getResources().getString(id));
		}
		return defaultValue;
	}

	public static long getConfLong(Context context, String key){
		int id = context.getResources().getIdentifier(key, "string", context.getPackageName());
		if (id > 0){
			return Long.parseLong(context.getResources().getString(id));
		}
		return 0;
	}
	public static String[] getConfArray(Context context,String key) {
		int id = context.getResources().getIdentifier(key, "array", context.getPackageName());
		if (id > 0) {
			String[] tab_user_web = context.getResources().getStringArray(id);
			return  tab_user_web;
		}
		return  null ;
	}
	public static Drawable getConfDrawable(Context context, String key){
		int id = context.getResources().getIdentifier(key,"drawable",context.getPackageName());
		if(id>0){
			return context.getResources().getDrawable(id);
		}
		return null;
	}


	public static int getConfInt(Context context, String key, int defaultValue) {
		int id = context.getResources().getIdentifier(key, "string", context.getPackageName());
		if(id > 0){
			return Integer.parseInt(context.getResources().getString(id));
		}
		return defaultValue;
	}
}
