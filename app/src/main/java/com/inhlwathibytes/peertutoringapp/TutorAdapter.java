package com.inhlwathibytes.peertutoringapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.RatingBar;

import androidx.recyclerview.widget.RecyclerView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TutorAdapter extends RecyclerView.Adapter<TutorAdapter.TutorViewHolder> implements Filterable {

    private Context context;
    private List<Tutor> tutorList;
    private List<Tutor> tutorListFull;  // Backup list for filtering
    private DatabaseHelper dbHelper;
    private String studentEmail;



    public TutorAdapter(Context context, List<Tutor> tutorList) {
        this.context = context;
        this.tutorList = tutorList;
        tutorListFull = new ArrayList<>(tutorList);  // Copy full list
        dbHelper = new DatabaseHelper(context);
        this.studentEmail = studentEmail;
    }

    @Override
    public TutorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tutor, parent, false);
        return new TutorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TutorViewHolder holder, int position) {
        Tutor tutor = tutorList.get(position);
        holder.textViewName.setText(tutor.getName() + " " + tutor.getSurname());
        holder.textViewSubject.setText(tutor.getSubject());
        holder.textViewCategory.setText(tutor.getCategory());
        holder.textViewDescription.setText(tutor.getDescription());
        holder.ratingBar.setRating(tutor.getRating());
        holder.btnAdd.setOnClickListener(v -> {
            boolean inserted = dbHelper.insertTutorRequest(studentEmail, tutor.getEmail());
            if (inserted) {
                Toast.makeText(context, "Request sent!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to send request.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return tutorList.size();
    }

    @Override
    public Filter getFilter() {
        return tutorFilter;
    }

    private Filter tutorFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Tutor> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(tutorListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Tutor tutor : tutorListFull) {
                    if (tutor.getName().toLowerCase().contains(filterPattern)
                            || tutor.getSurname().toLowerCase().contains(filterPattern)
                            || tutor.getSubject().toLowerCase().contains(filterPattern)
                            || tutor.getCategory().toLowerCase().contains(filterPattern)) {
                        filteredList.add(tutor);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            tutorList.clear();
            tutorList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public static class TutorViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewSubject, textViewCategory, textViewDescription;
        RatingBar ratingBar;
        Button btnAdd;

        public TutorViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textName);
            textViewSubject = itemView.findViewById(R.id.textSubject);
            textViewCategory = itemView.findViewById(R.id.textCategory);
            textViewDescription = itemView.findViewById(R.id.textDescription);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            btnAdd = itemView.findViewById(R.id.btnAdd);
        }
    }
}
