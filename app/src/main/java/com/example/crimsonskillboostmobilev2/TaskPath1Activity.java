package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TaskPath1Activity extends AppCompatActivity {

    private static final String TAG = "TaskPath1Activity";
    private RecyclerView taskRecyclerView;
    private TasksAdapter tasksAdapter;
    private final List<TaskModel> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_path1);

        // ✅ Set up custom back button from XML header
        ImageButton backButton = findViewById(R.id.backButtonTask);
        backButton.setOnClickListener(v -> {
            // Go back to previous screen
            finish();
        });

        // ✅ Find RecyclerView
        taskRecyclerView = findViewById(R.id.taskRecyclerView);
        if (taskRecyclerView == null) {
            Log.e(TAG, "RecyclerView is null. Check the layout file and ID.");
            return;
        }

        setupRecyclerView();
        fetchAllTasksFromAllCourses();
    }

    private void setupRecyclerView() {
        tasksAdapter = new TasksAdapter(tasks, task -> {
            // Debugging: Log task details before navigating
            Log.d(TAG, "Task clicked: ID = " + task.getId() + ", CourseID = " + task.getCourseId());

            if (task.getId() == null || task.getCourseId() == null) {
                Log.e(TAG, "Task or Course ID is null. Cannot navigate to TaskPath2Activity.");
                Toast.makeText(this, "Task or Course ID is missing", Toast.LENGTH_SHORT).show();
                return;
            }

            // Navigate to TaskPath2Activity with task details
            Intent intent = new Intent(TaskPath1Activity.this, TaskPath2Activity.class);
            intent.putExtra("taskId", task.getId());      // Pass taskId
            intent.putExtra("courseId", task.getCourseId()); // Pass courseId
            startActivity(intent);
        });

        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskRecyclerView.setAdapter(tasksAdapter);
    }

    private void fetchAllTasksFromAllCourses() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collectionGroup("tasks")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    tasks.clear();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        TaskModel task = new TaskModel();
                        task.setId(document.getId()); // Set task ID

                        // Extract courseId from the document path
                        String path = document.getReference().getPath(); // e.g., courses/{courseId}/tasks/{taskId}
                        String[] segments = path.split("/");
                        if (segments.length >= 2) {
                            task.setCourseId(segments[1]); // Set courseId
                        }

                        // Fetch and format the end_date
                        String endDate = document.getString("end_date");
                        if (endDate != null && !endDate.isEmpty()) {
                            try {
                                task.setEndDate(formatDate(endDate)); // Format the date
                            } catch (Exception e) {
                                Log.e(TAG, "Error formatting end_date for task: " + task.getId(), e);
                                task.setEndDate("Invalid Date");
                            }
                        } else {
                            task.setEndDate("No Due Date"); // Default message for missing dates
                        }

                        // Set other task fields
                        task.setTitle(document.getString("title"));
                        task.setStatus(document.getString("status"));

                        Log.d(TAG, "Fetched Task: ID = " + task.getId() + ", CourseID = " + task.getCourseId() + ", End Date = " + task.getEndDate());
                        tasks.add(task);
                    }
                    tasksAdapter.updateTasks(tasks);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load tasks: " + e.getMessage(), e);
                    Toast.makeText(this, "Failed to load tasks: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Helper method to format the date
    private String formatDate(String date) throws Exception {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return outputFormat.format(inputFormat.parse(date));
    }
}
