package com.adigastudio.kodesoalguru.viewmodels;

import android.view.View;

import com.adigastudio.kodesoalguru.interfaces.MyListeners.OnClickListener;
import com.adigastudio.kodesoalguru.interfaces.MyListeners.OnNavigationItemSelectedListener;
import com.adigastudio.kodesoalguru.models.User;
import com.adigastudio.kodesoalguru.repositories.AuthRepository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    private String TAG = "HomeViewModel";
    private OnNavigationItemSelectedListener callback;
    private OnClickListener clickCallback;
    private MutableLiveData<User> data = new MutableLiveData<>();
    private MutableLiveData<User> dataReload = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoadingReload = new MutableLiveData<>();

    public void sendEmail(){
        isLoading.setValue(true);
        new AuthRepository().sendEmail(user -> {
            data.setValue(user);
            isLoading.postValue(false);
        });
    }

    public void reload(){
        isLoadingReload.setValue(true);
        new AuthRepository().reload(user -> {
            dataReload.setValue(user);
            isLoadingReload.postValue(false);
        });
    }

    public void setCallback(OnNavigationItemSelectedListener callback){
        this.callback = callback;
    }

    public OnNavigationItemSelectedListener getCallback() {
        return this.callback;
    }

    public void setClickCallback(OnClickListener clickCallback){
        this.clickCallback = clickCallback;
    }

    public void onClicked(OnClickListener clickCallback, View view){
        clickCallback.onClicked(view);
    }

    public LiveData<User> getSendEmailStatus(){
        return data;
    }

    public LiveData<Boolean> getIsLoading(){
        return isLoading;
    }

    public LiveData<User> getReloadStatus(){
        return dataReload;
    }

    public OnClickListener getClickCallback() {
        return this.clickCallback;
    }
}