package com.minxing.client.tab;

import android.app.Activity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TabHost;

import com.minxing.client.R;
import com.minxing.kit.MXConstants;
import com.minxing.kit.plugin.web.MXWebActivity;

import com.minxing.kit.MXKit;

public class MenuTabHost extends TabHost {

	public final static String TAB_TAG_CHAT = "chat";
	public final static String TAB_TAG_CONTACTS = "contacts";
	public final static String TAB_TAG_APP_CENTER = "appcenter";
	public final static String TAB_TAG_CIRCLES = "circle";

	private List<MenuTabItem> menuTabItemList = new ArrayList<MenuTabItem>();
	private Context mContext;


	public MenuTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public MenuTabHost(Context context) {
		super(context);
	}

	@Override
	public void setCurrentTab(int index) {
		MenuTabItem menuTabItem = getMenuTabItemByIndex(index);

		//tab页点击次数统计
		if (menuTabItem.getTag() != null) {
			String eventId = null;
			HashMap<String, String> map_value = new HashMap<String, String>();
			if (TAB_TAG_CHAT.equals(menuTabItem.getTag())) {
				eventId = "mx_im_click";
			} else if (TAB_TAG_CONTACTS.equals(menuTabItem.getTag())) {
				eventId = "mx_contacts_click";
			} else if (TAB_TAG_APP_CENTER.equals(menuTabItem.getTag())) {
				eventId = "mx_apps_click";
			} else if (TAB_TAG_CIRCLES.equals(menuTabItem.getTag())) {
				eventId = "mx_circles_click";
			}
			MXKit.getInstance().callBackClickNumberEvent(mContext, eventId,map_value);
		}

		if (index == getCurrentTab()) {
			if (null != menuTabItem.getOnReClickListener()) {
				menuTabItem.getOnReClickListener().onReClick(menuTabItem);
			}
		} else {
			if(null != menuTabItem.getBeforeTabChangeListener()){
				menuTabItem.getBeforeTabChangeListener().beforeTabChange(menuTabItem);
			}
			super.setCurrentTab(index);
		}
	}

	public void addMenuItems(List<MenuTabItem> menuTabItemList ){
		for(MenuTabItem item : menuTabItemList){
			Log.i("ClientTabActivity", "[addMenuItems]item.getName "+item.getName());
			addMenuItem(item);
		}
	}

	public void addMenuItem(MenuTabItem tabItem){
		TabHost.TabSpec tSpec = newTabSpec(tabItem.getTag());
		tSpec.setContent(tabItem.getIntent());
		tSpec.setIndicator(tabItem.getView());
		addTab(tSpec);
	}

	private MenuTabItem getMenuTabItemByIndex(int index) {
		return menuTabItemList.get(index);
	}

	public MenuTabItem getMenuTabItemByTag( int id) {
		String tag = getResources().getString(id);
		for (MenuTabItem item : menuTabItemList) {
			if (tag.equals(item.getTag())) {
				return item;
			}
		}
		return null;
	}

	public Intent getMenuTabIntentByTag(int id) {
		String tag = getResources().getString(id);
		for (MenuTabItem item : menuTabItemList) {
			if (tag.equals(item.getTag())) {
				return item.getIntent();
			}
		}
		return null;
	}

	public List<MenuTabItem> getMenuTabItemList() {
		return menuTabItemList;
	}

	public List<MenuTabItem> generateTabItemList(Context context) {
		TypedArray client_tab_item_tags = context.getResources().obtainTypedArray(R.array.client_tab_item_tags);
		String[]  client_tab_item_launchers = context.getResources().getStringArray(R.array.client_tab_item_launchers);
		TypedArray client_tab_item_names = context.getResources().obtainTypedArray(R.array.client_tab_item_names);
		TypedArray   client_tab_item_drawables = context.getResources().obtainTypedArray(R.array.client_tab_item_drawables);
		for (int i =0;i<client_tab_item_tags.length();i++) {
			MenuTabItem tabItem  = generateTabItem(context, client_tab_item_tags.getResourceId(i, -1), client_tab_item_launchers[i], client_tab_item_names.getResourceId(i, -1), client_tab_item_drawables.getResourceId(i, -1));
			menuTabItemList.add(tabItem);
		}
		return menuTabItemList;
	}

	private MenuTabItem generateTabItem(Context context, int client_tab_item_id, String client_tab_item_launcher, int client_tab_item_name, int client_tab_item_drawable) {
		String  tag =context.getResources().getString( client_tab_item_id);
		String launcher = client_tab_item_launcher;
		String name = context.getResources().getString(client_tab_item_name);
		Drawable icon = context.getResources().getDrawable(client_tab_item_drawable);

		Log.i("ClientTabActivity", "[generateTabItem]item.tag: "+tag);
		Log.i("ClientTabActivity", "[generateTabItem]item.launcher: "+launcher);
		Log.i("ClientTabActivity", "[generateTabItem]item.name: "+name);
		Intent intent = null;
		int tabType = MenuTabItem.TAB_ITEM_TYPE_NATIVE;
		if (!launcher.startsWith("http") && !launcher.startsWith("launchApp://")) {
			try {
				Class<?> c = null;
				if ((launcher != null) && !("".equals(launcher))) {
					c = Class.forName(launcher);
				}
				if (c != null & Activity.class.isAssignableFrom(c)) {

					intent = new Intent(context, c);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			tabType = MenuTabItem.TAB_ITEM_TYPE_NATIVE;
		}else{
			intent = new Intent(context,MXWebActivity.class);
			intent.putExtra(MXConstants.IntentKey.MXKIT_WEB_LAUNCH_URL, launcher);
			intent.putExtra("CustomSetting",true);
			tabType = MenuTabItem.TAB_ITEM_TYPE_WEB;
		}
		MenuTabItem tabItem = new MenuTabItem(context, tag, intent, icon, name);
		tabItem.setTabType(tabType);

		return tabItem;
	}


}
