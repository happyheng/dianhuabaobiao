package com.hengswings.phoneguard.ServiceAndBroadCast;

import com.hengswings.phoneguard.ActivityClock.ClockRingActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ServiceExitBroadcastReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {

		
		/* 如果时钟到了的话，说明要呼出闹钟了 */
		Bundle bundle = intent.getExtras();

		if (bundle != null) {
			
			Intent clockIntent = new Intent(context, ClockRingActivity.class);
			// 必须要设置标志位，第一个说明是在程序的外面调出Activity，
			// 第二个说明如果调出的Activtivity只是一个功能片段，并没有实际的意义，也没有
			// 必要出现在长按Home键调出最近使用过的程序类表中，那么使用FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
			clockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			context.startActivity(clockIntent);

		
		}

		// 结束service
		Intent stopPhone_Service = new Intent(context, PhoneService.class);
		context.stopService(stopPhone_Service);

	}

}
