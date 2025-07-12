package com.example.crimsonskillboostmobilev2;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class TopicsPageActivity extends AppCompatActivity {

    private FrameLayout contentViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topics_page);

        // Initialize views
        contentViewer = findViewById(R.id.contentViewer);

        // Retrieve the topic description from the intent
        String topicDescription = getIntent().getStringExtra("topic_description");

        // Load content dynamically into the contentViewer
        loadContentIntoViewer(topicDescription);
    }

    private void loadContentIntoViewer(String descriptionOrUrl) {
        contentViewer.removeAllViews();

        if (descriptionOrUrl.endsWith(".pdf")) {
            WebView webView = new WebView(this);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + descriptionOrUrl);
            contentViewer.addView(webView);
        } else if (descriptionOrUrl.endsWith(".mp4")) {
            VideoView videoView = new VideoView(this);
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(Uri.parse(descriptionOrUrl));
            videoView.start();
            contentViewer.addView(videoView);
        } else if (descriptionOrUrl.endsWith(".jpg") || descriptionOrUrl.endsWith(".jpeg") || descriptionOrUrl.endsWith(".png")) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            Glide.with(this).load(descriptionOrUrl).into(imageView);
            contentViewer.addView(imageView);
        } else if (descriptionOrUrl.endsWith(".txt")) {
            new Thread(() -> {
                try {
                    URL fileUrl = new URL(descriptionOrUrl);
                    BufferedReader in = new BufferedReader(new InputStreamReader(fileUrl.openStream()));
                    StringBuilder text = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        text.append(line).append("\n");
                    }
                    in.close();

                    runOnUiThread(() -> {
                        TextView textView = new TextView(TopicsPageActivity.this);
                        textView.setText(text.toString());
                        textView.setTextSize(16f);
                        contentViewer.addView(textView);
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            // Display the topic description directly
            TextView descriptionView = new TextView(this);
            descriptionView.setText(descriptionOrUrl);
            descriptionView.setTextSize(16f);
            descriptionView.setTextColor(Color.BLACK);
            contentViewer.addView(descriptionView);
        }
    }
}