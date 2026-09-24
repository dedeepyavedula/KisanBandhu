package com.example.kisanbandhu.utils;

import android.graphics.Bitmap;

/**
 * PLACEHOLDER for leaf disease detection - there is NO ML model yet.
 *
 * FUTURE TENSORFLOW LITE INTEGRATION:
 *  1. Add  implementation 'org.tensorflow:tensorflow-lite-task-vision:0.4.4'  to build.gradle
 *  2. Put the trained model (e.g. plant_disease.tflite) + labels in app/src/main/assets/
 *  3. Replace the body of classify() with an ImageClassifier run and return the top label.
 * LeafDiseaseActivity already calls classify(), so no UI change is needed.
 */
public class DiseaseClassifier {

    public static boolean isModelAvailable() {
        return false;
    }

    /** Returns a user-facing result string. Currently always the "coming soon" message. */
    public static String classify(Bitmap leafImage) {
        // TODO(TFLite): run the model on leafImage and return "<Disease> (<confidence>%)".
        return "Disease detection model is not installed yet.\n\n"
                + "Your photo is ready for analysis. Once the TensorFlow Lite model is added, "
                + "the result will appear here. For now, compare symptoms in the Pest / Disease "
                + "Guidance screen.";
    }
}
