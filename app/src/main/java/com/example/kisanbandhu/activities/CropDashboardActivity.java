package com.example.kisanbandhu.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kisanbandhu.R;
import com.example.kisanbandhu.data.CropData;
import com.example.kisanbandhu.models.Crop;
import com.example.kisanbandhu.models.GrowthStage;
import com.example.kisanbandhu.utils.CropStageHelper;
import com.example.kisanbandhu.utils.PrefsManager;
import com.example.kisanbandhu.utils.ReminderManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * PHASE 4 - CROP DASHBOARD.
 *
 * Hub for the selected crop: shows current growth stage (from the sowing
 * date) and links to Calendar, Fertilizer, Pesticide, Weather, Reminders
 * and Leaf Disease screens.
 */
public class CropDashboardActivity extends AppCompatActivity {

    private Crop crop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_dashboard);

        crop = CropData.getSelectedCrop(this);
        if (crop == null) {
            Toast.makeText(this, "Please select a crop first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, CropSelectionActivity.class));
            finish();
            return;
        }

        ((TextView) findViewById(R.id.tvDashCrop)).setText(crop.getEmoji() + "  " + crop.getName());
        ((TextView) findViewById(R.id.tvDashInfo)).setText(
                "Season: " + crop.getSuitableSeasonsText() + "  |  Water: " + crop.getWaterRequirement()
                        + "  |  Duration: " + crop.getGrowthDuration());

        Button btnSowing = findViewById(R.id.btnSetSowingDate);
        btnSowing.setOnClickListener(v -> showDatePicker());

        open(R.id.btnCalendar, CropCalendarActivity.class);
        open(R.id.btnFertilizer, FertilizerActivity.class);
        open(R.id.btnPesticide, PesticideActivity.class);
        open(R.id.btnWeather, WeatherActivity.class);
        open(R.id.btnReminders, RemindersActivity.class);
        open(R.id.btnLeafDisease, LeafDiseaseActivity.class);
        findViewById(R.id.btnChangeCrop).setOnClickListener(v ->
                startActivity(new Intent(this, CropSelectionActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (crop != null) refreshStageCard();
    }

    private void open(int buttonId, Class<?> target) {
        findViewById(buttonId).setOnClickListener(v -> startActivity(new Intent(this, target)));
    }

    private void refreshStageCard() {
        TextView tvStage = findViewById(R.id.tvDashStage);
        TextView tvActivity = findViewById(R.id.tvDashActivity);
        ProgressBar progress = findViewById(R.id.progressGrowth);
        Button btnSowing = findViewById(R.id.btnSetSowingDate);

        if (!CropStageHelper.hasSowingDate(this)) {
            tvStage.setText("Sowing date not set");
            tvActivity.setText("Set your sowing date to see the growth stage, fertilizer timing and reminders.");
            progress.setProgress(0);
            btnSowing.setText("Set Sowing Date");
            return;
        }

        String date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(new Date(CropStageHelper.getSowingDate(this)));
        btnSowing.setText("Sowing date: " + date + " (tap to change)");

        int days = CropStageHelper.getDaysSinceSowing(this);
        int total = CropStageHelper.getTotalDays(crop);
        List<GrowthStage> stages = CropStageHelper.getStages(crop);
        int index = CropStageHelper.getCurrentStageIndex(this, stages);

        if (days < 0) {
            tvStage.setText("Sowing starts in " + (-days) + " day(s)");
            tvActivity.setText("Prepare the field: ploughing, manure and seed treatment.");
            progress.setProgress(0);
        } else {
            GrowthStage stage = stages.get(index);
            tvStage.setText("Day " + days + " of ~" + total + "  •  " + stage.getName());
            tvActivity.setText("Now: " + stage.getActivity());
            progress.setProgress(Math.min(100, days * 100 / total));
        }
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        if (CropStageHelper.hasSowingDate(this)) {
            cal.setTimeInMillis(CropStageHelper.getSowingDate(this));
        }
        new DatePickerDialog(this, (view, year, month, day) -> {
            Calendar picked = Calendar.getInstance();
            picked.clear();
            picked.set(year, month, day); // midnight local time
            PrefsManager.saveLong(this, PrefsManager.KEY_SOWING_DATE, picked.getTimeInMillis());
            ReminderManager.cancelAll(this); // old alarms were for the old date
            Toast.makeText(this, "Sowing date saved. Open Reminders to schedule alerts.",
                    Toast.LENGTH_SHORT).show();
            refreshStageCard();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }
}
