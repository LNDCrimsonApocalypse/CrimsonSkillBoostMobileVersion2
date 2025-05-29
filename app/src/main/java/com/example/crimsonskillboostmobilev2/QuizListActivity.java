package com.example.crimsonskillboostmobilev2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class QuizListActivity extends AppCompatActivity {

    private ImageButton backButton;
    private Button startQuizButton, reviewQuizButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_list);

        backButton = findViewById(R.id.backButton);
        startQuizButton = findViewById(R.id.startQuizButton); // Replace with actual id if added
        reviewQuizButton = findViewById(R.id.reviewQuizButton); // Replace with actual id if added

        backButton.setOnClickListener(v -> finish());

        // Start Quiz Example
        startQuizButton.setOnClickListener(v -> {
//            Intent intent = new Intent(QuizListActivity.this, QuizActivity.class);
//            intent.putExtra("quizId", 1); // Pass relevant quiz data
//            startActivity(intent);
        });

        // Review Quiz Example
        reviewQuizButton.setOnClickListener(v -> {
//            Intent intent = new Intent(QuizListActivity.this, ReviewActivity.class);
//            intent.putExtra("quizId", 1);
//            startActivity(intent);
        });
    }
}
