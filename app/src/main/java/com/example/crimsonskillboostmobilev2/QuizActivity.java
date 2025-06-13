package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_content);

        initViews();

        // Retrieve quizId from Intent
        String quizId = getIntent().getStringExtra("quizId");
        if (quizId != null && !quizId.isEmpty()) {
            fetchQuestionsFromFirebase(quizId); // Pass quizId as a String
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
                            // Log raw document data
                            Log.d("QuizActivity", "Document Data: " + document.getData());

                            // Retrieve fields from Firestore
                            String questionText = document.getString("question_text");
                            List<String> options = (List<String>) document.get("options");
                            Object correctAnswerObj = document.get("correct_answer");
                            String correctAnswer = correctAnswerObj != null ? correctAnswerObj.toString() : null;

                            // Validate and map options
                            if (options != null && options.size() == 4) {
                                String option1 = options.get(0);
                                String option2 = options.get(1);
                                String option3 = options.get(2);
                                String option4 = options.get(3);

                                // Validate and add to question list
                                if (questionText != null && correctAnswer != null) {
                                    QuestionModel question = new QuestionModel();
                                    question.setQuestion(questionText);
                                    question.setOption1(option1);
                                    question.setOption2(option2);
                                    question.setOption3(option3);
                                    question.setOption4(option4);
                                    question.setCorrectAnswer(correctAnswer);
                                    questionList.add(question);

                                    // Log added question
                                    Log.d("QuizActivity", "Added Question: " + question.getQuestion());
                                } else {
                                    Log.w("QuizActivity", "Invalid Question Data: " + document.getId());
                                }
                            } else {
                                Log.w("QuizActivity", "Invalid Options Data: " + document.getId());
                            }
                        } catch (Exception e) {
                            Log.e("QuizActivity", "Error processing document: " + document.getId(), e);
                        }
                    }
                    // Log final question list
                    Log.d("QuizActivity", "Final Question List: " + questionList);

                    if (questionList.isEmpty()) {
                        Toast.makeText(this, "No valid questions found for this quiz.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        displayQuestion();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("QuizActivity", "Failed to load questions: " + e.getMessage(), e);
                    Toast.makeText(this, "Failed to load questions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questionList.size()) {
            QuestionModel currentQuestion = questionList.get(currentQuestionIndex);

            // Log current question
            Log.d("QuizActivity", "Displaying Question: " + currentQuestion.getQuestion());

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
            String selectedAnswer = "";

            // Determine which option was clicked
            if (v.getId() == R.id.option1) {
                selectedAnswer = currentQuestion.getOption1();
            } else if (v.getId() == R.id.option2) {
                selectedAnswer = currentQuestion.getOption2();
            } else if (v.getId() == R.id.option3) {
                selectedAnswer = currentQuestion.getOption3();
            } else if (v.getId() == R.id.option4) {
                selectedAnswer = currentQuestion.getOption4();
            }

            // Log values for debugging
            Log.d("QuizActivity", "Selected Answer: " + selectedAnswer);
            Log.d("QuizActivity", "Correct Answer: " + currentQuestion.getCorrectAnswer());

            // Check if the selected answer is correct
            if (selectedAnswer.trim().equalsIgnoreCase(currentQuestion.getCorrectAnswer().trim())) {
                score++;
            }

            // Proceed to the next question or finish the quiz
            if (currentQuestionIndex < questionList.size() - 1) {
                currentQuestionIndex++;
                displayQuestion();
            } else {
                // Navigate to ResultActivity
                Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                intent.putExtra("score", score);
                intent.putExtra("maxScore", questionList.size());
                startActivity(intent);
                finish();
            }
        };

        option1.setOnClickListener(listener);
        option2.setOnClickListener(listener);
        option3.setOnClickListener(listener);
        option4.setOnClickListener(listener);
    }
}