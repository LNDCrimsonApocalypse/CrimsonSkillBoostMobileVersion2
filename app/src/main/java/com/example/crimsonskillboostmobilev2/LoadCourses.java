package com.example.crimsonskillboostmobilev2;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        String collection = mode.equals("enrolled") ? "enrolled_courses" : "courses";

        Log.d("LoadCourses", "Fetching courses from collection: " + collection);

        firestore.collection(collection)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    courseList.clear();
                    Log.d("LoadCourses", "Query successful. Number of documents: " + querySnapshot.size());
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        try {
                            Log.d("LoadCourses", "Processing document: " + document.getId());
                            CourseModel course = new CourseModel();
                            course.setCourseName(document.getString("course_name"));
                            course.setCreatedAt(document.getTimestamp("created_at"));
                            course.setInstructorName(document.getString("instructor_name"));
                            course.setOverview(document.getString("overview"));
                            course.setRequirements(document.getString("requirements"));

                            Log.d("LoadCourses", "Course details: " +
                                    "Name=" + course.getCourseName() +
                                    ", Instructor=" + course.getInstructorName() +
                                    ", Overview=" + course.getOverview());

                            courseList.add(course);
                        } catch (Exception e) {
                            Log.e("LoadCourses", "Error processing document: " + document.getId(), e);
                        }
                    }
                    notifyDataSetChanged();
                    Log.d("LoadCourses", "Courses loaded. Total courses: " + courseList.size());
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
        String courseName = course.getCourseName() != null ? course.getCourseName() : "Untitled Course";
        String overview = course.getOverview() != null ? course.getOverview() : "No overview available.";

        Log.d("LoadCourses", "Binding course to view: Name=" + courseName + ", Overview=" + overview);

        // Set course name and overview
        holder.courseTitle.setText(courseName);
        holder.courseDescription.setText(overview);

        // Set click listener based on mode
        holder.itemView.setOnClickListener(v -> {
            Intent intent;
            if (mode.equals("available")) {
                intent = new Intent(context, SubjectDetailsAvailableCourse.class);
            } else {
                intent = new Intent(context, SubjectDetailsEnrolledCourse.class);
            }
            intent.putExtra("course_id", course.getCourseName()); // Pass course name as ID
            intent.putExtra("overview", course.getOverview());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        Log.d("LoadCourses", "RecyclerView item count: " + courseList.size());
        return courseList.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseTitle, courseDescription;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTitle = itemView.findViewById(R.id.textViewCourseTitle);
            courseDescription = itemView.findViewById(R.id.textViewCourseOverview);
        }
    }
}