package com.minxing.client.plugin.web.zsmarter.zsimei;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hechengbin on 2017/5/25.
 */

public class ZSImei extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.i("hcb","ZSImei");
        if(action.equals("GetImei")){
            String imei = ((TelephonyManager) cordova.getActivity().getSystemService(Context.TELEPHONY_SERVICE))
                    .getDeviceId();

            Log.i("hcb",imei);
            callbackContext.success(new JSONObject().put("imei",imei).toString());
            return true;
        }

        return false;
    }
}
