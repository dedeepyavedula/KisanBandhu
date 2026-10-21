package com.example.kisanbandhu.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.kisanbandhu.R;
import com.example.kisanbandhu.models.WeatherResult;
import com.example.kisanbandhu.utils.LocationHelper;
import com.example.kisanbandhu.utils.LocationNameHelper;
import com.example.kisanbandhu.utils.PrefsManager;
import com.example.kisanbandhu.utils.WeatherService;
import com.example.kisanbandhu.utils.CropStageHelper;
import com.example.kisanbandhu.utils.FirestoreCropGuideManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    // ================= WEATHER =================

    private TextView tvHomeWeatherCondition;
    private TextView tvHomeWeatherTemperature;
    private TextView tvHomeWeatherLocation;
    private TextView tvHomeWeatherDetails;

    private LocationHelper locationHelper;

    // ================= CROP CALENDAR =================

    private LinearLayout homeCalendarGrid;
    private TextView homeCalendarMonth;
    private TextView tvCalendarSelectedDate;
    private TextView tvCalendarSelectedTask;

    private Calendar displayedMonth;
    private Calendar selectedCalendar;

    private final Map<String, List<String>> calendarEvents =
            new HashMap<>();

    private final Map<String, String> calendarStageDetails =
            new HashMap<>();

    // Every date in a growth stage gets the same colour, creating
    // a continuous-looking streak across the calendar.
    private final Map<String, Integer> calendarDateColors =
            new HashMap<>();

    private final Map<String, String> calendarDateStageDetails =
            new HashMap<>();

    private LinearLayout homeCalendarLegend;

    private static final int[] STAGE_COLORS = {
            Color.parseColor("#DCECE0"), // soft green
            Color.parseColor("#D8E8FA"), // soft blue
            Color.parseColor("#F9E4C7"), // soft amber
            Color.parseColor("#E9DDF8"), // soft purple
            Color.parseColor("#F7DDE2"), // soft pink
            Color.parseColor("#D7EEEF")  // soft teal
    };

    private boolean calendarGuideLoaded = false;

    // ================= COLORS =================

    private static final int GREEN =
            Color.parseColor("#2E7D32");

    private static final int LIGHT_GREEN =
            Color.parseColor("#E8F2DF");

    private static final int TEXT_DARK =
            Color.parseColor("#30372E");

    private static final int SOFT_TEXT =
            Color.parseColor("#795548");

    // ==================================================
    // ON CREATE
    // ==================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        initializeWeatherViews();
        initializeCalendarViews();
        initializeNavigation();
        initializeLocation();
    }

    // ==================================================
    // WEATHER VIEW INITIALIZATION
    // ==================================================

    private void initializeWeatherViews() {

        tvHomeWeatherCondition =
                findViewById(R.id.tvHomeWeatherCondition);

        tvHomeWeatherTemperature =
                findViewById(R.id.tvHomeWeatherTemperature);

        tvHomeWeatherLocation =
                findViewById(R.id.tvHomeWeatherLocation);

        tvHomeWeatherDetails =
                findViewById(R.id.tvHomeWeatherDetails);
    }

    // ==================================================
    // CALENDAR INITIALIZATION
    // ==================================================

    private void initializeCalendarViews() {

        homeCalendarGrid =
                findViewById(R.id.homeCalendarGrid);

        homeCalendarMonth =
                findViewById(R.id.homeCalendarMonth);

        tvCalendarSelectedDate =
                findViewById(R.id.tvCalendarSelectedDate);

        tvCalendarSelectedTask =
                findViewById(R.id.tvCalendarSelectedTask);

        homeCalendarLegend =
                findViewById(R.id.homeCalendarLegend);

        displayedMonth = Calendar.getInstance();

        displayedMonth.set(
                Calendar.DAY_OF_MONTH,
                1
        );

        selectedCalendar = Calendar.getInstance();

        findViewById(R.id.btnCalendarPrev)
                .setOnClickListener(v -> {

                    displayedMonth.add(
                            Calendar.MONTH,
                            -1
                    );

                    renderHomeCalendar();
                });

        findViewById(R.id.btnCalendarNext)
                .setOnClickListener(v -> {

                    displayedMonth.add(
                            Calendar.MONTH,
                            1
                    );

                    renderHomeCalendar();
                });

        renderHomeCalendar();
    }

    // ==================================================
    // NAVIGATION AND LOGOUT
    // ==================================================

    private void initializeNavigation() {

        Button btnLogout =
                findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {

            com.google.firebase.auth.FirebaseAuth
                    .getInstance()
                    .signOut();

            Intent intent = new Intent(
                    HomeActivity.this,
                    LoginActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        });

        CardView cardWeather =
                findViewById(R.id.cardWeather);

        cardWeather.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                this,
                                WeatherActivity.class
                        )
                )
        );

        CardView cardCropRecommend =
                findViewById(R.id.cardCropRecommend);

        CardView cardCalendar =
                findViewById(R.id.cardCalendar);

        CardView cardFertilizer =
                findViewById(R.id.cardFertilizer);

        CardView cardPesticide =
                findViewById(R.id.cardPesticide);

        CardView cardReminders =
                findViewById(R.id.cardReminders);

        CardView cardLeafDisease =
                findViewById(R.id.cardLeafDisease);

        cardCropRecommend.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                this,
                                SoilInputActivity.class
                        )
                )
        );

        // Open the complete crop timeline when the card
        // itself is clicked. Individual dates have their
        // own click listeners inside the calendar grid.

        cardCalendar.setOnClickListener(v ->
                openForCrop(
                        CropCalendarActivity.class
                )
        );

        cardFertilizer.setOnClickListener(v ->
                openForCrop(
                        FertilizerActivity.class
                )
        );

        cardPesticide.setOnClickListener(v ->
                openForCrop(
                        PesticideActivity.class
                )
        );

        cardReminders.setOnClickListener(v ->
                openForCrop(
                        RemindersActivity.class
                )
        );

        cardLeafDisease.setOnClickListener(v ->
                openForCrop(
                        LeafDiseaseActivity.class
                )
        );

        TextView tvSelectedCrop =
                findViewById(R.id.tvSelectedCrop);

        CardView cardSelectedCrop =
                findViewById(R.id.cardSelectedCrop);

        tvSelectedCrop.setOnClickListener(v ->
                openForCrop(
                        CropDashboardActivity.class
                )
        );

        cardSelectedCrop.setOnClickListener(v ->
                openForCrop(
                        CropDashboardActivity.class
                )
        );
    }

    // ==================================================
    // LOCATION
    // ==================================================

    private void initializeLocation() {

        locationHelper = new LocationHelper(
                this,
                new LocationHelper.Callback() {

                    @Override
                    public void onLocation(Location location) {

                        loadHomeWeather(
                                location.getLatitude(),
                                location.getLongitude()
                        );

                        LocationNameHelper.getLocationName(
                                HomeActivity.this,
                                location,
                                name -> {

                                    if (isFinishing()
                                            || isDestroyed()) {
                                        return;
                                    }

                                    tvHomeWeatherLocation.setText(
                                            "📍 " + name
                                    );
                                }
                        );
                    }

                    @Override
                    public void onPermissionDenied() {

                        showWeatherUnavailable(
                                "Location permission required"
                        );
                    }

                    @Override
                    public void onLocationError() {

                        showWeatherUnavailable(
                                "Location unavailable"
                        );
                    }
                }
        );
    }

    // ==================================================
    // REFRESH HOME SCREEN
    // ==================================================

    @Override
    protected void onResume() {
        super.onResume();

        updateSelectedCrop();

        // Refresh the crop guide and calendar whenever
        // the farmer returns to the home screen.

        loadHomeCalendarGuide();

        // Preserve the existing live weather refresh.

        if (locationHelper != null) {
            locationHelper.getLocation();
        }
    }

    private void updateSelectedCrop() {

        TextView tvSelectedCrop =
                findViewById(R.id.tvSelectedCrop);

        TextView tvCropCardText =
                findViewById(R.id.tvCropCardText);

        String selectedCrop =
                PrefsManager.getString(
                        this,
                        PrefsManager.KEY_SELECTED_CROP,
                        null
                );

        if (selectedCrop == null
                || selectedCrop.trim().isEmpty()) {

            tvSelectedCrop.setText(
                    "No crop selected yet"
            );

            tvCropCardText.setText(
                    "No crop selected"
            );

        } else {

            tvSelectedCrop.setText(
                    "Selected crop: " + selectedCrop
            );

            tvCropCardText.setText(
                    selectedCrop
            );
        }
    }

    // ==================================================
    // WEATHER DATA
    // ==================================================

    private void loadHomeWeather(
            double latitude,
            double longitude
    ) {

        tvHomeWeatherLocation.setText(
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

                    if (!live || result == null) {

                        showWeatherUnavailable(
                                "Weather unavailable"
                        );

                        return;
                    }

                    displayWeather(result);
                }
        );
    }

    private void displayWeather(
            WeatherResult result
    ) {

        String condition = getWeatherCondition(
                result.getCurrentWeatherCode()
        );

        tvHomeWeatherCondition.setText(
                condition
        );

        tvHomeWeatherTemperature.setText(
                String.format(
                        Locale.getDefault(),
                        "%.0f°C",
                        result.getCurrentTemperature()
                )
        );

        tvHomeWeatherDetails.setText(
                String.format(
                        Locale.getDefault(),
                        "💧 %.0f%%   •   🌧 %.1f mm   •   💨 %.0f km/h",
                        result.getCurrentHumidity(),
                        result.getCurrentRain(),
                        result.getCurrentWind()
                )
        );
    }

    private void showWeatherUnavailable(
            String message
    ) {

        if (tvHomeWeatherCondition == null) {
            return;
        }

        tvHomeWeatherCondition.setText(
                "Weather unavailable"
        );

        tvHomeWeatherTemperature.setText(
                "--°C"
        );

        tvHomeWeatherLocation.setText(
                message
        );

        tvHomeWeatherDetails.setText(
                "Tap here to try again"
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

    // ==================================================
    // LOAD CROP GUIDE FOR CALENDAR
    // ==================================================

    private void loadHomeCalendarGuide() {

        String cropName =
                PrefsManager.getString(
                        this,
                        PrefsManager.KEY_SELECTED_CROP,
                        null
                );

        calendarEvents.clear();
        calendarStageDetails.clear();
        calendarDateColors.clear();
        calendarDateStageDetails.clear();
        renderCalendarLegend(null);

        calendarGuideLoaded = false;

        if (cropName == null
                || cropName.trim().isEmpty()) {

            renderHomeCalendar();

            tvCalendarSelectedTask.setText(
                    "Select a crop to view its farming schedule."
            );

            return;
        }

        if (!CropStageHelper.hasSowingDate(this)) {

            renderHomeCalendar();

            tvCalendarSelectedTask.setText(
                    "Set your sowing date on the crop dashboard "
                            + "to display crop milestones."
            );

            return;
        }

        long sowingDate =
                CropStageHelper.getSowingDate(this);

        new FirestoreCropGuideManager().getGuide(
                cropName,
                new FirestoreCropGuideManager.OnGuideLoadedListener() {

                    @Override
                    public void onSuccess(
                            FirestoreCropGuideManager.CropGuide guide) {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        // Ignore a response if the selected crop
                        // changed while Firestore was loading.

                        String currentCrop =
                                PrefsManager.getString(
                                        HomeActivity.this,
                                        PrefsManager.KEY_SELECTED_CROP,
                                        null
                                );

                        if (currentCrop == null
                                || !currentCrop.equals(cropName)
                                || !CropStageHelper.hasSowingDate(
                                HomeActivity.this)
                                || CropStageHelper.getSowingDate(
                                HomeActivity.this) != sowingDate) {
                            return;
                        }

                        calendarEvents.clear();
                        calendarStageDetails.clear();
                        calendarDateColors.clear();
                        calendarDateStageDetails.clear();

                        if (guide == null
                                || guide.stages == null
                                || guide.stages.isEmpty()) {

                            renderHomeCalendar();

                            tvCalendarSelectedTask.setText(
                                    "No crop growth information is available."
                            );

                            return;
                        }

                        Calendar sowing =
                                getNormalizedCalendar(sowingDate);

                        addCalendarEvent(
                                sowing,
                                "Sowing",
                                "Sowing date for " + cropName + "."
                        );

                        renderCalendarLegend(guide.stages);

                        for (int stageIndex = 0;
                             stageIndex < guide.stages.size();
                             stageIndex++) {

                            FirestoreCropGuideManager.StageData stage =
                                    guide.stages.get(stageIndex);

                            int stageColor =
                                    STAGE_COLORS[stageIndex % STAGE_COLORS.length];

                            int startDay = Math.max(0, stage.getStartDay());
                            int endDay = Math.max(startDay, stage.getEndDay());

                            // Colour every date in this stage, not only
                            // the start/end milestone dates.
                            for (int dayOffset = startDay;
                                 dayOffset <= endDay;
                                 dayOffset++) {

                                Calendar stageDate =
                                        (Calendar) sowing.clone();

                                stageDate.add(
                                        Calendar.DAY_OF_YEAR,
                                        dayOffset
                                );

                                String stageDateKey =
                                        calendarDateKey(stageDate);

                                calendarDateColors.put(
                                        stageDateKey,
                                        stageColor
                                );

                                calendarDateStageDetails.put(
                                        stageDateKey,
                                        stage.getName() + "\\n"
                                                + stage.getActivity()
                                );
                            }

                            Calendar stageStart =
                                    (Calendar) sowing.clone();

                            stageStart.add(
                                    Calendar.DAY_OF_YEAR,
                                    stage.getStartDay()
                            );

                            addCalendarEvent(
                                    stageStart,
                                    stage.getName(),
                                    stage.getActivity()
                            );

                            Calendar stageEnd =
                                    (Calendar) sowing.clone();

                            stageEnd.add(
                                    Calendar.DAY_OF_YEAR,
                                    stage.getEndDay()
                            );

                            addCalendarEvent(
                                    stageEnd,
                                    "Stage transition: " + stage.getName(),
                                    stage.getActivity()
                            );
                        }

                        calendarGuideLoaded = true;

                        renderHomeCalendar();
                    }

                    @Override
                    public void onFailure(Exception e) {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        renderHomeCalendar();

                        tvCalendarSelectedTask.setText(
                                "Crop schedule could not be loaded. "
                                        + "Please try again."
                        );
                    }
                }
        );
    }

    private void renderCalendarLegend(
            List<FirestoreCropGuideManager.StageData> stages) {

        if (homeCalendarLegend == null) return;

        homeCalendarLegend.removeAllViews();

        if (stages == null || stages.isEmpty()) return;

        for (int i = 0; i < stages.size(); i++) {
            FirestoreCropGuideManager.StageData stage = stages.get(i);

            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.HORIZONTAL);
            item.setGravity(Gravity.CENTER_VERTICAL);

            LinearLayout.LayoutParams itemParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
            itemParams.rightMargin = dp(14);
            itemParams.bottomMargin = dp(6);
            item.setLayoutParams(itemParams);

            View swatch = new View(this);
            swatch.setBackground(roundedBackground(
                    STAGE_COLORS[i % STAGE_COLORS.length], 4));

            LinearLayout.LayoutParams swatchParams =
                    new LinearLayout.LayoutParams(dp(12), dp(12));
            swatchParams.rightMargin = dp(6);
            item.addView(swatch, swatchParams);

            TextView label = new TextView(this);
            label.setText(stage.getName());
            label.setTextColor(SOFT_TEXT);
            label.setTextSize(11);
            item.addView(label);

            homeCalendarLegend.addView(item);
        }
    }

    // ==================================================
    // CALENDAR DATE HELPERS
    // ==================================================

    private Calendar getNormalizedCalendar(long time) {

        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(time);

        calendar.set(
                Calendar.HOUR_OF_DAY,
                12
        );

        calendar.set(
                Calendar.MINUTE,
                0
        );

        calendar.set(
                Calendar.SECOND,
                0
        );

        calendar.set(
                Calendar.MILLISECOND,
                0
        );

        return calendar;
    }

    private String calendarDateKey(Calendar calendar) {

        return String.format(
                Locale.US,
                "%04d-%02d-%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
        );
    }

    private void addCalendarEvent(
            Calendar date,
            String title,
            String description
    ) {

        String key = calendarDateKey(date);

        List<String> events =
                calendarEvents.get(key);

        if (events == null) {

            events = new ArrayList<>();

            calendarEvents.put(
                    key,
                    events
            );
        }

        events.add(title);

        String detail =
                title + "\n" + description;

        String existing =
                calendarStageDetails.get(key);

        calendarStageDetails.put(
                key,
                existing == null
                        ? detail
                        : existing + "\n\n" + detail
        );
    }

    // ==================================================
    // CALENDAR DRAWING
    // ==================================================

    private int dp(float value) {

        return (int) (
                value * getResources()
                        .getDisplayMetrics()
                        .density + 0.5f
        );
    }

    private GradientDrawable roundedBackground(
            int color,
            int radiusDp
    ) {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(color);

        drawable.setCornerRadius(
                dp(radiusDp)
        );

        return drawable;
    }

    private void renderHomeCalendar() {

        if (homeCalendarGrid == null
                || homeCalendarMonth == null) {
            return;
        }

        homeCalendarMonth.setText(
                new SimpleDateFormat(
                        "MMMM yyyy",
                        Locale.getDefault()
                ).format(
                        displayedMonth.getTime()
                )
        );

        homeCalendarGrid.removeAllViews();

        Calendar month =
                (Calendar) displayedMonth.clone();

        month.set(
                Calendar.DAY_OF_MONTH,
                1
        );

        int firstDayOffset =
                month.get(Calendar.DAY_OF_WEEK)
                        - Calendar.SUNDAY;

        int daysInMonth =
                month.getActualMaximum(
                        Calendar.DAY_OF_MONTH
                );

        int totalCells =
                firstDayOffset + daysInMonth;

        int rows =
                (totalCells + 6) / 7;

        for (int row = 0; row < rows; row++) {

            LinearLayout week =
                    new LinearLayout(this);

            week.setOrientation(
                    LinearLayout.HORIZONTAL
            );

            week.setGravity(
                    Gravity.CENTER_VERTICAL
            );

            week.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            dp(42)
                    )
            );

            for (int col = 0; col < 7; col++) {

                final int day =
                        row * 7 + col - firstDayOffset + 1;

                LinearLayout cell =
                        new LinearLayout(this);

                cell.setOrientation(
                        LinearLayout.VERTICAL
                );

                cell.setGravity(
                        Gravity.CENTER
                );

                cell.setLayoutParams(
                        new LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                1f
                        )
                );

                if (day < 1 || day > daysInMonth) {

                    week.addView(cell);
                    continue;
                }

                Calendar date =
                        (Calendar) displayedMonth.clone();

                date.set(
                        Calendar.DAY_OF_MONTH,
                        day
                );

                String key =
                        calendarDateKey(date);

                boolean isSelected =
                        key.equals(
                                calendarDateKey(
                                        selectedCalendar
                                )
                        );

                boolean hasEvent =
                        calendarEvents.containsKey(key);

                Integer stageColor = calendarDateColors.get(key);

                // Fill the date cell with its growth-stage colour.
                // Dates belonging to the same stage share the same fill.
                TextView number = new TextView(this);
                number.setText(String.valueOf(day));
                number.setGravity(Gravity.CENTER);
                number.setTextSize(14);

                int backgroundColor = stageColor == null
                        ? Color.TRANSPARENT : stageColor;

                GradientDrawable dateBackground =
                        roundedBackground(backgroundColor, 8);

                if (isSelected) {
                    // Keep the stage colour visible while clearly marking
                    // the selected date with a dark-green outline.
                    dateBackground.setStroke(dp(2), GREEN);
                    number.setTextColor(TEXT_DARK);
                    number.setTypeface(null, Typeface.BOLD);
                } else {
                    number.setTextColor(TEXT_DARK);
                    number.setTypeface(null, Typeface.NORMAL);
                }

                number.setBackground(dateBackground);

                LinearLayout.LayoutParams numberParams =
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                dp(36)
                        );
                numberParams.setMargins(dp(1), dp(1), dp(1), dp(1));
                cell.addView(number, numberParams);

                cell.setContentDescription(
                        new SimpleDateFormat(
                                "d MMMM yyyy",
                                Locale.getDefault()
                        ).format(date.getTime())
                                + (stageColor != null
                                ? ", " + calendarDateStageDetails.get(key)
                                : hasEvent ? ", crop milestone" : "")
                );

                cell.setOnClickListener(v -> {

                    selectedCalendar =
                            (Calendar) date.clone();

                    updateSelectedCalendarDetails();

                    renderHomeCalendar();
                });

                week.addView(cell);
            }

            homeCalendarGrid.addView(week);
        }

        updateSelectedCalendarDetails();
    }

    // ==================================================
    // SELECTED DATE DETAILS
    // ==================================================

    private void updateSelectedCalendarDetails() {

        if (tvCalendarSelectedDate == null
                || tvCalendarSelectedTask == null
                || selectedCalendar == null) {
            return;
        }

        String formattedDate =
                new SimpleDateFormat(
                        "EEEE, d MMMM yyyy",
                        Locale.getDefault()
                ).format(
                        selectedCalendar.getTime()
                );

        tvCalendarSelectedDate.setText(
                formattedDate
        );

        String key =
                calendarDateKey(selectedCalendar);

        String details =
                calendarStageDetails.get(key);

        // Show milestone details first; otherwise show the stage
        // that spans the selected date.
        if (details != null) {
            tvCalendarSelectedTask.setText(details);
            return;
        }

        String activeStageDetails =
                calendarDateStageDetails.get(key);

        if (activeStageDetails != null) {
            tvCalendarSelectedTask.setText(activeStageDetails);
            return;
        }

        String cropName =
                PrefsManager.getString(
                        this,
                        PrefsManager.KEY_SELECTED_CROP,
                        null
                );

        if (cropName == null
                || cropName.trim().isEmpty()) {

            tvCalendarSelectedTask.setText(
                    "Select a crop to view its farming schedule."
            );

            return;
        }

        if (!CropStageHelper.hasSowingDate(this)) {

            tvCalendarSelectedTask.setText(
                    "Set your sowing date to view crop activities."
            );

            return;
        }

        if (!calendarGuideLoaded) {

            tvCalendarSelectedTask.setText(
                    "Loading crop schedule..."
            );

            return;
        }

        // For dates without a milestone, show the growth
        // stage that is active on that date.

        int selectedDay =
                getDaysBetween(
                        CropStageHelper.getSowingDate(this),
                        selectedCalendar.getTimeInMillis()
                );

        if (selectedDay < 0) {

            tvCalendarSelectedTask.setText(
                    "Sowing is scheduled in "
                            + (-selectedDay)
                            + " day(s)."
            );

            return;
        }

        tvCalendarSelectedTask.setText(
                "No crop milestone scheduled for this date."
        );
    }

    private int getDaysBetween(
            long startMillis,
            long endMillis
    ) {

        Calendar start =
                getNormalizedCalendar(startMillis);

        Calendar end =
                getNormalizedCalendar(endMillis);

        long difference =
                end.getTimeInMillis()
                        - start.getTimeInMillis();

        return (int) Math.floorDiv(
                difference,
                CropStageHelper.DAY_MS
        );
    }

    // ==================================================
    // OPEN FEATURES THAT REQUIRE A SELECTED CROP
    // ==================================================

    private void openForCrop(
            Class<?> target
    ) {

        String selectedCrop =
                PrefsManager.getString(
                        this,
                        PrefsManager.KEY_SELECTED_CROP,
                        null
                );

        if (selectedCrop == null
                || selectedCrop.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Please select a crop first",
                    Toast.LENGTH_SHORT
            ).show();

            startActivity(
                    new Intent(
                            this,
                            CropSelectionActivity.class
                    )
            );

        } else {

            startActivity(
                    new Intent(
                            this,
                            target
                    )
            );
        }
    }

    // ==================================================
    // LOCATION PERMISSIONS
    // ==================================================

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
