package com.sumeet.crash;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

/**
 * @author sumeet.kumar@intelligenes.com 14/12/2015
 */

public final class Crash {

	private static final String LOG_CAT = "LOGCAT";
	public static final String EMAIL = "sumeet@gmail.com";
	public static final String SUBJECT = "Demo App v1.0 is crashed";

	private final static String TAG = "CrashReport";
	private static final int SIZE = 128000;

	private static Application app;
	// as acra use to monitor last activity which crashed app
	private static WeakReference<Activity> last_activity = new WeakReference<>(null);

	// initialize Crash Report
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public static void init(Context context) {
		try {
			if (context == null)
				return;
			Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
			if (handler != null) {
				Log.e(TAG, "remove ACRA.init(); & add  CRASH.init();");
				return;
			}

			app = (Application) context.getApplicationContext();

			Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
				@Override
				public void uncaughtException(Thread thread, final Throwable throwable) {
					// print throwable to stack trace
					android.util.Log.e(TAG, "UncaughtExceptionHandler ", throwable);
					if (checkLogCat(throwable, ErrorActivity.class)) {
						Log.e(TAG, "uncaughtException ", throwable);
					} else {
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						throwable.printStackTrace(pw);
						// throwable.getStackTrace().toString()
						String logs = sw.toString();

						// TransactionTooLargeException is thrown when
						// sending the intent, Reduce data to 128KB
						if (logs.length() > SIZE) {
							logs = logs.substring(0, SIZE);
						}
						launchErrorActivity(logs);
						/// Log.i(TAG, logs);
					}
					final Activity lastActivity = last_activity.get();
					if (lastActivity != null) {
						lastActivity.finish();
						last_activity.clear();
					}
					killProcess();
				}
			});

			// from ACRA:
			if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {

					@Override
					public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
						if (activity.getClass() != ErrorActivity.class) {
							last_activity = new WeakReference<>(activity);
						}
					}

					@Override
					public void onActivityStarted(Activity activity) {
					}

					@Override
					public void onActivityResumed(Activity activity) {
					}

					@Override
					public void onActivityPaused(Activity activity) {
					}

					@Override
					public void onActivityStopped(Activity activity) {
					}

					@Override
					public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
					}

					@Override
					public void onActivityDestroyed(Activity activity) {
					}
				});
			}

		} catch (Throwable t) {
			android.util.Log.e(TAG, "An unknown error occurred", t);
		}
	}

	public static String getLogs(Intent intent) {
		return intent.getStringExtra(Crash.LOG_CAT);
	}

	public static void launchErrorActivity(String str) {
		final Intent intent = new Intent(app, ErrorActivity.class);
		intent.putExtra(LOG_CAT, str);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		app.startActivity(intent);
	}

	public static void restart(Activity activity, Intent intent) {
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		activity.finish();
		activity.startActivity(intent);
		killProcess();
	}

	public static void restart(Context context) {
		Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
		launchIntent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(launchIntent);
	}

	// from ACRA
	private static boolean checkLogCat(Throwable throwable, Class<? extends Activity> activityClass) {
		do {
			StackTraceElement[] stackTrace = throwable.getStackTrace();
			for (StackTraceElement element : stackTrace) {
				if ((element.getClassName().equals("android.app.ActivityThread")
						&& element.getMethodName().equals("handleBindApplication"))
						|| element.getClassName().equals(activityClass.getName())) {
					return true;
				}
			}
		} while ((throwable = throwable.getCause()) != null);
		return false;
	}

	// ACRA
	static void killProcess() {
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(10);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public static String getErrorDetails(ErrorActivity context, Intent intent) {
		Date currentDate = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

		StringBuilder error = new StringBuilder();
		error.append("Current date: " + dateFormat.format(currentDate) + " \n");
		error.append("Device: " + Build.MANUFACTURER + " " + Build.MODEL + " \n");
		error.append("Android SDK: " + Build.VERSION.SDK_INT + " \n");
		error.append("Android Version: " + Build.VERSION.RELEASE + " \n");
		error.append("Product: " + Build.PRODUCT + " \n");
		if (Build.VERSION.SDK_INT > 20)
			error.append("ABIS (86x64): " + Arrays.toString(Build.SUPPORTED_ABIS) + " \n");
		error.append("\n\n LogCat:  \n");
		error.append(getLogs(intent));
		return error.toString();
	}

}
