package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizListActivity extends AppCompatActivity {

    private LinearLayout quizListContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_list);

        quizListContainer = findViewById(R.id.quizListContainer);

        // Back button functionality
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish()); // Navigate back to the previous activity

        fetchQuizzes();
    }

    private void fetchQuizzes() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getQuizzes().enqueue(new Callback<List<QuizModel>>() {
            @Override
            public void onResponse(Call<List<QuizModel>> call, Response<List<QuizModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<QuizModel> quizzes = response.body();
                    populateQuizList(quizzes);
                } else {
                    Toast.makeText(QuizListActivity.this, "Failed to load quizzes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<QuizModel>> call, Throwable t) {
                Toast.makeText(QuizListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateQuizList(List<QuizModel> quizzes) {
        quizListContainer.removeAllViews(); // Clear existing views

        for (QuizModel quiz : quizzes) {
            View quizItemView = getLayoutInflater().inflate(R.layout.quiz_item, quizListContainer, false);

            TextView quizTitle = quizItemView.findViewById(R.id.quizTitle);
            TextView quizDescription = quizItemView.findViewById(R.id.quizDescription);

            quizTitle.setText(quiz.getTitle());
            quizDescription.setText(quiz.getDescription());

            Button startQuizButton = quizItemView.findViewById(R.id.startQuizButton);
            startQuizButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, QuizActivity.class);
                intent.putExtra("quizId", quiz.getId()); // Pass the selected quiz ID
                startActivity(intent);
            });

            quizListContainer.addView(quizItemView);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed(); // This will return to the previous activity
    }
}