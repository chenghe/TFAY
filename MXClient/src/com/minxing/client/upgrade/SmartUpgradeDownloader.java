package com.minxing.client.upgrade;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.minxing.client.R;
import com.minxing.client.util.ResourceUtil;
import com.minxing.kit.MXKit;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

public class SmartUpgradeDownloader {
	private long fileSize;
	private File savePath;
	private String saveFileName;
	private File saveFile;
	private InputStream inputStream;
	private FileOutputStream fileOutputStream;
	private DownloadListener downloadListener;
	private long downFileSize;
	private long lastProgress;
	private boolean isCanProgress = true;
	private AppUpgradeInfo info;
	private Context mContext;

	public SmartUpgradeDownloader(Context context, AppUpgradeInfo info, File savePath, String saveFileName, DownloadListener downloadListener) {
		this.info = info;
		this.savePath = savePath;
		this.saveFileName = saveFileName;
		this.downloadListener = downloadListener;
		this.mContext = context;
	}

	public void startDownload(Context context, String url, String cookie) throws DownloadException {
		if (!isMountedSdCard())
			throw new DownloadException(context.getString(R.string.download_error_no_sdcard));

		try {
			downloadProcessing(url, cookie);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new DownloadException(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new DownloadException(e.getMessage());
		}
	}

	private void downloadProcessing(String url, String cookie) throws IOException, DownloadException {
		createDownloadRelative(url, cookie);
		writeAndDownloadFile();
		closeDownloadRelative();
	}

	private void createDownloadRelative(String url, String cookie) throws IOException, DownloadException {
		HttpURLConnection conn = buildHttpUrlConnection(url, cookie);
		if (!isConnectSuccess(conn))
			throw new IOException();
		saveFile = new File(savePath, saveFileName);
		if (saveFile.exists()) {
			saveFile.delete();
		} else {
			saveFile.createNewFile();
		}

		fileSize = conn.getContentLength();
		if (fileSize > 0) {
			isCanProgress = true;
		} else {
			fileSize = 0;
			isCanProgress = false;
		}

		inputStream = conn.getInputStream();
		fileOutputStream = new FileOutputStream(saveFile);
	}

	private void writeAndDownloadFile() throws IOException {
		byte[] buffer = new byte[1024];
		int i = 0;
		lastProgress = -1;
		downFileSize = 0;
		while ((i = inputStream.read(buffer)) != -1) {
			fileOutputStream.write(buffer, 0, i);
			downFileSize = downFileSize + i;
			long progress = calculateProgress(downFileSize);
			transferProgressToListener(progress);
		}

		if (i == -1) {
			downloadListener.onDownloadComplete(saveFile.getPath());
		}
	}

	private void transferProgressToListener(long progress) {
		if (downloadListener == null)
			return;
		if (isProgressChange(progress)) {
			downloadListener.onDownloading(progress);
		}
	}

	private boolean isProgressChange(long progress) {
		if (lastProgress != progress) {
			lastProgress = progress;
			return true;
		}
		return false;
	}

	private long calculateProgress(long downFileSize) {
		if (isCanProgress) {
			return (long) (downFileSize * 100.0 / fileSize);
		} else if(info != null && info.getSize() > 0) {
			return (long) (downFileSize * 100.0 / info.getSize());
		}else{
			return fileSize + downFileSize;
		}
	}

	private void closeDownloadRelative() throws IOException {
		fileOutputStream.flush();
		fileOutputStream.close();
		inputStream.close();
	}

	private HttpURLConnection buildHttpUrlConnection(String urlString, String cookie) throws IOException {
		if(! TextUtils.isEmpty(urlString) && ! urlString.startsWith("http")){
			urlString = ResourceUtil.getConfString(mContext, "client_conf_http_host") + urlString;
		}
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		if (cookie != null && !"".equals(cookie)) {
			conn.setRequestProperty("Cookie", cookie);
		}

		if (urlString.startsWith("https")) {
			try {
				TrustManager[] tm = { new DefaultX509TrustManager() };
				SSLContext sslContext = SSLContext.getInstance("TLS");
				sslContext.init(null, tm, null);
				HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		addMXUseAgent(conn);
		return conn;
	}

	private void addMXUseAgent(HttpURLConnection conn) {
		String userAgent = MXKit.getInstance().getUseragent();
		conn.setRequestProperty("User-Agent", userAgent);
	}

	private boolean isConnectSuccess(HttpURLConnection conn) throws IOException {
		return conn.getResponseCode() == HttpURLConnection.HTTP_OK;
	}

	private boolean isMountedSdCard() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	public interface DownloadListener {
		public void onDownloading(long progress);

		public void onDownloadComplete(String filePath);
	}

	class DefaultX509TrustManager implements X509TrustManager {
		X509TrustManager myJSSEX509TrustManager;

		public DefaultX509TrustManager() throws Exception {
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			// ks.load(new FileInputStream("trustedCerts"),
			// "passphrase".toCharArray()); //---->
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
			tmf.init(ks);
			TrustManager tms[] = tmf.getTrustManagers();
			for (int i = 0; i < tms.length; i++) {
				if (tms[i] instanceof X509TrustManager) {
					myJSSEX509TrustManager = (X509TrustManager) tms[i];
					return;
				}
			}
		}

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}
}
