package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.Review;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final List<Review> list;

    public ReviewAdapter(List<Review> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review r = list.get(position);
        holder.tvUser.setText(r.getUser() != null ? r.getUser() : "");
        holder.tvRating.setText(r.getRating() > 0 ? r.getRating() + " sao" : "");
        holder.tvComment.setText(r.getComment() != null ? r.getComment() : "");
        holder.tvDate.setText(r.getDate() != null ? r.getDate() : "");
        if (r.getReply() != null && !r.getReply().trim().isEmpty()) {
            holder.tvReply.setVisibility(View.VISIBLE);
            holder.tvReply.setText("Shop phản hồi: " + r.getReply().trim());
        } else {
            holder.tvReply.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvUser, tvRating, tvComment, tvDate, tvReply;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tv_review_user);
            tvRating = itemView.findViewById(R.id.tv_review_rating);
            tvComment = itemView.findViewById(R.id.tv_review_comment);
            tvDate = itemView.findViewById(R.id.tv_review_date);
            tvReply = itemView.findViewById(R.id.tv_review_reply);
        }
    }
}
