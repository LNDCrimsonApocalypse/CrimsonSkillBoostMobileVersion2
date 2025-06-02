package com.example.crimsonskillboostmobilev2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, "Selected Quiz: " + quiz.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView quizTitle, quizDescription;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            quizTitle = itemView.findViewById(R.id.quizTitle);
            quizDescription = itemView.findViewById(R.id.quizDescription);
        }
    }
}