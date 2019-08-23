package com.adigastudio.kodesoalguru.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adigastudio.kodesoalguru.MainApplication;
import com.adigastudio.kodesoalguru.R;
import com.adigastudio.kodesoalguru.adapters.ExamAdapter;
import com.adigastudio.kodesoalguru.adapters.EmptyAdapter;
import com.adigastudio.kodesoalguru.databinding.ExamFragmentBinding;
import com.adigastudio.kodesoalguru.databinding.TimerBindingAdapters;
import com.adigastudio.kodesoalguru.interfaces.MyListeners;
import com.adigastudio.kodesoalguru.interfaces.MyListeners.OnItemClickedListener;
import com.adigastudio.kodesoalguru.models.Exam;
import com.adigastudio.kodesoalguru.utils.AdConfig;
import com.adigastudio.kodesoalguru.utils.MyErrorHandling;
import com.adigastudio.kodesoalguru.viewmodels.ExamViewModel;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.ButterKnife;

import static com.adigastudio.kodesoalguru.utils.MyEnum.DEFAULT_QUERY_LIMIT;

public class ExamFragment extends Fragment {
    private ExamViewModel viewModel;
    private ExamFragmentBinding binding;
    private ExamAdapter adapter;
    private String TAG = "ExamFragment";
    private OnItemClickedListener<Exam> onItemClickedListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        new TimerBindingAdapters().removeHandler();
        binding.adView.setAdListener(null);
        binding.adView.destroy();
        MainApplication.getRefWatcher(getContext().getApplicationContext()).watch(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.exam_fragment, container, false);
        ButterKnife.bind(this, binding.getRoot());

        binding.adView.loadAd(new AdConfig().getAdRequest());
        viewModel = ViewModelProviders.of(this).get(ExamViewModel.class);

        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setAdapter(new EmptyAdapter());

        viewModel.init(DEFAULT_QUERY_LIMIT);
        binding.appBar.toolbar.setNavigationIcon(null);

//        onItemClickedListener = item -> {
//            Intent intent = new Intent(getContext(), DetailExamActivity.class);
//            intent.putExtra("exam_id", item.getExamId());
//            startActivity(intent);
//        };

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

        viewModel.getExams().observe(getViewLifecycleOwner(), exams -> {
            initRecyclerView(exams);
        });

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.refresh(DEFAULT_QUERY_LIMIT);
        });

        binding.editSearch.setOnTextChangedListener(text -> {
            adapter.getFilter().filter(text);
        });

        return binding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (binding.swipeRefreshLayout != null) {
            binding.swipeRefreshLayout.setRefreshing(false);
            binding.swipeRefreshLayout.destroyDrawingCache();
            binding.swipeRefreshLayout.clearAnimation();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.refresh(DEFAULT_QUERY_LIMIT);
    }

    private void initRecyclerView(List<Exam> exams){
        adapter = new ExamAdapter(getContext(), exams, onItemClickedListener);
        if (adapter.getItemCount() > 0) {
            if (exams.get(0).getError() != null) {
                Intent intent = new Intent(getContext(), ErrorActivity.class);
                intent.putExtra("error_message", MyErrorHandling.getErrorMessageFromThrowable(exams.get(0).getError()));
                startActivity(intent);
                return;
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
}