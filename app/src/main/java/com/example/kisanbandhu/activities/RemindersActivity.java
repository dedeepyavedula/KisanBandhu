package com.example.kisanbandhu.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kisanbandhu.R;
import com.example.kisanbandhu.adapters.InfoAdapter;
import com.example.kisanbandhu.data.CropData;
import com.example.kisanbandhu.models.Crop;
import com.example.kisanbandhu.models.InfoItem;
import com.example.kisanbandhu.models.Reminder;
import com.example.kisanbandhu.utils.CropStageHelper;
import com.example.kisanbandhu.utils.ReminderManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** PHASE 4 - REMINDERS: activity reminders from the sowing date, delivered as notifications. */
public class RemindersActivity extends AppCompatActivity {

    private Crop crop;

    private final ActivityResultLauncher<String> notificationPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (!granted) {
                    Toast.makeText(this, "Notifications are off - reminders are scheduled but "
                            + "will not be shown until you allow them in Settings.", Toast.LENGTH_LONG).show();
                }
                schedule();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_screen);

        crop = CropData.getSelectedCrop(this);
        if (crop == null) {
            Toast.makeText(this, "Please select a crop first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ((TextView) findViewById(R.id.tvTitle)).setText("🔔 " + crop.getName() + " Reminders");

        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));

        if (!CropStageHelper.hasSowingDate(this)) {
            ((TextView) findViewById(R.id.tvSubtitle)).setText("Sowing date needed");
            TextView empty = findViewById(R.id.tvEmpty);
            empty.setText("Set your sowing date on the Crop Dashboard to get activity reminders.");
            empty.setVisibility(View.VISIBLE);
            return;
        }

        ((TextView) findViewById(R.id.tvSubtitle)).setText(
                "One reminder at 8:00 AM on the first day of each growth stage");

        SimpleDateFormat fmt = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());
        long now = System.currentTimeMillis();
        List<InfoItem> items = new ArrayList<>();
        for (Reminder r : ReminderManager.buildReminders(crop, CropStageHelper.getSowingDate(this))) {
            boolean past = r.getTimeMillis() <= now;
            items.add(new InfoItem(r.getTitle(), past ? "DONE / PAST" : "UPCOMING",
                    Color.parseColor(past ? "#757575" : "#2E7D32"),
                    fmt.format(new Date(r.getTimeMillis())) + "\n" + r.getMessage(), false));
        }
        rv.setAdapter(new InfoAdapter(items));

        Button btn = findViewById(R.id.btnAction);
        btn.setText("Schedule Notifications");
        btn.setVisibility(View.VISIBLE);
        btn.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                schedule();
            }
        });
    }

    private void schedule() {
        int count = ReminderManager.scheduleAll(this, crop, CropStageHelper.getSowingDate(this));
        Toast.makeText(this, count + " upcoming reminder(s) scheduled", Toast.LENGTH_SHORT).show();
    }
}
