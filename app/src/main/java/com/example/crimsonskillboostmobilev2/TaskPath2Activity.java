package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class TaskPath2Activity extends AppCompatActivity {

    private TextView taskTitle, taskDescription, taskDueDate, fileNameText, scoreDisplayTextView;
    private LinearLayout uploadContainer;
    private String taskId;
    private String courseId;
    private Uri selectedFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_path2);

        taskTitle = findViewById(R.id.headerTitleTask);
        taskDescription = findViewById(R.id.taskDescription);
        taskDueDate = findViewById(R.id.taskDueDate);
        fileNameText = findViewById(R.id.fileNameText);
        uploadContainer = findViewById(R.id.uploadContainer);
        scoreDisplayTextView = findViewById(R.id.scoreTextView); // make sure this exists in your XML

        ImageButton backButton = findViewById(R.id.backButtonTask2);
        Button submitButton = findViewById(R.id.submitBtn);

        backButton.setOnClickListener(v -> finish());

        // Get extras
        taskId = getIntent().getStringExtra("taskId");
        courseId = getIntent().getStringExtra("courseId");

        if (taskId == null || courseId == null) {
            Toast.makeText(this, "Missing task or course information", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchTaskDetails(courseId, taskId);

        uploadContainer.setOnClickListener(v -> openFilePicker());
        submitButton.setOnClickListener(v -> uploadTask());

        // Fetch score if user is logged in
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            fetchStudentScore(taskId, userId);
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Select a file"), 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                String name = getFileName(selectedFileUri);
                fileNameText.setText(name);
            } else {
                Toast.makeText(this, "Unable to retrieve file path", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                try {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1 && cursor.moveToFirst()) {
                        result = cursor.getString(nameIndex);
                    }
                } finally {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    private void fetchTaskDetails(String courseId, String taskId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("courses")
                .document(courseId)
                .collection("tasks")
                .document(taskId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String title = documentSnapshot.getString("title");
                        String description = documentSnapshot.getString("description");
                        String endDate = documentSnapshot.getString("end_date");

                        taskTitle.setText(title != null ? title : "No Title");
                        taskDescription.setText(description != null ? description : "No Description");
                        taskDueDate.setText(endDate != null ? "Due: " + endDate : "No due date");
                    } else {
                        Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load task details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadTask() {
        if (selectedFileUri == null) {
            Toast.makeText(this, "Please select a file to upload", Toast.LENGTH_SHORT).show();
            return;
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "No signed-in user. Please log in first.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String originalName = fileNameText.getText().toString().trim();
        if (originalName.isEmpty()) {
            originalName = "submission_file";
        }

        String uniqueName = System.currentTimeMillis() + "_" + originalName;

        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference("task_submissions")
                .child(courseId)
                .child(taskId)
                .child(userId)
                .child(uniqueName);

        String finalOriginalName = originalName;
        storageReference.putFile(selectedFileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        HashMap<String, Object> submissionData = new HashMap<>();
                        submissionData.put("score", 0);
                        submissionData.put("totalPossiblePoints", 0);
                        submissionData.put("timestamp", System.currentTimeMillis());
                        submissionData.put("userId", userId);
                        submissionData.put("fileUrl", uri.toString());
                        submissionData.put("fileName", finalOriginalName);

                        FirebaseFirestore.getInstance()
                                .collection("courses")
                                .document(courseId)
                                .collection("tasks")
                                .document(taskId)
                                .collection("submissions")
                                .document(userId)
                                .set(submissionData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(TaskPath2Activity.this,
                                            "Submission saved successfully!", Toast.LENGTH_SHORT).show();

                                    fetchStudentScore(taskId, userId); // refresh score after submission

                                    startActivity(new Intent(TaskPath2Activity.this, TaskPath3Activity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(TaskPath2Activity.this,
                                            "Failed to save submission: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to upload file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchStudentScore(String taskId, String userId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("courses")
                .document(courseId)
                .collection("tasks")
                .document(taskId)
                .collection("submissions")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long score = documentSnapshot.getLong("score");
                        if (score != null) {
                            scoreDisplayTextView.setText("Score: " + score);
                        } else {
                            scoreDisplayTextView.setText("Score not available");
                        }
                    } else {
                        scoreDisplayTextView.setText("No submission found");
                    }
                })
                .addOnFailureListener(e -> {
                    scoreDisplayTextView.setText("Failed to fetch score: " + e.getMessage());
                });
    }
}
