package com.example.kisanbandhu.activities;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kisanbandhu.R;
import com.example.kisanbandhu.adapters.InfoAdapter;
import com.example.kisanbandhu.data.CropData;
import com.example.kisanbandhu.models.Crop;
import com.example.kisanbandhu.models.InfoItem;
import com.example.kisanbandhu.models.WeatherAlert;
import com.example.kisanbandhu.models.WeatherResult;
import com.example.kisanbandhu.utils.LocationHelper;
import com.example.kisanbandhu.utils.WeatherAlertEngine;
import com.example.kisanbandhu.utils.WeatherService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeatherAlertsActivity extends AppCompatActivity {

    private View progress;
    private TextView tvSubtitle;
    private TextView tvEmpty;
    private RecyclerView recyclerView;

    private LocationHelper locationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_screen);

        Crop crop = CropData.getSelectedCrop(this);

        ((TextView) findViewById(R.id.tvTitle))
                .setText("⚠ Weather Alerts");

        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvEmpty = findViewById(R.id.tvEmpty);
        recyclerView = findViewById(R.id.recyclerView);
        progress = findViewById(R.id.progressBar);

        tvSubtitle.setText("Getting your location...");

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        progress.setVisibility(View.VISIBLE);

        locationHelper = new LocationHelper(
                this,
                new LocationHelper.Callback() {

                    @Override
                    public void onLocation(Location location) {

                        loadAlerts(
                                location.getLatitude(),
                                location.getLongitude(),
                                crop
                        );
                    }

                    @Override
                    public void onPermissionDenied() {

                        showError(
                                "Location permission is required " +
                                        "to show weather alerts."
                        );
                    }

                    @Override
                    public void onLocationError() {

                        showError(
                                "Unable to get your current location."
                        );
                    }
                }
        );

        locationHelper.getLocation();
    }

    private void loadAlerts(
            double latitude,
            double longitude,
            Crop crop
    ) {

        tvSubtitle.setText(
                "Checking local weather..."
        );

        WeatherService.fetchForecast(
                this,
                latitude,
                longitude,
                (result, live, place) -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    progress.setVisibility(View.GONE);

                    if (!live || result == null) {

                        showError(
                                "Unable to load live weather data."
                        );

                        return;
                    }

                    tvSubtitle.setText(
                            "Alerts for the next 7 days"
                    );

                    List<WeatherAlert> alerts =
                            WeatherAlertEngine.generate(
                                    result.getForecast(),
                                    crop
                            );

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

                    for (WeatherAlert alert : alerts) {

                        String date = alert.getDate();

                        try {
                            date = output.format(
                                    input.parse(alert.getDate())
                            );
                        } catch (Exception ignored) {
                        }

                        String level;
                        String color;

                        if (alert.getSeverity()
                                == WeatherAlert.SEVERE) {

                            level = "SEVERE";
                            color = "#C62828";

                        } else if (alert.getSeverity()
                                == WeatherAlert.WARNING) {

                            level = "WARNING";
                            color = "#EF6C00";

                        } else {

                            level = "INFO";
                            color = "#1565C0";
                        }

                        items.add(
                                new InfoItem(
                                        alert.getTitle()
                                                + " - "
                                                + date,
                                        level,
                                        Color.parseColor(color),
                                        alert.getMessage(),
                                        false
                                )
                        );
                    }

                    if (items.isEmpty()) {

                        tvEmpty.setText(
                                "✅ No weather alerts. " +
                                        "Conditions look normal " +
                                        "for the next 7 days."
                        );

                        tvEmpty.setVisibility(View.VISIBLE);

                    } else {

                        tvEmpty.setVisibility(View.GONE);
                    }

                    recyclerView.setAdapter(
                            new InfoAdapter(items)
                    );
                }
        );
    }

    private void showError(String message) {

        progress.setVisibility(View.GONE);

        tvSubtitle.setText(
                "Weather alerts unavailable"
        );

        tvEmpty.setText(
                "⚠ " + message
        );

        tvEmpty.setVisibility(View.VISIBLE);

        recyclerView.setAdapter(
                new InfoAdapter(
                        new ArrayList<>()
                )
        );
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