/**
 * 
 */
package com.cxg.internal.storagemanager;

import com.cxg.internal.forecastdata.CXGForecast;

/**
 * @author CXG Pvt Ltd., Singapore.
 *
 */
public interface ICXGForecastStorageManager {

	void shutDownManager();

	CXGForecast getSavedCXGForecastData();

	void storeCXGForecastData(String jsonStr);
}
