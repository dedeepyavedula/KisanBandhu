package com.example.kisanbandhu.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.kisanbandhu.models.WeatherDay;
import com.example.kisanbandhu.models.WeatherResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WeatherService {

    public interface Callback {
        void onResult(WeatherResult result, boolean live, String place);
    }

    public static void fetchForecast(
            Context context,
            double latitude,
            double longitude,
            Callback callback
    ) {

        new Thread(() -> {

            WeatherResult result = null;

            try {
                result = download(latitude, longitude);
            } catch (Exception ignored) {
                // Network/API error
            }

            final WeatherResult finalResult = result;

            new Handler(Looper.getMainLooper()).post(() -> {

                if (finalResult != null) {

                    callback.onResult(
                            finalResult,
                            true,
                            "Current location"
                    );

                } else {

                    callback.onResult(
                            null,
                            false,
                            "Current location"
                    );
                }
            });

        }).start();
    }

    private static WeatherResult download(
            double latitude,
            double longitude
    ) throws Exception {

        String weatherUrl =
                "https://api.open-meteo.com/v1/forecast"
                        + "?latitude=" + latitude
                        + "&longitude=" + longitude

                        + "&current="
                        + "temperature_2m,"
                        + "relative_humidity_2m,"
                        + "precipitation,"
                        + "wind_speed_10m,"
                        + "weather_code"

                        + "&daily="
                        + "temperature_2m_max,"
                        + "temperature_2m_min,"
                        + "precipitation_sum,"
                        + "windspeed_10m_max,"
                        + "weather_code"

                        + "&hourly=relative_humidity_2m"

                        + "&timezone=auto"
                        + "&forecast_days=7";

        JSONObject root =
                new JSONObject(httpGet(weatherUrl));

        /*
         * CURRENT WEATHER
         */

        JSONObject current =
                root.getJSONObject("current");

        double currentTemperature =
                current.optDouble(
                        "temperature_2m",
                        0
                );

        double currentHumidity =
                current.optDouble(
                        "relative_humidity_2m",
                        0
                );

        double currentRain =
                current.optDouble(
                        "precipitation",
                        0
                );

        double currentWind =
                current.optDouble(
                        "wind_speed_10m",
                        0
                );

        int currentWeatherCode =
                current.optInt(
                        "weather_code",
                        0
                );

        /*
         * DAILY FORECAST
         */

        JSONObject daily =
                root.getJSONObject("daily");

        JSONArray dates =
                daily.getJSONArray("time");

        JSONArray maxTemps =
                daily.getJSONArray(
                        "temperature_2m_max"
                );

        JSONArray minTemps =
                daily.getJSONArray(
                        "temperature_2m_min"
                );

        JSONArray rain =
                daily.getJSONArray(
                        "precipitation_sum"
                );

        JSONArray wind =
                daily.getJSONArray(
                        "windspeed_10m_max"
                );

        JSONArray weatherCodes =
                daily.getJSONArray(
                        "weather_code"
                );

        /*
         * HOURLY HUMIDITY
         */

        JSONArray hourlyHumidity =
                root.getJSONObject("hourly")
                        .getJSONArray(
                                "relative_humidity_2m"
                        );

        List<WeatherDay> days =
                new ArrayList<>();

        for (int i = 0;
             i < dates.length();
             i++) {

            double humiditySum = 0;
            int humidityCount = 0;

            int startHour = i * 24;

            int endHour =
                    Math.min(
                            startHour + 24,
                            hourlyHumidity.length()
                    );

            for (int h = startHour;
                 h < endHour;
                 h++) {

                double value =
                        hourlyHumidity.optDouble(
                                h,
                                Double.NaN
                        );

                if (!Double.isNaN(value)) {
                    humiditySum += value;
                    humidityCount++;
                }
            }

            double averageHumidity =
                    humidityCount == 0
                            ? 0
                            : humiditySum / humidityCount;

            days.add(
                    new WeatherDay(
                            dates.getString(i),
                            minTemps.optDouble(i, 0),
                            maxTemps.optDouble(i, 0),
                            averageHumidity,
                            rain.optDouble(i, 0),
                            wind.optDouble(i, 0),
                            weatherCodes.optInt(i, 0)
                    )
            );
        }

        return new WeatherResult(
                days,
                currentTemperature,
                currentHumidity,
                currentRain,
                currentWind,
                currentWeatherCode,
                "Current location"
        );
    }

    private static String httpGet(
            String urlString
    ) throws Exception {

        HttpURLConnection connection =
                (HttpURLConnection)
                        new URL(urlString)
                                .openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        try {

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    connection.getInputStream()
                            )
                    );

            StringBuilder result =
                    new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            reader.close();

            return result.toString();

        } finally {
            connection.disconnect();
        }
    }
}