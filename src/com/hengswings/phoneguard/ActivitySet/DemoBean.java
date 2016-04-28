package com.hengswings.phoneguard.ActivitySet;

public class DemoBean {

	//名字
	private String title;
	//手机号码
	private String phone_num;

	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getphone_num() {
		return phone_num;
	}

	public void setphone_num(String num) {
		this.phone_num = num;
	}


	public DemoBean(String title, String num) {
		this.title = title;
		this.phone_num = num;
	}

	public DemoBean() {
	}

}

