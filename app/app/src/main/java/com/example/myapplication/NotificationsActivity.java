package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.adapter.NotificationAdapter;
import com.example.myapplication.api.RetrofitClient;
import com.example.myapplication.model.Notification;
import com.example.myapplication.utils.UserSession;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private TextView tvEmpty;
    private final List<Notification> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thông báo");
        }

        recycler = findViewById(R.id.recycler_notifications);
        tvEmpty = findViewById(R.id.tv_empty_notifications);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        String userId = UserSession.getInstance(this).getUserId();
        if (userId == null || userId.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
            return;
        }

        RetrofitClient.getService().getNotificationsByUser(userId).enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                list.clear();
                if (response.isSuccessful() && response.body() != null) {
                    list.addAll(response.body());
                }
                recycler.setAdapter(new NotificationAdapter(list));
                if (list.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    recycler.setVisibility(View.GONE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                    recycler.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                tvEmpty.setVisibility(View.VISIBLE);
                recycler.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
