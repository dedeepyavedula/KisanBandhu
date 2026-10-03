package com.example.kisanbandhu.utils;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirestoreCropGuideManager {

    private final FirebaseFirestore db;

    public FirestoreCropGuideManager() {
        db = FirebaseFirestore.getInstance();
    }

    public void getGuide(
            String cropName,
            OnGuideLoadedListener listener) {

        String documentId = makeDocumentId(cropName);

        db.collection("cropGuides")
                .document(documentId)
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {
                        listener.onFailure(
                                new Exception(
                                        "No guide found for " + cropName
                                )
                        );
                        return;
                    }

                    CropGuide guide =
                            createGuideFromDocument(document);

                    listener.onSuccess(guide);
                })
                .addOnFailureListener(listener::onFailure);
    }

    private CropGuide createGuideFromDocument(
            DocumentSnapshot document) {

        CropGuide guide = new CropGuide();

        guide.cropName =
                document.getString("cropName");

        List<Map<String, Object>> stages =
                (List<Map<String, Object>>)
                        (List<?>) document.get("stages");

        if (stages != null) {

            for (Map<String, Object> stage : stages) {

                guide.stages.add(
                        new StageData(
                                String.valueOf(
                                        stage.get("name")
                                ),
                                String.valueOf(
                                        stage.get("activity")
                                ),
                                getInt(stage.get("startDay")),
                                getInt(stage.get("endDay"))
                        )
                );
            }
        }

        List<Map<String, Object>> fertilizers =
                (List<Map<String, Object>>)
                        (List<?>) document.get("fertilizers");

        if (fertilizers != null) {

            for (Map<String, Object> fertilizer :
                    fertilizers) {

                guide.fertilizers.add(
                        new FertilizerData(
                                String.valueOf(
                                        fertilizer.get("chemical")
                                ),
                                String.valueOf(
                                        fertilizer.get("organic")
                                )
                        )
                );
            }
        }

        List<Map<String, Object>> pests =
                (List<Map<String, Object>>)
                        (List<?>) document.get("pests");

        if (pests != null) {

            for (Map<String, Object> pest : pests) {

                guide.pests.add(
                        new PestData(
                                String.valueOf(
                                        pest.get("name")
                                ),
                                String.valueOf(
                                        pest.get("symptoms")
                                ),
                                String.valueOf(
                                        pest.get("chemicalControl")
                                ),
                                String.valueOf(
                                        pest.get("organicControl")
                                )
                        )
                );
            }
        }

        return guide;
    }

    private int getInt(Object value) {

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        try {
            return Integer.parseInt(
                    String.valueOf(value)
            );
        } catch (Exception e) {
            return 0;
        }
    }

    private String makeDocumentId(String cropName) {

        return cropName
                .toLowerCase()
                .replace(" ", "_")
                .replace("(", "")
                .replace(")", "");
    }

    public interface OnGuideLoadedListener {

        void onSuccess(CropGuide guide);

        void onFailure(Exception e);
    }

    public static class CropGuide {

        public String cropName;

        public List<StageData> stages =
                new ArrayList<>();

        public List<FertilizerData> fertilizers =
                new ArrayList<>();

        public List<PestData> pests =
                new ArrayList<>();
    }

    public static class StageData {

        private final String name;
        private final String activity;
        private final int startDay;
        private final int endDay;

        public StageData(
                String name,
                String activity,
                int startDay,
                int endDay) {

            this.name = name;
            this.activity = activity;
            this.startDay = startDay;
            this.endDay = endDay;
        }

        public String getName() {
            return name;
        }

        public String getActivity() {
            return activity;
        }

        public int getStartDay() {
            return startDay;
        }

        public int getEndDay() {
            return endDay;
        }
    }

    public static class FertilizerData {

        private final String chemical;
        private final String organic;

        public FertilizerData(
                String chemical,
                String organic) {

            this.chemical = chemical;
            this.organic = organic;
        }

        public String getChemical() {
            return chemical;
        }

        public String getOrganic() {
            return organic;
        }
    }

    public static class PestData {

        private final String name;
        private final String symptoms;
        private final String chemicalControl;
        private final String organicControl;

        public PestData(
                String name,
                String symptoms,
                String chemicalControl,
                String organicControl) {

            this.name = name;
            this.symptoms = symptoms;
            this.chemicalControl = chemicalControl;
            this.organicControl = organicControl;
        }

        public String getName() {
            return name;
        }

        public String getSymptoms() {
            return symptoms;
        }

        public String getChemicalControl() {
            return chemicalControl;
        }

        public String getOrganicControl() {
            return organicControl;
        }
    }
}