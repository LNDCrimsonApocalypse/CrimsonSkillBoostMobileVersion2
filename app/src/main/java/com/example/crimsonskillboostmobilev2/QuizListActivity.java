package com.example.crimsonskillboostmobilev2;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class QuizListActivity extends AppCompatActivity {

    private RecyclerView quizRecyclerView;
    private TextView emptyStateText;
    private List<QuizModel> quizList = new ArrayList<>();
    private QuizAdapter quizAdapter;
    private FirebaseFirestore firestore;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_list);

        // ✅ Back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // ✅ RecyclerView and empty state
        quizRecyclerView = findViewById(R.id.quizRecyclerView);
        emptyStateText = findViewById(R.id.emptyStateText);

        firestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        setupRecyclerView();
        fetchQuizzesFromFirebase();
    }

    private void setupRecyclerView() {
        quizAdapter = new QuizAdapter(quizList, this);
        quizRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        quizRecyclerView.setAdapter(quizAdapter);
    }

    private void fetchQuizzesFromFirebase() {
        firestore.collection("quizzes")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    quizList.clear();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        QuizModel quiz = new QuizModel();
                        quiz.setId(document.getId());
                        quiz.setTitle(document.getString("title"));
                        quiz.setDescription(document.getString("description"));
                        quiz.setCourseId(document.getString("course_id"));
                        quiz.setCreatedAt(document.getString("created_at"));
                        quiz.setPublished(document.getBoolean("published") != null ? document.getBoolean("published") : false);
                        quiz.setPublishedAt(document.getString("published_at"));
                        quiz.setAttempts(document.getLong("attempts") != null ? document.getLong("attempts").intValue() : 0);
                        quiz.setCompleted(document.getBoolean("completed") != null ? document.getBoolean("completed") : false);
                        quiz.setStartDate(document.getString("start_date"));
                        quiz.setEndDate(document.getString("end_date"));
                        quiz.setAllowLate(document.getBoolean("allow_late") != null ? document.getBoolean("allow_late") : false);
                        quiz.setRequiredQuiz(document.getString("requiredQuiz")); // ✅ Add prerequisite field

                        quizList.add(quiz);
                    }

                    // Once all quizzes are loaded, verify prerequisites
                    checkLockedQuizzes(querySnapshot);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching quizzes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * ✅ Checks which quizzes should be locked based on requiredQuiz completion.
     */
    private void checkLockedQuizzes(QuerySnapshot querySnapshot) {
        if (userId == null) {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (QuizModel quiz : quizList) {
            String requiredQuizId = quiz.getRequiredQuiz();
            if (requiredQuizId != null && !requiredQuizId.isEmpty()) {
                // Check if user has submitted a result for that required quiz
                firestore.collection("quizzes")
                        .document(requiredQuizId)
                        .collection("submissions")
                        .whereEqualTo("userId", userId)
                        .get()
                        .addOnSuccessListener(submissionSnapshot -> {
                            boolean completed = !submissionSnapshot.isEmpty();
                            quiz.setLocked(!completed); // lock if no submission found
                            quizAdapter.notifyDataSetChanged();
                        })
                        .addOnFailureListener(e -> {
                            quiz.setLocked(true);
                            quizAdapter.notifyDataSetChanged();
                        });
            } else {
                quiz.setLocked(false); // no prerequisite, always open
            }
        }

        quizAdapter.notifyDataSetChanged();
        toggleEmptyState();
    }

    private void toggleEmptyState() {
        if (quizList.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            quizRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(View.GONE);
            quizRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
