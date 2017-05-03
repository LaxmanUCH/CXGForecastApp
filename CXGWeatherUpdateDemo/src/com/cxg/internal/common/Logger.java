package com.cxg.internal.common;

import android.util.Log;

/**
 * @author CXG Pvt Ltd., of Singapore.
 *
 */
public abstract class Logger {

	private static boolean BUILD_CHECK_FLAG = false;

	private static String TAG = Logger.class.getName();

	static {
		if (Env.DEBUG_MODE && !BUILD_CHECK_FLAG) {
			Log.w(TAG, "Current Build is in DEBUG Mode., Not Recommanded for Production Release! ");
			BUILD_CHECK_FLAG = true;
		}
	}

	public static void warn(String TAG, String message) {
		if (isDebugEnable()) {
			Log.w(TAG, message);
		}
	}

	public static void error(String TAG, String message) {
		if (isDebugEnable()) {
			Log.e(TAG, message);
		}
	}

	public static void debug(String TAG, String message) {
		if (isDebugEnable()) {
			Log.d(TAG, message);
		}
	}

	public static void info(String TAG, String message) {
		if (isDebugEnable()) {
			Log.i(TAG, message);
		}
	}

	public static void ver(String TAG, String message) {
		if (isDebugEnable()) {
			Log.v(TAG, message);
		}
	}

	private static boolean isDebugEnable() {
		return Env.DEBUG_MODE;

	}

}
