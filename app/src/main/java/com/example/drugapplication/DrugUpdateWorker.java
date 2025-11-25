package com.example.drugapplication;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DrugUpdateWorker extends Worker {

    public DrugUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i("DrugUpdateWorker", "Worker started at: " + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        PrescriptionDrugDao drugDao = db.prescriptionDrugDao();

        try {
            List<PrescriptionDrugWithTimeTerm> drugs = drugDao.getPrescriptionDrugsWithTimeTerm();
            LocalDate today = LocalDate.now();

            for (PrescriptionDrugWithTimeTerm drugWithTerm : drugs) {
                PrescriptionDrug drug = drugWithTerm.getPrescriptionDrug();

                LocalDate start = LocalDate.parse(drug.getStartDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                LocalDate end = LocalDate.parse(drug.getEndDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                boolean shouldBeActive = ( !today.isBefore(start) && !today.isAfter(end) );

                if (drug.isActive() != shouldBeActive) {
                    drug.setActive(shouldBeActive);
                    Log.i("DrugUpdateWorker", "Drug ID: " + drug.getId() + " - isActive updated to: " + shouldBeActive);
                }

                if (drug.getLastDateReceived() != null) {
                    LocalDate lastReceived = LocalDate.parse(drug.getLastDateReceived(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    boolean hasReceivedToday = lastReceived.equals(today);
                    if (drug.isHasReceivedToday() != hasReceivedToday) {
                        drug.setHasReceivedToday(hasReceivedToday);
                        Log.i("DrugUpdateWorker", "Drug ID: " + drug.getId() + " - hasReceivedToday updated to: " + hasReceivedToday);
                    }
                } else {
                    if (drug.isHasReceivedToday()) {
                        drug.setHasReceivedToday(false);
                        Log.i("DrugUpdateWorker", "Drug ID: " + drug.getId() + " - hasReceivedToday reset to false");
                    }
                }
                drugDao.updatePrescriptionDrug(drug);
            }

            Log.i("DrugUpdateWorker", "Worker completed successfully at: " + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DrugUpdateWorker", "Worker failed at: " + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            return Result.failure();
        }
    }
}