package com.hengswings.phoneguard.myUtils;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

public class SendMessage {

	// 第一种方法，调起系统发短信功能 这种用户体验并不是太好， 是跳到 系统中发送短信的界面，
	// 而不是 直接发送，只适用于特定的情况
	// Uri uri =Uri.parse("smsto:10010");
	// Intent it = new Intent(Intent.ACTION_SENDTO, uri);
	// 这个102应该是跳到系统自带的短信应用中是自动添加的短信内容
	// it.putExtra("sms_body", "102");
	// activity.startActivity(it);

	public void send(String phonemunber, String txt, final Context context) {
		// 直接调用短信接口发短信
		// 初始化发短信SmsManager类
		SmsManager smsManager = SmsManager.getDefault();
		// 如果短信内容长度超过70则分为若干条发
		String SENT_SMS_ACTION = "SENT_SMS_ACTION";
		Intent sentIntent = new Intent(SENT_SMS_ACTION);
		PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
				sentIntent, 0);
		if (txt.length() > 70) {
			ArrayList<String> msgs = smsManager.divideMessage(txt);
			for (String msg : msgs) {
				smsManager
						.sendTextMessage(phonemunber, null, msg, sentPI, null);
			}
		} else {
			smsManager.sendTextMessage(phonemunber, null, txt, sentPI, null);
		}

	}

}
