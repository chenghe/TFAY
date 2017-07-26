package com.minxing.client.tab;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.minxing.client.R;

public class MenuTabItem {

	public final static int TAB_ITEM_TYPE_NATIVE = 0;
	public final static int TAB_ITEM_TYPE_WEB = 1;

	private Context context;
	private String tag;
	private Intent intent;
	private Drawable imageRes;
	private String name;
	private View view;
	private TextView unreadTV;
	private ImageView unreadIM;
	private OnReClickListener onReClickListener;
	private BeforeTabChangeListener beforeTabChangeListener;
	private int tabType = TAB_ITEM_TYPE_NATIVE;

	@SuppressWarnings("deprecation")
	public MenuTabItem(Context context, String tag, Intent intent, Drawable imageRes, String name) {
		this.context = context;
		this.tag = tag;
		this.intent = intent;
		this.imageRes = imageRes;
		this.name = name;

		view = LayoutInflater.from(this.context).inflate(R.layout.menu_tab_view, null);
		TextView tv = (TextView) view.findViewById(R.id.menu_tab_name);
		if (imageRes == null) {
			imageRes = context.getResources().getDrawable(R.drawable.tab_appcenter);
		}
		tv.setCompoundDrawablesWithIntrinsicBounds(null, imageRes, null, null);
		tv.setText(name);

		unreadTV = (TextView) view.findViewById(R.id.main_tab_unread_tv);
		unreadIM = (ImageView) view.findViewById(R.id.main_tab_unread_image);
	}

	public void showNumberMarker(String number) {
		if(unreadTV != null){
			unreadTV.setVisibility(View.VISIBLE);
			unreadTV.setText(number);
		}
	}

	public void hideNumberMarker() {
		if(unreadTV != null){
			unreadTV.setVisibility(View.INVISIBLE);
			unreadTV.setText("");
		}
	}

	public void showMarker() {
		if(unreadIM != null){
			unreadIM.setVisibility(View.VISIBLE);
		}
	}

	public void hideMarker() {
		if(unreadIM != null){
			unreadIM.setVisibility(View.INVISIBLE);
		}
	}

	public Intent getIntent() {
		return intent;
	}

	public void setIntent(Intent intent) {
		this.intent = intent;
	}

	public Drawable getImageRes() {
		return imageRes;
	}

	public void setImageRes(Drawable imageRes) {
		this.imageRes = imageRes;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public View getView() {
		return view;
	}

	public TextView getUnreadTV() {
		return unreadTV;
	}

	public ImageView getUnreadIM() {
		return unreadIM;
	}

	public OnReClickListener getOnReClickListener() {
		return onReClickListener;
	}

	public void setOnReClickListener(OnReClickListener onReClickListener) {
		this.onReClickListener = onReClickListener;
	}

	public BeforeTabChangeListener getBeforeTabChangeListener() {
		return beforeTabChangeListener;
	}

	public void setBeforeTabChangeListener(BeforeTabChangeListener beforeTabChangeListener) {
		this.beforeTabChangeListener = beforeTabChangeListener;
	}

	public interface OnReClickListener {
		public void onReClick(MenuTabItem menuTabItem);
	}

	public interface BeforeTabChangeListener {
		public void beforeTabChange(MenuTabItem menuTabItem);
	}

	public int getTabType() {
		return tabType;
	}

	public void setTabType(int tabType) {
		this.tabType = tabType;
	}
}
