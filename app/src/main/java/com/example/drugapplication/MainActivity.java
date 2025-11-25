package com.example.drugapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PrescriptionDrugAdapter drugAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scheduleHourlyWorker();

        recyclerView = findViewById(R.id.recycler_view_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadDrugs();

        findViewById(R.id.add_drug_button).setOnClickListener(view -> {
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(DrugUpdateWorker.class).build();
            WorkManager.getInstance(this).enqueue(workRequest);
            Intent intent_add_drug = new Intent(MainActivity.this, AddPrescriptionDrugActivity.class);
            startActivity(intent_add_drug);
        });

        findViewById(R.id.view_drugs_button).setOnClickListener(view -> {
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(DrugUpdateWorker.class).build();
            WorkManager.getInstance(this).enqueue(workRequest);
            Intent intent_view_drugs = new Intent(MainActivity.this, ViewActiveDrugsActivity.class);
            startActivity(intent_view_drugs);
        });

        findViewById(R.id.delete_drug_button).setOnClickListener(v -> {
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(DrugUpdateWorker.class).build();
            WorkManager.getInstance(this).enqueue(workRequest);
            Intent intent_delete_drug = new Intent(MainActivity.this, DeletePrescriptionDrugActivity.class);
            startActivity(intent_delete_drug);
        });

        findViewById(R.id.export_button).setOnClickListener(view -> {
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(DrugUpdateWorker.class).build();
            WorkManager.getInstance(this).enqueue(workRequest);
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                List<PrescriptionDrugWithTimeTerm> activeDrugs = db.prescriptionDrugDao().getActiveDrugs();

                if (activeDrugs.isEmpty()) {
                    runOnUiThread(() -> Toast.makeText(this, "No active drugs to export.", Toast.LENGTH_SHORT).show());
                    return;
                }

                GenerateFileHTML fileGenerator = new GenerateFileHTML(this);
                String content = fileGenerator.generateFileContent(activeDrugs);
                fileGenerator.exportToFile("ActiveDrugs.html", content);
            }).start();
        });
    }

    private void loadDrugs() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<PrescriptionDrugWithTimeTerm> drugsWithTimeTerm = db.prescriptionDrugDao().getPrescriptionDrugsWithTimeTerm();

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

    private void scheduleHourlyWorker() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextHour = now.truncatedTo(ChronoUnit.HOURS).plusHours(1);
        long initialDelay = Duration.between(now, nextHour).toMinutes();

        PeriodicWorkRequest hourlyWorkRequest = new PeriodicWorkRequest.Builder(HourlyPrescriptionDrugUpdateWorker.class, 1, TimeUnit.HOURS)
                .setInitialDelay(initialDelay, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "HourlyDrugUpdate",
                ExistingPeriodicWorkPolicy.KEEP,
                hourlyWorkRequest
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDrugs();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppDatabase.getInstance(getApplicationContext()).closeDatabase();
    }
}
