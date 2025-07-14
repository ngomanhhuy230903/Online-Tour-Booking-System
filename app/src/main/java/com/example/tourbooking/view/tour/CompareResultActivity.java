package com.example.tourbooking.view.tour;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tourbooking.R;
import com.example.tourbooking.model.Tour;

public class CompareResultActivity extends AppCompatActivity {

    private TextView tvTour1Name, tvTour2Name;
    private TextView tvPrice1, tvPrice2, tvPriceCompare;
    private TextView tvRating1, tvRating2, tvRatingCompare;
    private TextView tvDays1, tvDays2, tvDaysCompare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_result);

        tvTour1Name = findViewById(R.id.tvTour1Name);
        tvTour2Name = findViewById(R.id.tvTour2Name);

        tvPrice1 = findViewById(R.id.tvPrice1);
        tvPrice2 = findViewById(R.id.tvPrice2);
        tvPriceCompare = findViewById(R.id.tvPriceCompare);

        tvRating1 = findViewById(R.id.tvRating1);
        tvRating2 = findViewById(R.id.tvRating2);
        tvRatingCompare = findViewById(R.id.tvRatingCompare);

        tvDays1 = findViewById(R.id.tvDays1);
        tvDays2 = findViewById(R.id.tvDays2);
        tvDaysCompare = findViewById(R.id.tvDaysCompare);

        Tour tour1 = (Tour) getIntent().getSerializableExtra("tour1");
        Tour tour2 = (Tour) getIntent().getSerializableExtra("tour2");

        if (tour1 != null && tour2 != null) {
            displayComparison(tour1, tour2);
        }
    }

    private void displayComparison(Tour t1, Tour t2) {
        tvTour1Name.setText(t1.getTourName());
        tvTour2Name.setText(t2.getTourName());

        tvPrice1.setText(String.valueOf(t1.getPrice()));
        tvPrice2.setText(String.valueOf(t2.getPrice()));
        tvPriceCompare.setText(getCompareSymbol(t1.getPrice(), t2.getPrice()));

        tvRating1.setText(String.valueOf(t1.getRating()));
        tvRating2.setText(String.valueOf(t2.getRating()));
        tvRatingCompare.setText(getCompareSymbol(t1.getRating(), t2.getRating()));

        tvDays1.setText(String.valueOf(t1.getDays()));
        tvDays2.setText(String.valueOf(t2.getDays()));
        tvDaysCompare.setText(getCompareSymbol(t1.getDays(), t2.getDays()));
    }

    private String getCompareSymbol(Number val1, Number val2) {
        if (val1.doubleValue() > val2.doubleValue()) return ">";
        else if (val1.doubleValue() < val2.doubleValue()) return "<";
        else return "=";
    }
}


