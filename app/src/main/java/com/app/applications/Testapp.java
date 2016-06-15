package com.app.applications;

import com.utils.crash.crash;

import android.app.Application;

public class Testapp extends Application {

	public Testapp() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();//必须保留
		//创建crash 
		crash cr = (crash) crash.getDefaultException();
		cr.init(this);
	}
	
}
