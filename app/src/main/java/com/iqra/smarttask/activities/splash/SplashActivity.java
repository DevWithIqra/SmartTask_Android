package com.iqra.smarttask.activities.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.iqra.smarttask.R;
import com.iqra.smarttask.activities.auth.LoginActivity;
import com.iqra.smarttask.activities.dashboard.DashboardActivity;
import com.iqra.smarttask.utils.Constants;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            Intent intent;

            if (user != null) {

                intent = new Intent(
                        SplashActivity.this,
                        DashboardActivity.class
                );

            } else {

                intent = new Intent(
                        SplashActivity.this,
                        LoginActivity.class
                );

            }

            startActivity(intent);
            finish();

        }, Constants.SPLASH_DELAY);

    }
}