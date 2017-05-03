package com.cxg.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cxg.api.R;
import com.cxg.internal.forecastdata.CXGForecast;
import com.cxg.internal.forecastdata.Currently;
import com.cxg.internal.forecastdata.Flags;
import com.cxg.internal.storagemanager.CXGForecastStorageManager;
import com.cxg.internal.storagemanager.ICXGForecastStorageManager;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

final public class CXGForeCastDashBoard extends Activity {

	private ExpandableListView cxgExpandableListView;
	private ExpandableListAdapter cxgExpandableListAdapter;
	private List<String> forecastDataTitles;
	private CXGForecast cxgForecastDataBundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle("CXG Forecast Dashboard");
		setContentView(R.layout.list_main);
		cxgExpandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
		final ICXGForecastStorageManager storageManager = CXGForecastStorageManager
				.getInstance(getApplicationContext());

		cxgForecastDataBundle = storageManager.getSavedCXGForecastData();

		forecastDataTitles = new ArrayList<String>();

		forecastDataTitles.add("Hourly");
		forecastDataTitles.add("Daily");

		cxgExpandableListAdapter = new CXGForecastListAdapter(this, forecastDataTitles, cxgForecastDataBundle);
		cxgExpandableListView.setAdapter(cxgExpandableListAdapter);

		cxgExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition,
					long id) {
				Log.d("ListMainActivity", "groupPositoin: " + groupPosition + " & childPosition: " + childPosition);
				if (groupPosition == 0) {
					Toast.makeText(getApplicationContext(),
							forecastDataTitles.get(groupPosition) + " -> Hourly summary: "
									+ cxgForecastDataBundle.getHourly().getData().get(childPosition).getSummary()
									+ "\n Humidity: "
									+ cxgForecastDataBundle.getHourly().getData().get(childPosition).getHumidity()
									+ "\n Ozone: "
									+ cxgForecastDataBundle.getHourly().getData().get(childPosition).getOzone()
									+ "\n temperature: "
									+ cxgForecastDataBundle.getHourly().getData().get(childPosition).getTemperature(),
							Toast.LENGTH_SHORT).show();
				} else if (groupPosition == 1) {
					Toast.makeText(getApplicationContext(),
							forecastDataTitles.get(groupPosition) + " -> Daily summary: "
									+ cxgForecastDataBundle.getDaily().getData().get(childPosition).getSummary()
									+ " \n Humidity: "
									+ cxgForecastDataBundle.getDaily().getData().get(childPosition).getHumidity()
									+ " \n Ozone: "
									+ cxgForecastDataBundle.getDaily().getData().get(childPosition).getOzone()
									+ " \n PrecipType: "
									+ cxgForecastDataBundle.getDaily().getData().get(childPosition).getPrecipType()
									+ " \n temperatureMin: "
									+ cxgForecastDataBundle.getDaily().getData().get(childPosition).getTemperatureMin()
									+ " \n temperatureMax: "
									+ cxgForecastDataBundle.getDaily().getData().get(childPosition).getTemperatureMax(),
							Toast.LENGTH_LONG).show();
				}
				return false;
			}
		});

		loadForecastCurrentlyData(cxgForecastDataBundle.getCurrently());
		loadForecastFlags(cxgForecastDataBundle.getFlags());
	}

	final private void loadForecastCurrentlyData(final Currently currently) {
		try {
			final TextView currentlyTxt = (TextView) findViewById(R.id.currently);

			Log.i("CXGDashBoard", "-->Timezone: " + cxgForecastDataBundle.getTimezone());
			String currentlyStr = "Currently\n Timezone: " + cxgForecastDataBundle.getTimezone() + "\nTime:"
					+ new Date(currently.getTime().longValue() * 1000) + "\nSummary:" + currently.getSummary()
					+ "\nApparentTemperature:" + currently.getApparentTemperature() + "\n Humidity:"
					+ currently.getHumidity() + "\nTemperature:" + currently.getTemperature() + "\n WindSpeed:"
					+ currently.getWindSpeed() + "\n WindBearing:" + currently.getWindBearing() + "\nPressure: "
					+ currently.getPressure() + "\n PrecipType: " + currently.getPrecipType() + "\n CloudCover:"
					+ currently.getCloudCover();

			currentlyTxt.setText(currentlyStr);
			currentlyTxt.setMovementMethod(new ScrollingMovementMethod());

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	final private void loadForecastFlags(final Flags flags) {
		try {
			final TextView flagsTxt = (TextView) findViewById(R.id.flags);

			String flagStr = "Flags\n";

			final String sources = "\nSources:[";

			final String isd_stations = "\nISD-Stations:[";
			final String madis_stations = "\nMADIS-Stations:[";
			flagStr += sources;

			for (String source : flags.getSources()) {
				flagStr += source + ",";
			}
			flagStr += "]";

			flagStr += isd_stations;

			for (String isdSts : flags.getIsdStations()) {
				flagStr += isdSts + ",";
			}
			flagStr += "]";

			flagStr += madis_stations;
			for (String madisSt : flags.getMadisStations()) {
				flagStr += madisSt + ",";
			}
			flagStr += "]";

			flagsTxt.setText(flagStr);
			flagsTxt.setMovementMethod(new ScrollingMovementMethod());

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
