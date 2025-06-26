package com.example.tourbooking.view.info;

import android.content.Context;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tourbooking.R;

/**
 * M29 – Hiển thị điều khoản & điều kiện cho người dùng.
 *  - Tìm kiếm trong văn bản
 *  - Điều chỉnh kích thước font
 *  - In nội dung
 *  - Người dùng có thể Accept / Decline
 */
public class TermsActivity extends AppCompatActivity {

    private TextView tvTerms;
    private EditText edtSearch;
    private SeekBar fontSizeSlider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Hiển thị nút back trên toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvTerms = findViewById(R.id.terms);
        edtSearch = findViewById(R.id.searchInput);
        fontSizeSlider = findViewById(R.id.fontSizeSlider);
        Button btnAccept = findViewById(R.id.accept);
        Button btnDecline = findViewById(R.id.decline);
        Button btnPrint = findViewById(R.id.print);

        // Điều chỉnh font size
        fontSizeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float size = 12 + (progress * 0.5f); // 12sp tới ~37sp
                tvTerms.setTextSize(size);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Tìm kiếm đơn giản – highlight đầu tiên (chỉ báo toast)
        findViewById(R.id.searchInput).setOnKeyListener((v, keyCode, event) -> {
            String query = edtSearch.getText().toString();
            if (!query.isEmpty() && tvTerms.getText().toString().toLowerCase().contains(query.toLowerCase())) {
                Toast.makeText(this, getString(R.string.terms_found, query), Toast.LENGTH_SHORT).show();
            }
            return false;
        });

        // Accept / Decline
        btnAccept.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });
        btnDecline.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        // In nội dung
        btnPrint.setOnClickListener(v -> startPrintJob());
    }

    private void startPrintJob() {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        if (printManager != null) {
            printManager.print("TermsPrintJob", new TermsPrintDocumentAdapter(this, tvTerms.getText().toString()),
                    new PrintAttributes.Builder().build());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class TermsPrintDocumentAdapter extends android.print.PrintDocumentAdapter {
        private final Context context;
        private final String content;

        TermsPrintDocumentAdapter(Context context, String content) {
            this.context = context;
            this.content = content;
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                              android.os.CancellationSignal cancellationSignal,
                              LayoutResultCallback callback, Bundle extras) {
            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }
            android.print.PrintDocumentInfo info = new android.print.PrintDocumentInfo
                    .Builder("terms.pdf")
                    .setContentType(android.print.PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .build();
            callback.onLayoutFinished(info, true);
        }

        @Override
        public void onWrite(android.print.PageRange[] pages, android.os.ParcelFileDescriptor destination,
                            android.os.CancellationSignal cancellationSignal, WriteResultCallback callback) {
            java.io.InputStream in = null;
            java.io.OutputStream out = null;
            try {
                in = new java.io.ByteArrayInputStream(content.getBytes());
                out = new java.io.FileOutputStream(destination.getFileDescriptor());

                byte[] buf = new byte[1024];
                int size;
                while ((size = in.read(buf)) >= 0 && !cancellationSignal.isCanceled()) {
                    out.write(buf, 0, size);
                }
                callback.onWriteFinished(new android.print.PageRange[]{android.print.PageRange.ALL_PAGES});
            } catch (Exception e) {
                callback.onWriteFailed(e.getMessage());
            } finally {
                try { if (in != null) in.close(); } catch (Exception ignored) {}
                try { if (out != null) out.close(); } catch (Exception ignored) {}
            }
        }
    }
} 