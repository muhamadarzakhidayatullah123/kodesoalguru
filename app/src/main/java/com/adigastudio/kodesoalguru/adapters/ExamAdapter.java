package com.adigastudio.kodesoalguru.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.adigastudio.kodesoalguru.R;
import com.adigastudio.kodesoalguru.databinding.ExamListBinding;
import com.adigastudio.kodesoalguru.interfaces.MyListeners.OnItemClickedListener;
import com.adigastudio.kodesoalguru.models.Exam;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ViewHolder> implements Filterable {

    private String TAG = "ExamAdapter";
    private List<Exam> exam = new ArrayList<>();
    private List<Exam> examFiltered = new ArrayList<>();
    private Context context;
    private OnItemClickedListener<Exam> onItemClickedListener;
    private LayoutInflater layoutInflater;

    public ExamAdapter(Context context, List<Exam> exam, OnItemClickedListener<Exam> onItemClickedListener) {
        this.exam = exam;
        this.examFiltered = exam;
        this.context = context;
        this.onItemClickedListener = onItemClickedListener;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ExamListBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.exam_list, parent, false);
        binding.getRoot().setOnClickListener(v -> {
            final Exam chosen = binding.getExam();
            if (chosen != null) {
                onItemClickedListener.onClicked(chosen);
            }
        });
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(examFiltered.get(position));
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.binding.unbind();
    }

    @Override
    public int getItemCount() {
        return examFiltered == null ? 0 : examFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    examFiltered = exam;
                } else {
                    List<Exam> filteredList = new ArrayList<>();
                    for (Exam row : exam) {
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getSubject().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    examFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = examFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                examFiltered = (ArrayList<Exam>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final ExamListBinding binding;

        private ViewHolder(ExamListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Exam exam) {
            binding.setExam(exam);
            binding.executePendingBindings();
        }
    }

}
