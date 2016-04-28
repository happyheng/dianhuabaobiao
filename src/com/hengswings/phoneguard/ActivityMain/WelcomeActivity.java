package com.hengswings.phoneguard.ActivityMain;

import java.util.Timer;
import java.util.TimerTask;

import com.hengswings.phoneguard.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class WelcomeActivity extends Activity {
	public final int wait_time = 1000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏,这个必须在setContentView之前
		setContentView(R.layout.welcome);
		
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				Intent intent = new Intent(WelcomeActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
				// 这个应该是设置退出的动作
				overridePendingTransition(android.R.anim.fade_in,
						android.R.anim.fade_out);
			}

		};
		Timer timer = new Timer();
		// 应该是为上方的时间表设置时间
		timer.schedule(task, wait_time);
	}

}
