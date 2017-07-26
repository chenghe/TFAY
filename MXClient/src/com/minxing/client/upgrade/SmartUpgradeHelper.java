package com.minxing.client.upgrade;

import java.io.File;
import java.io.IOException;

import com.minxing.client.AppConstants;
import com.minxing.client.R;
import com.minxing.client.upgrade.SmartUpgradeDownloader.DownloadListener;
import com.minxing.client.util.FileUtils;
import com.minxing.client.util.MD5Util;
import com.minxing.client.util.Utils;
import com.minxing.kit.MXKit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

public class SmartUpgradeHelper {
	private static final int MESSAGE_DOWNLOAD_COMPLETE = 2;
	private static final int MESSAGE_DOWNLOAD_ERROR = 3;
	private static final int MESSAGE_SDCARD_ERROR = 4;
	private final Context mContext;
	private SmartUpgradeDownloader downloader;
//	private PatchHelper patchHelper;
	private AppUpgradeInfo info;

	private String downloadURL = null;
	private boolean isNeedPatchAPK = false;
	private boolean isRetryDownload = false;

	private NotificationManager mNotificationManager = null;
	private Notification notification = null;
	private UpgradeThread mThread = new UpgradeThread();

	public SmartUpgradeHelper(Context context, AppUpgradeInfo info) {
		this.mContext = context;
		this.info = info;
		
		mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		initInstance();
	}

	private void initInstance() {
		File folder = new File(MXKit.getInstance().getKitConfiguration().getDownloadApkRoot());
		if (!folder.exists())
			folder.mkdirs();
		if (info.isSmartUpgrade()) {
			downloadURL = info.getSmart_url();
			isNeedPatchAPK = true;
			Log.i("hcb",downloadURL);
//			patchHelper = new PatchHelper(mContext);
		} else {
			downloadURL = info.getUpgrade_url();
			isNeedPatchAPK = false;
			Log.i("hcb",downloadURL);
		}
		downloader = new SmartUpgradeDownloader(mContext, info, folder, AppConstants.SAVE_UPGRADE_FILENAME, mThread);
	}

