package com.example.drugapplication;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HourlyPrescriptionDrugUpdateWorker extends Worker {

    public HourlyPrescriptionDrugUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i("HourlyPrescriptionDrugUpdateWorker", "Worker started at: " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        PrescriptionDrugDao dao = db.prescriptionDrugDao();

        try {
            List<PrescriptionDrugWithTimeTerm> drugs = dao.getPrescriptionDrugsWithTimeTerm();
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (PrescriptionDrugWithTimeTerm drugWithTimeTerm : drugs) {
                PrescriptionDrug drug = drugWithTimeTerm.getPrescriptionDrug();

                LocalDate endDate = LocalDate.parse(drug.getEndDate(), formatter);
                boolean newActive = !today.isAfter(endDate);

                boolean updated = false;

                if (drug.getLastDateReceived() != null) {
                    LocalDate lastReceived = LocalDate.parse(drug.getLastDateReceived(), formatter);
                    if (!lastReceived.equals(today) && drug.isHasReceivedToday()) {
                        drug.setHasReceivedToday(false);
                        updated = true;
                        Log.i("HourlyPrescriptionDrugUpdateWorker",
                                "Drug ID: " + drug.getId() + " - hasReceivedToday reset to false");
                    }
                }

                if (drug.isActive() != newActive) {
                    drug.setActive(newActive);
                    updated = true;
                    Log.i("HourlyPrescriptionDrugUpdateWorker",
                            "Drug ID: " + drug.getId() + " - Active set to: " + newActive);
                }

                if (updated) {
                    dao.updatePrescriptionDrug(drug);
                }
            }

            Log.i("HourlyPrescriptionDrugUpdateWorker", "Worker completed successfully at: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("HourlyPrescriptionDrugUpdateWorker", "Worker failed at: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            return Result.failure();
        }
    }
}