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

        if (mode.equals("enrolled")) {
            firestore.collection("enrollment_requests")
                    .whereEqualTo("student_id", studentId)
                    .whereEqualTo("status", "approved")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        List<CourseModel> tempCourseList = new ArrayList<>();
                        int totalRequests = querySnapshot.size();
                        final int[] processedRequests = {0};

                        if (totalRequests == 0) {
                            // No enrollments found
                            courseList.clear();
                            notifyDataSetChanged();
                            return;
                        }

                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String courseId = document.getString("course_id");

                            firestore.collection("courses").document(courseId)
                                    .get()
                                    .addOnSuccessListener(courseDoc -> {
                                        processedRequests[0]++;
                                        if (courseDoc.exists()) {
                                            String status = courseDoc.getString("status");
                                            if (!"inactive".equals(status)) {
                                                CourseModel course = new CourseModel();
                                                course.setCourseId(courseId);
                                                course.setCourseName(courseDoc.getString("course_name"));
                                                course.setInstructorName(courseDoc.getString("instructor_name"));
                                                course.setOverview(courseDoc.getString("overview"));
                                                course.setRequirements(courseDoc.getString("requirements"));
                                                course.setYear(courseDoc.getString("year"));
                                                course.setSection(courseDoc.getString("section"));
                                                course.setSemester(courseDoc.getString("semester"));
                                                course.setUserId(courseDoc.getString("user_id"));

                                                tempCourseList.add(course);
                                            }
                                        } else {
                                            Log.e("LoadCourses", "Course not found: " + courseId);
                                        }

                                        if (processedRequests[0] == totalRequests) {
                                            courseList.clear();
                                            courseList.addAll(tempCourseList);
                                            notifyDataSetChanged();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        processedRequests[0]++;
                                        Log.e("LoadCourses", "Error fetching course details: " + e.getMessage(), e);
                                        if (processedRequests[0] == totalRequests) {
                                            courseList.clear();
                                            courseList.addAll(tempCourseList);
                                            notifyDataSetChanged();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(e -> Log.e("LoadCourses", "Error fetching enrollments: " + e.getMessage(), e));

        } else {
            firestore.collection("users").document(studentId)
                    .get()
                    .addOnSuccessListener(userDoc -> {
                        if (userDoc.exists()) {
                            String year = userDoc.getString("year");
                            String section = userDoc.getString("section");

                            firestore.collection("courses")
                                    .whereEqualTo("year", year)
                                    .whereEqualTo("section", section)
                                    .get()
                                    .addOnSuccessListener(querySnapshot -> {
                                        List<CourseModel> tempCourseList = new ArrayList<>();

                                        for (QueryDocumentSnapshot document : querySnapshot) {
                                            String status = document.getString("status");
                                            if (!"inactive".equals(status)) {
                                                CourseModel course = new CourseModel();
                                                course.setCourseId(document.getId());
                                                course.setCourseName(document.getString("course_name"));
                                                course.setInstructorName(document.getString("instructor_name"));
                                                course.setOverview(document.getString("overview"));
                                                course.setRequirements(document.getString("requirements"));
                                                course.setYear(document.getString("year"));
                                                course.setSection(document.getString("section"));
                                                course.setSemester(document.getString("semester"));
                                                course.setUserId(document.getString("user_id"));

                                                tempCourseList.add(course);
                                            }
                                        }

                                        courseList.clear();
                                        courseList.addAll(tempCourseList);
                                        notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> Log.e("LoadCourses", "Error fetching courses: " + e.getMessage(), e));
                        } else {
                            Log.e("LoadCourses", "User not found for ID: " + studentId);
                        }
                    })
                    .addOnFailureListener(e -> Log.e("LoadCourses", "Error fetching user: " + e.getMessage(), e));
        }
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

        String courseName = course.getCourseName() != null ? course.getCourseName() : "Untitled Course";
        String overview = course.getOverview() != null ? course.getOverview() : "No overview available.";
        String year = course.getYear() != null ? course.getYear() : "Unknown Year";

        holder.courseTitle.setText(courseName);
        holder.courseDescription.setText(overview + " | Year: " + year);

        holder.itemView.setOnClickListener(v -> {
            Log.d("LoadCourses", "Navigating with course_id: " + course.getCourseId());
            Intent intent = new Intent(context, mode.equals("available") ? SubjectDetailsAvailableCourse.class : SubjectDetailsEnrolledCourse.class);
            intent.putExtra("course_id", course.getCourseId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        Log.d("LoadCourses", "Item count: " + courseList.size());
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
