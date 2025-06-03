package com.example.crimsonskillboostmobilev2;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuestionModel {
    private int id;

    @SerializedName("question_text") // Ensure this matches the API field name
    private String questionText;

    @SerializedName("options") // Ensure this matches the API field name
    private List<String> options;

    @SerializedName("correct_answer") // Ensure this matches the API field name
    private int correctAnswer;

    public int getId() {
        return id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}