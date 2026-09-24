package com.example.kisanbandhu.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kisanbandhu.R;
import com.example.kisanbandhu.adapters.InfoAdapter;
import com.example.kisanbandhu.models.InfoItem;
import com.example.kisanbandhu.models.WeatherDay;
import com.example.kisanbandhu.utils.WeatherService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** PHASE 4 - WEATHER FORECAST (7 days). Live from Open-Meteo, sample data if offline. */
public class WeatherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_screen);

        ((TextView) findViewById(R.id.tvTitle)).setText("🌦 Weather Forecast");
        ((TextView) findViewById(R.id.tvSubtitle)).setText("Loading forecast...");

        Button btnAlerts = findViewById(R.id.btnAction);
        btnAlerts.setText("⚠ View Weather Alerts");
        btnAlerts.setVisibility(View.VISIBLE);
        btnAlerts.setOnClickListener(v -> startActivity(new Intent(this, WeatherAlertsActivity.class)));

        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        View progress = findViewById(R.id.progressBar);
        progress.setVisibility(View.VISIBLE);

        WeatherService.fetchForecast(this, (days, live, place) -> {
            if (isFinishing() || isDestroyed()) return;
            progress.setVisibility(View.GONE);
            ((TextView) findViewById(R.id.tvSubtitle)).setText(live
                    ? "7-day forecast for " + place
                    : "No internet or location not found - showing OFFLINE SAMPLE data");

            SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat out = new SimpleDateFormat("EEE, dd MMM", Locale.getDefault());
            List<InfoItem> items = new ArrayList<>();
            for (WeatherDay d : days) {
                String label = d.getDate();
                try {
                    label = out.format(in.parse(d.getDate()));
                } catch (Exception ignored) {
                    // keep the raw date text
                }
                items.add(new InfoItem(label, d.getCondition(), Color.parseColor("#1565C0"),
                        String.format(Locale.getDefault(),
                                "🌡 Temp: %.0f - %.0f °C     💧 Humidity: %.0f%%\n🌧 Rain: %.1f mm     💨 Wind: %.0f km/h",
                                d.getTempMin(), d.getTempMax(), d.getHumidity(), d.getRain(), d.getWind()),
                        false));
            }
            rv.setAdapter(new InfoAdapter(items));
        });
    }
}
