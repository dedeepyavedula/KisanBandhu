package com.example.kisanbandhu.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.kisanbandhu.R;
import com.example.kisanbandhu.utils.CropStageHelper;
import com.example.kisanbandhu.utils.FirestoreCropGuideManager;
import com.example.kisanbandhu.utils.PrefsManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Displays the actual crop stages returned by FirestoreCropGuideManager. */
public class CropCycleActivity extends BaseActivity {

    private TextView tvCropCycleName;
    private TextView tvCropCycleSummary;
    private TextView tvCycleProgressText;
    private ProgressBar progressCycle;
    private LinearLayout stageList;
    private Button btnCycleSowingDate;

    private FirestoreCropGuideManager.CropGuide loadedGuide;
    private String cropName;

    private final int green = Color.rgb(35, 98, 47);
    private final int paleGreen = Color.rgb(234, 241, 230);
    private final int textDark = Color.rgb(38, 48, 40);
    private final int muted = Color.rgb(111, 127, 112);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_cycle);

        tvCropCycleName = findViewById(R.id.tvCropCycleName);
        tvCropCycleSummary = findViewById(R.id.tvCropCycleSummary);
        tvCycleProgressText = findViewById(R.id.tvCycleProgressText);
        progressCycle = findViewById(R.id.progressCycle);
        stageList = findViewById(R.id.stageList);
        btnCycleSowingDate = findViewById(R.id.btnCycleSowingDate);

        btnCycleSowingDate.setOnClickListener(v -> showDatePicker());

        // Open the existing Firestore-backed calendar with highlighted growth stages.
        findViewById(R.id.btnOpenCropCalendar).setOnClickListener(v ->
                startActivity(new Intent(
                        CropCycleActivity.this,
                        CropCalendarActivity.class
                ))
        );

        cropName = PrefsManager.getString(this, PrefsManager.KEY_SELECTED_CROP, null);
        if (cropName == null || cropName.trim().isEmpty()) {
            tvCropCycleName.setText("No crop selected");
            tvCropCycleSummary.setText("Choose a crop to view its growing stages.");
            tvCycleProgressText.setText("Select a crop to begin");
            btnCycleSowingDate.setEnabled(false);
            TextView empty = makeText("Select a crop first from My Crops.", 15, muted, false);
            empty.setPadding(0, dp(18), 0, dp(18));
            stageList.addView(empty);
            return;
        }

        tvCropCycleName.setText(cropName);
        loadGuide();
    }

    private void loadGuide() {
        tvCropCycleSummary.setText("Loading growth stages from your crop guide…");
        stageList.removeAllViews();

        new FirestoreCropGuideManager().getGuide(cropName,
                new FirestoreCropGuideManager.OnGuideLoadedListener() {
                    @Override
                    public void onSuccess(FirestoreCropGuideManager.CropGuide guide) {
                        if (isFinishing() || isDestroyed()) return;
                        loadedGuide = guide;
                        renderCycle();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        if (isFinishing() || isDestroyed()) return;
                        tvCropCycleSummary.setText("Crop stages could not be loaded.");
                        TextView error = makeText(
                                "Please check your connection and try again.", 14, muted, false);
                        stageList.addView(error);
                        Toast.makeText(CropCycleActivity.this,
                                "Could not load crop stages from Firestore",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void renderCycle() {
        stageList.removeAllViews();
        if (loadedGuide == null || loadedGuide.stages == null || loadedGuide.stages.isEmpty()) {
            tvCropCycleSummary.setText("No growth stages are available for this crop yet.");
            tvCycleProgressText.setText("Crop guide unavailable");
            progressCycle.setProgress(0);
            return;
        }

        List<FirestoreCropGuideManager.StageData> stages = loadedGuide.stages;
        int totalDays = Math.max(1, stages.get(stages.size() - 1).getEndDay());
        boolean hasSowing = CropStageHelper.hasSowingDate(this);
        int days = hasSowing ? CropStageHelper.getDaysSinceSowing(this) : -1;
        int currentIndex = hasSowing && days >= 0
                ? CropStageHelper.getCurrentStageIndex(this, stages) : -1;

        if (!hasSowing) {
            tvCropCycleSummary.setText("Set your sowing date to track germination, growth, flowering and harvest.");
            tvCycleProgressText.setText("Sowing date not set");
            progressCycle.setProgress(0);
            btnCycleSowingDate.setText("Set sowing date");
        } else {
            long sowingMillis = CropStageHelper.getSowingDate(this);
            String date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    .format(new Date(sowingMillis));
            btnCycleSowingDate.setText("Sowing date · " + date + "  Change");
            int progress = days < 0 ? 0 : Math.min(100, days * 100 / totalDays);
            progressCycle.setProgress(progress);
            if (days < 0) {
                tvCropCycleSummary.setText("Your crop cycle will begin on " + date + ".");
                tvCycleProgressText.setText("Sowing starts in " + (-days) + " day(s)");
            } else if (days > totalDays) {
                tvCropCycleSummary.setText("The expected growing duration has passed. Check the crop and harvest condition in the field.");
                tvCycleProgressText.setText("Day " + days + " of about " + totalDays + " days · Harvest may be due");
            } else {
                tvCropCycleSummary.setText("Follow each stage's guidance as your crop develops.");
                String stageName = currentIndex >= 0 && currentIndex < stages.size()
                        ? stages.get(currentIndex).getName() : "Growth in progress";
                tvCycleProgressText.setText("Day " + days + " of about " + totalDays + " days · " + stageName);
            }
        }

        for (int i = 0; i < stages.size(); i++) {
            FirestoreCropGuideManager.StageData stage = stages.get(i);
            boolean isCurrent = hasSowing && days >= 0 && i == currentIndex;
            boolean isCompleted = hasSowing && days >= 0 && i < currentIndex;
            addStageCard(i + 1, stage, isCurrent, isCompleted);
        }
    }

    private void addStageCard(int number,
                              FirestoreCropGuideManager.StageData stage,
                              boolean isCurrent,
                              boolean isCompleted) {
        androidx.cardview.widget.CardView card = new androidx.cardview.widget.CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = dp(12);
        card.setLayoutParams(cardParams);
        card.setRadius(dp(18));
        card.setCardElevation(dp(isCurrent ? 3 : 1));
        card.setCardBackgroundColor(isCurrent ? paleGreen : Color.WHITE);
        card.setUseCompatPadding(true);

        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.HORIZONTAL);
        body.setGravity(Gravity.TOP);
        body.setPadding(dp(15), dp(16), dp(15), dp(16));

        TextView numberBubble = makeText(String.valueOf(number), 15, green, true);
        numberBubble.setGravity(Gravity.CENTER);
        numberBubble.setBackground(round(paleGreen, 24));
        body.addView(numberBubble, new LinearLayout.LayoutParams(dp(38), dp(38)));

        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams detailParams = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        detailParams.leftMargin = dp(12);
        details.setLayoutParams(detailParams);

        TextView title = makeText(stage.getName(), 16, textDark, true);
        details.addView(title);

        String range = "Day " + Math.max(0, stage.getStartDay())
                + " – " + Math.max(stage.getStartDay(), stage.getEndDay());
        TextView dayRange = makeText(range, 12, muted, true);
        LinearLayout.LayoutParams rangeParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rangeParams.topMargin = dp(4);
        details.addView(dayRange, rangeParams);

        TextView activity = makeText(stage.getActivity(), 14, Color.rgb(76, 86, 77), false);
        LinearLayout.LayoutParams activityParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        activityParams.topMargin = dp(9);
        details.addView(activity, activityParams);
        body.addView(details);

        String status = isCurrent ? "CURRENT" : (isCompleted ? "DONE" : "UPCOMING");
        TextView badge = makeText(status, 9, isCurrent ? green : muted, true);
        badge.setGravity(Gravity.CENTER);
        badge.setPadding(dp(7), dp(5), dp(7), dp(5));
        badge.setBackground(round(isCurrent ? Color.rgb(214, 232, 207) : Color.rgb(242, 245, 240), 10));
        LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        badgeParams.leftMargin = dp(8);
        body.addView(badge, badgeParams);

        card.addView(body);
        stageList.addView(card);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        if (CropStageHelper.hasSowingDate(this)) {
            calendar.setTimeInMillis(CropStageHelper.getSowingDate(this));
        }
        new DatePickerDialog(this, (picker, year, month, day) -> {
            Calendar selected = Calendar.getInstance();
            selected.clear();
            selected.set(year, month, day, 12, 0, 0);
            PrefsManager.saveLong(this, PrefsManager.KEY_SOWING_DATE,
                    selected.getTimeInMillis());
            renderCycle();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private TextView makeText(String text, float sizeSp, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(text == null ? "" : text);
        view.setTextSize(sizeSp);
        view.setTextColor(color);
        if (bold) view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return view;
    }

    private GradientDrawable round(int color, int radiusDp) {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(dp(radiusDp));
        return bg;
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
