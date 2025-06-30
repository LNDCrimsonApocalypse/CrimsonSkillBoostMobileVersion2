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

    // Updated fetchCoursesFromFirestore method in LoadCourses.java
    // Updated fetchCoursesFromFirestore method in LoadCourses.java
    public void fetchCoursesFromFirestore() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        if (mode.equals("enrolled")) {
            // Fetch approved enrollment requests
            firestore.collection("enrollment_requests")
                    .whereEqualTo("student_id", studentId)
                    .whereEqualTo("status", "approved")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        courseList.clear();
                        Log.d("LoadCourses", "Query successful. Number of enrollment requests: " + querySnapshot.size());
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String courseId = document.getString("course_id");

                            // Fetch course by document ID
                            firestore.collection("courses").document(courseId)
                                    .get()
                                    .addOnSuccessListener(courseDoc -> {
                                        if (courseDoc.exists()) {
                                            CourseModel course = new CourseModel();
                                            course.setCourseId(courseId); // Set courseId properly
                                            course.setCourseName(courseDoc.getString("course_name"));
                                            course.setInstructorName(courseDoc.getString("instructor_name"));
                                            course.setOverview(courseDoc.getString("overview"));
                                            course.setRequirements(courseDoc.getString("requirements"));
                                            course.setYear(courseDoc.getString("year"));
                                            course.setSection(courseDoc.getString("section"));
                                            course.setSemester(courseDoc.getString("semester"));
                                            course.setUserId(courseDoc.getString("user_id"));

                                            courseList.add(course);
                                            notifyDataSetChanged();
                                        } else {
                                            Log.e("LoadCourses", "Course not found for ID: " + courseId);
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.e("LoadCourses", "Error fetching course details: " + e.getMessage(), e));
                        }
                    })
                    .addOnFailureListener(e -> Log.e("LoadCourses", "Error fetching enrollment requests: " + e.getMessage(), e));
        } else {
            // Fetch available courses
            firestore.collection("users").document(studentId).get()
                    .addOnSuccessListener(userDocument -> {
                        if (userDocument.exists()) {
                            String year = userDocument.getString("year");
                            String section = userDocument.getString("section");

                            firestore.collection("courses")
                                    .whereEqualTo("year", year)
                                    .whereEqualTo("section", section)
                                    .get()
                                    .addOnSuccessListener(querySnapshot -> {
                                        courseList.clear();
                                        for (QueryDocumentSnapshot document : querySnapshot) {
                                            CourseModel course = new CourseModel();
                                            course.setCourseId(document.getId()); // Set courseId properly
                                            course.setCourseName(document.getString("course_name"));
                                            course.setInstructorName(document.getString("instructor_name"));
                                            course.setOverview(document.getString("overview"));
                                            course.setRequirements(document.getString("requirements"));
                                            course.setYear(document.getString("year"));
                                            course.setSection(document.getString("section"));
                                            course.setSemester(document.getString("semester"));
                                            course.setUserId(document.getString("user_id"));

                                            courseList.add(course);
                                        }
                                        notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> Log.e("LoadCourses", "Error fetching courses: " + e.getMessage(), e));
                        } else {
                            Log.e("LoadCourses", "User data not found for ID: " + studentId);
                        }
                    })
                    .addOnFailureListener(e -> Log.e("LoadCourses", "Error fetching user data: " + e.getMessage(), e));
        }
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.loadcourses, parent, false);
        return new CourseViewHolder(view);
    }

    // Updated onBindViewHolder method in LoadCourses.java
    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        CourseModel course = courseList.get(position);

        String courseName = course.getCourseName() != null ? course.getCourseName() : "Untitled Course";
        String overview = course.getOverview() != null ? course.getOverview() : "No overview available.";
        String year = course.getYear() != null ? course.getYear() : "Unknown Year";

        holder.courseTitle.setText(courseName);
        holder.courseDescription.setText(overview + " | Year: " + year);

        holder.itemView.setOnClickListener(v -> {
            Log.d("LoadCourses", "Navigating to SubjectDetailsEnrolledCourse with course_id: " + course.getCourseId());
            Intent intent = new Intent(context, mode.equals("available") ? SubjectDetailsAvailableCourse.class : SubjectDetailsEnrolledCourse.class);
            intent.putExtra("course_id", course.getCourseId()); // Use the correct course_id
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