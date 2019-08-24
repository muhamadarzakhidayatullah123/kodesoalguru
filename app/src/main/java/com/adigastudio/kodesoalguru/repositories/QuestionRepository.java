package com.adigastudio.kodesoalguru.repositories;

import android.util.Log;

import com.adigastudio.kodesoalguru.models.Question;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;

import static com.adigastudio.kodesoalguru.utils.MyEnum.ADD_DATA;
import static com.adigastudio.kodesoalguru.utils.MyEnum.REMOVE_DATA;
import static com.adigastudio.kodesoalguru.utils.MyEnum.UPDATE_DATA;
import static com.google.firebase.firestore.Query.Direction.DESCENDING;

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
        Log.d(TAG, "examId: " + examId);
        firestore.collection("questions")
                .whereEqualTo("exam", examId)
                .orderBy("created", DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            List<Question> questions = new ArrayList<>();
                            questions.add(new Question(e));
                            firestoreGetQuestions.onCallback(questions);
                            return;
                        }
                        List<Question> questions = new ArrayList<>();
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            Question question = new Question();
                            question.setQuestionId(dc.getDocument().getId());
                            question.setQuestion(dc.getDocument().getString("question"));
                            question.setQuestionImage(dc.getDocument().getString("question_image"));
                            question.setQuestionImageStatus(dc.getDocument().getBoolean("question_image_status"));
                            question.setQuestionImageInformation(dc.getDocument().getString("question_image_information"));
                            question.setChoiceA(dc.getDocument().getString("choice_a"));
                            question.setChoiceB(dc.getDocument().getString("choice_b"));
                            question.setChoiceC(dc.getDocument().getString("choice_c"));
                            question.setChoiceD(dc.getDocument().getString("choice_d"));
                            question.setChoiceE(dc.getDocument().getString("choice_e"));
                            question.setChoiceImageA(dc.getDocument().getString("choice_image_a"));
                            question.setChoiceImageB(dc.getDocument().getString("choice_image_b"));
                            question.setChoiceImageC(dc.getDocument().getString("choice_image_c"));
                            question.setChoiceImageD(dc.getDocument().getString("choice_image_d"));
                            question.setChoiceImageE(dc.getDocument().getString("choice_image_e"));
                            question.setChoiceImageStatus(dc.getDocument().getBoolean("choice_image_status"));
                            question.setCorrectChoice(dc.getDocument().getString("correct_choice"));
                            question.setBasic(dc.getDocument().getString("basic"));
                            question.setUserId(dc.getDocument().getString("user"));
                            question.setExamId(dc.getDocument().getString("exam"));
                            question.setCreated(dc.getDocument().getTimestamp("created").toDate());
                            Log.d(TAG, "onEvent: " + dc.getDocument().getData().get("question"));
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "New city: " + dc.getDocument().getData());
                                    question.setAction(ADD_DATA);
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                    question.setAction(UPDATE_DATA);
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                    question.setAction(REMOVE_DATA);
                                    break;
                            }
                            questions.add(question);
                        }

                        firestoreGetQuestions.onCallback(questions);
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
