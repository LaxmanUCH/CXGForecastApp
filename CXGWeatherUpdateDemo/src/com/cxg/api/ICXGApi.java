package com.cxg.api;

import com.cxg.internal.common.CXGApiException;
import com.cxg.internal.network.ICXGResponseCallback;

/**
 * Created by CXG Pvt Ltd., Singapore.
 */

public interface ICXGApi {

	void pingForecastApi(ICXGResponseCallback responseCallBack, String userAuthKey) throws CXGApiException;

	void shutdownApi();

}
