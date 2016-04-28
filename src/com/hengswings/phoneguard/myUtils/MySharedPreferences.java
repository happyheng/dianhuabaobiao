package com.hengswings.phoneguard.myUtils;


import com.hengswings.phoneguard.ActivitySet.SetActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MySharedPreferences {
	private Context myContext;
	private SharedPreferences sharedPreferences;
	private Editor editor;

	public MySharedPreferences(Context context) {
		this.myContext = context;
		sharedPreferences = myContext.getSharedPreferences(SetActivity.main,
				Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
	}

	public boolean getBoolean(String key, boolean defValue) {
		boolean result = sharedPreferences.getBoolean(key, defValue);
		return result;
	}

	public String getString(String key, String defValue) {
		String result = sharedPreferences.getString(key, defValue);
		return result;
	}

	public void putBoolean(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	public void putString(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}
}
