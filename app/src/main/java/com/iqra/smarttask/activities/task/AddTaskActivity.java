package com.iqra.smarttask.activities.task;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import android.app.DatePickerDialog;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

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
    private Spinner spPriority;
    private Button btnSelectDate;
    private TextView txtSelectedDate;

    private String selectedDate = "";

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

        setupPrioritySpinner();
        clickListeners();

        checkEditMode();
    }

    private void initViews() {

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);

        btnSaveTask = findViewById(R.id.btnSaveTask);
        spPriority = findViewById(R.id.spPriority);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        txtSelectedDate = findViewById(R.id.txtSelectedDate);

    }

    private void setupPrioritySpinner() {

        String[] priorities = {
                "Low",
                "Medium",
                "High"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                priorities
        );

        spPriority.setAdapter(adapter);

    }
    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(

                this,

                (view, year, month, dayOfMonth) -> {

                    selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;

                    txtSelectedDate.setText(selectedDate);

                },

                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)

        );

        dialog.show();

    }
    private void clickListeners() {

        btnSelectDate.setOnClickListener(v -> {

            showDatePicker();

        });
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

            selectedDate = getIntent().getStringExtra("dueDate");

            if (selectedDate != null && !selectedDate.isEmpty()) {
                txtSelectedDate.setText(selectedDate);
            }

            String priority = getIntent().getStringExtra("priority");

            if (priority != null) {

                ArrayAdapter adapter = (ArrayAdapter) spPriority.getAdapter();

                int position = adapter.getPosition(priority);

                spPriority.setSelection(position);

            }

            btnSaveTask.setText("Update Task");

        }

    }

    private void saveTask() {

        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priority = spPriority.getSelectedItem().toString();

        if (title.isEmpty()) {

            etTitle.setError("Enter task title");
            etTitle.requestFocus();
            return;

        }

        HashMap<String, Object> task = new HashMap<>();

        task.put("title", title);
        task.put("description", description);

        task.put("priority", priority);
        task.put("dueDate", selectedDate);

        task.put("completed", false);
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
        String priority = spPriority.getSelectedItem().toString();

        if (title.isEmpty()) {

            etTitle.setError("Enter task title");
            etTitle.requestFocus();
            return;

        }

        HashMap<String, Object> task = new HashMap<>();

        task.put("title", title);
        task.put("description", description);
        task.put("priority", priority);
        task.put("dueDate", selectedDate);

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