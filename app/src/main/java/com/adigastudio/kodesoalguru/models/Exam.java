package com.adigastudio.kodesoalguru.models;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Parcel
public class Exam {
    private String examId;
    private String title;
    private String subtitle;
    private String subject;
    private String user;
    private String institute;
    private List<String> studentClass;
    private Date datetime;
    private int duration;
    private String year;
    private Boolean showResult;
    private Boolean active;
    private String information;
    private Date serverDate;
    private Date created;
    private Throwable error;

    @ParcelConstructor
    public Exam(String examId, String title, String subtitle, String subject, String user, String institute, List<String> studentClass, Date datetime, int duration, String year, Boolean showResult, Boolean active, String information, Date serverDate, Date created) {
        this.examId = examId;
        this.title = title;
        this.subtitle = subtitle;
        this.subject = subject;
        this.user = user;
        this.institute = institute;
        this.studentClass = studentClass;
        this.datetime = datetime;
        this.duration = duration;
        this.year = year;
        this.showResult = showResult;
        this.active = active;
        this.information = information;
        this.serverDate = serverDate;
        this.created = created;
    }

    public Exam(String examId, String title, String subject, Date datetime, Boolean showResult) {
        this.examId = examId;
        this.title = title;
        this.subject = subject;
        this.datetime = datetime;
        this.showResult = showResult;
    }

    public Exam(Throwable error) {
        this.error = error;
    }

    public Boolean isAvailableToJoin(){
        long differenceMillis = serverDate.getTime() - datetime.getTime();
        return  (differenceMillis >= 0 && differenceMillis <= TimeUnit.MINUTES.toMillis(duration));
    }

    public Boolean isShowResult() {
        return showResult;
    }

    public void setShowResult(Boolean showResult) {
        this.showResult = showResult;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public List<String> getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(List<String> studentClass) {
        this.studentClass = studentClass;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getServerDate() {
        return serverDate;
    }

    public void setServerDate(Date serverDate) {
        this.serverDate = serverDate;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
