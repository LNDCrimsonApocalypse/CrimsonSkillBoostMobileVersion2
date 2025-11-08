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

public class QuizzesAdapter extends RecyclerView.Adapter<QuizzesAdapter.QuizzesViewHolder> {

    private List<QuizModel> quizList;
    private Context context;
    private String courseId;

    private FirebaseFirestore db;
    private String currentUserId;

    public QuizzesAdapter(List<QuizModel> quizList, Context context, String courseId) {
        this.quizList = quizList != null ? quizList : new ArrayList<>();
        this.context = context;
        this.courseId = courseId;

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public QuizzesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quiz_item, parent, false);
        return new QuizzesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizzesViewHolder holder, int position) {
        QuizModel quiz = quizList.get(position);

        holder.quizTitle.setText(quiz.getTitle());
        holder.quizDescription.setText(quiz.getDescription());
        String endDate = quiz.getEndDate();
        holder.quizDueDate.setText((endDate != null && !endDate.isEmpty()) ? "Due on: " + endDate : "No due date");

        String requiredQuizId = quiz.getRequiredQuiz();

        if (requiredQuizId == null || requiredQuizId.isEmpty()) {
            // ✅ No prerequisite
            enableQuiz(holder, quiz);
        } else {
            // 🔒 Check if the user has a submission for the required quiz
            CollectionReference submissionsRef = db.collection("quizzes")
                    .document(requiredQuizId)
                    .collection("submissions");

            Query query = submissionsRef.whereEqualTo("userId", currentUserId).limit(1);

            query.get().addOnSuccessListener(querySnapshot -> {
                if (!querySnapshot.isEmpty()) {
                    // ✅ User has completed the required quiz
                    enableQuiz(holder, quiz);
                } else {
                    // 🔒 User hasn’t completed it yet
                    lockQuiz(holder);
                }
            }).addOnFailureListener(e -> {
                lockQuiz(holder);
            });
        }
    }

    private void enableQuiz(QuizzesViewHolder holder, QuizModel quiz) {
        holder.startQuizButton.setEnabled(true);
        holder.startQuizButton.setText("Start Quiz");
        holder.startQuizButton.setBackgroundColor(Color.parseColor("#4CAF50")); // green
        holder.startQuizButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, QuizActivity.class);
            intent.putExtra("quizId", quiz.getId());
            intent.putExtra("courseId", courseId);
            context.startActivity(intent);
        });
    }

    private void lockQuiz(QuizzesViewHolder holder) {
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

    public void updateQuizzes(List<QuizModel> newQuizzes) {
        quizList.clear();
        quizList.addAll(newQuizzes);
        notifyDataSetChanged();
    }

    static class QuizzesViewHolder extends RecyclerView.ViewHolder {
        TextView quizTitle, quizDescription, quizDueDate;
        Button startQuizButton;

        public QuizzesViewHolder(@NonNull View itemView) {
            super(itemView);
            quizTitle = itemView.findViewById(R.id.quizTitle);
            quizDescription = itemView.findViewById(R.id.quizDescription);
            quizDueDate = itemView.findViewById(R.id.quizDueDate);
            startQuizButton = itemView.findViewById(R.id.startQuizButton);
        }
    }
}
