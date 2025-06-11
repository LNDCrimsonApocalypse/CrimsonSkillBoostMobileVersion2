package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TaskPath1Activity extends AppCompatActivity {

    private LinearLayout taskListContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_path1);

        taskListContainer = findViewById(R.id.taskListContainer);
        ImageButton backButton = findViewById(R.id.backButtonTask);

        backButton.setOnClickListener(v -> finish()); // Navigate back
        fetchTasksFromFirebase();
    }

    private void fetchTasksFromFirebase() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("tasks")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<TaskModel> tasks = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        TaskModel task = document.toObject(TaskModel.class);
                        task.setId(document.getId()); // Set Firestore document ID
                        tasks.add(task);
                    }
                    populateTaskList(tasks);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TaskPath1Activity.this, "Failed to load tasks: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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