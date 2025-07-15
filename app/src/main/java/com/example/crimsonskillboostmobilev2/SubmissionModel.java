package com.example.crimsonskillboostmobilev2;

import java.util.List;

public class SubmissionModel {
    private int score;
    private int totalPossiblePoints;
    private long timestamp;
    private String userId;
    private List<QuestionSubmissionModel> questionSubmissions;

    // No-argument constructor (required by Firestore)
    public SubmissionModel() {
    }

    public SubmissionModel(int score, int totalPossiblePoints, long timestamp, String userId, List<QuestionSubmissionModel> questionSubmissions) {
        this.score = score;
        this.totalPossiblePoints = totalPossiblePoints;
        this.timestamp = timestamp;
        this.userId = userId;
        this.questionSubmissions = questionSubmissions;
    }

    // Public getters
    public int getScore() {
        return score;
    }

    public int getTotalPossiblePoints() {
        return totalPossiblePoints;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public List<QuestionSubmissionModel> getQuestionSubmissions() {
        return questionSubmissions;
    }

    // Public setters (optional, if needed)
    public void setScore(int score) {
        this.score = score;
    }

    public void setTotalPossiblePoints(int totalPossiblePoints) {
        this.totalPossiblePoints = totalPossiblePoints;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setQuestionSubmissions(List<QuestionSubmissionModel> questionSubmissions) {
        this.questionSubmissions = questionSubmissions;
    }
}