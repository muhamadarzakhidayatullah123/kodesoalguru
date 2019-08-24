package com.adigastudio.kodesoalguru.repositories;

import android.util.Log;

import com.adigastudio.kodesoalguru.models.Exam;
import com.adigastudio.kodesoalguru.models.ExamToken;
import com.adigastudio.kodesoalguru.models.User;
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
                new UserRepository().getUserDetail(user -> {
                    if (user == null) {
                        firestoreGetExams.onCallback(null);
                    } else {
                        firestore.collection("exams")
                                .whereEqualTo("user", user.getUserId())
                                .whereEqualTo("active", true)
                                .orderBy("datetime", Query.Direction.DESCENDING).limit(limit).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
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
                                Log.d(TAG, "Error getting documents: task " + ((FirebaseFirestoreException) task.getException()).getCode());
                                List<Exam> exams = new ArrayList<>();
                                exams.add(new Exam(task.getException()));
                                firestoreGetExams.onCallback(exams);
                            }
                        });
                    }
                });
            } else {
                Log.d(TAG, "Error getting documents: taskServerTimeResult ", taskServerTimeResult.getException());
                List<Exam> exams = new ArrayList<>();
                exams.add(new Exam(taskServerTimeResult.getException()));
                firestoreGetExams.onCallback(exams);
            }
        });
    }

    public void getDetailExam(FirestoreGetDetailExam firestoreGetDetailExam, String examId){
        getFirestoreInstance();
        Task<Long> taskServerTime = new CloudFunctionRepository().getServerTime();
        taskServerTime.addOnCompleteListener(taskServerTimeResult -> {
            if (taskServerTimeResult.isSuccessful()) {
                DocumentReference docRef = firestore.collection("exams").document(examId);
                docRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
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

    public void checkToken(FirestoreCheckToken firestoreCheckToken, String examId, String token){
        getFirestoreInstance();
        Task<Long> taskServerTime = new CloudFunctionRepository().getServerTime();
        taskServerTime.addOnCompleteListener(taskServerTimeResult -> {
            if (taskServerTimeResult.isSuccessful()) {
                new UserRepository().getUserDetail(user -> firestore.collection("tokens").document(examId + "_" + user.getUserId())
                        .get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    long serverTimeMillis = new Date(taskServerTimeResult.getResult()).getTime();
                                    long tokenMillis = document.getTimestamp("created").toDate().getTime();
                                    long diffSeconds = TimeUnit.MILLISECONDS.toSeconds(serverTimeMillis - tokenMillis);
                                    if ((diffSeconds <= 60) && (diffSeconds >= 0) && (token.equals(document.getString("token")))) {
                                        firestoreCheckToken.onCallback(new ExamToken(true));
                                    } else {
                                        firestoreCheckToken.onCallback(new ExamToken(false));
                                    }
                                    Log.d(TAG, "checkToken: exists");
                                } else {
                                    firestoreCheckToken.onCallback(new ExamToken(false));
                                    Log.d(TAG, "checkToken: not exists");
                                }
                            } else {
                                firestoreCheckToken.onCallback(new ExamToken(task.getException()));
                            }
                        }));
            } else {
                firestoreCheckToken.onCallback(new ExamToken(taskServerTimeResult.getException()));
            }
        });
    }

    public interface FirestoreGetExams {
        void onCallback(List<Exam> exam);
    }

    public interface FirestoreGetDetailExam {
        void onCallback(Exam exam);
    }

    public interface FirestoreCheckToken {
        void onCallback(ExamToken examToken);
    }
}
