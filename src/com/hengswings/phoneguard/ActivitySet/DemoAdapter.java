package com.hengswings.phoneguard.ActivitySet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hengswings.phoneguard.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class DemoAdapter extends BaseAdapter {
	/**
	 * 上下文对象
	 */
	private Context context = null;
	/**
	 * 数据集合
	 */
	private List<DemoBean> datas = null;

	/**
	 * CheckBox 是否选择的存储集合,key 是 position , value 是该position是否选中
	 */
	private Map<Integer, Boolean> isCheckMap = new HashMap<Integer, Boolean>();

	public DemoAdapter(Context context, List<DemoBean> datas) {
		this.datas = datas;
		this.context = context;

		// 初始化,默认都没有选中
		configCheckMap(false);
	}

	/**
	 * 首先,默认情况下,所有项目都是没有选中的.这里进行初始化
	 */
	public void configCheckMap(boolean bool) {

		for (int i = 0; i < datas.size(); i++) {
			isCheckMap.put(i, bool);
		}

	}

	@Override
	public int getCount() {
		return datas == null ? 0 : datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewGroup layout = null;

		/**
		 * 进行ListView 的优化
		 */
		if (convertView == null) {
			layout = (ViewGroup) LayoutInflater.from(context).inflate(
					R.layout.listview_item_layout, parent, false);
		} else {
			layout = (ViewGroup) convertView;
		}

		DemoBean bean = datas.get(position);

		/*
		 * 设置每一个item的文本
		 */
		TextView tvTitle = (TextView) layout.findViewById(R.id.tvTitle);
		tvTitle.setText(bean.getTitle());

		/*
		 * 获得单选按钮
		 */
		CheckBox cbCheck = (CheckBox) layout.findViewById(R.id.cbCheckBox);

		/*
		 * 设置单选按钮的选中
		 */
		cbCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				/*
				 * 将选择项加载到map里面寄存
				 */
				isCheckMap.put(position, isChecked);
			}
		});

		cbCheck.setVisibility(View.VISIBLE);

		if (isCheckMap.get(position) == null) {
			isCheckMap.put(position, false);
		}

		cbCheck.setChecked(isCheckMap.get(position));

		ViewHolder holder = new ViewHolder();

		holder.cbCheck = cbCheck;

		holder.tvTitle = tvTitle;

		/**
		 * 将数据保存到tag
		 */
		layout.setTag(holder);

		return layout;
	}

	/**
	 * 增加一项的时候
	 */
	public void add(DemoBean bean) {
		this.datas.add(0, bean);

		// 让所有项目都为不选择
		configCheckMap(false);
	}

	// 移除一个项目的时候
	public void remove(int position) {
		this.datas.remove(position);
	}

	public Map<Integer, Boolean> getCheckMap() {
		return this.isCheckMap;
	}

	public static class ViewHolder {

		public TextView tvTitle = null;

		public CheckBox cbCheck = null;
		public Object data = null;

	}

	public List<DemoBean> getDatas() {
		return datas;
	}

}
