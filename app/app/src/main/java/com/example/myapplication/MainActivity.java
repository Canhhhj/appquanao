package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.myapplication.api.RetrofitClient;
import com.example.myapplication.model.User;
import com.example.myapplication.utils.UserSession;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_mall) {
                selectedFragment = new MallFragment();
            } else if (id == R.id.nav_live) {
                selectedFragment = new LiveFragment();
            } else if (id == R.id.nav_cart) {
                selectedFragment = new CartFragment();
            } else if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // Set default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAccountStatus();
    }

    /** Khi admin khóa tài khoản: kiểm tra trạng thái user, nếu bị khóa thì ép thoát và về màn đăng nhập. */
    private void checkAccountStatus() {
        UserSession session = UserSession.getInstance(this);
        if (!session.isLoggedIn()) return;

        String userId = session.getUserId();
        if (userId == null || userId.isEmpty()) return;

        RetrofitClient.getService().getUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (!response.isSuccessful() || response.body() == null) return;

                for (User u : response.body()) {
                    if (userId.equals(u.getId())) {
                        if (!"Hoạt động".equals(u.getStatus())) {
                            session.logout();
                            Toast.makeText(MainActivity.this,
                                "Tài khoản đã bị khóa. Vui lòng liên hệ nhân viên để biết chi tiết.", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(MainActivity.this, LoginActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        }
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                // Không ép thoát khi mất mạng, tránh đăng xuất nhầm
            }
        });
    }
}
