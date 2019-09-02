package com.adigastudio.kodesoalguru.utils;

import com.google.android.gms.ads.AdRequest;

public class AdConfig {
    public AdRequest getAdRequest(){
        return new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("69CB51EB1C49511CF7258A2E2421973E") //J5
                .build();
    }
}
