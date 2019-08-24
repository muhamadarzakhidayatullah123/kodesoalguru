package com.adigastudio.kodesoalguru.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.adigastudio.kodesoalguru.MainApplication;
import com.adigastudio.kodesoalguru.R;
import com.adigastudio.kodesoalguru.adapters.EmptyAdapter;
import com.adigastudio.kodesoalguru.adapters.ExamAdapter;
import com.adigastudio.kodesoalguru.adapters.QuestionAdapter;
import com.adigastudio.kodesoalguru.databinding.QuestionActivityBinding;
import com.adigastudio.kodesoalguru.interfaces.MyListeners.OnItemClickedListener;
import com.adigastudio.kodesoalguru.models.Exam;
import com.adigastudio.kodesoalguru.models.Question;
import com.adigastudio.kodesoalguru.utils.AdConfig;
import com.adigastudio.kodesoalguru.utils.MyErrorHandling;
import com.adigastudio.kodesoalguru.viewmodels.LoginViewModel;
import com.adigastudio.kodesoalguru.viewmodels.QuestionViewModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.adigastudio.kodesoalguru.utils.MyEnum.DEFAULT_QUERY_LIMIT;

public class QuestionActivity extends AppCompatActivity {
    private QuestionViewModel viewModel;
    private QuestionActivityBinding binding;
    private QuestionAdapter adapter;
    private OnItemClickedListener<Question> onItemClickedListener;

    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();
    private AdLoader adLoader;
    private List<Object> mRecyclerViewItems = new ArrayList<>();
    private String examId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(QuestionActivity.this, R.layout.question_activity);

        checkUser();
        binding.adView.loadAd(new AdConfig().getAdRequest());

        Intent intent = getIntent();
        examId = intent.getStringExtra("exam_id");

        viewModel = ViewModelProviders.of(this).get(QuestionViewModel.class);
        viewModel.init(examId);

        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setAdapter(new EmptyAdapter());

//        onItemClickedListener = item -> {
//            Intent intent = new Intent(getApplicationContext(), DetailExamActivity.class);
//            intent.putExtra("exam_id", item.getExamId());
//            startActivity(intent);
//        };

        binding.layoutSearch.setVisibility(View.GONE);
        viewModel.getIsLoading().observe(this, status -> {
            if(status){
                binding.mainLayout.setVisibility(View.GONE);
                binding.shimmerViewContainer.startShimmer();
                binding.shimmerViewContainer.setVisibility(View.VISIBLE);
            }
            else{
                binding.mainLayout.setVisibility(View.VISIBLE);
                binding.shimmerViewContainer.stopShimmer();
                binding.shimmerViewContainer.setVisibility(View.GONE);
            }
        });

        viewModel.getQuestions().observe(this, results -> {
            mRecyclerViewItems.clear();
            mNativeAds.clear();
            insertData(results);
            loadNativeAds();
            initRecyclerView();
        });

        binding.editSearch.setOnTextChangedListener(text -> {
            adapter.getFilter().filter(text);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.adView.setAdListener(null);
        binding.adView.destroy();
        MainApplication.getRefWatcher(getApplicationContext()).watch(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void insertData(List<Question> questions){
        mRecyclerViewItems.addAll(questions);
    }

    private void initRecyclerView(){
        adapter = new QuestionAdapter(getApplicationContext(), mRecyclerViewItems, onItemClickedListener);
        if (adapter.getItemCount() > 0) {
            if (mRecyclerViewItems.get(0) instanceof Exam) {
                Exam result = (Exam) mRecyclerViewItems.get(0);
                if (result.getError() != null) {
                    Intent intent = new Intent(getApplicationContext(), ErrorActivity.class);
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
        adapter.getFilter().filter(binding.editSearch.getText());
    }

    private void loadNativeAds() {
        AdLoader.Builder builder = new AdLoader.Builder(getApplicationContext(), getString(R.string.ad_unit_native));
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
        int numberOfAds = mRecyclerViewItems.size() / 3;
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
        loginViewModel.getCurrentUser().observe(this, user -> {
            if (user == null) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}