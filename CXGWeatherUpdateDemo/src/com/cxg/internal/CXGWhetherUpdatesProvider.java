package com.cxg.internal;

import com.cxg.api.ICXGApi;
import com.cxg.internal.locationservice.CXGLocationManager;
import com.cxg.internal.locationservice.CXGLocationManager.ICXLocationListener;
import com.cxg.internal.locationservice.ICXGLocationManager;
import com.cxg.internal.network.ICXGResponseCallback;
import com.cxg.internal.weathertracker.CXGWeatherAsyncTracker;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

/**
 * Created by CXG Pvt Ltd., Singapore.
 */

final public class CXGWhetherUpdatesProvider implements ICXGApi, ICXLocationListener {

	private static ICXGApi INSTANCE = null;
	private static Context appContext = null;
	private static Object apiLock = new Object();
	private static boolean isRunning = false;
	private ICXGResponseCallback responseCallBack;

	@Override
	public void shutdownApi() {

		isRunning = false;
		this.responseCallBack = null;
		Log.i("CXGBackgroundService", "CXGWhetherUpdatesProvider.isRunning: " + CXGWhetherUpdatesProvider.isRunning);
		appContext = null;
	}

	@Override
	public void pingForecastApi(ICXGResponseCallback cxgCallBack, String userAuthKey) {

		this.responseCallBack = cxgCallBack;
		new Thread(new Runnable() {

			@Override
			public void run() {

				Looper.prepare();
				Log.i("CXGBackgroundService",
						"CXGWhetherUpdatesProvider.isRunning: " + CXGWhetherUpdatesProvider.isRunning);
				final ICXGLocationManager icxgLocationManager = CXGLocationManager.getInstance(appContext);
				icxgLocationManager.getLocation(appContext, CXGWhetherUpdatesProvider.this);
				Looper.loop();
			}
		}).start();

	}

	@Override
	public void onForecastDataReceived(double latitude, double longitude) {
		Log.i("CXGBackgroundService", "onForecastDataReceived.. " + latitude + "," + longitude);
		final CXGWeatherAsyncTracker updatesAsyncTask = new CXGWeatherAsyncTracker(responseCallBack, "" + latitude,
				"" + longitude);
		updatesAsyncTask.execute();
	}

	public static ICXGApi getInstance(Context context) {

		synchronized (apiLock) {
			if (INSTANCE == null) {
				INSTANCE = new CXGWhetherUpdatesProvider(context);
			}
		}
		CXGWhetherUpdatesProvider.appContext = context;
		return INSTANCE;
	}

	private CXGWhetherUpdatesProvider(Context context) {
		CXGWhetherUpdatesProvider.appContext = context;
	}
}