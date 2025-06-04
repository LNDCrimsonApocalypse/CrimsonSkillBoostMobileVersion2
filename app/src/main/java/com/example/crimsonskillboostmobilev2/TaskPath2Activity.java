package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskPath2Activity extends AppCompatActivity {

    private TextView taskTitle, taskDescription, taskDueDate, fileNameText;
    private LinearLayout uploadContainer; // Declare uploadContainer
    private int taskId;
    private String selectedFilePath;

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
        Button submitButton = findViewById(R.id.submitBtn); // Find the submit button

        backButton.setOnClickListener(v -> finish()); // Navigate back
        taskId = getIntent().getIntExtra("taskId", -1);
        fetchTaskDetails(taskId);

        uploadContainer.setOnClickListener(v -> openFilePicker());

        // Hook up the submit button with uploadTask()
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
            Uri uri = data.getData();
            selectedFilePath = getFilePath(uri);

            if (selectedFilePath != null) {
                fileNameText.setText(new File(selectedFilePath).getName());
            } else {
                Toast.makeText(this, "Unable to retrieve file path", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFilePath(Uri uri) {
        String filePath = null;
        if (uri != null) {
            try {
                String[] projection = {android.provider.MediaStore.MediaColumns.DATA};
                try (Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndexOrThrow(android.provider.MediaStore.MediaColumns.DATA);
                        filePath = cursor.getString(columnIndex);
                    }
                }
            } catch (Exception e) {
                Log.e("TaskPath2Activity", "Error retrieving file path: " + e.getMessage());
            }
        }
        return filePath;
    }

    private void fetchTaskDetails(int taskId) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getTaskDetails(taskId).enqueue(new Callback<TaskModel>() {
            @Override
            public void onResponse(Call<TaskModel> call, Response<TaskModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TaskModel task = response.body();
                    taskTitle.setText(task.getTitle());
                    taskDescription.setText(task.getDescription());
                    taskDueDate.setText("Due: " + task.getDueDate());
                } else {
                    Toast.makeText(TaskPath2Activity.this, "Failed to load task details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TaskModel> call, Throwable t) {
                Toast.makeText(TaskPath2Activity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadTask() {
        if (selectedFilePath == null) {
            Toast.makeText(this, "Please select a file to upload", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(selectedFilePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        RequestBody taskIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(taskId));

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.uploadTask(taskIdBody, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("UploadTask", "Upload successful");
                    Intent intent = new Intent(TaskPath2Activity.this, TaskPath3Activity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("UploadTask", "Failed: " + response.code() + " - " + response.message());
                    Toast.makeText(TaskPath2Activity.this, "Failed to upload task", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("UploadTask", "Error: " + t.getMessage());
                Toast.makeText(TaskPath2Activity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}