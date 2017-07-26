package com.minxing.client.http;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONException;

import com.minxing.client.R;
import com.minxing.client.core.BaseCallBack;
import com.minxing.client.core.ServiceError;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

@SuppressWarnings("deprecation")
public class HttpClientAsync extends AsyncTask<HttpRequestParams, Void, Object> {
	private BaseCallBack mCallBack;
	private boolean isOk = false;
	private ProgressDialog mProgressDialog = null;
	private Context mContext;

	private static ExecutorService scheduledTaskExecutor = Executors.newScheduledThreadPool(5);

	public HttpClientAsync(BaseCallBack callBack) {
		this.mCallBack = callBack;
		this.mContext = callBack.getContext();
	}

	 public  void  execute(HttpRequestParams  hrp) {
		 if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			    executeOnExecutor(scheduledTaskExecutor, hrp);
			} else {
			   super. execute(hrp);
			}
	    }
	 
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (mCallBack.isShowProgressDialog) {
			HttpClientAsync.this.mProgressDialog = ProgressDialog.show(mCallBack.mContext, mCallBack.dialogTitle, mCallBack.dialogContent, true); // 如果已经设置了ProgressDialog则显示
		}
	}

	@Override
	public Object doInBackground(HttpRequestParams... params) {
		HttpRequestParams httpRequestParam = params[0];

		ClientHttpClient client = new ClientHttpClient();
		Object obj = null;
		try {
			try {
				obj = client.request(mContext, httpRequestParam);
				System.out.println("obj:" + obj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (HttpHostConnectException e) {
			e.printStackTrace();
			ServiceError error = new ServiceError();
			error.setErrors(-1);
			error.setMessage(mContext.getString(R.string.error_no_network));
			obj = error;
		} catch (Exception e) {
			e.printStackTrace();
			ServiceError error = new ServiceError();
			error.setErrors(-2);
			error.setMessage(mContext.getString(R.string.http_error));
			obj = error;
		}

		if (obj instanceof ServiceError) {
			isOk = false;
		} else {
			isOk = true;
		}
		return obj;
	}

	@Override
	public void onPostExecute(Object obj) {
		super.onPostExecute(obj);
		if (mCallBack == null) {
			return;
		}

		// 进度对话框已经不在，说明用户取消了该请求
		if (mCallBack.isShowProgressDialog) {
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
				ifCallBack(obj);
				return;
			}
		}
		ifCallBack(obj);
	}

	public void ifCallBack(Object obj) {
		if (isOk) {
			mCallBack.success(obj);
		} else {
			mCallBack.failure((ServiceError) obj);
		}
	}
}