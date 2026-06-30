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
import com.iqra.smarttask.activities.task.AddTaskActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iqra.smarttask.adapters.TaskAdapter;
import com.iqra.smarttask.models.Task;

import java.util.ArrayList;
import java.util.List;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DashboardActivity extends AppCompatActivity {

    private TextView txtUserName;
    private TextView txtUserEmail;

    private MaterialButton btnAddTask;
    private MaterialButton btnLogout;

    private RecyclerView recyclerTasks;
    private TextView txtNoTasks;

    private List<Task> taskList;
    private TaskAdapter taskAdapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initViews();

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        loadUserProfile();

        taskList = new ArrayList<>();

        taskAdapter = new TaskAdapter(

                taskList,

                task -> {

                    Intent intent = new Intent(
                            DashboardActivity.this,
                            AddTaskActivity.class
                    );

                    intent.putExtra("taskId", task.getTaskId());
                    intent.putExtra("title", task.getTitle());
                    intent.putExtra("description", task.getDescription());
                    intent.putExtra("dueDate", task.getDueDate());
                    intent.putExtra("priority", task.getPriority());

                    startActivity(intent);

                },

                task -> {

                    new AlertDialog.Builder(DashboardActivity.this)
                            .setTitle("Delete Task")
                            .setMessage("Are you sure you want to delete this task?")
                            .setPositiveButton("Delete", (dialog, which) -> {

                                deleteTask(task);

                            })
                            .setNegativeButton("Cancel", null)
                            .show();

                }

        );

        recyclerTasks.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerTasks.setAdapter(taskAdapter);
        loadTasks();

        clickListeners();
    }

    private void initViews() {

        txtUserName = findViewById(R.id.txtUserName);
        txtUserEmail = findViewById(R.id.txtUserEmail);

        btnAddTask = findViewById(R.id.btnAddTask);
        btnLogout = findViewById(R.id.btnLogout);

        recyclerTasks = findViewById(R.id.recyclerTasks);
        txtNoTasks = findViewById(R.id.txtNoTasks);
    }

    private void clickListeners() {

        btnAddTask.setOnClickListener(v -> {

            startActivity(new Intent(
                    DashboardActivity.this,
                    AddTaskActivity.class
            ));

        });

        btnLogout.setOnClickListener(v -> showLogoutDialog());

    }

    private void loadUserProfile() {

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null)
            return;

        firestore.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        txtUserName.setText(documentSnapshot.getString("fullName"));
                        txtUserEmail.setText(documentSnapshot.getString("email"));

                    }

                });

    }

    private void loadTasks() {

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null)
            return;

        firestore.collection("tasks")
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    taskList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                        Task task = document.toObject(Task.class);

                        task.setTaskId(document.getId());

                        taskList.add(task);

                    }

                    taskAdapter.notifyDataSetChanged();

                    if (taskList.isEmpty()) {
                        txtNoTasks.setVisibility(TextView.VISIBLE);
                    } else {
                        txtNoTasks.setVisibility(TextView.GONE);
                    }

                });

    }

    private void deleteTask(Task task) {

        firestore.collection("tasks")
                .document(task.getTaskId())
                .delete()
                .addOnSuccessListener(unused -> {

                    loadTasks();

                    android.widget.Toast.makeText(
                            DashboardActivity.this,
                            "Task deleted successfully",
                            android.widget.Toast.LENGTH_SHORT
                    ).show();

                })
                .addOnFailureListener(e -> {

                    android.widget.Toast.makeText(
                            DashboardActivity.this,
                            e.getMessage(),
                            android.widget.Toast.LENGTH_SHORT
                    ).show();

                });

    }

    @Override
    protected void onResume() {
        super.onResume();

        loadTasks();
    }

    private void showLogoutDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {

                    firebaseAuth.signOut();

                    Intent intent = new Intent(
                            DashboardActivity.this,
                            LoginActivity.class
                    );

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);

                })
                .setNegativeButton("No", null)
                .show();

    }

}