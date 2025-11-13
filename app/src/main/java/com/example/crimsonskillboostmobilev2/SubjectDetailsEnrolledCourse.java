package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SubjectDetailsEnrolledCourse extends AppCompatActivity {

    private TextView tvCourseTitle, tvInstructorName, tvInstructorEmail, tvCourseOverview;
    private RecyclerView rvTopics, rvTasks, rvQuizzes;
    private TopicsAdapter topicsAdapter;
    private TasksAdapter tasksAdapter;
    private QuizzesAdapter quizzesAdapter;
    private String courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_details_enrolled_course);

        tvCourseTitle = findViewById(R.id.tvCourseTitle);
        tvInstructorName = findViewById(R.id.tvInstructorName);
        tvInstructorEmail = findViewById(R.id.tvInstructorEmail);
        tvCourseOverview = findViewById(R.id.tvCourseOverview);
        rvTopics = findViewById(R.id.rvTopics);
        rvTasks = findViewById(R.id.rvTasks);
        rvQuizzes = findViewById(R.id.rvQuizzes);
        ImageView ivBack = findViewById(R.id.ivBack);

        courseId = getIntent().getStringExtra("course_id");
        if (courseId == null || courseId.isEmpty()) {
            Toast.makeText(this, "Invalid course ID received", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ivBack.setOnClickListener(v -> finish());

        // --- Topics setup ---
        rvTopics.setLayoutManager(new LinearLayoutManager(this));
        topicsAdapter = new TopicsAdapter(new ArrayList<>(), (title, description) -> {
            Intent intent = new Intent(this, TopicsPageActivity.class);
            for (TopicModel topic : topicsAdapter.topics) {
                if (topic.getTitle().equals(title)) {
                    intent.putExtra("topic_id", topic.getId());
                    break;
                }
            }
            intent.putExtra("topic_title", title);
            intent.putExtra("topic_description", description);
            startActivity(intent);
        });
        rvTopics.setAdapter(topicsAdapter);

        // --- Tasks setup ---
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new TasksAdapter(new ArrayList<>(), task -> {
            Intent intent = new Intent(this, TaskPath2Activity.class);
            intent.putExtra("taskId", task.getId());
            intent.putExtra("courseId", courseId);
            startActivity(intent);
        });
        rvTasks.setAdapter(tasksAdapter);

        // --- Quizzes setup ---
        rvQuizzes.setLayoutManager(new LinearLayoutManager(this));
        quizzesAdapter = new QuizzesAdapter(new ArrayList<>(), this, courseId);
        rvQuizzes.setAdapter(quizzesAdapter);

        // --- Load everything ---
        loadCourseDetails(courseId);
        loadCourseTopics(courseId);
        loadCourseTasks(courseId);
        loadCourseQuizzes(courseId);
    }

    // ✅ Load course metadata
    private void loadCourseDetails(String courseId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("courses").document(courseId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        Toast.makeText(this, "Course not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    tvCourseTitle.setText(documentSnapshot.getString("course_name"));
                    tvInstructorName.setText(documentSnapshot.getString("instructor_name"));
                    tvCourseOverview.setText(documentSnapshot.getString("overview"));

                    String userId = documentSnapshot.getString("user_id");
                    if (userId == null || userId.isEmpty()) {
                        tvInstructorEmail.setText("Email Not Available");
                        return;
                    }

                    firestore.collection("users").document(userId)
                            .get()
                            .addOnSuccessListener(userDoc -> {
                                String email = userDoc.getString("email");
                                tvInstructorEmail.setText(email != null ? email : "Email Not Available");
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Firestore", "Error fetching email: " + e.getMessage(), e);
                                tvInstructorEmail.setText("Email Not Available");
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching course: " + e.getMessage(), e);
                    Toast.makeText(this, "Error loading course", Toast.LENGTH_SHORT).show();
                });
    }

    // ✅ Load topics
    private void loadCourseTopics(String courseId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();

        firestore.collection("users").document(userId)
                .collection("completedTopics")
                .get()
                .addOnSuccessListener(completedSnapshot -> {
                    List<String> completedIds = new ArrayList<>();
                    for (DocumentSnapshot doc : completedSnapshot.getDocuments()) {
                        completedIds.add(doc.getId());
                    }

                    firestore.collection("courses").document(courseId)
                            .collection("topics")
                            .get()
                            .addOnSuccessListener(topicSnapshot -> {
                                List<TopicModel> topics = new ArrayList<>();
                                for (DocumentSnapshot document : topicSnapshot.getDocuments()) {
                                    TopicModel topic = new TopicModel();
                                    topic.setId(document.getId());
                                    topic.setTitle(document.getString("title"));
                                    topic.setDescription(document.getString("description"));
                                    topic.setCreatedAt(document.getTimestamp("created_at"));
                                    topic.setCreatedBy(document.getString("created_by"));
                                    topic.setRequiredTopic(document.getString("requiredTopic"));

                                    boolean locked = false;
                                    String required = document.getString("requiredTopic");
                                    if (required != null && !required.isEmpty()) {
                                        locked = !completedIds.contains(required);
                                    }
                                    topic.setLocked(locked);

                                    topics.add(topic);
                                }
                                topicsAdapter.updateTopics(topics);
                            });
                });
    }

    // ✅ Load tasks (fixed Firestore field mapping)
    private void loadCourseTasks(String courseId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();

        // 1️⃣ Completed topics
        firestore.collection("users").document(userId)
                .collection("completedTopics")
                .get()
                .addOnSuccessListener(completedTopicsSnapshot -> {
                    List<String> completedTopicIds = new ArrayList<>();
                    for (DocumentSnapshot doc : completedTopicsSnapshot.getDocuments()) {
                        completedTopicIds.add(doc.getId());
                    }

                    // 2️⃣ Completed tasks (via submissions)
                    firestore.collectionGroup("submissions")
                            .whereEqualTo("userId", userId)
                            .get()
                            .addOnSuccessListener(submissionsSnapshot -> {
                                List<String> completedTaskIds = new ArrayList<>();
                                for (DocumentSnapshot subDoc : submissionsSnapshot.getDocuments()) {
                                    String[] parts = subDoc.getReference().getPath().split("/");
                                    if (parts.length >= 4) {
                                        completedTaskIds.add(parts[3]);
                                    }
                                }

                                // 3️⃣ Load all course tasks
                                firestore.collection("courses").document(courseId)
                                        .collection("tasks")
                                        .get()
                                        .addOnSuccessListener(taskSnapshot -> {
                                            List<TaskModel> tasks = new ArrayList<>();

                                            for (DocumentSnapshot taskDoc : taskSnapshot.getDocuments()) {
                                                TaskModel task = new TaskModel();
                                                task.setId(taskDoc.getId());
                                                task.setCourseId(courseId);
                                                task.setTitle(taskDoc.getString("title"));
                                                task.setDescription(taskDoc.getString("description"));

                                                // ✅ Firestore uses "requiredTopic", not "topic_id"
                                                task.setRequiredTopic(taskDoc.getString("requiredTopic"));
                                                task.setRequiredTask(taskDoc.getString("requiredTask"));

                                                boolean locked = false;

                                                // Lock if required topic not completed
                                                String requiredTopic = task.getRequiredTopic();
                                                if (requiredTopic != null && !completedTopicIds.contains(requiredTopic)) {
                                                    locked = true;
                                                }

                                                // Lock if required task not completed
                                                String requiredTaskId = task.getRequiredTask();
                                                if (requiredTaskId != null && !requiredTaskId.isEmpty()
                                                        && !completedTaskIds.contains(requiredTaskId)) {
                                                    locked = true;
                                                }

                                                task.setLocked(locked);
                                                tasks.add(task);
                                            }

                                            tasksAdapter.updateTasks(tasks);
                                        })
                                        .addOnFailureListener(e ->
                                                Log.e("TaskError", "Failed to load tasks: " + e.getMessage(), e)
                                        );
                            });
                });
    }

    private String formatDate(String date) throws Exception {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return outputFormat.format(inputFormat.parse(date));
    }

    // ✅ Load quizzes (unchanged)
    private void loadCourseQuizzes(String courseId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();

        firestore.collection("users").document(userId)
                .collection("completedTopics")
                .get()
                .addOnSuccessListener(completedTopicsSnapshot -> {
                    List<String> completedTopicIds = new ArrayList<>();
                    for (DocumentSnapshot doc : completedTopicsSnapshot.getDocuments()) {
                        completedTopicIds.add(doc.getId());
                    }

                    firestore.collection("quiz_submissions")
                            .whereEqualTo("userId", userId)
                            .get()
                            .addOnSuccessListener(submissionSnapshot -> {
                                List<String> completedQuizIds = new ArrayList<>();
                                for (DocumentSnapshot doc : submissionSnapshot.getDocuments()) {
                                    String quizId = doc.getString("quizId");
                                    if (quizId != null) completedQuizIds.add(quizId);
                                }

                                firestore.collection("quizzes")
                                        .whereEqualTo("course_id", courseId)
                                        .get()
                                        .addOnSuccessListener(querySnapshot -> {
                                            List<QuizModel> quizzes = new ArrayList<>();

                                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                                QuizModel quiz = new QuizModel();
                                                quiz.setId(document.getId());
                                                quiz.setTitle(document.getString("title"));
                                                quiz.setDescription(document.getString("description"));
                                                quiz.setCourseId(document.getString("course_id"));
                                                quiz.setTopicId(document.getString("topic_id"));
                                                quiz.setRequiredQuiz(document.getString("requiredQuiz"));
                                                quiz.setPublished(document.getBoolean("published") != null && document.getBoolean("published"));
                                                quiz.setAttempts(document.getLong("attempts") != null ? document.getLong("attempts").intValue() : 0);
                                                quiz.setStartDate(document.getString("start_date"));
                                                quiz.setEndDate(document.getString("end_date"));
                                                quiz.setAllowLate(document.getBoolean("allow_late") != null && document.getBoolean("allow_late"));

                                                boolean locked = false;
                                                if (quiz.getTopicId() != null && !completedTopicIds.contains(quiz.getTopicId())) {
                                                    locked = true;
                                                }
                                                if (quiz.getRequiredQuiz() != null && !completedQuizIds.contains(quiz.getRequiredQuiz())) {
                                                    locked = true;
                                                }

                                                quiz.setLocked(locked);
                                                quizzes.add(quiz);
                                            }

                                            quizzesAdapter.updateQuizzes(quizzes);
                                        });
                            });
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (courseId != null && !courseId.isEmpty()) {
            loadCourseTopics(courseId);
            loadCourseTasks(courseId);
            loadCourseQuizzes(courseId);
        }
    }
}
