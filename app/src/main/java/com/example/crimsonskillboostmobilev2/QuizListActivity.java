package com.example.crimsonskillboostmobilev2;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class QuizListActivity extends AppCompatActivity {

    private RecyclerView quizRecyclerView;
    private TextView emptyStateText;
    private List<QuizModel> quizList = new ArrayList<>();
    private QuizAdapter quizAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_list);

        // Enable back button on the ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true); // Show back arrow
            actionBar.setHomeButtonEnabled(true); // Ensure the back button is clickable
            actionBar.setTitle("Quizzes"); // Optional: set title
        }

        quizRecyclerView = findViewById(R.id.quizRecyclerView);
        emptyStateText = findViewById(R.id.emptyStateText);

        setupRecyclerView();
        fetchQuizzesFromFirebase();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle back button click
            finish(); // Close the current activity and return to the previous one
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        quizAdapter = new QuizAdapter(quizList, this);
        quizRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        quizRecyclerView.setAdapter(quizAdapter);
    }

    private void fetchQuizzesFromFirebase() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
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
                        quizList.add(quiz);
                    }
                    quizAdapter.notifyDataSetChanged();
                    toggleEmptyState();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching quizzes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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
