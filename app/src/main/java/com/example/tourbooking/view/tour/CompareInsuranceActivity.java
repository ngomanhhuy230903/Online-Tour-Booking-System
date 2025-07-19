package com.example.tourbooking.view.tour;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tourbooking.R;
import com.example.tourbooking.model.Insurance;
import java.util.ArrayList;

public class CompareInsuranceActivity extends AppCompatActivity {

    private TableLayout tableComparison;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_insurance);

        tableComparison = findViewById(R.id.tableComparison);
        ArrayList<Insurance> selectedPackages = (ArrayList<Insurance>) getIntent().getSerializableExtra("selectedPackages");

        if (selectedPackages != null && selectedPackages.size() >= 2) {
            buildComparisonTable(selectedPackages);
        }
    }

    private void buildComparisonTable(ArrayList<Insurance> packages) {
        Insurance pkg1 = packages.get(0);
        Insurance pkg2 = packages.get(1);

        addTableHeader(pkg1.getName(), pkg2.getName());

        addTableRow("Provider", pkg1.getProvider(), pkg2.getProvider());
        addTableRow("Price", pkg1.getPrice() + "đ", pkg2.getPrice() + "đ");

    }

    private void addTableHeader(String pkg1Name, String pkg2Name) {
        TableRow headerRow = new TableRow(this);

        headerRow.addView(createTextView("Feature"));
        headerRow.addView(createTextView(pkg1Name));
        headerRow.addView(createTextView(pkg2Name));

        tableComparison.addView(headerRow);
    }

    private void addTableRow(String feature, String pkg1Value, String pkg2Value) {
        TableRow row = new TableRow(this);

        row.addView(createTextView(feature));
        row.addView(createTextView(pkg1Value));
        row.addView(createTextView(pkg2Value));

        tableComparison.addView(row);
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        textView.setTextSize(16);
        return textView;
    }
}
