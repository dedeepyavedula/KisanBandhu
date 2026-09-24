package com.example.kisanbandhu.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.kisanbandhu.models.Soil;
import com.example.kisanbandhu.models.WeatherDay;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Fetches a 7-day forecast from Open-Meteo (free, no API key) for the
 * farmer's district. If there is no internet / district not found / any error,
 * it falls back to built-in SAMPLE data so the screens still work in a demo.
 * Network work runs on a background thread; the callback runs on the UI thread.
 */
public class WeatherService {

    public interface Callback {
        /** @param live true = real forecast, false = offline sample data */
        void onResult(List<WeatherDay> days, boolean live, String place);
    }

    public static void fetchForecast(Context context, Callback callback) {
        Soil soil = PrefsManager.getSoilProfile(context.getApplicationContext());
        final String district = soil == null ? "" : soil.getDistrict().trim();
        final String state = soil == null ? "" : soil.getState().trim();

        new Thread(() -> {
            List<WeatherDay> days = null;
            String place = district.isEmpty() ? state : district;
            try {
                if (!district.isEmpty()) days = download(district);
                if ((days == null || days.isEmpty()) && !state.isEmpty()) {
                    days = download(state);
                    place = state;
                }
            } catch (Exception ignored) {
                // fall through to sample data
            }
            final boolean live = days != null && !days.isEmpty();
            final List<WeatherDay> result = live ? days : sampleForecast();
            final String shownPlace = place.isEmpty() ? "your area" : place;
            new Handler(Looper.getMainLooper()).post(() -> callback.onResult(result, live, shownPlace));
        }).start();
    }

    private static List<WeatherDay> download(String placeName) throws Exception {
        String geoUrl = "https://geocoding-api.open-meteo.com/v1/search?count=1&name="
                + URLEncoder.encode(placeName, "UTF-8");
        JSONArray results = new JSONObject(httpGet(geoUrl)).optJSONArray("results");
        if (results == null || results.length() == 0) return null;
        JSONObject loc = results.getJSONObject(0);

        String url = "https://api.open-meteo.com/v1/forecast?latitude=" + loc.getDouble("latitude")
                + "&longitude=" + loc.getDouble("longitude")
                + "&daily=temperature_2m_max,temperature_2m_min,precipitation_sum,windspeed_10m_max,weathercode"
                + "&hourly=relativehumidity_2m&timezone=auto&forecast_days=7";
        JSONObject root = new JSONObject(httpGet(url));
        JSONObject daily = root.getJSONObject("daily");
        JSONArray hourlyHumidity = root.getJSONObject("hourly").getJSONArray("relativehumidity_2m");
        JSONArray dates = daily.getJSONArray("time");

        List<WeatherDay> days = new ArrayList<>();
        for (int i = 0; i < dates.length(); i++) {
            double sum = 0;
            int count = 0;
            for (int h = i * 24; h < (i + 1) * 24 && h < hourlyHumidity.length(); h++) {
                sum += hourlyHumidity.optDouble(h, 0);
                count++;
            }
            days.add(new WeatherDay(
                    dates.getString(i),
                    daily.getJSONArray("temperature_2m_max").optDouble(i, 0),
                    daily.getJSONArray("temperature_2m_min").optDouble(i, 0),
                    count == 0 ? 0 : sum / count,
                    daily.getJSONArray("precipitation_sum").optDouble(i, 0),
                    daily.getJSONArray("windspeed_10m_max").optDouble(i, 0),
                    daily.getJSONArray("weathercode").optInt(i, 0)));
        }
        return days;
    }

    private static String httpGet(String urlString) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(8000);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            return sb.toString();
        } finally {
            conn.disconnect();
        }
    }

    /** Offline demo data (includes a rainy, windy and hot day so alerts can be tested). */
    private static List<WeatherDay> sampleForecast() {
        double[][] v = { // tMax, tMin, humidity, rain, wind, code
                {32, 22, 65, 0, 12, 1}, {33, 23, 70, 2, 15, 2}, {30, 22, 88, 55, 25, 65},
                {29, 21, 90, 30, 45, 95}, {34, 22, 60, 0, 20, 1}, {42, 26, 40, 0, 44, 0},
                {33, 24, 62, 0, 14, 1}};
        List<WeatherDay> days = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        for (double[] d : v) {
            String date = String.format(Locale.US, "%04d-%02d-%02d", cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
            days.add(new WeatherDay(date, d[0], d[1], d[2], d[3], d[4], (int) d[5]));
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return days;
    }
}
