package com.adigastudio.kodesoalguru.databinding;

import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.adigastudio.kodesoalguru.R;
import com.adigastudio.kodesoalguru.models.Exam;
import com.adigastudio.kodesoalguru.utils.MyTextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;

public class TextViewBindingAdapters {

    @NonNull
    @BindingAdapter("textHtml")
    public static void setTextHtml(TextView view, String text){
        if (text == null) {
            view.setVisibility(View.GONE);
            return;
        }
        view.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
        } else {
            view.setText(Html.fromHtml(text));
        }
    }

    @NonNull
    @BindingAdapter("textDouble")
    public static void setDouble(TextView view, Double value){
        if (value != null) {
            view.setText(new MyTextView().doubleToString(value));
        }
    }

    @NonNull
    @BindingAdapter("textStudentClass")
    public static void setStudentClass(TextView view, List<String> studentClasses){
        if (studentClasses != null && studentClasses.size() > 0) {
            String studentClass = "";
            for (String item : studentClasses) {
                studentClass = studentClass.concat(item + ",");
            }
            studentClass = studentClass.substring(0, studentClass.length()-1);
            view.setText(studentClass);
        }
    }

    @NonNull
    @BindingAdapter("showResult")
    public static void setShowResult(TextView view, Boolean showResult){
        Log.d("setShowResult", "setShowResult: " + showResult);
        if (showResult == null || !showResult) {
            view.setText(view.getContext().getResources().getString(R.string.unavailable));
            view.setTextColor(view.getContext().getResources().getColor(R.color.colorDanger));
            view.setBackground(view.getContext().getResources().getDrawable(R.drawable.shape_rounded_rectangle_nocolor_danger_outline));
        } else {
            view.setText(view.getContext().getResources().getString(R.string.view_result));
            view.setTextColor(view.getContext().getResources().getColor(R.color.colorSuccess));
            view.setBackground(view.getContext().getResources().getDrawable(R.drawable.shape_rounded_rectangle_nocolor_success_outline));
        }
    }

    @BindingAdapter({"bind:isAdministrator", "bind:isHeadmaster", "bind:isModerator", "bind:isTeacher" })
    public static void setRole(TextView textView, Boolean isAdministrator, Boolean isHeadmaster, Boolean isModerator, Boolean isTeacher) {
        if (isAdministrator != null && isAdministrator) {
            textView.setText(textView.getContext().getResources().getString(R.string.is_administrator));
        }

        if (isHeadmaster != null && isHeadmaster) {
            textView.setText(textView.getContext().getResources().getString(R.string.is_headmaster));
        }

        if (isModerator != null && isModerator) {
            textView.setText(textView.getContext().getResources().getString(R.string.is_moderator));
        }

        if (isTeacher != null && isTeacher) {
            textView.setText(textView.getContext().getResources().getString(R.string.is_teacher));
        }
    }

}
























