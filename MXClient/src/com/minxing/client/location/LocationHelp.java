package com.minxing.client.location;


import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.minxing.client.location.callback.OnceLocationCallBack;

public class LocationHelp {

    private static LocationHelp help;


    public static LocationHelp getInstance(){
        if (help == null){
            help = new LocationHelp();
        }
        return help;
    }

    public void getLocationDetailOnce(Context context, final OnceLocationCallBack onceLocationCallBack){

        final AMapLocationClient locationClient = new AMapLocationClient(context);
        AMapLocationClientOption locationOption = new AMapLocationClientOption();
        // 设置定位模式改为网络定位
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置只请求一次，只有高精度定位的情况下，单次定位才有效
        //locationOption.setOnceLocation(true);
        //设置GPS优先
        //locationOption.setGpsFirst(true);

        locationOption.setInterval(2000);

        locationOption.setNeedAddress(false);
        //locationOption.setWifiActiveScan(true);
        locationOption.setLocationCacheEnable(true);

        //设置定位监听
        locationClient.setLocationListener(new AMapLocationListener() {

            @Override
            public void onLocationChanged(AMapLocation loc) {
                if(loc == null){
                    return;
                }
                onceLocationCallBack.onLocationChanged(loc);
                locationClient.unRegisterLocationListener(this);
                locationClient.stopLocation();
            }
        });
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }
}
