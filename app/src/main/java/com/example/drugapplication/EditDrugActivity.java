package com.example.drugapplication;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditDrugActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_drug);

        EditText nameEditText = findViewById(R.id.edit_name);
        EditText descriptionEditText = findViewById(R.id.edit_description);
        DatePicker startDatePicker = findViewById(R.id.edit_start_date);
        DatePicker endDatePicker = findViewById(R.id.edit_end_date);
        Spinner timeTermSpinner = findViewById(R.id.edit_time_term);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.time_term_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeTermSpinner.setAdapter(adapter);
        EditText doctorNameEditText = findViewById(R.id.edit_doctor_name);
        EditText doctorLocationEditText = findViewById(R.id.edit_location);
        Button saveButton = findViewById(R.id.save_changes_button);

        int drugId = getIntent().getIntExtra("DRUG_ID", -1);
        if (drugId == -1) {
            Toast.makeText(this, "Invalid drug ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        PrescriptionDrugDao drugDao = db.prescriptionDrugDao();

        new Thread(() -> {
            PrescriptionDrug drug = drugDao.getPrescriptionDrugById(drugId);
            if (drug != null) {
                runOnUiThread(() -> {
                    nameEditText.setText(drug.getShortName());
                    descriptionEditText.setText(drug.getDescription());

                    // start date
                    String[] startParts = drug.getStartDate().split("/");
                    startDatePicker.updateDate(
                            Integer.parseInt(startParts[2]),
                            Integer.parseInt(startParts[1]) - 1,
                            Integer.parseInt(startParts[0])
                    );

                    // end date
                    String[] endParts = drug.getEndDate().split("/");
                    endDatePicker.updateDate(
                            Integer.parseInt(endParts[2]),
                            Integer.parseInt(endParts[1]) - 1,
                            Integer.parseInt(endParts[0])
                    );

                    doctorNameEditText.setText(drug.getDoctorName());
                    doctorLocationEditText.setText(drug.getDoctorLocation());
                });
            }
        }).start();

        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();

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

            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

            java.time.LocalDate start = java.time.LocalDate.parse(startDate, formatter);
            java.time.LocalDate end = java.time.LocalDate.parse(endDate, formatter);

            if (name.isEmpty()) {
                Toast.makeText(this, "Drug name is required!", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                PrescriptionDrug drugToUpdate = drugDao.getPrescriptionDrugById(drugId);
                if (drugToUpdate != null) {
                    drugToUpdate.setShortName(name);
                    drugToUpdate.setDescription(description);
                    drugToUpdate.setStartDate(startDate);
                    drugToUpdate.setEndDate(endDate);
                    drugToUpdate.setTimeTerm(timeTerm);
                    drugToUpdate.setActive(!today.isBefore(start) && !today.isAfter(end));
                    drugToUpdate.setDoctorName(doctorName);
                    drugToUpdate.setDoctorLocation(doctorLocation);

                    drugDao.updatePrescriptionDrug(drugToUpdate);

                    runOnUiThread(() -> {
                        setResult(RESULT_OK);
                        Toast.makeText(this, "Drug updated successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            }).start();
        });
    }
}
