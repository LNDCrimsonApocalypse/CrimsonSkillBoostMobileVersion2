package com.example.crimsonskillboostmobilev2;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class TopicsPageActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton navButton, backButton;
    private FrameLayout contentViewer;
    private LinearLayout subtopicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topics_page);

        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.navButton);
        backButton = findViewById(R.id.backButton);
        contentViewer = findViewById(R.id.contentViewer);
        subtopicList = findViewById(R.id.subtopicList);

        // Open drawer
        navButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Go back to previous screen
        backButton.setOnClickListener(v -> finish());

        // Get passed topic description
        String topicDescription = getIntent().getStringExtra("topic_description");

        // Load parsed content
        loadContentIntoViewer(topicDescription);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed(); // ✅ Proper override
        }
    }

    private void loadContentIntoViewer(String description) {
        contentViewer.removeAllViews();
        subtopicList.removeAllViews();

        if (description == null || description.trim().isEmpty()) {
            TextView error = new TextView(this);
            error.setText("No content available.");
            error.setTextColor(Color.RED);
            error.setTextSize(16f);
            contentViewer.addView(error);
            return;
        }

        String[] blocks = description.split("\\n\\s*\\n");
        boolean hasSubtopics = false;

        for (int i = 0; i < blocks.length; i++) {
            String[] lines = blocks[i].split("\\n", 2);
            if (lines.length >= 2) {
                hasSubtopics = true;
                String title = lines[0].trim();
                String content = lines[1].trim();

                TextView subtopicButton = new TextView(this);
                subtopicButton.setText(title);
                subtopicButton.setPadding(16, 16, 16, 16);
                subtopicButton.setBackgroundColor(Color.parseColor("#eef3fb"));
                subtopicButton.setTextSize(16f);
                subtopicButton.setClickable(true);

                int index = i;
                subtopicButton.setOnClickListener(v -> {
                    renderSubtopicContent(title, content);
                    highlightSelected(index);
                    drawerLayout.closeDrawer(GravityCompat.START);
                });

                subtopicList.addView(subtopicButton);

                // Auto-select first subtopic
                if (i == 0) {
                    subtopicButton.performClick();
                }
            }
        }

        if (!hasSubtopics) {
            TextView descriptionView = new TextView(this);
            descriptionView.setText(description);
            descriptionView.setTextSize(16f);
            contentViewer.addView(descriptionView);
        }
    }

    private void renderSubtopicContent(String title, String content) {
        contentViewer.removeAllViews();

        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(18f);
        titleView.setTextColor(Color.BLACK);
        titleView.setPadding(0, 0, 0, 12);

        TextView contentView = new TextView(this);
        contentView.setText(content);
        contentView.setTextSize(16f);
        contentView.setTextColor(Color.DKGRAY);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);
        layout.addView(titleView);
        layout.addView(contentView);

        contentViewer.addView(layout);
    }

    private void highlightSelected(int selectedIndex) {
        for (int i = 0; i < subtopicList.getChildCount(); i++) {
            View view = subtopicList.getChildAt(i);
            view.setBackgroundColor(i == selectedIndex ? Color.WHITE : Color.parseColor("#eef3fb"));
        }
    }
}
