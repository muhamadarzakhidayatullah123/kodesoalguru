package com.adigastudio.kodesoalguru.models;

import android.util.Patterns;

import java.util.Date;

public class User {
    private String userId;
    private String fullName;
    private String email;
    private String password;
    private String institute;
    private boolean administrator;
    private boolean headmaster;
    private boolean moderator;
    private boolean teacher;
    private boolean student;
    private String studentClass;
    private String studentNumber;
    private Date created;
    private boolean verified;
    private Throwable error;

    public User(String email) {
        this.email = email;
    }

    public User(Throwable error) {
        this.error = error;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public User(boolean verified) {
        this.verified = verified;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public User(String userId, String fullName, String email, String institute, boolean administrator, boolean headmaster, boolean moderator, boolean teacher, boolean student, String studentClass, String studentNumber, Date created) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.institute = institute;
        this.administrator = administrator;
        this.headmaster = headmaster;
        this.moderator = moderator;
        this.teacher = teacher;
        this.student = student;
        this.studentClass = studentClass;
        this.studentNumber = studentNumber;
        this.created = created;
    }

    public boolean isHeadmaster() {
        return headmaster;
    }

    public void setHeadmaster(boolean headmaster) {
        this.headmaster = headmaster;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public boolean isAdministrator() {
        return administrator;
    }

    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }

    public boolean isModerator() {
        return moderator;
    }

    public void setModerator(boolean moderator) {
        this.moderator = moderator;
    }

    public boolean isTeacher() {
        return teacher;
    }

    public void setTeacher(boolean teacher) {
        this.teacher = teacher;
    }

    public boolean isStudent() {
        return student;
    }

    public void setStudent(boolean student) {
        this.student = student;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public boolean isEmailValid() {
        return Patterns.EMAIL_ADDRESS.matcher(getEmail()).matches();
    }


    public boolean isPasswordLengthGreaterThan5() {
        return getPassword().length() > 10;
    }
}
