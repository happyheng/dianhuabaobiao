package com.hengswings.phoneguard.ActivitySet;

import com.hengswings.phoneguard.R;
import com.hengswings.phoneguard.ActivityMain.FirstActivity;
import com.hengswings.phoneguard.ServiceAndBroadCast.PhoneService;
import com.hengswings.phoneguard.myUtils.MySharedPreferences;
import com.umeng.update.UmengUpdateAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SetActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener {
	/* 通话设置中的RelativeLayout */
	RelativeLayout re_phone_set0, re_phone_set1, re_phone_set2, re_phone_set3;
	/* 白名单中的RelativeLayout */
	RelativeLayout re_pass_set1, re_pass_set2;
	/* 功能中的RelativeLayout */
	RelativeLayout re_set_roll, set_short_cut;
	CheckBox refuse_check, roll_check;
	/* 其他中的RelativeLayout */
	RelativeLayout re_other_set;

	/* 设置中的XML文件名 */
	public static final String main = "main";

	/* 设置回发短信内容的键 */
	public static final String return_context = "return_context";

	/* 设置每日回发短信次数的键 */
	public static final String day_back = "day_back";

	/* 设置每日同一手机号回发短信的键 */
	public static final String oneday_phone_back = "oneday_phone_back";

	/* 设置是否回发短信的键 */
	public static final String is_return__message = "is_return_message";

	/* 设置是否翻转的键 */
	public static final String roll = "is_roll";

	/* 设置是否为添加白名单的键 */
	public static final String is_add_first = "is_add_first";

	// SharedPreferences
	MySharedPreferences mySharedPreferences = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.set);

		// 第一个CheckBox
		refuse_check = (CheckBox) this.findViewById(R.id.refuse_check);
		mySharedPreferences = new MySharedPreferences(this);
		Boolean is_return = mySharedPreferences.getBoolean(is_return__message,
				true);
		refuse_check.setChecked(is_return);
		// 给CheckBox加监听事件
		refuse_check.setOnCheckedChangeListener(this);

		// 第二个CheckBox
		roll_check = (CheckBox) this.findViewById(R.id.roll_check);
		// 设置CheckBox的设置
		SharedPreferences sharedPreferences = getSharedPreferences(main,
				Context.MODE_PRIVATE);
		boolean is_roll = sharedPreferences.getBoolean(roll, true);
		roll_check.setChecked(is_roll);
		// 给CheckBox加监听事件
		roll_check.setOnCheckedChangeListener(this);

		re_phone_set0 = (RelativeLayout) this.findViewById(R.id.phone_set0);
		re_phone_set0.setOnClickListener(this);

		re_phone_set1 = (RelativeLayout) this.findViewById(R.id.phone_set1);
		re_phone_set1.setOnClickListener(this);

		re_phone_set2 = (RelativeLayout) this.findViewById(R.id.phone_set2);
		re_phone_set2.setOnClickListener(this);

		re_phone_set3 = (RelativeLayout) this.findViewById(R.id.phone_set3);
		re_phone_set3.setOnClickListener(this);

		re_pass_set1 = (RelativeLayout) this.findViewById(R.id.pass_set1);
		re_pass_set1.setOnClickListener(this);

		re_pass_set2 = (RelativeLayout) this.findViewById(R.id.pass_set2);
		re_pass_set2.setOnClickListener(this);

		set_short_cut = (RelativeLayout) this.findViewById(R.id.set_short_cut);
		set_short_cut.setOnClickListener(this);

		re_other_set = (RelativeLayout) this.findViewById(R.id.other_set);
		re_other_set.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.phone_set1) {

			boolean result = mySharedPreferences.getBoolean(is_return__message,
					true);
			if (!result) {
				Toast.makeText(this, "请先开启回发短信功能", 0).show();
				return;
			}
			/* 设置拦截回发短信内容 */
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
					.setTitle("设置拦截回发短信内容").setItems(
							new String[] { "常用回发短信", "自定义" },
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case 0:
										/* 设置常用回发短信 */
										set_usual_contract();

										break;
									case 1:
										/* 设置自定义回发短信 */
										set_custom_dialog();
										break;

									default:
										break;
									}

								}

								public void set_usual_contract() {
									final String usual[] = new String[] {
											"您好，我现在不方便接听电话，稍后我会联系您",
											"您好，我现在正在开会，暂时不方便接听电话，稍后我会联系您",
											"您好，我现在正在上课，暂时不方便接听电话，稍后我会联系您",
											"本人已死，小事烧香，大事挖坟" };
									// 设置常用的回发短信
									AlertDialog.Builder builder1 = new AlertDialog.Builder(
											SetActivity.this)
											.setTitle("常用回发短信内容")
											.setItems(
													usual,
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int which) {
															// 设置CheckBox的设置
															SharedPreferences sharedPreferences = getSharedPreferences(
																	SetActivity.main,
																	Context.MODE_PRIVATE);
															Editor editor = sharedPreferences
																	.edit();
															switch (which) {
															case 0:
																editor.putString(
																		return_context,
																		usual[0]);
																editor.commit();

																

																break;
															case 1:
																editor.putString(
																		return_context,
																		usual[1]);
																editor.commit();
																

																break;
															case 2:
																editor.putString(
																		return_context,
																		usual[2]);
																editor.commit();
																

																break;
															case 3:
																editor.putString(
																		return_context,
																		usual[3]);
																editor.commit();

																break;

															default:
																break;
															}
															
															Toast.makeText(SetActivity.this, "设置成功", 0).show();

														}

														
													});

									builder1.show();

								}

								private void set_custom_dialog() {
									MyAlteDialog alter_dialog = new MyAlteDialog(
											SetActivity.this, "设置自定义回发短信",
											false, 3,
											SetActivity.return_context);
								}

							});
			builder.show();

		} else if (v.getId() == R.id.phone_set2) {

			/* 设置每日回发短信次数 */
			boolean result2 = mySharedPreferences.getBoolean(
					is_return__message, true);
			if (!result2) {
				Toast.makeText(this, "请先开启回发短信功能", 0).show();
				return;
			}
			new MyAlteDialog(this, "设置每日回发短信次数", true, 3, day_back);

		} else if (v.getId() == R.id.phone_set3) {
			/* 设置每日同一手机号回发短信次数 */
			boolean result3 = mySharedPreferences.getBoolean(
					is_return__message, true);
			if (!result3) {
				Toast.makeText(this, "请先开启回发短信功能", 0).show();
				return;
			}
			new MyAlteDialog(this, "设置每日同一手机号回发短信次数", true, 2,
					oneday_phone_back);
		} else if (v.getId() == R.id.pass_set1) {

			/* 添加拦截白名单 */
			// 判断服务是不是正在运行
			if (PhoneService.sIsServiceRun) {
				Toast.makeText(this, "拦截服务正在运行，请先停止服务在设置", 0).show();
				return;
			}
			Intent intent = new Intent(SetActivity.this,
					SetPassContractActivity.class);
			// 说明是添加拦截白名单的
			intent.putExtra(is_add_first, true);
			startActivity(intent);

		} else if (v.getId() == R.id.pass_set2) {

			/* 查看拦截白名单 */
			// 判断服务是不是正在运行
			if (PhoneService.sIsServiceRun) {
				Toast.makeText(this, "拦截服务正在运行，请先停止服务在设置", 0).show();
				return;
			}
			Intent intent2 = new Intent(SetActivity.this,
					SetPassContractActivity.class);
			// 说明是查看拦截白名单的
			intent2.putExtra(is_add_first, false);
			startActivity(intent2);

		} else if (v.getId() == R.id.set_short_cut) {

			/* 设置添加"一键免打扰"快捷方式功能 */
			/**
			 * 注意创建快捷方式是需要权限的！！！
			 */
			create_shortCut();

		} else if (v.getId() == R.id.other_set) {
			/* 检查更新 */
			UmengUpdateAgent.setUpdateOnlyWifi(false);
			UmengUpdateAgent.update(this);

		}

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// 如果是第一个是否回发短信的
		if (buttonView.getId() == R.id.refuse_check) {
			mySharedPreferences.putBoolean(is_return__message, isChecked);
			if (isChecked) {
				Toast.makeText(this, "开启回发短信功能", 0).show();
			} else {
				Toast.makeText(this, "关闭回发短信功能", 0).show();
			}

		} else if (buttonView.getId() == R.id.roll_check) {

			SharedPreferences sharedPreferences = getSharedPreferences("main",
					Context.MODE_PRIVATE);
			Editor editor = sharedPreferences.edit();
			if (isChecked) {
				editor.putBoolean(roll, true);
				editor.commit();
				Toast.makeText(this, "可以翻转静音", 0).show();

			} else {
				editor.putBoolean(roll, false);
				editor.commit();
				Toast.makeText(this, "不可以翻转静音", 0).show();
			}

		}
	}

	/**
	 * 创建快捷方式的方法s
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
