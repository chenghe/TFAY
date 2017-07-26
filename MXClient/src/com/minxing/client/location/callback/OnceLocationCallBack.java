package com.minxing.client.location.callback;


import com.amap.api.location.AMapLocation;

import java.io.Serializable;

public interface OnceLocationCallBack extends Serializable{

    void onLocationChanged(AMapLocation loc);
}
