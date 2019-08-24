package com.adigastudio.kodesoalguru.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;

import com.adigastudio.kodesoalguru.MainApplication;
import com.adigastudio.kodesoalguru.R;
import com.adigastudio.kodesoalguru.databinding.ForgotPasswordActivityBinding;
import com.adigastudio.kodesoalguru.interfaces.MyListeners.OnClickListener;
import com.adigastudio.kodesoalguru.models.ForgotPassword;
import com.adigastudio.kodesoalguru.utils.AdConfig;
import com.adigastudio.kodesoalguru.utils.CheckConnectionData;
import com.adigastudio.kodesoalguru.utils.MyErrorHandling;
import com.adigastudio.kodesoalguru.utils.MyProgressBar;
import com.adigastudio.kodesoalguru.utils.MySnackBar;
import com.adigastudio.kodesoalguru.viewmodels.ForgotPasswordViewModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.snackbar.Snackbar;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import pub.devrel.easypermissions.EasyPermissions;

import static com.adigastudio.kodesoalguru.utils.MyEnum.FAILED_MESSAGE;

public class ForgotPassowrdActivity extends AppCompatActivity {
    String TAG = "ForgotPassowrdActivity";
    private ForgotPasswordViewModel viewModel;
    private ForgotPasswordActivityBinding binding;
    private OnClickListener clickListener;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(ForgotPassowrdActivity.this, R.layout.forgot_password_activity);

        binding.adView.loadAd(new AdConfig().getAdRequest());
        loadInterstitialAd();

        viewModel = ViewModelProviders.of(this).get(ForgotPasswordViewModel.class);
        binding.setViewModel(viewModel);
        MyProgressBar.init(this, binding.appBar.progressBar, android.R.color.white);

        clickListener = v -> {
            if (v.getId() == binding.buttonSend.getId()) {
                String email = binding.editEmail.getText().toString();
                if (TextUtils.isEmpty(Objects.requireNonNull(email))) {
                    MySnackBar.Show(binding.getRoot(), getString(R.string.error_empty_email), Snackbar.LENGTH_LONG, FAILED_MESSAGE);
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    MySnackBar.Show(binding.getRoot(),getString(R.string.error_invalid_email), Snackbar.LENGTH_LONG, FAILED_MESSAGE);
                }
                else {
                    viewModel.forgotPassword(email);
                }
            }
        };
        viewModel.setCallback(clickListener);

        viewModel.getIsLoading().observe(this, status -> {
            if (status) {
                showProgressBar();
            } else {
                hideProgressBar();
            }
        });

        viewModel.getForgotPassword().observe(this, forgotPassword -> {
            if (interstitialAd.isLoaded()) {
                interstitialAd.show();
                interstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        getForgotPasswordStatus(forgotPassword);
                    }
                });
            } else {
                getForgotPasswordStatus(forgotPassword);
            }
        });

        CheckConnectionData.Check(this, this, binding.getRoot());
    }

    private void loadInterstitialAd(){
        if (interstitialAd == null) {
            interstitialAd = new InterstitialAd(getApplicationContext());
            interstitialAd.setAdUnitId(getString(R.string.ad_unit_interstitial));
        }
        interstitialAd.loadAd(new AdConfig().getAdRequest());
    }

    private void getForgotPasswordStatus(ForgotPassword forgotPassword) {

        if (forgotPassword.getError() == null) {
            new FancyGifDialog.Builder(this)
                    .setTitle(getString(R.string.success))
                    .setMessage(getString(R.string.success_send_forgot_password_email))
                    .setPositiveBtnText(getString(R.string.back))
                    .setGifResource(R.drawable.img_success)
                    .isCancellable(false)
                    .OnPositiveClicked(() -> {
                        onBackPressed();
                    })
                    .build();
        } else {
            new FancyGifDialog.Builder(this)
                    .setTitle(getString(R.string.error_title))
                    .setMessage(MyErrorHandling.getErrorMessageFromThrowable(forgotPassword.getError()))
                    .setPositiveBtnText(getString(R.string.back))
                    .setGifResource(R.drawable.img_danger)
                    .isCancellable(false)
                    .OnPositiveClicked(() -> {
                        loadInterstitialAd();
                    })
                    .build();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideProgressBar();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.adView.setAdListener(null);
        binding.adView.destroy();
        interstitialAd.setAdListener(null);
        MainApplication.getRefWatcher(getApplicationContext()).watch(this);
    }
}