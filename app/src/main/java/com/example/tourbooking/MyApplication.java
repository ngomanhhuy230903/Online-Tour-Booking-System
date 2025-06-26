// File: MyApplication.java
package com.example.tourbooking;

import android.app.Application;
import com.stripe.android.PaymentConfiguration;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Lấy Publishable Key của bạn
        String stripePublishableKey = "pk_test_51RapljIY5qDEsr09JM1eLufHKrDJwyZ4j2GRLTYnJQXEaG6ltNaF9XhZh5HWsokrIPcN4ujc1uxCnhcYOiLpapeK00jtimB14Q";

        // Khởi tạo cấu hình Stripe một lần duy nhất cho toàn bộ ứng dụng
        PaymentConfiguration.init(
                getApplicationContext(),
                stripePublishableKey
        );
    }
}