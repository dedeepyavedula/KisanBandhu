package com.example.kisanbandhu.activities;

import android.graphics.Color;
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
import com.example.kisanbandhu.utils.WeatherAlertEngine;
import com.example.kisanbandhu.utils.WeatherService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** PHASE 4 - WEATHER ALERTS: rule-based warnings from the forecast, personalised to the selected crop. */
public class WeatherAlertsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_screen);

        Crop crop = CropData.getSelectedCrop(this); // may be null - alerts still work
        ((TextView) findViewById(R.id.tvTitle)).setText("⚠ Weather Alerts");
        ((TextView) findViewById(R.id.tvSubtitle)).setText("Checking the forecast...");

        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        View progress = findViewById(R.id.progressBar);
        progress.setVisibility(View.VISIBLE);

        WeatherService.fetchForecast(this, (days, live, place) -> {
            if (isFinishing() || isDestroyed()) return;
            progress.setVisibility(View.GONE);
            ((TextView) findViewById(R.id.tvSubtitle)).setText(live
                    ? "Alerts for the next 7 days in " + place
                    : "OFFLINE SAMPLE data (no internet / location not found)");

            SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat out = new SimpleDateFormat("EEE, dd MMM", Locale.getDefault());
            List<InfoItem> items = new ArrayList<>();
            for (WeatherAlert a : WeatherAlertEngine.generate(days, crop)) {
                String date = a.getDate();
                try {
                    date = out.format(in.parse(a.getDate()));
                } catch (Exception ignored) {
                    // keep the raw date text
                }
                String level;
                String color;
                if (a.getSeverity() == WeatherAlert.SEVERE) {
                    level = "SEVERE";
                    color = "#C62828";
                } else if (a.getSeverity() == WeatherAlert.WARNING) {
                    level = "WARNING";
                    color = "#EF6C00";
                } else {
                    level = "INFO";
                    color = "#1565C0";
                }
                items.add(new InfoItem(a.getTitle() + " - " + date, level, Color.parseColor(color),
                        a.getMessage(), false));
            }

            if (items.isEmpty()) {
                TextView empty = findViewById(R.id.tvEmpty);
                empty.setText("✅ No weather alerts. Conditions look normal for the next 7 days.");
                empty.setVisibility(View.VISIBLE);
            }
            rv.setAdapter(new InfoAdapter(items));
        });
    }
}
