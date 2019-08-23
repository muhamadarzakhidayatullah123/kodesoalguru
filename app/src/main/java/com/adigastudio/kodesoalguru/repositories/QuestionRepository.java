package com.adigastudio.kodesoalguru.repositories;

import android.util.Log;

import com.adigastudio.kodesoalguru.models.Question;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class QuestionRepository {

    private FirebaseFirestore firestore;
    private String TAG = "QuestionRepository";

    private void getFirestoreInstance(){
        if(firestore == null){
            firestore = FirebaseFirestore.getInstance();
        }
    }

    public void getQuestions(FirestoreGetQuestions firestoreGetQuestions, String examId){
        getFirestoreInstance();
        firestore.collection("questions")
                .whereEqualTo("exam", examId)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().getDocuments().size() == 0) {
                    firestoreGetQuestions.onCallback(null);
                    return;
                }
                List<Question> questions = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    questions.add(new Question(
                            document.getId(),
                            document.getString("question"),
                            document.getString("question_image"),
                            document.getBoolean("question_image_status"),
                            document.getString("question_image_information"),
                            document.getString("choice_a"),
                            document.getString("choice_b"),
                            document.getString("choice_c"),
                            document.getString("choice_d"),
                            document.getString("choice_e"),
                            document.getString("choice_image_a"),
                            document.getString("choice_image_b"),
                            document.getString("choice_image_c"),
                            document.getString("choice_image_d"),
                            document.getString("choice_image_e"),
                            document.getBoolean("choice_image_status"),
                            document.getString("correct_choice"),
                            document.getString("basic"),
                            document.getString("user"),
                            document.getString("exam"),
                            document.getTimestamp("created").toDate()
                    ));
                }
                firestoreGetQuestions.onCallback(questions);
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
                List<Question> questions = new ArrayList<>();
                questions.add(new Question(task.getException()));
                firestoreGetQuestions.onCallback(questions);
            }
        });
    }

    public void getDetailQuestion(FirestoreGetDetailQuestion firestoreGetDetailQuestion, String questionId){
        getFirestoreInstance();
        firestore.collection("questions")
                .document(questionId)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Question question = new Question(
                            document.getId(),
                            document.getString("question"),
                            document.getString("question_image"),
                            document.getBoolean("question_image_status"),
                            document.getString("question_image_information"),
                            document.getString("choice_a"),
                            document.getString("choice_b"),
                            document.getString("choice_c"),
                            document.getString("choice_d"),
                            document.getString("choice_e"),
                            document.getString("choice_image_a"),
                            document.getString("choice_image_b"),
                            document.getString("choice_image_c"),
                            document.getString("choice_image_d"),
                            document.getString("choice_image_e"),
                            document.getBoolean("choice_image_status"),
                            document.getString("correct_choice"),
                            document.getString("basic"),
                            document.getString("user"),
                            document.getString("exam"),
                            document.getTimestamp("created").toDate());
                    firestoreGetDetailQuestion.onCallback(question);
                } else {
                    firestoreGetDetailQuestion.onCallback(null);
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                firestoreGetDetailQuestion.onCallback(new Question(task.getException()));

            }
        });
    }

    public FirebaseStorage getFirebaseStorageInstance(){
        return FirebaseStorage.getInstance();
    }

    public interface FirestoreGetQuestions {
        void onCallback(List<Question> questions);
    }

    public interface FirestoreGetDetailQuestion {
        void onCallback(Question question);
    }

}