	@SuppressWarnings("deprecation")
	public void startUpdate(Context context) {
		try{
			if (notification != null) {
				mNotificationManager.cancel(AppConstants.UPGRADE_NOTIFICATION_ID);
				notification = null;
			}
			NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
					.setSmallIcon(android.R.drawable.stat_sys_download);
			builder.setContentText(mContext.getString(R.string.notification_downloading_upgrade));
//			Notification.Builder builder = new Notification.Builder(context)
//		    .setSmallIcon(android.R.drawable.stat_sys_download);
//			builder.setContentText(mContext.getString(R.string.notification_downloading_upgrade));
			
			notification = builder.build();

			notification.defaults = Notification.DEFAULT_LIGHTS;
			notification.flags = Notification.FLAG_ONGOING_EVENT;

			Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
			notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

			PendingIntent contentItent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
			notification.setLatestEventInfo(mContext, null, null, contentItent);
			setNotificationView(0);
			mNotificationManager.notify(AppConstants.UPGRADE_NOTIFICATION_ID, notification);
			mThread.execute();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	private void showError(int stringResourceId) {
		Utils.toast(mContext, mContext.getString(stringResourceId), Toast.LENGTH_SHORT);
		if (mNotificationManager != null)
			mNotificationManager.cancel(AppConstants.UPGRADE_NOTIFICATION_ID);
	}

	private void setNotificationView(int progress) {
		try{
			if(notification == null){
				return;
			}
			String progressInfo = "";
			if(progress > 100 ){
				progressInfo = FileUtils.bytesToHuman(Long.valueOf(progress));
				
			}else{
				progressInfo = progress + "%";
			}
			String tips = mContext.getString(R.string.notification_downloading_upgrade_progress) + progressInfo;
			RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.upgrade_notification);
			remoteViews.setTextViewText(R.id.tv_process, tips);
			remoteViews.setProgressBar(R.id.pb_download, 100, progress, false);
			notification.contentView = remoteViews;	
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	@SuppressWarnings("deprecation")
	private void switchAskInstallNotification(String filePath) {
		try{
			if (notification != null) {
				mNotificationManager.cancel(AppConstants.UPGRADE_NOTIFICATION_ID);
				notification = null;
			}
			notification = new Notification(android.R.drawable.stat_sys_download_done,
					mContext.getString(R.string.notification_downloading_upgrade_complete), System.currentTimeMillis());
			notification.defaults = Notification.DEFAULT_LIGHTS;
			notification.flags = Notification.FLAG_AUTO_CANCEL;

			Intent notificationIntent = new Intent();
			notificationIntent.setAction(android.content.Intent.ACTION_VIEW);
			notificationIntent.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent contentItent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
			notification.setLatestEventInfo(mContext, null, null, contentItent);

			RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.upgrade_notification);
			remoteViews.setInt(R.id.downloading_bar, "setVisibility", View.GONE);
			remoteViews.setInt(R.id.downloaded_bar, "setVisibility", View.VISIBLE);
			notification.contentView = remoteViews;

			mNotificationManager.notify(AppConstants.UPGRADE_NOTIFICATION_ID, notification);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	private class UpgradeThread extends AsyncTask<Integer, Long, Integer> implements DownloadListener {

		@Override
		public void onDownloading(long progress) {
			publishProgress(progress);
		}

		@Override
		public void onDownloadComplete(String filePath) {
			updateNotificationProgress(100);
			String fileName = info.getApp_name() + "_version_" + info.getVersion_code() + ".apk";
			try {
				if (isNeedPatchAPK) {
					String newApkFilePath =  MXKit.getInstance().smartPatch(mContext, filePath, MXKit.getInstance().getKitConfiguration().getDownloadApkRoot() + fileName);
					deleteFile(filePath);
					String md5 = MD5Util.getFileMD5String(new File(newApkFilePath));
					if (md5.equalsIgnoreCase(info.getFingerprint())) {
						installNewApk(newApkFilePath);
						switchAskInstallNotification(newApkFilePath);
					} else {
						retryDownload();
					}
				} else {
					File downloadFile = new File(filePath);
					File newApkFile = new File(MXKit.getInstance().getKitConfiguration().getDownloadApkRoot() + fileName);
					downloadFile.renameTo(newApkFile);
					String md5 = MD5Util.getFileMD5String(newApkFile);
					if (md5.equalsIgnoreCase(info.getFingerprint())) {
						installNewApk(newApkFile.getAbsolutePath());
						switchAskInstallNotification(newApkFile.getAbsolutePath());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Throwable e) {
				retryDownload();
			}
		}

		@Override
		protected Integer doInBackground(Integer... integers) {
			return startToDownloadUpdateFile();
		}

		@Override
		protected void onProgressUpdate(Long... values) {
			updateNotificationProgress((int) (long) (values[0]));
		}

		@Override
		protected void onPostExecute(Integer message) {
			if (message == MESSAGE_DOWNLOAD_ERROR)
				showError(R.string.toast_download_error);
			else if (message == MESSAGE_SDCARD_ERROR)
				showError(R.string.toast_sdcard_not_existed);
		}
	}

	private void retryDownload() {
		if (isRetryDownload) {
			return;
		}
		isRetryDownload = true;
		((Activity) mContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showRetryDownloadDialog();
			}
		});
	}

	private void showRetryDownloadDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(String.format(mContext.getString(R.string.app_upgrade), info.getVersion()));

		builder.setMessage(R.string.notification_downloading_smart_error_message);
		builder.setPositiveButton(R.string.ok, new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				downloadURL = info.getUpgrade_url();
				isNeedPatchAPK = false;
				if (notification != null) {
					mNotificationManager.cancel(AppConstants.UPGRADE_NOTIFICATION_ID);
					notification = null;
				}
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse(downloadURL);
				intent.setData(content_url);
				intent.putExtra("direct", true);
				mContext.startActivity(intent);
				dialog.dismiss();
			}
		});

		if (!info.isMandatoryUpgrade()) {
			builder.setNegativeButton(R.string.cancel, new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					if (mNotificationManager != null) {
						mNotificationManager.cancel(AppConstants.UPGRADE_NOTIFICATION_ID);
					}
				}
			});
		}
		AlertDialog dialog = builder.create();
		dialog.setCancelable(!info.isMandatoryUpgrade());
		dialog.show();
	}

	private int startToDownloadUpdateFile() {
		try {
			downloader.startDownload(mContext, downloadURL, null);
			return MESSAGE_DOWNLOAD_COMPLETE;
		} catch (DownloadException e) {
			return MESSAGE_DOWNLOAD_ERROR;
		}
	}

	private void updateNotificationProgress(int progress) {
		try{
			if(notification == null){
				return;
			}
			setNotificationView(progress);
			mNotificationManager.notify(AppConstants.UPGRADE_NOTIFICATION_ID, notification);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	private void deleteFile(String path) {
		new File(path).delete();
	}

	private void installNewApk(String newApkFilePath) {
		if (mNotificationManager != null) {
			mNotificationManager.cancel(AppConstants.UPGRADE_NOTIFICATION_ID);
		}
		Utils.installApk(mContext, newApkFilePath);
	}
	
}
