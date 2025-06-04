package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskPath1Activity extends AppCompatActivity {

    private LinearLayout taskListContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_path1);

        taskListContainer = findViewById(R.id.taskListContainer);
        ImageButton backButton = findViewById(R.id.backButtonTask);

        backButton.setOnClickListener(v -> finish()); // Navigate back
        fetchTasks();
    }

    private void fetchTasks() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getTasks().enqueue(new Callback<List<TaskModel>>() {
            @Override
            public void onResponse(Call<List<TaskModel>> call, Response<List<TaskModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    populateTaskList(response.body());
                } else {
                    Toast.makeText(TaskPath1Activity.this, "Failed to load tasks", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TaskModel>> call, Throwable t) {
                Toast.makeText(TaskPath1Activity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateTaskList(List<TaskModel> tasks) {
        taskListContainer.removeAllViews();

        for (TaskModel task : tasks) {
            View taskItemView = getLayoutInflater().inflate(R.layout.task_item, taskListContainer, false);

            TextView taskTitle = taskItemView.findViewById(R.id.taskTitle);
            TextView taskDueDate = taskItemView.findViewById(R.id.taskDueDate);

            taskTitle.setText(task.getTitle());
            taskDueDate.setText("Due: " + task.getDueDate());

            taskItemView.setOnClickListener(v -> {
                Intent intent = new Intent(this, TaskPath2Activity.class);
                intent.putExtra("taskId", task.getId());
                startActivity(intent);
            });

            taskListContainer.addView(taskItemView);
        }
    }
}