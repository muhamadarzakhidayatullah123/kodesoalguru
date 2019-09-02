package com.adigastudio.kodesoalguru.viewmodels;

import com.adigastudio.kodesoalguru.models.AppUpdate;
import com.adigastudio.kodesoalguru.repositories.AppUpdateRepository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AppUpdateViewModel extends ViewModel {
    private MutableLiveData<AppUpdate> appUpdate = new MutableLiveData<>();

    public void init(){
        new AppUpdateRepository().checkVersion(response -> {
            appUpdate.setValue(response);
        });
    }

    public LiveData<AppUpdate> getCheckVersion(){
        return appUpdate;
    }

}
