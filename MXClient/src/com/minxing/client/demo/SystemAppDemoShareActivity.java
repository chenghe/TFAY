package com.minxing.client.demo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.minxing.client.R;
import com.minxing.client.util.Utils;
import com.minxing.kit.MXUIEngine;
import com.minxing.kit.api.MXAPI;
import com.minxing.kit.api.bean.MXCircleGroup;
import com.minxing.kit.api.bean.MXCurrentUser;
import com.minxing.kit.api.bean.MXError;
import com.minxing.kit.api.bean.ShareLink;
import com.minxing.kit.api.callback.MXApiCallback;

public class SystemAppDemoShareActivity extends Activity {
	private LinearLayout shareImageToCircle;
	private LinearLayout shareImagesToCircle;
	private LinearLayout shareImageToChat;
	private LinearLayout shareImagesToChat;
	private LinearLayout shareLinkToChat;
	private LinearLayout shareLinkToCircle;
	private LinearLayout autoShareLinkToCircle;
	private LinearLayout shareTextToChat;
	private LinearLayout shareTextToCircle;
	private LinearLayout shareToMail;
	private TextView title;
	private ImageButton leftBackButton;
	private Uri uri = null;
	public static final int FORCHAT = 1;
	public static final int FORCIRCLE = 2;
	public static final int LIST_FORCHAT = 3;
	public static final int LIST_FORCIRCLE = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_app_demo_share);
		title = (TextView) findViewById(R.id.title_name);
		title.setText("share Demo");

		leftBackButton = (ImageButton) findViewById(R.id.title_left_button);
		leftBackButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		shareImageToCircle = (LinearLayout) findViewById(R.id.demo_share_image_circle);
		shareImageToCircle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				choosePhoto(FORCIRCLE);
			}
		});
		shareImagesToCircle = (LinearLayout) findViewById(R.id.demo_share_images_circle);
		shareImagesToCircle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				choosePhoto(LIST_FORCIRCLE);
			}
		});
		shareImageToChat = (LinearLayout) findViewById(R.id.demo_share_image_chat);
		shareImageToChat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				choosePhoto(FORCHAT);
			}
		});
		shareImagesToChat = (LinearLayout) findViewById(R.id.demo_share_images_chat);
		shareImagesToChat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				choosePhoto(LIST_FORCHAT);
			}
		});
		shareLinkToChat = (LinearLayout) findViewById(R.id.demo_share_link_chat);
		shareLinkToChat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MXCurrentUser user = MXAPI.getInstance(SystemAppDemoShareActivity.this).currentUser();
				ShareLink shareLink = new ShareLink();
				shareLink.setThumbnail(user.getAvatarUrl());
				shareLink.setTitle(getString(R.string.interface_share_link_chat));
				shareLink.setUrl("http://www.minxing365.com/");
				MXAPI.getInstance(SystemAppDemoShareActivity.this).shareToChat(SystemAppDemoShareActivity.this, shareLink);
			}
		});
		shareLinkToCircle = (LinearLayout) findViewById(R.id.demo_share_link_circle);
		shareLinkToCircle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MXCurrentUser user = MXAPI.getInstance(SystemAppDemoShareActivity.this).currentUser();
				ShareLink shareLink = new ShareLink();
				shareLink.setThumbnail(user.getAvatarUrl());
				shareLink.setTitle(getString(R.string.interface_share_link_circle));
				shareLink.setUrl("http://www.minxing365.com/");
				MXAPI.getInstance(SystemAppDemoShareActivity.this).shareToCircle(SystemAppDemoShareActivity.this, shareLink);
			}
		});
		shareTextToChat = (LinearLayout) findViewById(R.id.demo_share_text_chat);
		shareTextToChat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MXAPI.getInstance(SystemAppDemoShareActivity.this).shareTextToChat(SystemAppDemoShareActivity.this,
						getString(R.string.interface_share_text_chat));
			}
		});
		shareTextToCircle = (LinearLayout) findViewById(R.id.demo_share_text_circle);
		shareTextToCircle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MXAPI.getInstance(SystemAppDemoShareActivity.this).shareTextToCircle(SystemAppDemoShareActivity.this,
						getString(R.string.interface_share_text_circle));
			}
		});
		autoShareLinkToCircle = (LinearLayout) findViewById(R.id.demo_autoshare_link_circle);
		autoShareLinkToCircle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectGroupForShareToCircle();
			}
		});
		shareToMail = (LinearLayout) findViewById(R.id.demo_share_mail);
		shareToMail.setVisibility(View.GONE);
		shareToMail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			uri = data.getData();
			if (uri == null) {
				return;
			} else {
				if (requestCode == FORCHAT) {
					MXAPI.getInstance(SystemAppDemoShareActivity.this).shareImageToChat(SystemAppDemoShareActivity.this, uri);
				} else if (requestCode == FORCIRCLE) {
					MXAPI.getInstance(SystemAppDemoShareActivity.this).shareImageToCircle(SystemAppDemoShareActivity.this, uri);
				} else if (requestCode == LIST_FORCHAT) {
					List<Uri> list = new ArrayList<Uri>();
					list.add(uri);
					list.add(uri);
					MXAPI.getInstance(SystemAppDemoShareActivity.this).shareImagesToChat(SystemAppDemoShareActivity.this, list);
				} else if (requestCode == LIST_FORCIRCLE) {
					List<Uri> list = new ArrayList<Uri>();
					list.add(uri);
					list.add(uri);
					MXAPI.getInstance(SystemAppDemoShareActivity.this).shareImagesToCircle(SystemAppDemoShareActivity.this, list);
				}
			}
		}
	}

	private void choosePhoto(final int position) {
		AlertDialog.Builder builder = new Builder(SystemAppDemoShareActivity.this);
		builder.setTitle(R.string.share_choose_image_path);
		builder.setItems(new String[] { getString(R.string.share_choose_image_capture), getString(R.string.share_choose_image_photo) },
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							it.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
							if (position == FORCHAT) {
								startActivityForResult(it, FORCHAT);
							} else if (position == FORCIRCLE) {
								startActivityForResult(it, FORCIRCLE);
							} else if (position == LIST_FORCHAT) {
								startActivityForResult(it, LIST_FORCHAT);
							} else if (position == LIST_FORCIRCLE) {
								startActivityForResult(it, LIST_FORCIRCLE);
							}
						} else {
							Intent it = new Intent();
							it.setAction(Intent.ACTION_PICK);
							it.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							if (position == FORCHAT) {
								startActivityForResult(it, FORCHAT);
							} else if (position == FORCIRCLE) {
								startActivityForResult(it, FORCIRCLE);
							} else if (position == LIST_FORCHAT) {
								startActivityForResult(it, LIST_FORCHAT);
							} else if (position == LIST_FORCIRCLE) {
								startActivityForResult(it, LIST_FORCIRCLE);
							}
						}
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	private void selectGroupForShareToCircle() {
		Map<Integer, MXCircleGroup> groupMap = MXUIEngine.getInstance().getCircleManager().getJoinedGroup();
		final LinkedList<Integer> list_ID = new LinkedList<Integer>();
		LinkedList<String> list_Name = new LinkedList<String>();
		if (groupMap == null) {
			return;
		} else {
			for (Integer id : groupMap.keySet()) {
				list_ID.add(id);
				list_Name.add(groupMap.get(id).getName());
			}
		}
		String[] names = new String[list_ID.size()];
		for (int i = 0; i < list_Name.size(); i++) {
			names[i] = list_Name.get(i) + "";
		}
		AlertDialog.Builder builder = new Builder(SystemAppDemoShareActivity.this);
		MXCurrentUser user = MXAPI.getInstance(SystemAppDemoShareActivity.this).currentUser();
		final ShareLink shareLink = new ShareLink();
		shareLink.setThumbnail(user.getAvatarUrl());
		shareLink.setTitle(getString(R.string.interface_auto_share_link_circle));
		shareLink.setUrl("http://www.minxing365.com/");
		builder.setTitle("select group");
		builder.setItems(names, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				MXAPI.getInstance(SystemAppDemoShareActivity.this).shareToCircleAuto(SystemAppDemoShareActivity.this, list_ID.get(which), shareLink,
						getString(R.string.interface_auto_share_link_circle), new MXApiCallback() {
							@Override
							public void onSuccess() {
								Utils.toast(SystemAppDemoShareActivity.this, getString(R.string.interface_auto_share_link_circle), Toast.LENGTH_SHORT);
							}

							@Override
							public void onLoading() {
								Utils.toast(SystemAppDemoShareActivity.this, "loading...", Toast.LENGTH_SHORT);
							}

							@Override
							public void onFail(MXError error) {
								Utils.toast(SystemAppDemoShareActivity.this, "fail", Toast.LENGTH_SHORT);
							}
						});
				dialog.dismiss();

			}
		});
		builder.create().show();
	}

}
