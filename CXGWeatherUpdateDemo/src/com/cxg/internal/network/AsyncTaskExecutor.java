/**
 * 
 */
package com.cxg.internal.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
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
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
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
import android.os.AsyncTask;

/**
 * @author CXG Pvt Ltd., Singapore.
 *
 */
final public class AsyncTaskExecutor extends AsyncTask<Void, Void, String>
		implements com.cxg.internal.network.CXGCommunicationManager.RequestHanlder {

	final private String TAG = AsyncTaskExecutor.class.getName();

	final private ICXGResponseCallback responseCallBack;

	private String forcastApiUrl;
	private String apiKey;
	// private String acceptLanguage;
	private String actionType;
	private String contentType = "";

	final private CXGRequest cxgRequest;
	final private Context appContext;

	private CXGResponse cxgResponse = null;

	private ClientConnectionManager clientConnectionManager = null;

	public AsyncTaskExecutor(Context context, CXGRequest uchRequest, ICXGResponseCallback responseCallback) {
		responseCallBack = responseCallback;
		this.cxgRequest = uchRequest;
		appContext = context;

	}

	@Override
	protected void onPreExecute() {
		// avoid putting complex/more logic / block of code here since this
		// method runs on UI
		// thread..
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(Void... params) {

		if (null == cxgRequest || null == cxgRequest.getInputParams()) {
			responseCallBack.onCancel(appContext.getResources().getString(R.string.msg_null_or_empty));
			this.cancel(true);
		}

		HashMap<String, String> inputParams = cxgRequest.getInputParams();

		apiKey = inputParams.get(CXGRequest.API_KEY);
		contentType = inputParams.get(CXGRequest.CONTENT_TYPE);

		forcastApiUrl = cxgRequest.getUrl();

		if (null == forcastApiUrl || forcastApiUrl.equalsIgnoreCase("")) {
			forcastApiUrl = inputParams.get(CXGRequest.URL);
		}

		// to add api_key, latitude,longitude to the url.
		formatRequest();

		actionType = cxgRequest.getRequest_type();

		if (null == actionType || null == forcastApiUrl) {
			responseCallBack.onCancel(appContext.getResources().getString(R.string.msg_null_or_empty));
			this.cancel(true);
		}

		if (null != cxgRequest.getJsonStrInputParam() && !cxgRequest.getJsonStrInputParam().isEmpty()
				&& null == contentType) {
			String err_msg = appContext.getResources().getString(R.string.msg_invld_rqst);
			Logger.warn(TAG, err_msg);
			responseCallBack.onCancel(err_msg);
		}

		if (cxgRequest.getRequest_id() != CXGCommunicationManager.LOGIN_REQUEST_ID && null == apiKey) {
			responseCallBack.onCancel(appContext.getResources().getString(R.string.msg_null_or_empty));
			this.cancel(true);
		}

		// preExecute() code ends..

		String responseStr = null;

		if (actionType == CXGRequest.GET) {
			responseStr = processGetRequest();
		} else if (actionType == CXGRequest.POST) {
			responseStr = processPostRequest();
		} else {
			String err_msg = appContext.getResources().getString(R.string.msg_invld_rqst_mt);
			Logger.warn(TAG, err_msg);
			responseCallBack.onCancel(err_msg);
		}

		if (null != cxgResponse && null != cxgResponse.getErrorMessage()) {
			Logger.warn(TAG, "Request not processed successfully due to " + cxgResponse.getErrorMessage());
		} else
			Logger.debug(TAG, " UCH Response: " + responseStr);

		return responseStr;
	}

	private void formatRequest() {

		if (cxgRequest.getInputParams().size() == 3) {
			final String API_KEY = cxgRequest.getInputParams().get(CXGRequest.API_KEY);
			final String latitude = cxgRequest.getInputParams().get(CXGRequest.LATITUDE);
			final String longitude = cxgRequest.getInputParams().get(CXGRequest.LONGITUDE);
			forcastApiUrl += API_KEY + "/" + latitude + "," + longitude;
			Logger.debug(AsyncTaskExecutor.class.getName(), forcastApiUrl);
		} else {
			Logger.warn(TAG, "Insuffcient Parameters passed !");
			responseCallBack.onCancel(
					"Missing any required Parameters (latitude,longitude,api_key) to pass to the forcast API !!!");
		}
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (null != cxgResponse && null != cxgRequest) {
			if (null != cxgResponse.getResponseAsJonStr())
				responseCallBack.onComplete(cxgResponse);
			else if (null != cxgResponse.getErrorMessage() || result == null) {
				Logger.warn(TAG, "Error result !!!");
				if (cxgResponse.getErrorMessage().startsWith("Socket")
						|| cxgResponse.getErrorMessage().contains("abort")) {
					Logger.warn(TAG, "Error result due to " + cxgResponse.getErrorMessage());
					responseCallBack.onCancel(cxgResponse.getErrorMessage());
				} else
					responseCallBack.onFail(cxgResponse.getErrorMessage(), cxgResponse.getStatusCode());
			}

		} else if (!Util.isNetworkOnline(appContext)) {
			Logger.warn(TAG, "onPostExecute: " + appContext.getString(R.string.lbl_network_unavailable));
			responseCallBack.onFail(appContext.getString(R.string.lbl_network_unavailable), -444);
		} else {
			responseCallBack.onCancel(appContext.getString(R.string.lbl_network_unavailable));
		}
	}

	public CXGResponse getResponse() {
		return cxgResponse;
	}

	// private methods implementations..
	private String processPostRequest() {

		StringBuffer responseBuilder = null;

		HttpPost httpPost = new HttpPost(forcastApiUrl);

		cxgResponse = null;

		HttpResponse httpResponse = null;
		try {

			if (null != cxgRequest.getJsonStrInputParam() && !cxgRequest.getJsonStrInputParam().isEmpty()
					&& null != contentType) {
				Logger.debug(TAG, "UCH Request contains jsonString in processing request: "
						+ cxgRequest.getJsonStrInputParam() + " with content-type: " + contentType);
				httpPost.addHeader("Content-Type", contentType);
				httpPost.setEntity(getJsonRequestEntity());
			} else {
				Logger.debug(TAG, "UrlEncodedFormEntity request preparing..");
				httpPost.setEntity(new UrlEncodedFormEntity(getRequestParameters(), HTTP.UTF_8));
			}
			// TODO set timeout for client connection..
			HttpParams httpParams = new BasicHttpParams();
			httpParams.setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.FALSE);
			HttpConnectionParams.setConnectionTimeout(httpParams, 15000);
			HttpConnectionParams.setSoTimeout(httpParams, 15000);
			HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);
			HttpConnectionParams.setTcpNoDelay(httpParams, true);

			HttpClient httpClient = new DefaultHttpClient(httpParams);
			httpClient.getConnectionManager().closeExpiredConnections();

			// HttpClient httpClient = new DefaultHttpClient();

			cxgResponse = new CXGResponse();

			httpResponse = httpClient.execute(httpPost);
			StatusLine statusLine = httpResponse.getStatusLine();
			int statusCode = statusLine.getStatusCode();

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			httpResponse.getEntity().writeTo(out);
			responseBuilder = new StringBuffer(out.toString());

			if (statusCode == CXGResponse.SUCCESS || statusCode == CXGResponse.POST_SUCCESS) {
				cxgResponse.setResponseAsJonStr(responseBuilder.toString());
				cxgResponse.setStatusCode(CXGResponse.SUCCESS);
			} else {
				if (null != statusLine.getReasonPhrase()) {
					if ("" != (CXGResponse.parseError(responseBuilder.toString()))) {
						cxgResponse.setErrorMessage(CXGResponse.parseError(responseBuilder.toString()));
						cxgResponse.setServerErrMessage(statusLine.getReasonPhrase());
						cxgResponse.setStatusCode(statusCode);
					} else {
						cxgResponse.setErrorMessage(statusLine.getReasonPhrase());
						cxgResponse.setStatusCode(statusCode);
					}
					Logger.error(ParseException.class.toString(), "Exception reasonPhrase: "
							+ statusLine.getReasonPhrase() + " and statusCode: " + statusCode);
				}
			}

		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
			// Logger.error(TAG, e.getMessage());
			cxgResponse.setErrorMessage(e.getMessage());
			cxgResponse.setStatusCode(504);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			// Logger.error(TAG, e.getMessage());
			cxgResponse.setErrorMessage(e.getMessage());
			cxgResponse.setStatusCode(406);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			// Logger.error(TAG, "" + e.getMessage());
			cxgResponse.setStatusCode(415);
			cxgResponse.setErrorMessage(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			// Logger.error(TAG, e.getMessage());
			cxgResponse.setErrorMessage(e.getMessage());
			cxgResponse.setStatusCode(403);
		} catch (Exception e) {
			e.printStackTrace();
			// Logger.error(TAG, e.getMessage());
			cxgResponse.setErrorMessage(e.getMessage());
			cxgResponse.setStatusCode(500);
		} finally {
			// TODO, make sure all I/O streams and communication channels close
			// or else close them here.
		}

		// return responseBuilder.toString();
		return responseBuilder == null ? null : responseBuilder.toString();

	}

	private String processGetRequest() {

		StringBuffer responseBuilder = new StringBuffer();
		Uri uri = Uri.parse(forcastApiUrl);

		if (uri.getQueryParameter("after_id") != null & uri.getQueryParameter("after_id") == "") {
		}

		HttpGet httpGet = new HttpGet(forcastApiUrl);

		try {
			final HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 50000);
			HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);
			HttpConnectionParams.setTcpNoDelay(httpParams, true);

			// HttpClient httpClient = new DefaultHttpClient(httpParams);
			HttpClient httpClient = CXGCommunicationManager.getInstance().getCXGHttpClient();
			httpClient.getConnectionManager().closeExpiredConnections();

			clientConnectionManager = httpClient.getConnectionManager();

			HttpResponse httpResponse;
			cxgResponse = new CXGResponse();

			httpResponse = httpClient.execute(httpGet);
			StatusLine statusLine = httpResponse.getStatusLine();
			int statusCode = statusLine.getStatusCode();

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			httpResponse.getEntity().writeTo(out);
			responseBuilder = new StringBuffer(out.toString());

			if (statusCode == CXGResponse.SUCCESS) {
				cxgResponse.setResponseAsJonStr(responseBuilder.toString());
				cxgResponse.setStatusCode(CXGResponse.SUCCESS);
			} else {
				if (null != statusLine.getReasonPhrase()) {

					cxgResponse.setErrorMessage(CXGResponse.parseError(responseBuilder.toString()));
					cxgResponse.setServerErrMessage(statusLine.getReasonPhrase());
					cxgResponse.setStatusCode(statusCode);
					Logger.error(ParseException.class.toString(), "Exception reasonPhrase: "
							+ statusLine.getReasonPhrase() + " and statusCode: " + statusCode);
				}
			}

		} catch (SocketException e) {
			Logger.warn(TAG, "Request processing cancelled due to " + e.getMessage());
			cxgResponse.setErrorMessage(e.getMessage());
			cxgResponse.setStatusCode(404);
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
			// Logger.error(TAG, e.getMessage());
			cxgResponse.setErrorMessage(e.getMessage());
			cxgResponse.setStatusCode(504);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			// Logger.error(TAG, e.getMessage());
			cxgResponse.setErrorMessage(e.getMessage());
			cxgResponse.setStatusCode(406);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			// Logger.error(TAG, e.getMessage());
			cxgResponse.setErrorMessage(e.getMessage());
			cxgResponse.setStatusCode(415);
		} catch (IOException e) {
			e.printStackTrace();
			// Logger.error(TAG, e.getMessage());
			cxgResponse.setErrorMessage(e.getMessage());
			cxgResponse.setStatusCode(403);
		} catch (Exception e) {
			e.printStackTrace();
			// Logger.error(TAG, e.getMessage());
			cxgResponse.setErrorMessage(e.getMessage());
			cxgResponse.setStatusCode(500);
		}

		// return responseBuilder.toString();

		return responseBuilder == null ? null : responseBuilder.toString();

	}

	private List<NameValuePair> getRequestParameters() {

		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		for (Map.Entry<String, String> entry : cxgRequest.getInputParams().entrySet()) {
			String key = (String) entry.getKey();
			String value = (String) cxgRequest.getInputParams().get(key);
			Logger.debug(TAG, "param-key: " + key + " & param-value: " + value);
			nameValuePairs.add(new BasicNameValuePair(key, value));
		}

		return nameValuePairs;
	}

	private StringEntity getJsonRequestEntity() {
		try {
			StringEntity requestEntity = new StringEntity(cxgRequest.getJsonStrInputParam());
			requestEntity.setContentType(contentType);
			return requestEntity;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void cancelRequest() {
		if (null != clientConnectionManager) {
			clientConnectionManager.closeIdleConnections(10, TimeUnit.MILLISECONDS);
			clientConnectionManager.shutdown();
		} else {
			Logger.warn(TAG, "request manager null !");
		}
	}

}
