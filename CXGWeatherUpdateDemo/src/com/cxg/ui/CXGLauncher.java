package com.cxg.ui;

import com.cxg.api.R;
import com.cxg.internal.CXGWhetherUpdatesProvider;
import com.cxg.internal.network.CXGCommunicationManager;
import com.cxg.internal.network.CXGResponse;
import com.cxg.internal.network.ICXGResponseCallback;
import com.cxg.internal.storagemanager.CXGForecastStorageManager;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CXGLauncher extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cxglauncher);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		CXGWhetherUpdatesProvider.getInstance(getApplicationContext()).shutdownApi();
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements ICXGResponseCallback {

		private EditText api_key = null;

		private TextView weather_updates = null;

		private Button pingBtn = null;
		private Button showMe = null;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_cxglauncher, container, false);
			handleUI(rootView);
			return rootView;
		}

		private void handleUI(View rootView) {
			try {
				api_key = (EditText) rootView.findViewById(R.id.cxg_api_key);

				weather_updates = (TextView) rootView.findViewById(R.id.weather_updates);

				api_key.setText(CXGCommunicationManager.CXG_DEFAULT_APIKEY.trim());

				pingBtn = (Button) rootView.findViewById(R.id.start);

				pingBtn.setEnabled(true);

				showMe = (Button) rootView.findViewById(R.id.showMe);

				if (null == CXGForecastStorageManager.getInstance(getActivity()).getSavedCXGForecastData()) {
					Log.i("CXGLauncher", "No CGX DATA AVAILABLE !!!");
					showMe.setEnabled(false);
				} else
					showMe.setEnabled(true);

				pingBtn.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						final String apiKeyTxt = api_key.getText().toString();

						if (apiKeyTxt == null || apiKeyTxt.isEmpty()) {
							Toast.makeText(getActivity(), getResources().getString(R.string.msg_invalid_api_key),
									Toast.LENGTH_LONG).show();
							api_key.requestFocus();
							return;
						}

						pingCXGForecastApi();
						pingBtn.setEnabled(false);
					}
				});

				showMe.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						final Intent forecastDashboard = new Intent(getActivity(), CXGForeCastDashBoard.class);
						forecastDashboard.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						startActivity(forecastDashboard);
					}
				});

			} catch (Exception e) {
				// TODO: handle exception
			}

			pingBtn.setEnabled(true);
		}

		@Override
		public void onResume() {
			super.onResume();
			pingBtn.setEnabled(true);
		}

		private void pingCXGForecastApi() {
			try {
				final String apiKeyTxt = api_key.getText().toString();

				CXGCommunicationManager.storeAPIKey(apiKeyTxt.trim());

				CXGWhetherUpdatesProvider.getInstance(getActivity()).pingForecastApi(this, apiKeyTxt);
				pingBtn.setEnabled(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void stopCXGWeatherApi() {
			try {
				CXGWhetherUpdatesProvider.getInstance(getActivity().getApplicationContext()).shutdownApi();

			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onCancel(String errorMessage) {
			pingBtn.setEnabled(true);
			Toast.makeText(getActivity(), "Response errorMessage : " + errorMessage + "", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onComplete(CXGResponse cxg_response) {
			Log.i("CXGLauncher", "CXGForecast Respose: " + cxg_response.getResponseAsJonStr());

			weather_updates.setText(cxg_response.getResponseAsJonStr());
			weather_updates.setMovementMethod(new ScrollingMovementMethod());
			pingBtn.setEnabled(true);

			if (!cxg_response.getResponseAsJonStr().isEmpty()) {
				CXGForecastStorageManager.getInstance(getActivity().getApplicationContext())
						.storeCXGForecastData(cxg_response.getResponseAsJonStr());
				showMe.setEnabled(true);
			}
		}

		@Override
		public void onFail(String errorMessage, int errorCode) {
			pingBtn.setEnabled(true);
			Toast.makeText(getActivity(), "Response Failed : " + errorCode + "", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onProgress(String progressMsg) {
			pingBtn.setEnabled(true);
			Toast.makeText(getActivity(), "Response onProgress : " + progressMsg + "", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onDestroyView() {
			super.onDestroyView();
			stopCXGWeatherApi();
		}
	}
}
