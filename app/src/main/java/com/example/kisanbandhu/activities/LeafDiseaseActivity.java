package com.example.kisanbandhu.activities;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kisanbandhu.R;
import com.example.kisanbandhu.utils.DiseaseClassifier;

import java.io.InputStream;

/**
 * PHASE 4 - LEAF DISEASE DETECTION (PLACEHOLDER).
 * Camera + gallery UI is fully working; analysis is a stub in DiseaseClassifier
 * so a TensorFlow Lite model can be plugged in later without touching this UI.
 */
public class LeafDiseaseActivity extends AppCompatActivity {

    private ImageView ivLeaf;
    private TextView tvResult;
    private Button btnAnalyze;
    private Bitmap leafBitmap;

    private final ActivityResultLauncher<Void> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bitmap -> {
                if (bitmap != null) showImage(bitmap);
            });

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::loadFromUri);

    private final ActivityResultLauncher<String> cameraPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    cameraLauncher.launch(null);
                } else {
                    Toast.makeText(this, "Camera permission is needed to take a photo. "
                            + "You can still pick one from the gallery.", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaf_disease);

        ivLeaf = findViewById(R.id.ivLeaf);
        tvResult = findViewById(R.id.tvResult);
        btnAnalyze = findViewById(R.id.btnAnalyze);

        findViewById(R.id.btnCamera).setOnClickListener(v -> cameraPermission.launch(Manifest.permission.CAMERA));
        findViewById(R.id.btnGallery).setOnClickListener(v -> galleryLauncher.launch("image/*"));
        btnAnalyze.setOnClickListener(v -> {
            if (leafBitmap == null) {
                Toast.makeText(this, "Please choose a leaf photo first", Toast.LENGTH_SHORT).show();
                return;
            }
            tvResult.setText(DiseaseClassifier.classify(leafBitmap));
        });
    }

    private void loadFromUri(Uri uri) {
        if (uri == null) return;
        try (InputStream stream = getContentResolver().openInputStream(uri)) {
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            if (bitmap == null) throw new IllegalStateException("decode failed");
            showImage(bitmap);
        } catch (Exception | OutOfMemoryError e) {
            Toast.makeText(this, "Could not open that image. Try another one.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showImage(Bitmap bitmap) {
        leafBitmap = bitmap;
        ivLeaf.setImageBitmap(bitmap);
        btnAnalyze.setEnabled(true);
        tvResult.setText("");
    }
}
