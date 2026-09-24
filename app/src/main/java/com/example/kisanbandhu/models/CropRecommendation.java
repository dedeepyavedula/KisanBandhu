// FILE: app/src/main/java/com/example/kisanbandhu/models/CropRecommendation.java
package com.example.kisanbandhu.models;

/**
 * Result of scoring one Crop against one farmer's Soil profile.
 * Produced by CropRecommendationEngine, consumed by CropAdapter.
 */
public class CropRecommendation {

    private Crop crop;
    private int score;              // 0-100
    private String suitabilityLabel; // "High" / "Medium" / "Low"

    public CropRecommendation(Crop crop, int score, String suitabilityLabel) {
        this.crop = crop;
        this.score = score;
        this.suitabilityLabel = suitabilityLabel;
    }

    public Crop getCrop() { return crop; }
    public int getScore() { return score; }
    public String getSuitabilityLabel() { return suitabilityLabel; }
}
