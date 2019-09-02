package com.adigastudio.kodesoalguru.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.adigastudio.kodesoalguru.BuildConfig;
import com.adigastudio.kodesoalguru.MainApplication;
import com.adigastudio.kodesoalguru.R;
import com.adigastudio.kodesoalguru.databinding.HomeActivityBinding;
import com.adigastudio.kodesoalguru.interfaces.MyListeners;
import com.adigastudio.kodesoalguru.interfaces.MyListeners.OnClickListener;
import com.adigastudio.kodesoalguru.interfaces.MyListeners.OnNavigationItemSelectedListener;
import com.adigastudio.kodesoalguru.utils.CheckConnectionData;
import com.adigastudio.kodesoalguru.utils.InitFragment;
import com.adigastudio.kodesoalguru.utils.MyErrorHandling;
import com.adigastudio.kodesoalguru.utils.MyProgressBar;
import com.adigastudio.kodesoalguru.utils.MySnackBar;
import com.adigastudio.kodesoalguru.viewmodels.AppUpdateViewModel;
import com.adigastudio.kodesoalguru.viewmodels.HomeViewModel;
import com.adigastudio.kodesoalguru.viewmodels.LoginViewModel;
import com.adigastudio.kodesoalguru.viewmodels.SettingViewModel;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuthException;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import static com.adigastudio.kodesoalguru.utils.MyEnum.FAILED_MESSAGE;
import static com.adigastudio.kodesoalguru.utils.MyEnum.FRAGMENT;

public class HomeActivity extends AppCompatActivity {

    private HomeViewModel viewModel;
    private AppUpdateViewModel appUpdateViewModel;
    private HomeActivityBinding binding;
    private String TAG = "HomeActivity";
    private OnNavigationItemSelectedListener onNavigationItemSelectedListener;
    private OnClickListener clickListener;
    private int DELAY_IN_MINUTES = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(HomeActivity.this, R.layout.home_activity);

        checkUser();

        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        appUpdateViewModel = ViewModelProviders.of(this).get(AppUpdateViewModel.class);
        MyProgressBar.init(this, binding.progressBar, R.color.colorPrimary);

        binding.setViewModel(viewModel);

        handler = new Handler(getMainLooper());

