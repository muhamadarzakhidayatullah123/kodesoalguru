package com.adigastudio.kodesoalguru.repositories;

import com.adigastudio.kodesoalguru.BuildConfig;
import com.adigastudio.kodesoalguru.models.AppUpdate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;

public class AppUpdateRepository {
    private FirebaseRemoteConfig config;

    private void initRemoteConfigSettings(){
        if(config == null){
            config = FirebaseRemoteConfig.getInstance();
        }
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(BuildConfig.DEBUG ? 0 : TimeUnit.HOURS.toSeconds(12))
                .setFetchTimeoutInSeconds(5)
                .build();
        config.setConfigSettingsAsync(configSettings);
    }

    public void checkVersion(RemoteConfigCheckVersion remoteConfigCheckVersion){
        initRemoteConfigSettings();
        String versionCode = "kodesoalguru_version_code";
        String forceUpdate = "kodesoalguru_force_update";
        HashMap<String, Object> defaults = new HashMap<>();
        defaults.put(versionCode, BuildConfig.VERSION_CODE);
        defaults.put(forceUpdate, false);
        config.setDefaultsAsync(defaults);
        config.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    remoteConfigCheckVersion.onCallback(new AppUpdate(config.getLong(versionCode), config.getBoolean(forceUpdate)));
                } else {
                    remoteConfigCheckVersion.onCallback(null);
                }
            }
        });
    }

    public interface RemoteConfigCheckVersion {
        void onCallback(AppUpdate appUpdate);
    }
}
