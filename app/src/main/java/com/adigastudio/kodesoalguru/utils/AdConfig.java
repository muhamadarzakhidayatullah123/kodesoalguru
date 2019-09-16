package com.adigastudio.kodesoalguru.utils;

import com.google.android.gms.ads.AdRequest;

public class AdConfig {
    public AdRequest getAdRequest(){
        return new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("69CB51EB1C49511CF7258A2E2421973E") //J5
                .addTestDevice("6E5199F72E7F4D19AE51F2823D43C589") //E5 Pink
                .build();
    }
}
