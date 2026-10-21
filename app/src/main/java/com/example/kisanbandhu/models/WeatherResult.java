package com.example.kisanbandhu.models;

import java.util.List;

public class WeatherResult {

    private final List<WeatherDay> forecast;
    private final double currentTemperature;
    private final double currentHumidity;
    private final double currentRain;
    private final double currentWind;
    private final int currentWeatherCode;
    private final String place;

    public WeatherResult(
            List<WeatherDay> forecast,
            double currentTemperature,
            double currentHumidity,
            double currentRain,
            double currentWind,
            int currentWeatherCode,
            String place
    ) {
        this.forecast = forecast;
        this.currentTemperature = currentTemperature;
        this.currentHumidity = currentHumidity;
        this.currentRain = currentRain;
        this.currentWind = currentWind;
        this.currentWeatherCode = currentWeatherCode;
        this.place = place;
    }

    public List<WeatherDay> getForecast() {
        return forecast;
    }

    public double getCurrentTemperature() {
        return currentTemperature;
    }

    public double getCurrentHumidity() {
        return currentHumidity;
    }

    public double getCurrentRain() {
        return currentRain;
    }

    public double getCurrentWind() {
        return currentWind;
    }

    public int getCurrentWeatherCode() {
        return currentWeatherCode;
    }

    public String getPlace() {
        return place;
    }
}