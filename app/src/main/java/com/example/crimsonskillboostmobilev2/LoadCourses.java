package com.example.crimsonskillboostmobilev2;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class LoadCourses extends RecyclerView.Adapter<LoadCourses.CourseViewHolder> {

    private final Context context;
    private final RecyclerView recyclerView;

    private final String mode; // "available" or "enrolled"
    private final String studentId;

    private final List<CourseModel> courseList = new ArrayList<>();

    public LoadCourses(Context context, RecyclerView recyclerView, String mode, String studentId) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.mode = mode;
        this.studentId = studentId;
        this.recyclerView.setAdapter(this);
        fetchCoursesFromFirestore();
    }

    public void fetchCoursesFromFirestore() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String collection = mode.equals("enrolled") ? "enrolled_courses" : "available_courses";

        firestore.collection(collection)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    courseList.clear();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        CourseModel course = new CourseModel();
                        course.setTitle(document.getString("course_name"));
                        course.setOverview(document.getString("overview"));
                        course.setInstructorName(document.getString("instructor_name"));
                        course.setRequirements(document.getString("requirements"));
                        course.setCreatedAt(document.getTimestamp("created_at"));
                        courseList.add(course);
                    }
                    notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("LoadCourses", "Error fetching courses: " + e.getMessage(), e));
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

        // Pass course_id to SubjectDetailsAvailableCourse
        if (mode.equals("available")) {
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, SubjectDetailsAvailableCourse.class);
                intent.putExtra("course_id", course.getId()); // Pass Firestore document ID
                intent.putExtra("title", course.getTitle());
                intent.putExtra("instructor_name", course.getInstructorName());
                intent.putExtra("overview", course.getOverview());
                intent.putExtra("requirements", course.getRequirements());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseTitle, courseOverview;
        ImageView subjectIcon;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTitle = itemView.findViewById(R.id.textViewCourseTitle);
            courseOverview = itemView.findViewById(R.id.textViewCourseOverview);
            subjectIcon = itemView.findViewById(R.id.imageViewSubjectIcon);
        }
    }
}