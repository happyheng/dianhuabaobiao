package com.hengswings.phoneguard.ActivityMain;

import java.util.ArrayList;

import com.hengswings.phoneguard.R;
import com.hengswings.phoneguard.ActivitySet.SetActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class GuideActivity extends Activity {
	private ViewPager mViewPager;
	private ImageView mPage0;
	private ImageView mPage1;
	private ImageView mPage2;
	private ImageView mPage3;

	private int currIndex = 0;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_guide);
		// 获取当前系统的版本号
		// int currentApiVersion = android.os.Build.VERSION.SDK_INT;
		// if (currentApiVersion >= 11) {
		// /* 这个方法只有API版本大于11的时候才能用 */
		// ActionBar actionBar = getActionBar();
		// actionBar.setDisplayShowHomeEnabled(false);
		// actionBar.setTitle("操作指南");
		// }

		mViewPager = (ViewPager) findViewById(R.id.whatsnew_viewpager);
		// 找到点
		mPage0 = (ImageView) findViewById(R.id.page0);
		mPage1 = (ImageView) findViewById(R.id.page1);
		mPage2 = (ImageView) findViewById(R.id.page2);
		mPage3 = (ImageView) findViewById(R.id.page3);

		// 将要分页显示的View装入数组中
		LayoutInflater mLi = LayoutInflater.from(this);
		View view1 = mLi.inflate(R.layout.guide1, null);
		View view2 = mLi.inflate(R.layout.guide2, null);
		View view3 = mLi.inflate(R.layout.guide3, null);
		View view4 = mLi.inflate(R.layout.guide4, null);

		// 每个页面的view数据
		final ArrayList<View> views = new ArrayList<View>();
		views.add(view1);
		views.add(view2);
		views.add(view3);
		views.add(view4);

		// 填充ViewPager的数据适配器
		PagerAdapter mPagerAdapter = new PagerAdapter() {

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
		};

		// 设置适配器
		mViewPager.setAdapter(mPagerAdapter);
		// 设置界面移动监听器
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:
				mPage0.setImageDrawable(getResources().getDrawable(
						R.drawable.page_now));
				mPage1.setImageDrawable(getResources().getDrawable(
						R.drawable.page));
				break;
			case 1:
				mPage1.setImageDrawable(getResources().getDrawable(
						R.drawable.page_now));
				mPage0.setImageDrawable(getResources().getDrawable(
						R.drawable.page));
				mPage2.setImageDrawable(getResources().getDrawable(
						R.drawable.page));
				break;
			case 2:
				mPage2.setImageDrawable(getResources().getDrawable(
						R.drawable.page_now));
				mPage1.setImageDrawable(getResources().getDrawable(
						R.drawable.page));
				mPage3.setImageDrawable(getResources().getDrawable(
						R.drawable.page));
				break;
			case 3:
				mPage3.setImageDrawable(getResources().getDrawable(
						R.drawable.page_now));
				
				mPage2.setImageDrawable(getResources().getDrawable(
						R.drawable.page));
				break;
			}
			currIndex = arg0;
		
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		public void onPageScrollStateChanged(int arg0) {
		}
	}

	public void startbutton(View v) {
		/* 将第一次启动的标志位置为False */
		SharedPreferences sharedPreferences = getSharedPreferences(
				SetActivity.main, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(FirstActivity.is_first_log_in, false);
		editor.commit();
		/* 第一次启动，来个快捷方式 */
		create_shortCut();
		/* 给主界面发一个消息，说明我是第一次进入此程序的 */
		Intent intent = new Intent();
		intent.putExtra(MainActivity.is_first, true);
		intent.setClass(GuideActivity.this, MainActivity.class);
		startActivity(intent);
		this.finish();
	}

	/**
	 * 创建快捷方式的方法
	 */
	private void create_shortCut() {
		Intent addIntent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		Parcelable icon = Intent.ShortcutIconResource.fromContext(this,
				R.drawable.logo);
		Intent myIntent = new Intent(this, FirstActivity.class);
		myIntent.putExtra("short_cut", true);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "一键进入免打扰模式");
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, myIntent);
		sendBroadcast(addIntent);

	}

}