package com.hengswings.phoneguard.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import com.set.SetPassContractActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MyDBController {
	private SQLiteDatabase sqLiteDatabase;
	private DBHelper sqllitehelper;

	public MyDBController(Context context) {
		
		// 得到sqlliteopenhelper对象,其中封装了一个SQLiteDatabase对象和对其的操作方法
		sqllitehelper = new DBHelper(context, "db", null, 1);
		// 得到sqlliteopenhelper中的数据库对象
		sqLiteDatabase = sqllitehelper.getWritableDatabase();

	}

	// 关闭数据库操作对象和其中的其中的数据库
	public void Close() {
		// 先关闭数据库
		if (sqLiteDatabase != null) {
			sqLiteDatabase.close();
		}
		// 在关闭数据库操作对象
		sqllitehelper.close();
	}

	/**
	 * 写入一个hashmap,然后将其写入到数据库中
	 * 
	 * @param data
	 */
	public void write_db(HashMap<String, String> data) {
		if (sqLiteDatabase != null) {
			String name = data.get(DBType.NAME);
			String phone_number = data.get(DBType.PHONE_NUMBER);
			String home_number = data.get(DBType.HOME_NUMBER);
			String work_name = data.get(DBType.WORK_NAME);
			String remark = data.get(DBType.REMARK_STRING);

			sqLiteDatabase.execSQL("INSERT INTO "
					+ DBType.DBNAME
					+ " VALUES (null,'" + name + "','" + phone_number + "','"
					+ home_number + "','" + work_name + "','" + remark + "');");

		}
	}

	/**
	 * 读数据库,返回数据库中所有的数据
	 * 
	 */
	
	public ArrayList<HashMap<String, String>> read_all_information() {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		if (sqLiteDatabase != null) {
			// sqLiteDatabase = sqllitehelper.getReadableDatabase();
			Cursor cursor = sqLiteDatabase.rawQuery("select * from "
					+ DBType.DBNAME + ";", null);
			while (cursor.moveToNext()) {
				// 把cursor中指的行的放在一个HashMap中
				HashMap<String, String> hash = retu_map(cursor);

				// 将HashMap加入list中
				list.add(hash);
			}

		}
		return list;
	}

	/**
	 * 读数据库，返回数据库所有的姓名和手机号
	 */
	public List<HashMap<String, String>> read_name_phone() {
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		if (sqLiteDatabase != null) {
			// sqLiteDatabase = sqllitehelper.getReadableDatabase();
			Cursor cursor = sqLiteDatabase.rawQuery(
					"select name,phone_number from " + DBType.DBNAME + ";", null);
			while (cursor.moveToNext()) {
				HashMap<String, String> hash = null;
				hash = new HashMap<String, String>();
				// new HashMap<String, String>();
				// 将其依次取出
				String name = cursor
						.getString(cursor.getColumnIndex(DBType.NAME));
				String phone_number = cursor.getString(cursor
						.getColumnIndex(DBType.PHONE_NUMBER));
				// 然后放在hash中
				hash.put(DBType.NAME, name);
				hash.put(DBType.PHONE_NUMBER, phone_number);

				// 将HashMap加入list中
				list.add(hash);
			}

		}
		return list;
	}

	/**
	 * 读数据库中特定的手机号，返回次收好所在的所有信息
	 * 
	 * @param number
	 * @return
	 */
	public HashMap<String, String> read_inf_wherenumber(String number) {
		HashMap<String, String> hash = null;
		if (sqLiteDatabase != null) {
			Cursor cursor = sqLiteDatabase.rawQuery("select * from "
					+ DBType.DBNAME + " Where " + DBType.PHONE_NUMBER + " ='"
					+ number + "';", null);
			if (cursor.moveToNext()) {
				// 把cursor中指的行的放在一个HashMap中
				hash = retu_map(cursor);
			}
		}
		return hash;
	}

	/**
	 * 将数据库中的全部返回
	 * 
	 * @param cursor
	 * @return
	 */
	public HashMap<String, String> retu_map(Cursor cursor) {
		HashMap<String, String> hash = null;
		hash = new HashMap<String, String>();
		// new HashMap<String, String>();
		// 将其依次取出
		String name = cursor.getString(cursor.getColumnIndex(DBType.NAME));
		String phone_number = cursor.getString(cursor
				.getColumnIndex(DBType.PHONE_NUMBER));
		String home_number = cursor.getString(cursor
				.getColumnIndex(DBType.HOME_NUMBER));
		String work_name = cursor.getString(cursor
				.getColumnIndex(DBType.WORK_NAME));
		String remark = cursor.getString(cursor
				.getColumnIndex(DBType.REMARK_STRING));

		// 然后放在HashMap中
		hash.put(DBType.NAME, name);
		hash.put(DBType.PHONE_NUMBER, phone_number);
		hash.put(DBType.HOME_NUMBER, home_number);
		hash.put(DBType.WORK_NAME, work_name);
		hash.put(DBType.REMARK_STRING, remark);

		// 返回回去
		return hash;
	}

	/**
	 * 传进去一个手机号，将手机号所在的数据库行给删除
	 * 
	 * @param number
	 */
	public void delete_map_fornumber(String name, String number) {

		sqLiteDatabase.execSQL("delete FROM " + DBType.DBNAME + " WHERE name='"
				+ name + "' AND phone_number='" + number + "';",
				new Object[] {});

	}

	public long insert_to_pass(List<HashMap<String, String>> pass) {
		ContentValues pass_content = new ContentValues();
		long result = 1;
		// 意思为遍历list集合pass中的所有元素，然后都赋值给i
		for (HashMap<String, String> i : pass) {
			pass_content.put(DBHelper.db2_people_name,
					i.get(DBHelper.db2_people_name));
			// 添加进入手机号
			pass_content.put(DBHelper.db2_num,
					i.get(DBHelper.db2_num));
			result = sqLiteDatabase.insert(DBHelper.db2_name,
					DBHelper.db2_main, pass_content);

		}
		/**
		 * 返回数据库的所有的
		 */
		Cursor cursor = sqLiteDatabase.query(DBHelper.db2_name,
				new String[] { DBHelper.db2_num }, null, null, null,
				null, null);
		while (cursor.moveToNext()) {
			String num = cursor.getString(cursor
					.getColumnIndex(DBHelper.db2_num));
			if (num == null) {
				continue;
			}
		}

		return result;

	}

	/**
	 * 返回一个白名单List<String>集合,里面只有手机号
	 * 
	 * @return
	 */
	public List<String> return_pass_num() {
		List<String> psss = null;
		psss = new ArrayList<String>();
		Cursor cursor = sqLiteDatabase.query(DBHelper.db2_name,
				new String[] { DBHelper.db2_num }, null, null, null,
				null, null);
		while (cursor.moveToNext()) {
			String num_db = cursor.getString(cursor
					.getColumnIndex(DBHelper.db2_num));
			if (num_db == null) {
				continue;
			}
			// 如果不为空，去除掉空格
			num_db = num_db.trim();
			psss.add(num_db);

		}
		return psss;
	}

	/**
	 * 返回一个白名单List<HashMap<String, String>>集合,里面有所有白名单的手机号以及其对应的姓名
	 * 
	 * @return
	 */
	public List<HashMap<String, String>> return_pass_all() {
		List<HashMap<String, String>> psss = null;
		psss = new ArrayList<HashMap<String, String>>();
		Cursor cursor = sqLiteDatabase.query(DBHelper.db2_name,
				new String[] { DBHelper.db2_people_name,
						DBHelper.db2_num }, null, null, null, null,
				null);
		while (cursor.moveToNext()) {
			HashMap<String, String> hashMap = null;
			hashMap = new HashMap<String, String>();
			// 得到姓名
			String name = cursor.getString(cursor
					.getColumnIndex(DBHelper.db2_people_name));
			// 得到手机号
			String num_db = cursor.getString(cursor
					.getColumnIndex(DBHelper.db2_num));

			// 姓名为空或者手机号为空都是不行的
			if (num_db == null || name == null || num_db.equals("")
					|| name.equals("")) {
				continue;
			}
			name = name.trim();
			num_db = num_db.trim();
			hashMap.put(DBHelper.db2_people_name, name);
			hashMap.put(DBHelper.db2_num, num_db);
			psss.add(hashMap);
		}
		return psss;
	}

	/**
	 * 
	 * @param num_list
	 * @return 返回的为删除的行数 返回0说明传进来的为空，失败了，或者是删除失败 返回大于0的说明删除成功了
	 */
	public int delete_pass_fromphone_num(List<String> num_list) {
		int result = 0;
		if (num_list == null || num_list.isEmpty()) {
			return 0;
		}
		for (String num : num_list) {
			result = sqLiteDatabase.delete(DBHelper.db2_name,
					DBHelper.db2_num + " = '" + num + "'", null);
		}
		return result;

	}
}
