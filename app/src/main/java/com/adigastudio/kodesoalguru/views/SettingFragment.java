package com.adigastudio.kodesoalguru.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adigastudio.kodesoalguru.MainApplication;
import com.adigastudio.kodesoalguru.R;
import com.adigastudio.kodesoalguru.databinding.SettingFragmentBinding;
import com.adigastudio.kodesoalguru.interfaces.MyListeners.OnClickListener;
import com.adigastudio.kodesoalguru.utils.AdConfig;
import com.adigastudio.kodesoalguru.viewmodels.SettingViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class SettingFragment extends Fragment {
    String TAG = "SettingFragment";
    private SettingViewModel viewModel;
    private SettingFragmentBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.setting_fragment, container, false);

        binding.appBar.toolbar.setNavigationIcon(null);
        binding.adView.loadAd(new AdConfig().getAdRequest());
        viewModel = ViewModelProviders.of(this).get(SettingViewModel.class);
        binding.setViewModel(viewModel);

        OnClickListener clickListener = v -> {
            if (v.getId() == binding.buttonAccount.getId()) {
                Intent intent = new Intent(getContext(), DetailAccountActivity.class);
                startActivity(intent);
            } else if (v.getId() == binding.buttonTerms.getId()) {
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("title", getString(R.string.terms_activity));
                intent.putExtra("url", getString(R.string.terms_url));
                startActivity(intent);
            } else if (v.getId() == binding.buttonPrivacy.getId()) {
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("title", getString(R.string.privacy_activity));
                intent.putExtra("url", getString(R.string.privacy_url));
                startActivity(intent);
            } else if (v.getId() == binding.buttonReview.getId()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_store_url)));
                startActivity(intent);
            } else if (v.getId() == binding.buttonExit.getId()) {
                viewModel.doLogout();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };
        viewModel.setCallback(clickListener);

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.adView.setAdListener(null);
        binding.adView.destroy();
        MainApplication.getRefWatcher(getContext().getApplicationContext()).watch(this);
    }
}