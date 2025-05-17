package com.example.crimsonskillboostmobilev2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadCourses extends RecyclerView.Adapter<LoadCourses.CourseViewHolder> {

    private Context context;
    private List<CourseModel> courseList = new ArrayList<>();
    private RecyclerView recyclerView;

    public LoadCourses(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
        fetchCoursesFromApi(); // Load data on initialization
    }

    private void fetchCoursesFromApi() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<CourseModel>> call = apiService.getCourses();

        call.enqueue(new Callback<List<CourseModel>>() {
            @Override
            public void onResponse(Call<List<CourseModel>> call, Response<List<CourseModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    courseList = response.body();
                    notifyDataSetChanged(); // Refresh adapter
                    recyclerView.setAdapter(LoadCourses.this); // Set adapter after loading
                } else {
                    Toast.makeText(context, "Empty or bad response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CourseModel>> call, Throwable t) {
                Toast.makeText(context, "Failed to load courses: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LoadCourses", "API error", t);
            }
        });
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

        if (course.isPending()) {
            holder.pendingIcon.setVisibility(View.VISIBLE);
        } else {
            holder.pendingIcon.setVisibility(View.GONE);
        }

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
