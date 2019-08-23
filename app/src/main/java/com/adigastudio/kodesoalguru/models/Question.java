package com.adigastudio.kodesoalguru.models;

import java.util.Date;

public class Question {
    private String questionId;
    private String question;
    private String questionImage;
    private boolean questionImageStatus;
    private String questionImageInformation;
    private String choiceA;
    private String choiceB;
    private String choiceC;
    private String choiceD;
    private String choiceE;
    private String choiceImageA;
    private String choiceImageB;
    private String choiceImageC;
    private String choiceImageD;
    private String choiceImageE;
    private boolean choiceImageStatus;
    private String correctChoice;
    private String basic;
    private String userId;
    private String examId;
    private Date created;
    private Throwable error;


    public Question(String questionId, String question, String questionImage, boolean questionImageStatus, String questionImageInformation, String choiceA, String choiceB, String choiceC, String choiceD, String choiceE, String choiceImageA, String choiceImageB, String choiceImageC, String choiceImageD, String choiceImageE, boolean choiceImageStatus, String correctChoice, String basic, String userId, String examId, Date created) {
        this.questionId = questionId;
        this.question = question;
        this.questionImage = questionImage;
        this.questionImageStatus = questionImageStatus;
        this.questionImageInformation = questionImageInformation;
        this.choiceA = choiceA;
        this.choiceB = choiceB;
        this.choiceC = choiceC;
        this.choiceD = choiceD;
        this.choiceE = choiceE;
        this.choiceImageA = choiceImageA;
        this.choiceImageB = choiceImageB;
        this.choiceImageC = choiceImageC;
        this.choiceImageD = choiceImageD;
        this.choiceImageE = choiceImageE;
        this.choiceImageStatus = choiceImageStatus;
        this.correctChoice = correctChoice;
        this.basic = basic;
        this.userId = userId;
        this.examId = examId;
        this.created = created;
    }

    public Question(Throwable error) {
        this.error = error;
    }

    public boolean isQuestionImageStatus() {
        return questionImageStatus;
    }

    public void setQuestionImageStatus(boolean questionImageStatus) {
        this.questionImageStatus = questionImageStatus;
    }

    public boolean isChoiceImageStatus() {
        return choiceImageStatus;
    }

    public void setChoiceImageStatus(boolean choiceImageStatus) {
        this.choiceImageStatus = choiceImageStatus;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionImage() {
        return questionImage;
    }

    public void setQuestionImage(String questionImage) {
        this.questionImage = questionImage;
    }

    public String getQuestionImageInformation() {
        return questionImageInformation;
    }

    public void setQuestionImageInformation(String questionImageInformation) {
        this.questionImageInformation = questionImageInformation;
    }

    public String getChoiceA() {
        return choiceA;
    }

    public void setChoiceA(String choiceA) {
        this.choiceA = choiceA;
    }

    public String getChoiceB() {
        return choiceB;
    }

    public void setChoiceB(String choiceB) {
        this.choiceB = choiceB;
    }

    public String getChoiceC() {
        return choiceC;
    }

    public void setChoiceC(String choiceC) {
        this.choiceC = choiceC;
    }

    public String getChoiceD() {
        return choiceD;
    }

    public void setChoiceD(String choiceD) {
        this.choiceD = choiceD;
    }

    public String getChoiceE() {
        return choiceE;
    }

    public void setChoiceE(String choiceE) {
        this.choiceE = choiceE;
    }

    public String getChoiceImageA() {
        return choiceImageA;
    }

    public void setChoiceImageA(String choiceImageA) {
        this.choiceImageA = choiceImageA;
    }

    public String getChoiceImageB() {
        return choiceImageB;
    }

    public void setChoiceImageB(String choiceImageB) {
        this.choiceImageB = choiceImageB;
    }

    public String getChoiceImageC() {
        return choiceImageC;
    }

    public void setChoiceImageC(String choiceImageC) {
        this.choiceImageC = choiceImageC;
    }

    public String getChoiceImageD() {
        return choiceImageD;
    }

    public void setChoiceImageD(String choiceImageD) {
        this.choiceImageD = choiceImageD;
    }

    public String getChoiceImageE() {
        return choiceImageE;
    }

    public void setChoiceImageE(String choiceImageE) {
        this.choiceImageE = choiceImageE;
    }

    public String getCorrectChoice() {
        return correctChoice;
    }

    public void setCorrectChoice(String correctChoice) {
        this.correctChoice = correctChoice;
    }

    public String getBasic() {
        return basic;
    }

    public void setBasic(String basic) {
        this.basic = basic;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
