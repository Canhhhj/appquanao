package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.adapter.AddressAdapter;
import com.example.myapplication.api.RetrofitClient;
import com.example.myapplication.model.Address;
import com.example.myapplication.utils.UserSession;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressActivity extends AppCompatActivity {

    public static final String EXTRA_ADDRESS = "address";
    public static final String EXTRA_PHONE = "phone";

    private RecyclerView recycler;
    private TextView tvEmpty;
    private final List<Address> addresses = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresses);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Sổ địa chỉ");
        }

        recycler = findViewById(R.id.recycler_addresses);
        tvEmpty = findViewById(R.id.tv_empty_addresses);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        loadAddresses();
    }

    private void loadAddresses() {
        String userId = UserSession.getInstance(this).getUserId();
        if (userId == null || userId.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
            return;
        }

        RetrofitClient.getService().getAddressesByUser(userId).enqueue(new Callback<List<Address>>() {
            @Override
            public void onResponse(Call<List<Address>> call, Response<List<Address>> response) {
                addresses.clear();
                if (response.isSuccessful() && response.body() != null) {
                    addresses.addAll(response.body());
                }
                AddressAdapter adapter = new AddressAdapter(addresses, addr -> {
                    Intent data = new Intent();
                    data.putExtra(EXTRA_ADDRESS, addr.getFullAddress());
                    data.putExtra(EXTRA_PHONE, addr.getPhone() != null ? addr.getPhone() : "");
                    setResult(RESULT_OK, data);
                    finish();
                });
                recycler.setAdapter(adapter);
                if (addresses.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    recycler.setVisibility(View.GONE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                    recycler.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<List<Address>> call, Throwable t) {
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
