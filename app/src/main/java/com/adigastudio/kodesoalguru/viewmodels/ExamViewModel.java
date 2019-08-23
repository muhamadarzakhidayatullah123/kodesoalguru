package com.adigastudio.kodesoalguru.viewmodels;

import com.adigastudio.kodesoalguru.models.Exam;
import com.adigastudio.kodesoalguru.repositories.ExamRepository;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ExamViewModel extends ViewModel {

    private String TAG = "ExamViewModel";
    private MutableLiveData<List<Exam>> data = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> isRefreshLoading = new MutableLiveData<>();

    public void init(int limit){
        isLoading.setValue(true);
        new ExamRepository().getExams(exam -> {
            data.setValue(exam);
            isLoading.postValue(false);
        }, limit);
    }

    public void refresh(int limit){
        isRefreshLoading.setValue(true);
        new ExamRepository().getExams(exam -> {
            data.setValue(exam);
            isRefreshLoading.postValue(false);
        }, limit);
    }

    public LiveData<List<Exam>> getExams(){
        return data;
    }

    public LiveData<Boolean> getIsLoading(){
        return isLoading;
    }

    public LiveData<Boolean> getIsRefreshLoading(){
        return isRefreshLoading;
    }

}
