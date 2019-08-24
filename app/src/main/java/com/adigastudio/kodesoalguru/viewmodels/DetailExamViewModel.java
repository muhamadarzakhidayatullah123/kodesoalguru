package com.adigastudio.kodesoalguru.viewmodels;

import android.view.View;

import com.adigastudio.kodesoalguru.interfaces.MyListeners.OnClickListener;
import com.adigastudio.kodesoalguru.models.Exam;
import com.adigastudio.kodesoalguru.repositories.ExamRepository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DetailExamViewModel extends ViewModel {

    private MutableLiveData<Exam> data = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private OnClickListener callback;

    public void init(String examId){
        isLoading.setValue(true);
        new ExamRepository().getDetailExam(exam -> {
            data.setValue(exam);
            isLoading.postValue(false);
        }, examId);
    }

    public void onClicked(OnClickListener callback, View view){
        callback.onClicked(view);
    }

    public void setCallback(OnClickListener callback){
        this.callback = callback;
    }

    public OnClickListener getCallback() {
        return this.callback;
    }

    public LiveData<Exam> getDetailExam(){
        return data;
    }

    public LiveData<Boolean> getIsLoading(){
        return isLoading;
    }

}
