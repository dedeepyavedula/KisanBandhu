package com.example.kisanbandhu.utils;

import com.example.kisanbandhu.models.Crop;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FirestoreCropManager {

    private final FirebaseFirestore db;

    public FirestoreCropManager() {
        db = FirebaseFirestore.getInstance();
    }

    public void getAllCrops(OnCropsLoadedListener listener) {

        db.collection("crops")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    List<Crop> crops = new ArrayList<>();

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

                        Crop crop = createCropFromDocument(document);

                        if (crop != null) {
                            crops.add(crop);
                        }
                    }

                    listener.onSuccess(crops);
                })
                .addOnFailureListener(listener::onFailure);
    }

    public void getCrop(
            String cropName,
            OnCropLoadedListener listener) {

        String documentId = makeDocumentId(cropName);

        db.collection("crops")
                .document(documentId)
                .get()
                .addOnSuccessListener(document -> {

                    Crop crop = createCropFromDocument(document);

                    if (crop == null) {
                        listener.onFailure(
                                new Exception(
                                        "Crop not found: " + cropName
                                )
                        );
                        return;
                    }

                    listener.onSuccess(crop);
                })
                .addOnFailureListener(listener::onFailure);
    }

    private Crop createCropFromDocument(
            DocumentSnapshot document) {

        if (!document.exists()) {
            return null;
        }

        String name = document.getString("name");

        List<String> soils =
                (List<String>) document.get("suitableSoils");

        List<String> seasons =
                (List<String>) document.get("suitableSeasons");

        String water =
                document.getString("waterRequirement");

        String duration =
                document.getString("growthDuration");

        String description =
                document.getString("description");

        boolean rainfed =
                Boolean.TRUE.equals(
                        document.get("rainfedSuitable")
                );

        boolean lowFertility =
                Boolean.TRUE.equals(
                        document.get("lowFertilityTolerant")
                );

        boolean nutrientDemanding =
                Boolean.TRUE.equals(
                        document.get("nutrientDemanding")
                );

        if (name == null) {
            return null;
        }

        if (soils == null) {
            soils = new ArrayList<>();
        }

        if (seasons == null) {
            seasons = new ArrayList<>();
        }

        return new Crop(
                name,
                "",
                soils.toArray(new String[0]),
                seasons.toArray(new String[0]),
                water == null ? "" : water,
                duration == null ? "" : duration,
                description == null ? "" : description,
                rainfed,
                lowFertility,
                nutrientDemanding
        );
    }

    private String makeDocumentId(String cropName) {

        return cropName
                .toLowerCase()
                .replace(" ", "_")
                .replace("(", "")
                .replace(")", "");
    }

    public interface OnCropsLoadedListener {

        void onSuccess(List<Crop> crops);

        void onFailure(Exception e);
    }

    public interface OnCropLoadedListener {

        void onSuccess(Crop crop);

        void onFailure(Exception e);
    }
}