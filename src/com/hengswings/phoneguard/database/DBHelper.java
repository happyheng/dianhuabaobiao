package com.hengswings.phoneguard.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
	public static String db2_name = "PASS_PHONEMUN";
	public static String db2_main = "main_num";
	public static String db2_people_name = "name";
	public static String db2_num = "phone_num";

	public DBHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);

	}

	/**
	 * 在调getReadableDatabase或getWritableDatabase时，
	 * 会判断指定的数据库是否存在，不存在则调SQLiteDatabase.create创建， onCreate只在数据库第一次创建时才执行
	 */
	// 注意这个onCreate只是有一个SQLiteDatabase的局部对象，本身并没有封装
	public void onCreate(SQLiteDatabase db) {
		// sqLiteDatabase=db;
		// 如果没有则创建
		//
		/**
		 * 从 SQLite 的 2.3.4 版本开始，如果将一个表中的一个字段声明为 INTEGER PRIMARY KEY，那么只
		 * 需向该表的该字段插入一个 NULL 值， 这个 NULL 值将自动被更换为比表中该字段所有行的最大值大 1
		 * 的整数；如果表为空，那么将被更换为 1。
		 * 
		 * 网址:http://blog.csdn.net/lanruoshui/article/details/5179946
		 */
		//创建私人通讯录的语句
		db.execSQL("CREATE TABLE  IF NOT EXISTS  PHONE ( main_number integer(4)  primary key ,"
				+ "name varchar(50),"
				+ "phone_number varchar(50),"
				+ "home_number varchar(50),"
				+ "work_name varchar(50),"
				+ "remark varchar(70)" + ");");
		//创建白名单的语句
		db.execSQL("CREATE TABLE  PASS_PHONEMUN ( main_num integer(4)  primary key ,"
				+ "name varchar(30)," + "phone_num varchar(30)" + ");");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		db.execSQL("DROP TABLE PASS_PHONEMUN;");
		onCreate(db);

	}

}
