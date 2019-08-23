package com.adigastudio.kodesoalguru.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.adigastudio.kodesoalguru.R;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class ClearableEditText extends RelativeLayout {

    private final String TAG = "ClearableEditText";
    private LayoutInflater inflater = null;
    private EditText edit_text;
    private ImageView img_clear;
    private ImageView img_edit;
    private OnTextChangedListener onTextChangedListener;


    public interface OnTextChangedListener {
        void onTextChangedListener(String text);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ClearableEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initViews(attrs);
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews(attrs);
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(attrs);
    }

    public ClearableEditText(Context context) {
        super(context);
        initViews(null);
    }

    private void initViews(@Nullable AttributeSet set) {
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.clearable_edit_text, this, true);
        img_edit = (ImageView) findViewById(R.id.clearable_image);
        edit_text = (EditText) findViewById(R.id.clearable_edit);
        img_clear = (ImageView) findViewById(R.id.clearable_image_clear);
        img_clear.setVisibility(RelativeLayout.INVISIBLE);

        TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.ClearableEditText);
        int backgroundRef = ta.getResourceId(R.styleable.ClearableEditText_bg, android.R.color.transparent);
        int imageRef = ta.getResourceId(R.styleable.ClearableEditText_image, -1);
        String hint = ta.getString(R.styleable.ClearableEditText_hint);
        edit_text.setBackgroundResource(backgroundRef);
        img_edit.setImageResource(imageRef);
        edit_text.setHint(hint);
        ta.recycle();

        clearText();
        showHideClearButton();
    }

    private void clearText() {
        img_clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_text.setText("");
            }
        });
    }

    private void showHideClearButton() {
        edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(onTextChangedListener != null){
                    onTextChangedListener.onTextChangedListener(s.toString());
                }
                if (s.length() > 0)
                    img_clear.setVisibility(RelativeLayout.VISIBLE);
                else
                    img_clear.setVisibility(RelativeLayout.INVISIBLE);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public Editable getText() {
        Editable text = edit_text.getText();
        return text;
    }

    public void setOnTextChangedListener(OnTextChangedListener onLoadingClickListener){
        onTextChangedListener = onLoadingClickListener;
    }
}