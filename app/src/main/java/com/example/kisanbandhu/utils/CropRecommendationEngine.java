// FILE: app/src/main/java/com/example/kisanbandhu/utils/CropRecommendationEngine.java
package com.example.kisanbandhu.utils;

import com.example.kisanbandhu.data.CropData;
import com.example.kisanbandhu.models.Crop;
import com.example.kisanbandhu.models.CropRecommendation;
import com.example.kisanbandhu.models.Soil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * SCREEN 4 LOGIC - CROP RECOMMENDATIONS.
 *
 * A RULE-BASED scoring engine (NOT machine learning). This is the class
 * to walk through in your viva to explain "how the app decides which
 * crops to recommend".
 *
 * SCORING BREAKDOWN (out of 100):
 *   - Soil type match:        40 points if the crop lists this soil as suitable, else 5
 *   - Season match:           30 points if the crop lists this season as suitable, else 0
 *   - Irrigation compatibility: up to 15 points based on whether the crop's
 *                              water need fits rain-fed vs irrigated conditions
 *   - Fertility compatibility: up to 15 points based on whether the crop
 *                              suits the field's fertility level
 *
 * Crops scoring below MINIMUM_SCORE_TO_SHOW are left out of the results
 * entirely, since a very low score means the crop is a poor match.
 */
public class CropRecommendationEngine {

    private static final int MINIMUM_SCORE_TO_SHOW = 30;

    public static List<CropRecommendation> getRecommendations(Soil soil) {
        List<Crop> allCrops = CropData.getAllCrops();
        List<CropRecommendation> results = new ArrayList<>();

        for (Crop crop : allCrops) {
            int score = calculateScore(crop, soil);
            if (score >= MINIMUM_SCORE_TO_SHOW) {
                results.add(new CropRecommendation(crop, score, scoreToLabel(score)));
            }
        }

        // Highest suitability score first.
        Collections.sort(results, new Comparator<CropRecommendation>() {
            @Override
            public int compare(CropRecommendation a, CropRecommendation b) {
                return Integer.compare(b.getScore(), a.getScore());
            }
        });

        return results;
    }

    private static int calculateScore(Crop crop, Soil soil) {
        int score = 0;

        // 1) Soil type match (40 points)
        if (crop.getSuitableSoils().contains(soil.getSoilType())) {
            score += 40;
        } else {
            score += 5; // small baseline so no crop scores absolute zero
        }

        // 2) Season match (30 points)
        if (crop.getSuitableSeasons().contains(soil.getSeason())) {
            score += 30;
        }

        // 3) Irrigation compatibility (up to 15 points)
        if (soil.isIrrigated()) {
            // Irrigated fields can support any water requirement.
            score += 15;
        } else {
            // Rain-fed fields favour crops marked rainfedSuitable, or
            // crops with Low/Moderate water needs.
            if (crop.isRainfedSuitable() || !crop.getWaterRequirement().equals("High")) {
                score += 15;
            }
            // High-water crops on rain-fed land get 0 bonus here - risky combination.
        }

        // 4) Fertility compatibility (up to 15 points)
        switch (soil.getFertility()) {
            case "Low":
                if (crop.isLowFertilityTolerant()) score += 15;
                break;
            case "High":
                if (crop.isNutrientDemanding()) score += 15;
                else score += 8; // still fine, just not maximizing the extra fertility
                break;
            case "Medium":
            default:
                score += 10; // medium fertility is a safe middle ground for most crops
                break;
        }

        // Score should never exceed 100.
        return Math.min(score, 100);
    }

    private static String scoreToLabel(int score) {
        if (score >= 70) return "High";
        if (score >= 45) return "Medium";
        return "Low";
    }
}
