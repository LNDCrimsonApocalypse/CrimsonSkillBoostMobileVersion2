package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.database.Cursor;
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

    private TextView taskTitle, taskDescription, taskDueDate, fileNameText;
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
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // allow any file type
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

    // Safely fetch display name from content resolver
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
            // fallback to last segment
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
                        // Manually map fields since Firestore uses "end_date"
                        String title = documentSnapshot.getString("title");
                        String description = documentSnapshot.getString("description");
                        String endDate = documentSnapshot.getString("end_date"); // ✅ use end_date field

                        // Set UI text
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

        // ✅ Add a file name under the userId to match your rules
        String uniqueName = System.currentTimeMillis() + "_" + originalName;

        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference("task_submissions")
                .child(courseId)
                .child(taskId)
                .child(userId)
                .child(uniqueName); // <-- IMPORTANT to match security rules

        String finalOriginalName = originalName;
        storageReference.putFile(selectedFileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Build Firestore submission document
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
                                .document(userId) // one submission per user; use .add() if multiple
                                .set(submissionData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(TaskPath2Activity.this,
                                            "Submission saved successfully!", Toast.LENGTH_SHORT).show();
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
}
