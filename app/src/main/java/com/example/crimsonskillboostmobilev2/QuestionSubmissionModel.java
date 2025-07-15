package com.example.crimsonskillboostmobilev2;

import java.util.List;

public class QuestionSubmissionModel {
    private String question;
    private List<String> options;
    private int correctOption;
    private String userAnswer;
    private boolean isCorrect;

    // No-argument constructor (required by Firestore)
    public QuestionSubmissionModel() {
    }

    public QuestionSubmissionModel(String question, List<String> options, int correctOption, String userAnswer, boolean isCorrect) {
        this.question = question;
        this.options = options;
        this.correctOption = correctOption;
        this.userAnswer = userAnswer;
        this.isCorrect = isCorrect;
    }

    // Public getters
    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectOption() {
        return correctOption;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    // Public setters (optional, if needed)
    public void setQuestion(String question) {
        this.question = question;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public void setCorrectOption(int correctOption) {
        this.correctOption = correctOption;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}