package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class TaskPath3Activity extends AppCompatActivity {

    private TextView submissionMessage, instructorFileName, studentFileName, headerTitleTask;
    private String courseId, taskId, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_path3);

        submissionMessage = findViewById(R.id.submissionMessage);
        instructorFileName = findViewById(R.id.instructorFileName);
        studentFileName = findViewById(R.id.studentFileName);
        headerTitleTask = findViewById(R.id.headerTitleTask);

        ImageButton backButton = findViewById(R.id.backButtonTask2);
        backButton.setOnClickListener(v -> finish());

        // Get extras
        taskId = getIntent().getStringExtra("taskId");
        courseId = getIntent().getStringExtra("courseId");
        userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (taskId == null || courseId == null || userId == null) {
            Toast.makeText(this, "Missing data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchTaskTitle();
        fetchInstructorFile();
        fetchStudentFile();
    }

    private void fetchTaskTitle() {
        FirebaseFirestore.getInstance()
                .collection("courses")
                .document(courseId)
                .collection("tasks")
                .document(taskId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String taskTitle = doc.getString("title");
                        if (taskTitle != null) {
                            headerTitleTask.setText(taskTitle);
                        } else {
                            headerTitleTask.setText("No Title");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch task title: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchInstructorFile() {
        FirebaseFirestore.getInstance()
                .collection("courses")
                .document(courseId)
                .collection("tasks")
                .document(taskId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String fileName = doc.getString("file_name");
                        String fileUrl = doc.getString("file_url");
                        if (fileName != null && fileUrl != null) {
                            instructorFileName.setText(fileName);
                            instructorFileName.setOnClickListener(v -> {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl));
                                startActivity(intent);
                            });
                        }
                    }
                });
    }

    private void fetchStudentFile() {
        FirebaseFirestore.getInstance()
                .collection("courses")
                .document(courseId)
                .collection("tasks")
                .document(taskId)
                .collection("submissions")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String fileName = doc.getString("file_name");
                        String fileUrl = doc.getString("file_url");
                        if (fileName != null && fileUrl != null) {
                            studentFileName.setText(fileName);
                            studentFileName.setOnClickListener(v -> {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl));
                                startActivity(intent);
                            });
                        }
                    }
                });
    }
}