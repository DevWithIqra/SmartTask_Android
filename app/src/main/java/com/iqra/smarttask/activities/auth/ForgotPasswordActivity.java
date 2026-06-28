package com.iqra.smarttask.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.iqra.smarttask.R;
import com.iqra.smarttask.utils.Validator;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private Button btnResetPassword;
    private TextView txtBackToLogin;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initViews();

        firebaseAuth = FirebaseAuth.getInstance();

        clickListeners();
    }

    private void initViews() {

        etEmail = findViewById(R.id.etEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        txtBackToLogin = findViewById(R.id.txtBackToLogin);

    }

    private void clickListeners() {

        btnResetPassword.setOnClickListener(v -> {

            if (validateEmail()) {

                resetPassword();

            }

        });

        txtBackToLogin.setOnClickListener(v -> {

            startActivity(new Intent(
                    ForgotPasswordActivity.this,
                    LoginActivity.class));

            finish();

        });

    }

    private boolean validateEmail() {

        String email = etEmail.getText().toString().trim();

        if (!Validator.isValidEmail(email)) {

            etEmail.setError("Enter a valid email");
            etEmail.requestFocus();
            return false;

        }

        return true;

    }

    private void resetPassword() {

        String email = etEmail.getText().toString().trim();

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                ForgotPasswordActivity.this,
                                "Password reset email sent successfully",
                                Toast.LENGTH_LONG
                        ).show();

                        finish();

                    } else {

                        Toast.makeText(
                                ForgotPasswordActivity.this,
                                task.getException().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }

}