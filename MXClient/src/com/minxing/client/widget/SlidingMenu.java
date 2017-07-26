package com.minxing.client.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.minxing.client.AppConstants;
import com.minxing.client.R;
import com.minxing.client.activity.SystemSettingActivity;
import com.minxing.client.util.ResourceUtil;
import com.minxing.kit.api.MXAPI;
import com.minxing.kit.api.bean.MXCurrentUser;
import com.minxing.kit.api.bean.MXNetwork;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class SlidingMenu extends ScrollView {
	private Context context;
	private RoundImageView slidAvatar;
	private TextView slidName;
	private LinearLayout slidNetworkMainHeader;
	private LinearLayout slidNetworkMain;
	private LinearLayout slidNetworkExternalHeader;
	private View slidNetworkExternalDivider;
	private LinearLayout slidNetworkExternal;
	private LinearLayout slidBrowseExternalNetwork;
	private RelativeLayout slidUser;
	private LinearLayout slidQrCode;
	private DrawerLayout drawerLayout;
	private RelativeLayout slidSetting;
	private TextView slidNewflag;
	private View slid_feedback_divider;
	private RelativeLayout slidFeedback;
	private boolean showExtNetwork = false;
	private int itemId = 0;
	private String feedbackOcuID;
	private OnNetworkSwitchListener onNetworkSwitchListener;

	private static final int VIEW_CURRENT_USER = 1;
	private static final int VIEW_CURRENT_USER_QRCODE = 2;
	private static final int SYSTEM_SETTING = 3;
	private static final int START_OCU_CHAT = 4;
	private static final int VIEW_NET_WORKLIST = 5;
	private static final int VIEW_FAVORITE = 6;
	private static final int VIEW_MESSAGE = 7;
	private static final int VIEW_UPLOAD_FILES = 8;
	private static final int VIEW_DOWNLOAD_FILES = 9;
	
	public interface OnNetworkSwitchListener {
		public void switchNetwork(MXNetwork net);
	}

	public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public SlidingMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public SlidingMenu(Context context) {
		super(context);
		this.context = context;
	}

	public void setOnNetworkSwitchListener(OnNetworkSwitchListener onNetworkSwitchListener) {
		this.onNetworkSwitchListener = onNetworkSwitchListener;
	}

	public void init(DrawerLayout drawer) {
		showExtNetwork = ResourceUtil.getConfBoolean(context, "client_show_ext_network");
		this.drawerLayout = drawer;
		View main = LayoutInflater.from(context).inflate(R.layout.sliding_drawer, null);
		this.addView(main);
		slidUser = (RelativeLayout) main.findViewById(R.id.slid_user);
		slidAvatar = (RoundImageView) main.findViewById(R.id.slid_avatar);
		slidName = (TextView) main.findViewById(R.id.slid_name);
		slidQrCode = (LinearLayout) main.findViewById(R.id.slid_qr_code);

		slidNetworkMainHeader = (LinearLayout) main.findViewById(R.id.slid_network_main_header);
		slidNetworkMain = (LinearLayout) main.findViewById(R.id.slid_network_main);
		slidNetworkExternalHeader = (LinearLayout) main.findViewById(R.id.slid_network_external_header);
		slidNetworkExternalDivider = (View) main.findViewById(R.id.slid_network_external_divider);
		slidNetworkExternal = (LinearLayout) main.findViewById(R.id.slid_network_external);
		slidBrowseExternalNetwork = (LinearLayout) main.findViewById(R.id.slid_browse_external_network);
		slidSetting = (RelativeLayout) main.findViewById(R.id.slid_setting);
		slidNewflag = (TextView) main.findViewById(R.id.slid_newflag);
		slid_feedback_divider = main.findViewById(R.id.slid_feedback_divider);
		slidFeedback = (RelativeLayout) main.findViewById(R.id.slid_feedback);

		drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {

			@Override
			public void onDrawerStateChanged(int state) {
				if (state == DrawerLayout.STATE_DRAGGING) {
					refreshNetwork(MXAPI.getInstance(
							context).currentUser());
				}
			}

			@Override
			public void onDrawerSlide(View arg0, float arg1) {
			}

			@Override
			public void onDrawerOpened(View arg0) {
			}

			@Override
			public void onDrawerClosed(View arg0) {
				if (itemId == 0){
					return;
				}else {
					openItem(itemId);
					itemId = 0;
				}
			}
		});



		slidUser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				drawerLayout.closeDrawers();
				itemId = 1;
			}
		});
		
		slidQrCode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				drawerLayout.closeDrawers();
				itemId = 2;
			}
		});
		
		slidSetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				drawerLayout.closeDrawers();
				itemId = 3;
			}
		});
		
		 feedbackOcuID = ResourceUtil.getConfString(context, "client_sliding_menu_feedback_ocu");
		if(feedbackOcuID != null && !"".equals(feedbackOcuID)){
			slid_feedback_divider.setVisibility(View.VISIBLE);
			slidFeedback.setVisibility(View.VISIBLE);
			slidFeedback.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					drawerLayout.closeDrawers();
					itemId = 4;
				}
			});
		}
		
		slidBrowseExternalNetwork.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				drawerLayout.closeDrawers();
				itemId = 5;
			}
		});
		MXCurrentUser user = MXAPI.getInstance(context).currentUser();
		if (user != null) {
			updateAvatar(user);
		}

		RelativeLayout my_fav = (RelativeLayout) findViewById(R.id.slid_my_fav);
		RelativeLayout slid_my_message = (RelativeLayout) findViewById(R.id.slid_my_message);
		RelativeLayout slid_my_upload = (RelativeLayout) findViewById(R.id.slid_my_upload);
		RelativeLayout slid_my_download = (RelativeLayout) findViewById(R.id.slid_my_download);
		String tabSortHide = ResourceUtil.getConfString(context, "client_sort_hide");
		if(TextUtils.isEmpty(tabSortHide) || (!TextUtils.isEmpty(tabSortHide) && tabSortHide.contains("circle"))) {
			slid_my_message.setVisibility(View.VISIBLE);
		}else{
			slid_my_message.setVisibility(View.GONE);
		}

		my_fav.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				drawerLayout.closeDrawers();
				itemId = 6;
			}
		});
		slid_my_message.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				drawerLayout.closeDrawers();
				itemId = 7;
			}
		});
		slid_my_upload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				drawerLayout.closeDrawers();
				itemId = 8;
			}
		});
		slid_my_download.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				drawerLayout.closeDrawers();
				itemId = 9;
			}
		});
	}

	private void updateAvatar(MXCurrentUser user) {
		ImageLoader.getInstance().displayImage(user.getAvatarUrl(), slidAvatar, AppConstants.USER_AVATAR_OPTIONS, AppConstants.animateFirstListener);
		slidName.setText(user.getName());
	}

	public void refreshNetwork(MXCurrentUser user) {
		updateAvatar(user);
		appendNetworks(user);
	}

	private void appendNetworks(MXCurrentUser user) {
		if (user == null) {
			return;
		}
		List<MXNetwork> networks = user.getNetworks();

		slidNetworkMain.removeAllViews();
		slidNetworkExternal.removeAllViews();

		List<MXNetwork> innerNets = new ArrayList<MXNetwork>();
		List<MXNetwork> outerNets = new ArrayList<MXNetwork>();
		boolean isCurrentOutNet = false;

		for (MXNetwork network : networks) {

			if (network.isExternal()) {
				outerNets.add(network);
				if (network.getId() == user.getNetworkID()) {
					isCurrentOutNet = true;
				}
			} else {
				innerNets.add(network);
			}
		}

		// 初始化内部社区
		for (int i = 0; i < innerNets.size(); i++) {
			MXNetwork net = innerNets.get(i);
			View item = LayoutInflater.from(context).inflate(R.layout.network_item, null);
			TextView mynetwork = (TextView) item.findViewById(R.id.mynetwork);
			ImageView checkmark = (ImageView) item.findViewById(R.id.checkmark);
			TextView sms_num = (TextView) item.findViewById(R.id.sms_num);
			ImageView work_circle_icon = (ImageView) item.findViewById(R.id.work_circle_icon);

			mynetwork.setText(net.getName());

			int chatUnread = MXAPI.getInstance(context).queryNetworkChatUnread(net.getId());
			boolean circleMark = MXAPI.getInstance(context).checkNetworkCircleUnread(net.getId());

			if (chatUnread > 0 || circleMark) {
				if (chatUnread > 0) {
					sms_num.setVisibility(View.VISIBLE);
					work_circle_icon.setVisibility(View.GONE);
					String unReadCount = chatUnread <= 99 ? String.valueOf(chatUnread) : "...";
					sms_num.setText(unReadCount);
				} else {
					sms_num.setVisibility(View.GONE);
					work_circle_icon.setVisibility(View.VISIBLE);
					sms_num.setText("");
				}
			} else {
				sms_num.setVisibility(View.GONE);
				work_circle_icon.setVisibility(View.GONE);
				sms_num.setText("");
			}

			setOnClickListener(item, net);
			slidNetworkMain.addView(item);

			if (user.getNetworkID() == net.getId()) {
				checkmark.setVisibility(View.VISIBLE);
				slidNetworkMainHeader.setSelected(true);
				slidNetworkExternalHeader.setSelected(false);
				item.setSelected(true);
				slidBrowseExternalNetwork.setVisibility(View.VISIBLE);
				sms_num.setVisibility(View.GONE);
				work_circle_icon.setVisibility(View.GONE);
				sms_num.setText("");
			} else {
				if (!slidNetworkMainHeader.isSelected()) {
					slidNetworkMainHeader.setSelected(false);
				}
				item.setSelected(false);
				checkmark.setVisibility(View.GONE);
			}
		}

		// 初始化外部社区
		if(outerNets.isEmpty()){
			showExtNetwork = false;
		}
		if ( !showExtNetwork) {
			slidNetworkExternalHeader.setVisibility(View.GONE);
			slidNetworkExternalDivider.setVisibility(View.GONE);
			slidNetworkExternal.setVisibility(View.GONE);
			if (isCurrentOutNet || !showExtNetwork) {
				slidBrowseExternalNetwork.setVisibility(View.GONE);
			} else {
				slidBrowseExternalNetwork.setVisibility(View.VISIBLE);
			}
		} else {
			slidNetworkExternalHeader.setVisibility(View.VISIBLE);
			slidNetworkExternalDivider.setVisibility(View.VISIBLE);
			slidNetworkExternal.setVisibility(View.VISIBLE);
			slidBrowseExternalNetwork.setVisibility(View.VISIBLE);
			for (int i = 0; i < outerNets.size(); i++) {
				MXNetwork net = outerNets.get(i);
				View item = LayoutInflater.from(context).inflate(R.layout.network_item, null);
				TextView mynetwork = (TextView) item.findViewById(R.id.mynetwork);
				ImageView checkmark = (ImageView) item.findViewById(R.id.checkmark);
				TextView sms_num = (TextView) item.findViewById(R.id.sms_num);
				ImageView work_circle_icon = (ImageView) item.findViewById(R.id.work_circle_icon);

				mynetwork.setText(net.getName());

				int chatUnread = MXAPI.getInstance(context).queryNetworkChatUnread(net.getId());
				boolean circleMark = MXAPI.getInstance(context).checkNetworkCircleUnread(net.getId());

				if (chatUnread > 0 || circleMark) {
					if (chatUnread > 0) {
						sms_num.setVisibility(View.VISIBLE);
						work_circle_icon.setVisibility(View.GONE);
						String unReadCount = chatUnread <= 99 ? String.valueOf(chatUnread) : "...";
						sms_num.setText(unReadCount);
					} else {
						sms_num.setVisibility(View.GONE);
						work_circle_icon.setVisibility(View.VISIBLE);
						sms_num.setText("");
					}
				} else {
					sms_num.setVisibility(View.GONE);
					work_circle_icon.setVisibility(View.GONE);
					sms_num.setText("");
				}

				if (user.getNetworkID() == net.getId()) {
					checkmark.setVisibility(View.VISIBLE);
					slidNetworkExternalHeader.setSelected(true);
					slidNetworkMainHeader.setSelected(false);
					item.setSelected(true);
					slidBrowseExternalNetwork.setVisibility(View.GONE);
					sms_num.setVisibility(View.GONE);
					work_circle_icon.setVisibility(View.GONE);
					sms_num.setText("");
				} else {
					checkmark.setVisibility(View.GONE);
					item.setSelected(false);
					if (!slidNetworkExternalHeader.isSelected()) {
						slidNetworkExternalHeader.setSelected(false);
					}
				}

				setOnClickListener(item, net);
				slidNetworkExternal.addView(item);
			}
		}
	}

	public void setOnClickListener(final View view, final MXNetwork net) {
		view.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				MXCurrentUser user = MXAPI.getInstance(context).currentUser();
				if (user.getNetworkID() != net.getId()) {	
					if (MXAPI.getInstance(context).switchNetwork(view.getContext(), net.getId())) {
						drawerLayout.closeDrawers();
						if (onNetworkSwitchListener != null) {
							onNetworkSwitchListener.switchNetwork(net);
						}
						return;
					}
				}
				drawerLayout.closeDrawers();
			}
		});
	}

	public void UpgradeNewVersionMark(boolean showMark) {
		if (showMark) {
			slidNewflag.setVisibility(View.VISIBLE);
		} else {
			slidNewflag.setVisibility(View.GONE);
		}
	}
	private void openItem(int itemId){
		switch (itemId){
			case VIEW_CURRENT_USER :
				//查看当前用户信息
				MXAPI.getInstance(context).viewCurrentUser();
				break;
			case VIEW_CURRENT_USER_QRCODE :
				//二维码
				MXAPI.getInstance(context).viewCurrentUserQRCode();
				break;
			case SYSTEM_SETTING :
				//设置
				Intent intent = new Intent(context, SystemSettingActivity.class);
				context.startActivity(intent);
				((Activity) context).overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
				break;
			case START_OCU_CHAT :
				//我的社区
				MXAPI.getInstance(context).startOcuChat(feedbackOcuID);
				break;
			case VIEW_NET_WORKLIST :
				//更新network
				MXAPI.getInstance(context).viewNetworkList();
				break;
			case VIEW_FAVORITE :
				//我的收藏
				MXAPI.getInstance(context).viewFavorite();
				break;
			case VIEW_MESSAGE :
				//我的消息
				MXAPI.getInstance(context).viewMessages();
				break;
			case VIEW_UPLOAD_FILES :
				//我上传的文件
				MXAPI.getInstance(context).viewUploadedFile();
				break;
			case VIEW_DOWNLOAD_FILES :
				//我下载的文件
				MXAPI.getInstance(context).viewDownloadedFile();
				break;
		}
	}
}
