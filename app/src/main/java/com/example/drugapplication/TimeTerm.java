package com.example.drugapplication;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "time_terms",
        indices = {@Index("drug_id")},
        foreignKeys = @ForeignKey(
                entity = PrescriptionDrug.class,
                parentColumns = "id",
                childColumns = "drug_id",
                onDelete = ForeignKey.CASCADE
        )
)
public class TimeTerm {

    @PrimaryKey
    @ColumnInfo(name = "drug_id")
    private int drugId;

    @ColumnInfo(name = "time_term_name")
    private String timeTermName;

    public TimeTerm(int drugId, String timeTermName) {
        this.drugId = drugId;
        this.timeTermName = timeTermName;
    }

    public int getDrugId() { return drugId; }
    public void setDrugId(int drugId) { this.drugId = drugId; }

    public String getTimeTermName() { return timeTermName; }
    public void setTimeTermName(String timeTermName) { this.timeTermName = timeTermName; }
}