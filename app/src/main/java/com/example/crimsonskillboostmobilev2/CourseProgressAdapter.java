package com.example.crimsonskillboostmobilev2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CourseProgressAdapter extends RecyclerView.Adapter<CourseProgressAdapter.ViewHolder> {

    private List<CourseProgress> progressList;

    public CourseProgressAdapter(List<CourseProgress> progressList) {
        this.progressList = progressList;
    }

    @NonNull
    @Override
    public CourseProgressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_progress, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseProgressAdapter.ViewHolder holder, int position) {
        CourseProgress course = progressList.get(position);
        holder.courseTitle.setText(course.getTitle());
        holder.coursePercent.setText(course.getProgress() + " %");
        holder.courseBar.setProgress(course.getProgress());
    }

    @Override
    public int getItemCount() {
        return progressList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView courseTitle, coursePercent;
        ProgressBar courseBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTitle = itemView.findViewById(R.id.textCourseTitle);
            coursePercent = itemView.findViewById(R.id.textProgressPercent);
            courseBar = itemView.findViewById(R.id.progressBar1);
        }
    }
}

