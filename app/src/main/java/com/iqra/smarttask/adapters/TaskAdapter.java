package com.iqra.smarttask.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iqra.smarttask.R;
import com.iqra.smarttask.models.Task;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    public interface OnTaskLongClickListener {
        void onTaskLongClick(Task task);
    }

    public interface OnStatusClickListener {
        void onStatusClick(Task task);
    }

    private final OnTaskClickListener clickListener;

    private final OnTaskLongClickListener listener;

    private final OnStatusClickListener statusClickListener;

    private final List<Task> taskList;

    public TaskAdapter(List<Task> taskList,
                       OnTaskClickListener clickListener,
                       OnTaskLongClickListener listener,
                       OnStatusClickListener statusClickListener) {

        this.taskList = taskList;
        this.clickListener = clickListener;
        this.listener = listener;
        this.statusClickListener = statusClickListener;
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

        holder.btnToggleStatus.setText(
                task.isCompleted() ? "Mark Pending" : "Mark Complete"
        );
        if (task.isCompleted()) {

            holder.txtTaskTitle.setPaintFlags(
                    holder.txtTaskTitle.getPaintFlags()
                            | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            );

            holder.txtTaskDescription.setPaintFlags(
                    holder.txtTaskDescription.getPaintFlags()
                            | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            );

            holder.txtTaskStatus.setTextColor(
                    android.graphics.Color.parseColor("#2E7D32")
            );

        } else {

            holder.txtTaskTitle.setPaintFlags(
                    holder.txtTaskTitle.getPaintFlags()
                            & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG)
            );

            holder.txtTaskDescription.setPaintFlags(
                    holder.txtTaskDescription.getPaintFlags()
                            & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG)
            );

            holder.txtTaskStatus.setTextColor(
                    android.graphics.Color.parseColor("#F57C00")
            );

        }

        holder.itemView.setOnClickListener(v -> {

            clickListener.onTaskClick(task);

        });

        holder.itemView.setOnLongClickListener(v -> {

            listener.onTaskLongClick(task);

            return true;
        });

        holder.btnToggleStatus.setOnClickListener(v -> {

            statusClickListener.onStatusClick(task);

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

        MaterialButton btnToggleStatus;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTaskTitle = itemView.findViewById(R.id.txtTaskTitle);
            txtTaskDescription = itemView.findViewById(R.id.txtTaskDescription);
            txtTaskStatus = itemView.findViewById(R.id.txtTaskStatus);

            btnToggleStatus = itemView.findViewById(R.id.btnToggleStatus);
        }
    }
}