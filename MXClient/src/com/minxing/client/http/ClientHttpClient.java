package com.minxing.client.http;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.minxing.client.R;
import com.minxing.client.core.ServiceError;
import com.minxing.client.util.ResourceUtil;
import com.minxing.client.util.Utils;
import com.minxing.kit.MXKit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

@SuppressWarnings("deprecation")
public class ClientHttpClient {
	int errorCode = 0;
	Context mContext;

	public Object request(final Context context, final HttpRequestParams httpRequestParmas) throws Exception {
		this.mContext = context;

		if (mContext != null && !Utils.checkConnectState(mContext)) {
			ServiceError error = new ServiceError();
			error.setErrors(-1);
			error.setMessage(mContext.getString(R.string.error_no_network));
			return error;
		}

		String json = requestServer(httpRequestParmas);

		Class<?> clazz = httpRequestParmas.getWbinterface().getClazz();
		ServiceError error = new ServiceError();

		if (errorCode >= 400) {
			error.setErrors(errorCode);
			if (json != null && json.trim().length() != 0) {
				try {
					JSONObject errroObj = new JSONObject(json);
					JSONObject errorsJSON = errroObj.optJSONObject("errors");
					String message = errorsJSON.optString("message");
					error.setMessage(message);
				} catch (Exception e) {
					error.setMessage(mContext.getString(R.string.server_error));
					e.printStackTrace();
				}
			} else {
				error.setMessage(mContext.getString(R.string.server_error));
			}
			return error;
		} else {
			return JSON.parseObject(json, clazz);
		}
	}

	private String requestServer(HttpRequestParams httpRequestParams) throws Exception {

		HttpMethod requestType = httpRequestParams.getRequestType();
		TreeMap<String, String> headers = httpRequestParams.getHeaders();
		List<NameValuePair> params = httpRequestParams.getParams();
		Interface httpInterface = httpRequestParams.getWbinterface();
		String formatFace =  httpInterface.getFormatFace();
		String url;
		if(! TextUtils.isEmpty(formatFace) && ! formatFace.startsWith("http")){
			url = ResourceUtil.getConfString(mContext, "client_conf_http_host") + httpInterface.getFormatFace();
		}else{
			url = httpInterface.getFormatFace();
		}

		HttpUriRequest request = null;
		String json = null;
		HttpClient httpClient = getNewHttpClient(url);

		if (requestType == HttpMethod.GET) {
			if (params != null) {
				StringBuilder stringBuilder;
				if(! TextUtils.isEmpty(url) && ! url.contains("?")){
					stringBuilder = new StringBuilder(url + "?");
				}else{
					stringBuilder = new StringBuilder(url + "&");
				}

				for (NameValuePair nameValuePair : params) {
					String name = nameValuePair.getName();
					String value = nameValuePair.getValue();
					stringBuilder.append(name + "=" + value + "&");
				}
				url = stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
			}
			request = new HttpGet(url);
		}

		if (requestType == HttpMethod.POST) {
			request = new HttpPost(url);
		}

		if (requestType == HttpMethod.DELETE) {
			request = new HttpDelete(url);
		}

		if (requestType == HttpMethod.PUT) {
			request = new HttpPut(url);
		}

		if (headers != null && headers.size() > 0) {
			Set<Map.Entry<String, String>> h = headers.entrySet();
			for (Iterator<Map.Entry<String, String>> it = h.iterator(); it.hasNext();) {
				Map.Entry<String, String> me = it.next();
				String key = me.getKey();
				String value = me.getValue();
				request.addHeader(key, value);
			}
		}

		if (requestType == HttpMethod.POST || requestType == HttpMethod.PUT) {
			HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			if (requestType == HttpMethod.POST) {
				((HttpPost) request).setEntity(entity);
			}
			if (requestType == HttpMethod.PUT) {
				((HttpPut) request).setEntity(entity);
			}
		}

		Log.e("ClientHttpClient", "url=" + url);
		HttpResponse response = httpClient.execute(request);
		errorCode = response.getStatusLine().getStatusCode();
		Log.v("ClientHttpClient", "code=" + errorCode);

		InputStream is = response.getEntity().getContent();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		byte[] buf = new byte[1024 * 8];
		int len = -1;

		while ((len = is.read(buf)) != -1) {
			baos.write(buf, 0, len);
		}

		json = new String(baos.toByteArray());

		try {
			is.close();
			baos.close();
			buf = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.v(httpInterface.getInsType(), "json=" + json);
		return json;
	}

	public HttpClient getNewHttpClient(String url) {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new ClientSSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams httpParams = new BasicHttpParams();
			HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
			HttpConnectionParams.setConnectionTimeout(httpParams, 30 * 1000);
			HttpConnectionParams.setSoTimeout(httpParams, 30 * 1000);
			HttpConnectionParams.setSocketBufferSize(httpParams, 1024 * 8);
			HttpClientParams.setRedirecting(httpParams, true);

			Uri uri = Uri.parse(url);
			int port = uri.getPort();

			if (port == -1){
				if(url.startsWith("https")){
					port = 443;
				}else{
					port = 80;
				}

			}

			addMXUseAgent(httpParams);
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), port));
			schemeRegistry.register(new Scheme("https", sf, port));

			ClientConnectionManager connManager = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
			HttpClient httpClient = new DefaultHttpClient(connManager, httpParams);
			return httpClient;
		} catch (Exception e) {
			HttpParams httpParams = new BasicHttpParams();
			addMXUseAgent(httpParams);
			return new DefaultHttpClient(httpParams);
		}
	}

	private void addMXUseAgent(HttpParams httpParams){
		String userAgent = MXKit.getInstance().getUseragent();
		httpParams.setParameter(CoreProtocolPNames.USER_AGENT, userAgent);
	}
}
