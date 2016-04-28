package com.hengswings.phoneguard.ActivityMain;


import com.hengswings.phoneguard.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class AppExitDialogActivity extends Activity {
	public static String exit_Action="app_exit";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exit_dialog);

	}


	public void exitbutton1(View v) {
		this.finish();
		
	}

	public void exitbutton0(View v) {
		Intent intent = new Intent();  
		intent.setAction(exit_Action); // 退出动作  
		this.sendBroadcast(intent);// 发送广播  
		//退出后台线程,以及销毁静态变量  
		this.finish();
		//退出程序
		//System.exit(0);
	}

}