        onNavigationItemSelectedListener = new OnNavigationItemSelectedListener() {
            @Override
            public void OnNavigationItemSelected(MenuItem item) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
                switch (item.getItemId()) {
                    case R.id.action_exam:
                        if (!(currentFragment instanceof ExamFragment)) {
                            new InitFragment().init(getSupportFragmentManager(), new ExamFragment());
                        }
                        break;
                    case R.id.action_help:
                        if (!(currentFragment instanceof HelpFragment)) {
                            new InitFragment().init(getSupportFragmentManager(), new HelpFragment());
                        }
                        break;
                    case R.id.action_setting:
                        if (!(currentFragment instanceof SettingFragment)) {
                            new InitFragment().init(getSupportFragmentManager(), new SettingFragment());
                        }
                        break;
                }
            }
        };
        viewModel.setCallback(onNavigationItemSelectedListener);

        clickListener = v -> {
            if (v.getId() == binding.buttonSend.getId()) {
                viewModel.sendEmail();
            }
        };
        viewModel.setClickCallback(clickListener);

        viewModel.getSendEmailStatus().observe(this, user -> {
            if (user.getError() == null) {
                delay = TimeUnit.MINUTES.toMillis(DELAY_IN_MINUTES);
                handler.postDelayed(runLogout, 1000);
                new FancyGifDialog.Builder(HomeActivity.this)
                        .setTitle(getString(R.string.success))
                        .setMessage(String.format(getString(R.string.success_send_verify_email), user.getEmail()))
                        .setPositiveBtnText(getString(R.string.back))
                        .setGifResource(R.drawable.img_success)
                        .isCancellable(false)
                        .OnPositiveClicked(() -> {
                            binding.imageError.setImageResource(R.drawable.ic_email_success_96dp);
                            binding.textError.setText(String.format(getString(R.string.success_send_verify_email), user.getEmail()));
                        })
                        .build();
            } else {
                Log.d(TAG, "getSendEmailStatus: " + user.getError());
                new FancyGifDialog.Builder(HomeActivity.this)
                        .setTitle(getString(R.string.failed))
                        .setMessage(MyErrorHandling.getErrorMessageFromThrowable(user.getError()))
                        .setPositiveBtnText(getString(R.string.back))
                        .setGifResource(R.drawable.img_danger)
                        .isCancellable(false)
                        .OnPositiveClicked(() -> {

                            try {
                                if (((FirebaseAuthException) user.getError()).getErrorCode().equals("ERROR_USER_TOKEN_EXPIRED")) {
                                    SettingViewModel viewModel = ViewModelProviders.of(this).get(SettingViewModel.class);
                                    viewModel.doLogout();
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            } catch (Exception err) {
                                Log.d(TAG, "onCreate: " + err.getMessage());
                            }
                        })
                        .build();
            }
        });

        viewModel.getReloadStatus().observe(this, user -> {
            if (user.getError() == null) {
                if (user.isVerified()) {
                    checkUser();
                    removeHandler();
                }
            } else {
                MySnackBar.Show(binding.getRoot(), MyErrorHandling.getErrorMessageFromThrowable(user.getError()), Toast.LENGTH_LONG, FAILED_MESSAGE);
                removeHandler();
            }
        });

        viewModel.getIsLoading().observe(this, status -> {
            if(status){
                showProgressBar();
            }
            else{
                hideProgressBar();
            }
        });

        Fragment fragment;
        String target;
        try {
            target = getIntent().getStringExtra(FRAGMENT);
            if (target.equals(getString(R.string.exam_fragment))) {
                fragment = new ExamFragment();
                binding.bottomNavigation.setSelectedItemId(R.id.action_exam);
            } else if (target.equals(getString(R.string.setting_fragment))) {
                fragment = new SettingFragment();
                binding.bottomNavigation.setSelectedItemId(R.id.action_setting);
            } else if (target.equals(getString(R.string.help_fragment))) {
                fragment = new HelpFragment();
                binding.bottomNavigation.setSelectedItemId(R.id.action_help);
            } else {
                fragment = new ExamFragment();
                binding.bottomNavigation.setSelectedItemId(R.id.action_exam);
            }
        } catch (Exception e) {
            fragment = new ExamFragment();
        }
        new InitFragment().init(getSupportFragmentManager(), fragment);

        CheckConnectionData.Check(this, this, binding.getRoot());
    }

    private long delay = TimeUnit.MINUTES.toMillis(DELAY_IN_MINUTES);
    private Handler handler;
    private Runnable runLogout = new Runnable() {
        @Override
        public void run() {
            Log.d("HomeActivity", "run: LOGOUT");
            delay = delay - TimeUnit.SECONDS.toMillis(1);
            binding.textTimer.setVisibility(View.VISIBLE);
            binding.buttonSend.setVisibility(View.GONE);
            binding.textTimer.setText(String.format(Locale.getDefault(), "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(delay) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(delay)),
                    TimeUnit.MILLISECONDS.toSeconds(delay) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(delay))));

            handler.postDelayed(runLogout, 1000);
            viewModel.reload();

            if (delay <= 0) {
                binding.imageError.setImageResource(R.drawable.ic_error_96dp);
                binding.textError.setText(getString(R.string.error_email_not_verified));
                binding.textTimer.setVisibility(View.GONE);
                binding.buttonSend.setVisibility(View.VISIBLE);
                removeHandler();
            }
        }
    };

    private void removeHandler(){
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        hideProgressBar();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeHandler();
        MainApplication.getRefWatcher(getApplicationContext()).watch(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void checkUser(){
        LoginViewModel loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        loginViewModel.getCurrentUser().observe(this, user -> {
            if (user == null) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Crashlytics.setUserIdentifier(user.getEmail());
                if (!user.isEmailVerified()) {
                    binding.bottomNavigation.setVisibility(View.GONE);
                    binding.contentFrame.setVisibility(View.GONE);
                    binding.layoutVerify.setVisibility(View.VISIBLE);
                } else {
                    binding.bottomNavigation.setVisibility(View.VISIBLE);
                    binding.contentFrame.setVisibility(View.VISIBLE);
                    binding.layoutVerify.setVisibility(View.GONE);

                    appUpdateViewModel.init();
                    appUpdateViewModel.getCheckVersion().observe(this, appUpdate -> {
                        if (appUpdate != null) {
                            if (appUpdate.getVersionCode() > BuildConfig.VERSION_CODE) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this)
                                        .setCancelable(false)
                                        .setTitle(R.string.new_update_available);

                                if (appUpdate.isForceUpdate()) {
                                    builder.setMessage(R.string.new_update_force_action)
                                            .setPositiveButton(R.string.ok, null);
                                } else {
                                    builder.setMessage(R.string.new_update_action)
                                            .setPositiveButton(R.string.yes, null)
                                            .setNegativeButton(R.string.pending, null);
                                }

                                AlertDialog alertDialog = builder.create();
                                alertDialog.setOnShowListener(dialog -> {
                                    Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                    button.setOnClickListener(view -> {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_store_url)));
                                        startActivity(intent);
                                    });
                                });
                                alertDialog.show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void showProgressBar(){
        binding.progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgressBar(){
        binding.progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
