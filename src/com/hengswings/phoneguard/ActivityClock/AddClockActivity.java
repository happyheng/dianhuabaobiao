package com.hengswings.phoneguard.ActivityClock;



import com.hengswings.phoneguard.R;
import com.hengswings.phoneguard.ServiceAndBroadCast.PhoneService;
import com.hengswings.phoneguard.myUtils.MySharedPreferences;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;



public class AddClockActivity extends Activity {
	public Button button;
	public TimePicker timePicker;
	// 判断用户是否为第一次使用闹钟的键
	public static String is_first = "is_first_clock";
	// 点击按钮之后要显示的内容
	public static String note = "由于此软件是应用软件，闹钟服务少数情况下可能会被系统杀死。"
			+ "为避免出现这种情况，您最好在退出时不要将软件清除出内存，否则会出现错误";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.my_clock);
		button = (Button) findViewById(R.id.button3);
		timePicker = (TimePicker) this.findViewById(R.id.timePicker1);
		// 设置为24小时制
		timePicker.setIs24HourView(true);

		Time t = new Time(); // 获取系统中的Time
		// 获取现在的时间
		t.setToNow();
		int hour = t.hour; // 0-23
		// 设置为现在的小时数
		timePicker.setCurrentHour(hour);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!(PhoneService.sIsServiceRun)) {
					// 得到timePicker的小时数
					int h_service = timePicker.getCurrentHour();
					// 得到timePicker的分钟数
					int m_service = timePicker.getCurrentMinute();

					Time t = new Time(); // 获取系统中的Time
					t.setToNow(); // 取得系统时间。
					int hour = t.hour; // 0-23
					int minute = t.minute;
					if (h_service == hour && m_service == minute) {
						Toast.makeText(AddClockActivity.this,
								"不能以现在的时间设定为闹钟的时间", 0).show();
						return;
					}
					/**
					 * 这些应该是24小时制的
					 */
					// 在这里开启service服务
					Intent intent = new Intent(
							"com.example.teacherassistant.clock_service");
					Bundle bundle = new Bundle();
					// 注意传过去的是一个int的数值对象
					bundle.putInt(PhoneService.sGethour, h_service);
					bundle.putInt(PhoneService.sGetMunite, m_service);
					intent.putExtras(bundle);
					startService(intent);
					Toast.makeText(AddClockActivity.this,
							"已启动拦截电话服务，点击通知栏消息停用", 1).show();

					// 判断是否为第一次使用闹钟
					SharedPreferences sharedPreferences = getSharedPreferences(
							"main", Context.MODE_PRIVATE);
					boolean is_first_clock = sharedPreferences.getBoolean(
							is_first, true);
					// 如果是第一次的话
					if (is_first_clock) {
						Builder alertDialog = new AlertDialog.Builder(
								AddClockActivity.this).setTitle("提示");
						alertDialog.setMessage(AddClockActivity.note);
						alertDialog.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// 自己写的工具类就是好用
										MySharedPreferences mySharedPreferences = new MySharedPreferences(
												AddClockActivity.this);
										mySharedPreferences.putBoolean(
												AddClockActivity.is_first,
												false);
									}
								});
						alertDialog.show();
					}

				} else {
					Toast.makeText(AddClockActivity.this,
							"拦截电话服务正在运行中，点击通知栏消息停用", 1).show();
				}
			}
		});

	}

	@Override
	protected void onPause() {
		// TODO 自动生成的方法存根
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO 自动生成的方法存根
		super.onDestroy();
	}

}
