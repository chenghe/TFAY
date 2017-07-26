package com.minxing.client.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSONObject;
import com.minxing.client.AppConstants;
import com.minxing.client.util.PreferenceUtils;
import com.minxing.client.util.ResourceUtil;
import com.minxing.kit.MXConstants;
import com.minxing.kit.MXKit;

import java.util.ArrayList;
import java.util.List;

public class MXKitPushReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		if (intent.getAction().equals(MXConstants.BroadcastAction.MXKIT_OUTSIDE_PUSH_MESSAGE)) {
			final String pushData = intent.getStringExtra(MXConstants.IntentKey.MXKIT_OUTSIDE_PUSH_MESSAGE_KEY);
			if (pushData != null && !"".equals(pushData)) {
				JSONObject data = null;
				// 判断是否为JSON数据格式
				try {
					data = JSONObject.parseObject(pushData);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// JSON数据格式处理，此处处理的是敏行自己的升级推送，第三方用户如果自行实现升级，可忽略删除此部分代码。
				if (data != null) {
					String event = data.getString("event");
					if (event != null && !"".equals(event) && "upgrade".equals(event)) {
						int versonCode = data.getIntValue("version_code");
						int currentVersionCode = ResourceUtil.getVerCode(context);
						if (versonCode > currentVersionCode) {
							PreferenceUtils.saveUpgradeMark(context);
						} else {
							PreferenceUtils.clearUpgradeMark(context);
						}
						Intent refresh = new Intent();
						refresh.setAction(AppConstants.MXCLIENT_REFRESH_NEW_VERSION);
						context.sendBroadcast(refresh);
					}

					String pushType = data.getString("pushType");
					List<String> pushTypeLists = new ArrayList<String>();
					for (int i = 0; i <= 11; i++) {
						String type = Integer.toString(i);
						pushTypeLists.add(type);
					}
					if (pushType != null && pushTypeLists.contains(pushType)) {
						final Intent receiveIntent = new Intent();
						receiveIntent.setAction(AppConstants.MXCLIENT_MODE_VIDEO_ACCEPT);
						receiveIntent.putExtra(AppConstants.CALL_RECEIVE_DATA, pushData);
						context.sendBroadcast(receiveIntent, MXKit.getInstance().getAppSignaturePermission());
					}

				} else {
					// 非JSON数据格式处理
				}

			}
		}

	}

}
