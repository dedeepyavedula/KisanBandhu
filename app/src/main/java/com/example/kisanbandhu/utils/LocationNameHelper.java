package com.example.kisanbandhu.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocationNameHelper {

    public interface Callback {
        void onLocationName(String name);
    }

    private static final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    public static void getLocationName(
            Context context,
            Location location,
            Callback callback
    ) {

        executor.execute(() -> {

            String locationName = "Current location";

            try {

                Geocoder geocoder =
                        new Geocoder(
                                context,
                                Locale.getDefault()
                        );

                List<Address> addresses =
                        geocoder.getFromLocation(
                                location.getLatitude(),
                                location.getLongitude(),
                                1
                        );

                if (addresses != null
                        && !addresses.isEmpty()) {

                    Address address = addresses.get(0);

                    String area = address.getLocality();
                    String district = address.getSubAdminArea();
                    String state = address.getAdminArea();

                    if (area != null
                            && !area.trim().isEmpty()) {

                        if (state != null
                                && !state.trim().isEmpty()) {

                            locationName =
                                    area + ", " + state;

                        } else {

                            locationName = area;
                        }

                    } else if (district != null
                            && !district.trim().isEmpty()) {

                        if (state != null
                                && !state.trim().isEmpty()) {

                            locationName =
                                    district + ", " + state;

                        } else {

                            locationName = district;
                        }
                    }
                }

            } catch (Exception ignored) {
            }

            String finalLocationName = locationName;

            new Handler(Looper.getMainLooper()).post(() ->
                    callback.onLocationName(finalLocationName)
            );
        });
    }
}