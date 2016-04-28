package com.hengswings.phoneguard.ServiceAndBroadCast;

import java.util.Calendar;
import java.util.List;

import com.hengswings.phoneguard.R;
import com.hengswings.phoneguard.database.MyDBController;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;

public class PhoneService extends Service {
	// 一分钟的时间
	public static final int sMinute = 60000;

	private MediaPlayer mMediaPlayer;

	public final static String sPhoneServiceExit = "com.example.service.phone_service_exit";
	// 服务是否在运行
	public static boolean sIsServiceRun = false;

	// 线程是否在运行 5月29号已经废弃
	// public static boolean is_thread_run = false;

	// 判断是否为闹钟
	public static boolean sIsClock = false;

	// // 闹钟是否已经到时间了
	// public static boolean is_clock_on_time = false;

	/* 核心！拦截电话的类 */
	private TelInternalBroadcastReceiver mTelinternalbroBroadcastReceiver;

	// 运行分钟的键
	public static String sGethour = "sGethour";
	public static String sGetMunite = "sGetMunite";

	// 需要停止的时间
	private int mStopAndClockHour;
	private int mStopAndClockMinute;

	/* 状态栏管理类 */
	private NotificationManager mNotificationManager;

	// 判断时间的线程 5.29已废弃
	// public Thread my_service_thread;

	/* 管理手机震动的类 */
	private Vibrator mVibrator = null;

	/* 自定义数据库操作对象 */
	private MyDBController mDbHelper = null;

	/* 从数据库中读出来的白名单 */
	List<String> mListPass = null;

	// 5.29已废弃
	/* 设置闹钟的Intent */
	// private Intent mIntentAlarm;
	/* 传值的Bundle */
	// private Bundle mClockBundle;

	/* 发送闹钟自毁信息的PendingIntent */
	private PendingIntent mPendingIntent;

	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {

		super.onCreate();

		// 从数据库白名单表中读入到服务中来
		if (mDbHelper == null) {
			mDbHelper = new MyDBController(this);
		}
		// 读取到白名单
		mListPass = mDbHelper.return_pass_num();

		// 关闭数据库
		mDbHelper.Close();
		mDbHelper = null;

		// 设置标志位，即service正在运行中
		sIsServiceRun = true;

		// 震动提醒
		phone_vibrator();

		// 在当前的service中注册一个拦截电话的广播
		mTelinternalbroBroadcastReceiver = new TelInternalBroadcastReceiver(
				mListPass);
		IntentFilter phone_filter = new IntentFilter();
		// 点击源码，会发现与拿来addAction后面加的是String类型，
		phone_filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		// 只有这样才可以，毕竟电话打过来的时候发送的是
		phone_filter.addAction("android.intent.action.PHONE_STATE");
		registerReceiver(mTelinternalbroBroadcastReceiver, phone_filter);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (intent != null) {

			mPendingIntent = getExitPedingIntent(true);

			/**
			 * 注意传过来的intent是不一样的
			 * 1.如果是在主页面或者快捷方式启动的，那么是不限时间的，就不会传过来Intent，即不限时间，只有用户自己可以停止
			 * 2.如果是在my_clock_service启动的，那么只要把正确的时间传过来即可
			 */

			Bundle bundle = intent.getExtras();

			/* 如果不为空,说明是闹钟,并且说明是要定时的 */
			if (bundle != null) {
				try {
					// 主线程休眠0.8秒，目的是让以前的线程结束
					Thread.sleep(800);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				// 说明是闹钟
				sIsClock = true;

				// 得到运行的时间
				mStopAndClockHour = bundle.getInt(sGethour);
				mStopAndClockMinute = bundle.getInt(sGetMunite);

				// 利用AlarmManager来更安全的唤醒闹钟
				// 停止服务和唤醒闹钟的时间类
				Calendar calStopAndClockTime = Calendar.getInstance();
				calStopAndClockTime
						.set(Calendar.HOUR_OF_DAY, mStopAndClockHour);
				calStopAndClockTime.set(Calendar.MINUTE, mStopAndClockMinute);
				calStopAndClockTime.set(Calendar.SECOND, 0);

				Calendar calCurrent = Calendar.getInstance();
				// 判断时间是否正确，如果不正确，说明是下一天，所以要将日期加一
				if (calCurrent.getTimeInMillis() > calStopAndClockTime
						.getTimeInMillis()) {
					calStopAndClockTime.set(Calendar.DAY_OF_YEAR,
							calStopAndClockTime.get(Calendar.DAY_OF_YEAR) + 1);
				}

				// 设置AlarmManager
				AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
				am.set(AlarmManager.RTC_WAKEUP,
						calStopAndClockTime.getTimeInMillis(), mPendingIntent);

			}
			// 创建状态栏
			createNotification();
		}

		return super.onStartCommand(intent, flags, startId);

	}

	/**
	 * 
	 * 根据是否是闹钟返回不同的PendingIntent
	 * 
	 * @param isAlarmPendingIntent
	 *            是否是闹钟的PendingIntent
	 * 
	 * @return 如果是,返回带有Bundle的PendingIntent，如果不是，返回没有Bundle的PendingIntent
	 */
	private PendingIntent getExitPedingIntent(Boolean isAlarmPendingIntent) {

		Intent intent = new Intent();
		intent.setAction(sPhoneServiceExit);

		// 如果是闹钟，那么加上Bundle
		if (isAlarmPendingIntent) {
			Bundle clockBundle = new Bundle();
			clockBundle.putBoolean("is_clock_on_time", true);
			intent.putExtras(clockBundle);
		}

		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				getApplicationContext(), 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		return pendingIntent;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		am.cancel(mPendingIntent);

		// 设置标志位
		sIsServiceRun = false;

		// 停止通知栏
		if (mNotificationManager == null) {
			mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		}
		mNotificationManager.cancel(1);

		// 设置标志位
		sIsClock = false;

		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
		}
		// 将拦截电话的广播接受者finish掉
		this.unregisterReceiver(mTelinternalbroBroadcastReceiver);

	}

