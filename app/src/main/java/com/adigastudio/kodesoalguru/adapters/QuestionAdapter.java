package com.adigastudio.kodesoalguru.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.adigastudio.kodesoalguru.R;
import com.adigastudio.kodesoalguru.databinding.AdUnifiedQuestionBinding;
import com.adigastudio.kodesoalguru.databinding.QuestionListBinding;
import com.adigastudio.kodesoalguru.interfaces.MyListeners.OnItemClickedListener;
import com.adigastudio.kodesoalguru.models.Question;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> implements Filterable {

    private String TAG = "QuestionAdapter";
    private List<Object> questions = new ArrayList<>();
    private List<Object> questionsFiltered = new ArrayList<>();
    private Context context;
    private OnItemClickedListener<Question> onItemClickedListener;
    private LayoutInflater layoutInflater;
    private final int MENU_ITEM_VIEW_TYPE = 0;
    private final int UNIFIED_NATIVE_AD_VIEW_TYPE = 1;

    public QuestionAdapter(Context context, List<Object> questionsFiltered, OnItemClickedListener<Question> onItemClickedListener) {
        this.questions = questionsFiltered;
        this.questionsFiltered = questionsFiltered;
        this.context = context;
        this.onItemClickedListener = onItemClickedListener;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                AdUnifiedQuestionBinding adBinding = DataBindingUtil.inflate(layoutInflater, R.layout.ad_unified_question, parent, false);
                return new ViewHolder(adBinding);
            case MENU_ITEM_VIEW_TYPE:
                // Fall through.
            default:
                QuestionListBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.question_list, parent, false);
                return new ViewHolder(binding);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object recyclerViewItem = questionsFiltered.get(position);
        if (recyclerViewItem instanceof UnifiedNativeAd) {
            return UNIFIED_NATIVE_AD_VIEW_TYPE;
        }
        return MENU_ITEM_VIEW_TYPE;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                UnifiedNativeAd nativeAd = (UnifiedNativeAd) questionsFiltered.get(position);
//                populateNativeAdView(nativeAd, holder.adBinding.adView);
                populateNativeAdView(nativeAd, holder.adBinding.nativeAdView);
                break;
            case MENU_ITEM_VIEW_TYPE:
                // fall through
            default:
                holder.bindResult(questionsFiltered.get(position));

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
                holder.questionBinding.unbind();
        }
    }

    @Override
    public int getItemCount() {
        return questionsFiltered == null ? 0 : questionsFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    questionsFiltered = questions;
                } else {
                    List<Object> filteredList = new ArrayList<>();
                    for (Object row : questions) {
                        if (row instanceof Question) {
                            Question question = (Question) row;
                            if (question.getQuestion() != null) {
                                if ( question.getQuestion().toLowerCase().contains(charString.toLowerCase()) ) {
                                    filteredList.add(row);
                                }
                            }

                            if (question.getQuestionImageInformation() != null) {
                                if ( question.getQuestionImageInformation().toLowerCase().contains(charString.toLowerCase()) ) {
                                    filteredList.add(row);
                                }
                            }

                            if (question.getChoiceA() != null) {
                                if ( question.getChoiceA().toLowerCase().contains(charString.toLowerCase()) ) {
                                    filteredList.add(row);
                                }
                            }

                            if (question.getChoiceB() != null) {
                                if ( question.getChoiceB().toLowerCase().contains(charString.toLowerCase()) ) {
                                    filteredList.add(row);
                                }
                            }

                            if (question.getChoiceC() != null) {
                                if ( question.getChoiceC().toLowerCase().contains(charString.toLowerCase()) ) {
                                    filteredList.add(row);
                                }
                            }

                            if (question.getChoiceD() != null) {
                                if ( question.getChoiceD().toLowerCase().contains(charString.toLowerCase()) ) {
                                    filteredList.add(row);
                                }
                            }

                            if (question.getChoiceE() != null) {
                                if ( question.getChoiceE().toLowerCase().contains(charString.toLowerCase()) ) {
                                    filteredList.add(row);
                                }
                            }

                            if (question.getBasic() != null) {
                                if ( question.getBasic().toLowerCase().contains(charString.toLowerCase()) ) {
                                    filteredList.add(row);
                                }
                            }
                        }
                    }
                    questionsFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = questionsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                questionsFiltered = (ArrayList<Object>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private QuestionListBinding questionBinding;
        private AdUnifiedQuestionBinding adBinding;

        ViewHolder(QuestionListBinding binding) {
            super(binding.getRoot());
            this.questionBinding = binding;
        }

        void bindResult(Object question) {
            questionBinding.setQuestion((Question) question);
            questionBinding.executePendingBindings();
        }

        ViewHolder(AdUnifiedQuestionBinding binding){
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
