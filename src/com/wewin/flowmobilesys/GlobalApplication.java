package com.wewin.flowmobilesys;

import android.app.Application;

/**
 * ȫ�ֱ��������࣬��Ҫ�洢�û�ID
 * 
 * @author HCOU
 * @time 2013.05.27 17:37:00
 */
public class GlobalApplication extends Application {
	private String userId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
