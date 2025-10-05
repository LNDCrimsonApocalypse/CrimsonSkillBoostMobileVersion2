package com.example.crimsonskillboostmobilev2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class QuizzesAdapter extends RecyclerView.Adapter<QuizzesAdapter.QuizzesViewHolder> {

    private List<QuizModel> quizList;
    private Context context; // Declare context
    private String courseId; // Declare courseId
    public QuizzesAdapter(List<QuizModel> quizList, Context context, String courseId) {
        this.quizList = quizList != null ? quizList : new ArrayList<>(); // Initialize quizList
        this.context = context;
        this.courseId = courseId;
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

        // Format and display the end_date
        String endDate = quiz.getEndDate();
        if (endDate != null && !endDate.isEmpty()) {
            holder.quizDueDate.setText("Due on: " + endDate);
            holder.quizDueDate.setVisibility(View.VISIBLE);
        } else {
            holder.quizDueDate.setText("No due date");
            holder.quizDueDate.setVisibility(View.VISIBLE);
        }

        holder.startQuizButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, QuizActivity.class);
            intent.putExtra("quizId", quiz.getId());
            intent.putExtra("courseId", courseId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public void updateQuizzes(List<QuizModel> newQuizzes) {
        if (quizList == null) {
            quizList = new ArrayList<>();
        }
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