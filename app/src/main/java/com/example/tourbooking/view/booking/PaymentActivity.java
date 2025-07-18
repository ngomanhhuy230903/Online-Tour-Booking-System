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
import com.example.tourbooking.model.Booking;
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
    private static final String BACKEND_URL = "https://tourbooking-api-huy.onrender.com/create-payment-intent";

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
    private String tourName, guestCount, selectedDate;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Intent intent = getIntent();
        tourName = intent.getStringExtra("TOUR_NAME");
        guestCount = intent.getStringExtra("GUEST_COUNT");
        selectedDate = intent.getStringExtra("SELECTED_DATE");
        currentTotalAmount = intent.getDoubleExtra("TOTAL_AMOUNT", 0.0);

        initializeViews();
        populateData();
        setupClickListeners();

        volleyQueue = Volley.newRequestQueue(this);

        stripe = new Stripe(
                getApplicationContext(),
                PaymentConfiguration.getInstance(getApplicationContext()).getPublishableKey()
        );
    }


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
        tvTourName.setText(tourName);
        tvBookingDate.setText("Ngày đi: " + selectedDate);
        tvGuestCount.setText("Số khách: " + guestCount);

        // Giả sử thuế và phí dịch vụ là cố định hoặc tính theo %
        double tax = currentTotalAmount * 0.1; // 10% thuế
        double serviceFee = 150000; // phí dịch vụ
        updatePrice(currentTotalAmount, tax, serviceFee, 0);
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
                        // Thanh toán thành công!
                        Toast.makeText(PaymentActivity.this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();

                        // BƯỚC 1: Lưu đơn hàng vào Database (Firebase & Room)
                        saveBookingToDatabase(paymentIntent);

                        // BƯỚC 2: Chuẩn bị dữ liệu và chuyển sang màn hình xác nhận
                        Intent intent = new Intent(PaymentActivity.this, PaymentConfirmationActivity.class);
                        intent.putExtra(PaymentConfirmationActivity.EXTRA_BOOKING_ID, paymentIntent.getId());
                        intent.putExtra(PaymentConfirmationActivity.EXTRA_TOUR_NAME, "Khám phá Vịnh Hạ Long 2 ngày 1 đêm"); // Lấy tên tour thật
                        intent.putExtra(PaymentConfirmationActivity.EXTRA_BOOKING_DATE, "17/06/2025"); // Lấy ngày thật
                        startActivity(intent);
                        finish(); // Đóng màn hình thanh toán

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

    private void saveBookingToDatabase(PaymentIntent paymentIntent) {
        // TODO: Lấy userId của người dùng hiện tại từ Firebase Auth
        String currentUserId = "some_user_id";

        // Tạo đối tượng Booking
        Booking newBooking = new Booking(
                        paymentIntent.getId(),
                        "Khám phá Vịnh Hạ Long 2 ngày 1 đêm", // Dữ liệu thật
                "COMPLETED", // Ngày hiện tại
                currentTotalAmount,
                currentUserId,
                new java.util.Date()
                );

        // TODO: Gọi Controller/Service để lưu vào Firebase Firestore
        // Ví dụ: bookingController.saveRemoteBooking(newBooking);

        // TODO: Sau khi lưu Firebase thành công, lưu vào Room để cache
        // Ví dụ: bookingController.saveLocalBooking(newBooking);

        Log.d(TAG, "Đã lưu đơn hàng với ID: " + paymentIntent.getId());
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