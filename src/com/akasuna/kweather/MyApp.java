package com.akasuna.kweather;

import android.content.Context;

public class MyApp extends android.app.Application {
	private static MyApp instance;

	public MyApp() {
		instance = this;
	}

	public static Context getContext() {
		return instance;
	}
}
