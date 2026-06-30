package com.iqra.smarttask.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iqra.smarttask.R;
import com.iqra.smarttask.models.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    public interface OnTaskLongClickListener {
        void onTaskLongClick(Task task);
    }

    private final OnTaskClickListener clickListener;

    private final OnTaskLongClickListener listener;

    private final List<Task> taskList;

    public TaskAdapter(List<Task> taskList,
                       OnTaskClickListener clickListener,
                       OnTaskLongClickListener listener) {

        this.taskList = taskList;
        this.clickListener = clickListener;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);

        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {

        Task task = taskList.get(position);

        holder.txtTaskTitle.setText(task.getTitle());
        holder.txtTaskDescription.setText(task.getDescription());
        holder.txtTaskStatus.setText(
                task.isCompleted() ? "Completed" : "Pending"
        );

        holder.itemView.setOnClickListener(v -> {

            clickListener.onTaskClick(task);

        });

        holder.itemView.setOnLongClickListener(v -> {

            listener.onTaskLongClick(task);

            return true;
        });

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView txtTaskTitle;
        TextView txtTaskDescription;
        TextView txtTaskStatus;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTaskTitle = itemView.findViewById(R.id.txtTaskTitle);
            txtTaskDescription = itemView.findViewById(R.id.txtTaskDescription);
            txtTaskStatus = itemView.findViewById(R.id.txtTaskStatus);
        }
    }
}