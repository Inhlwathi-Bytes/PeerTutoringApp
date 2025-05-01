package com.inhlwathibytes.peertutoringapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private Context context;
    private List<Request> requestList;
    private DatabaseHelper dbHelper;

    public RequestAdapter(Context context, List<Request> requestList, DatabaseHelper dbHelper) {
        this.context = context;
        this.requestList = requestList;
        this.dbHelper = dbHelper;
    }

    @Override
    public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RequestViewHolder holder, int position) {
        Request request = requestList.get(position);
        holder.textStudentEmail.setText(request.getStudentEmail());
        holder.textStatus.setText("Status: " + request.getStatus());

        holder.btnAccept.setOnClickListener(v -> {
            dbHelper.updateRequestStatus(request.getId(), "Accepted");
            holder.textStatus.setText("Status: Accepted");
        });

        holder.btnDecline.setOnClickListener(v -> {
            dbHelper.updateRequestStatus(request.getId(), "Declined");
            holder.textStatus.setText("Status: Declined");
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView textStudentEmail, textStatus;
        Button btnAccept, btnDecline;

        public RequestViewHolder(View itemView) {
            super(itemView);
            textStudentEmail = itemView.findViewById(R.id.textStudentEmail);
            textStatus = itemView.findViewById(R.id.textStatus);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDecline = itemView.findViewById(R.id.btnDecline);
        }
    }
}
