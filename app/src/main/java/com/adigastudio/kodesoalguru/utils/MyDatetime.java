package com.adigastudio.kodesoalguru.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MyDatetime {
    private static String TAG = "MyDatetime";
    private static long millisInFuture;
    public static String formatDate(Date inputDate){
        try {
            Locale indonesia = new Locale("in", "ID");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd LLL yyyy HH:mm", indonesia);
            return dateFormat.format(inputDate);
        }catch (Exception err) {
            err.printStackTrace();
            return err.getMessage();
        }
    }

    public static String getRemainingTimeString(Long differenceMillis){
        try {
            String remainingTime = "";
            long dayMillis = TimeUnit.DAYS.toMillis(1);
            long hourMillis = TimeUnit.HOURS.toMillis(1);
            long minuteMillis = TimeUnit.MINUTES.toMillis(1);
            long secondMillis = TimeUnit.SECONDS.toMillis(1);

            if (differenceMillis >= dayMillis) {
                remainingTime = TimeUnit.MILLISECONDS.toDays(differenceMillis) + " HARI LAGI";
            } else if (differenceMillis >= hourMillis) {
                remainingTime = TimeUnit.MILLISECONDS.toHours(differenceMillis) + " JAM LAGI";
            } else if (differenceMillis >= minuteMillis) {
                remainingTime = TimeUnit.MILLISECONDS.toMinutes(differenceMillis) + " MENIT LAGI";
            } else if (differenceMillis >= secondMillis) {
                remainingTime = TimeUnit.MILLISECONDS.toSeconds(differenceMillis) + " DETIK LAGI";
            } else {
                remainingTime = "Proses";
            }
            return remainingTime;

        }catch (Exception err) {
            err.printStackTrace();
            return "Terjadi kesalahan";
        }
    }

    public static String getRealtimeRemainingTimeString(Long differenceMillis){
        if (differenceMillis >= TimeUnit.DAYS.toMillis(1)) {
            return String.format(Locale.getDefault(), "MULAI DALAM  %02d:%02d:%02d:%02d", TimeUnit.MILLISECONDS.toDays(differenceMillis),
                    TimeUnit.MILLISECONDS.toHours(differenceMillis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(differenceMillis)),
                    TimeUnit.MILLISECONDS.toMinutes(differenceMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(differenceMillis)),
                    TimeUnit.MILLISECONDS.toSeconds(differenceMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(differenceMillis)));

        } else {
            return String.format(Locale.getDefault(), "MULAI DALAM %02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(differenceMillis),
                    TimeUnit.MILLISECONDS.toMinutes(differenceMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(differenceMillis)),
                    TimeUnit.MILLISECONDS.toSeconds(differenceMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(differenceMillis)));
        }
    }

    public static String getExamTimeString(Long durationMillis){
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(durationMillis),
                TimeUnit.MILLISECONDS.toMinutes(durationMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(durationMillis)),
                TimeUnit.MILLISECONDS.toSeconds(durationMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationMillis)));
    }
}