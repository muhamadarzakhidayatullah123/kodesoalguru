package com.adigastudio.kodesoalguru.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.adigastudio.kodesoalguru.MainApplication;
import com.adigastudio.kodesoalguru.R;
import com.adigastudio.kodesoalguru.adapters.EmptyAdapter;
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

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import static com.adigastudio.kodesoalguru.utils.MyEnum.ADD_DATA;
import static com.adigastudio.kodesoalguru.utils.MyEnum.REMOVE_DATA;
import static com.adigastudio.kodesoalguru.utils.MyEnum.UPDATE_DATA;

public class QuestionActivity extends AppCompatActivity {
    private QuestionViewModel viewModel;
    private QuestionActivityBinding binding;
    private QuestionAdapter adapter;
    private OnItemClickedListener<Question> onItemClickedListener;

    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();
    private AdLoader adLoader;
    private List<Object> mRecyclerViewItems = new ArrayList<>();
    private RecyclerView.LayoutManager linearLayoutManager;

    private int REPEAT_OF_ADS = 4;
    private int numberOfAds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(QuestionActivity.this, R.layout.question_activity);
        Exam exam = Parcels.unwrap(getIntent().getParcelableExtra("exam"));
        binding.setExam(exam);
        binding.appBar.toolbar.setSubtitle(exam.getTitle());
        checkUser();
        binding.adView.loadAd(new AdConfig().getAdRequest());

        viewModel = ViewModelProviders.of(this).get(QuestionViewModel.class);
        viewModel.init(exam.getExamId());

        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setAdapter(new EmptyAdapter());

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

        viewModel.getQuestions().observe(this, questions -> {
            if (questions.size() > 0) {
                Log.d("questions", "onCreate: " + mRecyclerViewItems.size());
                numberOfAds = mRecyclerViewItems.size() / REPEAT_OF_ADS;
                switch (questions.get(0).getAction()) {
                    case ADD_DATA:
                        mRecyclerViewItems.addAll(questions);
                        break;
                    case UPDATE_DATA:
                        int updateIndex = findIndex(questions);
                        mRecyclerViewItems.set(updateIndex, questions.get(0));
                        break;
                    case REMOVE_DATA:
                        int removeIndex = findIndex(questions);
                        mRecyclerViewItems.remove(removeIndex);
                        break;
                }
                mNativeAds.clear();
                removeNativeAds();
                loadNativeAds();
            }

            initRecyclerView();
        });

        binding.editSearch.setOnTextChangedListener(text -> {
            adapter.getFilter().filter(text);
        });
    }

    private Integer findIndex(List<Question> questions){
        for (int i = 0; i < mRecyclerViewItems.size(); i++) {
            if (mRecyclerViewItems.get(i) instanceof Question) {
                Question question = (Question) mRecyclerViewItems.get(i);
                if (questions.get(0).getQuestionId().equals(question.getQuestionId())) {
                    return i;
                }
            }
        }
        return null;
    }

    private void removeNativeAds(){
        for (int i = 0; i < mRecyclerViewItems.size(); i++) {
            if (mRecyclerViewItems.get(i) instanceof UnifiedNativeAd) {
                mRecyclerViewItems.remove(i);
            }
        }
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



        Log.d("initRecyclerView", "mRecyclerViewItems: " + adapter.getItemCount());
        binding.recyclerView.scrollToPosition(adapter.getItemCount() -1);
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