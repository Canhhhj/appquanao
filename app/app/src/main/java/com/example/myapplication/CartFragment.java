package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import com.example.myapplication.adapter.CartAdapter;
import com.example.myapplication.utils.CartManager;
import java.text.NumberFormat;
import java.util.Locale;

public class CartFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        
        RecyclerView recycler = view.findViewById(R.id.recycler_cart);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(new CartAdapter(CartManager.getInstance().getCartItems()));
        
        TextView total = view.findViewById(R.id.tv_total);
        double totalPrice = CartManager.getInstance().getTotalPrice();
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        total.setText("Tổng: " + format.format(totalPrice));
        
        return view;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Refresh when coming back
        if (getView() != null) {
            RecyclerView recycler = getView().findViewById(R.id.recycler_cart);
            if (recycler.getAdapter() != null) {
                recycler.getAdapter().notifyDataSetChanged();
            }
            TextView total = getView().findViewById(R.id.tv_total);
            double totalPrice = CartManager.getInstance().getTotalPrice();
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            total.setText("Tổng: " + format.format(totalPrice));
        }
    }
}
