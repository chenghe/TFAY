package com.minxing.client.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.minxing.client.bean.ADBean;
import com.minxing.client.bean.ADCallBack;
import com.minxing.client.util.ResourceUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;

/**
 * Created by hechengbin on 2017/5/26.
 */

public class Adservice extends Service {

    public static final String SP_AD = "SP_AD";//广告SharedPreferences
    public static final String SP_AD_ADMD5 = "SP_AD_ADMD5";//广告 SharedPreferences picture md5

    private String httpurl;
    private String cachePicturePath;
    private String md5;
    private SharedPreferences sp;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("hcb","Adservice onStartCommand");

        cachePicturePath = Environment.getExternalStorageDirectory() + "/MXClient/Picture/";
        sp = getSharedPreferences(SP_AD, Context.MODE_PRIVATE);
        md5 = sp.getString(SP_AD_ADMD5,"");
        String baseAPPUrl = ResourceUtil.getConfString(getApplicationContext(), "client_app_store");
        httpurl = baseAPPUrl +"ad?&adType=startAD";
        Log.i("hcb","ad URL is "+ httpurl);

        OkHttpUtils.get()
                .url(httpurl)
                .addParams("md5Key", md5)
//                .addParams("adType", "startAD")
                .build()
                .execute(new ADCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i("hcb", e.toString());

                    }

                    @Override
                    public void onResponse(ADBean response, int id) {
                        String modify= response.getContext().getModified();
                        if (!TextUtils.isEmpty(modify)&&modify.equals("Y")){
                            md5 = response.getContext().getMd5Key();
                            Log.i("hcb", md5);
                            if (response.getContext().getAdImages()!=null&&
                                    response.getContext().getAdImages().size()>0){
                                String base64picture = "data:image/jpeg;base64,"+response.getContext().getAdImages().get(0);
                                Log.i("hcb", base64picture);
                                Bitmap bitmap = stringToBitmap(base64picture);
                                saveMyBitmap(bitmap, "tfayad", cachePicturePath);
                            }

                            sp.edit().putString(SP_AD_ADMD5,md5).commit();
                        }

                    }
                });

        return super.onStartCommand(intent, flags, startId);
    }

    public Bitmap stringToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray = Base64.decode(string.split(",")[1], Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    public void saveMyBitmap(Bitmap mBitmap, String bitName, String path) {
        File f = new File(path + bitName + ".jpg");
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
