package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private TextView subjectTitle, textQuestionNumber, textQuestion;
    private Button option1, option2, option3, option4, btnNext;
    private List<QuestionModel> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;
    private String selectedAnswer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_content);

        initViews();

        // Retrieve quizId from Intent
        String quizId = getIntent().getStringExtra("quizId");
        if (quizId == null || quizId.isEmpty()) {
            Toast.makeText(this, "Invalid Quiz ID", Toast.LENGTH_SHORT).show();
            Log.e("QuizActivity", "Quiz ID is null or empty");
            finish();
            return;
        }

        checkQuizCompletion(quizId); // Check completion before fetching questions

        btnNext.setOnClickListener(v -> {
            if (currentQuestionIndex < questionList.size() - 1) {
                currentQuestionIndex++;
                displayQuestion();
            } else {
                finishQuiz(quizId); // Mark quiz as completed and navigate to ResultActivity
            }
        });

        setOptionClickListeners();
    }

    private void checkQuizCompletion(String quizId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("quizzes")
                .document(quizId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Boolean isCompleted = documentSnapshot.getBoolean("completed");
                        Long maxAttempts = documentSnapshot.getLong("max_attempts");
                        Long currentAttempts = documentSnapshot.getLong("attempts");

                        if (isCompleted != null && isCompleted) {
                            Toast.makeText(this, "This quiz is already completed and cannot be retaken.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else if (currentAttempts != null && maxAttempts != null && currentAttempts >= maxAttempts) {
                            Toast.makeText(this, "You have reached the maximum number of attempts for this quiz.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            fetchQuestionsFromFirebase(quizId);
                        }
                    } else {
                        Toast.makeText(this, "Quiz not found.", Toast.LENGTH_SHORT).show();
                        Log.e("QuizActivity", "Quiz document does not exist");
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error checking quiz completion: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("QuizActivity", "Error checking quiz completion", e);
                    finish();
                });
    }

    private void fetchQuestionsFromFirebase(String quizId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("quizzes")
                .document(quizId)
                .collection("questions")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    questionList.clear();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        try {
                            String questionText = document.getString("question_text");
                            List<String> options = (List<String>) document.get("options");
                            Object correctAnswerObj = document.get("correct_answer");
                            String correctAnswer = correctAnswerObj != null ? correctAnswerObj.toString() : null;

                            if (questionText != null && options != null && options.size() == 4 && correctAnswer != null) {
                                QuestionModel question = new QuestionModel();
                                question.setQuestion(questionText);
                                question.setOption1(options.get(0));
                                question.setOption2(options.get(1));
                                question.setOption3(options.get(2));
                                question.setOption4(options.get(3));
                                question.setCorrectAnswer(correctAnswer);
                                questionList.add(question);
                            } else {
                                Log.w("QuizActivity", "Invalid question data: " + document.getId());
                            }
                        } catch (Exception e) {
                            Log.e("QuizActivity", "Error processing document: " + document.getId(), e);
                        }
                    }

                    if (questionList.isEmpty()) {
                        Toast.makeText(this, "No valid questions found for this quiz.", Toast.LENGTH_SHORT).show();
                        Log.e("QuizActivity", "No valid questions found");
                        finish();
                    } else {
                        displayQuestion();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load questions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("QuizActivity", "Failed to load questions", e);
                    finish();
                });
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questionList.size()) {
            QuestionModel currentQuestion = questionList.get(currentQuestionIndex);
            textQuestion.setText(currentQuestion.getQuestion());
            option1.setText(currentQuestion.getOption1());
            option2.setText(currentQuestion.getOption2());
            option3.setText(currentQuestion.getOption3());
            option4.setText(currentQuestion.getOption4());
            textQuestionNumber.setText("Question " + (currentQuestionIndex + 1) + " of " + questionList.size());
        }
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

    private void setOptionClickListeners() {
        View.OnClickListener listener = v -> {
            QuestionModel currentQuestion = questionList.get(currentQuestionIndex);

            // Reset all buttons to default background
            option1.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            option2.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            option3.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            option4.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

            // Highlight the selected button
            if (v.getId() == R.id.option1) {
                selectedAnswer = currentQuestion.getOption1();
                option1.setBackgroundColor(getResources().getColor(R.color.purple_500));
            } else if (v.getId() == R.id.option2) {
                selectedAnswer = currentQuestion.getOption2();
                option2.setBackgroundColor(getResources().getColor(R.color.purple_500));
            } else if (v.getId() == R.id.option3) {
                selectedAnswer = currentQuestion.getOption3();
                option3.setBackgroundColor(getResources().getColor(R.color.purple_500));
            } else if (v.getId() == R.id.option4) {
                selectedAnswer = currentQuestion.getOption4();
                option4.setBackgroundColor(getResources().getColor(R.color.purple_500));
            }
        };

        option1.setOnClickListener(listener);
        option2.setOnClickListener(listener);
        option3.setOnClickListener(listener);
        option4.setOnClickListener(listener);

        btnNext.setOnClickListener(v -> {
            // Check if the selected answer is correct
            QuestionModel currentQuestion = questionList.get(currentQuestionIndex);
            if (selectedAnswer != null && selectedAnswer.trim().equalsIgnoreCase(currentQuestion.getCorrectAnswer().trim())) {
                score++;
            }

            // Move to the next question or finish the quiz
            if (currentQuestionIndex < questionList.size() - 1) {
                currentQuestionIndex++;
                displayQuestion();

                // Reset selectedAnswer and button states
                selectedAnswer = null;
                option1.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                option2.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                option3.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                option4.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            } else {
                finishQuiz(getIntent().getStringExtra("quizId"));
            }
        });
    }

    private void finishQuiz(String quizId) {
        markQuizAsCompleted(quizId);

        Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("maxScore", questionList.size());
        startActivity(intent);
        finish();
    }

    private void markQuizAsCompleted(String quizId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("quizzes")
                .document(quizId)
                .update("completed", true, "attempts", FieldValue.increment(1))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Quiz marked as completed.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error marking quiz as completed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("QuizActivity", "Error marking quiz as completed", e);
                });
    }
}