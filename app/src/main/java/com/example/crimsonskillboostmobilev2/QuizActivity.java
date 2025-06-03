package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizActivity extends AppCompatActivity {

    private TextView subjectTitle, textQuestionNumber, textQuestion;
    private Button option1, option2, option3, option4, btnNext;

    private List<QuestionModel> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_content);

        initViews();

        // Retrieve quizId from Intent
        int quizId = getIntent().getIntExtra("quizId", -1);
        Toast.makeText(this, "Received Quiz ID: " + quizId, Toast.LENGTH_SHORT).show(); // Debugging
        if (quizId != -1) {
            fetchQuestions(quizId); // Pass quizId to fetchQuestions
        } else {
            Toast.makeText(this, "Invalid Quiz ID", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnNext.setOnClickListener(v -> {
            if (currentQuestionIndex < questionList.size() - 1) {
                currentQuestionIndex++;
                displayQuestion();
            } else {
                Toast.makeText(this, "Quiz Completed! Your score: " + score, Toast.LENGTH_LONG).show();
                finish();
            }
        });

        setOptionClickListeners();
    }

    private void fetchQuestions(int quizId) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getQuizQuestions(quizId).enqueue(new Callback<List<QuestionModel>>() {
            @Override
            public void onResponse(Call<List<QuestionModel>> call, Response<List<QuestionModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<QuestionModel> allQuestions = response.body();

                    // Filter questions by quizId
                    questionList.clear();
                    for (QuestionModel question : allQuestions) {
                        if (question.getId() == quizId) {
                            questionList.add(question);
                        }
                    }

                    Log.d("QuizActivity", "Filtered Questions: " + questionList);

                    if (questionList.isEmpty()) {
                        Log.e("QuizActivity", "No questions found for quiz ID: " + quizId);
                        Toast.makeText(QuizActivity.this, "No questions available for this quiz.", Toast.LENGTH_SHORT).show();
                    } else {
                        displayQuestion(); // Display the first question
                    }
                } else {
                    Log.e("QuizActivity", "Failed to load questions. Response code: " + response.code());
                    Toast.makeText(QuizActivity.this, "Failed to load questions.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<QuestionModel>> call, Throwable t) {
                Log.e("QuizActivity", "Error fetching questions: " + t.getMessage(), t);
                Toast.makeText(QuizActivity.this, "Error fetching questions.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        subjectTitle = findViewById(R.id.subjectTitle);
        textQuestionNumber = findViewById(R.id.textQuestionNumber);
        textQuestion = findViewById(R.id.textQuestion);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        btnNext = findViewById(R.id.btnNext);
    }

    private void displayQuestion() {
        if (questionList.isEmpty()) {
            Log.e("QuizActivity", "Question list is empty.");
            return;
        }

        QuestionModel currentQuestion = questionList.get(currentQuestionIndex);
        List<String> options = currentQuestion.getOptions();

        // Debugging logs
        Log.d("QuizActivity", "Displaying Question: " + currentQuestion.getQuestionText());
        Log.d("QuizActivity", "Options: " + options);

        if (options != null && options.size() >= 4) {
            textQuestion.setText(currentQuestion.getQuestionText()); // Display question text
            option1.setText(options.get(0));
            option2.setText(options.get(1));
            option3.setText(options.get(2));
            option4.setText(options.get(3));
            textQuestionNumber.setText("Question " + (currentQuestionIndex + 1) + " of " + questionList.size());
        } else {
            Log.e("QuizActivity", "Invalid options data for question ID: " + currentQuestion.getId());
            Toast.makeText(this, "Error loading question options.", Toast.LENGTH_SHORT).show();
        }
    }

    private void finishQuiz() {
        Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
        intent.putExtra("score", score); // Pass the score
        intent.putExtra("maxScore", questionList.size()); // Pass the maximum score
        startActivity(intent);
        finish(); // Close the current activity
    }

    private void setOptionClickListeners() {
        View.OnClickListener listener = v -> {
            if (questionList.isEmpty()) {
                Toast.makeText(this, "No questions available", Toast.LENGTH_SHORT).show();
                return;
            }

            Button selectedButton = (Button) v;
            int selectedAnswerIndex = -1;

            // Get the index of the selected button
            if (selectedButton == option1) selectedAnswerIndex = 1;
            else if (selectedButton == option2) selectedAnswerIndex = 2;
            else if (selectedButton == option3) selectedAnswerIndex = 3;
            else if (selectedButton == option4) selectedAnswerIndex = 4;

            QuestionModel currentQuestion = questionList.get(currentQuestionIndex);

            // Debugging logs
            Log.d("QuizActivity", "Selected Answer Index: " + selectedAnswerIndex);
            Log.d("QuizActivity", "Correct Answer Index: " + currentQuestion.getCorrectAnswer());

            // Compare the indices
            if (selectedAnswerIndex == currentQuestion.getCorrectAnswer() + 1) {
                score++;
                Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
                if (currentQuestionIndex < questionList.size() - 1) {
                    currentQuestionIndex++;
                    displayQuestion(); // Proceed to next question
                } else {
                    finishQuiz(); // Redirect to ResultActivity
                }
            } else {
                Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show();
            }
        };

        option1.setOnClickListener(listener);
        option2.setOnClickListener(listener);
        option3.setOnClickListener(listener);
        option4.setOnClickListener(listener);
    }
}