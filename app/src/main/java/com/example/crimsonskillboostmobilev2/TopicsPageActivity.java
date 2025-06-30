package com.example.crimsonskillboostmobilev2;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class TopicsPageActivity extends AppCompatActivity {

    private RecyclerView rvTopics;
    private TextView tvLessonDescription;
    private TopicsAdapter topicsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topics_page);

        rvTopics = findViewById(R.id.rvTopics);
        tvLessonDescription = findViewById(R.id.tvLessonDescription);

        rvTopics.setLayoutManager(new LinearLayoutManager(this));
        topicsAdapter = new TopicsAdapter(new ArrayList<>(), description -> {
            // Display the lesson description when a topic is clicked
            tvLessonDescription.setText(description);
        });
        rvTopics.setAdapter(topicsAdapter);

        loadTopics();
    }

    private void loadTopics() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String courseId = getIntent().getStringExtra("course_id");

        firestore.collection("courses").document(courseId).collection("topics")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<TopicModel> topics = new ArrayList<>();
                    querySnapshot.forEach(document -> {
                        TopicModel topic = new TopicModel();
                        topic.setTitle(document.getString("title"));
                        topic.setDescription(document.getString("description"));
                        topic.setCreatedAt(document.getTimestamp("created_at"));
                        topics.add(topic);
                    });
                    topicsAdapter.updateTopics(topics);
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }
}