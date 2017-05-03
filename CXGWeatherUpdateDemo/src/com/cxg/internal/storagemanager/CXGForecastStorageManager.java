package com.cxg.internal.storagemanager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.cxg.internal.forecastdata.CXGForecast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.content.Context;
import android.util.Log;

/**
 * 
 * @author CXG Pvt Ltd., Singapore.
 * 
 *         This class is used to persist the latest forecast data (hourly,daily
 *         & everything) in a file and get it as CXGForecast POJO Class to make
 *         it use wherever and whenever we want it !
 *
 */
final public class CXGForecastStorageManager implements ICXGForecastStorageManager {

	private String jsonStr = null;
	private Context appContext = null;

	private static ICXGForecastStorageManager INSTANCE = null;

	private static Object classLock = new Object();

	public static ICXGForecastStorageManager getInstance(Context appContext) {

		synchronized (classLock) {

			if (INSTANCE == null) {
				INSTANCE = new CXGForecastStorageManager(appContext);
			}
			return INSTANCE;
		}
	}

	@Override
	public void shutDownManager() {
		appContext = null;
		jsonStr = null;
	}

	private CXGForecastStorageManager(Context context) {
		appContext = context;
	}

	@Override
	public CXGForecast getSavedCXGForecastData() {
		try {
			return readObject();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void storeCXGForecastData(String jsonRespStr) {
		try {
			this.jsonStr = jsonRespStr;
			writeToFile(jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void writeToFile(String data) {
		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					appContext.openFileOutput("cxg_forecast.json", Context.MODE_PRIVATE));
			outputStreamWriter.write(data);
			outputStreamWriter.close();
		} catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}

	private CXGForecast readObject() {

		CXGForecast forecastDataObj = null;
		try {
			InputStream inputStream = appContext.openFileInput("cxg_forecast.json");

			if (inputStream != null) {
				final InputStreamReader reader = new InputStreamReader(inputStream);

				Gson gson = new GsonBuilder().create();
				forecastDataObj = gson.fromJson(reader, CXGForecast.class);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return forecastDataObj;
	}

}
