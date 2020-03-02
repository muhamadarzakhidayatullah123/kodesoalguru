package com.adigastudio.kodesoalguru.repositories;

import android.util.Log;

import com.adigastudio.kodesoalguru.models.Exam;
import com.adigastudio.kodesoalguru.models.ExamToken;
import com.adigastudio.kodesoalguru.models.User;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExamRepository {

    private FirebaseFirestore firestore;
    private String TAG = "ExamRepository";

    private void getFirestoreInstance(){
        if(firestore == null){
            firestore = FirebaseFirestore.getInstance();
        }
    }

    public void getExams(FirestoreGetExams firestoreGetExams, int limit){
        getFirestoreInstance();
        Task<Long> taskServerTime = new CloudFunctionRepository().getServerTime();
        taskServerTime.addOnCompleteListener(taskServerTimeResult -> {
            if (taskServerTimeResult.isSuccessful()) {
                if (taskServerTimeResult.getResult() == null) {
                    NullPointerException nullPointerException = new NullPointerException("Terjadi kesalahan, mohon ulangi lagi");
                    Crashlytics.logException(nullPointerException);
                    List<Exam> exams = new ArrayList<>();
                    exams.add(new Exam(nullPointerException));
                    firestoreGetExams.onCallback(exams);
                    return;
                }
                new UserRepository().getUserDetail(user -> {
                    if (user == null) {
                        firestoreGetExams.onCallback(null);
                    } else {
                        firestore.collection("exams")
                                .whereEqualTo("user", user.getUserId())
                                .orderBy("datetime", Query.Direction.DESCENDING).limit(limit).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (task.getResult() == null) {
                                    NullPointerException nullPointerException = new NullPointerException("Terjadi kesalahan, mohon ulangi lagi");
                                    Crashlytics.logException(nullPointerException);
                                    List<Exam> exams = new ArrayList<>();
                                    exams.add(new Exam(nullPointerException));
                                    firestoreGetExams.onCallback(exams);
                                    return;
                                }
                                if (task.getResult().getDocuments().size() == 0) {
                                    firestoreGetExams.onCallback(null);
                                    return;
                                }
                                List<Exam> exams = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    exams.add(new Exam(
                                            document.getId(),
                                            document.getString("title"),
                                            document.getString("subtitle"),
                                            document.getString("subject"),
                                            document.getString("user"),
                                            document.getString("instance"),
                                            (List<String>) document.get("class"),
                                            document.getTimestamp("datetime").toDate(),
                                            Integer.parseInt(document.getString("duration")),
                                            document.getString("year"),
                                            document.getBoolean("show_result"),
                                            document.getBoolean("active"),
                                            document.getString("information"),
                                            new Date(taskServerTimeResult.getResult()),
                                            document.getTimestamp("created").toDate()
                                    ));
                                }
                                firestoreGetExams.onCallback(exams);
                            } else {
                                List<Exam> exams = new ArrayList<>();
                                exams.add(new Exam(task.getException()));
                                firestoreGetExams.onCallback(exams);
                            }
                        });
                    }
                });
            } else {
                List<Exam> exams = new ArrayList<>();
                exams.add(new Exam(taskServerTimeResult.getException()));
                firestoreGetExams.onCallback(exams);
            }
        });
    }

    public void getDetailExam(FirestoreGetDetailExam firestoreGetDetailExam, String examId){
        getFirestoreInstance();
        if (examId == null) {
            NullPointerException nullPointerException = new NullPointerException("Ujian tidak ditemukan");
            Crashlytics.logException(nullPointerException);
            firestoreGetDetailExam.onCallback(new Exam(nullPointerException));
            return;
        }
        Task<Long> taskServerTime = new CloudFunctionRepository().getServerTime();
        taskServerTime.addOnCompleteListener(taskServerTimeResult -> {
            if (taskServerTimeResult.isSuccessful()) {
                if (taskServerTimeResult.getResult() == null) {
                    NullPointerException nullPointerException = new NullPointerException("Terjadi kesalahan, mohon ulangi lagi");
                    Crashlytics.logException(nullPointerException);
                    firestoreGetDetailExam.onCallback(new Exam(nullPointerException));
                    return;
                }
                DocumentReference docRef = firestore.collection("exams").document(examId);
                docRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document == null) {
                            NullPointerException nullPointerException = new NullPointerException("Terjadi kesalahan, mohon ulangi lagi");
                            Crashlytics.logException(nullPointerException);
                            firestoreGetDetailExam.onCallback(new Exam(nullPointerException));
                            return;
                        }
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            Exam exam = new Exam(
                                    document.getId(),
                                    document.getString("title"),
                                    document.getString("subtitle"),
                                    document.getString("subject"),
                                    document.getString("user"),
                                    document.getString("instance"),
                                    (List<String>) document.getData().get("class"),
                                    document.getTimestamp("datetime").toDate(),
                                    Integer.parseInt(document.getString("duration")),
                                    document.getString("year"),
                                    document.getBoolean("show_result"),
                                    document.getBoolean("active"),
                                    document.getString("information"),
                                    new Date(taskServerTimeResult.getResult()),
                                    document.getTimestamp("created").toDate()
                            );
                            firestoreGetDetailExam.onCallback(exam);
                        } else {
                            Log.d(TAG, "No such document");
                            firestoreGetDetailExam.onCallback(null);
                        }
                    } else {
                        firestoreGetDetailExam.onCallback(new Exam(task.getException()));
                    }
                });
            } else {
                firestoreGetDetailExam.onCallback(new Exam(taskServerTimeResult.getException()));
            }
        });
    }

    public interface FirestoreGetExams {
        void onCallback(List<Exam> exam);
    }

    public interface FirestoreGetDetailExam {
        void onCallback(Exam exam);
    }
}
