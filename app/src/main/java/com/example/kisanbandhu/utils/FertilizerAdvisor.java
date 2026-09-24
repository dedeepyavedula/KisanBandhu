package com.example.kisanbandhu.utils;

import com.example.kisanbandhu.models.Soil;

/** Rule-based tweaks to the standard fertilizer plan, based on the farmer's soil. */
public class FertilizerAdvisor {

    public static String getSoilAdjustment(Soil soil) {
        if (soil == null) {
            return "Fill in your soil profile to get soil-specific adjustments.";
        }
        StringBuilder sb = new StringBuilder();

        switch (soil.getFertility()) {
            case "Low":
                sb.append("• Low fertility: increase doses by about 10-15% and add more organic manure.\n");
                break;
            case "High":
                sb.append("• High fertility: reduce nitrogen doses by about 10-15% to avoid waste.\n");
                break;
            default:
                sb.append("• Medium fertility: follow the standard doses below.\n");
        }

        switch (soil.getSoilType()) {
            case "Sandy":
                sb.append("• Sandy soil drains fast: split doses into smaller, more frequent applications.\n");
                break;
            case "Clay":
                sb.append("• Clay soil holds water: avoid over-irrigation after applying urea.\n");
                break;
            case "Black soil":
                sb.append("• Black soil is rich in potash but often low in nitrogen and zinc.\n");
                break;
            case "Red soil":
                sb.append("• Red soil is low in nutrients and organic matter: add compost/FYM regularly.\n");
                break;
            default:
                break;
        }

        if (soil.hasPH()) {
            try {
                double ph = Double.parseDouble(soil.getSoilPH().trim());
                if (ph < 6.0) {
                    sb.append("• Acidic soil (pH ").append(ph).append("): apply agricultural lime.\n");
                } else if (ph > 8.0) {
                    sb.append("• Alkaline soil (pH ").append(ph).append("): apply gypsum and organic matter.\n");
                }
            } catch (NumberFormatException ignored) {
                // pH text was not a number - skip the pH tip
            }
        }

        if (!soil.isIrrigated()) {
            sb.append("• Rain-fed farm: apply fertilizer only when the soil is moist.\n");
        }
        return sb.toString().trim();
    }
}
