package com.example.crimsonskillboostmobilev2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    private TextView tvSubject, tvScore, tvResult, tvMessage;
    private ProgressBar progressRing;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_score);

        // Initialize Views
        tvSubject = findViewById(R.id.tvSubject);
        tvScore = findViewById(R.id.tvScore);
        tvResult = findViewById(R.id.tvResult);
        tvMessage = findViewById(R.id.tvMessage);
        progressRing = findViewById(R.id.progressRing);
        btnNext = findViewById(R.id.btnNext);

        // Retrieve score and maxScore from Intent
        int score = getIntent().getIntExtra("score", 0);
        int maxScore = getIntent().getIntExtra("maxScore", 0);

        // Set score dynamically
        progressRing.setMax(maxScore);
        progressRing.setProgress(score);
        tvScore.setText(score + "/" + maxScore);

        // Set result message based on score
        if (score == maxScore) {
            tvResult.setText("Outstanding!");
            tvMessage.setText("You got everything right, our hard work really shows!!");
        } else if (score >= maxScore * 0.8) {
            tvResult.setText("Great Job!");
            tvMessage.setText("You did well, just a few mistakes.");
        } else {
            tvResult.setText("Keep Going!");
            tvMessage.setText("Review your answers and try again.");
        }

        // Handle Finish button click
        btnNext.setOnClickListener(v -> {
            finish(); // Close activity or navigate back
        });
    }
}

