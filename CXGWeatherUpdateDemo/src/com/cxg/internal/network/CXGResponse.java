package com.cxg.internal.network;

import org.json.JSONException;
import org.json.JSONObject;

import com.cxg.internal.common.Logger;

public class CXGResponse {
	final public static int POST_SUCCESS = 201;
	final public static int SUCCESS = 200;
	final public static int SERVER_ERROR = 500;

	final public static String AUTH_TOKEN = "AUTH_TOKEN";

	private CXGRequest currentRequestId = null;

	public CXGRequest getCurrentRequestId() {
		return currentRequestId;
	}

	public void setCurrentRequestId(CXGRequest currentRequestId) {
		this.currentRequestId = currentRequestId;
	}

	private String jResponseAsJonStr = null;
	private String errorMessage = null;
	private String serverErrMessage = null;
	private int statusCode = 0;

	public String getResponseAsJonStr() {
		return jResponseAsJonStr;
	}

	public void setResponseAsJonStr(String jSonResponseStr) {
		this.jResponseAsJonStr = jSonResponseStr;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public final String getServerErrMessage() {
		return serverErrMessage;
	}

	public final void setServerErrMessage(String serverErrMessage) {
		this.serverErrMessage = serverErrMessage;
	}

	public static String parseError(String responseStr) {
		try {
			JSONObject jsonStr = new JSONObject(responseStr);
			if ("" != jsonStr.optString("message")) {
				return jsonStr.optString("message");
			}

		} catch (JSONException e) {
			Logger.error(AsyncTaskExecutor.class.getName(), "getErrorMessage: " + e.getMessage());
			responseStr = "";
		}

		return responseStr;
	}

}
