package com.iqra.smarttask.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.iqra.smarttask.R;
import com.iqra.smarttask.utils.Validator;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText etFullName;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;

    private Button btnSignup;
    private TextView txtLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initViews();
        clickListeners();
    }

    private void initViews() {

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnSignup = findViewById(R.id.btnSignup);
        txtLogin = findViewById(R.id.txtLogin);

    }

    private void clickListeners() {

        btnSignup.setOnClickListener(v -> {

            if (validateInputs()) {

                Toast.makeText(
                        SignupActivity.this,
                        "Validation Successful",
                        Toast.LENGTH_SHORT
                ).show();

            }

        });

        txtLogin.setOnClickListener(v -> {

            startActivity(new Intent(
                    SignupActivity.this,
                    LoginActivity.class));

            finish();

        });

    }

    private boolean validateInputs() {

        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (fullName.isEmpty()) {

            etFullName.setError("Enter your full name");
            etFullName.requestFocus();
            return false;

        }

        if (!Validator.isValidEmail(email)) {

            etEmail.setError("Enter valid email");
            etEmail.requestFocus();
            return false;

        }

        if (!Validator.isValidPassword(password)) {

            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return false;

        }

        if (!password.equals(confirmPassword)) {

            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return false;

        }

        return true;
    }

}