package com.adigastudio.kodesoalguru.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.adigastudio.kodesoalguru.R;
import com.adigastudio.kodesoalguru.databinding.AdUnifiedExamBinding;
import com.adigastudio.kodesoalguru.databinding.ExamListBinding;
import com.adigastudio.kodesoalguru.interfaces.MyListeners.OnItemClickedListener;
import com.adigastudio.kodesoalguru.models.Exam;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ViewHolder> implements Filterable {

    private String TAG = "ExamAdapter";
    private List<Object> exams = new ArrayList<>();
    private List<Object> examsFiltered = new ArrayList<>();
    private Context context;
    private OnItemClickedListener<Exam> onItemClickedListener;
    private LayoutInflater layoutInflater;
    private final int MENU_ITEM_VIEW_TYPE = 0;
    private final int UNIFIED_NATIVE_AD_VIEW_TYPE = 1;

    public ExamAdapter(Context context, List<Object> exams, OnItemClickedListener<Exam> onItemClickedListener) {
        this.exams = exams;
        this.examsFiltered = exams;
        this.context = context;
        this.onItemClickedListener = onItemClickedListener;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                AdUnifiedExamBinding adBinding = DataBindingUtil.inflate(layoutInflater, R.layout.ad_unified_exam, parent, false);
                return new ViewHolder(adBinding);
            case MENU_ITEM_VIEW_TYPE:
                // Fall through.
            default:
                ExamListBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.exam_list, parent, false);
                binding.getRoot().setOnClickListener(v -> {
                    final Exam chosen = binding.getExam();
                    if (chosen != null) {
                        onItemClickedListener.onClicked(chosen);
                    }
                });
                return new ViewHolder(binding);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object recyclerViewItem = examsFiltered.get(position);
        if (recyclerViewItem instanceof UnifiedNativeAd) {
            return UNIFIED_NATIVE_AD_VIEW_TYPE;
        }
        return MENU_ITEM_VIEW_TYPE;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.bindResult(examsFiltered.get(position));
        int viewType = getItemViewType(position);
        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                UnifiedNativeAd nativeAd = (UnifiedNativeAd) examsFiltered.get(position);
//                populateNativeAdView(nativeAd, holder.adBinding.adView);
                populateNativeAdView(nativeAd, holder.adBinding.nativeAdView);
                break;
            case MENU_ITEM_VIEW_TYPE:
                // fall through
            default:
                holder.bindResult(examsFiltered.get(position));

        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        int viewType = holder.getItemViewType();
        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                holder.adBinding.unbind();
                break;
            case MENU_ITEM_VIEW_TYPE:
                // fall through
            default:
                holder.examBinding.unbind();
        }
    }

    @Override
    public int getItemCount() {
        return examsFiltered == null ? 0 : examsFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    examsFiltered = exams;
                } else {
                    List<Object> filteredList = new ArrayList<>();
                    for (Object row : exams) {
                        if (row instanceof Exam) {
                            Exam exam = (Exam) row;
                            if (exam.getTitle() != null) {
                                if ( exam.getTitle().toLowerCase().contains(charString.toLowerCase()) ) {
                                    filteredList.add(row);
                                }
                            }

                            if (exam.getSubject() != null) {
                                if ( exam.getSubject().toLowerCase().contains(charString.toLowerCase()) ) {
                                    filteredList.add(row);
                                }
                            }
                        }
                    }
                    examsFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = examsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                examsFiltered = (ArrayList<Object>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ExamListBinding examBinding;
        private AdUnifiedExamBinding adBinding;

        ViewHolder(ExamListBinding binding) {
            super(binding.getRoot());
            this.examBinding = binding;
        }

        void bindResult(Object result) {
            examBinding.setExam((Exam) result);
            examBinding.executePendingBindings();
        }

        ViewHolder(AdUnifiedExamBinding binding){
            super(binding.getRoot());
            this.adBinding = binding;
            adBinding.nativeAdView.setHeadlineView(adBinding.nativeAdView.findViewById(R.id.text_headline));
            adBinding.nativeAdView.setCallToActionView(adBinding.nativeAdView.findViewById(R.id.cta));
            adBinding.nativeAdView.setIconView(adBinding.nativeAdView.findViewById(R.id.icon));
            adBinding.nativeAdView.setAdvertiserView(adBinding.nativeAdView.findViewById(R.id.text_advertiser));
        }
    }

    private void populateNativeAdView(UnifiedNativeAd nativeAd,
                                      UnifiedNativeAdView adView) {
        // Some assets are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        NativeAd.Image icon = nativeAd.getIcon();

        if (icon == null) {
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd);
    }

}
