// FILE: app/src/main/java/com/example/kisanbandhu/models/Crop.java
package com.example.kisanbandhu.models;

import java.util.Arrays;
import java.util.List;

/**
 * Static information about one crop (Rice, Wheat, etc.). This is
 * reference data - it does not change per farmer. CropData.java holds
 * a fixed list of these, and CropRecommendationEngine scores each one
 * against the farmer's Soil profile.
 */
public class Crop {

    private String name;
    private String emoji;
    private List<String> suitableSoils;
    private List<String> suitableSeasons;
    private String waterRequirement;   // "Low" / "Moderate" / "High"
    private String growthDuration;     // display string, e.g. "120-150 days"
    private String description;

    // Extra flags used by the scoring rules in CropRecommendationEngine
    private boolean rainfedSuitable;      // can this crop do reasonably well without irrigation?
    private boolean lowFertilityTolerant; // does it still do okay in low-fertility soil?
    private boolean nutrientDemanding;    // does it especially benefit from high fertility?

    public Crop(String name, String emoji, String[] suitableSoils, String[] suitableSeasons,
                String waterRequirement, String growthDuration, String description,
                boolean rainfedSuitable, boolean lowFertilityTolerant, boolean nutrientDemanding) {
        this.name = name;
        this.emoji = emoji;
        this.suitableSoils = Arrays.asList(suitableSoils);
        this.suitableSeasons = Arrays.asList(suitableSeasons);
        this.waterRequirement = waterRequirement;
        this.growthDuration = growthDuration;
        this.description = description;
        this.rainfedSuitable = rainfedSuitable;
        this.lowFertilityTolerant = lowFertilityTolerant;
        this.nutrientDemanding = nutrientDemanding;
    }

    public String getName() { return name; }
    public String getEmoji() { return emoji; }
    public List<String> getSuitableSoils() { return suitableSoils; }
    public List<String> getSuitableSeasons() { return suitableSeasons; }
    public String getWaterRequirement() { return waterRequirement; }
    public String getGrowthDuration() { return growthDuration; }
    public String getDescription() { return description; }
    public boolean isRainfedSuitable() { return rainfedSuitable; }
    public boolean isLowFertilityTolerant() { return lowFertilityTolerant; }
    public boolean isNutrientDemanding() { return nutrientDemanding; }

    /** Comma-separated list of suitable soils, for display. */
    public String getSuitableSoilsText() {
        return String.join(" / ", suitableSoils);
    }

    /** Comma-separated list of suitable seasons, for display. */
    public String getSuitableSeasonsText() {
        return String.join(" / ", suitableSeasons);
    }
}
