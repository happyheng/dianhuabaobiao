package com.hengswings.phoneguard.ActivityMain;


import com.hengswings.phoneguard.R;
import com.hengswings.phoneguard.ActivitySet.SetActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;

/**
 * 这是刚进去的第一个Activity
 * 注意这个Activity是透明的,是做判断用的
 * @author liuheng
 *
 */
public class FirstActivity extends Activity {
	
	public final String short_cut = "short_cut";

	public final static String is_first_log_in = "is_first_use";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏,这个必须在setContentView之前
		setContentView(R.layout.main);

		Intent intentShortCut = getIntent();
		boolean isShortCut = intentShortCut.getBooleanExtra(short_cut,
				false);
		if (isShortCut) {
			Intent intent = new Intent(
					"com.example.teacherassistant.clock_service");
			startService(intent);
			finish();
			return;
		} else {
			// 看用户是否是第一次启动
			SharedPreferences sharedPreferences = getSharedPreferences(
					SetActivity.main, Context.MODE_PRIVATE);
			boolean is_first = sharedPreferences.getBoolean(is_first_log_in,
					true);
			// 如果是第一次启动，开启引导界面
			if (is_first) {
				Intent intent1 = new Intent(FirstActivity.this, GuideActivity.class);
				startActivity(intent1);
				finish();
				return;
			}
			// 如果没有，说明不是由快捷方式启动的，那么启动欢迎界面
			Intent intent2 = new Intent(FirstActivity.this, WelcomeActivity.class);
			startActivity(intent2);
			finish();
			return;
		}

	}

}
