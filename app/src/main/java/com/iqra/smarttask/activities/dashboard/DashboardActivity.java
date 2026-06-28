package com.iqra.smarttask.activities.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iqra.smarttask.R;
import com.iqra.smarttask.activities.auth.LoginActivity;

public class DashboardActivity extends AppCompatActivity {

    private TextView txtUserName;
    private TextView txtUserEmail;
    private MaterialButton btnLogout;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initViews();

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        loadUserData();

        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void initViews() {

        txtUserName = findViewById(R.id.txtUserName);
        txtUserEmail = findViewById(R.id.txtUserEmail);
        btnLogout = findViewById(R.id.btnLogout);

    }

    private void loadUserData() {

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null)
            return;

        txtUserEmail.setText(user.getEmail());

        firestore.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        String fullName = documentSnapshot.getString("fullName");

                        if (fullName != null) {
                            txtUserName.setText(fullName);
                        }

                    }

                });

    }

    private void showLogoutDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {

                    firebaseAuth.signOut();

                    Intent intent = new Intent(
                            DashboardActivity.this,
                            LoginActivity.class
                    );

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);

                })
                .setNegativeButton("Cancel", null)
                .show();

    }

}