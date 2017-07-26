package com.minxing.client.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.minxing.client.R;
import com.minxing.client.RootActivity;
import com.minxing.client.util.PreferenceUtils;
import com.minxing.client.util.ResourceUtil;
import com.minxing.kit.api.MXAPI;
import com.minxing.kit.api.bean.MXCurrentUser;

public class SystemSettingMessageNotificationActivity extends RootActivity {

    private RelativeLayout notification_sound;//声音父控件
    private RelativeLayout notification_shake;//震动父控件

    private TextView mx_system_setting_notify_tip;

    private Switch mx_system_setting_new_message_notify;//新消息通知
    private Switch mx_system_setting_new_mail_notify;//新邮件通知
    private Switch mx_system_setting_sound_notify;//声音
    private Switch mx_system_setting_shake_notify;//震动

	private RelativeLayout new_message_mail_notification;//新邮件通知  父控件

	private LinearLayout message_disturb = null;//消息免打扰

	private ImageButton backButton = null;
	private Vibrator vib = null;

	private String loginName = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_setting_message_notification);
		MXCurrentUser currentUser = MXAPI.getInstance(this).currentUser();
		if(currentUser != null){
			loginName = currentUser.getLoginName();
		}
		
		vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		((TextView) findViewById(R.id.title_name)).setText(R.string.setting_message_notification);
		backButton = (ImageButton) findViewById(R.id.title_left_button);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finishWithAnimation();
			}
		});

        mx_system_setting_notify_tip = (TextView) findViewById(R.id.mx_system_setting_notify_tip);

        //消息免打扰
		message_disturb = (LinearLayout) findViewById(R.id.message_disturb);
		message_disturb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SystemSettingMessageNotificationActivity.this, SystemSettingDisturbActivity.class);
				startActivity(intent);
			}
		});

        //新邮件通知设置是否显示
        new_message_mail_notification = (RelativeLayout) findViewById(R.id.new_message_mail_notification);
        if(ResourceUtil.getConfBoolean(this, "client_show_mail")){
            new_message_mail_notification.setVisibility(View.VISIBLE);
        } else {
            new_message_mail_notification.setVisibility(View.GONE);
        }

        notification_sound = (RelativeLayout) findViewById(R.id.notification_sound);
        notification_shake = (RelativeLayout) findViewById(R.id.notification_shake);


        //新消息通知
        mx_system_setting_new_message_notify = (Switch) findViewById(R.id.mx_system_setting_new_message_notify);
        //新邮件通知
        mx_system_setting_new_mail_notify = (Switch) findViewById(R.id.mx_system_setting_new_mail_notify);
        //声音
        mx_system_setting_sound_notify = (Switch) findViewById(R.id.mx_system_setting_sound_notify);
        //震动
        mx_system_setting_shake_notify = (Switch) findViewById(R.id.mx_system_setting_shake_notify);

        //判断是否会接受新消息通知
        if (PreferenceUtils.isMessageNotification(this, loginName)) {
            mx_system_setting_new_message_notify.setChecked(true);
        } else {
            mx_system_setting_new_message_notify.setChecked(false);
        }
        mx_system_setting_new_message_notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    PreferenceUtils.enableMessageNotification(SystemSettingMessageNotificationActivity.this, loginName);
                    mx_system_setting_notify_tip.setVisibility(View.VISIBLE);
                    message_disturb.setVisibility(View.VISIBLE);
                    notification_sound.setVisibility(View.VISIBLE);
                    notification_shake.setVisibility(View.VISIBLE);
                    if(ResourceUtil.getConfBoolean(SystemSettingMessageNotificationActivity.this, "client_show_mail")){
                        new_message_mail_notification.setVisibility(View.VISIBLE);
                    } else {
                        new_message_mail_notification.setVisibility(View.GONE);
                    }
                }else{
                    PreferenceUtils.disableMessageNotification(SystemSettingMessageNotificationActivity.this, loginName);
                    mx_system_setting_notify_tip.setVisibility(View.GONE);
                    message_disturb.setVisibility(View.GONE);
                    new_message_mail_notification.setVisibility(View.GONE);
                    notification_sound.setVisibility(View.GONE);
                    notification_shake.setVisibility(View.GONE);
                }
            }
        });

        //邮件
        if (PreferenceUtils.isMailNotification(SystemSettingMessageNotificationActivity.this, loginName)){
            //接受新邮件通知
            mx_system_setting_new_mail_notify.setChecked(true);
        }else{
            mx_system_setting_new_mail_notify.setChecked(false);
        }
        mx_system_setting_new_mail_notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    PreferenceUtils.enableMailNotification(SystemSettingMessageNotificationActivity.this, loginName);
                }else{
                    PreferenceUtils.disableMailNotification(SystemSettingMessageNotificationActivity.this, loginName);
                }
            }
        });

        //声音
        if (PreferenceUtils.isNotificationSound(this, loginName)){
            mx_system_setting_sound_notify.setChecked(true);
        }else{
            mx_system_setting_sound_notify.setChecked(false);
        }
        mx_system_setting_sound_notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    PreferenceUtils.enableNotificationSound(SystemSettingMessageNotificationActivity.this, loginName);
                }else{
                    PreferenceUtils.disableNotificationSound(SystemSettingMessageNotificationActivity.this, loginName);
                }
            }
        });


        //震动
        if (PreferenceUtils.isNotificationShake(this, loginName)){
            mx_system_setting_shake_notify.setChecked(true);
        }else{
            mx_system_setting_shake_notify.setChecked(false);
        }
        mx_system_setting_shake_notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    vib.vibrate(new long[] { 300, 300, 300, 300 }, -1);
                    PreferenceUtils.enableNotificationShake(SystemSettingMessageNotificationActivity.this, loginName);
                }else{
                    PreferenceUtils.disableNotificationShake(SystemSettingMessageNotificationActivity.this, loginName);
                }
            }
        });
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finishWithAnimation();
		}
		return false;
	}
}