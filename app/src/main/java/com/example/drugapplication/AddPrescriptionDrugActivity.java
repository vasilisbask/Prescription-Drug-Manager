package com.example.drugapplication;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class AddPrescriptionDrugActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drug);

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(DrugUpdateWorker.class).build();
        WorkManager.getInstance(this).enqueue(workRequest);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "prescription_drugs_db").build();
        PrescriptionDrugDao dao = db.prescriptionDrugDao();

        EditText drugNameEditText = findViewById(R.id.name);
        EditText drugDescriptionEditText = findViewById(R.id.description);
        DatePicker startDatePicker = findViewById(R.id.start_date);
        DatePicker endDatePicker = findViewById(R.id.end_date);
        Spinner timeTermSpinner = findViewById(R.id.time_term);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.time_term_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeTermSpinner.setAdapter(adapter);
        EditText doctorNameEditText = findViewById(R.id.doctor_name);
        EditText doctorLocationEditText = findViewById(R.id.doctor_location);
        Button saveDrugButton = findViewById(R.id.save_drug_button);

        saveDrugButton.setOnClickListener(v -> {
            String drugName = drugNameEditText.getText().toString().trim();
            String drugDescription = drugDescriptionEditText.getText().toString().trim();

            int startDay = startDatePicker.getDayOfMonth();
            int startMonth = startDatePicker.getMonth() + 1;
            int startYear = startDatePicker.getYear();
            String startDate = String.format("%02d/%02d/%d", startDay, startMonth, startYear);

            int endDay = endDatePicker.getDayOfMonth();
            int endMonth = endDatePicker.getMonth() + 1;
            int endYear = endDatePicker.getYear();
            String endDate = String.format("%02d/%02d/%d", endDay, endMonth, endYear);

            String timeTerm = timeTermSpinner.getSelectedItem().toString();
            String doctorName = doctorNameEditText.getText().toString().trim();
            String doctorLocation = doctorLocationEditText.getText().toString().trim();

            if (drugName.isEmpty()) {
                Toast.makeText(this, "Drug name is required!", Toast.LENGTH_SHORT).show();
                return;
            }

            PrescriptionDrug drug = new PrescriptionDrug(drugName, drugDescription, startDate, endDate, timeTerm, doctorName, doctorLocation);

            new Thread(() -> {
                long drugId = dao.insertPrescriptionDrug(drug);

                if (drugId > 0) {
                    TimeTerm term = new TimeTerm((int) drugId, timeTerm);
                    dao.insertTimeTerm(term);

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Drug added successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Failed to add drug. Try again.", Toast.LENGTH_SHORT).show());
                }
            }).start();
        });
    }
}