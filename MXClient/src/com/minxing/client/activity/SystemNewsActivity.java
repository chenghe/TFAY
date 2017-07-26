package com.minxing.client.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.minxing.client.LoginActivity;
import com.minxing.client.R;
import com.minxing.client.RootActivity;
import com.minxing.client.util.PreferenceUtils;

public class SystemNewsActivity extends RootActivity {

	private ViewPager mViewPager = null;
	private ImageView mDot1 = null;
	private ImageView mDot2 = null;
	private ImageView mDot3 = null;
	private ImageView mDot4 = null;
	private TextView start_btn = null;

	private boolean isCanLaunch = false;
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHandleStatusColor(false);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.whatsnew_viewpager);
		mViewPager = (ViewPager) findViewById(R.id.whatsnew_viewpager);

		mDot1 = (ImageView) findViewById(R.id.dot_1);
		mDot2 = (ImageView) findViewById(R.id.dot_2);
		mDot3 = (ImageView) findViewById(R.id.dot_3);
		mDot4 = (ImageView) findViewById(R.id.dot_4);

		LayoutInflater mLi = LayoutInflater.from(this);
		View view1 = mLi.inflate(R.layout.system_welcome_1, null);
		View view2 = mLi.inflate(R.layout.system_welcome_2, null);
		View view3 = mLi.inflate(R.layout.system_welcome_3, null);
		View view4 = mLi.inflate(R.layout.system_welcome_4, null);

		start_btn = (TextView) view4.findViewById(R.id.start_btn);
		final ArrayList<View> views = new ArrayList<View>();
		views.add(view1);
		views.add(view2);
		views.add(view3);
		views.add(view4);

		start_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startMainView();
			}
		});

		mViewPager.setAdapter(new PagerAdapter() {
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return views.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(views.get(position));
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(views.get(position));
				return views.get(position);
			}
		});

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:
					mDot1.setImageDrawable(getResources().getDrawable(R.drawable.dot_blue));
					mDot2.setImageDrawable(getResources().getDrawable(R.drawable.dot_gray));
					mDot3.setImageDrawable(getResources().getDrawable(R.drawable.dot_gray));
					mDot4.setImageDrawable(getResources().getDrawable(R.drawable.dot_gray));
					break;
				case 1:
					mDot1.setImageDrawable(getResources().getDrawable(R.drawable.dot_gray));
					mDot2.setImageDrawable(getResources().getDrawable(R.drawable.dot_blue));
					mDot3.setImageDrawable(getResources().getDrawable(R.drawable.dot_gray));
					mDot4.setImageDrawable(getResources().getDrawable(R.drawable.dot_gray));
					break;
				case 2:
					mDot1.setImageDrawable(getResources().getDrawable(R.drawable.dot_gray));
					mDot2.setImageDrawable(getResources().getDrawable(R.drawable.dot_gray));
					mDot3.setImageDrawable(getResources().getDrawable(R.drawable.dot_blue));
					mDot4.setImageDrawable(getResources().getDrawable(R.drawable.dot_gray));
					break;
				case 3:
					mDot1.setImageDrawable(getResources().getDrawable(R.drawable.dot_gray));
					mDot2.setImageDrawable(getResources().getDrawable(R.drawable.dot_gray));
					mDot3.setImageDrawable(getResources().getDrawable(R.drawable.dot_gray));
					mDot4.setImageDrawable(getResources().getDrawable(R.drawable.dot_blue));
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				if (arg0 == 3 && arg2 == 0 && isCanLaunch) {
					startMainView();
				} else if (arg0 == 3 && arg2 == 0) {
					isCanLaunch = true;
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	public void startMainView() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		if (PreferenceUtils.isAPPFTT(this)) {
			PreferenceUtils.saveAPPFTTStatus(this);
		}
		this.finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

}
