package com.example.crimsonskillboostmobilev2;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {

    private List<TaskModel> tasks;
    private final OnTaskClickListener onTaskClickListener;

    public interface OnTaskClickListener {
        void onTaskClick(TaskModel task);
    }

    public TasksAdapter(List<TaskModel> tasks, OnTaskClickListener onTaskClickListener) {
        this.tasks = tasks;
        this.onTaskClickListener = onTaskClickListener;
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
        TaskModel task = tasks.get(position);

        // ✅ Title
        holder.taskTitle.setText(task.getTitle() != null ? task.getTitle() : "Untitled Task");

        // ✅ Parse Firestore date (end_date field)
        Date dueDate = parseDate(task.getEndDate());
        boolean isCompleted = "completed".equalsIgnoreCase(task.getStatus());

        String labelText;
        int color;

        if (isCompleted) {
            // Completed
            String formatted = (dueDate != null) ? formatDate(dueDate, "MMM d yyyy") : "";
            labelText = "Completed: " + formatted;
            color = Color.parseColor("#808080"); // gray
        } else if (dueDate != null) {
            // Compare with today/tomorrow
            Calendar today = zeroTime(Calendar.getInstance());
            Calendar tomorrow = (Calendar) today.clone();
            tomorrow.add(Calendar.DAY_OF_MONTH, 1);

            Calendar dueCal = Calendar.getInstance();
            dueCal.setTime(dueDate);
            Calendar dueDateOnly = zeroTime(dueCal);

            if (isSameDay(dueDateOnly, today)) {
                labelText = "Due today";
                color = Color.parseColor("#FFA500"); // orange
            } else if (isSameDay(dueDateOnly, tomorrow)) {
                labelText = "Due tomorrow";
                color = Color.parseColor("#FFA500"); // orange
            } else {
                labelText = "Due on " + formatDate(dueDate, "MMM d yyyy");
                color = Color.parseColor("#4CAF50"); // green
            }
        } else {
            // No due date
            labelText = "No due date";
            color = Color.parseColor("#808080"); // gray
        }

        // ✅ Apply to UI
        holder.taskDueDate.setText(labelText);
        holder.taskDueDate.setTextColor(color);
        holder.taskDueIcon.setColorFilter(color);

        // ✅ Click listener
        holder.itemView.setOnClickListener(v -> {
            if (onTaskClickListener != null) {
                onTaskClickListener.onTaskClick(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks != null ? tasks.size() : 0;
    }

    public void updateTasks(List<TaskModel> newTasks) {
        this.tasks = newTasks;
        notifyDataSetChanged();
    }

    // --- Helpers ---
    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            // Update the format to match the formatted date from TaskPath1Activity
            return new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String formatDate(Date d, String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(d);
    }

    private Calendar zeroTime(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    private boolean isSameDay(Calendar a, Calendar b) {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
                && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    }

    // --- ViewHolder ---
    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle, taskDueDate;
        ImageView taskDueIcon;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskDueDate = itemView.findViewById(R.id.taskDueDate);
            taskDueIcon = itemView.findViewById(R.id.taskDueIcon);
        }
    }
}
