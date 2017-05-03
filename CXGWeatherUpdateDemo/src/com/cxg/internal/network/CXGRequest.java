/**
 * 
 */
package com.cxg.internal.network;

import java.util.HashMap;

import android.util.Log;

/**
 * @author CXG Pvt Ltd., Singapore
 *
 */

/**
 * 
 * This class is used to hold the CXG Request.
 *
 */

final public class CXGRequest {

	public String getRequest_type() {
		return request_type;
	}

	public void setRequest_type(String request_type) {
		this.request_type = request_type;
	}

	final public static String GET = "GET";
	final public static String POST = "POST";
	final public static String MIME = "application/mime";

	// input parameters basic constants..
	final public static String TYPE = "TYPE"; // to know the
												// request type
												// i.e.
												// GET/POST/PATCH/MIME..
												// method.

	final public static String URL = "URL";

	final public static String API_KEY = "API_KEY";

	final public static String LATITUDE = "LATITUDE";

	final public static String LONGITUDE = "LONGITUDE";

	final public static String ACCEPT_LANGUAGE = "Accept-Language";

	final public static String CONTENT_TYPE = "CONTENT-TYPE";

	private String request_type = null;

	final int request_id;

	final private HashMap<String, String> inputParams = new HashMap<String, String>();

	private String jsonStrInputParam = null;

	public CXGRequest(int requestId) {
		request_id = requestId;
	}

	public int getRequest_id() {
		return request_id;
	}

	/**
	 * To add input parameter to request.
	 * 
	 * @param paramName
	 * @param paramValue
	 */
	public void addInputParam(String paramName, String paramValue) {
		inputParams.put(paramName, paramValue);
	}

	/**
	 * To add json string parameter to request. <br>
	 * <br>
	 * <b> <font color="red">Important Note:</font></b> <br>
	 * Each Request may take only one jsonStr input parameter only. If pass more
	 * than one, it will be overwrite by recent set value.
	 * 
	 * @param paramName
	 * @param jsonStrParamValue
	 */
	public void setJsonInputParam(String jSonRequestStr) {
		if (null == jSonRequestStr) {
			Log.w("Request",
					"request, in json string, should not be null., this request may not get response from uch service");
			return;
		}
		this.jsonStrInputParam = jSonRequestStr;
	}

	public HashMap<String, String> getInputParams() {
		return inputParams;
	}

	private String url = null;

	/**
	 * To get url that used by request.
	 * 
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	public final String getJsonStrInputParam() {
		return jsonStrInputParam;
	}

	/**
	 * To set url to request.
	 * 
	 * @param url
	 */

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 
	 */
}
