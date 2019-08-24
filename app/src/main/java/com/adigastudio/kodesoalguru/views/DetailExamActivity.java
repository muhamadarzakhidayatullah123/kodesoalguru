package com.adigastudio.kodesoalguru.views;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.adigastudio.kodesoalguru.MainApplication;
import com.adigastudio.kodesoalguru.R;
import com.adigastudio.kodesoalguru.databinding.DetailExamActivityBinding;
import com.adigastudio.kodesoalguru.databinding.QuestionActivityBinding;
import com.adigastudio.kodesoalguru.databinding.TimerBindingAdapters;
import com.adigastudio.kodesoalguru.interfaces.MyListeners.OnClickListener;
import com.adigastudio.kodesoalguru.utils.AdConfig;
import com.adigastudio.kodesoalguru.utils.CheckConnectionData;
import com.adigastudio.kodesoalguru.utils.MyProgressBar;
import com.adigastudio.kodesoalguru.viewmodels.DetailExamViewModel;
import com.adigastudio.kodesoalguru.viewmodels.LoginViewModel;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.firebase.auth.FirebaseUser;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;

import org.parceler.Parcels;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

public class DetailExamActivity extends AppCompatActivity {
    private DetailExamActivityBinding binding;

    private DetailExamViewModel viewModel;
    private OnClickListener clickListener;
    private String TAG = "DetailExamActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(DetailExamActivity.this, R.layout.detail_exam_activity);
        MyProgressBar.init(this, binding.appBar.progressBar, android.R.color.white);

        checkUser();
        binding.adView.loadAd(new AdConfig().getAdRequest());

        Intent intent = getIntent();
        String examId = intent.getStringExtra("exam_id");

        viewModel = ViewModelProviders.of(this).get(DetailExamViewModel.class);
        binding.setViewModel(viewModel);

        viewModel.init(examId);

        clickListener = v -> {
            if (v.getId() == binding.buttonExam.getId()) {
                onExamClick();
            }
        };
        viewModel.setCallback(clickListener);

        viewModel.getIsLoading().observe(this, status -> {
            if(status){
                binding.scrollView.setVisibility(View.GONE);
                binding.shimmerViewContainer.startShimmer();
                binding.shimmerViewContainer.setVisibility(View.VISIBLE);
                binding.buttonExam.setVisibility(View.GONE);
            }
            else{
                binding.scrollView.setVisibility(View.VISIBLE);
                binding.shimmerViewContainer.stopShimmer();
                binding.shimmerViewContainer.setVisibility(View.GONE);
                binding.buttonExam.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getDetailExam().observe(this, exam -> {
            binding.setExam(exam);
        });

        CheckConnectionData.Check(getApplicationContext(), this, binding.getRoot());
    }

    private void checkUser(){
        LoginViewModel loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        loginViewModel.getCurrentUser().observe(this, user -> {
            if (user == null) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    void onExamClick(){

        Intent intent = new Intent(getApplicationContext(), QuestionActivity.class);
        intent.putExtra("exam_id", binding.getExam().getExamId());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new TimerBindingAdapters().removeHandler();
        binding.adView.setAdListener(null);
        binding.adView.destroy();
        MainApplication.getRefWatcher(getApplicationContext()).watch(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideProgressBar();
    }

    private void showProgressBar(){
        binding.appBar.progressBar.setVisibility(View.VISIBLE);
        binding.appBar.toolbar.setNavigationIcon(null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgressBar(){
        binding.appBar.progressBar.setVisibility(View.GONE);
        binding.appBar.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_keyboard_arrow_left_white_32dp));
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
