/**
 * 
 */
package com.cxg.internal.network;

import java.security.KeyStore;
import java.util.Stack;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import com.cxg.api.R;
import com.cxg.internal.common.CXGApiException;
import com.cxg.internal.common.Util;

import android.content.Context;

/**
 * @author CXG Pvt Ltd., Singapore.
 *
 */

/**
 * This class is used to manage entire communication between front-end and
 * back-end server.
 */
public class CXGCommunicationManager implements ICXGCommunicationMananger {

	final public static String CXG_FORCAST_API_URL = "https://api.darksky.net/forecast/";

	public static String CXG_DEFAULT_APIKEY = "925820e5f7ad50fb5d1c3ad2c54081c0";

	final private String TAG = CXGCommunicationManager.class.getName();

	final public static int LOGIN_REQUEST_ID = 1;

	final private static Stack<RequestHanlder> requestList = new Stack<RequestHanlder>();

	final static private Object classLock = new Object();
	private static ICXGCommunicationMananger INSTANCE = new CXGCommunicationManager();

	private CXGCommunicationManager() {

	}

	/**
	 * To get CXG Communication Manager Instance to communicate with back-end
	 * server.
	 * 
	 * @return
	 */
	public static ICXGCommunicationMananger getInstance() throws CXGApiException {

		synchronized (classLock) {
			if (null == INSTANCE) {
				INSTANCE = new CXGCommunicationManager();
			}
			return INSTANCE;
		}
	}

	/**
	 * To send CXG Request to server..
	 */

	@Override
	public void sendAsyncRequest(final Context context, CXGRequest cxgRequest,
			final ICXGResponseCallback responseCallback) {

		if (!Util.isNetworkOnline(context)) {
			responseCallback.onFail(context.getResources().getString(R.string.lbl_network_unavailable), -444);
			return;
		}

		final AsyncTaskExecutor asycTaskExecutor = new AsyncTaskExecutor(context, cxgRequest,
				new ICXGResponseCallback() {

					@Override
					public void onProgress(String progressMsg) {
						responseCallback.onProgress(progressMsg);
					}

					@Override
					public void onFail(String errorMessage, int errorCode) {
						if (null == errorMessage)
							errorMessage = "Request Failed !";
						responseCallback.onFail(errorMessage, errorCode);
					}

					@Override
					public void onComplete(CXGResponse cxgResponse) {
						responseCallback.onComplete(cxgResponse);
					}

					@Override
					public void onCancel(String errorMessage) {
						responseCallback.onCancel(errorMessage);
					}
				});

		requestList.add((RequestHanlder) asycTaskExecutor);
		asycTaskExecutor.execute();
	}

	@Override
	public void sendSyncRequest(final Context context, CXGRequest uchRequest,
			final ICXGResponseCallback responseCallback) {

		if (!Util.isNetworkOnline(context)) {
			responseCallback.onFail(context.getResources().getString(R.string.lbl_network_unavailable), -444);
			return;
		}

		SyncTaskExecutor syncTaskExecutor = new SyncTaskExecutor(context, uchRequest, new ICXGResponseCallback() {

			@Override
			public void onProgress(String progressMsg) {
				responseCallback.onProgress(progressMsg);
			}

			@Override
			public void onFail(String errorMessage, int errorCode) {
				responseCallback.onFail(errorMessage, errorCode);
			}

			@Override
			public void onComplete(CXGResponse uchResponse) {
				responseCallback.onComplete(uchResponse);
			}

			@Override
			public void onCancel(String errorMessage) {
				responseCallback.onCancel(errorMessage);
			}

		});

		requestList.add((RequestHanlder) syncTaskExecutor);
		syncTaskExecutor.execute();

	}

	public interface RequestHanlder {
		void cancelRequest();
	}

	public static void storeAPIKey(String api_key) {
		CXG_DEFAULT_APIKEY = api_key;
	}

	public HttpClient getCXGHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			CXGSSLSocketFactory sf = new CXGSSLSocketFactory(trustStore);
			sf.setHostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

}
