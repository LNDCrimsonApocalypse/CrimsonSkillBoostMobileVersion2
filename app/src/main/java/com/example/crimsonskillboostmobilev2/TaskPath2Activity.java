package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;

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
        intent.setType("*/*");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                String name = new File(selectedFileUri.getPath()).getName();
                fileNameText.setText(name);
            } else {
                Toast.makeText(this, "Unable to retrieve file path", Toast.LENGTH_SHORT).show();
            }
        }
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
                        TaskModel task = documentSnapshot.toObject(TaskModel.class);
                        if (task != null) {
                            taskTitle.setText(task.getTitle());
                            taskDescription.setText(task.getDescription());
                            taskDueDate.setText("Due: " + task.getDueDate());
                        }
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

        // TODO: Implement Firebase Storage upload here
        // Example placeholder
        Toast.makeText(this, "Upload logic not implemented yet", Toast.LENGTH_SHORT).show();

//        StorageReference storageReference = FirebaseStorage.getInstance()
//                .getReference("task_submissions/" + courseId + "/" + taskId + "/" + userId);
//        storageReference.putFile(selectedFileUri)
//                .addOnSuccessListener(taskSnapshot -> {
//                    Toast.makeText(this, "File uploaded successfully", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(TaskPath2Activity.this, TaskPath3Activity.class);
//                    startActivity(intent);
//                    finish();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(this, "Failed to upload file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
    }
}
