package com.hengswings.phoneguard.ActivityContact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hengswings.phoneguard.R;
import com.hengswings.phoneguard.adapter.SortAdapter;
import com.hengswings.phoneguard.bean.SortModel;
import com.hengswings.phoneguard.database.MyDBController;
import com.hengswings.phoneguard.database.DBType;
import com.hengswings.phoneguard.utils.AsyncTaskBase;
import com.hengswings.phoneguard.utils.CharacterParser;
import com.hengswings.phoneguard.utils.PinyinComparator;
import com.hengswings.phoneguard.wight.LoadingView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 私人通讯录的界面
 * 
 * @author liuheng
 * 
 */
public class ContractActivity extends Activity {
	private Context mContext;
	private ListView sortListView;
	private TextView dialog;
	private ImageView addJiaHaoView;
	private SortAdapter adapter;
	private Map<String, String> callRecords;
	private LoadingView mLoadingView;
	private final String TAG = "hello";
	public static boolean is_create = false;

	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	public MyDBController dbhelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.contract);

		mContext = ContractActivity.this;
		// 都寻找控件
		mLoadingView = (LoadingView) findViewById(R.id.loading);
		dialog = (TextView) findViewById(R.id.dialog);

		addJiaHaoView = (ImageView) findViewById(R.id.contractJiaHao);

		addJiaHaoView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//跳转到添加通讯录界面的方法
				startToAddContractActivity();
			}
		});

		sortListView = (ListView) findViewById(R.id.country_lvcountry);

	}

	// 在这里开始重新加载数据库
	@Override
	protected void onResume() {
		super.onResume();
		if (dbhelper == null) {
			dbhelper = new MyDBController(this);
		}

		
		initData();
	}

	@Override
	protected void onPause() {
		// TODO 自动生成的方法存根
		super.onPause();

	}

	@Override
	protected void onStop() {
		// TODO 自动生成的方法存根
		super.onStop();

	}

	@Override
	protected void onDestroy() {
		// TODO 自动生成的方法存根
		super.onDestroy();
		if (dbhelper != null) {
			dbhelper.Close();
		}
		

	}

	private void initData() {

		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();

		pinyinComparator = new PinyinComparator();

		
		// 设置listView的点击事件
		// 不是滑动监听
		sortListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// 这里要利用adapter.getItem(position)来获取当前position所对应的对象
				// Toast.makeText(getApplication(),
				// ((SortModel)adapter.getItem(position)).getName(),
				// Toast.LENGTH_SHORT).show();
				/**
				 * 这时callRecords已经有值了，这时得到点击的手机号
				 */
				String number = callRecords.get(((SortModel) adapter
						.getItem(position)).getName());
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
						.setTitle("选项").setItems(
								new String[] { "添加", "删除", "拨打电话" },
								new OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										switch (which) {
										case 0:
											/**
											 * “添加”事件的监听接口
											 */
											Toast.makeText(mContext, "是第一个",
													Toast.LENGTH_SHORT).show();

											break;
										case 1:
											/**
											 * “删除”事件的监听接口
											 */
											// 得到点击对应的名字
											String name = ((SortModel) adapter
													.getItem(position))
													.getName();
											// 这时callRecords已经有值了，这时得到点击的手机号
											String number = callRecords
													.get(((SortModel) adapter
															.getItem(position))
															.getName());

											// 从数据库中删除指定的数据
											dbhelper.delete_map_fornumber(name,
													number);
											// 刷新列表
											refresh_list();

											break;
										case 2:
											// 这时callRecords已经有值了，这时得到点击的手机号
											String phone_number = callRecords
													.get(((SortModel) adapter
															.getItem(position))
															.getName());
											Intent intent = new Intent(
													"android.intent.action.CALL",
													Uri.parse("tel:"
															+ phone_number));
											startActivity(intent);

											break;

										default:
											break;
										}

									}
								});
				builder.show();

			}
		});

		new AsyncTaskConstact(mLoadingView).execute(0);

	}

	/**
	 * 向listView中写入数据的类
	 */
	private class AsyncTaskConstact extends AsyncTaskBase {

		public AsyncTaskConstact(LoadingView loadingView) {
			super(loadingView);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			int result = -1;

			fille_callRecords();

			// 设置标志位
			result = 1;
			return result;
		}

		/**
		 * 将得到的数据进行分类，整合，排序，然后显示在listView上
		 */
		@Override
		protected void onPostExecute(Integer result) {

			super.onPostExecute(result);
			if (result == 1) {
				List<String> constact = new ArrayList<String>();
				/**
				 * callRecords为手机上所有的联系人，此为得到constact上所有的联系人的名字
				 */
				for (Iterator<String> keys = callRecords.keySet().iterator(); keys
						.hasNext();) {
					String key = keys.next();
					constact.add(key);
				}
				String[] names = new String[] {};
				// 将联系人上所有的名字改为字符串数组
				names = constact.toArray(names);
				// 将name数组转换为SortModel数组
				SourceDateList = filledData(names);

				// 根据a-z进行排序源数据
				Collections.sort(SourceDateList, pinyinComparator);
				/**
				 * 给ListView设置View的adapter
				 */
				adapter = new SortAdapter(mContext, SourceDateList);
				// 给ListView设置View
				sortListView.setAdapter(adapter);
				/**
				 * 输入框中的
				 */
			}

		}

	}

	/**
	 * 
	 * 刷新列表
	 * 
	 */
	public void refresh_list() {

		fille_callRecords();

		// 将callRecords这个map上的所有键，即性命，改为字符串数组
		List<String> constact = new ArrayList<String>();
		for (Iterator<String> keys = callRecords.keySet().iterator(); keys
				.hasNext();) {
			String key = keys.next();
			constact.add(key);
		}
		String[] names = new String[] {};
		// 将联系人上所有的名字改为字符串数组
		names = constact.toArray(names);
		// 将name数组转换为SortModel数组
		List<SortModel> SourceDate = new ArrayList<SortModel>();
		SourceDate = filledData(names);
		// 根据a-z进行排序
		if (pinyinComparator == null) {
			pinyinComparator = new PinyinComparator();
		}
		Collections.sort(SourceDate, pinyinComparator);
		// 对listview进行更新操作
		adapter.updateListView(SourceDate);

	}

	public void fille_callRecords() {
		if (callRecords == null) {
			callRecords = new HashMap<String, String>();
		}
		/**
		 * callRecords现在已经有了以前的数据，必须对其进行清除！！！
		 */
		callRecords.clear();
		/**
		 * callRecords现在已经有了以前的数据，必须对其进行清除！！！
		 */
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		// 从数据库中读取出所有的数据
		list = dbhelper.read_all_information();
		// 将其转换为HashMap<String, String>()类型的
		for (int i = 0; i < list.size(); i++) {
			// 先实例化一个map
			HashMap<String, String> mymap = new HashMap<String, String>();
			mymap = list.get(i);
			// 读出名字
			String name = mymap.get(DBType.NAME);
			// 读出电话号码
			String number = mymap.get(DBType.PHONE_NUMBER);
			// 将其放在callRecords中
			callRecords.put(name, number);
		}
		/**
		 * 在这获取手机中的所有联系人
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 */
	}

	/**
	 * 这个方法传进去的是一个手机通讯录的联系人的名字 是把通讯录中所有人的姓名变为SortModel类型的方法
	 */
	private List<SortModel> filledData(String[] date) {
		List<SortModel> mSortList = new ArrayList<SortModel>();
		// 将“联系人的名字”数组改成sortModel类型
		for (int i = 0; i < date.length; i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(date[i]);
			// 将名字转换成拼音
			String pinyin = characterParser.getSelling(date[i]);
			// 得到首字母
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				// 设置其首字母
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<SortModel> filterDateList = new ArrayList<SortModel>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (SortModel sortModel : SourceDateList) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		menu.findItem(R.id.action_plus).setOnMenuItemClickListener(
				new MenuItem.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {

						// 跳到添加通讯录的界面
						startToAddContractActivity();

						return false;
					}
				});

		return true;
	}

	/**
	 * 跳转到添加通讯录界面的方法
	 */
	public void startToAddContractActivity() {
		Intent intent = new Intent(ContractActivity.this,
				AddContractActivity.class);
		startActivity(intent);
	}

}
