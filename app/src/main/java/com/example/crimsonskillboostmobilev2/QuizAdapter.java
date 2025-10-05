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

import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {

    private List<QuizModel> quizList;
    private Context context;

    public QuizAdapter(List<QuizModel> quizList, Context context) {
        this.quizList = quizList;
        this.context = context;
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

        // Format and display the end_date
        String endDate = quiz.getEndDate();
        if (endDate != null && !endDate.isEmpty()) {
            holder.quizDueDate.setText("Due on: " + endDate);
            holder.quizDueDate.setVisibility(View.VISIBLE);
        } else {
            holder.quizDueDate.setText("No due date");
            holder.quizDueDate.setVisibility(View.VISIBLE);
        }

        // Set click listener for the "Start Quiz" button
        holder.startQuizButton.setOnClickListener(v -> {
            String quizId = quiz.getId();
            Intent intent = new Intent(context, QuizActivity.class);
            intent.putExtra("quizId", quizId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView quizTitle, quizDescription, quizDueDate; // Add quizDueDate
        Button startQuizButton;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            quizTitle = itemView.findViewById(R.id.quizTitle);
            quizDescription = itemView.findViewById(R.id.quizDescription);
            quizDueDate = itemView.findViewById(R.id.quizDueDate); // Initialize quizDueDate
            startQuizButton = itemView.findViewById(R.id.startQuizButton);
        }
    }
}