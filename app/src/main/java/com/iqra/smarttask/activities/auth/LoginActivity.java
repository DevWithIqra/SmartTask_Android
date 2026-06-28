package com.iqra.smarttask.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.iqra.smarttask.R;
import com.iqra.smarttask.utils.Validator;
import com.google.firebase.auth.FirebaseAuth;
import com.iqra.smarttask.activities.dashboard.DashboardActivity;

public class LoginActivity extends AppCompatActivity {

    // Email & Password
    private TextInputEditText etEmail, etPassword;

    // TextView
    private TextView txtForgotPassword;

    // Buttons
    private Button btnLogin, btnCreateAccount;

    // Checkbox
    private CheckBox cbRememberMe;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        firebaseAuth = FirebaseAuth.getInstance();
        clickListeners();
    }

    private void initViews() {

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        txtForgotPassword = findViewById(R.id.txtForgotPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        cbRememberMe = findViewById(R.id.cbRememberMe);
    }

    private void clickListeners() {

        txtForgotPassword.setOnClickListener(v -> {

            Intent intent = new Intent(LoginActivity.this,
                    ForgotPasswordActivity.class);

            startActivity(intent);

        });

        btnCreateAccount.setOnClickListener(v -> {

            Intent intent = new Intent(LoginActivity.this,
                    SignupActivity.class);

            startActivity(intent);

        });

        btnLogin.setOnClickListener(v -> {

            if (validateInputs()) {

                loginUser();

            }

        });

    }

    private boolean validateInputs() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!Validator.isValidEmail(email)) {

            etEmail.setError("Enter a valid email");
            etEmail.requestFocus();
            return false;

        }

        if (!Validator.isValidPassword(password)) {

            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return false;

        }

        return true;
    }

    private void loginUser() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                LoginActivity.this,
                                "Login Successful",
                                Toast.LENGTH_SHORT
                        ).show();

                        startActivity(
                                new Intent(
                                        LoginActivity.this,
                                        DashboardActivity.class
                                )
                        );

                        finish();

                    } else {

                        Toast.makeText(
                                LoginActivity.this,
                                task.getException().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }

}