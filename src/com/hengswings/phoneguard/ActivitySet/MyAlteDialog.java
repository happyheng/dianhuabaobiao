package com.hengswings.phoneguard.ActivitySet;

import com.hengswings.phoneguard.R;

import android.R.integer;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MyAlteDialog  {

	/* 传过来的参数包括上下文对象，title，是否为必须为num，num的限定位数，写入文档的键 */
	public MyAlteDialog(final Context context, String title, boolean is_num,
			int num_weishu, final String key) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		// 通过LayoutInflater来加载一个xml的布局文件作为弹出框的View对象
		View view = LayoutInflater.from(context).inflate(
				R.layout.my_dialog_set, null);
		builder.setView(view);
		final EditText editText1 = (EditText) view.findViewById(R.id.editText1);
		if (is_num) {
			// 设置只能为数字
			editText1.setInputType(InputType.TYPE_CLASS_NUMBER);
			// 设置输入的最大长度
			editText1
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							num_weishu) });
			// 获取先前的内容
			SharedPreferences sharedPreferences2 = context
					.getSharedPreferences(SetActivity.main, Context.MODE_PRIVATE);
			int content2;
			if (key.equals(SetActivity.day_back)) {
				/*如果为每日返回的短信次数的话，，默认为20*/
				content2 = sharedPreferences2.getInt(key, 20);
			} else {
				/*如果为每日同一手机返回的短信次数的话，，默认为3*/
				content2 = sharedPreferences2.getInt(key, 3);
			}
			// 将先前的内容写入
			editText1.setText(String.valueOf(content2));

		}

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String contextString = editText1.getText().toString().trim();
				SharedPreferences sharedPreferences = context
						.getSharedPreferences(SetActivity.main,
								Context.MODE_PRIVATE);
				Editor editor = sharedPreferences.edit();
				/* 设置回发短信内容的键 */
				if (key.equals(SetActivity.return_context)) {
					editor.putString(key, contextString);
					editor.commit();
					Toast.makeText(context, "设置成功", 0).show();
					
				}
				/* 设置每日回发短信次数的键或设置每日同一手机号回发短信的键 */
				else if (key.equals(SetActivity.day_back)
						|| key.equals(SetActivity.oneday_phone_back)) {

					int num = Integer.parseInt(contextString);
					if (num < 0) {
						Toast.makeText(context, "每日回发短信次数必须为大于等于0", 0).show();
						return;
					}
					editor.putInt(key, num);
					editor.commit();

				}

			}
		});
		// 设置取消键
		builder.setNegativeButton("取消", null);
		builder.show();

	}

}
