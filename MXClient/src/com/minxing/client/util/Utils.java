package com.minxing.client.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.minxing.client.R;
import com.minxing.client.activity.LaunchShareCircleActivity;
import com.minxing.client.upgrade.AppUpgradeInfo;
import com.minxing.client.upgrade.SmartUpgradeHelper;

public class Utils {

	public static boolean sdcardAvailable() {
		try {
			return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void toast(Context context, String message, int duration) {
		if (isApplicationForeground(context)) {
			Toast.makeText(context, message, duration).show();
		}
	}
	
	public static void toast(Context context, int messageID, int duration) {
		if (isApplicationForeground(context)) {
			Toast.makeText(context, messageID, duration).show();
		}
	}

	public static boolean checkConnectState(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeInfo = manager.getActiveNetworkInfo();
		if (activeInfo != null) {
			return activeInfo.isConnected();
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public static boolean isApplicationForeground(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		KeyguardManager kgm = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);

		boolean isForeground = false;
		boolean isScreenActive = true;
		if (kgm.inKeyguardRestrictedInputMode()) {
			isScreenActive = false;
		}

		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			if (topActivity.getPackageName().equals(context.getPackageName())) {
				isForeground = true;
			}
		}
		return isForeground && isScreenActive;
	}
	
	public static boolean checkMobileNumber(String mobile) {
		///^[a-z]([a-z0-9]*[-_]?[a-z0-9]+)*@([a-z0-9]*[-_]?[a-z0-9]+)+[\.][a-z]{2,3}([\.][a-z]{2})?$/i
		//^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$
		//^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$
		String check = "^((\\+{0,1}86){0,1})((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(mobile);
		return matcher.matches();
	}

	public static void showSystemDialog(Context context, String title, String tip, final DialogInterface.OnClickListener okListener,
			final DialogInterface.OnClickListener cancelListener, boolean isCancelable) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle(title);
		builder.setMessage(tip);

		builder.setPositiveButton(R.string.ok, new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				okListener.onClick(dialog, which);
				dialog.dismiss();
			}
		});

		builder.setNegativeButton(R.string.cancel, new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (cancelListener != null) {
					cancelListener.onClick(dialog, which);
				}
				dialog.dismiss();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.setCancelable(isCancelable);
		dialog.show();
	}

	public static void showUpgradeDialog(final Context context, final AppUpgradeInfo upgradeInfo) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle(String.format(context.getString(R.string.app_upgrade), upgradeInfo.getVersion()));

		String message = buildUpgradeMessage(context, upgradeInfo);
		if (message != null && !"".equals(message)) {
			builder.setMessage(message);
		}

		builder.setPositiveButton(R.string.ok, new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SmartUpgradeHelper updates = new SmartUpgradeHelper(context, upgradeInfo);
				updates.startUpdate(context);
				dialog.dismiss();
			}
		});
		if (!upgradeInfo.isMandatoryUpgrade()) {
			builder.setNegativeButton(R.string.cancel, new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		}
		AlertDialog dialog = builder.create();
		dialog.setCancelable(!upgradeInfo.isMandatoryUpgrade());
		dialog.show();
	}

	public static String buildUpgradeMessage(Context context, AppUpgradeInfo upgradeInfo) {
		StringBuffer sb = new StringBuffer();

		if (upgradeInfo.getDescription() != null && !"".equals(upgradeInfo.getDescription())) {
			sb.append(upgradeInfo.getDescription()).append("\r\n\r\n");
		}
		if (upgradeInfo.getUpdateTime() != null && !"".equals(upgradeInfo.getUpdateTime())) {
			sb.append(context.getString(R.string.upgrade_info_time));
			sb.append(upgradeInfo.getUpdateTime()).append("\r\n");
		}

		if (upgradeInfo.getSize() > 0) {
			sb.append(context.getString(R.string.upgrade_info_size));
			if (upgradeInfo.isSmartUpgrade()) {
				sb.append(getFileSize(upgradeInfo.getSmart_size()));
			} else {
				sb.append(getFileSize(upgradeInfo.getSize()));
			}
		}
		return sb.toString();
	}

	/** 计算文件的大小（G,M,KB） */
	public static String getFileSize(double size) {
		double dis = size / 1024 / 1024;
		if (dis > 1024) {
			return new java.text.DecimalFormat("#0.00").format(size / 1024 / 1024 / 1024) + "G";
		}
		if (dis > 1) {
			return new java.text.DecimalFormat("#0.00").format(dis) + "M";
		}
		return new java.text.DecimalFormat("#0.00").format(size / 1024) + "KB";
	}
	
	public static String iso8601ToCustomerDate(String iso8601, String format) {
		if (iso8601 == null || iso8601.trim().length() == 0) {
			return "";
		}

		DateTime dateTime = null;
		try {
			dateTime = new DateTime(iso8601);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (dateTime == null) {
			return "";
		}

		if (format == null || "".equals(format)) {
			return String.valueOf(dateTime.getMillis());
		} else {
			return dateTime.toString(format);
		}
	}
	
	public static void installApk(Context context, String filePath) {
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static void setShareFromExternalBrowserEnable(Context context, boolean enabled) {
		PackageManager pm = context.getPackageManager();
		if (enabled) {
			pm.setComponentEnabledSetting(new ComponentName(context, LaunchShareCircleActivity.class),
					PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
		} else {
			pm.setComponentEnabledSetting(new ComponentName(context, LaunchShareCircleActivity.class),
					PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
		}
	}
}
