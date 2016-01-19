package com.sumeet.crash;

import android.app.Application;

public class MyApp extends Application {

	public void onCreate() {
		Crash.init(getApplicationContext());
	};
}
