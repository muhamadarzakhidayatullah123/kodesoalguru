package com.adigastudio.kodesoalguru.viewmodels;

import android.util.Log;

import com.adigastudio.kodesoalguru.models.Exam;
import com.adigastudio.kodesoalguru.models.Question;
import com.adigastudio.kodesoalguru.repositories.ExamRepository;
import com.adigastudio.kodesoalguru.repositories.QuestionRepository;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QuestionViewModel extends ViewModel {

    private String TAG = "QuestionViewModel";
    private MutableLiveData<List<Question>> data = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> isRefreshLoading = new MutableLiveData<>();

    public void init(String examId){
        isLoading.setValue(true);
        new QuestionRepository().getQuestions(questions -> {
            data.setValue(questions);
            isLoading.postValue(false);
        }, examId);
    }

    public LiveData<List<Question>> getQuestions(){
        return data;
    }

    public LiveData<Boolean> getIsLoading(){
        return isLoading;
    }

}
