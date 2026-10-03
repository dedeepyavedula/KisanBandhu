// FILE: app/src/main/java/com/example/kisanbandhu/utils/CropRecommendationEngine.java

package com.example.kisanbandhu.utils;

import com.example.kisanbandhu.models.Crop;
import com.example.kisanbandhu.models.CropRecommendation;
import com.example.kisanbandhu.models.Soil;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * SCREEN 4 LOGIC - CROP RECOMMENDATIONS.
 *
 * A RULE-BASED scoring engine (NOT machine learning).
 *
 * Crops are loaded from Firebase Firestore.
 *
 * SCORING BREAKDOWN (out of 100):
 *   - Soil type match:        40 points if the crop lists this soil as suitable, else 5
 *   - Season match:           30 points if the crop lists this season as suitable, else 0
 *   - Irrigation compatibility: up to 15 points
 *   - Fertility compatibility: up to 15 points
 *
 * Crops scoring below MINIMUM_SCORE_TO_SHOW are left out.
 */
public class CropRecommendationEngine {

    private static final int MINIMUM_SCORE_TO_SHOW = 30;

    /**
     * Gets crop recommendations from Firestore.
     *
     * Firestore is asynchronous, so the results are returned
     * through the OnRecommendationsLoadedListener.
     */
    public static void getRecommendations(
            Soil soil,
            OnRecommendationsLoadedListener listener) {

        FirebaseFirestore db =
                FirebaseFirestore.getInstance();

        db.collection("crops")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    List<CropRecommendation> results =
                            new ArrayList<>();

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

                        Map<String, Object> data =
                                document.getData();

                        if (data == null) {
                            continue;
                        }

                        String name =
                                (String) data.get("name");

                        List<String> suitableSoils =
                                (List<String>) data.get("suitableSoils");

                        List<String> suitableSeasons =
                                (List<String>) data.get("suitableSeasons");

                        String waterRequirement =
                                (String) data.get("waterRequirement");

                        String growthDuration =
                                (String) data.get("growthDuration");

                        String description =
                                (String) data.get("description");

                        boolean rainfedSuitable =
                                Boolean.TRUE.equals(
                                        data.get("rainfedSuitable")
                                );

                        boolean lowFertilityTolerant =
                                Boolean.TRUE.equals(
                                        data.get("lowFertilityTolerant")
                                );

                        boolean nutrientDemanding =
                                Boolean.TRUE.equals(
                                        data.get("nutrientDemanding")
                                );

                        /*
                         * Crop.java still expects an emoji.
                         * Firestore does not store emoji, so we use
                         * an empty string here.
                         */
                        Crop crop = new Crop(
                                name,
                                "",
                                suitableSoils.toArray(new String[0]),
                                suitableSeasons.toArray(new String[0]),
                                waterRequirement,
                                growthDuration,
                                description,
                                rainfedSuitable,
                                lowFertilityTolerant,
                                nutrientDemanding
                        );

                        int score =
                                calculateScore(crop, soil);

                        if (score >= MINIMUM_SCORE_TO_SHOW) {

                            results.add(
                                    new CropRecommendation(
                                            crop,
                                            score,
                                            scoreToLabel(score)
                                    )
                            );
                        }
                    }

                    // Highest suitability score first.
                    Collections.sort(
                            results,
                            new Comparator<CropRecommendation>() {
                                @Override
                                public int compare(
                                        CropRecommendation a,
                                        CropRecommendation b) {

                                    return Integer.compare(
                                            b.getScore(),
                                            a.getScore()
                                    );
                                }
                            }
                    );

                    listener.onSuccess(results);
                })
                .addOnFailureListener(listener::onFailure);
    }

    /**
     * Calculates the crop suitability score.
     */
    private static int calculateScore(
            Crop crop,
            Soil soil) {

        int score = 0;

        // 1) Soil type match - 40 points
        if (crop.getSuitableSoils()
                .contains(soil.getSoilType())) {

            score += 40;

        } else {

            score += 5;
        }

        // 2) Season match - 30 points
        if (crop.getSuitableSeasons()
                .contains(soil.getSeason())) {

            score += 30;
        }

        // 3) Irrigation compatibility - 15 points
        if (soil.isIrrigated()) {

            // Irrigated fields can support any water requirement.
            score += 15;

        } else {

            // Rain-fed fields favour rainfed crops
            // or crops with Low/Moderate water needs.
            if (crop.isRainfedSuitable()
                    || !crop.getWaterRequirement().equals("High")) {

                score += 15;
            }
        }

        // 4) Fertility compatibility - 15 points
        switch (soil.getFertility()) {

            case "Low":

                if (crop.isLowFertilityTolerant()) {
                    score += 15;
                }

                break;

            case "High":

                if (crop.isNutrientDemanding()) {
                    score += 15;
                } else {
                    score += 8;
                }

                break;

            case "Medium":
            default:

                score += 10;

                break;
        }

        // Score should never exceed 100.
        return Math.min(score, 100);
    }

    /**
     * Converts numerical score into a suitability label.
     */
    private static String scoreToLabel(int score) {

        if (score >= 70) {
            return "High";

        } else if (score >= 45) {
            return "Medium";

        } else {
            return "Low";
        }
    }

    /**
     * Callback for Firestore recommendation results.
     */
    public interface OnRecommendationsLoadedListener {

        void onSuccess(
                List<CropRecommendation> recommendations
        );

        void onFailure(Exception e);
    }
}