package com.cxg.ui;

import java.util.Date;
import java.util.List;

import com.cxg.api.R;
import com.cxg.internal.forecastdata.CXGForecast;
import com.cxg.internal.forecastdata.Datum;
import com.cxg.internal.forecastdata.Datum_;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

final public class CXGForecastListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private List<String> expandableListTitle;
	private CXGForecast expandableListDetail;

	public CXGForecastListAdapter(Context context, List<String> expandableListTitle, CXGForecast expandableListDetail) {
		this.context = context;
		this.expandableListTitle = expandableListTitle;
		this.expandableListDetail = expandableListDetail;
	}

	@Override
	public Object getChild(int listPosition, int expandedListPosition) {

		if (listPosition == 0) {
			return expandableListDetail.getHourly().getData();
		} else if (listPosition == 1) {
			return expandableListDetail.getDaily().getData();
		} else
			return null;
	}

	@Override
	public long getChildId(int listPosition, int expandedListPosition) {
		return expandedListPosition;
	}

	@Override
	public View getChildView(int listPosition, final int expandedListPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {

		String dataStr = "";

		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.list_item, null);
		}

		if (listPosition == 0) {
			@SuppressWarnings("unchecked")
			final List<Datum> dataList = (List<Datum>) getChild(listPosition, expandedListPosition);
			final Datum data = dataList.get(expandedListPosition);

			dataStr += "\nTime: " + (new Date(data.getTime().longValue() * 1000)) + ", summary: " + data.getSummary()
					+ ", temperature: " + data.getTemperature() + ", pressure:" + data.getPressure() + ", humidity: "
					+ data.getHumidity() + ", windSpeed" + data.getWindSpeed() + "\n";
		} else if (listPosition == 1) {
			@SuppressWarnings("unchecked")
			final List<Datum_> dataList = (List<Datum_>) getChild(listPosition, expandedListPosition);
			final Datum_ daily = dataList.get(expandedListPosition);

			dataStr = "\nTime" + new Date(daily.getTime().longValue() * 1000) + ", probility: "
					+ daily.getPrecipProbability() + ", precipType:" + daily.getPrecipType() + ", summary: "
					+ daily.getSummary() + ", sunriseTime: " + daily.getSunriseTime() + ", sunsetTime: "
					+ daily.getSunsetTime() + ", Moonphase: " + daily.getMoonPhase() + ", apprentTemperatureMin: "
					+ daily.getApparentTemperatureMin() + ", apprentTemperatureMax: "
					+ daily.getApparentTemperatureMax() + ", cloudCover: " + daily.getCloudCover() + ", DewPoint: "
					+ daily.getDewPoint() + ", Humidity: " + daily.getHumidity() + ", Ozone: " + daily.getOzone()
					+ ", PrecipIntensity: " + daily.getPrecipIntensity() + ", TemperatureMax: "
					+ daily.getTemperatureMax() + "\n";
		}

		TextView data = (TextView) convertView.findViewById(R.id.data);
		data.setText(dataStr);

		return convertView;
	}

	@Override
	public int getChildrenCount(int listPosition) {

		if (listPosition == 0) {
			Log.d("CELA", "Hourly  data clicked !");
			return expandableListDetail.getHourly().getData().size();
		} else if (listPosition == 1) {
			Log.d("CELA", "Daily  data clicked !");
			return expandableListDetail.getDaily().getData().size();
		} else
			return 1;
	}

	@Override
	public Object getGroup(int listPosition) {
		return this.expandableListTitle.get(listPosition);
	}

	@Override
	public int getGroupCount() {
		return this.expandableListTitle.size();
	}

	@Override
	public long getGroupId(int listPosition) {
		return listPosition;
	}

	@Override
	public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		String listTitle = (String) getGroup(listPosition);
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.list_group, null);
		}
		TextView listTitleTextView = (TextView) convertView.findViewById(R.id.listTitle);
		listTitleTextView.setTypeface(null, Typeface.BOLD);
		listTitleTextView.setText(listTitle);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int listPosition, int expandedListPosition) {
		return true;
	}
}