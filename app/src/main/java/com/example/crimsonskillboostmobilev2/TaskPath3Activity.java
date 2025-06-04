package com.example.crimsonskillboostmobilev2;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TaskPath3Activity extends AppCompatActivity {

    private TextView submissionMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_path3);

        submissionMessage = findViewById(R.id.fileNameText);
        submissionMessage.setText("Your task has been successfully submitted!");
        ImageButton backButton = findViewById(R.id.backButtonTask2);

        backButton.setOnClickListener(v -> finish()); // Navigate back
    }
}