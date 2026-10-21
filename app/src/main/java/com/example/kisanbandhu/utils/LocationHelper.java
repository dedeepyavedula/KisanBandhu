package com.example.kisanbandhu.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

public class LocationHelper {

    public interface Callback {
        void onLocation(Location location);
        void onPermissionDenied();
        void onLocationError();
    }

    private static final int LOCATION_PERMISSION_REQUEST = 1001;

    private final Activity activity;
    private final FusedLocationProviderClient fusedLocationClient;
    private final Callback callback;

    public LocationHelper(
            Activity activity,
            Callback callback
    ) {
        this.activity = activity;
        this.callback = callback;

        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(
                        activity
                );
    }

    public void getLocation() {

        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST
            );

            return;
        }

        // First try cached location for instant response
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(lastLocation -> {

                    if (lastLocation != null) {
                        // Immediately send cached location
                        callback.onLocation(lastLocation);
                    }

                    // Then request a fresh location
                    getFreshLocation();

                })
                .addOnFailureListener(e -> getFreshLocation());
    }
    private void getFreshLocation() {

        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {

            callback.onPermissionDenied();
            return;
        }

        CancellationTokenSource tokenSource =
                new CancellationTokenSource();

        fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                tokenSource.getToken()
        ).addOnSuccessListener(location -> {

            if (location != null) {
                // Fresh location replaces cached location
                callback.onLocation(location);
            }

        }).addOnFailureListener(e -> {
            // Don't show an error if cached location already worked
        });
    }

    private void getLastKnownLocation() {

        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {

            callback.onPermissionDenied();
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {

                    if (location != null) {
                        callback.onLocation(location);
                    } else {
                        callback.onLocationError();
                    }

                })
                .addOnFailureListener(e ->
                        callback.onLocationError()
                );
    }

    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {

        if (requestCode != LOCATION_PERMISSION_REQUEST) {
            return;
        }

        if (grantResults.length > 0
                && grantResults[0]
                == PackageManager.PERMISSION_GRANTED) {

            getLocation();

        } else if (grantResults.length > 1
                && grantResults[1]
                == PackageManager.PERMISSION_GRANTED) {

            getLocation();

        } else {

            callback.onPermissionDenied();
        }
    }
}