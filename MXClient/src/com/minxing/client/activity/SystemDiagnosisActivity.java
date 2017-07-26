package com.minxing.client.activity;

import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.minxing.client.R;
import com.minxing.client.RootActivity;
import com.minxing.client.core.ServiceError;
import com.minxing.client.http.HttpCallBack;
import com.minxing.client.http.HttpClientAsync;
import com.minxing.client.http.HttpMethod;
import com.minxing.client.http.HttpRequestParams;
import com.minxing.client.http.Interface;
import com.minxing.client.service.ViewCallBack;
import com.minxing.kit.MXKit;

public class SystemDiagnosisActivity extends RootActivity {

	private TextView diagosis_http_server_tv = null;
	private EditText diagosis_http_server_et = null;
	private LinearLayout diagosis_http_server_btn_layout = null;
	private TextView diagosis_http_server_btn = null;

	private TextView diagosis_push_server_tv = null;
	private EditText diagosis_push_server_et = null;
	private LinearLayout diagosis_push_server_btn_layout = null;
	private TextView diagosis_push_server_btn = null;

	private LinearLayout diagnosis_btn_layout = null;

	private LinearLayout diagosis_http_server_check = null;
	private ProgressBar diagosis_http_server_check_loading = null;
	private TextView diagosis_http_server_check_result = null;
	private TextView diagosis_http_server_check_detail = null;

	private LinearLayout diagosis_push_server_check = null;
	private ProgressBar diagosis_push_server_check_loading = null;
	private TextView diagosis_push_server_check_result = null;
	private TextView diagosis_push_server_check_detail = null;

