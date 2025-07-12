package com.example.crimsonskillboostmobilev2;

public class SubmissionModel {
    private int score;
    private int maxScore;
    private long timestamp;
    private String userId;

    public SubmissionModel() {
        // Firestore requires a public no-arg constructor
    }

    public SubmissionModel(int score, int maxScore, long timestamp, String userId) {
        this.score = score;
        this.maxScore = maxScore;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    // Getters
    public int getScore() {
        return score;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }
}
