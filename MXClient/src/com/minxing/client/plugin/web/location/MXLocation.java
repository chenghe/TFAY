package com.minxing.client.plugin.web.location;

import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.minxing.client.location.LocationHelp;
import com.minxing.client.location.callback.OnceLocationCallBack;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MXLocation extends CordovaPlugin {

	@Override
	public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

		if (TextUtils.isEmpty(action)) {
			return false;
		}

		if(action.equals("getLocation")){
			Log.i("hcb","action.equals()");
			LocationHelp.getInstance().getLocationDetailOnce(cordova.getActivity().getBaseContext(), new OnceLocationCallBack() {
				@Override
				public void onLocationChanged(AMapLocation loc) {
					if(loc == null){
						return;
					}
					if(loc.getErrorCode() == 0){
						//定位成功
						JSONObject jo = new JSONObject();
						try {
							jo.put("longitude", loc.getLongitude());
							jo.put("latitude", loc.getLatitude());
							jo.put("accuracy", loc.getAccuracy());
							if (loc.getProvider().equalsIgnoreCase(
									android.location.LocationManager.GPS_PROVIDER)){
								jo.put("speed", loc.getSpeed());
							}
						} catch (Exception e) {
						}

						callbackContext.success(jo.toString());
					}else if (loc.getErrorCode() == 7){
						//KEY鉴权失败,请仔细检查key绑定的sha1值与apk签名sha1值是否对应
						Log.e("MXLocation","KEY鉴权失败,请仔细检查key绑定的sha1值与apk签名sha1值是否对应");
					}else {
						//定位失败
//							Log.e("TAG", "=======fail===========");
					}
				}
			});

			return true;
		}

		return false;

	}


}
