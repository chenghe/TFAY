package com.minxing.client.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import android.content.Context;

import com.minxing.kit.MXKit;
import com.nostra13.universalimageloader.core.assist.FlushedInputStream;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

public class HttpsImageDownloader extends BaseImageDownloader {
	public static Map<String, String> headers;

	public HttpsImageDownloader(Context context) {
		super(context);
	}

	public HttpsImageDownloader(Context context, int connectTimeout,
								int readTimeout) {
		super(context, connectTimeout, readTimeout);
	}

	@Override
	protected HttpURLConnection createConnection(String url, Object extra)
			throws IOException {

		HttpURLConnection conn = super.createConnection(url, extra);
		if (url.startsWith("https")) {
			try {
				TrustManager[] tm = { new DefaultX509TrustManager() };
				SSLContext sslContext = SSLContext.getInstance("TLS");
				sslContext.init(null, tm, null);
				HttpsURLConnection.setDefaultSSLSocketFactory(sslContext
						.getSocketFactory());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (headers != null) {
			for (Map.Entry<String, String> header : headers.entrySet()) {
				conn.setRequestProperty(header.getKey(), header.getValue());
			}
		}
		addMXUseAgent(conn);
		return conn;
	}

	@Override
	protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {

		URL url = null;
		try {
			url = new URL(imageUri);
		} catch (MalformedURLException e) {
		}
		HttpURLConnection http = null;

		if (Scheme.ofUri(imageUri) == Scheme.HTTPS) {
			trustAllHosts();
			HttpsURLConnection https = (HttpsURLConnection) url
					.openConnection();
			appendHeaders(https);
			https.setHostnameVerifier(DO_NOT_VERIFY);
			http = https;
			http.connect();
		} else {
			http = (HttpURLConnection) url.openConnection();
			appendHeaders(http);
		}

		http.setConnectTimeout(connectTimeout);
		http.setReadTimeout(readTimeout);
		return new FlushedInputStream(new BufferedInputStream(
				http.getInputStream()));
	}

	private void appendHeaders(HttpURLConnection conn) {
		if (headers != null) {
			for (Map.Entry<String, String> header : headers.entrySet()) {
				conn.setRequestProperty(header.getKey(), header.getValue());
			}
		}
		addMXUseAgent(conn);
	}

	private void addMXUseAgent(HttpURLConnection conn) {
		String userAgent = MXKit.getInstance().getUseragent();
		conn.setRequestProperty("User-Agent", userAgent);
	}

	// always verify the host - dont check for certificate
	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};


	/**
	 * Trust every server - dont check for any certificate
	 */
	private static void trustAllHosts() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			@Override
			public void checkClientTrusted(
					java.security.cert.X509Certificate[] x509Certificates,
					String s) throws java.security.cert.CertificateException {
			}

			@Override
			public void checkServerTrusted(
					java.security.cert.X509Certificate[] x509Certificates,
					String s) throws java.security.cert.CertificateException {
			}

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		public void checkClientTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}
}
