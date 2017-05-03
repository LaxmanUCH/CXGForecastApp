/**
 * 
 */
package com.cxg.internal.network;

/**
 * @author CXG Pvt Ltd., Singapore.
 *
 */
public interface ICXGResponseCallback {

	public void onProgress(String progressMsg);

	public void onCancel(String errorMessage);

	public void onFail(String errorMessage, int errorCode);

	/**
	 * Callback on successful response
	 * 
	 * @param CXG_response
	 */

	public void onComplete(CXGResponse cxg_response);

}
