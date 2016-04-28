package com.hengswings.phoneguard.ActivityMain;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.hengswings.phoneguard.R;
import com.hengswings.phoneguard.ActivityClock.AddClockActivity;
import com.hengswings.phoneguard.ActivityContact.ContractActivity;
import com.hengswings.phoneguard.ActivitySet.SetActivity;
import com.hengswings.phoneguard.ServiceAndBroadCast.PhoneService;
import com.hengswings.phoneguard.myUtils.MySharedPreferences;
import com.umeng.update.UmengUpdateAgent;

/**
 * 电话挂断源码详情见http://blog.csdn.net/yudajun/article/details/8151709
 * 
 * @author Administrator
 * 
 */
public class MainActivity extends Activity implements OnClickListener {

	private Button button1, button2, button3, button4, button5;

	// 传感器管理器
	private SensorManager sensorManager;
	// 姿态监听事件
	private SensorEventListener mySensorEventListener;
	// 判断手机现在正在下方的键
	public static String is_first = "is_first_log_in";

	/**
	 * DevicePolicyManager 顾名思义，这个类的作用是管理设备。通过这个类，我们可以实现屏幕锁定、亮度调节 甚至是恢复出厂设置等功能。
	 * 
	 * 这是锁屏的类
	 * 
	 */
	private DevicePolicyManager policyManager;
	private ComponentName componentName;
	private static final int MY_REQUEST_CODE = 9999;

	// 判断用户是否看过免责声明的键
	public static String looked_treaty = "looked_treaty";
	// 判断手机现在正在下方的标志位
	public static boolean is_down = false;

	// 第一个Dialog要显示的内容
	private String first_String = "回发短信功能是当拦截服务拦截到电话后，将自动回复自定义短信，提示对方自己在忙\n此功能默认为一天最多回发20条短信，同一手机号最多回发3条短信";

	// 选择开启之后要显示的内容
	public static String two_Positive_String = "您选择了开启，在设置界面中可以关闭此功能";

	// 选择取消之后要显示的内容
	public static String two_Negative_String = "您选择了取消，在设置界面中可以开启此功能";

	// 第三个Dialog要显示的内容
	public static String three_String = "本软件是一款免费软件，此软件的制作已采取最大之谨慎。制作者对于可能的故障声明及其后果不承担任何法律义务或其它性质的义务。\n点击我同意表示您同意此条款";

	// 定义门把手
	public static Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏,这个必须在setContentView之前
		setContentView(R.layout.phone_main);
		
		
		UmengUpdateAgent.setUpdateOnlyWifi(true);
		UmengUpdateAgent.update(this);