	public void createNotification() {

		// 定义通知栏展现的内容信息
		int icon = R.drawable.logo;

		// 开启时显示的内容
		CharSequence tickerText1;
		// 通知栏显示的标题
		CharSequence contentTitle = "拼命拦截电话中";
		// 通知栏显示的主内容
		CharSequence contentText;

		// 如果是闹钟的话
		if (sIsClock) {
			tickerText1 = "免打扰闹钟已启动";
			contentText = "服务将在" + mStopAndClockHour + "时"
					+ mStopAndClockMinute + "分停止并开启闹钟,点击关闭";
		} else {
			tickerText1 = "免打扰服务已启动";
			contentText = "点击关闭";
		}

		Notification notification = new Notification(icon, tickerText1, 0);

		// 看看能不能通过这种方法进行初始化
		// Notification notification2=new Notification();
		//
		// notification2.icon=icon;
		//
		// notification.tickerText=tickerText1;

		// 设置notification是点击自动取消的
		// notification.flags = Notification.FLAG_AUTO_CANCEL;

		// 是不能被清除掉的
		notification.flags = Notification.FLAG_NO_CLEAR;
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;

		// 定义下拉通知栏时要展现的内容信息
		Context context = getApplicationContext();


		// 得到一个不是闹钟的PendingIntent,专为通知栏的点击事件而生
		PendingIntent pendingIntent = getExitPedingIntent(false);

		/**
		 * PendingIntent可以看作是对Intent的包装。
		 * PendingIntent主要持有的信息是它所包装的Intent和当前Application的Context
		 * 。正由于PendingIntent中保存有当前Application的Context
		 * ，使它赋予带他程序一种执行的Intent的能力，就算在执行时当前Application已经不存在了
		 * ，也能通过存在PendingIntent里的Context照样执行Intent。
		 * 
		 * PendingIntent就是一个可以在满足一定条件下执行的Intent，它相比于Intent的优势在于自己携带有Context对象，
		 * 这样他就不必依赖于某个activity才可以存在
		 */
		notification.setLatestEventInfo(context, contentTitle, contentText,
				pendingIntent);


	}

	// 震动提醒
	private void phone_vibrator() {
		// 创建Vibrator对象
		mVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
		/**
		 * 根据指定的模式进行震动 第一个参数:该数组中第一个元素是等待多长的时间才会开启震动 之后将会是开启和关闭震动的持续时间，单位都是毫秒
		 * 第二个参数：重复震动的pattern中的索引，如果设置为-1则表示不重复震动
		 */
		mVibrator.vibrate(300);
		// mVibrator.vibrate(new long[]{0,}, -1);

	}
}
