package com.example.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class FavoritesManager {
    private static final String PREF = "Favorites";
    private static final String KEY_IDS = "productIds";

    private static FavoritesManager instance;
    private SharedPreferences prefs;

    private FavoritesManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public static FavoritesManager getInstance(Context context) {
        if (instance == null) {
            instance = new FavoritesManager(context);
        }
        return instance;
    }

    public Set<String> getFavoriteIds() {
        return prefs.getStringSet(KEY_IDS, new HashSet<>());
    }

    public boolean isFavorite(String productId) {
        return productId != null && getFavoriteIds().contains(productId);
    }

    public void addFavorite(String productId) {
        if (productId == null) return;
        Set<String> set = new HashSet<>(getFavoriteIds());
        set.add(productId);
        prefs.edit().putStringSet(KEY_IDS, set).apply();
    }

    public void removeFavorite(String productId) {
        if (productId == null) return;
        Set<String> set = new HashSet<>(getFavoriteIds());
        set.remove(productId);
        prefs.edit().putStringSet(KEY_IDS, set).apply();
    }

    public void toggle(String productId) {
        if (isFavorite(productId)) removeFavorite(productId);
        else addFavorite(productId);
    }
}
