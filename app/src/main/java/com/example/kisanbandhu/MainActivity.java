// FILE: app/src/main/java/com/example/kisanbandhu/MainActivity.java
package com.example.kisanbandhu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kisanbandhu.activities.HomeActivity;

/**
 * SCREEN 1 - Splash Screen.
 *
 * This is the very first screen the user sees. It simply shows the app
 * name and tagline for a short time, then moves on to the Home screen.
 *
 * This is also the "launcher" Activity — the one Android starts when the
 * user taps the app icon (see AndroidManifest.xml, it has the
 * LAUNCHER intent-filter).
 */
public class MainActivity extends AppCompatActivity {

    // How long the splash screen stays visible, in milliseconds.
    private static final int SPLASH_DELAY_MS = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Handler + postDelayed is the simplest beginner-friendly way to
        // "wait a bit, then do something" on Android. It runs the code
        // inside run() after SPLASH_DELAY_MS milliseconds have passed.
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                goToHomeScreen();
            }
        }, SPLASH_DELAY_MS);
    }

    /**
     * Moves from the splash screen to the Home dashboard.
     * finish() is called so that pressing "Back" on the Home screen does
     * NOT bring the user back to the splash screen.
     */
    private void goToHomeScreen() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
