package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ContactActivity extends AppCompatActivity {

    private static final String PHONE = "0123456789";
    private static final String EMAIL = "admin@sport.com";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Liên hệ shop");
        }

        TextView tvPhone = findViewById(R.id.tv_contact_phone);
        TextView tvEmail = findViewById(R.id.tv_contact_email);
        tvPhone.setText("Điện thoại: " + PHONE);
        tvEmail.setText("Email: " + EMAIL);

        tvPhone.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_DIAL);
            i.setData(Uri.parse("tel:" + PHONE));
            startActivity(i);
        });
        tvEmail.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_SENDTO);
            i.setData(Uri.parse("mailto:" + EMAIL));
            startActivity(i);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
