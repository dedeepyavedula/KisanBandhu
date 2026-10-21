package com.example.kisanbandhu.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kisanbandhu.R;
import com.example.kisanbandhu.adapters.InfoAdapter;
import com.example.kisanbandhu.models.InfoItem;
import com.example.kisanbandhu.models.WeatherDay;
import com.example.kisanbandhu.models.WeatherResult;
import com.example.kisanbandhu.utils.LocationHelper;
import com.example.kisanbandhu.utils.WeatherService;
import com.example.kisanbandhu.utils.LocationNameHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeatherActivity extends AppCompatActivity {

    private TextView tvWeatherLocation;
    private TextView tvCurrentCondition;
    private TextView tvCurrentTemperature;
    private TextView tvCurrentDetails;
    private ProgressBar progressWeather;
    private RecyclerView rvWeatherForecast;

    private LocationHelper locationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        tvWeatherLocation = findViewById(R.id.tvWeatherLocation);
        tvCurrentCondition = findViewById(R.id.tvCurrentCondition);
        tvCurrentTemperature = findViewById(R.id.tvCurrentTemperature);
        tvCurrentDetails = findViewById(R.id.tvCurrentDetails);
        progressWeather = findViewById(R.id.progressWeather);
        rvWeatherForecast = findViewById(R.id.rvWeatherForecast);

        rvWeatherForecast.setLayoutManager(
                new LinearLayoutManager(this)
        );

        Button btnWeatherAlerts =
                findViewById(R.id.btnWeatherAlerts);

        btnWeatherAlerts.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                this,
                                WeatherAlertsActivity.class
                        )
                )
        );

        locationHelper = new LocationHelper(
                this,
                new LocationHelper.Callback() {

                    @Override
                    public void onLocation(Location location) {

                        loadWeather(
                                location.getLatitude(),
                                location.getLongitude()
                        );

                        LocationNameHelper.getLocationName(
                                WeatherActivity.this,
                                location,
                                name -> tvWeatherLocation.setText("📍 " + name)
                        );
                    }

                    @Override
                    public void onPermissionDenied() {

                        showLocationError(
                                "Location permission is required " +
                                        "to show local weather."
                        );
                    }

                    @Override
                    public void onLocationError() {

                        showLocationError(
                                "Unable to get your current location."
                        );
                    }
                }
        );

        progressWeather.setVisibility(View.VISIBLE);
        tvWeatherLocation.setText(
                "Getting your location..."
        );

        locationHelper.getLocation();
    }

    private void loadWeather(
            double latitude,
            double longitude
    ) {

        tvWeatherLocation.setText(
                "Loading local weather..."
        );

        WeatherService.fetchForecast(
                this,
                latitude,
                longitude,
                (result, live, place) -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    progressWeather.setVisibility(
                            View.GONE
                    );

                    if (!live || result == null) {

                        showLocationError(
                                "Unable to load live weather data."
                        );

                        return;
                    }

                    tvCurrentCondition.setText(
                            getWeatherCondition(
                                    result.getCurrentWeatherCode()
                            )
                    );

                    tvCurrentTemperature.setText(
                            String.format(
                                    Locale.getDefault(),
                                    "%.0f°C",
                                    result.getCurrentTemperature()
                            )
                    );

                    tvCurrentDetails.setText(
                            String.format(
                                    Locale.getDefault(),
                                    "Humidity %.0f%%   •   Rain %.1f mm   •   Wind %.0f km/h",
                                    result.getCurrentHumidity(),
                                    result.getCurrentRain(),
                                    result.getCurrentWind()
                            )
                    );

                    showForecast(
                            result.getForecast()
                    );
                }
        );
    }

    private void showLocationError(
            String message
    ) {

        progressWeather.setVisibility(
                View.GONE
        );

        tvWeatherLocation.setText(
                "📍 Location unavailable"
        );

        tvCurrentCondition.setText(
                "Weather unavailable"
        );

        tvCurrentTemperature.setText(
                "--°C"
        );

        tvCurrentDetails.setText(
                message
        );

        rvWeatherForecast.setAdapter(
                new InfoAdapter(
                        new ArrayList<>()
                )
        );
    }

    private void showForecast(
            List<WeatherDay> days
    ) {

        SimpleDateFormat input =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.US
                );

        SimpleDateFormat output =
                new SimpleDateFormat(
                        "EEE, dd MMM",
                        Locale.getDefault()
                );

        List<InfoItem> items =
                new ArrayList<>();

        for (WeatherDay day : days) {

            String date = day.getDate();

            try {

                date = output.format(
                        input.parse(day.getDate())
                );

            } catch (Exception ignored) {
            }

            String details =
                    String.format(
                            Locale.getDefault(),
                            "🌡 %.0f - %.0f °C     💧 %.0f%%\n🌧 %.1f mm     💨 %.0f km/h",
                            day.getTempMin(),
                            day.getTempMax(),
                            day.getHumidity(),
                            day.getRain(),
                            day.getWind()
                    );

            items.add(
                    new InfoItem(
                            date,
                            day.getCondition(),
                            getResources().getColor(
                                    R.color.green_primary,
                                    getTheme()
                            ),
                            details,
                            false
                    )
            );
        }

        rvWeatherForecast.setAdapter(
                new InfoAdapter(items)
        );
    }

    private String getWeatherCondition(
            int weatherCode
    ) {

        switch (weatherCode) {

            case 0:
                return "☀ Clear";

            case 1:
            case 2:
            case 3:
                return "⛅ Cloudy";

            case 45:
            case 48:
                return "🌫 Foggy";

            case 51:
            case 53:
            case 55:
            case 61:
            case 63:
            case 65:
            case 80:
            case 81:
            case 82:
                return "🌧 Rain";

            case 71:
            case 73:
            case 75:
            case 77:
            case 85:
            case 86:
                return "❄ Snow";

            case 95:
            case 96:
            case 99:
                return "⛈ Thunderstorm";

            default:
                return "🌤 Unknown";
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (locationHelper != null) {

            locationHelper.onRequestPermissionsResult(
                    requestCode,
                    permissions,
                    grantResults
            );
        }
    }
}