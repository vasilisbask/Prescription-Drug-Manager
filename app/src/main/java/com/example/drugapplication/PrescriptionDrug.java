package com.example.drugapplication;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "prescription_drugs")
public class PrescriptionDrug {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "short_name")
    private String shortName;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "start_date")
    private String startDate;

    @ColumnInfo(name = "end_date")
    private String endDate;

    @ColumnInfo(name = "time_term")
    private String timeTerm;

    @ColumnInfo(name = "doctor_name")
    private String doctorName;

    @ColumnInfo(name = "doctor_location")
    private String doctorLocation;

    @ColumnInfo(name = "is_active")
    private boolean isActive;

    @ColumnInfo(name = "last_date_received")
    private String lastDateReceived;

    @ColumnInfo(name = "has_received_today")
    private boolean hasReceivedToday;

    public PrescriptionDrug(String shortName, String description, String startDate, String endDate, String timeTerm, String doctorName, String doctorLocation) {
        this.shortName = shortName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.timeTerm = timeTerm;
        this.doctorName = doctorName;
        this.doctorLocation = doctorLocation;

        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

        java.time.LocalDate start = java.time.LocalDate.parse(startDate, formatter);
        java.time.LocalDate end = java.time.LocalDate.parse(endDate, formatter);

        this.isActive = (!today.isBefore(start) && !today.isAfter(end));
        this.lastDateReceived = null;
        this.hasReceivedToday = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getShortName() { return shortName; }
    public void setShortName(String shortName) { this.shortName = shortName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getTimeTerm() { return timeTerm; }
    public void setTimeTerm(String timeTerm) { this.timeTerm = timeTerm; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getDoctorLocation() { return doctorLocation; }
    public void setDoctorLocation(String doctorLocation) { this.doctorLocation = doctorLocation; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getLastDateReceived() { return lastDateReceived; }
    public void setLastDateReceived(String lastDateReceived) { this.lastDateReceived = lastDateReceived; }

    public boolean isHasReceivedToday() { return hasReceivedToday; }
    public void setHasReceivedToday(boolean hasReceivedToday) { this.hasReceivedToday = hasReceivedToday; }
}