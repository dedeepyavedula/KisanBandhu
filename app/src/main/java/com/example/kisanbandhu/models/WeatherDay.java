package com.example.kisanbandhu.models;

/** Forecast for a single day. Units: Celsius, %, mm, km/h. */
public class WeatherDay {

    private final String date;      // yyyy-MM-dd
    private final double tempMax;
    private final double tempMin;
    private final double humidity;  // daily average
    private final double rain;
    private final double wind;      // daily max
    private final int weatherCode;  // WMO code (Open-Meteo)

    public WeatherDay(String date, double tempMax, double tempMin, double humidity,
                      double rain, double wind, int weatherCode) {
        this.date = date;
        this.tempMax = tempMax;
        this.tempMin = tempMin;
        this.humidity = humidity;
        this.rain = rain;
        this.wind = wind;
        this.weatherCode = weatherCode;
    }

    public String getDate() { return date; }
    public double getTempMax() { return tempMax; }
    public double getTempMin() { return tempMin; }
    public double getHumidity() { return humidity; }
    public double getRain() { return rain; }
    public double getWind() { return wind; }

    /** Human readable condition from the WMO weather code. */
    public String getCondition() {
        if (weatherCode == 0) return "Clear";
        if (weatherCode <= 3) return "Partly cloudy";
        if (weatherCode == 45 || weatherCode == 48) return "Fog";
        if (weatherCode >= 51 && weatherCode <= 67) return "Rain";
        if (weatherCode >= 71 && weatherCode <= 77) return "Snow";
        if (weatherCode >= 80 && weatherCode <= 82) return "Showers";
        if (weatherCode >= 95) return "Thunderstorm";
        return "Cloudy";
    }
}
