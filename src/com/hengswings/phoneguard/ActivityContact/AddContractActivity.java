package com.hengswings.phoneguard.ActivityContact;

import java.util.HashMap;

import com.hengswings.phoneguard.R;
import com.hengswings.phoneguard.database.MyDBController;
import com.hengswings.phoneguard.database.DBType;


import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.Toast;

public class AddContractActivity extends Activity {
	private ImageButton userimg;
	private EditText userphonenum;
	private EditText username;
	private EditText userhomenum;
	private EditText userhost;
	private EditText userremark;
	public MyDBController dbhelper;
	SQLiteDatabase db;
	View view;
	Button btnsave;
	Button btndiss;
	int ig = 0;
	ImageSwitcher imgsw;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.addviewx);

		userimg = (ImageButton) findViewById(R.id.userimg);

		// 得到输入姓名的EditText
		username = (EditText) findViewById(R.id.username);
		// 得到输入手机号的EditText
		userphonenum = (EditText) findViewById(R.id.phonenum);
		// 得到输入家庭电话的EditText
		userhomenum = (EditText) findViewById(R.id.homephonenum);
		// 得到单位名称的EditText
		userhost = (EditText) findViewById(R.id.posthost);
		// 得到备份的EditText
		userremark = (EditText) findViewById(R.id.remark);
		// 保存按钮
		btnsave = (Button) findViewById(R.id.saveuser);
		// 取消按钮
		btndiss = (Button) findViewById(R.id.dissmuser);

		// 初始化一个dbhelper类
		dbhelper = new MyDBController(this);

		btnsave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 这个一定要弄成姓名和电话号码都不能为空
				if (username.getText().toString().equals("")
						|| userphonenum.getText().toString().equals("")) {
					Toast.makeText(AddContractActivity.this, "姓名和手机号不能为空",
							Toast.LENGTH_SHORT).show();
				}

				else {

					HashMap<String, String> hash = new HashMap<String, String>();
					// 如果姓名和电话号码都不为空的话
					// 将数据都写入到数据库中
					// 写入姓名
					hash.put(DBType.NAME, username.getText().toString());
					// 写入电话号码
					hash.put(DBType.PHONE_NUMBER, userphonenum.getText()
							.toString());
					// 写入家庭号码
					hash.put(DBType.HOME_NUMBER, userhomenum.getText().toString());
					// 写入工作单位
					hash.put(DBType.WORK_NAME, userhost.getText().toString());
					// 写入备注
					hash.put(DBType.REMARK_STRING, userremark.getText()
							.toString());
					// 向数据库中写入
					dbhelper.write_db(hash);

					// 关闭数据库
					dbhelper.Close();

					AddContractActivity.this.finish();

				}

			}

		});

	}

	@Override
	protected void onResume() {
		// TODO 自动生成的方法存根
		super.onResume();
		if (dbhelper == null) {
			dbhelper = new MyDBController(this);
		}

	}

}
