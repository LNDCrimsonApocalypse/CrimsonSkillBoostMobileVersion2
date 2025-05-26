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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadCourses extends RecyclerView.Adapter<LoadCourses.CourseViewHolder> {

    private final Context context;
    private final List<CourseModel> courseList = new ArrayList<>();
    private final RecyclerView recyclerView;

    public LoadCourses(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.recyclerView.setAdapter(this); // Attach adapter
        fetchCoursesFromApi(); // Trigger API fetch
    }

    public void fetchCoursesFromApi() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<CourseModel>> call = apiService.getCourses();

        call.enqueue(new Callback<List<CourseModel>>() {
            @Override
            public void onResponse(Call<List<CourseModel>> call, Response<List<CourseModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    courseList.clear();
                    courseList.addAll(response.body());
                    Log.d("API_RESPONSE", "Courses loaded: " + courseList.size());

                    for (CourseModel c : courseList) {
                        Log.d("COURSE_DEBUG", "Title: " + c.getTitle() + ", Overview: " + c.getOverview());
                    }

                    notifyDataSetChanged();
                } else {
                    String errorMsg = "Empty or bad response";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.e("API_RESPONSE", "Unsuccessful: code=" + response.code());
                    Log.e("API_RESPONSE", "Error body: " + errorMsg);
                    Toast.makeText(context, "Failed: " + errorMsg, Toast.LENGTH_SHORT).show();
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

        // Safeguard: check for nulls
        String title = course.getTitle() != null ? course.getTitle() : "Untitled";
        String overview = course.getOverview() != null ? course.getOverview() : "No description.";

        holder.courseTitle.setText(title);
        holder.courseOverview.setText(overview);
        holder.progressBar.setProgress(course.getProgress());
        holder.progressPercent.setText(course.getProgress() + "%");

        holder.pendingIcon.setVisibility(course.isPending() ? View.VISIBLE : View.GONE);

        // Optional safeguard: fallback image if iconResId is 0
        int iconResId = course.getIconResId() != 0 ? course.getIconResId() : R.drawable.gamedev_icon;
        holder.subjectIcon.setImageResource(iconResId);
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
