package com.minxing.client.demo;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.minxing.client.R;
import com.minxing.client.util.Utils;
import com.minxing.kit.api.MXAPI;
import com.minxing.kit.api.bean.AddressContact;
import com.minxing.kit.api.bean.MXCurrentUser;
import com.minxing.kit.api.bean.MXError;
import com.minxing.kit.api.bean.MXUser;
import com.minxing.kit.api.callback.AddressContactsCallback;
import com.minxing.kit.api.callback.MXJsonCallBack;
import com.minxing.kit.api.callback.UserCallback;
import com.minxing.kit.internal.common.bean.im.ConversationGraph;

public class SystemAppDemoChatActivity extends Activity {
	private ImageButton leftBackButton;
	private TextView title;
	private MXCurrentUser currentUser;
	private LinearLayout starChatCallBack;
	private LinearLayout starContactConfig;
	private LinearLayout selectUser;
	private LinearLayout selectAddressContact;
	private LinearLayout queryNetworkUnread;
	private LinearLayout queryNoCurrentNetworkUnread;

	private LinearLayout switchNetwork;
	private LinearLayout createGraph;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_app_demo_chat);
		currentUser = MXAPI.getInstance(SystemAppDemoChatActivity.this).currentUser();
		title = (TextView) findViewById(R.id.title_name);
		title.setText("chat Demo");
		leftBackButton = (ImageButton) findViewById(R.id.title_left_button);
		leftBackButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		starChatCallBack = (LinearLayout) findViewById(R.id.appdemo_star_chat_callback);
		starChatCallBack.setVisibility(View.GONE);
		starChatCallBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		starContactConfig = (LinearLayout) findViewById(R.id.appdemo_star_contact_config);
		starContactConfig.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MXAPI.getInstance(SystemAppDemoChatActivity.this).contactsConfig(SystemAppDemoChatActivity.this);
			}
		});
		selectUser = (LinearLayout) findViewById(R.id.appdemo_select_user);
		selectUser.setVisibility(View.GONE);
		selectUser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		selectAddressContact = (LinearLayout) findViewById(R.id.appdemo_select_address_contacts);
		selectAddressContact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectAddressContacts();
			}
		});
		queryNetworkUnread = (LinearLayout) findViewById(R.id.appdemo_query_network_unread);
		queryNetworkUnread.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int unread = MXAPI.getInstance(SystemAppDemoChatActivity.this).queryNetworkChatUnread(currentUser.getNetworkID());
				Utils.toast(SystemAppDemoChatActivity.this, getString(R.string.interface_query_network_unread) + "：" + unread, Toast.LENGTH_SHORT);
			}
		});
		queryNoCurrentNetworkUnread = (LinearLayout) findViewById(R.id.appdemo_query_oncurrent_network_unread);
		queryNoCurrentNetworkUnread.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int unread = MXAPI.getInstance(SystemAppDemoChatActivity.this).queryNonCurrentNetworkChatUnread(currentUser.getNetworkID());
				Utils.toast(SystemAppDemoChatActivity.this, getString(R.string.interface_query_oncurrent_network_unread) + "：" + unread,
						Toast.LENGTH_SHORT);
			}
		});

		switchNetwork = (LinearLayout) findViewById(R.id.appdemo_switch_network);
		switchNetwork.setVisibility(View.GONE);
		switchNetwork.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MXAPI.getInstance(SystemAppDemoChatActivity.this).switchNetwork(SystemAppDemoChatActivity.this, currentUser.getNetworkID());
			}
		});
		createGraph = (LinearLayout) findViewById(R.id.appdemo_create_graph);
		createGraph.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				createNewGraph();

			}
		});
	}

	private void createNewGraph() {
		MXAPI.getInstance(SystemAppDemoChatActivity.this).selectUser(SystemAppDemoChatActivity.this, getString(R.string.interface_create_graph),
				true, true, new UserCallback() {

					@Override
					public void onSuccess() {

					}

					@Override
					public void onLoading() {

					}

					@Override
					public void onFail(MXError error) {

					}

					@Override
					public void onResult(List<MXUser> userList) {

						if (userList != null) {
							String[] loginNames = new String[userList.size()];
							MXCurrentUser user = MXAPI.getInstance(SystemAppDemoChatActivity.this).currentUser();
							for (int i = 0; i < userList.size(); i++) {
								loginNames[i] = userList.get(i).getLoginName();
							}
							ConversationGraph graph = new ConversationGraph();
							graph.setImage(user.getAvatarUrl());
							graph.setTitle(getString(R.string.interface_create_graph));
							graph.setUrl("http://www.minxing365.com/");
							MXAPI.getInstance(SystemAppDemoChatActivity.this).createGraph(SystemAppDemoChatActivity.this, graph, loginNames,
									new MXJsonCallBack() {

										@Override
										public void onSuccess() {
										}

										@Override
										public void onLoading() {
										}

										@Override
										public void onFail(MXError error) {
										}

										@Override
										public void onResult(String result) {
										}
									});
						}
					}

					@Override
					public void onResult(MXUser user) {

					}
				});
	}

	private void selectAddressContacts() {
		MXAPI.getInstance(SystemAppDemoChatActivity.this).selectAddressContacts(SystemAppDemoChatActivity.this,
				getString(R.string.interface_select_address_contacts), new AddressContactsCallback() {

					@Override
					public void onSuccess() {
						Utils.toast(SystemAppDemoChatActivity.this, "success", Toast.LENGTH_SHORT);
					}

					@Override
					public void onLoading() {
						Utils.toast(SystemAppDemoChatActivity.this, "loading...", Toast.LENGTH_SHORT);
					}

					@Override
					public void onFail(MXError error) {
						Utils.toast(SystemAppDemoChatActivity.this, "fail", Toast.LENGTH_SHORT);
					}

					@Override
					public void onResult(List<AddressContact> contacts) {
						Utils.toast(SystemAppDemoChatActivity.this, "success", Toast.LENGTH_SHORT);
					}
				});
	}
}
