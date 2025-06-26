// File: PaymentActivity.java (Đã sửa lỗi khởi tạo Stripe)
package com.example.tourbooking.view.booking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration; // Cần import thêm dòng này
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.view.CardInputWidget;

import com.example.tourbooking.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = "PaymentActivity";
    private static final String BACKEND_URL = "https://lackadaisical-lively-ambulance.glitch.me/create-payment-intent";

    private MaterialToolbar toolbar;
    private TextView tvTourName, tvBookingDate, tvGuestCount;
    private TextView tvBasePrice, tvTax, tvServiceFee;
    private TextInputEditText etCoupon;
    private Button btnApplyCoupon, btnPayNow;
    private View layoutPromoSavings;
    private TextView tvPromoSavings;
    private CardInputWidget cardInputWidget;
    private TextInputEditText etBillingName;

    private Stripe stripe;
    private RequestQueue volleyQueue;
    private double currentTotalAmount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initializeViews();
        populateData();
        setupClickListeners();

        volleyQueue = Volley.newRequestQueue(this);

        // --- DÒNG CODE GÂY LỖI ĐÃ ĐƯỢC SỬA LẠI ---
        // Lấy lại Publishable Key đã được cấu hình trong MyApplication
        stripe = new Stripe(
                getApplicationContext(),
                PaymentConfiguration.getInstance(getApplicationContext()).getPublishableKey()
        );
    }

    // ... (Toàn bộ các hàm còn lại giữ nguyên không thay đổi)

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar_payment);
        tvTourName = findViewById(R.id.tvTourName);
        tvBookingDate = findViewById(R.id.tvBookingDate);
        tvGuestCount = findViewById(R.id.tvGuestCount);
        tvBasePrice = findViewById(R.id.tvBasePrice);
        tvTax = findViewById(R.id.tvTax);
        tvServiceFee = findViewById(R.id.tvServiceFee);
        etCoupon = findViewById(R.id.etCoupon);
        btnApplyCoupon = findViewById(R.id.btnApplyCoupon);
        btnPayNow = findViewById(R.id.btnPayNow);
        layoutPromoSavings = findViewById(R.id.layoutPromoSavings);
        tvPromoSavings = findViewById(R.id.tvPromoSavings);
        cardInputWidget = findViewById(R.id.stripe_card_widget);
        etBillingName = findViewById(R.id.etBillingName);
        cardInputWidget.setPostalCodeEnabled(false);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void populateData() {
        tvTourName.setText("Tour khám phá Vịnh Hạ Long 2 ngày 1 đêm");
        tvBookingDate.setText("Ngày đi: 17/06/2025");
        tvGuestCount.setText("Số khách: 2 người lớn");
        updatePrice(1000000, 100000, 150000, 0);
    }

    private void setupClickListeners() {
        btnApplyCoupon.setOnClickListener(v -> applyCoupon());
        btnPayNow.setOnClickListener(v -> startCheckout());
    }

    private void updatePrice(double base, double tax, double fee, double discount) {
        currentTotalAmount = base + tax + fee - discount;
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvBasePrice.setText(currencyFormatter.format(base));
        tvTax.setText(currencyFormatter.format(tax));
        tvServiceFee.setText(currencyFormatter.format(fee));
        if (discount > 0) {
            layoutPromoSavings.setVisibility(View.VISIBLE);
            tvPromoSavings.setText(String.format("-%s", currencyFormatter.format(discount)));
        } else {
            layoutPromoSavings.setVisibility(View.GONE);
        }
        btnPayNow.setText(String.format("Thanh toán ngay (%s)", currencyFormatter.format(currentTotalAmount)));
    }

    private void applyCoupon() {
        String couponCode = etCoupon.getText().toString().trim();
        if (couponCode.equalsIgnoreCase("GIAM50K")) {
            Toast.makeText(this, "Áp dụng mã giảm giá thành công!", Toast.LENGTH_SHORT).show();
            updatePrice(1000000, 100000, 150000, 50000);
        } else {
            Toast.makeText(this, "Mã giảm giá không hợp lệ!", Toast.LENGTH_SHORT).show();
        }
    }

    private void startCheckout() {
        btnPayNow.setEnabled(false);
        btnPayNow.setText("Đang kết nối...");

        PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
        if (params == null) {
            onPaymentFailed("Thông tin thẻ không hợp lệ.");
            return;
        }

        JSONObject requestBody = new JSONObject();
        try {
            long amount = (long) currentTotalAmount;
            requestBody.put("amount", amount);
        } catch (JSONException e) {
            onPaymentFailed("Lỗi tạo yêu cầu thanh toán.");
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                BACKEND_URL,
                requestBody,
                response -> {
                    try {
                        String clientSecret = response.getString("clientSecret");
                        ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                                .createWithPaymentMethodCreateParams(params, clientSecret);
                        stripe.confirmPayment(this, confirmParams);
                    } catch (JSONException e) {
                        onPaymentFailed("Lỗi đọc phản hồi từ server.");
                    }
                },
                error -> {
                    onPaymentFailed("Không thể kết nối đến máy chủ thanh toán: " + error.getMessage());
                }
        );

        volleyQueue.add(jsonObjectRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (stripe != null) {
            stripe.onPaymentResult(requestCode, data, new ApiResultCallback<PaymentIntentResult>() {
                @Override
                public void onSuccess(@NonNull PaymentIntentResult result) {
                    PaymentIntent paymentIntent = result.getIntent();
                    PaymentIntent.Status status = paymentIntent.getStatus();
                    if (status == PaymentIntent.Status.Succeeded) {
                        Toast.makeText(PaymentActivity.this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();
                    } else {
                        onPaymentFailed("Trạng thái thanh toán không thành công: " + status);
                    }
                }

                @Override
                public void onError(@NonNull Exception e) {
                    onPaymentFailed("Lỗi thanh toán: " + e.getMessage());
                }
            });
        }
    }

    private void onPaymentFailed(@Nullable String message) {
        if (message != null) {
            Log.e(TAG, "Payment failed: " + message);
            Toast.makeText(this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
        }
        btnPayNow.setEnabled(true);
        updatePrice(1000000, 100000, 150000, 0);
    }
}