		button1 = (Button) this.findViewById(R.id.button1);
		button2 = (Button) this.findViewById(R.id.button2);
		button3 = (Button) this.findViewById(R.id.button33);
		button4 = (Button) this.findViewById(R.id.button44);
		button5 = (Button) this.findViewById(R.id.button55);

		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);
		button4.setOnClickListener(this);
		button5.setOnClickListener(this);

		// 获取设备管理服务
		policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		// ComponentName这个我们在用intent跳转的时候用到过。
		// 自己的AdminReceiver 继承自 DeviceAdminReceiver
		componentName = new ComponentName(this, AdminReceiver.class);

		mySensorEventListener = new SensorEventListener() {

			public void onSensorChanged(SensorEvent event) {
				if (Sensor.TYPE_ACCELEROMETER != event.sensor.getType()) {
					return;
				}
				float[] values = event.values;
				float ax = values[0];
				float ay = values[1];
				float az = values[2];
				// 说面屏幕一定是向下或者向上平放着的
				if (ax < 3 && ax > -3 && ay < 3 && ay > -3) {

					// 屏幕朝着下面
					if (az < 0) {

						// 判断是否有锁屏权限，若有则立即锁屏并结束自己，若没有则获取权限
						if (policyManager.isAdminActive(componentName)) {
							policyManager.lockNow();// 锁屏

							// 先判断有无service在运行,如果没有运行的话
							if (!(PhoneService.sIsServiceRun)) {
								// 在这里开启service服务
								Intent intent = new Intent(
										"com.example.teacherassistant.clock_service");
								startService(intent);
							}

						}

						// is_down = true;
					}
					// if (az < 0 && is_down) {
					// is_down = false;
					// }

				}

			}

			public void onAccuracyChanged(Sensor sensor, int accuracy) {

			}
		};
		handler = new Handler() {
			public void handleMessage(Message message) {
				if (message.what == 1) {
					Builder alertDialog = new AlertDialog.Builder(
							MainActivity.this).setTitle("免责声明");
					alertDialog.setMessage(MainActivity.three_String);
					alertDialog.setPositiveButton("我同意",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									SharedPreferences sharedPreferences = getSharedPreferences(
											SetActivity.main,
											Context.MODE_PRIVATE);
									Editor editor = sharedPreferences.edit();
									editor.putBoolean(
											MainActivity.looked_treaty, true);
									editor.commit();
								}
							});
					alertDialog.show();
				}
				super.handleMessage(message);
			}

		};
		// 看看是否为第一次登陆
		Intent intent = getIntent();
		boolean is_first_bool = intent.getBooleanExtra(is_first, false);
		// 是第一次登陆
		if (is_first_bool) {

			first_dialog();

		}
		// 不是第一次登陆
		else {
			// 看用户是否看过免责声明
			SharedPreferences sharedPreferences = getSharedPreferences(
					SetActivity.main, Context.MODE_PRIVATE);
			boolean is_first = sharedPreferences.getBoolean(looked_treaty,
					false);

			// 如果没有看过
			if (!is_first) {
				// 那就让他看看
				new Thread(new Runnable() {
					public void run() {
						Message message1 = new Message();
						message1.what = 1;
						MainActivity.handler.sendMessage(message1);
					}
				}).start();
			}

			// 判断是否支持一键旋转的键
			boolean is_roll = sharedPreferences.getBoolean(SetActivity.roll,
					true);

			// 如果支持一键旋转且没有许可,就得到许可
			if (is_roll && !(policyManager.isAdminActive(componentName))) {

				// 获取许可的方法
				activeManage();

			}

		}

	}

	/**
	 * 创建第一个dialog
	 */
	private void first_dialog() {
		Builder alertDialog = new AlertDialog.Builder(this)
				.setTitle("是否开启回发短信功能？");
		// 设置内容
		alertDialog.setMessage(first_String);
		// 设置开启后的功能
		alertDialog.setPositiveButton("开启",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						// 如果选择开启，则写入开启true
						MySharedPreferences mySharedPreferences = new MySharedPreferences(
								MainActivity.this);
						mySharedPreferences.putBoolean(
								SetActivity.is_return__message, true);

						Builder alertDialog = new AlertDialog.Builder(
								MainActivity.this).setTitle("提示");
						alertDialog
								.setMessage(MainActivity.two_Positive_String);
						alertDialog.setPositiveButton("确认",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										new Thread(new Runnable() {
											public void run() {
												Message message1 = new Message();
												message1.what = 1;
												MainActivity.handler
														.sendMessage(message1);
											}
										}).start();
									}
								});
						alertDialog.show();

					}

				});
		alertDialog.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						// 如果选择取消，则写入开启false
						MySharedPreferences mySharedPreferences = new MySharedPreferences(
								MainActivity.this);
						mySharedPreferences.putBoolean(
								SetActivity.is_return__message, false);

						Builder alertDialog = new AlertDialog.Builder(
								MainActivity.this).setTitle("提示");
						alertDialog
								.setMessage(MainActivity.two_Negative_String);
						alertDialog.setPositiveButton("确认",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										new Thread(new Runnable() {
											public void run() {
												Message message1 = new Message();
												message1.what = 1;
												MainActivity.handler
														.sendMessage(message1);
											}
										}).start();
									}
								});
						alertDialog.show();

					}
				});
		alertDialog.show();
	}

	@Override
	public void onResume() {
		super.onResume();
		// 在当前的activity中注册自毁广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(AppExitDialogActivity.exit_Action);
		this.registerReceiver(this.broadcastReceiver, filter);
		// 判断是否要开启翻转拦截功能
		SharedPreferences sharedPreferences = getSharedPreferences("main",
				Context.MODE_PRIVATE);
		boolean is_roll = sharedPreferences.getBoolean(SetActivity.roll, true);
		// 支持旋屏功能且有许可的情况下才开启
		if (is_roll && policyManager.isAdminActive(componentName)) {
			sensorManager = (SensorManager) this
					.getSystemService(Context.SENSOR_SERVICE);
			sensorManager.registerListener(mySensorEventListener,
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	protected void onStop() {
		super.onStop();
		if (sensorManager != null) {
			// 取消监听
			sensorManager.unregisterListener(mySensorEventListener,
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
		}

	};

	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(this.broadcastReceiver);
	};

	// 广播的内部类，当收到关闭事件时，调用finish方法结束activity
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}

	};

	/**
	 * 跳转到获取权限
	 */
	private void activeManage() {

		// 启动设备管理(隐式Intent) - 在AndroidManifest.xml中设定相应过滤器
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

		// 权限列表
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);

		// 描述(additional explanation) 在申请权限时出现的提示语句
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "激活后就能旋转锁屏了");

		startActivityForResult(intent, MY_REQUEST_CODE);
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.button1) {
			/* 开启闹钟 */
			// 如果是拦截服务没有在运行，那么开始免打扰状态，并且开始静音
			if (!(PhoneService.sIsServiceRun)) {
				// 开启service服务
				Intent intent = new Intent(
						"com.example.teacherassistant.clock_service");
				startService(intent);
				Toast.makeText(this, "拦截服务已经启动，可以切走去干些别的了( ^_^ )", 1).show();
			}
			// 如果是免打扰状态，那么结束免打扰状态，然后恢复正常铃声
			else {
				Toast.makeText(this, "拦截服务正在运行，请点击通知栏结束服务", 0).show();
			}
		} else if (v.getId() == R.id.button2) {
			/* 私人通讯录 */
			Intent intent = new Intent(MainActivity.this,
					ContractActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.button33) {
			/* 设置 */
			Intent intent2 = new Intent(MainActivity.this, SetActivity.class);
			startActivity(intent2);

		} else if (v.getId() == R.id.button44) {
			/* 定时拦截闹钟 */
			Intent intent1 = new Intent(MainActivity.this,
					AddClockActivity.class);
			startActivity(intent1);
		} else if (v.getId() == R.id.button55) {
			/* 退出程序 */
			finish();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// 获取back键,getRepeatCount方法的意思是应该是没有重复
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent(MainActivity.this,
					AppExitDialogActivity.class);
			startActivity(intent);
		}

		return true;
	}
}
