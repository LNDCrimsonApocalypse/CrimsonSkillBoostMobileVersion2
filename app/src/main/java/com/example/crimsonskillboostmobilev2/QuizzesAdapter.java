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

public class QuizzesAdapter extends RecyclerView.Adapter<QuizzesAdapter.QuizzesViewHolder> {

    private List<QuizModel> quizList;
    private Context context;
    private String courseId;

    public QuizzesAdapter(List<QuizModel> quizList, Context context, String courseId) {
        this.quizList = quizList;
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

        holder.startQuizButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, QuizActivity.class);
            intent.putExtra("quizId", quiz.getId());
            intent.putExtra("courseId", courseId); // keep course context
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    // 👇 This is the important part
    public void updateQuizzes(List<QuizModel> newQuizzes) {
        this.quizList.clear();
        this.quizList.addAll(newQuizzes);
        notifyDataSetChanged();
    }

    static class QuizzesViewHolder extends RecyclerView.ViewHolder {
        TextView quizTitle, quizDescription;
        Button startQuizButton;

        public QuizzesViewHolder(@NonNull View itemView) {
            super(itemView);
            quizTitle = itemView.findViewById(R.id.quizTitle);
            quizDescription = itemView.findViewById(R.id.quizDescription);
            startQuizButton = itemView.findViewById(R.id.startQuizButton);
        }
    }
}
