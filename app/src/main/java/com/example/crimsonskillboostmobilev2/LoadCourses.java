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

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

        firestore.collection("users").document(studentId).get()
                .addOnSuccessListener(userDocument -> {
                    if (userDocument.exists()) {
                        String year = userDocument.getString("year");
                        String section = userDocument.getString("section");

                        Log.d("LoadCourses", "User data retrieved: Year=" + year + ", Section=" + section);

                        firestore.collection(collection)
                                .whereEqualTo("year", year)
                                .whereEqualTo("section", section)
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    courseList.clear();
                                    Log.d("LoadCourses", "Query successful. Number of documents: " + querySnapshot.size());
                                    for (QueryDocumentSnapshot document : querySnapshot) {
                                        Log.d("LoadCourses", "Document data: " + document.getData());
                                        try {
                                            CourseModel course = new CourseModel();
                                            course.setCourseName(document.getString("course_name"));
                                            course.setInstructorName(document.getString("instructor_name"));
                                            course.setOverview(document.getString("overview"));
                                            course.setRequirements(document.getString("requirements"));
                                            course.setYear(document.getString("year"));
                                            course.setSection(document.getString("section"));
                                            course.setSemester(document.getString("semester"));
                                            course.setUserId(document.getString("user_id"));

                                            // Handle created_at field as a string and convert to Timestamp
                                            String createdAtString = document.getString("created_at");
                                            if (createdAtString != null) {
                                                try {
                                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                                                    Date date = sdf.parse(createdAtString);
                                                    Timestamp createdAt = new Timestamp(date);
                                                    course.setCreatedAt(createdAt);
                                                } catch (ParseException e) {
                                                    Log.e("LoadCourses", "Error parsing created_at field: " + createdAtString, e);
                                                }
                                            }

                                            courseList.add(course);
                                        } catch (Exception e) {
                                            Log.e("LoadCourses", "Error processing document: " + document.getId(), e);
                                        }
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
            Intent intent = new Intent(context, mode.equals("available") ? SubjectDetailsAvailableCourse.class : SubjectDetailsEnrolledCourse.class);
            intent.putExtra("course_id", course.getCourseName());
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