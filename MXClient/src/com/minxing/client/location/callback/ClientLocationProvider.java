package com.minxing.client.location.callback;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.minxing.client.location.LocationHelp;
import com.minxing.kit.api.bean.ScheduleUpLoadPO;
import com.minxing.kit.api.callback.MXCommonCallBack;
import com.minxing.kit.api.callback.MXLocationProvider;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;


public class ClientLocationProvider implements MXLocationProvider {

    private Context context;

    public ClientLocationProvider(Context context){
        this.context = context;
    }
    @Override
    public void getLocationContent(final MXCommonCallBack mxCommonCallBack) {
        LocationHelp.getInstance().getLocationDetailOnce(context, new OnceLocationCallBack() {
            @Override
            public void onLocationChanged(AMapLocation loc) {
                if(loc == null){
                    return;
                }
                if(loc.getErrorCode() == 0){
                    //定位成功
                    ScheduleUpLoadPO scheduleUpLoadPO = new ScheduleUpLoadPO();
                    List<NameValuePair> param = new ArrayList<NameValuePair>();
                    param.add(new BasicNameValuePair("longitude", String.valueOf(loc.getLongitude())));
                    param.add(new BasicNameValuePair("latitude", String.valueOf(loc.getLatitude())));
                    scheduleUpLoadPO.setParams(param);

                    mxCommonCallBack.onSuccess(scheduleUpLoadPO);
                }else {
                    //定位失败
                    mxCommonCallBack.mxError(loc.getErrorInfo());
                }
            }
        });
    }
}
