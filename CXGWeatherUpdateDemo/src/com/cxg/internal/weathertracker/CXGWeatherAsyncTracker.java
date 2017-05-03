package com.cxg.internal.weathertracker;

import com.cxg.internal.CXGContext;
import com.cxg.internal.common.CXGApiException;
import com.cxg.internal.common.Logger;
import com.cxg.internal.network.CXGCommunicationManager;
import com.cxg.internal.network.CXGRequest;
import com.cxg.internal.network.ICXGCommunicationMananger;
import com.cxg.internal.network.ICXGResponseCallback;

import android.os.AsyncTask;

/**
 * Created by CXG Pvt Ltd., Singapore.
 */

/**
 * This Async Task is to get updated weather report and send to you UI using
 * responseCallback to reflect the latest weather data in that UI.
 */
public class CXGWeatherAsyncTracker extends AsyncTask<Void, Void, Void> {

	private ICXGResponseCallback icxgResponseCallBack;
	private String latitude = null;
	private String longitude = null;

	public CXGWeatherAsyncTracker(ICXGResponseCallback responseCallBack, String latitude, String longitude) {
		this.icxgResponseCallBack = responseCallBack;
		this.latitude = latitude;
		this.longitude = longitude;

	}

	@Override
	protected Void doInBackground(Void... param) {

		Logger.debug("CXGWeatherAsyncTracker", "latitude: " + latitude + " & longitude: " + longitude);

		final CXGRequest cxgRequest = new CXGRequest(1);
		cxgRequest.addInputParam(CXGRequest.LATITUDE, latitude);
		cxgRequest.addInputParam(CXGRequest.LONGITUDE, longitude);
		cxgRequest.addInputParam(CXGRequest.API_KEY, CXGCommunicationManager.CXG_DEFAULT_APIKEY);
		cxgRequest.setUrl(CXGCommunicationManager.CXG_FORCAST_API_URL);
		cxgRequest.setRequest_type(CXGRequest.GET);

		try {
			final ICXGCommunicationMananger mananger = CXGCommunicationManager.getInstance();
			mananger.sendAsyncRequest(CXGContext.getCXGContext(), cxgRequest, icxgResponseCallBack);

		} catch (CXGApiException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void s) {
		super.onPostExecute(s);
	}
}
