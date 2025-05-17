package com.inhlwathibytes.peertutoringapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inhlwathibytes.peertutoringapp.R;
import com.inhlwathibytes.peertutoringapp.models.TutorshipSubject;

import java.util.List;

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder> {

    private List<TutorshipSubject> subjectList;
    private Context context;


    public SubjectsAdapter(List<TutorshipSubject> subjectList, Context context) {
        this.subjectList = subjectList;
        this.context = context;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_subject_card, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        try {
            TutorshipSubject subject = subjectList.get(position);
            holder.subjectNameText.setText(subject.getSubjectName());
            holder.modeOfDeliveryText.setText(subject.getDeliveryMode());
            holder.levelText.setText(subject.getLevel());
            holder.hourlyRateText.setText("R" + subject.getHourlyRate() + "/hr");
            holder.availabilityText.setText(subject.getAvailability());
            holder.outlineText.setText(subject.getOutline());
        } catch (Exception e) {
            Log.e("BindError", "Error binding subject at position " + position + ": " + e.getMessage(), e);
        }
    }


    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        TextView subjectNameText, modeOfDeliveryText, levelText, hourlyRateText, availabilityText, outlineText;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            modeOfDeliveryText = itemView.findViewById(R.id.modeOfDelivery);
            subjectNameText = itemView.findViewById(R.id.subjectName);
            levelText = itemView.findViewById(R.id.level);
            hourlyRateText = itemView.findViewById(R.id.hourlyRate);
            availabilityText = itemView.findViewById(R.id.availability);
            outlineText = itemView.findViewById(R.id.outline);
        }
    }
}
