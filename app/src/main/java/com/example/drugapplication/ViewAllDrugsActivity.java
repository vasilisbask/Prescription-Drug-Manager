package com.example.drugapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ViewAllDrugsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PrescriptionDrugAdapter drugAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_drugs);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadDrugs();
    }

    private void loadDrugs() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            PrescriptionDrugDao dao = db.prescriptionDrugDao();

            List<PrescriptionDrugWithTimeTerm> drugsWithTimeTerm = dao.getPrescriptionDrugsWithTimeTerm();

            runOnUiThread(() -> {
                if (drugAdapter == null) {
                    drugAdapter = new PrescriptionDrugAdapter(this, drugsWithTimeTerm);
                    recyclerView.setAdapter(drugAdapter);
                } else {
                    drugAdapter.updateDrugs(drugsWithTimeTerm);
                }
            });
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadDrugs();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDrugs();
    }
}
