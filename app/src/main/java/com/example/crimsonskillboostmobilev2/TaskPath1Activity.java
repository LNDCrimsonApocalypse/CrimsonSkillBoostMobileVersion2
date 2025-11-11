package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class TaskPath1Activity extends AppCompatActivity {

    private static final String TAG = "TaskPath1Activity";
    private RecyclerView taskRecyclerView;
    private TasksAdapter tasksAdapter;
    private final List<TaskModel> tasks = new ArrayList<>();
    private FirebaseFirestore firestore;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_path1);

        firestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "testUser";

        ImageButton backButton = findViewById(R.id.backButtonTask);
        backButton.setOnClickListener(v -> finish());

        taskRecyclerView = findViewById(R.id.taskRecyclerView);
        if (taskRecyclerView == null) {
            Log.e(TAG, "RecyclerView is null. Check your XML ID.");
            return;
        }

        setupRecyclerView();
        fetchAllTasksFromAllCourses();
    }

    private void setupRecyclerView() {
        tasksAdapter = new TasksAdapter(tasks, task -> {
            if (task.isLocked()) {
                Toast.makeText(this, "This task is locked. Complete the required task first.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (task.getId() == null || task.getCourseId() == null) {
                Toast.makeText(this, "Task or Course ID is missing", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(TaskPath1Activity.this, TaskPath2Activity.class);
            intent.putExtra("taskId", task.getId());
            intent.putExtra("courseId", task.getCourseId());
            startActivity(intent);
        });

        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskRecyclerView.setAdapter(tasksAdapter);
    }

    private void fetchAllTasksFromAllCourses() {
        firestore.collectionGroup("tasks")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    tasks.clear();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        TaskModel task = new TaskModel();
                        task.setId(document.getId());

                        // Get courseId from path
                        String path = document.getReference().getPath(); // e.g. courses/{courseId}/tasks/{taskId}
                        String[] segments = path.split("/");
                        if (segments.length >= 2) {
                            task.setCourseId(segments[1]);
                        }

                        String endDate = document.getString("end_date");
                        if (endDate != null && !endDate.isEmpty()) {
                            try {
                                task.setEndDate(formatDate(endDate));
                            } catch (Exception e) {
                                Log.e(TAG, "Error formatting end_date", e);
                                task.setEndDate("Invalid Date");
                            }
                        } else {
                            task.setEndDate("No Due Date");
                        }

                        task.setTitle(document.getString("title"));
                        task.setStatus(document.getString("status"));
                        task.setRequiredTask(document.getString("requiredTask"));

                        // Default locked
                        task.setLocked(true);

                        tasks.add(task);
                    }

                    evaluateTaskLocks(tasks);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load tasks", e);
                    Toast.makeText(this, "Failed to load tasks: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * For every task that has a requiredTask,
     * check if the current user exists in that requiredTask’s submissions subcollection.
     */
    private void evaluateTaskLocks(List<TaskModel> tasks) {
        Map<String, TaskModel> taskById = new HashMap<>();
        for (TaskModel t : tasks) taskById.put(t.getId(), t);

        AtomicInteger checksRemaining = new AtomicInteger(0);

        for (TaskModel task : tasks) {
            String requiredTaskId = task.getRequiredTask();

            if (requiredTaskId == null || requiredTaskId.trim().isEmpty()) {
                // No prerequisite
                task.setLocked(false);
                continue;
            }

            checksRemaining.incrementAndGet();

            // Try to find the courseId of the requiredTask
            TaskModel requiredTaskModel = taskById.get(requiredTaskId);
            if (requiredTaskModel != null) {
                String requiredCourseId = requiredTaskModel.getCourseId();
                checkIfSubmissionExists(requiredCourseId, requiredTaskId, exists -> {
                    task.setLocked(!exists);
                    if (checksRemaining.decrementAndGet() == 0) tasksAdapter.updateTasks(tasks);
                });
            } else {
                // Not found — query Firestore to locate requiredTask
                findCourseForRequiredTask(requiredTaskId, foundCourseId -> {
                    if (foundCourseId != null) {
                        checkIfSubmissionExists(foundCourseId, requiredTaskId, exists -> {
                            task.setLocked(!exists);
                            if (checksRemaining.decrementAndGet() == 0) tasksAdapter.updateTasks(tasks);
                        });
                    } else {
                        Log.w(TAG, "Could not find required task: " + requiredTaskId);
                        task.setLocked(true);
                        if (checksRemaining.decrementAndGet() == 0) tasksAdapter.updateTasks(tasks);
                    }
                });
            }
        }

        if (checksRemaining.get() == 0) {
            tasksAdapter.updateTasks(tasks);
        }
    }

    /**
     * Locate courseId of requiredTask if it’s not already known.
     */
    private void findCourseForRequiredTask(String requiredTaskId, Consumer<String> callback) {
        firestore.collectionGroup("tasks")
                .whereEqualTo(FieldPath.documentId(), requiredTaskId)
                .get()
                .addOnSuccessListener(snap -> {
                    if (!snap.isEmpty()) {
                        DocumentSnapshot doc = snap.getDocuments().get(0);
                        String path = doc.getReference().getPath();
                        String[] segments = path.split("/");
                        if (segments.length >= 2) {
                            callback.accept(segments[1]);
                            return;
                        }
                    }
                    callback.accept(null);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to find course for task " + requiredTaskId, e);
                    callback.accept(null);
                });
    }

    /**
     * Checks if submission exists under:
     * /courses/{courseId}/tasks/{taskId}/submissions/{userId}
     */
    private void checkIfSubmissionExists(String courseId, String taskId, Consumer<Boolean> callback) {
        if (courseId == null || taskId == null) {
            callback.accept(false);
            return;
        }

        firestore.collection("courses")
                .document(courseId)
                .collection("tasks")
                .document(taskId)
                .collection("submissions")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    boolean exists = doc.exists();
                    Log.d(TAG, "Checked submission: " + courseId + "/" + taskId + " user=" + userId + " -> " + exists);
                    callback.accept(exists);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking submission for " + taskId, e);
                    callback.accept(false);
                });
    }

    private String formatDate(String date) throws Exception {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return outputFormat.format(inputFormat.parse(date));
    }
}
