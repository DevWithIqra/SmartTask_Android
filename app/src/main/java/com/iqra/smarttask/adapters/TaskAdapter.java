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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
        holder.txtTaskDueDate.setText("Due Date: " + task.getDueDate());
        holder.txtTaskPriority.setText("Priority: " + task.getPriority());
        String priority = task.getPriority();

        if ("High".equalsIgnoreCase(priority)) {

            holder.txtTaskPriority.setTextColor(
                    holder.itemView.getContext().getColor(android.R.color.holo_red_dark)
            );

        } else if ("Medium".equalsIgnoreCase(priority)) {

            holder.txtTaskPriority.setTextColor(
                    holder.itemView.getContext().getColor(android.R.color.holo_orange_dark)
            );

        } else {

            holder.txtTaskPriority.setTextColor(
                    holder.itemView.getContext().getColor(android.R.color.holo_green_dark)
            );

        }
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

        try {

            if (!task.isCompleted()
                    && task.getDueDate() != null
                    && !task.getDueDate().isEmpty()) {

                SimpleDateFormat sdf =
                        new SimpleDateFormat("d/M/yyyy", Locale.getDefault());

                Date dueDate = sdf.parse(task.getDueDate());

                if (dueDate != null && dueDate.before(new Date())) {

                    holder.txtOverdue.setVisibility(View.VISIBLE);

                } else {

                    holder.txtOverdue.setVisibility(View.GONE);

                }

            } else {

                holder.txtOverdue.setVisibility(View.GONE);

            }

        } catch (Exception e) {

            holder.txtOverdue.setVisibility(View.GONE);

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
        TextView txtTaskDueDate;
        TextView txtTaskPriority;
        TextView txtOverdue;

        MaterialButton btnToggleStatus;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTaskTitle = itemView.findViewById(R.id.txtTaskTitle);
            txtTaskDescription = itemView.findViewById(R.id.txtTaskDescription);
            txtTaskStatus = itemView.findViewById(R.id.txtTaskStatus);

            txtTaskDueDate = itemView.findViewById(R.id.txtTaskDueDate);
            txtTaskPriority = itemView.findViewById(R.id.txtTaskPriority);
            txtOverdue = itemView.findViewById(R.id.txtOverdue);

            btnToggleStatus = itemView.findViewById(R.id.btnToggleStatus);
        }
    }
}