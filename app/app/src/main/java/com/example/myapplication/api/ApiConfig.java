package com.example.myapplication.api;

/**
 * Cấu hình API. Đổi BASE_URL khi chạy trên máy thật:
 * - Emulator: http://10.0.2.2:3000/
 * - Máy thật (cùng WiFi với máy chạy json-server): http://IP_MÁY_TÍNH:3000/
 *   Ví dụ: http://192.168.1.100:3000/
 */
public final class ApiConfig {
    public static final String BASE_URL = "http://10.0.2.2:3000/";
    private ApiConfig() {}
}
