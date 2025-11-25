package com.example.drugapplication;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface PrescriptionDrugDao {

    @Insert
    long insertPrescriptionDrug(PrescriptionDrug prescriptionDrug);

    @Insert
    void insertTimeTerm(TimeTerm timeTerm);

    @Query("SELECT prescription_drugs.*, time_terms.time_term_name FROM prescription_drugs " +
            "INNER JOIN time_terms ON prescription_drugs.id = time_terms.drug_id")
    List<PrescriptionDrugWithTimeTerm> getPrescriptionDrugsWithTimeTerm();

    @Query("DELETE FROM prescription_drugs WHERE id = :drugId")
    int deletePrescriptionDrugById(int drugId);

    @Query("SELECT * FROM prescription_drugs WHERE id = :drugId")
    PrescriptionDrug getPrescriptionDrugById(int drugId);

    @Update
    void updatePrescriptionDrug(PrescriptionDrug prescriptionDrug);

    @Query("SELECT pd.*, tt.time_term_name " +
            "FROM prescription_drugs AS pd " +
            "LEFT JOIN time_terms AS tt ON pd.id = tt.drug_id " +
            "WHERE pd.is_active = 1 AND pd.has_received_today = 0 " +
            "ORDER BY pd.start_date ASC")
    List<PrescriptionDrugWithTimeTerm> getActiveDrugs();


    @Query("SELECT prescription_drugs.*, time_terms.time_term_name FROM prescription_drugs " +
            "INNER JOIN time_terms ON prescription_drugs.id = time_terms.drug_id " +
            "WHERE prescription_drugs.is_active = 1 " +
            "ORDER BY " +
            "CASE time_terms.time_term_name " +
            "   WHEN 'before-breakfast' THEN 1 " +
            "   WHEN 'at-breakfast' THEN 2 " +
            "   WHEN 'after-breakfast' THEN 3 " +
            "   WHEN 'before-lunch' THEN 4 " +
            "   WHEN 'at-lunch' THEN 5 " +
            "   WHEN 'after-lunch' THEN 6 " +
            "   WHEN 'before-dinner' THEN 7 " +
            "   WHEN 'at-dinner' THEN 8 " +
            "   WHEN 'after-dinner' THEN 9 " +
            "   ELSE 10 END, " +
            "prescription_drugs.start_date ASC")
    List<PrescriptionDrugWithTimeTerm> getActivePrescriptionDrugs();

}