package com.cxg.internal.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import com.cxg.api.R;
import com.cxg.internal.common.Logger;
import com.cxg.internal.common.Util;

import android.content.Context;
import android.net.ParseException;
import android.net.Uri;

final public class SyncTaskExecutor implements com.cxg.internal.network.CXGCommunicationManager.RequestHanlder {

	final private String TAG = SyncTaskExecutor.class.getName();

	final private ICXGResponseCallback responseCallBack;

	private String actionUrl;
	private String authToken;

	private String contentType = "";

	// private String acceptLanguage;

	private String actionType;

	final private CXGRequest uchRequest;
	final private Context appContext;
	private CXGResponse uchResponse = null;

	private ClientConnectionManager clientConnectionManager = null;

	// private long request_start_time = 0;
	// private long request_end_time = 0;

	public SyncTaskExecutor(Context context, CXGRequest uchRequest, ICXGResponseCallback responseCallback) {
		responseCallBack = responseCallback;
		this.uchRequest = uchRequest;
		appContext = context;
		// request_start_time = request_end_time = 0;
	}

	public void execute() {

		String responseStr = null;

		if (null == uchRequest || null == uchRequest.getInputParams()) {
			responseCallBack.onCancel(appContext.getResources().getString(R.string.msg_null_or_empty));
			return;
		}

		HashMap<String, String> inputParams = uchRequest.getInputParams();

		authToken = inputParams.get(CXGRequest.API_KEY);

		contentType = inputParams.get(CXGRequest.CONTENT_TYPE);

		actionUrl = uchRequest.getUrl();

		if (null == actionUrl || actionUrl.equalsIgnoreCase("")) {
			actionUrl = inputParams.get(CXGRequest.URL);
		}
		actionType = uchRequest.getRequest_type();

		if (null == actionType || null == actionUrl) {
			responseCallBack.onCancel(appContext.getResources().getString(R.string.msg_null_or_empty));
			return;
		}

		if (null != uchRequest.getJsonStrInputParam() && !uchRequest.getJsonStrInputParam().isEmpty()
				&& null == contentType) {
			String err_msg = appContext.getResources().getString(R.string.msg_invld_rqst);
			Logger.warn(TAG, err_msg);
			responseCallBack.onCancel(err_msg);
			return;
		}

		if (uchRequest.getRequest_id() != 1 && null == authToken) {
			responseCallBack.onCancel(appContext.getResources().getString(R.string.msg_null_or_empty));
			return;
		}

		if (actionType == CXGRequest.GET) {
			responseStr = processGetRequest();
		} else if (actionType == CXGRequest.POST) {
			responseStr = processPostRequest();
		} else {
			String err_msg = appContext.getResources().getString(R.string.msg_invld_rqst_mt);
			Logger.warn(TAG, err_msg);
			responseCallBack.onCancel(err_msg);
		}

		Logger.debug(TAG, "CXG Response: " + responseStr);

		if (null != uchResponse
				&& (null != uchResponse.getResponseAsJonStr() && !uchResponse.getResponseAsJonStr().isEmpty())) {
			responseCallBack.onComplete(uchResponse);
		} else if (null != uchResponse
				&& (null != uchResponse.getErrorMessage() || null != uchResponse.getServerErrMessage())) {
			if (uchResponse.getErrorMessage().startsWith("Socket") || uchResponse.getErrorMessage().contains("abort")) {
				responseCallBack.onCancel(uchResponse.getErrorMessage());
			} else
				responseCallBack.onFail(uchResponse.getErrorMessage(), uchResponse.getStatusCode());
		} else if (!Util.isNetworkOnline(appContext)) {
			responseCallBack.onFail(appContext.getString(R.string.lbl_network_unavailable), -444);
		} else {
			responseCallBack.onCancel(appContext.getString(R.string.lbl_network_unavailable));
		}

	}

	// private methods implementations..
	private String processPostRequest() {

		StringBuffer responseBuilder = null;

		HttpPost httpPost = new HttpPost(actionUrl);

		if (uchRequest.getRequest_id() != 1) {
			httpPost.addHeader("Authorization", "Token " + authToken);
		} else {
			Logger.warn(TAG, "Token is ignoring to attach to your request due to request id "
					+ uchRequest.getRequest_id() + " represents login request !!!");
		}
		uchResponse = null;

		HttpResponse httpResponse = null;
		try {

			if (null != uchRequest.getJsonStrInputParam() && !uchRequest.getJsonStrInputParam().isEmpty()
					&& null != contentType) {
				Logger.debug(TAG, "UCH Request contains jsonString in processing request: "
						+ uchRequest.getJsonStrInputParam() + " with content-type: " + contentType);
				httpPost.addHeader("Content-Type", contentType);
				httpPost.setEntity(getJsonRequestEntity());
			} else {
				Logger.debug(TAG, "UrlEncodedFormEntity request preparing..");
				httpPost.setEntity(new UrlEncodedFormEntity(getRequestParameters(), HTTP.UTF_8));
			}

			// TODO set timeout for client connection..
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 60000);
			// HttpConnectionParams.setSoTimeout(httpParams, 15000);
			HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);
			HttpConnectionParams.setTcpNoDelay(httpParams, true);

