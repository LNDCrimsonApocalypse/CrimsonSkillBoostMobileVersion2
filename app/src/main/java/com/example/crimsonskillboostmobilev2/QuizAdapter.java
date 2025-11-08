package com.example.crimsonskillboostmobilev2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {

    private List<QuizModel> quizList;
    private Context context;

    private FirebaseFirestore db;
    private String currentUserId;

    public QuizAdapter(List<QuizModel> quizList, Context context) {
        this.quizList = quizList != null ? quizList : new ArrayList<>();
        this.context = context;

        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            currentUserId = null;
        }
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quiz_item, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        QuizModel quiz = quizList.get(position);

        holder.quizTitle.setText(quiz.getTitle());
        holder.quizDescription.setText(quiz.getDescription());

        // Display due date
        String endDate = quiz.getEndDate();
        if (endDate != null && !endDate.isEmpty()) {
            holder.quizDueDate.setText("Due on: " + endDate);
        } else {
            holder.quizDueDate.setText("No due date");
        }

        // Handle prerequisite logic
        String requiredQuizId = quiz.getRequiredQuiz();

        if (requiredQuizId == null || requiredQuizId.isEmpty()) {
            // ✅ No prerequisite — enable immediately
            enableQuiz(holder, quiz);
        } else if (currentUserId == null) {
            // ❌ No user logged in
            lockQuiz(holder);
        } else {
            // 🔒 Check if user completed the required quiz
            CollectionReference submissionsRef = db.collection("quizzes")
                    .document(requiredQuizId)
                    .collection("submissions");

            Query query = submissionsRef.whereEqualTo("userId", currentUserId).limit(1);

            query.get().addOnSuccessListener(querySnapshot -> {
                if (!querySnapshot.isEmpty()) {
                    // ✅ User has a submission for the required quiz — unlock
                    enableQuiz(holder, quiz);
                } else {
                    // 🔒 No submission found — lock
                    lockQuiz(holder);
                }
            }).addOnFailureListener(e -> {
                // 🔒 Error fetching data — keep locked
                lockQuiz(holder);
            });
        }
    }

    private void enableQuiz(QuizViewHolder holder, QuizModel quiz) {
        holder.startQuizButton.setEnabled(true);
        holder.startQuizButton.setText("Start Quiz");
        holder.startQuizButton.setBackgroundColor(Color.parseColor("#4CAF50")); // green

        holder.startQuizButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, QuizActivity.class);
            intent.putExtra("quizId", quiz.getId());
            context.startActivity(intent);
        });
    }

    private void lockQuiz(QuizViewHolder holder) {
        holder.startQuizButton.setEnabled(false);
        holder.startQuizButton.setText("Locked");
        holder.startQuizButton.setBackgroundColor(Color.GRAY);
        holder.startQuizButton.setOnClickListener(v ->
                Toast.makeText(context, "You must complete the required quiz first.", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView quizTitle, quizDescription, quizDueDate;
        Button startQuizButton;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            quizTitle = itemView.findViewById(R.id.quizTitle);
            quizDescription = itemView.findViewById(R.id.quizDescription);
            quizDueDate = itemView.findViewById(R.id.quizDueDate);
            startQuizButton = itemView.findViewById(R.id.startQuizButton);
        }
    }
}
