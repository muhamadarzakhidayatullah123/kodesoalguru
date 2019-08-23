package com.adigastudio.kodesoalguru.utils;

import com.google.android.gms.ads.AdRequest;

public class AdConfig {
    public AdRequest getAdRequest(){
        return new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
    }
}
