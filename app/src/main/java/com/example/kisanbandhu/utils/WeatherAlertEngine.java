package com.example.kisanbandhu.utils;

import com.example.kisanbandhu.models.Crop;
import com.example.kisanbandhu.models.WeatherAlert;
import com.example.kisanbandhu.models.WeatherDay;

import java.util.ArrayList;
import java.util.List;

/**
 * Rule-based weather alerts (no AI). Thresholds are simple constants so they
 * are easy to explain in a viva. The selected crop (may be null) is used only
 * to personalise the advice text.
 */
public class WeatherAlertEngine {

    private static final double HEAVY_RAIN_MM = 40;
    private static final double HIGH_HUMIDITY = 85;
    private static final double STRONG_WIND_KMH = 40;
    private static final double HEAT_C = 40;
    private static final double COLD_C = 5;
    private static final double SUDDEN_CHANGE_C = 7;

    public static List<WeatherAlert> generate(List<WeatherDay> days, Crop crop) {
        List<WeatherAlert> alerts = new ArrayList<>();
        String cropName = crop == null ? "your crop" : crop.getName();

        for (int i = 0; i < days.size(); i++) {
            WeatherDay d = days.get(i);
            String date = d.getDate();

            if (d.getRain() >= HEAVY_RAIN_MM) {
                alerts.add(new WeatherAlert("Heavy Rainfall",
                        String.format("%.0f mm rain expected. Ensure drainage for %s, and postpone spraying and fertilizer.",
                                d.getRain(), cropName), WeatherAlert.SEVERE, date));
            }
            if (d.getHumidity() >= HIGH_HUMIDITY) {
                alerts.add(new WeatherAlert("High Humidity",
                        String.format("Humidity around %.0f%%. Fungal disease risk for %s - inspect leaves and consider preventive spray.",
                                d.getHumidity(), cropName), WeatherAlert.WARNING, date));
            }
            if (d.getWind() >= STRONG_WIND_KMH) {
                alerts.add(new WeatherAlert("Strong Winds",
                        String.format("Winds up to %.0f km/h. Avoid spraying; support tall plants of %s.",
                                d.getWind(), cropName), WeatherAlert.WARNING, date));
            }
            // Heuristic: very strong wind + hot + dry = dust/sand storm risk
            if (d.getWind() >= STRONG_WIND_KMH && d.getRain() < 1 && d.getTempMax() >= 35) {
                alerts.add(new WeatherAlert("Sandstorm / Dust Storm Risk",
                        "Hot, dry and windy conditions. Protect seedlings and nursery beds, and irrigate lightly after the storm.",
                        WeatherAlert.SEVERE, date));
            }
            if (d.getTempMax() >= HEAT_C) {
                alerts.add(new WeatherAlert("Extreme Heat",
                        String.format("Maximum %.0f°C. Irrigate in early morning/evening and mulch to protect %s.",
                                d.getTempMax(), cropName), WeatherAlert.SEVERE, date));
            }
            if (d.getTempMin() <= COLD_C) {
                alerts.add(new WeatherAlert("Cold / Frost Risk",
                        String.format("Minimum %.0f°C. Light irrigation in the evening helps protect %s from frost.",
                                d.getTempMin(), cropName), WeatherAlert.SEVERE, date));
            }
            if (i > 0) {
                double change = Math.abs(d.getTempMax() - days.get(i - 1).getTempMax());
                if (change >= SUDDEN_CHANGE_C) {
                    alerts.add(new WeatherAlert("Sudden Temperature Change",
                            String.format("Maximum temperature shifts by %.0f°C compared to the previous day. Watch %s for stress.",
                                    change, cropName), WeatherAlert.INFO, date));
                }
            }
        }
        return alerts;
    }
}
