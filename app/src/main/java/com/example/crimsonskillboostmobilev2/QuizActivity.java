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

    private Button option1, option2, option3, option4, btnNext;
    private List<QuestionModel> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;
    private String selectedAnswer;
    private TextView subjectTitle, textQuestionNumber, textQuestion;
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

        checkQuizCompletion(quizId);

        btnNext.setOnClickListener(v -> {
            if (questionList == null || questionList.isEmpty()) {
                Log.e("QuizActivity", "Question list is empty or null");
                Toast.makeText(this, "No questions available.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentQuestionIndex < questionList.size()) {
                QuestionModel currentQuestion = questionList.get(currentQuestionIndex);

                if (selectedAnswer != null && selectedAnswer.equals(currentQuestion.getOptions().get(currentQuestion.getCorrectOption()))) {
                    score += currentQuestion.getPoints();
                }

                if (currentQuestionIndex < questionList.size() - 1) {
                    currentQuestionIndex++;
                    displayQuestion();
                    resetOptions();
                } else {
                    finishQuiz(quizId);
                }
            } else {
                Toast.makeText(this, "Please select an answer before proceeding.", Toast.LENGTH_SHORT).show();
            }
        });

        setOptionClickListeners();
    }

    private void resetOptions() {
        option1.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        option2.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        option3.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        option4.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        selectedAnswer = null;
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
                            int points = document.getLong("points") != null ? document.getLong("points").intValue() : 1;

                            if (questionText != null && options != null && options.size() == 4) {
                                QuestionModel question = new QuestionModel();
                                question.setQuestion(questionText);
                                question.setOptions(options);
                                question.setCorrectOption(correctOption);
                                question.setPoints(points);
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
                        finish();
                    } else {
                        displayQuestion();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load questions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

            option1.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            option2.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            option3.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            option4.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

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

    private void finishQuiz(String quizId) {
        submitQuiz(quizId);

        int totalPossiblePoints = 0;
        for (QuestionModel question : questionList) {
            totalPossiblePoints += question.getPoints();
        }

        Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("maxScore", totalPossiblePoints);
        startActivity(intent);
        finish();
    }

    private void submitQuiz(String quizId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser != null ? currentUser.getUid() : "anonymous";

        int totalPossiblePoints = 0;
        List<QuestionSubmissionModel> questionSubmissions = new ArrayList<>();
        for (QuestionModel question : questionList) {
            totalPossiblePoints += question.getPoints();

            QuestionSubmissionModel questionSubmission = new QuestionSubmissionModel(
                    question.getQuestion(),
                    question.getOptions(),
                    question.getCorrectOption(),
                    selectedAnswer,
                    selectedAnswer != null && selectedAnswer.equals(question.getOptions().get(question.getCorrectOption()))
            );
            questionSubmissions.add(questionSubmission);
        }

        SubmissionModel submission = new SubmissionModel(score, totalPossiblePoints, System.currentTimeMillis(), userId, questionSubmissions);

        firestore.collection("quizzes")
                .document(quizId)
                .collection("submissions")
                .document(userId)
                .set(submission)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Quiz submitted.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error submitting quiz: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}