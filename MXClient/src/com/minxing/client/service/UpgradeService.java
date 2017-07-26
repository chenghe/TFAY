package com.minxing.client.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSONObject;
import com.minxing.client.AppConstants;
import com.minxing.client.core.ServiceError;
import com.minxing.client.http.HttpCallBack;
import com.minxing.client.http.HttpClientAsync;
import com.minxing.client.http.HttpMethod;
import com.minxing.client.http.HttpRequestParams;
import com.minxing.client.http.Interface;
import com.minxing.client.upgrade.AppUpgradeInfo;
import com.minxing.client.util.PreferenceUtils;
import com.minxing.client.util.ResourceUtil;
import com.minxing.client.util.Utils;
import com.minxing.kit.MXKit;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

@SuppressWarnings("deprecation")
public class UpgradeService {
	
	public void checkUpgrade(Context context, String clientId, final int currentVersionCode, final boolean isNeedBroad, final ViewCallBack viewCallBack) {
		HttpRequestParams hrp = new HttpRequestParams();
		hrp.setHeaders(null);
		hrp.setRequestType(HttpMethod.GET);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("client_id", clientId));
		params.add(new BasicNameValuePair("client_version_code", String.valueOf(currentVersionCode)));
		hrp.setParams(params);
		hrp.setWbinterface(getUpgradeInterface(context));

		HttpCallBack callback = new HttpCallBack() {
			@Override
			public void success(Object obj) {
				AppUpgradeInfo result = new AppUpgradeInfo();
				if (obj != null) {
					JSONObject json = (JSONObject) obj;
					int versonCode = json.getIntValue("version_code");
					if (versonCode > currentVersionCode) {
						result.setNeedUpgrade(true);
						result.setApp_name(json.getString("app_name"));
						result.setUpdateTime(Utils.iso8601ToCustomerDate(json.getString("created_at"), "yyyy-M-d"));
						result.setSize(json.getLongValue("size"));
						result.setFingerprint(json.getString("fingerprint"));
						result.setVersion(json.getString("version"));
						result.setVersion_code(versonCode);
						result.setDescription(json.getString("description"));
						result.setUpgrade_url(json.getString("upgrade_url"));
						result.setMandatoryUpgrade(json.getBooleanValue("mandatory_upgrade"));

						if (json.getString("smart_url") != null && !"".equals(json.getString("smart_url"))) {
							result.setSmart_url(json.getString("smart_url"));
							result.setSmart_size(json.getLongValue("smart_size"));
							result.setSmartUpgrade(true);
						}
						PreferenceUtils.saveUpgradeMark(mContext);
					} else {
						PreferenceUtils.clearUpgradeMark(mContext);
					}
					Intent refresh = new Intent();
					refresh.setAction(AppConstants.MXCLIENT_REFRESH_NEW_VERSION);
					if(isNeedBroad){
						refresh.putExtra(AppConstants.MXCLIENT_UPGRADE_INFO, result);
					}
					mContext.sendBroadcast(refresh, MXKit.getInstance().getAppSignaturePermission());
				}
				this.mCallBack.success(result);
			}

			@Override
			public void failure(ServiceError error) {
				this.mCallBack.failure(error);
			}
		};
		callback.setViewCallBack(viewCallBack);
		new HttpClientAsync(callback).execute(hrp);
	}
	
	public static Interface getUpgradeInterface(Context context){
		String customAppUpgradeUrl = ResourceUtil.getConfString(context, "custom_app_upgrade_url");
		Interface  upgradeInterface = Interface.UPGRADE;
		if(! TextUtils.isEmpty(customAppUpgradeUrl)){
			upgradeInterface.replaceInterface(customAppUpgradeUrl);
		}
		return upgradeInterface;
	}
}
