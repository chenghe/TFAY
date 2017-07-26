package com.minxing.client.util;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.minxing.client.activity.GesturePasswordActivity;
import com.minxing.kit.MXKit;
import com.minxing.kit.MXKit.MXForegroundListener;
import com.minxing.kit.api.MXAPI;
import com.minxing.kit.api.bean.MXCurrentUser;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 启动问题一共有三个地方用到  1.AppApplication设置kit 的生命周期   2.Client的TootActivity的生命周期  3.ClientTabActivity的生命周期
 * 主要是设计其两个方法   onStart()  和  onStop()方法   onStartActivityForResult()方法在打开系统或第三方Activity时不打开后台计时器
 *
 */
public class BackgroundDetector {
	
	private static final String Tag = BackgroundDetector.class.getName();

//	private final static int BACKGROUNDTIME = 5*1000;
	private final static int BACKGROUNDTIME = 3 * 60 * 1000;

	public static BackgroundDetector instance;
	private static Context mContext;
	private static Timer schedulePopGesturePwdViewTimer;// 后台定时定时器
	
	private static boolean activated = true;// 界面是否切到前台
	// 是否停止手势密码计时监听 (当页面切到前台时不应弹出手势密码) true:是 false:否
	// 当启动系统或第三方的页面时，虽然当前项目处于后台，但不应该显示手势密码
	private static boolean detectorStop = false;
	// 是否启动密码输入界面 true:是 false:否
	private static boolean passwordCheckActive = false;
	
	private static boolean overTimes = false;//在后台时间超过了规定时间

	private static MXForegroundListener foregroundListener = new MXForegroundListener() {
		
		@Override
		public void onBecameForeground() {
//			Log.w(Tag, "====切换到前台======");
			activated = true;
			//判断是否需要打开手势密码页面
			if(schedulePopGesturePwdViewTimer != null){
				schedulePopGesturePwdViewTimer.cancel();
				schedulePopGesturePwdViewTimer = null;
//				Log.w(Tag, "====关闭后台计时======");
			}
			MXCurrentUser currentUser = MXAPI.getInstance(mContext).currentUser();
			if (currentUser != null){
				//为了防止用户被踢或者用户退出登陆
				String loginName = currentUser.getLoginName();
				if (PreferenceUtils.isInitGesturePwd(mContext, loginName)
						&& PreferenceUtils.isGesturePwdEnable(mContext, loginName)){
//					Log.w(Tag, "====有打开手势密码的权限======");
					if(overTimes){
//						Log.w(Tag, "====在后台超过规定时间，打开手势密码页面======");
						passwordCheckActive = true;
						Intent intent = new Intent(mContext, GesturePasswordActivity.class);
						intent.putExtra(GesturePasswordActivity.PWD_SCREEN_MODE_KEY, GesturePasswordActivity.PWD_SCREEN_MODE_BACKGROUND);
						intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
						mContext.startActivity(intent);
						overTimes = false;
						MXKit.getInstance().setStartGesturePsd(true);
					}else{
//						Log.w(Tag, "====没有超过规定时间，不打开手势密码页面======");
					}
				}else{
//					Log.w(Tag, "======没有打开手势密码的权限=======");
				}
			}
			
		}
		
		@Override
		public void onBecameBackground() {
			activated = false;
//			Log.w(Tag, "====切换到后台======");
			//判断是否需要开启后台计时器
			MXCurrentUser currentUser = MXAPI.getInstance(mContext).currentUser();
			if(currentUser != null){
				String loginName = currentUser.getLoginName();
				if(PreferenceUtils.isInitGesturePwd(mContext, loginName)
						&& PreferenceUtils.isGesturePwdEnable(mContext, loginName)){
					if(!detectorStop){
		        		//开启后台计时监听
//						Log.w(Tag, "====开启后台计时器======");
						Handler handler = new Handler(new Handler.Callback() {
							@Override
							public boolean handleMessage(Message msg) {
								schedulePopGesturePwdView(mContext);
								return false;
							}
						});
						handler.sendMessage(new Message());
		        	}else{
//		        		Log.w(Tag, "====停止了后台检测器======");
		        	}
				}else{
//					Log.w(Tag, "====没有设置手势密码，不需要开启后台计时======");
				}
			}
		}
	};
	
	/**
	 * 判断是否启动手势密码页面
	 * 
	 * @return
	 */
	public boolean isPasswordCheckActive() {
		return passwordCheckActive;
	}

	/**
	 * 设置是否启动手势密码页面
	 * 
	 * @param passwordCheckActive
	 */
	public void setPasswordCheckActive(boolean passwordCheckActive) {
		BackgroundDetector.passwordCheckActive = passwordCheckActive;
	}

	/**
	 * 设置是否停止程序切换到后台的计时监听
	 * 
	 * @param detectorStop
	 */
	public void setDetectorStop(boolean detectorStop) {
		BackgroundDetector.detectorStop = detectorStop;
	}

	/**
	 * 手势密码页面是否能用
	 * 
	 * @param context
	 * @return
	 */
	public boolean isGesturePwdViewEnabled(Context context) {
		if (activated || detectorStop) {
			return false;
		}
		return PreferenceUtils.isgesturePwdViewEnabled(context);
	}

	
	
	public static BackgroundDetector getInstance() {
		if (instance == null) {
			instance = new BackgroundDetector();
		}
		return instance;
	}

	public void init(Context context){
		mContext = context;
		//给kit的前后台检测器添加一个监听
		MXKit.getInstance().addBackgroundListener(foregroundListener);
	}
	
	private BackgroundDetector(){
	}
	

	private static void schedulePopGesturePwdView(final Context context) {
		if (schedulePopGesturePwdViewTimer != null) {
			schedulePopGesturePwdViewTimer.cancel();
			schedulePopGesturePwdViewTimer = null;
		}
		
		schedulePopGesturePwdViewTimer = new Timer();
		schedulePopGesturePwdViewTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				overTimes = true;
				PreferenceUtils.setGesturePwdViewEnable(context, true);//不知道什么作用，以前有所以还留着，怕别的地方会用到
			}
		}, BACKGROUNDTIME);
		
	}
}
