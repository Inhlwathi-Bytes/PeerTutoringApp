package com.inhlwathibytes.peertutoringapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;


import java.util.List;

public class MyTutorsAdapter extends RecyclerView.Adapter<MyTutorsAdapter.ViewHolder> {

    private Context context;
    private List<Request> requestList;

    public MyTutorsAdapter(Context context, List<Request> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Request request = requestList.get(position);
        holder.textViewTutorEmail.setText(request.getTutorEmail());
        holder.textViewStatus.setText(request.getStatus());
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTutorEmail, textViewStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewTutorEmail = itemView.findViewById(R.id.textTutorEmail);
            textViewStatus = itemView.findViewById(R.id.textStatus);
        }
    }
}

