package com.adigastudio.kodesoalguru.utils;

import java.text.DecimalFormat;

public class MyTextView {
    public String doubleToString(Double value){
        DecimalFormat format = new DecimalFormat("0.##");
        return format.format(value);
    }
}
