package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String userId = currentUser != null ? currentUser.getUid() : "anonymous";
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
            if (currentQuestionIndex < questionList.size()) {
                QuestionModel currentQuestion = questionList.get(currentQuestionIndex);

                // Check if the selected answer matches the correct option
                if (selectedAnswer != null && selectedAnswer.equals(currentQuestion.getOptions().get(currentQuestion.getCorrectOption()))) {
                    score += currentQuestion.getPoints(); // Add points for correct answer
                }

                // Move to the next question or finish the quiz
                if (currentQuestionIndex < questionList.size() - 1) {
                    currentQuestionIndex++;
                    displayQuestion();
                } else {
                    finishQuiz(quizId); // Mark quiz as completed and navigate to ResultActivity
                }
            } else {
                Toast.makeText(this, "Please select an answer before proceeding.", Toast.LENGTH_SHORT).show();
            }
        });

        setOptionClickListeners();
    }

    private void checkQuizCompletion(String quizId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser != null ? currentUser.getUid() : "anonymous";

        firestore.collection("quizzes")
                .document(quizId)
                .collection("submissions")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Toast.makeText(this, "You have already completed this quiz.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        fetchQuestionsFromFirebase(quizId);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error checking submission: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            String questionText = document.getString("question");
                            List<String> options = (List<String>) document.get("options");
                            int correctOption = document.getLong("correct_option").intValue();
                            int points = document.getLong("points") != null ? document.getLong("points").intValue() : 1; // Default to 1 if null

                            if (questionText != null && options != null && options.size() == 4) {
                                QuestionModel question = new QuestionModel();
                                question.setQuestion(questionText);
                                question.setOptions(options);
                                question.setCorrectOption(correctOption);
                                question.setPoints(points); // Set points
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
            option1.setText(currentQuestion.getOptions().get(0));
            option2.setText(currentQuestion.getOptions().get(1));
            option3.setText(currentQuestion.getOptions().get(2));
            option4.setText(currentQuestion.getOptions().get(3));
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
                selectedAnswer = currentQuestion.getOptions().get(0);
                option1.setBackgroundColor(getResources().getColor(R.color.purple_500));
            } else if (v.getId() == R.id.option2) {
                selectedAnswer = currentQuestion.getOptions().get(1);
                option2.setBackgroundColor(getResources().getColor(R.color.purple_500));
            } else if (v.getId() == R.id.option3) {
                selectedAnswer = currentQuestion.getOptions().get(2);
                option3.setBackgroundColor(getResources().getColor(R.color.purple_500));
            } else if (v.getId() == R.id.option4) {
                selectedAnswer = currentQuestion.getOptions().get(3);
                option4.setBackgroundColor(getResources().getColor(R.color.purple_500));
            }
        };

        option1.setOnClickListener(listener);
        option2.setOnClickListener(listener);
        option3.setOnClickListener(listener);
        option4.setOnClickListener(listener);
    }

    // In QuizActivity.java

    private void finishQuiz(String quizId) {
        submitQuiz(quizId);

        // Calculate the actual maximum possible score
        int totalPossiblePoints = 0;
        for (QuestionModel question : questionList) {
            totalPossiblePoints += question.getPoints();
        }

        Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("maxScore", totalPossiblePoints); // Pass the correct total possible points
        startActivity(intent);
        finish();
    }

    private void submitQuiz(String quizId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser != null ? currentUser.getUid() : "anonymous";

        // Prepare submission data
        int totalPossiblePoints = 0;
        for (QuestionModel question : questionList) {
            totalPossiblePoints += question.getPoints();
        }

        SubmissionModel submission = new SubmissionModel(score, totalPossiblePoints, System.currentTimeMillis(), userId);

        int finalTotalPossiblePoints = totalPossiblePoints;
        firestore.collection("quizzes")
                .document(quizId)
                .collection("submissions")
                .document(userId)
                .set(submission)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Quiz submitted.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                    intent.putExtra("score", score);
                    intent.putExtra("maxScore", finalTotalPossiblePoints);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error submitting quiz: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("QuizActivity", "Error submitting quiz", e);
                });
    }

}