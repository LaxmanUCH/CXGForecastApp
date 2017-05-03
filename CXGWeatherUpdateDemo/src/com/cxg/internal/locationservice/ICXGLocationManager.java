package com.cxg.internal.locationservice;

import com.cxg.internal.locationservice.CXGLocationManager.ICXLocationListener;

import android.content.Context;

public interface ICXGLocationManager {

	void getLocation(Context appContext, ICXLocationListener locationListener);

	void shutDownManager();

}