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
import com.example.kisanbandhu.utils.ApiService;
import com.example.kisanbandhu.utils.RetrofitClient;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeafDiseaseActivity extends AppCompatActivity {

    private ImageView ivLeaf;
    private TextView tvResult;
    private Button btnAnalyze;

    private Bitmap leafBitmap;

    private final ActivityResultLauncher<Void> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.TakePicturePreview(),
                    bitmap -> {
                        if (bitmap != null) {
                            showImage(bitmap);
                        }
                    }
            );

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    this::loadFromUri
            );

    private final ActivityResultLauncher<String> cameraPermission =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    granted -> {
                        if (granted) {
                            cameraLauncher.launch(null);
                        } else {
                            Toast.makeText(
                                    this,
                                    "Camera permission is needed to take a photo.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_leaf_disease);

        ivLeaf = findViewById(R.id.ivLeaf);
        tvResult = findViewById(R.id.tvResult);
        btnAnalyze = findViewById(R.id.btnAnalyze);

        findViewById(R.id.btnCamera).setOnClickListener(
                v -> cameraPermission.launch(Manifest.permission.CAMERA)
        );

        findViewById(R.id.btnGallery).setOnClickListener(
                v -> galleryLauncher.launch("image/*")
        );

        btnAnalyze.setOnClickListener(v -> {

            if (leafBitmap == null) {
                Toast.makeText(
                        this,
                        "Please choose a leaf photo first",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            analyzeLeaf();
        });
    }

    private void loadFromUri(Uri uri) {

        if (uri == null) {
            return;
        }

        try (InputStream stream =
                     getContentResolver().openInputStream(uri)) {

            Bitmap bitmap = BitmapFactory.decodeStream(stream);

            if (bitmap == null) {
                throw new IllegalStateException("Could not decode image");
            }

            showImage(bitmap);

        } catch (Exception | OutOfMemoryError e) {

            Toast.makeText(
                    this,
                    "Could not open that image. Try another one.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void showImage(Bitmap bitmap) {

        leafBitmap = bitmap;

        ivLeaf.setImageBitmap(bitmap);

        btnAnalyze.setEnabled(true);

        tvResult.setText("");
    }

    private void analyzeLeaf() {

        btnAnalyze.setEnabled(false);

        tvResult.setText("Analyzing leaf...");

        // Convert Bitmap to JPEG bytes
        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        leafBitmap.compress(
                Bitmap.CompressFormat.JPEG,
                90,
                outputStream
        );

        byte[] imageBytes = outputStream.toByteArray();

        // Create request body
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/jpeg"),
                        imageBytes
                );

        // Create multipart file
        MultipartBody.Part body =
                MultipartBody.Part.createFormData(
                        "file",
                        "leaf.jpg",
                        requestFile
                );

        // Get API service
        ApiService apiService =
                RetrofitClient.getApiService();

        // Send request
        Call<JsonObject> call =
                apiService.detectDisease(body);

        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(
                    Call<JsonObject> call,
                    Response<JsonObject> response) {

                btnAnalyze.setEnabled(true);

                if (response.isSuccessful()
                        && response.body() != null) {

                    displayResult(response.body());

                } else {

                    tvResult.setText(
                            "Analysis failed.\nHTTP Code: "
                                    + response.code()
                    );
                }
            }

            @Override
            public void onFailure(
                    Call<JsonObject> call,
                    Throwable t) {

                btnAnalyze.setEnabled(true);

                tvResult.setText(
                        "Could not connect to the disease detection server.\n\n"
                                + "Error: "
                                + t.getMessage()
                );
            }
        });
    }

    private void displayResult(JsonObject result) {

        StringBuilder output = new StringBuilder();

        addResultField(output, result, "disease_name", "Disease");
        addResultField(output, result, "disease_type", "Type");
        addResultField(output, result, "severity", "Severity");
        addResultField(output, result, "confidence", "Confidence");
        addResultField(output, result, "symptoms", "Symptoms");
        addResultField(output, result, "causes", "Causes");
        addResultField(output, result, "treatment", "Treatment");

        if (output.length() == 0) {
            output.append("No disease analysis details were returned.");
        }

        tvResult.setText(output.toString());
    }

    private void addResultField(
            StringBuilder output,
            JsonObject result,
            String jsonKey,
            String displayName) {

        if (!result.has(jsonKey) || result.get(jsonKey).isJsonNull()) {
            return;
        }

        output.append(displayName)
                .append(":\n");

        output.append(formatJsonValue(result.get(jsonKey)));

        output.append("\n\n");
    }

    private String formatJsonValue(
            com.google.gson.JsonElement element) {

        if (element.isJsonPrimitive()) {
            return element.getAsString();
        }

        if (element.isJsonArray()) {

            StringBuilder text = new StringBuilder();

            for (com.google.gson.JsonElement item
                    : element.getAsJsonArray()) {

                if (item.isJsonPrimitive()) {

                    text.append("• ")
                            .append(item.getAsString())
                            .append("\n");

                } else {

                    text.append("• ")
                            .append(item.toString())
                            .append("\n");
                }
            }

            return text.toString().trim();
        }

        if (element.isJsonObject()) {
            return element.toString();
        }

        return element.toString();
    }}