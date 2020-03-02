package com.adigastudio.kodesoalguru.databinding;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.adigastudio.kodesoalguru.R;
import com.adigastudio.kodesoalguru.models.Exam;
import com.adigastudio.kodesoalguru.utils.MyDatetime;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import androidx.databinding.BindingAdapter;

public class TimerBindingAdapters {
    static Handler handler = new Handler();

//    @BindingAdapter({"bind:serverDate", "bind:examDate", "bind:duration", "bind:realtime"})
    public static void setStatus(View view, Date serverDate, Date examDate, int duration, boolean isRealtime) {
        TextView textView = (TextView) view;
        if (serverDate == null || examDate == null) {
            textView.setText("--:--:--");
            return;
        }
        Context context = textView.getContext();
        long serverMillis = serverDate.getTime();
        long examMillis = examDate.getTime();
        long durationMillis = TimeUnit.MINUTES.toMillis(duration);
        long differenceMillis = examMillis - serverMillis;

        String remainingTime;
        if (differenceMillis >= 0) {
            if (isRealtime) {
                remainingTime = MyDatetime.getRealtimeRemainingTimeString(differenceMillis);
            } else {
                remainingTime = MyDatetime.getRemainingTimeString(differenceMillis);
            }
            textView.setBackground(context.getResources().getDrawable(R.drawable.shape_rounded_rectangle_danger_outline));
            textView.setTextColor(context.getResources().getColor(R.color.colorDanger));
        } else {
            differenceMillis = Math.abs(differenceMillis);
            if (differenceMillis >= durationMillis) {
                remainingTime = "SELESAI";
                textView.setBackground(context.getResources().getDrawable(R.drawable.shape_rounded_rectangle_info_outline));
                textView.setTextColor(context.getResources().getColor(R.color.colorHint));
            } else {
                remainingTime = "DIMULAI";
                textView.setBackground(context.getResources().getDrawable(R.drawable.shape_rounded_rectangle_success));
                textView.setTextColor(context.getResources().getColor(android.R.color.white));
            }
        }
        textView.setText(remainingTime);
    }

    @BindingAdapter({"bind:exam", "bind:isRealtime" })
    public static void setExamStatus(View view, Exam exam, boolean isRealtime) {
        if (exam == null || exam.getDatetime() == null) {
            return;
        }

        if (isRealtime) {
            Runnable r = new counter(view, exam, true);
            handler.postDelayed(r, 1000);
        } else {
            setStatus(view, exam.getServerDate(), exam.getDatetime(), exam.getDuration(), false);
        }
    }

    private static class counter implements Runnable {
        private View view;
        private Exam exam;
        private boolean isRealtime;
        private long examDateMillis;
        private final WeakReference<View> viewRef;

        public counter(View view, Exam exam, boolean isRealtime) {
            this.view = view;
            this.exam = exam;
            this.isRealtime = isRealtime;
            this.examDateMillis = exam.getDatetime().getTime();
            this.viewRef = new WeakReference<View>(view);
        }

        @Override
        public void run() {
            final View myView = viewRef.get();
            if (view.getId() == R.id.text_status) {
                setStatus(myView, exam.getServerDate(), new Date(examDateMillis), exam.getDuration(), isRealtime);
            } else if (view.getId() == R.id.text_timer) {
                setExamTimer(myView, exam.getServerDate(), new Date(examDateMillis), exam.getDuration());
            } else {
                setButton(myView, exam.getServerDate(), new Date(examDateMillis), exam.getDuration());
            }
            examDateMillis = examDateMillis - 1000;
            handler.postDelayed(this, 1000);
        }

        private void setExamTimer(View view, Date realtimeServerDate, Date realtimeExamDate, int realtimeDuration){
            TextView textView = (TextView) view;
            if (realtimeServerDate == null || realtimeExamDate == null) {
                textView.setText("--:--:--");
                return;
            }
            long differenceMillis = realtimeServerDate.getTime() - realtimeExamDate.getTime();
            long durationMillis = TimeUnit.MINUTES.toMillis(realtimeDuration);
            String remainingTime;
            if (differenceMillis <= durationMillis) {
                remainingTime = MyDatetime.getExamTimeString(durationMillis - differenceMillis);
            } else {
                remainingTime = "SELESAI";
//                view.getRootView().findViewById(R.id.congratulation_layout).setVisibility(View.VISIBLE);
            }
            textView.setText(remainingTime);
        }

        private void setButton(View view, Date realtimeServerDate, Date realtimeExamDate, int realtimeDuration){
            Button button = (Button) view;
            if (realtimeServerDate == null || realtimeExamDate == null) {
                button.setTextColor(button.getContext().getResources().getColor(R.color.colorHint));
                button.setBackground(button.getContext().getResources().getDrawable(R.drawable.shape_rounded_rectangle_info_outline));
                button.setEnabled(false);
                return;
            }
            long differenceMillis = realtimeServerDate.getTime() - realtimeExamDate.getTime();
            if (differenceMillis >= 0 && differenceMillis <= TimeUnit.MINUTES.toMillis(realtimeDuration)) {
                button.setBackground(button.getContext().getResources().getDrawable(R.drawable.shape_rounded_rectangle_button));
                button.setTextColor(button.getContext().getResources().getColor(android.R.color.white));
                button.setEnabled(true);
            } else {
                button.setTextColor(button.getContext().getResources().getColor(R.color.colorHint));
                button.setBackground(button.getContext().getResources().getDrawable(R.drawable.shape_rounded_rectangle_info_outline));
                button.setEnabled(false);
            }
        }
    }

    public void removeHandler(){
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            Log.d("removeHandler", "removeHandler: removed");
        }
    }
}