
package com.example.kisanbandhu.activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.kisanbandhu.R;
import com.example.kisanbandhu.utils.CropStageHelper;
import com.example.kisanbandhu.utils.FirestoreCropGuideManager;
import com.example.kisanbandhu.utils.PrefsManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CropCalendarActivity extends BaseActivity {

    private TextView tvMonth;
    private TextView tvSelectedDate;
    private TextView tvSelectedStage;
    private TextView tvSelectedActivity;
    private TextView tvNoSowingDate;

    private GridLayout calendarGrid;
    private LinearLayout legend;

    private Calendar displayedMonth;
    private Calendar selectedDate;

    private FirestoreCropGuideManager.CropGuide guide;
    private List<FirestoreCropGuideManager.StageData> stages =
            new ArrayList<>();

    private String cropName;
    private boolean guideLoaded = false;

    private final int[] stageColors = {
            Color.rgb(220, 236, 224),
            Color.rgb(216, 232, 250),
            Color.rgb(249, 228, 199),
            Color.rgb(233, 221, 248),
            Color.rgb(247, 221, 226),
            Color.rgb(215, 238, 239)
    };

    private final int green = Color.rgb(40, 103, 53);
    private final int darkText = Color.rgb(41, 45, 41);
    private final int mutedText = Color.rgb(121, 85, 72);
    private final int dayMillis = 24 * 60 * 60 * 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_calendar);

        tvMonth = findViewById(R.id.tvCalendarMonth);
        tvSelectedDate = findViewById(R.id.tvCalendarSelectedDate);
        tvSelectedStage = findViewById(R.id.tvCalendarSelectedStage);
        tvSelectedActivity = findViewById(R.id.tvCalendarSelectedActivity);
        tvNoSowingDate = findViewById(R.id.tvCalendarNoSowingDate);

        calendarGrid = findViewById(R.id.cropCalendarGrid);
        legend = findViewById(R.id.cropCalendarLegend);

        displayedMonth = Calendar.getInstance();
        displayedMonth.set(Calendar.DAY_OF_MONTH, 1);

        selectedDate = Calendar.getInstance();

        findViewById(R.id.btnCalendarPreviousMonth)
                .setOnClickListener(v -> {
                    displayedMonth.add(Calendar.MONTH, -1);
                    renderCalendar();
                });

        findViewById(R.id.btnCalendarNextMonth)
                .setOnClickListener(v -> {
                    displayedMonth.add(Calendar.MONTH, 1);
                    renderCalendar();
                });

        cropName = PrefsManager.getString(
                this,
                PrefsManager.KEY_SELECTED_CROP,
                null
        );

        if (cropName == null || cropName.trim().isEmpty()) {
            tvSelectedDate.setText("No crop selected");
            tvSelectedStage.setText("Choose a crop to view its schedule.");
            tvSelectedActivity.setText("");
            Toast.makeText(
                    this,
                    "Please select a crop first",
                    Toast.LENGTH_SHORT
            ).show();
            renderCalendar();
            return;
        }

        tvSelectedDate.setText("Loading crop calendar...");
        tvSelectedStage.setText("");
        tvSelectedActivity.setText("");

        loadGuide();
        renderCalendar();
    }

    private void loadGuide() {
        new FirestoreCropGuideManager().getGuide(
                cropName,
                new FirestoreCropGuideManager.OnGuideLoadedListener() {
                    @Override
                    public void onSuccess(
                            FirestoreCropGuideManager.CropGuide result) {

                        if (isFinishing() || isDestroyed()) return;

                        guide = result;
                        stages = result.stages != null
                                ? result.stages
                                : new ArrayList<>();

                        guideLoaded = true;

                        buildLegend();
                        renderCalendar();
                        showSelectedDateDetails();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        if (isFinishing() || isDestroyed()) return;

                        guideLoaded = false;
                        tvSelectedStage.setText(
                                "Growth stages could not be loaded."
                        );
                        tvSelectedActivity.setText(
                                "Check your connection and try again."
                        );

                        Toast.makeText(
                                CropCalendarActivity.this,
                                "Could not load crop guide",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void renderCalendar() {
        if (calendarGrid == null) return;

        calendarGrid.removeAllViews();

        tvMonth.setText(
                new SimpleDateFormat(
                        "MMMM yyyy",
                        Locale.getDefault()
                ).format(displayedMonth.getTime())
        );

        boolean hasSowingDate = CropStageHelper.hasSowingDate(this);
        tvNoSowingDate.setVisibility(
                hasSowingDate ? View.GONE : View.VISIBLE
        );

        Calendar month = (Calendar) displayedMonth.clone();
        month.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOffset = month.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = month.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int cell = 0; cell < 42; cell++) {
            int day = cell - firstDayOffset + 1;

            TextView dayView = new TextView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();

            params.width = 0;
            params.height = dp(43);
            params.columnSpec = GridLayout.spec(cell % 7, 1f);
            params.rowSpec = GridLayout.spec(cell / 7);
            params.setMargins(dp(2), dp(2), dp(2), dp(2));

            dayView.setLayoutParams(params);
            dayView.setGravity(Gravity.CENTER);
            dayView.setTextSize(13);
            dayView.setTextColor(darkText);

            if (day < 1 || day > daysInMonth) {
                dayView.setText("");
                dayView.setClickable(false);
                calendarGrid.addView(dayView);
                continue;
            }

            dayView.setText(String.valueOf(day));

            Calendar cellDate = (Calendar) month.clone();
            cellDate.set(Calendar.DAY_OF_MONTH, day);

            int stageIndex = getStageIndexForDate(cellDate);
            int backgroundColor = Color.TRANSPARENT;

            if (stageIndex >= 0) {
                backgroundColor = stageColors[stageIndex % stageColors.length];
            }

            boolean isSelected = sameDay(cellDate, selectedDate);
            boolean isToday = sameDay(cellDate, Calendar.getInstance());

            GradientDrawable background = new GradientDrawable();
            background.setCornerRadius(dp(9));

            if (isSelected) {
                background.setColor(Color.WHITE);
                background.setStroke(dp(2), green);
                dayView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            } else if (isToday) {
                background.setColor(
                        backgroundColor == Color.TRANSPARENT
                                ? Color.WHITE
                                : backgroundColor
                );
                background.setStroke(dp(1), green);
            } else if (backgroundColor != Color.TRANSPARENT) {
                background.setColor(backgroundColor);
            } else {
                background.setColor(Color.TRANSPARENT);
            }

            dayView.setBackground(background);

            dayView.setOnClickListener(v -> {
                selectedDate = (Calendar) cellDate.clone();
                showSelectedDateDetails();
                renderCalendar();
            });

            calendarGrid.addView(dayView);
        }
    }

    /**
     * Maps a calendar date to the matching Firestore growth stage.
     * Stage startDay and endDay are measured from the saved sowing date.
     */
    private int getStageIndexForDate(Calendar date) {
        if (!guideLoaded
                || stages == null
                || stages.isEmpty()
                || !CropStageHelper.hasSowingDate(this)) {
            return -1;
        }

        long sowingMillis = CropStageHelper.getSowingDate(this);

        Calendar sowing = Calendar.getInstance();
        sowing.setTimeInMillis(sowingMillis);

        Calendar normalizedDate = (Calendar) date.clone();
        Calendar normalizedSowing = (Calendar) sowing.clone();

        normalizedDate.set(
                Calendar.HOUR_OF_DAY, 12
        );
        normalizedDate.set(Calendar.MINUTE, 0);
        normalizedDate.set(Calendar.SECOND, 0);
        normalizedDate.set(Calendar.MILLISECOND, 0);

        normalizedSowing.set(
                Calendar.HOUR_OF_DAY, 12
        );
        normalizedSowing.set(Calendar.MINUTE, 0);
        normalizedSowing.set(Calendar.SECOND, 0);
        normalizedSowing.set(Calendar.MILLISECOND, 0);

        long daysSinceSowing =
                (normalizedDate.getTimeInMillis()
                        - normalizedSowing.getTimeInMillis())
                        / dayMillis;

        for (int i = 0; i < stages.size(); i++) {
            FirestoreCropGuideManager.StageData stage = stages.get(i);

            if (daysSinceSowing >= stage.getStartDay()
                    && daysSinceSowing <= stage.getEndDay()) {
                return i;
            }
        }

        return -1;
    }

    private void buildLegend() {
        legend.removeAllViews();

        if (!guideLoaded || stages.isEmpty()) return;

        for (int i = 0; i < stages.size(); i++) {
            FirestoreCropGuideManager.StageData stage = stages.get(i);

            LinearLayout row = new LinearLayout(this);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams rowParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            rowParams.topMargin = dp(5);
            row.setLayoutParams(rowParams);

            View swatch = new View(this);
            GradientDrawable swatchBackground = new GradientDrawable();
            swatchBackground.setColor(
                    stageColors[i % stageColors.length]
            );
            swatchBackground.setCornerRadius(dp(4));
            swatch.setBackground(swatchBackground);

            row.addView(
                    swatch,
                    new LinearLayout.LayoutParams(dp(13), dp(13))
            );

            TextView label = new TextView(this);
            label.setText(stage.getName());
            label.setTextColor(mutedText);
            label.setTextSize(11);
            label.setMaxLines(2);

            LinearLayout.LayoutParams labelParams =
                    new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f
                    );
            labelParams.leftMargin = dp(7);
            labelParams.rightMargin = dp(8);

            row.addView(label, labelParams);
            legend.addView(row);
        }
    }

    private void showSelectedDateDetails() {
        if (selectedDate == null) return;

        String fullDate = new SimpleDateFormat(
                "EEEE, dd MMMM yyyy",
                Locale.getDefault()
        ).format(selectedDate.getTime());

        tvSelectedDate.setText(fullDate);

        if (!CropStageHelper.hasSowingDate(this)) {
            tvSelectedStage.setText("Sowing date not set");
            tvSelectedActivity.setText(
                    "Set the sowing date on the crop cycle screen "
                            + "to calculate growth-stage dates."
            );
            return;
        }

        if (!guideLoaded) {
            tvSelectedStage.setText("Loading growth stages...");
            tvSelectedActivity.setText("");
            return;
        }

        int index = getStageIndexForDate(selectedDate);

        if (index < 0) {
            tvSelectedStage.setText("No crop milestone scheduled for this date.");
            tvSelectedActivity.setText(
                    "Choose a highlighted date to view its growth-stage guidance."
            );
            return;
        }

        FirestoreCropGuideManager.StageData stage = stages.get(index);

        tvSelectedStage.setText(stage.getName());
        tvSelectedActivity.setText(stage.getActivity());
    }

    private boolean sameDay(Calendar a, Calendar b) {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
                && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    }

    private int dp(float value) {
        return (int) (value
                * getResources().getDisplayMetrics().density + 0.5f);
    }
}
