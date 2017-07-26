package com.minxing.client.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.minxing.client.R;
import com.minxing.client.RootActivity;
import com.minxing.kit.api.MXAPI;
import com.minxing.kit.api.bean.MXCurrentUser;
import com.minxing.kit.internal.BaseActivity;
import com.minxing.kit.internal.common.bean.UserAccount;

/**
 * Created by Administrator on 2017/1/3.
 */
public class OpenHorizontalScreenActivity extends RootActivity{
    private Switch open_horizontal_screen_on;
    private ImageButton backButton = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_horizontal_screen);
        init();

    }
    private void init(){
        ((TextView) findViewById(R.id.title_name)).setText(R.string.open_horizontal_screen_on);
        backButton = (ImageButton) findViewById(R.id.title_left_button);
        open_horizontal_screen_on = (Switch)findViewById(R.id.mx_system_setting_open_horizontal_screen_on);
        int currentAccountID = 0;
        MXCurrentUser currentuser = MXAPI.getInstance(OpenHorizontalScreenActivity.this).currentUser();
        if (currentuser != null){
            currentAccountID = currentuser.getAccountId();
        }

        Boolean isSensor = MXAPI.getInstance(OpenHorizontalScreenActivity.this).getScreenOrientation(OpenHorizontalScreenActivity.this,currentAccountID);
        if(isSensor){
            open_horizontal_screen_on.setChecked(true);
        }else{
            open_horizontal_screen_on.setChecked(false);
        }
        final int finalCurrentAccountID = currentAccountID;
        open_horizontal_screen_on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    MXAPI.getInstance(OpenHorizontalScreenActivity.this).setScreenOrientation(OpenHorizontalScreenActivity.this, finalCurrentAccountID,true);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }else{
                    MXAPI.getInstance(OpenHorizontalScreenActivity.this).setScreenOrientation(OpenHorizontalScreenActivity.this,finalCurrentAccountID,false);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
         });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }
}
