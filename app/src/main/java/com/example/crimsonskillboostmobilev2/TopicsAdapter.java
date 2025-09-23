package com.example.crimsonskillboostmobilev2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.TopicViewHolder> {

    private List<TopicModel> topics;
    private OnTopicClickListener listener;

    public TopicsAdapter(List<TopicModel> topics, OnTopicClickListener listener) {
        this.topics = topics;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        TopicModel topic = topics.get(position);

        holder.tvTitle.setText(topic.getTitle());

        // Format and display the created_at timestamp
        Timestamp createdAt = topic.getCreatedAt();
        if (createdAt != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            holder.tvCreatedAt.setText(sdf.format(createdAt.toDate()));
        } else {
            holder.tvCreatedAt.setText("Unknown Date");
        }

        // Set click listener to send both title and description
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTopicClick(topic.getTitle(), topic.getDescription());
            }
        });
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public void updateTopics(List<TopicModel> newTopics) {
        this.topics = newTopics;
        notifyDataSetChanged();
    }

    static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCreatedAt;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
        }
    }

    // ✅ Updated interface to include both title & description
    public interface OnTopicClickListener {
        void onTopicClick(String title, String description);
    }
}
