package com.example.drugapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class DeletePrescriptionDrugActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_drug);

        EditText drugIdInput = findViewById(R.id.drug_id_input);
        Button deleteDrugButton = findViewById(R.id.delete_drug_button);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "prescription_drugs_db").build();
        PrescriptionDrugDao dao = db.prescriptionDrugDao();

        deleteDrugButton.setOnClickListener(v -> {
            String drugIdStr = drugIdInput.getText().toString();

            if (drugIdStr.isEmpty()) {
                Toast.makeText(this, "Please enter a Drug ID", Toast.LENGTH_SHORT).show();
                return;
            }

            int drugId = Integer.parseInt(drugIdStr);

            new Thread(() -> {
                int rowsAffected = dao.deletePrescriptionDrugById(drugId);

                runOnUiThread(() -> {
                    if (rowsAffected > 0) {
                        Toast.makeText(this, rowsAffected + " drug(s) deleted successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "No drug found with ID: " + drugId, Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });
    }
}