package com.hengswings.phoneguard.database;

import java.io.Serializable;
//姓名
//手机号码
//家庭号码
//单位名称
//备注

public class DBType implements Serializable {
	public static final String DBNAME = "PHONE";

	public static final String NAME = "name";
	public static final String PHONE_NUMBER = "phone_number";
	public static final String HOME_NUMBER = "home_number";
	public static final String WORK_NAME = "work_name";
	public static final String REMARK_STRING = "remark";

}
