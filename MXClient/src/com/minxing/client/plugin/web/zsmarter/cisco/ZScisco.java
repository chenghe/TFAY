package com.minxing.client.plugin.web.zsmarter.cisco;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.minxing.client.CiscoActivity;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hechengbin on 2017/5/25.
 */

public class ZScisco extends CordovaPlugin {

    private CallbackContext callbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.i("hcb","ZSImei");
        this.callbackContext = callbackContext;
        JSONObject obj = (JSONObject) args.get(0);
        String server = (String) obj.get("server");
        String address = (String)obj.get("address");
        String dtmf = (String)obj.get("dtmf");

        if (!TextUtils.isEmpty(server) && !TextUtils.isEmpty(address)){
            Intent intent = new Intent(cordova.getActivity(), CiscoActivity.class);
            intent.putExtra(CiscoActivity.CISCO_SERVER,server);
            intent.putExtra(CiscoActivity.CISCO_ADDRESS,address);
            cordova.getActivity().startActivityForResult(intent,0);
        }

        callbackContext.success();

//        Toast.makeText(cordova.getActivity(),"server:"+server+"address:"+address+"dtmf:"+dtmf,Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onActivityResult(int i, int i1, Intent intent) {
        Log.i("hcb","onActivityResult");
//        callbackContext.success();
    }
}
