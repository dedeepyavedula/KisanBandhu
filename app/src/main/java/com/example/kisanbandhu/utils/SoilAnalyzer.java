// FILE: app/src/main/java/com/example/kisanbandhu/utils/SoilAnalyzer.java
package com.example.kisanbandhu.utils;

import com.example.kisanbandhu.models.Soil;

/**
 * SCREEN 3 LOGIC - SOIL ANALYSIS.
 *
 * This is a deliberately simple RULE-BASED explanation generator -
 * NOT a machine learning model. Good talking point for your viva:
 * "The soil analysis uses if/else rules based on established
 * agricultural knowledge about each soil type, not AI."
 *
 * Each method returns a short plain-English sentence. SoilAnalysisActivity
 * combines these into the full analysis text shown to the farmer.
 */
public class SoilAnalyzer {

    /** Builds the full analysis paragraph for the given soil profile. */
    public static String generateAnalysis(Soil soil) {
        StringBuilder sb = new StringBuilder();
        sb.append(getSoilTypeExplanation(soil.getSoilType())).append("\n\n");
        sb.append(getFertilityExplanation(soil.getFertility()));

        if (soil.hasPH()) {
            sb.append("\n\n").append(getPHExplanation(soil.getSoilPH()));
        }

        sb.append("\n\n").append(getIrrigationExplanation(soil.isIrrigated()));
        return sb.toString();
    }

    private static String getSoilTypeExplanation(String soilType) {
        switch (soilType) {
            case "Sandy":
                return "Sandy soil drains quickly and warms up fast in spring, but it does not " +
                        "hold water or nutrients well, so crops may need more frequent irrigation.";
            case "Clay":
                return "Clay soil holds water and nutrients well but drains slowly and can " +
                        "become compacted, so good drainage management is important.";
            case "Loamy":
                return "Loamy soil generally has good water retention and drainage and is " +
                        "considered suitable for a wide range of agricultural crops.";
            case "Silt":
                return "Silt soil holds moisture well and is fairly fertile, but it can be prone " +
                        "to compaction and erosion if not managed carefully.";
            case "Black soil":
                return "Black soil (regur) retains moisture well and is rich in minerals, making " +
                        "it well suited to crops like cotton, soybean, and sorghum.";
            case "Red soil":
                return "Red soil is generally less fertile and lower in nitrogen and organic " +
                        "matter, so it often benefits from added fertilizers and organic matter.";
            case "Alluvial soil":
                return "Alluvial soil is typically very fertile due to river-deposited minerals " +
                        "and supports a wide variety of crops, especially cereals.";
            default:
                return "Soil type information will help determine which crops suit your field best.";
        }
    }

    private static String getFertilityExplanation(String fertility) {
        switch (fertility) {
            case "Low":
                return "With low fertility, consider adding organic matter (compost/manure) " +
                        "before sowing and choosing crops that tolerate lower nutrient levels.";
            case "Medium":
                return "With medium fertility, most common crops should grow reasonably well " +
                        "with standard fertilizer application.";
            case "High":
                return "With high fertility, your field can likely support nutrient-demanding " +
                        "crops, but avoid over-fertilizing.";
            default:
                return "Fertility level helps decide how much fertilizer support your crop will need.";
        }
    }

    private static String getPHExplanation(String phString) {
        double ph;
        try {
            ph = Double.parseDouble(phString);
        } catch (NumberFormatException e) {
            return "Soil pH: " + phString;
        }

        if (ph < 5.5) {
            return "Soil pH " + phString + " is acidic. Many crops prefer near-neutral soil, " +
                    "so liming may be considered based on local agricultural advice.";
        } else if (ph <= 7.5) {
            return "Soil pH " + phString + " is close to neutral, which suits most common crops well.";
        } else {
            return "Soil pH " + phString + " is alkaline. Some crops may show nutrient " +
                    "deficiencies in alkaline soil; local agricultural guidance is recommended.";
        }
    }

    private static String getIrrigationExplanation(boolean irrigated) {
        if (irrigated) {
            return "Since irrigation is available, you have more flexibility in crop choice " +
                    "and sowing timing.";
        } else {
            return "Since your field is rain-fed, crop choice and sowing timing should align " +
                    "closely with the expected monsoon/rainfall pattern.";
        }
    }
}