			final HttpClient httpClient = new DefaultHttpClient(httpParams);

			httpClient.getConnectionManager().closeExpiredConnections();

			// HttpClient httpClient = new DefaultHttpClient();

			httpResponse = httpClient.execute(httpPost);
			StatusLine statusLine = httpResponse.getStatusLine();
			int statusCode = statusLine.getStatusCode();

			uchResponse = new CXGResponse();

			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			httpResponse.getEntity().writeTo(out);
			responseBuilder = new StringBuffer(out.toString());

			if (statusCode == CXGResponse.SUCCESS || statusCode == CXGResponse.POST_SUCCESS) {
				uchResponse.setResponseAsJonStr(responseBuilder.toString());
				uchResponse.setStatusCode(CXGResponse.SUCCESS);
			} else {
				if (null != statusLine.getReasonPhrase()) {
					uchResponse.setResponseAsJonStr(null);
					uchResponse.setErrorMessage(CXGResponse.parseError(responseBuilder.toString()));
					uchResponse.setServerErrMessage(statusLine.getReasonPhrase());
					uchResponse.setStatusCode(statusCode);
					Logger.error(ParseException.class.toString(), "Exception reasonPhrase: "
							+ statusLine.getReasonPhrase() + " and statusCode: " + statusCode);
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}

		// return responseBuilder.toString();

		return responseBuilder == null ? null : responseBuilder.toString();

	}

	private String processGetRequest() {

		StringBuffer responseBuilder = null;

		Uri uri = Uri.parse(actionUrl);

		if (uri.getQueryParameter("after_id") != null & uri.getQueryParameter("after_id") == "") {
		}

		HttpGet httpGet = new HttpGet(actionUrl);

		if (null != authToken) {
			httpGet.addHeader("Authorization", "Token " + authToken);
		}

		try {
			// TODO set timeout for client connection..
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 60000);
			// HttpConnectionParams.setSoTimeout(httpParams, 15000);
			HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);
			HttpConnectionParams.setTcpNoDelay(httpParams, true);

			HttpClient httpClient = new DefaultHttpClient(httpParams);
			httpClient.getConnectionManager().closeExpiredConnections();

			HttpResponse httpResponse;

			httpResponse = httpClient.execute(httpGet);
			StatusLine statusLine = httpResponse.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			uchResponse = new CXGResponse();

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			httpResponse.getEntity().writeTo(out);
			responseBuilder = new StringBuffer(out.toString());

			if (statusCode == CXGResponse.SUCCESS) {
				uchResponse.setResponseAsJonStr(responseBuilder.toString());
				uchResponse.setStatusCode(CXGResponse.SUCCESS);
			} else {
				if (null != statusLine.getReasonPhrase()) {

					uchResponse.setErrorMessage(CXGResponse.parseError(responseBuilder.toString()));
					uchResponse.setStatusCode(statusCode);
					Logger.error(ParseException.class.toString(), "Exception reasonPhrase: "
							+ statusLine.getReasonPhrase() + " and statusCode: " + statusCode);
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return responseBuilder == null ? null : responseBuilder.toString();
	}

	private List<NameValuePair> getRequestParameters() {

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		for (Map.Entry<String, String> entry : uchRequest.getInputParams().entrySet()) {
			String key = (String) entry.getKey();
			String value = (String) uchRequest.getInputParams().get(key);
			Logger.debug(TAG, "param-key: " + key + " & param-value: " + value);
			nameValuePairs.add(new BasicNameValuePair(key, value));
		}

		return nameValuePairs;
	}

	private StringEntity getJsonRequestEntity() {
		try {
			StringEntity requestEntity = new StringEntity(uchRequest.getJsonStrInputParam());
			requestEntity.setContentType(contentType);
			return requestEntity;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void cancel() {
		try {
			uchResponse.setResponseAsJonStr(null);
			uchResponse.setErrorMessage(appContext.getResources().getString(R.string.msg_invld_rqst));
			uchResponse.setStatusCode(400);
			Logger.warn(ParseException.class.toString(),
					"Exception reasonPhrase: " + "REquest has been cancel by user forcefully !");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void cancelRequest() {
		if (null != clientConnectionManager) {
			clientConnectionManager.closeIdleConnections(50, TimeUnit.MILLISECONDS);
			clientConnectionManager.shutdown();
		} else {
			Logger.warn(TAG, "request manager null !");
		}
	}

}
