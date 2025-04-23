package com.example.crimsonskillboostmobilev2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LoadCourses extends RecyclerView.Adapter<LoadCourses.CourseViewHolder> {

    private Context context;
    private List<CourseModel> courseList;

    public LoadCourses(Context context, List<CourseModel> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.loadcourses, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        CourseModel course = courseList.get(position);
        holder.courseTitle.setText(course.getTitle());
        holder.courseOverview.setText(course.getOverview());
        holder.progressBar.setProgress(course.getProgress());
        holder.progressPercent.setText(course.getProgress() + "%");

        // Show/hide pending icon based on course status
        if (course.isPending()) {
            holder.pendingIcon.setVisibility(View.VISIBLE);
        } else {
            holder.pendingIcon.setVisibility(View.GONE);
        }

        // Set icon (assuming drawable resource ID is stored in model)
        holder.subjectIcon.setImageResource(course.getIconResId());
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {

        TextView courseTitle, courseOverview, progressPercent;
        ProgressBar progressBar;
        ImageView subjectIcon, pendingIcon;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTitle = itemView.findViewById(R.id.textViewCourseTitle);
            courseOverview = itemView.findViewById(R.id.textViewCourseOverview);
            progressBar = itemView.findViewById(R.id.progressBarCourse);
            progressPercent = itemView.findViewById(R.id.textViewProgressPercent);
            subjectIcon = itemView.findViewById(R.id.imageViewSubjectIcon);
            pendingIcon = itemView.findViewById(R.id.imageViewPending);
        }
    }
}

