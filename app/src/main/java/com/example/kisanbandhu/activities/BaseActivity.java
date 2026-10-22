package com.example.kisanbandhu.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kisanbandhu.R;
import com.example.kisanbandhu.utils.PrefsManager;

/**
 * Base class for signed-in screens that should share the floating three-item navigation.
 * Change authenticated feature activities from extends AppCompatActivity to extends BaseActivity.
 * LoginActivity and RegisterActivity can remain AppCompatActivity so they do not show this bar.
 */
public class BaseActivity extends AppCompatActivity {

    private static final int NAV_HEIGHT_DP = 78;
    private static final int CONTENT_BOTTOM_GAP_DP = 82;
    private static final int NAV_GREEN = Color.rgb(40, 93, 54);
    private static final int NAV_WHITE = Color.rgb(102, 117, 104);
    private static final int NAV_MUTED = Color.rgb(102, 117, 104);

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        View content = LayoutInflater.from(this).inflate(layoutResID, null, false);
        installContentWithNavigation(content);
    }

    @Override
    public void setContentView(View view) {
        installContentWithNavigation(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        installContentWithNavigation(view);
    }

    private void installContentWithNavigation(View content) {
        FrameLayout container = new FrameLayout(this);
        container.setClipChildren(false);
        container.setClipToPadding(false);

        FrameLayout.LayoutParams contentParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        contentParams.bottomMargin = dp(CONTENT_BOTTOM_GAP_DP);
        container.addView(content, contentParams);

        View navigation = LayoutInflater.from(this)
                .inflate(R.layout.view_floating_bottom_nav, container, false);
        FrameLayout.LayoutParams navParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(NAV_HEIGHT_DP),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL
        );
        container.addView(navigation, navParams);

        // Calling the parent implementation avoids wrapping this container recursively.
        super.setContentView(container);
        configureNavigation(navigation);
    }

    private void configureNavigation(View navigation) {
        View cropsItem = navigation.findViewById(R.id.navItemCrops);
        View homeItem = navigation.findViewById(R.id.navItemHome);
        View profileItem = navigation.findViewById(R.id.navItemProfile);

        ImageView cropsIcon = navigation.findViewById(R.id.navIconCrops);
        ImageView homeIcon = navigation.findViewById(R.id.navIconHome);
        ImageView profileIcon = navigation.findViewById(R.id.navIconProfile);
        View homeCircle = navigation.findViewById(R.id.navHomeCircle);

        String name = getClass().getSimpleName();
        boolean isHome = "HomeActivity".equals(name);
        boolean isProfile = "ProfileActivity".equals(name);
        boolean isWeather = name.toLowerCase().contains("weather")
                || name.toLowerCase().contains("alert");
        boolean isCrop = !isHome && !isProfile && !isWeather;

        setItemState(cropsItem, cropsIcon, null, isCrop, false);
        setItemState(homeItem, homeIcon, null, isHome, true);
        setItemState(profileItem, profileIcon, null, isProfile, false);

        if (homeCircle != null) {
            homeCircle.setBackgroundResource(R.drawable.bg_nav_center);
            homeCircle.setElevation(dp(8));
        }

        cropsItem.setOnClickListener(v -> {
            String selectedCrop = PrefsManager.getString(
                    BaseActivity.this,
                    PrefsManager.KEY_SELECTED_CROP,
                    null
            );
            Class<?> destination = (selectedCrop == null || selectedCrop.trim().isEmpty())
                    ? CropSelectionActivity.class
                    : CropDashboardActivity.class;
            navigateTo(destination);
        });

        homeItem.setOnClickListener(v -> navigateTo(HomeActivity.class));
        profileItem.setOnClickListener(v -> navigateTo(ProfileActivity.class));
    }

    private void setItemState(View item, ImageView icon, TextView label,
                              boolean selected, boolean isCenter) {
        if (item == null) return;
        item.setSelected(selected);

        if (!isCenter) {
            item.setBackgroundResource(selected
                    ? R.drawable.bg_nav_item_selected
                    : android.R.color.transparent);
        }

        int color = selected ? NAV_GREEN : NAV_MUTED;
        if (icon != null && !isCenter) {
            icon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
        if (label != null) {
            label.setVisibility(View.GONE);
        }
    }

    private void navigateTo(Class<?> destination) {
        if (destination == null || destination.equals(getClass())) return;
        Intent intent = new Intent(this, destination);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
