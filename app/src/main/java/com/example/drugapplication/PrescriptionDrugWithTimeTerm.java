package com.example.drugapplication;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

public class PrescriptionDrugWithTimeTerm {

    @Embedded
    public PrescriptionDrug prescriptionDrug;

    @ColumnInfo(name = "time_term_name")
    public String timeTermName;

    public PrescriptionDrugWithTimeTerm(PrescriptionDrug prescriptionDrug, String timeTermName) {
        this.prescriptionDrug = prescriptionDrug;
        this.timeTermName = timeTermName;
    }

    public PrescriptionDrug getPrescriptionDrug() {
        return prescriptionDrug;
    }
    public void setPrescriptionDrug(PrescriptionDrug prescriptionDrug) {
        this.prescriptionDrug = prescriptionDrug;
    }

    public String getTimeTermName() {
        return timeTermName;
    }
    public void setTimeTermName(String timeTermName) {
        this.timeTermName = timeTermName;
    }
}