package com.hengswings.phoneguard.ServiceAndBroadCast;

import java.lang.reflect.Method;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.hengswings.phoneguard.R;
import com.hengswings.phoneguard.ActivitySet.SetActivity;
import com.hengswings.phoneguard.myUtils.MySharedPreferences;
import com.hengswings.phoneguard.myUtils.SendMessage;


public class TelInternalBroadcastReceiver extends BroadcastReceiver {
	public String number = "15131201812";
	public SendMessage sendMessage;
	private Context context;
	/* 白名单 */
	private List<String> pass = null;

	private MySharedPreferences mySharedPreferences;

	public TelInternalBroadcastReceiver(List<String> pass) {
		/* 初始化发送短信的类 */
		sendMessage = new SendMessage();
		/* 白名单 */
		this.pass = pass;

	}

	public void onReceive(Context context, Intent intent) {

		this.context = context;
		// BlockList b=new BlockList(context);

		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {// Log.e("msg",
																			// "calling");
			// 如果是去电（拨出）
			String num = getResultData();

			if (num.equals(number)) {
				setResultData(null); // 清除电话
				// break;
			}

		} else { // 由于android没有来电广播所以，去掉拨打电话就是来电状态了
					// Log.e("msg", "coming");

			String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

			// 得到打进来的电话号码
			String number = intent
					.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
			if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {
				/**
				 * 如果pass不为空且包含打进来的电话号码，说明此电话在白名单中 ,所以不拦截.而且还要把声音调到平常的声音
				 */
				if (pass != null && !(pass.isEmpty()) && pass.contains(number)) {
					// 先不恢复正常铃声
					// mAudioManager
					// .setRingerMode(AudioManager.RINGER_MODE_NORMAL);
					// break;
				} else {
					/**
					 * 未在白名单中，说明要拦截掉这个电话
					 */


					try {
						/*
						 * //挂断电话 方法一 Method method = Class.forName(
						 * "android.os.ServiceManager").getMethod( "getService",
						 * String.class); // 获取远程TELEPHONY_SERVICE的IBinder对象的代理
						 * IBinder binder = (IBinder) method.invoke(null, new
						 * Object[] { Context.TELEPHONY_SERVICE }); //
						 * 将IBinder对象的代理转换为ITelephony对象 ITelephony telephony =
						 * ITelephony.Stub .asInterface(binder); // 挂断电话
						 * telephony.endCall(); Log.e("msg", "end");
						 */
						// 挂断电话 方法二
						ITelephony iTelephony = getITelephony(context); // 获取电话接口
						iTelephony.endCall(); // 挂断电话

						// 先判断允许不允许发回短信
						mySharedPreferences = new MySharedPreferences(context);
						boolean is_return = mySharedPreferences.getBoolean(
								SetActivity.is_return__message, true);
						// 如果允许
						if (is_return) {
							// 如果要发回短信，看看是否到界限了
							boolean allow_send_message = is_return(number);
						
							// 如果是要发回短信
							if (allow_send_message) {
								SharedPreferences sharedPreferences1 = context
										.getSharedPreferences(
												SetActivity.main,
												Context.MODE_PRIVATE);
								String return_context = sharedPreferences1
										.getString(
												SetActivity.return_context,
												"您好，我现在不方便接听电话，稍后我会联系您");
								// 加上信息，说明此条信息是由app发送的
								return_context = return_context
										+ "(此条信息由"+context.getString(R.string.app_name)+"自动发送)";
								// 给打过来的电话发送短信
								sendMessage.send(number, return_context,
										context);
								// 将手机号传输进去，然后在XML文件中将次数加1
								Write_time(number);
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		}
	}

	/**
	 * 
	 * @param num
	 *            传进来的拦截的手机号，判断是否要返回短信
	 * @return 返回true，说明需要返回短信，返回false，说明不要返回短信
	 */
	private boolean is_return(String num) {
		boolean is_return = false;
		// 得到今天的年月日
		Time t = new Time();
		t.setToNow();
		String year = String.valueOf(t.year);
		String month = String.valueOf(t.month);
		String day = String.valueOf(t.monthDay);

		SharedPreferences sharedPreferences = context.getSharedPreferences(
				SetActivity.main, Context.MODE_PRIVATE);
		// 得到XML中键为"all+year+month+day"的值n1
		int n1 = sharedPreferences.getInt("all" + year + month + day, 0);
		// 得到XML中键为"year+month+day+num"的值n2
		int n2 = sharedPreferences.getInt(year + month + day + num, 0);
		// 得到2号xml文件中的值，即每日回发短信次数
		int one_day_max = sharedPreferences.getInt(SetActivity.day_back,
				20);
		// 得到3号xml文件中的值，即每日同一手机号回发短信次数
		int one_num_preday_max = sharedPreferences.getInt(
				SetActivity.oneday_phone_back, 3);
		// 如果今天发的总短信数小于规定的并且这个手机号今天发送的也小于规定的话，说明可以回发短信
		if (n1 < one_day_max && n2 < one_num_preday_max) {
			is_return = true;
		}

		return is_return;
	}

	/**
	 * @param 传过来的手机号
	 *            ，此方法就是将今天的总手机次数加1，还有这个手机号的次数加1
	 */
	private void Write_time(String number2) {
		// 得到今天的年月日
		Time t = new Time();
		t.setToNow();
		String year = String.valueOf(t.year);
		String month = String.valueOf(t.month);
		String day = String.valueOf(t.monthDay);

		SharedPreferences sharedPreferences = context.getSharedPreferences(
				SetActivity.main, Context.MODE_PRIVATE);
		// 得到XML中键为"all+year+month+day"的值n1,即今天发的总短信数
		int n1 = sharedPreferences.getInt("all" + year + month + day, 0);
		// 得到XML中键为"year+month+day+num"的值n2,即今天这个手机号发的总短信数
		int n2 = sharedPreferences.getInt(year + month + day + number2, 0);
		// 短信数都加1
		n1 = n1 + 1;
		n2 = n2 + 1;
		Editor editor = sharedPreferences.edit();
		// 写进去
		editor.putInt("all" + year + month + day, n1);
		editor.putInt(year + month + day + number2, n2);
		editor.commit();
	}

	/**
	 * 根据反射获取end()方法2
	 * 
	 * @param context
	 * @return
	 */
	private static ITelephony getITelephony(Context context) {
		ITelephony iTelephony = null;
		TelephonyManager mTelephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		Class<TelephonyManager> c = TelephonyManager.class;
		Method getITelephonyMethod = null;
		try {
			getITelephonyMethod = c.getDeclaredMethod("getITelephony",
					(Class[]) null); // 获取声明的方法
			getITelephonyMethod.setAccessible(true);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		try {
			iTelephony = (ITelephony) getITelephonyMethod.invoke(
					mTelephonyManager, (Object[]) null); // 获取实例
			return iTelephony;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iTelephony;
	}

}
