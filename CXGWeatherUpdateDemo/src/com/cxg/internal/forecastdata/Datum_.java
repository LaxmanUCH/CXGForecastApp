package com.cxg.internal.forecastdata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum_ {

	@SerializedName("time")
	@Expose
	private Integer time;
	@SerializedName("summary")
	@Expose
	private String summary;
	@SerializedName("icon")
	@Expose
	private String icon;
	@SerializedName("sunriseTime")
	@Expose
	private Integer sunriseTime;
	@SerializedName("sunsetTime")
	@Expose
	private Integer sunsetTime;
	@SerializedName("moonPhase")
	@Expose
	private Double moonPhase;
	@SerializedName("precipIntensity")
	@Expose
	private Double precipIntensity;
	@SerializedName("precipIntensityMax")
	@Expose
	private Double precipIntensityMax;
	@SerializedName("precipIntensityMaxTime")
	@Expose
	private Integer precipIntensityMaxTime;
	@SerializedName("precipProbability")
	@Expose
	private Double precipProbability;
	@SerializedName("precipType")
	@Expose
	private String precipType;
	@SerializedName("temperatureMin")
	@Expose
	private Double temperatureMin;
	@SerializedName("temperatureMinTime")
	@Expose
	private Integer temperatureMinTime;
	@SerializedName("temperatureMax")
	@Expose
	private Double temperatureMax;
	@SerializedName("temperatureMaxTime")
	@Expose
	private Integer temperatureMaxTime;
	@SerializedName("apparentTemperatureMin")
	@Expose
	private Double apparentTemperatureMin;
	@SerializedName("apparentTemperatureMinTime")
	@Expose
	private Integer apparentTemperatureMinTime;
	@SerializedName("apparentTemperatureMax")
	@Expose
	private Double apparentTemperatureMax;
	@SerializedName("apparentTemperatureMaxTime")
	@Expose
	private Integer apparentTemperatureMaxTime;
	@SerializedName("dewPoint")
	@Expose
	private Double dewPoint;
	@SerializedName("humidity")
	@Expose
	private Double humidity;
	@SerializedName("windSpeed")
	@Expose
	private Double windSpeed;
	@SerializedName("windBearing")
	@Expose
	private Integer windBearing;
	@SerializedName("visibility")
	@Expose
	private Double visibility;
	@SerializedName("cloudCover")
	@Expose
	private Double cloudCover;
	@SerializedName("pressure")
	@Expose
	private Double pressure;
	@SerializedName("ozone")
	@Expose
	private Double ozone;

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Integer getSunriseTime() {
		return sunriseTime;
	}

	public void setSunriseTime(Integer sunriseTime) {
		this.sunriseTime = sunriseTime;
	}

	public Integer getSunsetTime() {
		return sunsetTime;
	}

	public void setSunsetTime(Integer sunsetTime) {
		this.sunsetTime = sunsetTime;
	}

	public Double getMoonPhase() {
		return moonPhase;
	}

	public void setMoonPhase(Double moonPhase) {
		this.moonPhase = moonPhase;
	}

	public Double getPrecipIntensity() {
		return precipIntensity;
	}

	public void setPrecipIntensity(Double precipIntensity) {
		this.precipIntensity = precipIntensity;
	}

	public Double getPrecipIntensityMax() {
		return precipIntensityMax;
	}

	public void setPrecipIntensityMax(Double precipIntensityMax) {
		this.precipIntensityMax = precipIntensityMax;
	}

	public Integer getPrecipIntensityMaxTime() {
		return precipIntensityMaxTime;
	}

	public void setPrecipIntensityMaxTime(Integer precipIntensityMaxTime) {
		this.precipIntensityMaxTime = precipIntensityMaxTime;
	}

	public Double getPrecipProbability() {
		return precipProbability;
	}

	public void setPrecipProbability(Double precipProbability) {
		this.precipProbability = precipProbability;
	}

	public String getPrecipType() {
		return precipType;
	}

	public void setPrecipType(String precipType) {
		this.precipType = precipType;
	}

	public Double getTemperatureMin() {
		return temperatureMin;
	}

	public void setTemperatureMin(Double temperatureMin) {
		this.temperatureMin = temperatureMin;
	}

	public Integer getTemperatureMinTime() {
		return temperatureMinTime;
	}

	public void setTemperatureMinTime(Integer temperatureMinTime) {
		this.temperatureMinTime = temperatureMinTime;
	}

	public Double getTemperatureMax() {
		return temperatureMax;
	}

	public void setTemperatureMax(Double temperatureMax) {
		this.temperatureMax = temperatureMax;
	}

	public Integer getTemperatureMaxTime() {
		return temperatureMaxTime;
	}

	public void setTemperatureMaxTime(Integer temperatureMaxTime) {
		this.temperatureMaxTime = temperatureMaxTime;
	}

	public Double getApparentTemperatureMin() {
		return apparentTemperatureMin;
	}

	public void setApparentTemperatureMin(Double apparentTemperatureMin) {
		this.apparentTemperatureMin = apparentTemperatureMin;
	}

	public Integer getApparentTemperatureMinTime() {
		return apparentTemperatureMinTime;
	}

	public void setApparentTemperatureMinTime(Integer apparentTemperatureMinTime) {
		this.apparentTemperatureMinTime = apparentTemperatureMinTime;
	}

	public Double getApparentTemperatureMax() {
		return apparentTemperatureMax;
	}

	public void setApparentTemperatureMax(Double apparentTemperatureMax) {
		this.apparentTemperatureMax = apparentTemperatureMax;
	}

	public Integer getApparentTemperatureMaxTime() {
		return apparentTemperatureMaxTime;
	}

	public void setApparentTemperatureMaxTime(Integer apparentTemperatureMaxTime) {
		this.apparentTemperatureMaxTime = apparentTemperatureMaxTime;
	}

	public Double getDewPoint() {
		return dewPoint;
	}

	public void setDewPoint(Double dewPoint) {
		this.dewPoint = dewPoint;
	}

	public Double getHumidity() {
		return humidity;
	}

	public void setHumidity(Double humidity) {
		this.humidity = humidity;
	}

	public Double getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(Double windSpeed) {
		this.windSpeed = windSpeed;
	}

	public Integer getWindBearing() {
		return windBearing;
	}

	public void setWindBearing(Integer windBearing) {
		this.windBearing = windBearing;
	}

	public Double getVisibility() {
		return visibility;
	}

	public void setVisibility(Double visibility) {
		this.visibility = visibility;
	}

	public Double getCloudCover() {
		return cloudCover;
	}

	public void setCloudCover(Double cloudCover) {
		this.cloudCover = cloudCover;
	}

	public Double getPressure() {
		return pressure;
	}

	public void setPressure(Double pressure) {
		this.pressure = pressure;
	}

	public Double getOzone() {
		return ozone;
	}

	public void setOzone(Double ozone) {
		this.ozone = ozone;
	}

}
