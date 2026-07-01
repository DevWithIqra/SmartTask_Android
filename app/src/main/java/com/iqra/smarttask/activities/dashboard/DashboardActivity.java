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
import com.google.android.material.textfield.TextInputEditText;

public class DashboardActivity extends AppCompatActivity {

    private TextView txtUserName;
    private TextView txtUserEmail;
    private TextView txtTotalTasks;
    private TextView txtPendingTasks;
    private TextView txtCompletedTasks;

    private MaterialButton btnAddTask;
    private MaterialButton btnSort;
    private MaterialButton btnLogout;
    private MaterialButton btnAll;
    private MaterialButton btnPending;
    private MaterialButton btnCompleted;

    private RecyclerView recyclerTasks;
    private TextView txtNoTasks;

    private TextInputEditText etSearchTask;

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
                },

                task -> {

                    toggleTaskStatus(task);

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

        txtTotalTasks = findViewById(R.id.txtTotalTasks);
        txtPendingTasks = findViewById(R.id.txtPendingTasks);
        txtCompletedTasks = findViewById(R.id.txtCompletedTasks);

        btnAddTask = findViewById(R.id.btnAddTask);
        btnSort = findViewById(R.id.btnSort);
        btnLogout = findViewById(R.id.btnLogout);

        btnAll = findViewById(R.id.btnAll);
        btnPending = findViewById(R.id.btnPending);
        btnCompleted = findViewById(R.id.btnCompleted);

        recyclerTasks = findViewById(R.id.recyclerTasks);
        txtNoTasks = findViewById(R.id.txtNoTasks);
        etSearchTask = findViewById(R.id.etSearchTask);
    }

    private void clickListeners() {

        btnAddTask.setOnClickListener(v -> {

            startActivity(new Intent(
                    DashboardActivity.this,
                    AddTaskActivity.class
            ));

        });

        btnSort.setOnClickListener(v -> {

            showSortDialog();

        });

        btnLogout.setOnClickListener(v -> showLogoutDialog());

        btnAll.setOnClickListener(v -> {

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
                                .setPositiveButton("Delete", (dialog, which) -> deleteTask(task))
                                .setNegativeButton("Cancel", null)
                                .show();

                    },

                    task -> {

                        toggleTaskStatus(task);

                    }

            );

            recyclerTasks.setAdapter(taskAdapter);

            loadTasks();

        });

        btnPending.setOnClickListener(v -> {

            filterByStatus(false);

        });

        btnCompleted.setOnClickListener(v -> {

            filterByStatus(true);

        });

        etSearchTask.addTextChangedListener(new android.text.TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                filterTasks(s.toString());

            }

            @Override
            public void afterTextChanged(android.text.Editable s) {

            }

        });

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
                    updateStatistics();

                    if (taskList.isEmpty()) {
                        txtNoTasks.setVisibility(TextView.VISIBLE);
                    } else {
                        txtNoTasks.setVisibility(TextView.GONE);
                    }

                });

    }

    private void updateStatistics() {

        int total = taskList.size();
        int pending = 0;
        int completed = 0;

        for (Task task : taskList) {

            if (task.isCompleted()) {
                completed++;
            } else {
                pending++;
            }

        }

        txtTotalTasks.setText(String.valueOf(total));
        txtPendingTasks.setText(String.valueOf(pending));
        txtCompletedTasks.setText(String.valueOf(completed));

    }

    private void filterTasks(String keyword) {

        List<Task> filteredList = new ArrayList<>();

        for (Task task : taskList) {

            if (task.getTitle().toLowerCase().contains(keyword.toLowerCase())
                    || task.getDescription().toLowerCase().contains(keyword.toLowerCase())) {

                filteredList.add(task);

            }

        }

        taskAdapter = new TaskAdapter(

                filteredList,

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
                            .setPositiveButton("Delete", (dialog, which) -> deleteTask(task))
                            .setNegativeButton("Cancel", null)
                            .show();

                },

                task -> {

                    toggleTaskStatus(task);

                }

        );

        recyclerTasks.setAdapter(taskAdapter);

    }

    private void filterByStatus(boolean completed) {

        List<Task> filteredList = new ArrayList<>();

        for (Task task : taskList) {

            if (task.isCompleted() == completed) {

                filteredList.add(task);

            }

        }

        taskAdapter = new TaskAdapter(

                filteredList,

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
                            .setPositiveButton("Delete", (dialog, which) -> deleteTask(task))
                            .setNegativeButton("Cancel", null)
                            .show();

                },

                task -> {

                    toggleTaskStatus(task);

                }

        );

        recyclerTasks.setAdapter(taskAdapter);

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

    private void toggleTaskStatus(Task task) {

        boolean newStatus = !task.isCompleted();

        firestore.collection("tasks")
                .document(task.getTaskId())
                .update("completed", newStatus)
                .addOnSuccessListener(unused -> {

                    loadTasks();

                })
                .addOnFailureListener(e -> {

                    android.widget.Toast.makeText(
                            DashboardActivity.this,
                            e.getMessage(),
                            android.widget.Toast.LENGTH_SHORT
                    ).show();

                });

    }

    private void showSortDialog() {

        String[] options = {
                "Newest First",
                "Oldest First",
                "Title A-Z",
                "Title Z-A"
        };

        new AlertDialog.Builder(this)
                .setTitle("Sort Tasks")
                .setItems(options, (dialog, which) -> {

                    switch (which) {

                        case 0:
                            sortNewestFirst();
                            break;

                        case 1:
                            sortOldestFirst();
                            break;

                        case 2:
                            sortTitleAZ();
                            break;

                        case 3:
                            sortTitleZA();
                            break;

                    }

                })
                .show();

    }

    private void sortNewestFirst() {

        java.util.Collections.sort(taskList,
                (task1, task2) ->
                        Long.compare(task2.getCreatedAt(), task1.getCreatedAt()));

        taskAdapter.notifyDataSetChanged();

    }

    private void sortOldestFirst() {

        java.util.Collections.sort(taskList,
                (task1, task2) ->
                        Long.compare(task1.getCreatedAt(), task2.getCreatedAt()));

        taskAdapter.notifyDataSetChanged();

    }

    private void sortTitleAZ() {

        java.util.Collections.sort(taskList,
                (task1, task2) ->
                        task1.getTitle().compareToIgnoreCase(task2.getTitle()));

        taskAdapter.notifyDataSetChanged();

    }

    private void sortTitleZA() {

        java.util.Collections.sort(taskList,
                (task1, task2) ->
                        task2.getTitle().compareToIgnoreCase(task1.getTitle()));

        taskAdapter.notifyDataSetChanged();

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