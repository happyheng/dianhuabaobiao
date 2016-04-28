package com.hengswings.phoneguard.ActivitySet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hengswings.phoneguard.R;
import com.hengswings.phoneguard.ActivitySet.DemoAdapter.ViewHolder;
import com.hengswings.phoneguard.database.MyDBController;
import com.hengswings.phoneguard.database.DBType;
import com.hengswings.phoneguard.database.DBHelper;
import com.hengswings.phoneguard.wight.ClearEditText;

import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

/**
 * 是设置白名单的Activity
 * 
 * @author liuheng
 * 
 */
public class SetPassContractActivity extends Activity implements
		OnClickListener, OnItemClickListener {

	/**
	 * 返回按钮
	 */
	private ViewGroup btnCancle = null;

	/**
	 * 选择所有
	 */
	private Button btnSelectAll = null;

	/**
	 * 添加进白名单
	 */
	private Button btnAdd = null;

	/**
	 * ListView列表
	 */
	private ListView lvListView = null;

	/**
	 * 适配对象
	 */
	private DemoAdapter adpAdapter = null;
	/**
	 * 可清除的EditText
	 */
	private ClearEditText mClearEditText;

	/**
	 * 进度条
	 */
	private ProgressDialog progressDialog;
	/**
	 * 文本标题
	 */
	private TextView textView;

	/** 获取库Phon表字段 **/
	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER };

	/** 联系人显示名称 **/
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;

	/** 电话号码 **/
	private static final int PHONES_NUMBER_INDEX = 1;

	/** Handler **/
	private Handler handler;

	public MyDBController dbhelper;

	/** 白名单 **/
	public List<String> pass;

	/** 是否为添加白名单 **/
	private boolean is_add_frist;

	// public static String NAME = "name";
	// public static String PHONE = "phone";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.set_pass);
		Intent intent = getIntent();
		is_add_frist = intent.getBooleanExtra(SetActivity.is_add_first, true);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (progressDialog != null) {
					// 关闭ProgressDialog
					progressDialog.dismiss();
				}
			};
		};
		/** 说明是添加白名单 **/
		if (is_add_frist) {
			// 显示ProgressDialog
			progressDialog = ProgressDialog.show(this, "Loading...", "正在导入通讯录");
			progressDialog.show();

			// 初始化视图
			initView();

			// 初始化控件,不筛选任何东西
			initData(null);
		} else {
			/** 说明是查看白名单 **/
			// 初始化视图
			initView2();

			// 填充控件
			initData2();

		}

	}

	/**
	 * 初始化控件
	 */
	private void initView() {

		btnCancle = (ViewGroup) findViewById(R.id.btnCancle);
		btnCancle.setOnClickListener(this);

		btnAdd = (Button) findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(this);

		btnSelectAll = (Button) findViewById(R.id.btnSelectAll);
		btnSelectAll.setOnClickListener(this);

		lvListView = (ListView) findViewById(R.id.lvListView);
		lvListView.setOnItemClickListener(this);

		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				initData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO 自动生成的方法存根

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO 自动生成的方法存根

			}
		});

	}

	/**
	 * 初始化视图,并写入数据
	 * 
	 * @category filter_String是写入的要筛选的数据
	 */
	private void initData(String filter_String) {
		/**
		 * 写入的数据
		 */
		List<DemoBean> demoDatas = new ArrayList<DemoBean>();

		/**
		 * 导入白名单，用于筛选
		 */
		if (dbhelper == null) {
			dbhelper = new MyDBController(this);
		}
		pass = dbhelper.return_pass_num();

		/**
		 * 获得数据库中的数据
		 */
		List<HashMap<String, String>> list = dbhelper.read_name_phone();
		for (HashMap<String, String> hashMap : list) {
			// 得到手机号
			String phoneNumber = hashMap.get(DBType.PHONE_NUMBER);
			// 当手机号码为空的或者为空字段或为白名单中的 跳过当前循环
			if (TextUtils.isEmpty(phoneNumber) || filter(phoneNumber)) {
				continue;
			}
			// 得到姓名
			String name = hashMap.get(DBType.NAME);
			demoDatas.add(new DemoBean(name, phoneNumber));

		}

		// 获取手机联系人
		ContentResolver resolver = this.getContentResolver();
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);

				// 当手机号码为空的或者为空字段或为白名单中的 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber) || filter(phoneNumber))
					continue;

				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);
				demoDatas.add(new DemoBean(contactName, phoneNumber));

			}
		}
		phoneCursor.close();

		// 刷选数据的通用方法
		demoDatas = filter_demo_th_str(demoDatas, filter_String);

		adpAdapter = new DemoAdapter(this, demoDatas);

		lvListView.setAdapter(adpAdapter);

		/**
		 * 解除progressDialog
		 */
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
			}
		}).start();
		// 关闭对数据库的操作
		dbhelper.Close();
		// 关闭dbhelper
		dbhelper = null;

	}

	/**
	 * 是否存在于白名单中
	 * 
	 * @param num
	 * @return
	 */
	private boolean filter(String num) {
		boolean bool = false;
		if (pass != null) {
			return pass.contains(num);
		}
		return bool;
	}

	/**
	 * 初始化控件2
	 */
	private void initView2() {
		// TODO 自动生成的方法存根
		btnCancle = (ViewGroup) findViewById(R.id.btnCancle);
		btnCancle.setOnClickListener(this);

		btnAdd = (Button) findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(this);
		btnAdd.setText("删除");

		btnSelectAll = (Button) findViewById(R.id.btnSelectAll);
		btnSelectAll.setOnClickListener(this);

		lvListView = (ListView) findViewById(R.id.lvListView);
		lvListView.setOnItemClickListener(this);

		textView = (TextView) findViewById(R.id.set_textView);
		textView.setText("查看白名单");

		// 如果是删除白名单的话，这个基本上是用不到的,所以隐藏它
		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
		/**
		 * 通常控件的可见与不可见分为三种情况。
		 * 
		 * 第一种 gone 表示不可见并且不占用空间
		 * 
		 * 第二种 visible 表示可见
		 * 
		 * 第三种 invisible 表示不可见但是占用空间
		 */
		mClearEditText.setVisibility(View.GONE);
	}

	/** 填充控件 **/
	private void initData2() {
		/**
		 * 写入的数据
		 */
		List<DemoBean> ListdemoDatas = new ArrayList<DemoBean>();
		/**
		 * 导入白名单，用于筛选
		 */
		if (dbhelper == null) {
			dbhelper = new MyDBController(this);

		}
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		list = dbhelper.return_pass_all();
		for (HashMap<String, String> map : list) {
			DemoBean demoBean = null;
			demoBean = new DemoBean();
			// 得到姓名
			String name = map.get(DBHelper.db2_people_name);
			demoBean.setTitle(name);
			// 得到手机号码
			String num = map.get(DBHelper.db2_num);

			demoBean.setphone_num(num);
			// 加入至List集合中
			ListdemoDatas.add(demoBean);
		}
		// 设置适配器
		adpAdapter = new DemoAdapter(this, ListdemoDatas);
		lvListView.setAdapter(adpAdapter);

		// 关闭对数据库的操作
		dbhelper.Close();
		// 关闭dbhelper
		dbhelper = null;

	}

	private ContextWrapper getContext() {
		// TODO 自动生成的方法存根
		return null;
	}

	/**
	 * 按钮点击事件
	 */
	@Override
	public void onClick(View v) {

		/*
		 * 当点击返回的时候
		 */
		if (v == btnCancle) {
			finish();

		}

		/*
		 * 当点击添加的时候，把得到的数据添加进白名单中
		 */
		if (v == btnAdd) {
			/**
			 * 如果是添加白名单的话
			 */
			if (is_add_frist) {
				Map<Integer, Boolean> map = adpAdapter.getCheckMap();

				// 获取当前的数据数量
				int count = adpAdapter.getCount();

				/** 此为得到的白名单 */
				List<HashMap<String, String>> get_pass = new ArrayList<HashMap<String, String>>();
				// 进行遍历
				for (int i = 0; i < count; i++) {
					// 因为List的特性,删除了2个item,则3变成2,所以这里要进行这样的换算,才能拿到删除后真正的position
					int position = i - (count - adpAdapter.getCount());

					if (map.get(i) != null && map.get(i)) {
						DemoBean bean = (DemoBean) adpAdapter.getItem(position);
						// 得到姓名
						String name = bean.getTitle().trim();
						// 得到手机号
						String unm = bean.getphone_num().trim();
						HashMap<String, String> hashMap = null;
						hashMap = new HashMap<String, String>();
						hashMap.put(DBHelper.db2_people_name, name);
						hashMap.put(DBHelper.db2_num, unm);
						// 加进去
						get_pass.add(hashMap);

						adpAdapter.getCheckMap().remove(i);
						adpAdapter.remove(position);

					}
				}
				if (!(get_pass.isEmpty())) {
					if (dbhelper == null) {
						dbhelper = new MyDBController(this);

					}
					// try {
					long my_num = dbhelper.insert_to_pass(get_pass);
					// } catch (Exception e) {
					// // TODO: handle exception
					// Toast.makeText(this, "存储失败", Toast.LENGTH_SHORT).show();
					// }
					Toast.makeText(this, "已将手机号填入白名单中", Toast.LENGTH_SHORT)
							.show();

					// 关闭操作，但没有为空
					dbhelper.Close();
					// 关闭dbhelper
					dbhelper = null;
				}

				// 看出来了，这个方法只会更新listView的getView方法
				adpAdapter.notifyDataSetChanged();

			}
			/**
			 * 说明是删除白名单，此功能即使实现从白名单中将选中的去除掉
			 */
			else {
				/*
				 * 删除算法最复杂,拿到checkBox选择寄存map
				 */
				Map<Integer, Boolean> map = adpAdapter.getCheckMap();

				// 获取当前的数据数量
				int count = adpAdapter.getCount();

				// 此为所需删除的list<String>集合
				List<String> list = new ArrayList<String>();
				// 进行遍历
				for (int i = 0; i < count; i++) {
					// 因为List的特性,删除了2个item,则3变成2,所以这里要进行这样的换算,才能拿到删除后真正的position
					int position = i - (count - adpAdapter.getCount());

					if (map.get(i) != null && map.get(i)) {
						DemoBean bean = (DemoBean) adpAdapter.getItem(position);
						// 得到手机号
						String unm = bean.getphone_num().trim();
						// 将将要移除的手机号加进去
						list.add(unm);

						adpAdapter.getCheckMap().remove(i);
						adpAdapter.remove(position);
					}
				}
				if (dbhelper == null) {
					dbhelper = new MyDBController(this);
				}
				int result = dbhelper.delete_pass_fromphone_num(list);
				// 如果大于0，说明删除成功
				if (result > 0) {
					Toast.makeText(this, "从白名单中删除成功", 0).show();
				} else {
					Toast.makeText(this, "从白名单中删除失败", 0).show();
				}
				// 更新listView
				// 看出来了，这个方法只会更新listView的getView方法
				adpAdapter.notifyDataSetChanged();
			}
		}

		/*
		 * 当点击全选的时候
		 */
		if (v == btnSelectAll) {

			if (btnSelectAll.getText().toString().trim().equals("全选")) {

				// 所有项目全部选中
				adpAdapter.configCheckMap(true);

				adpAdapter.notifyDataSetChanged();

				btnSelectAll.setText("全不选");
			} else {

				// 所有项目全部不选中
				adpAdapter.configCheckMap(false);

				adpAdapter.notifyDataSetChanged();

				btnSelectAll.setText("全选");
			}

		}
	}

	/**
	 * 当ListView 子项点击的时候
	 */
	@Override
	public void onItemClick(AdapterView<?> listView, View View, int position,
			long id) {

		if (View.getTag() instanceof ViewHolder) {

			ViewHolder holder = (ViewHolder) View.getTag();

			// 会自动出发CheckBox的checked事件
			holder.cbCheck.toggle();

		}

	}

	/**
	 * 传入一个List<DemoBean>和需要筛选的String，返回一个筛选了String的List<DemoBean>
	 */
	private List<DemoBean> filter_demo_th_str(List<DemoBean> demo,
			String filter_String) {
		List<DemoBean> filter_return = null;
		if (demo == null) {
			return null;
		}
		if (filter_String == null) {
			return demo;
		}
		filter_return = new ArrayList<DemoBean>();
		for (DemoBean demoBean : demo) {
			if (demoBean.getTitle().startsWith(filter_String)) {
				filter_return.add(demoBean);
			}
		}
		return filter_return;
	}
}
