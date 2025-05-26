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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TutorAdapter extends RecyclerView.Adapter<TutorAdapter.TutorViewHolder> implements Filterable {

    private Context context;
    private List<Tutor> tutorList;
    private List<Tutor> tutorListFull;  // Backup list for filtering
    private OnAddButtonClickListener onAddButtonClickListener;
    private Map<Integer, String> tutorRequestStatusMap; // Map of tutorId to request status

    // Constructor
    public TutorAdapter(Context context, List<Tutor> tutorList, Map<Integer, String> tutorRequestStatusMap) {
        this.context = context;
        this.tutorList = tutorList;
        this.tutorListFull = new ArrayList<>(tutorList);  // Copy full list for filtering
        this.tutorRequestStatusMap = tutorRequestStatusMap;
    }

    // Interface for the add button click
    public interface OnAddButtonClickListener {
        void onAddButtonClick(int tutorId);
    }

    // Setter for the listener
    public void setOnAddButtonClickListener(OnAddButtonClickListener listener) {
        this.onAddButtonClickListener = listener;
    }

    @Override
    public TutorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tutor, parent, false);
        return new TutorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TutorViewHolder holder, int position) {
        Tutor tutor = tutorList.get(position);

        // Set all tutor data
        holder.textViewName.setText(tutor.getFullName());
        holder.textViewSubject.setText(tutor.getSubject());
        holder.textViewCategory.setText(tutor.getCategory());
        holder.textViewDescription.setText(tutor.getDescription());
        holder.textViewEmail.setText(tutor.getEmail());

        // Set the actual rating
        holder.ratingBar.setRating(tutor.getRating());

        // Show rating count and formatted rating
        holder.textViewRatingCount.setText(
                String.format("%.1f", tutor.getRating()) + " (" + tutor.getRatingCount() + " ratings)");

        // Check if there's a request status for this tutor
        String requestStatus = tutorRequestStatusMap.get(tutor.getId());

        if (requestStatus != null) {
            switch (requestStatus.toLowerCase()) {
                case "pending":
                    holder.btnAdd.setText("Request Pending");
                    holder.btnAdd.setEnabled(false);
                    holder.btnAdd.setBackgroundColor(context.getResources().getColor(R.color.primary_color));
                    break;
                case "approved":
                    holder.btnAdd.setText("Request Approved");
                    holder.btnAdd.setEnabled(false);
                    holder.btnAdd.setBackgroundColor(context.getResources().getColor(R.color.green));
                    break;
                case "rejected":
                    holder.btnAdd.setText("Request Rejected - Try Again");
                    holder.btnAdd.setEnabled(true);
                    holder.btnAdd.setBackgroundColor(context.getResources().getColor(R.color.primary_color));
                    break;
                default:
                    holder.btnAdd.setText("Request Tutor");
                    holder.btnAdd.setEnabled(true);
                    holder.btnAdd.setBackgroundColor(context.getResources().getColor(R.color.primary_color));
            }
        } else {
            holder.btnAdd.setText("Request Tutor");
            holder.btnAdd.setEnabled(true);
            holder.btnAdd.setBackgroundColor(context.getResources().getColor(R.color.primary_color));
        }

        holder.btnAdd.setOnClickListener(v -> {
            if (onAddButtonClickListener != null) {
                onAddButtonClickListener.onAddButtonClick(tutor.getId());
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

    // Method to update the list and refresh the adapter
    public void updateList(List<Tutor> newList) {
        tutorList = new ArrayList<>(newList);
        tutorListFull = new ArrayList<>(newList); // Update the full list as well
        notifyDataSetChanged();
    }

    // ViewHolder
    public static class TutorViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewSubject, textViewCategory,
                textViewDescription, textViewEmail, textViewRatingCount;
        RatingBar ratingBar;
        Button btnAdd;

        public TutorViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textName);
            textViewSubject = itemView.findViewById(R.id.textSubject);
            textViewCategory = itemView.findViewById(R.id.textCategory);
            textViewDescription = itemView.findViewById(R.id.textDescription);
            textViewEmail = itemView.findViewById(R.id.textEmail);
            textViewRatingCount = itemView.findViewById(R.id.textRatingCount);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            btnAdd = itemView.findViewById(R.id.btnAdd);
        }
    }
}