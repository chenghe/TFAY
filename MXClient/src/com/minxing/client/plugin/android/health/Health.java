package com.minxing.client.plugin.android.health;


import android.content.Context;

import com.minxing.kit.api.bean.MXAppInfo;
import com.minxing.kit.health.MXHealth;
import com.minxing.kit.plugin.android.MXPlugin;

public class Health extends MXPlugin {

    @Override
    public void start(Context context, MXAppInfo app, String params, String extParams) {
        super.start(context, app, params, extParams);

        MXHealth.getInstance().startActivity(context,app,extParams);

    }


    @Override
    public boolean handleSettingLauncher(Context context, MXAppInfo app,int conversationId) {
        String extParams = MXHealth.OPEN_PATH + "=" + MXHealth.OPEN_SETTING + "&" + MXHealth.SETTING_PARAM + "=" + conversationId;
        MXHealth.getInstance().startActivity(context,app,extParams);
        return true;
    }


    @Override
    public void uninstall(Context context, MXAppInfo app) {
        super.uninstall(context, app);
        MXHealth.getInstance().unInstallApp(context,app);

    }

    @Override
    public void init(Context context) {
        super.init(context);

        MXHealth.getInstance().startSensorService(context);
    }
}
