package com.cxg.internal.network;

import org.apache.http.client.HttpClient;

import android.content.Context;

/**
 * 
 * @author CXG Pvt Ltd., Singapore.
 *
 */

/**
 * 
 * This class is used to communicate with back-end server.
 *
 */
public interface ICXGCommunicationMananger {

	/**
	 * To send asynchronous request {@link CXGRequest} to back-end server with
	 * callback {@link ICXGResponseCallback}. <br>
	 * Note: </br>
	 * It will NOT run on Main/UI Thread.
	 * 
	 * @param Context
	 * @param uchRequest
	 * @param responseCallback
	 */
	public void sendAsyncRequest(Context context, CXGRequest uchRequest, ICXGResponseCallback responseCallback);

	/**
	 * To send CXG synchronized {@link CXGRequest} to back-end server and wait
	 * for response with callback {@link ICXGResponseCallback} <br>
	 * Note: </br>
	 * It will run on Main/UI Thread.
	 * 
	 * @param Context
	 * @param uchRequest
	 * @param responseCallback
	 */
	public void sendSyncRequest(Context context, CXGRequest uchRequest, ICXGResponseCallback responseCallback);

	/**
	 * Custom CXG HttpClient for http and https request support !
	 * 
	 * @return
	 */
	public HttpClient getCXGHttpClient();

}
