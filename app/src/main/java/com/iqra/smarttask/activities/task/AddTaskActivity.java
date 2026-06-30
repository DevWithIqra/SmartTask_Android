package com.iqra.smarttask.activities.task;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iqra.smarttask.R;

import java.util.HashMap;

public class AddTaskActivity extends AppCompatActivity {

    private TextInputEditText etTitle;
    private TextInputEditText etDescription;

    private Button btnSaveTask;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    private String taskId = "";
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initViews();

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        clickListeners();

        checkEditMode();
    }

    private void initViews() {

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);

        btnSaveTask = findViewById(R.id.btnSaveTask);

    }

    private void clickListeners() {

        btnSaveTask.setOnClickListener(v -> {

            if (isEditMode) {
                updateTask();
            } else {
                saveTask();
            }

        });

    }

    private void checkEditMode() {

        taskId = getIntent().getStringExtra("taskId");

        if (taskId != null && !taskId.isEmpty()) {

            isEditMode = true;

            etTitle.setText(getIntent().getStringExtra("title"));
            etDescription.setText(getIntent().getStringExtra("description"));

            btnSaveTask.setText("Update Task");

        }

    }

    private void saveTask() {

        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty()) {

            etTitle.setError("Enter task title");
            etTitle.requestFocus();
            return;

        }

        HashMap<String, Object> task = new HashMap<>();

        task.put("title", title);
        task.put("description", description);
        task.put("status", "Pending");
        task.put("userId", firebaseAuth.getCurrentUser().getUid());
        task.put("createdAt", System.currentTimeMillis());

        firestore.collection("tasks")
                .add(task)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(
                            AddTaskActivity.this,
                            "Task Saved Successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();

                })
                .addOnFailureListener(e ->

                        Toast.makeText(
                                AddTaskActivity.this,
                                e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()

                );
    }

    private void updateTask() {

        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty()) {

            etTitle.setError("Enter task title");
            etTitle.requestFocus();
            return;

        }

        HashMap<String, Object> task = new HashMap<>();

        task.put("title", title);
        task.put("description", description);

        firestore.collection("tasks")
                .document(taskId)
                .update(task)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            AddTaskActivity.this,
                            "Task Updated Successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();

                })
                .addOnFailureListener(e ->

                        Toast.makeText(
                                AddTaskActivity.this,
                                e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()

                );

    }
}