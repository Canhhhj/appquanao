package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.Notification;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotifVH> {

    private final List<Notification> list;

    public NotificationAdapter(List<Notification> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public NotifVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotifVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifVH holder, int position) {
        Notification n = list.get(position);
        holder.tvTitle.setText(n.getTitle() != null ? n.getTitle() : "");
        holder.tvBody.setText(n.getBody() != null ? n.getBody() : "");
        holder.tvDate.setText(n.getCreatedAt() != null ? n.getCreatedAt() : "");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class NotifVH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvBody, tvDate;

        NotifVH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_notif_title);
            tvBody = itemView.findViewById(R.id.tv_notif_body);
            tvDate = itemView.findViewById(R.id.tv_notif_date);
        }
    }
}
