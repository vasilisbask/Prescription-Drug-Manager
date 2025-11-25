package com.example.drugapplication;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ViewActiveDrugsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_active_drugs);

        TableLayout tableLayout = findViewById(R.id.drug_table);
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        PrescriptionDrugDao dao = db.prescriptionDrugDao();

        loadActiveDrugs(tableLayout, dao);
    }

    private void loadActiveDrugs(TableLayout tableLayout, PrescriptionDrugDao dao) {
        new Thread(() -> {
            List<PrescriptionDrugWithTimeTerm> activeDrugs = dao.getActiveDrugs();

            runOnUiThread(() -> {
                tableLayout.removeAllViews();

                TableRow headerRow = new TableRow(this);
                headerRow.addView(createTextView("Name"));
                headerRow.addView(createTextView("Active"));
                headerRow.addView(createTextView("Start Date"));
                headerRow.addView(createTextView("Button"));
                tableLayout.addView(headerRow);

                for (PrescriptionDrugWithTimeTerm drugWithTimeTerm : activeDrugs) {
                    if (drugWithTimeTerm.getPrescriptionDrug().isActive() &&
                            !drugWithTimeTerm.getPrescriptionDrug().isHasReceivedToday()) {

                        TableRow row = new TableRow(this);
                        row.addView(createTextView(drugWithTimeTerm.getPrescriptionDrug().getShortName()));
                        row.addView(createTextView("Yes"));
                        row.addView(createTextView(drugWithTimeTerm.getPrescriptionDrug().getStartDate()));

                        ImageButton viewDetailsButton = new ImageButton(this);
                        viewDetailsButton.setImageResource(R.drawable.eye_icon);
                        viewDetailsButton.setBackgroundResource(android.R.color.transparent);
                        viewDetailsButton.setScaleType(ImageButton.ScaleType.FIT_CENTER);
                        viewDetailsButton.setPadding(8, 8, 8, 8);
                        TableRow.LayoutParams params = new TableRow.LayoutParams(80, 80);
                        viewDetailsButton.setLayoutParams(params);

                        viewDetailsButton.setOnClickListener(v -> showDrugDetails(drugWithTimeTerm));
                        row.addView(viewDetailsButton);

                        tableLayout.addView(row);
                    }
                }
            });
        }).start();
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        return textView;
    }

    private void showDrugDetails(PrescriptionDrugWithTimeTerm drugWithTimeTerm) {
        PrescriptionDrug drug = drugWithTimeTerm.getPrescriptionDrug();

        String details = "ID: " + drug.getId() + "\n" +
                "Name: " + drug.getShortName() + "\n" +
                "Description: " + drug.getDescription() + "\n" +
                "Start Date: " + drug.getStartDate() + "\n" +
                "End Date: " + drug.getEndDate() + "\n" +
                "Last Date Received: " + drug.getLastDateReceived() + "\n" +
                "Active: " + (drug.isActive() ? "Yes" : "No") + "\n" +
                "Has Received Today: " + (drug.isHasReceivedToday() ? "Yes" : "No");

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Drug Details");
        builder.setMessage(details);
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
