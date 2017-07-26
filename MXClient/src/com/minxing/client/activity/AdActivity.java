package com.minxing.client.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.minxing.client.ClientTabActivity;
import com.minxing.client.R;
import com.minxing.client.RootActivity;
import com.minxing.client.bean.ADBean;
import com.minxing.client.bean.ADCallBack;
import com.minxing.client.service.Adservice;
import com.minxing.client.util.CountDownView;
import com.minxing.client.util.FileUtils;
import com.minxing.client.util.MD5Util;
import com.minxing.client.util.PreferenceUtils;
import com.minxing.client.util.ResourceUtil;
import com.minxing.client.util.Utils;
import com.minxing.kit.MXKit;
import com.minxing.kit.api.MXAPI;
import com.minxing.kit.api.bean.MXCurrentUser;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;

/**
 * 广告页面Activity
 * Created by hechengbin on 2017/5/23.
 */

public class AdActivity extends RootActivity {

    private CountDownView countDownView;
    private String cachePicturePath;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);

        cachePicturePath = Environment.getExternalStorageDirectory() + "/MXClient/Picture/";
        FileUtils.makeRootDirectory(cachePicturePath);

        iv = (ImageView) findViewById(R.id.iv);
        Bitmap ivSrc = BitmapFactory.decodeFile(cachePicturePath + "tfayad.jpg");
        if (ivSrc == null){
//            ivSrc = BitmapFactory.decodeFile(cachePicturePath + "test1.jpg");
            iv.setImageResource(R.drawable.ad_default);
        }else {
            iv.setImageBitmap(ivSrc);
        }

        countDownView = (CountDownView) findViewById(R.id.countdownview);
        Intent serviceIntent = new Intent(this, Adservice.class);
        startService(serviceIntent);

//        countDownView.start();
        countDownView.setCountDownTimerListener(new CountDownView.CountDownTimerListener() {
            @Override
            public void onStartCount() {
//                Toast.makeText(getApplicationContext(), "开始计时", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinishCount() {
//                Toast.makeText(getApplicationContext(), "结束计时", Toast.LENGTH_SHORT).show();
//                launchApp();
            }
        });

        countDownView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchApp();
            }
        });

    }

    private void launchApp() {
        MXCurrentUser currentUser = MXAPI.getInstance(this).currentUser();
        if (currentUser != null) {
            String tabSortHide = ResourceUtil.getConfString(this, "client_sort_hide");
            if (TextUtils.isEmpty(tabSortHide) || (!TextUtils.isEmpty(tabSortHide) && tabSortHide.contains("circle"))) {
                Utils.setShareFromExternalBrowserEnable(this, true);
            } else {
                Utils.setShareFromExternalBrowserEnable(this, false);
            }
            Intent intent = null;
            if (PreferenceUtils.isGesturePwdEnable(this, currentUser.getLoginName())
                    && PreferenceUtils.isInitGesturePwd(this, currentUser.getLoginName())) {
                intent = new Intent(this, GesturePasswordActivity.class);
                intent.putExtra(GesturePasswordActivity.PWD_SCREEN_MODE_KEY, GesturePasswordActivity.PWD_SCREEN_MODE_FORCE);
                MXKit.getInstance().setStartGesturePsd(true);
            } else {
                intent = new Intent(this, ClientTabActivity.class);
            }
            startActivity(intent);
            finish();
        }
    }

}
