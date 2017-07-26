package com.minxing.client;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.minxing.client.tab.MenuTabHost;
import com.minxing.client.tab.MenuTabItem;
import com.minxing.client.tab.MenuTabItem.BeforeTabChangeListener;
import com.minxing.client.tab.MenuTabItem.OnReClickListener;
import com.minxing.client.upgrade.AppUpgradeInfo;
import com.minxing.client.util.BackgroundDetector;
import com.minxing.client.util.BadgeUtil;
import com.minxing.client.util.NotificationUtil;
import com.minxing.client.util.PreferenceUtils;
import com.minxing.client.util.ResourceUtil;
import com.minxing.client.util.Utils;
import com.minxing.client.widget.CircleTopRightPopMenu;
import com.minxing.client.widget.SlidingMenu;
import com.minxing.client.widget.SlidingMenu.OnNetworkSwitchListener;
import com.minxing.client.widget.SystemMainTopRightPopMenu;
import com.minxing.kit.MXConstants;
import com.minxing.kit.MXKit;
import com.minxing.kit.MXUIEngine;
import com.minxing.kit.api.MXAPI;
import com.minxing.kit.api.bean.CirclePopCenter;
import com.minxing.kit.api.bean.MXCircleGroup;
import com.minxing.kit.api.bean.MXCurrentUser;
import com.minxing.kit.api.bean.MXNetwork;
import com.minxing.kit.ui.appcenter.AppCenterManager;
import com.minxing.kit.ui.appcenter.AppCenterManager.OnEditModeListener;
import com.minxing.kit.ui.chat.ChatManager;
import com.minxing.kit.ui.chat.ChatManager.OnChatFinishListener;
import com.minxing.kit.ui.chat.ChatManager.ShareListener;
import com.minxing.kit.ui.circle.CircleManager;
import com.minxing.kit.ui.circle.CircleManager.OnGroupChangeListener;
import com.minxing.kit.ui.contacts.ContactManager;
import com.minxing.kit.ui.web.WebManager;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class ClientTabActivity extends TabActivity {
	private MenuTabHost mTabHost = null;

	private BroadcastReceiver receiver = null;

	private SlidingMenu slidingMenu;
	private DrawerLayout drawerLayout;

	public static final String SHOW_CHAT_LIST_FLAG = "show_chat_list";
	public static final String AUTO_ENTER_CHAT_ID = "auto_chat_id";

	private boolean isAutoEnterChat = false;
	private int autoEnterChatID = -1;
	boolean showHandle = true;
	private List<View> handleViewList = new ArrayList<View>();
	// boolean hideAppcenter;
	// boolean hideCircle;
	private String tab_sort_hide;
	private SystemMainTopRightPopMenu functionPopMenu;
	private CircleTopRightPopMenu circleTopRightPopMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		handleStatusBarColor();
		showHandle = ResourceUtil.getConfBoolean(this, "client_show_handle");

		if (getIntent().getAction() != null
				&& !"".equals(getIntent().getAction())
				&& ("finish".equals(getIntent().getAction()) || "master_clear"
				.equals(getIntent().getAction()))) {
			return;
		}

		MXCurrentUser currentUser = MXAPI.getInstance(this).currentUser();
		if (currentUser == null) {
			Intent intent = new Intent(this, LoadingActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
			return;
		}

		// 初始化界面
		initView();
		updateAll();
		initSlidingMenu();

		AppUpgradeInfo upgradeInfo = (AppUpgradeInfo) getIntent()
				.getSerializableExtra(AppConstants.MXCLIENT_UPGRADE_INFO);
		if (upgradeInfo != null && upgradeInfo.getVersion() != null
				&& !"".equals(upgradeInfo.getVersion())) {
			Utils.showUpgradeDialog(ClientTabActivity.this, upgradeInfo);
		}

		boolean haveUnread = getIntent().getBooleanExtra(
				AppConstants.MXCLIENT_HAVE_UNREAD, false);
		if (haveUnread) {
			updateAll();
		}
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent
						.getAction()
						.equals(MXConstants.BroadcastAction.MXKIT_REVOKE_DISPATCH_UNSEEN)
						|| intent.getAction().equals(
						AppConstants.MXCLIENT_REFRESH_NEW_VERSION)) {
					updateAll();
					if (PreferenceUtils
							.checkUpgradeMark(ClientTabActivity.this)) {
						slidingMenu.UpgradeNewVersionMark(true);

						AppUpgradeInfo upgradeInfo = (AppUpgradeInfo) intent
								.getSerializableExtra(AppConstants.MXCLIENT_UPGRADE_INFO);
						if (upgradeInfo != null
								&& upgradeInfo.getVersion() != null
								&& !"".equals(upgradeInfo.getVersion())) {
							Utils.showUpgradeDialog(ClientTabActivity.this,
									upgradeInfo);
						}
					} else {
						slidingMenu.UpgradeNewVersionMark(false);
					}
				}
			}
		};

		IntentFilter filter = new IntentFilter();
		filter.addAction(MXConstants.BroadcastAction.MXKIT_REVOKE_DISPATCH_UNSEEN);
		filter.addAction(AppConstants.MXCLIENT_REFRESH_NEW_VERSION);
		registerReceiver(receiver, filter, MXKit.getInstance()
				.getAppSignaturePermission(), null);

	}

	@Override
	protected void onPause() {
		super.onPause();
		updateBadgeCount();
	}

	private void initView() {
		setContentView(R.layout.main_tab);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		slidingMenu = (SlidingMenu) findViewById(R.id.sliding_menu);
		initTabhost();
		refreshAlertIcon();
	}

	/**
	 * 初始化底部菜单用户可以在此方法中添加自定义的底部菜单
	 */
	private void initTabhost() {
		this.mTabHost = (MenuTabHost) getTabHost();
//		mTabHost.initTabItems(ClientTabActivity.this);
		List<MenuTabItem> menuTabItemList = mTabHost.generateTabItemList(ClientTabActivity.this);
		for(MenuTabItem item : menuTabItemList){
			Log.i("ClientTabActivity", "[initTabhost]item.getName():"+item.getName());
			if(item.getTag().equals(getResources().getString(R.string.client_tab_item_tag_chat))){
				initChatHeaderView();
				initChatListener();
			}else if(item.getTag().equals(getResources().getString(R.string.client_tab_item_tag_contacts))){
				initContactsHeaderView();
			}else if(item.getTag().equals(getResources().getString(R.string.client_tab_item_tag_appcenter))){
				initAppcenterHeaderView();
			}else if(item.getTag().equals(getResources().getString(R.string.client_tab_item_tag_circle))){
				initCircleHeaderView();
				initCircleListener();
			}else if(item.getTabType() == MenuTabItem.TAB_ITEM_TYPE_WEB){
				initDefaultWebHeaderView(item);
			}
		}
		this.mTabHost.addMenuItems(menuTabItemList);


	}



	private void initCircleListener() {
		MenuTabItem circleTabItem = mTabHost.getMenuTabItemByTag(R.string.client_tab_item_tag_circle);
		if(circleTabItem != null){
			circleTabItem.setOnReClickListener(new OnReClickListener() {

				@Override
				public void onReClick(MenuTabItem menuTabItem) {
					MXAPI.getInstance(ClientTabActivity.this).forceRefreshCircle();
				}
			});
			circleTabItem.setBeforeTabChangeListener(new BeforeTabChangeListener() {

				@Override
				public void beforeTabChange(MenuTabItem menuTabItem) {
					MXCurrentUser currentUser = MXAPI.getInstance(
							ClientTabActivity.this).currentUser();
					if (currentUser != null) {
						if (MXAPI.getInstance(ClientTabActivity.this)
								.checkNetworkCircleUnread(
										currentUser.getNetworkID())) {MXAPI.getInstance(ClientTabActivity.this)
								.setCircleAutoRefresh();
						}
					}
				}
			});
		}

	}

	private void initDefaultWebHeaderView(MenuTabItem item) {
		String name  = item.getName();
		final WebManager webManager = MXUIEngine.getInstance()
				.getWebManager();
		RelativeLayout userWebviewHeader = (RelativeLayout) LayoutInflater
				.from(this).inflate(R.layout.user_webview_header, null);
		LinearLayout defaultWebHandleView = (LinearLayout) userWebviewHeader
				.findViewById(R.id.system_handle);


		handleViewList.add(defaultWebHandleView);//添加到集合类，用于刷新未读数及社区名称
		final TextView titleView= (TextView) userWebviewHeader.findViewById(R.id.title);

		item.setBeforeTabChangeListener(new BeforeTabChangeListener() {
			@Override
			public void beforeTabChange(MenuTabItem menuTabItem) {
				initDefaultWebHeaderView(menuTabItem);
				refreshAlertIcon();
				MXCurrentUser currentUser = MXAPI.getInstance(ClientTabActivity.this).currentUser();
				refreshNetworkName(currentUser);
			}
		});

		titleView.setText(name);

		defaultWebHandleView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				drawerLayout.openDrawer(Gravity.START);
				if (slidingMenu != null) {
					MXCurrentUser user = MXAPI.getInstance(
							ClientTabActivity.this).currentUser();
					slidingMenu.refreshNetwork(user);
				}
			}
		});
		webManager.setHeaderView(userWebviewHeader);

	}

	private void initChatListener() {
		ChatManager chatManager = MXUIEngine.getInstance().getChatManager();
		chatManager.setShareListener(new ShareListener() {
			@Override
			public void onSuccess() {
				showTabByTag(getResources().getString(R.string.client_tab_item_tag_chat));
			}
		});

		chatManager.setOnChatFinishListener(new OnChatFinishListener() {
			@Override
			public void onBackToChatRoot(Context context) {
				Intent intent = new Intent(context, ClientTabActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(intent);
				((Activity) context).overridePendingTransition(
						R.anim.slide_left_in, R.anim.slide_right_out);
				showTabByTag(getResources().getString(R.string.client_tab_item_tag_chat));
			}
		});
	}

	/**
	 * 初始化聊天header view
	 *
	 */
	private void initChatHeaderView() {
		final ChatManager chatManager = MXUIEngine.getInstance()
				.getChatManager();
		RelativeLayout chatHeader = (RelativeLayout) LayoutInflater.from(this)
				.inflate(R.layout.chat_header_view, null);
		View chatHandleView = chatHeader.findViewById(R.id.system_handle);
		handleViewList.add(chatHandleView);//添加到集合类，用于刷新未读数及社区名称

		ImageButton bacKBtn = (ImageButton) chatHeader
				.findViewById(R.id.title_left_back_button);
		boolean isSlidingEnabled = ResourceUtil.getConfBoolean(this,
				"client_show_sliding");
		if(isSlidingEnabled && showHandle ){
			chatHandleView.setVisibility(View.VISIBLE);
			chatHandleView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					drawerLayout.openDrawer(Gravity.START);
					if (slidingMenu != null) {
						MXCurrentUser user = MXAPI.getInstance(
								ClientTabActivity.this).currentUser();
						slidingMenu.refreshNetwork(user);
					}
				}
			});
		}else if(!showHandle){
			chatHandleView.setVisibility(View.GONE);
			bacKBtn.setVisibility(View.VISIBLE);
			bacKBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}

		chatHeader.findViewById(R.id.middle_title).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						chatManager.scrollChatToTop();
					}
				});

		final ImageButton addButton = (ImageButton) chatHeader
				.findViewById(R.id.title_right_new_function);
		functionPopMenu = new SystemMainTopRightPopMenu(
				ClientTabActivity.this);
		boolean isShowAllPersonalContact = ResourceUtil.getConfBoolean(
				getApplicationContext(),"sync_personal_contact_all_from_server");
		if (isShowAllPersonalContact) {
			functionPopMenu.isAddContactEnabled(false);
		} else {
			functionPopMenu.isAddContactEnabled(true);
		}
		addButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new Handler().post(new Runnable() {
					public void run() {
						if (!functionPopMenu.isShowing()) {
							functionPopMenu.showAsDropDown(addButton);
						}
					}
				});
			}
		});
		chatManager.setHeaderView(chatHeader);
		chatManager.setBackListener(new ChatManager.HomeScreenBackListener() {

			@Override
			public boolean onBack(Context context) {
				moveTaskToBack(true);
				return true;
			}
		});
	}

	/**
	 * 初始化通讯录header view
	 *
	 */
	private void initContactsHeaderView() {
		final ContactManager contactManager = MXUIEngine.getInstance()
				.getContactManager();
		RelativeLayout contactsHeader = (RelativeLayout) LayoutInflater.from(
				this).inflate(R.layout.contacts_header_view, null);
		View contactsHandleView = contactsHeader.findViewById(R.id.system_handle);
		handleViewList.add(contactsHandleView);//添加到集合类，用于刷新未读数及社区名称
		if (showHandle) {
			contactsHandleView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					drawerLayout.openDrawer(Gravity.START);
					if (slidingMenu != null) {
						MXCurrentUser user = MXAPI.getInstance(
								ClientTabActivity.this).currentUser();
						slidingMenu.refreshNetwork(user);
					}
				}
			});
		} else {
			ImageButton bacKBtn = (ImageButton) contactsHeader
					.findViewById(R.id.title_left_back_button);
			contactsHandleView.setVisibility(View.GONE);
			bacKBtn.setVisibility(View.VISIBLE);
			bacKBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}

		LinearLayout rightIcon = (LinearLayout) contactsHeader
				.findViewById(R.id.title_btn_layout);
		boolean isContactOcuShow = MXKit.getInstance().getKitConfiguration()
				.isContactOcu();
		boolean isShowAllPersonalContact = ResourceUtil.getConfBoolean(
				getApplicationContext(),"sync_personal_contact_all_from_server");
		if (!isContactOcuShow && isShowAllPersonalContact) {
			rightIcon.setVisibility(View.GONE);
		} else {
			rightIcon.setVisibility(View.VISIBLE);
		}

		ImageButton newContaceButton = (ImageButton) contactsHeader
				.findViewById(R.id.title_right_new_function);
		newContaceButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				contactManager.addContacts(ClientTabActivity.this);
			}
		});
		contactManager.setHeaderView(contactsHeader);
		contactManager
				.setBackListener(new ContactManager.HomeScreenBackListener() {

					@Override
					public boolean onBack(Context context) {
						moveTaskToBack(true);
						return true;
					}
				});

		contactManager.setQuickBtnMailEnable(ResourceUtil.getConfBoolean(this,
				"client_contact_mail_btn", false));
		contactManager.setQuickBtnSmsEnable(ResourceUtil.getConfBoolean(this,
				"client_contact_sms_btn", false));
		contactManager.setQuickBtnCallEnable(ResourceUtil.getConfBoolean(this,
				"client_contact_call_btn", true));
		contactManager.setQuickBtnChatEnable(ResourceUtil.getConfBoolean(this,
				"client_contact_chat_btn", true));
		// contactManager.setInputSearchEnable(false);
	}

	/**
	 * 初始化我的应用header view
	 *
	 */
	private void initAppcenterHeaderView() {
		final AppCenterManager appCenterManager = MXUIEngine.getInstance()
				.getAppCenterManager();
		RelativeLayout appCenterHeader = (RelativeLayout) LayoutInflater.from(
				this).inflate(R.layout.app_center_header_view, null);
		View appCenterHandlView = appCenterHeader.findViewById(R.id.system_handle);
		handleViewList.add(appCenterHandlView);//添加到集合类，用于刷新未读数及社区名称
		final ImageButton addButton = (ImageButton) appCenterHeader
				.findViewById(R.id.title_right_new_function);
		final Button finishButton = (Button) appCenterHeader
				.findViewById(R.id.finish_edit_btn);

		if (showHandle) {
			appCenterHandlView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					drawerLayout.openDrawer(Gravity.START);
					if (slidingMenu != null) {
						MXCurrentUser user = MXAPI.getInstance(
								ClientTabActivity.this).currentUser();
						slidingMenu.refreshNetwork(user);
					}
				}
			});
		} else {
			ImageButton bacKBtn = (ImageButton) appCenterHeader
					.findViewById(R.id.title_left_back_button);
			appCenterHandlView.setVisibility(View.GONE);
			bacKBtn.setVisibility(View.VISIBLE);
			bacKBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}

		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				appCenterManager.addApp(ClientTabActivity.this);
				overridePendingTransition(R.anim.slide_right_in,
						R.anim.slide_left_out);
			}
		});

		appCenterManager.setHeaderView(appCenterHeader);
		appCenterManager
				.setBackListener(new AppCenterManager.HomeScreenBackListener() {

					@Override
					public boolean onBack(Context context) {
						moveTaskToBack(true);
						return true;
					}
				});

		appCenterManager.setEditModeListener(new OnEditModeListener() {

			@Override
			public void onStartEditMode() {
				addButton.setVisibility(View.GONE);
				finishButton.setVisibility(View.VISIBLE);
				finishButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						appCenterManager.endEdit();
					}
				});
			}

			@Override
			public void onEndEditMode() {
				addButton.setVisibility(View.VISIBLE);
				finishButton.setVisibility(View.GONE);
			}
		});
	}

	/**
	 * 初始化圈子header view
	 *
	 */
	private void initCircleHeaderView() {
		final CircleManager circleManager = MXUIEngine.getInstance()
				.getCircleManager();
		RelativeLayout circleHeader = (RelativeLayout) LayoutInflater
				.from(this).inflate(R.layout.circle_header_view, null);
		View circleHandleView = (LinearLayout) circleHeader
				.findViewById(R.id.system_handle);
		handleViewList.add(circleHandleView);//添加到集合类，用于刷新未读数及社区名称
		final ImageButton addButton = (ImageButton) circleHeader
				.findViewById(R.id.title_right_new_function);

		final LinearLayout middleTitleBar = (LinearLayout) circleHeader
				.findViewById(R.id.middle_title_bar);
		final TextView title = (TextView) circleHeader.findViewById(R.id.title);
		circleTopRightPopMenu = new CircleTopRightPopMenu(
				ClientTabActivity.this);

		addButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new Handler(ClientTabActivity.this.getMainLooper())
						.post(new Runnable() {
							public void run() {
								if (!circleTopRightPopMenu.isShowing()) {
									circleTopRightPopMenu
											.showAsDropDown(addButton);
								}
							}
						});
			}
		});

		if (showHandle) {
			circleHandleView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					drawerLayout.openDrawer(Gravity.START);
					if (slidingMenu != null) {
						MXCurrentUser user = MXAPI.getInstance(
								ClientTabActivity.this).currentUser();
						slidingMenu.refreshNetwork(user);
					}
				}
			});
		} else {
			ImageButton bacKBtn = (ImageButton) circleHeader
					.findViewById(R.id.title_left_back_button);
			circleHandleView.setVisibility(View.GONE);
			bacKBtn.setVisibility(View.VISIBLE);
			bacKBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}

		middleTitleBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 获取组管理界面
				CirclePopCenter circlePopCenter = circleManager
						.getCirclePopCenter();
				if (circlePopCenter != null) {
					circlePopCenter.handleGroupOptionBtn(!ResourceUtil
							.getConfBoolean(getApplicationContext(),
									"client_app_circle_create_disable"));
					if (circleManager.isCreateGroupActionEnabled()) {
						circlePopCenter.handleCreateCicleBtn(true);
					} else {
						circlePopCenter.handleCreateCicleBtn(false);
					}
				}
				if (!circlePopCenter.isShowing()) {
					circlePopCenter.showAsDropDown(v);
				}
			}
		});

		circleManager.setOnGroupChangeListener(new OnGroupChangeListener() {

			@Override
			public void onGroupChange(MXCircleGroup group) {
				// 监听组切换事件来更新标题
				title.setText(group.getName());
				if (group.isUnLimitPost()) {
					addButton.setVisibility(View.VISIBLE);
				} else {
					addButton.setVisibility(View.GONE);
				}
			}
		});
		circleManager.setHeaderView(circleHeader);
		circleManager
				.setBackListener(new CircleManager.HomeScreenBackListener() {

					@Override
					public boolean onBack(Context context) {
						moveTaskToBack(true);
						return true;
					}
				});
	}

	private void updateAll() {
		MXCurrentUser currentUser = MXAPI.getInstance(this).currentUser();
		if (currentUser == null) {
			return;
		}
		int unreadMessage = MXAPI.getInstance(this).queryNetworkChatUnread(
				currentUser.getNetworkID());
		MenuTabItem chatItem = mTabHost.getMenuTabItemByTag(R.string.client_tab_item_tag_chat);
		if (chatItem != null) {
			if (unreadMessage > 0) {
				String unReadCount = unreadMessage <= 99 ? String
						.valueOf(unreadMessage) : "99+";
				chatItem.showNumberMarker(unReadCount);
			} else {
				chatItem.hideNumberMarker();
			}
		}
		MenuTabItem circleTabItem = mTabHost.getMenuTabItemByTag(R.string.client_tab_item_tag_circle);

		if(circleTabItem != null){
			if (MXAPI.getInstance(this).checkNetworkCircleUnread(
					currentUser.getNetworkID())) {
				circleTabItem.showMarker();
				if (!mTabHost.getCurrentTabTag()
						.equals(getResources().getString(R.string.client_tab_item_tag_circle)) ){
					MXAPI.getInstance(this).setCircleAutoRefresh();
				}
			} else {
				circleTabItem.hideMarker();
			}
		}

		refreshAlertIcon();
		updateBadgeCount();
	}

	private void updateBadgeCount() {
		MXCurrentUser currentUser = MXAPI.getInstance(this).currentUser();
		if (currentUser == null) {
			return;
		}
		int unreadMessage = MXAPI.getInstance(this).queryNetworkChatUnread(
				currentUser.getNetworkID());
		if (unreadMessage > 0) {
			BadgeUtil.setBadgeCount(this, unreadMessage);
		} else {
			BadgeUtil.resetBadgeCount(this);
		}
	}

	private void initSlidingMenu() {
		slidingMenu.init(drawerLayout);
		slidingMenu.setOnNetworkSwitchListener(new OnNetworkSwitchListener() {

			@Override
			public void switchNetwork(MXNetwork net) {
				MXUIEngine.getInstance().switchNetwork(ClientTabActivity.this,
						net.getId());
				updateNetworkTitle(net.getName());
			}
		});


		boolean isSlidingEnabled = ResourceUtil.getConfBoolean(this,
				"client_show_sliding");
		if (!isSlidingEnabled) {
			drawerLayout
					.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		}
	}

	private void refreshNetworkName(MXCurrentUser currentUser) {
		for(View view : handleViewList){
			TextView networkNameView = (TextView) view.findViewById(R.id.current_network);
			if (currentUser != null && currentUser.getNetworks() != null) {
				networkNameView.setText(currentUser.getNetworkName());
				networkNameView.setVisibility(View.VISIBLE);
			}else{
				networkNameView.setText("");
				networkNameView.setVisibility(View.GONE);
			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (getIntent().getAction() != null
				&& !"".equals(getIntent().getAction())
				&& "finish".equals(getIntent().getAction())) {

			if (getIntent().getBooleanExtra("need_login_page", true)) {
				// 打开登录页面
				Intent intent = new Intent(this, LoginActivity.class);
				intent.putExtra("error_message",
						getIntent().getStringExtra("error_message"));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(
						AppConstants.SYSTEM_START_TYPE_APP2APP,
						getIntent().getBooleanExtra(
								AppConstants.SYSTEM_START_TYPE_APP2APP, false));
				intent.putExtra(AppConstants.SYSTEM_APP2APP_TYPE, getIntent()
						.getIntExtra(AppConstants.SYSTEM_APP2APP_TYPE, -1));
				startActivity(intent);
			}
			finish();
			return;
		}

		if (getIntent().getAction() != null
				&& !"".equals(getIntent().getAction())
				&& "master_clear".equals(getIntent().getAction())) {
			finish();
			System.exit(0);
			return;
		}

		MXCurrentUser currentUser = MXAPI.getInstance(this).currentUser();
		if (currentUser == null) {
			Intent intent = new Intent(this, LoadingActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
			return;
		}

		refreshNetworkName(currentUser);

		int groupID = getIntent().getIntExtra(
				MXConstants.IntentKey.SHOW_CURRENT_GROUP_WORK_CIRCLE, -1);
		if (groupID != -1) {
			// if (!hideCircle) {
			switchToCircle(groupID);
			// }
			return;
		}

		if (getIntent().getBooleanExtra(SHOW_CHAT_LIST_FLAG, false)) {
			getIntent().removeExtra(SHOW_CHAT_LIST_FLAG);
			int chatID = getIntent().getIntExtra(AUTO_ENTER_CHAT_ID, -999);
			if (chatID == -999) {
				return;
			}
			NotificationUtil.clearAllNotification(this);
			getIntent().removeExtra(AUTO_ENTER_CHAT_ID);
			isAutoEnterChat = true;
			autoEnterChatID = chatID;
			if (BackgroundDetector.getInstance().isGesturePwdViewEnabled(this)
					|| BackgroundDetector.getInstance().isPasswordCheckActive()) {
				return;
			}
		}
		autoEnterChat();

		String app2appTabSheet = getIntent().getStringExtra(
				AppConstants.SYSTEM_APP2APP_TYPE_TAB_SHEET_VALUE);
		if (app2appTabSheet != null && !"".equals(app2appTabSheet)) {
			getIntent().removeExtra(
					AppConstants.SYSTEM_APP2APP_TYPE_TAB_SHEET_VALUE);
			showTabByTag(app2appTabSheet);
		}
	}

	private void autoEnterChat() {
		if (!isAutoEnterChat || autoEnterChatID == -1) {
			isAutoEnterChat = false;
			autoEnterChatID = -1;
			return;
		}
		if (getResources().getString(R.string.client_tab_item_tag_chat).equals(mTabHost.getCurrentTabTag())) {
			Intent autoEnterIntent = new Intent(
					MXConstants.BroadcastAction.MXKIT_AUTO_ENTER_CHAT);
			autoEnterIntent
					.putExtra(MXConstants.IntentKey.SHOW_CURRENT_CHAT_ID,
							autoEnterChatID);
			sendBroadcast(autoEnterIntent, MXKit.getInstance()
					.getAppSignaturePermission());
		} else {
			Intent intent = mTabHost.getMenuTabIntentByTag(R.string.client_tab_item_tag_chat);
			intent.putExtra(MXConstants.IntentKey.SHOW_CURRENT_CHAT_ID,
					autoEnterChatID);
			mTabHost.setCurrentTabByTag(getResources().getString(R.string.client_tab_item_tag_chat));
		}
		isAutoEnterChat = false;
		autoEnterChatID = -1;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		try {
			this.unregisterReceiver(receiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();

		MXAPI.getInstance(getApplicationContext());
		MXUIEngine.getInstance().clearAllManager();
	}

	protected void updateNetworkTitle(String name) {
		for(View view : handleViewList){
			TextView networkNameView = (TextView) view.findViewById(R.id.current_network);
			networkNameView.setText(name);
		}
	}

	private void switchToCircle(int groupID) {
		MXUIEngine.getInstance().switchCircleGroup(groupID);
		getIntent().removeExtra(
				MXConstants.IntentKey.SHOW_CURRENT_GROUP_WORK_CIRCLE);
		mTabHost.setCurrentTabByTag(getResources().getString(R.string.client_tab_item_tag_circle));
	}

	private void refreshAlertIcon() {
		MXCurrentUser currentUser = MXAPI.getInstance(this).currentUser();
		for(View view : handleViewList){
			refreshHandlerLayout(view, currentUser);
		}
	}

	private void refreshHandlerLayout(View handlerView,
									  MXCurrentUser currentUser) {
		if (handlerView == null) {
			return;
		}
		TextView unreadNum = (TextView) handlerView
				.findViewById(R.id.sms_num);
		ImageView unreadIcon = (ImageView) handlerView
				.findViewById(R.id.work_circle_icon);

		List<MXNetwork> networks = currentUser.getNetworks();
		boolean isCircleUnread = false;
		for (MXNetwork network : networks) {
			if (currentUser.getNetworkID() != network.getId()) {
				if (!isCircleUnread) {
					isCircleUnread = MXAPI.getInstance(this)
							.checkNetworkCircleUnread(network.getId());
				}
			}
		}

		int unReadChatMessage = MXAPI.getInstance(this)
				.queryNonCurrentNetworkChatUnread(
						currentUser.getNetworkID());

		if (unReadChatMessage > 0 || isCircleUnread) {
			if (unReadChatMessage > 0) {
				unreadNum.setVisibility(View.VISIBLE);
				unreadIcon.setVisibility(View.GONE);
				String unReadCount = unReadChatMessage <= 99 ? String
						.valueOf(unReadChatMessage) : "99+";
				unreadNum.setText(unReadCount);
			} else {
				unreadNum.setVisibility(View.GONE);
				unreadIcon.setVisibility(View.VISIBLE);
				unreadNum.setText("");
			}
		} else {
			unreadNum.setVisibility(View.GONE);
			unreadIcon.setVisibility(View.GONE);
			unreadNum.setText("");
		}

		if (unReadChatMessage == 0 && !isCircleUnread) {
			if (PreferenceUtils.checkUpgradeMark(this)) {
				unreadIcon.setVisibility(View.VISIBLE);
			} else {
				unreadIcon.setVisibility(View.GONE);
			}
		}
	}

	private void showTabByTag(String tag) {
		if (mTabHost != null) {
			mTabHost.setCurrentTabByTag(tag);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (!this.getLocalActivityManager().getCurrentActivity()
						.onKeyDown(keyCode, event)) {
					moveTaskToBack(true);
					return true;
				} else {
					return true;
				}
		}
		return super.onKeyDown(keyCode, event);
	}

	@TargetApi(21/* Build.VERSION_CODES.LOLLIPOP */)
	private void handleStatusBarColor() {
		if (Build.VERSION.SDK_INT >= 21/* Build.VERSION_CODES.LOLLIPOP */) {
			Window window = getWindow();
			// clear FLAG_TRANSLUCENT_STATUS flag:
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			// finally change the color
			window.setStatusBarColor(getResources().getColor(
					R.color.status_bar_color));
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		functionPopMenu.dismiss();
		circleTopRightPopMenu.dismiss();

		if (mTabHost.getCurrentTabTag()
				.equals(MenuTabHost.TAB_TAG_CIRCLES)) {
			Activity circleActivity = MXUIEngine.getInstance().getCircleManager().getActivity();
			if (circleActivity != null){
				circleActivity.onConfigurationChanged(newConfig);
			}
		}else if (mTabHost.getCurrentTabTag()
				.equals(MenuTabHost.TAB_TAG_CONTACTS)) {
			Activity contactActivity = MXUIEngine.getInstance().getContactManager().getContactActivity();
			if (contactActivity != null){
				contactActivity.onConfigurationChanged(newConfig);
			}
		}else if (mTabHost.getCurrentTabTag()
				.equals(MenuTabHost.TAB_TAG_APP_CENTER)) {
			Activity appCenterActivity = MXUIEngine.getInstance().getAppCenterManager().getAppCenterActivity();
			if (appCenterActivity != null){
				appCenterActivity.onConfigurationChanged(newConfig);
			}
		}else if (mTabHost.getCurrentTabTag()
				.equals(MenuTabHost.TAB_TAG_CONTACTS)) {
		}
	}
}