	private ImageButton backButton = null;
	private boolean isBtnDisable = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_diagnosis);

		((TextView) findViewById(R.id.title_name)).setText(R.string.diagosis_title);
		backButton = (ImageButton) findViewById(R.id.title_left_button);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finishWithAnimation();
			}
		});

		diagosis_http_server_tv = (TextView) findViewById(R.id.diagosis_http_server_tv);
		diagosis_http_server_et = (EditText) findViewById(R.id.diagosis_http_server_et);
		diagosis_http_server_btn_layout = (LinearLayout) findViewById(R.id.diagosis_http_server_btn_layout);
		diagosis_http_server_btn = (TextView) findViewById(R.id.diagosis_http_server_btn);

		diagosis_push_server_tv = (TextView) findViewById(R.id.diagosis_push_server_tv);
		diagosis_push_server_et = (EditText) findViewById(R.id.diagosis_push_server_et);
		diagosis_push_server_btn_layout = (LinearLayout) findViewById(R.id.diagosis_push_server_btn_layout);
		diagosis_push_server_btn = (TextView) findViewById(R.id.diagosis_push_server_btn);

		diagosis_http_server_check = (LinearLayout) findViewById(R.id.diagosis_http_server_check);
		diagosis_http_server_check_loading = (ProgressBar) findViewById(R.id.diagosis_http_server_check_loading);
		diagosis_http_server_check_result = (TextView) findViewById(R.id.diagosis_http_server_check_result);
		diagosis_http_server_check_detail = (TextView) findViewById(R.id.diagosis_http_server_check_detail);

		diagosis_push_server_check = (LinearLayout) findViewById(R.id.diagosis_push_server_check);
		diagosis_push_server_check_loading = (ProgressBar) findViewById(R.id.diagosis_push_server_check_loading);
		diagosis_push_server_check_result = (TextView) findViewById(R.id.diagosis_push_server_check_result);
		diagosis_push_server_check_detail = (TextView) findViewById(R.id.diagosis_push_server_check_detail);

		resetDefaultView();
		diagnosis_btn_layout = (LinearLayout) findViewById(R.id.diagnosis_btn_layout);
		diagnosis_btn_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isBtnDisable) {
					return;
				}
				resetDefaultView();
				isBtnDisable = true;
				doDiagnosis();
			}
		});
		
		refreshServerConfigData();
	}

	private void refreshServerConfigData() {
		diagosis_http_server_tv.setText(MXKit.getInstance().getKitConfiguration().getServerHost());
		diagosis_http_server_et.setText(MXKit.getInstance().getKitConfiguration().getServerHost());
		if (diagosis_http_server_tv.getVisibility() == View.VISIBLE) {
			diagosis_http_server_btn.setText("Change Http Server");
		} else {
			diagosis_http_server_btn.setText("Done");
		}
		diagosis_http_server_btn_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (diagosis_http_server_tv.getVisibility() == View.VISIBLE) {
					diagosis_http_server_tv.setVisibility(View.GONE);
					diagosis_http_server_et.setVisibility(View.VISIBLE);
				} else {
					String newHttpServerConfig = diagosis_http_server_et.getText().toString().trim();
					if (newHttpServerConfig != null && !"".equals(newHttpServerConfig)) {
						diagosis_http_server_tv.setVisibility(View.VISIBLE);
						diagosis_http_server_et.setVisibility(View.GONE);
						
						MXKit.getInstance().getKitConfiguration().switchHttpServerConfig(SystemDiagnosisActivity.this, newHttpServerConfig);
					}
				}
				refreshServerConfigData();
			}
		});

		diagosis_push_server_tv.setText(MXKit.getInstance().getKitConfiguration().getPushHost());
		diagosis_push_server_et.setText(MXKit.getInstance().getKitConfiguration().getPushHost());
		if (diagosis_push_server_tv.getVisibility() == View.VISIBLE) {
			diagosis_push_server_btn.setText("Change Push Server");
		} else {
			diagosis_push_server_btn.setText("Done");
		}
		diagosis_push_server_btn_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (diagosis_push_server_tv.getVisibility() == View.VISIBLE) {
					diagosis_push_server_tv.setVisibility(View.GONE);
					diagosis_push_server_et.setVisibility(View.VISIBLE);
				} else {
					String newPushServerConfig = diagosis_push_server_et.getText().toString().trim();
					if (newPushServerConfig != null && !"".equals(newPushServerConfig)) {
						diagosis_push_server_tv.setVisibility(View.VISIBLE);
						diagosis_push_server_et.setVisibility(View.GONE);
						
						MXKit.getInstance().getKitConfiguration().switchPushServerConfig(SystemDiagnosisActivity.this, newPushServerConfig);
					}
				}
				refreshServerConfigData();
			}
		});
	}

	private void resetDefaultView() {
		diagosis_http_server_check.setVisibility(View.GONE);
		diagosis_http_server_check_loading.setVisibility(View.VISIBLE);
		diagosis_http_server_check_result.setVisibility(View.GONE);
		diagosis_http_server_check_detail.setVisibility(View.GONE);

		diagosis_push_server_check.setVisibility(View.GONE);
		diagosis_push_server_check_loading.setVisibility(View.VISIBLE);
		diagosis_push_server_check_result.setVisibility(View.GONE);
		diagosis_push_server_check_detail.setVisibility(View.GONE);
	}

	private void doDiagnosis() {
		diagosis_http_server_check.setVisibility(View.VISIBLE);

		boolean isNetworkConnect = false;
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeInfo = manager.getActiveNetworkInfo();
		if (activeInfo != null) {
			isNetworkConnect = activeInfo.isConnected();
		}

		if (isNetworkConnect) {
			pingServerForHttpDiagnosis(new ViewCallBack(this) {
				@Override
				public void success(Object obj) {
					diagosis_http_server_check_loading.setVisibility(View.GONE);
					diagosis_http_server_check_result.setVisibility(View.VISIBLE);
					diagosis_http_server_check_result.setText(getString(R.string.diagosis_server_check_success));
					diagnosisPushSocket();
				}

				@Override
				public void failure(ServiceError error) {
					diagosis_http_server_check_loading.setVisibility(View.GONE);
					diagosis_http_server_check_result.setVisibility(View.VISIBLE);
					diagosis_http_server_check_detail.setVisibility(View.VISIBLE);
					if (error != null) {
						int code = error.getErrors();
						if (code > 0) {
							if (code == 401) {
								diagosis_http_server_check_result.setText(getString(R.string.diagosis_server_check_success));
								diagnosisPushSocket();
							} else {
								diagosis_http_server_check_result.setText(getString(R.string.diagosis_server_check_fail));
								if (code == 404) {
									diagosis_http_server_check_detail.setText(getString(R.string.diagosis_error_server_404) + "\r\n"
											+ error.getMessage());
								} else {
									diagosis_http_server_check_detail
											.setText(getString(R.string.diagosis_error_server) + "\r\n" + error.getMessage());
								}
							}
						} else {
							diagosis_http_server_check_result.setText(getString(R.string.diagosis_server_check_fail));
							if (code == -1) {
								diagosis_http_server_check_detail.setText(getString(R.string.diagosis_error_host));
							} else {
								diagosis_http_server_check_detail.setText(getString(R.string.diagosis_error));
							}
						}
					}
				}
			});
		} else {
			diagosis_http_server_check_loading.setVisibility(View.GONE);
			diagosis_http_server_check_result.setVisibility(View.VISIBLE);
			diagosis_http_server_check_result.setText(getString(R.string.diagosis_server_check_fail));
			diagosis_http_server_check_detail.setVisibility(View.VISIBLE);
			diagosis_http_server_check_detail.setText(getString(R.string.diagosis_cellphone_error_network));
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finishWithAnimation();
		}
		return false;
	}

	private void pingServerForHttpDiagnosis(ViewCallBack viewCallBack) {
		HttpRequestParams hrp = new HttpRequestParams();
		hrp.setHeaders(null);
		hrp.setParams(null);
		hrp.setRequestType(HttpMethod.GET);
		hrp.setWbinterface(Interface.PING);

		HttpCallBack callback = new HttpCallBack() {
			@Override
			public void success(Object obj) {
				this.mCallBack.success(obj);
			}

			@Override
			public void failure(ServiceError error) {
				this.mCallBack.failure(error);
			}
		};
		callback.setViewCallBack(viewCallBack);
		new HttpClientAsync(callback).execute(hrp);
	}

	private void diagnosisPushSocket() {
		diagosis_push_server_check.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					TrustManager[] tm = { new DefaultX509TrustManager() };
					SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
					sslContext.init(null, tm, new SecureRandom());
					// 生成SSLSocket
					String pushHost = MXKit.getInstance().getKitConfiguration().getPushHost();
					if (pushHost != null && !"".equals(pushHost)) {
						pushHost = pushHost.replaceAll("ssl://", "");
						String[] pushIPPort = pushHost.split(":");
						if (pushIPPort.length == 2) {
							SSLSocket socket = (SSLSocket) sslContext.getSocketFactory().createSocket(pushIPPort[0], Integer.parseInt(pushIPPort[1]));
							if (socket != null && socket.isConnected()) {
								final MqttClient client = new MqttClient(MXKit.getInstance().getKitConfiguration().getPushHost(),
										"mx_push_diagnosis", new MemoryPersistence());
								final MqttConnectOptions options = new MqttConnectOptions();
								options.setCleanSession(false);
								options.setUserName("root");
								options.setPassword("minxing123".toCharArray());

								TrustManager[] mqttTM = { new DefaultX509TrustManager() };
								SSLContext mqttSslContext = SSLContext.getInstance("TLSv1.2");
								mqttSslContext.init(null, mqttTM, new SecureRandom());
								options.setSocketFactory(mqttSslContext.getSocketFactory());

								client.setCallback(new MqttCallback() {
									@Override
									public void connectionLost(Throwable cause) {
										// 连接丢失后，一般在这里面进行重连
										if (client != null && client.isConnected()) {
											try {
												client.disconnect();
											} catch (MqttException e) {
												e.printStackTrace();
											}
										}
										refreshPushCheckError(getString(R.string.diagosis_error_mqtt_disconnect) + "\r\n" + cause.getMessage());
									}

									@Override
									public void deliveryComplete(IMqttDeliveryToken token) {
									}

									@Override
									public void messageArrived(String topicName, MqttMessage message) throws Exception {
									}
								});

								try {
									if (client != null) {
										if (client.isConnected()) {
											return;
										}
										client.connect(options);
										runOnUiThread(new Runnable() {
											public void run() {
												diagosis_push_server_check_loading.setVisibility(View.GONE);
												diagosis_push_server_check_result.setVisibility(View.VISIBLE);
												diagosis_push_server_check_result.setText(getString(R.string.diagosis_server_check_success));
											}
										});

									}
								} catch (Exception e) {
									e.printStackTrace();
									refreshPushCheckError(getString(R.string.diagosis_error_mqtt) + "\r\n" + e.getMessage());
								} catch (Throwable e) {
									e.printStackTrace();
									refreshPushCheckError(getString(R.string.diagosis_error_mqtt) + "\r\n" + e.getMessage());
								}
							} else {
								refreshPushCheckError(getString(R.string.diagosis_error_wrong_ssl));
							}
						} else {
							refreshPushCheckError(getString(R.string.diagosis_error_push_host_wrong));
						}
					} else {
						refreshPushCheckError(getString(R.string.diagosis_error_push_host_wrong));
					}
				} catch (Exception e) {
					refreshPushCheckError(e.getMessage());
				}
			}
		}).start();
	}

	private void refreshPushCheckError(final String errorMessage) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				diagosis_push_server_check_loading.setVisibility(View.GONE);
				diagosis_push_server_check_result.setVisibility(View.VISIBLE);
				diagosis_push_server_check_result.setText(getString(R.string.diagosis_server_check_fail));
				diagosis_push_server_check_detail.setVisibility(View.VISIBLE);
				diagosis_push_server_check_detail.setText(errorMessage);
			}
		});
	}

	private class DefaultX509TrustManager implements X509TrustManager {
		@SuppressWarnings("unused")
		X509TrustManager myJSSEX509TrustManager;

		public DefaultX509TrustManager() throws Exception {
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
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