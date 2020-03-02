package com.adigastudio.kodesoalguru.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adigastudio.kodesoalguru.MainApplication;
import com.adigastudio.kodesoalguru.R;
import com.adigastudio.kodesoalguru.adapters.EmptyAdapter;
import com.adigastudio.kodesoalguru.adapters.ExamAdapter;
import com.adigastudio.kodesoalguru.databinding.ExamFragmentBinding;
import com.adigastudio.kodesoalguru.interfaces.MyListeners;
import com.adigastudio.kodesoalguru.interfaces.MyListeners.OnItemClickedListener;
import com.adigastudio.kodesoalguru.interfaces.MyListeners.OnItemListClickedListener;
import com.adigastudio.kodesoalguru.models.Exam;
import com.adigastudio.kodesoalguru.utils.AdConfig;
import com.adigastudio.kodesoalguru.utils.MyErrorHandling;
import com.adigastudio.kodesoalguru.utils.MySnackBar;
import com.adigastudio.kodesoalguru.utils.MyTextView;
import com.adigastudio.kodesoalguru.viewmodels.ExamViewModel;
import com.adigastudio.kodesoalguru.viewmodels.LoginViewModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.adigastudio.kodesoalguru.utils.MyEnum.DEFAULT_QUERY_LIMIT;
import static com.adigastudio.kodesoalguru.utils.MyEnum.FAILED_MESSAGE;

public class ExamFragment extends Fragment {
    private ExamViewModel viewModel;
    private ExamFragmentBinding binding;
    private ExamAdapter adapter;
    private OnItemClickedListener<Exam> onItemClickedListener;

    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();
    private AdLoader adLoader;
    private List<Object> mRecyclerViewItems = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.exam_fragment, container, false);
        checkUser();

        binding.adView.loadAd(new AdConfig().getAdRequest());

        viewModel = ViewModelProviders.of(this).get(ExamViewModel.class);
        binding.appBar.toolbar.setNavigationIcon(null);

        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setAdapter(new EmptyAdapter());

        onItemClickedListener = item -> {
            Intent intent = new Intent(getContext(), DetailExamActivity.class);
            intent.putExtra("exam_id", item.getExamId());
            startActivity(intent);
        };

        binding.layoutSearch.setVisibility(View.GONE);
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), status -> {
            if(status){
                binding.swipeRefreshLayout.setVisibility(View.GONE);
                binding.shimmerViewContainer.startShimmer();
                binding.shimmerViewContainer.setVisibility(View.VISIBLE);
            }
            else{
                binding.swipeRefreshLayout.setVisibility(View.VISIBLE);
                binding.shimmerViewContainer.stopShimmer();
                binding.shimmerViewContainer.setVisibility(View.GONE);
            }
        });

        viewModel.getIsRefreshLoading().observe(getViewLifecycleOwner(), status -> {
            if(status){
                binding.swipeRefreshLayout.setRefreshing(true);
            }
            else{
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });

        viewModel.getExams().observe(getViewLifecycleOwner(), results -> {
            mRecyclerViewItems.clear();
            mNativeAds.clear();
            insertData(results);
            initRecyclerView();
        });

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.refresh(DEFAULT_QUERY_LIMIT);
            binding.swipeRefreshLayout.setRefreshing(false);
        });

        binding.editSearch.setOnTextChangedListener(text -> {
            if (adapter == null) {
                return;
            }
            if (text != null) {
                adapter.getFilter().filter(text);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.adView.setAdListener(null);
        binding.adView.destroy();
        MainApplication.getRefWatcher(getActivity()).watch(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.init(DEFAULT_QUERY_LIMIT);
    }

    private void insertData(List<Exam> results){
        if  (results != null) {
            mRecyclerViewItems.addAll(results);
            loadNativeAds();
        }
    }

    private void initRecyclerView(){
        adapter = new ExamAdapter(getActivity(), mRecyclerViewItems, onItemClickedListener);
        if (adapter.getItemCount() > 0) {
            if (mRecyclerViewItems.get(0) instanceof Exam) {
                Exam result = (Exam) mRecyclerViewItems.get(0);
                if (result.getError() != null) {
                    Intent intent = new Intent(getActivity(), ErrorActivity.class);
                    intent.putExtra("error_message", MyErrorHandling.getErrorMessageFromThrowable(result.getError()));
                    startActivity(intent);
                    return;
                }
            }
            binding.layoutEmptyData.setVisibility(View.GONE);
            binding.layoutSearch.setVisibility(View.VISIBLE);
        } else {
            binding.layoutEmptyData.setVisibility(View.VISIBLE);
            binding.layoutSearch.setVisibility(View.GONE);
        }
        binding.recyclerView.setAdapter(adapter);
        if (adapter != null) {
            adapter.getFilter().filter(binding.editSearch.getText());
        }
    }

    private void loadNativeAds() {
        AdLoader.Builder builder = new AdLoader.Builder(getContext(), getString(R.string.ad_unit_native));
        adLoader = builder.forUnifiedNativeAd(
                unifiedNativeAd -> {
                    // A native ad loaded successfully, check if the ad loader has finished loading
                    // and if so, insert the ads into the list.
                    mNativeAds.add(unifiedNativeAd);
                    if (!adLoader.isLoading()) {
                        insertAdsInMenuItems();
                    }
                }).withAdListener(
                new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // A native ad failed to load, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                + " load another.");
                        if (!adLoader.isLoading()) {
                            insertAdsInMenuItems();
                        }
                    }
                }).build();

        // Load the Native ads.
        int numberOfAds = mRecyclerViewItems.size() / 4;
        adLoader.loadAds(new AdConfig().getAdRequest(), numberOfAds);
    }

    private void insertAdsInMenuItems() {
        if (mNativeAds.size() <= 0) {
            return;
        }

        int offset = (mRecyclerViewItems.size() / mNativeAds.size()) + 1;
        int index = 0;
        for (UnifiedNativeAd ad : mNativeAds) {
            mRecyclerViewItems.add(index, ad);
            index = index + offset;
        }

        adapter.notifyDataSetChanged();
    }

    private void checkUser(){
        LoginViewModel loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        loginViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}