package com.example.crimsonskillboostmobilev2;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        TextView headerTitle = findViewById(R.id.headerTitle);

        navButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        backButton.setOnClickListener(v -> finish());

        // Get extras
        String topicId = getIntent().getStringExtra("topic_id");
        String topicTitle = getIntent().getStringExtra("topic_title");
        String topicDescription = getIntent().getStringExtra("topic_description");

        if (topicTitle != null && !topicTitle.trim().isEmpty()) {
            headerTitle.setText(topicTitle);
        } else {
            headerTitle.setText("Topic");
        }

        // ✅ Mark topic as completed by ID
        markTopicAsCompleted(topicId);

        loadContentIntoViewer(topicDescription);
    }

    private void markTopicAsCompleted(String topicId) {
        if (topicId == null || topicId.isEmpty()) return;

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "You must be logged in to track progress.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();

        HashMap<String, Object> data = new HashMap<>();
        data.put("completed", true);
        data.put("timestamp", FieldValue.serverTimestamp());

        firestore.collection("users")
                .document(userId)
                .collection("completedTopics")
                .document(topicId) // ✅ save by topic ID
                .set(data)
                .addOnSuccessListener(aVoid ->
                        Log.d("TopicProgress", "Marked completed: " + topicId))
                .addOnFailureListener(e ->
                        Log.e("TopicProgress", "Error marking topic complete", e));
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

                if (i == 0) {
                    subtopicButton.performClick();
                }
            }
        }

        if (!hasSubtopics) {
            renderSubtopicContent("Topic", description);
        }
    }

    private void renderSubtopicContent(String title, String content) {
        contentViewer.removeAllViews();

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(0, 0, 0, 0);

        String pdfUrl = extractPdfUrl(content);

        if (pdfUrl != null) {
            WebView webView = new WebView(this);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDisplayZoomControls(false);
            webView.setWebViewClient(new WebViewClient());

            String viewerUrl = "https://drive.google.com/viewerng/viewer?embedded=true&url=" + Uri.encode(pdfUrl);
            webView.loadUrl(viewerUrl);

            layout.addView(webView, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
        } else {
            TextView contentView = new TextView(this);
            contentView.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY));
            contentView.setTextSize(16f);
            contentView.setTextColor(Color.DKGRAY);
            layout.addView(contentView);
        }

        contentViewer.addView(layout);
    }

    private void highlightSelected(int selectedIndex) {
        for (int i = 0; i < subtopicList.getChildCount(); i++) {
            View view = subtopicList.getChildAt(i);
            view.setBackgroundColor(i == selectedIndex ? Color.WHITE : Color.parseColor("#eef3fb"));
        }
    }

    private String extractPdfUrl(String content) {
        Pattern pattern = Pattern.compile("href\\s*=\\s*\"(https?://[^\"]+?\\.pdf[^\"]*)\"", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }

        if (content.trim().endsWith(".pdf") && Patterns.WEB_URL.matcher(content).find()) {
            return content.trim();
        }

        return null;
    }
}
