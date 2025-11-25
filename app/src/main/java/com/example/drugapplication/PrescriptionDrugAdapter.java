package com.example.drugapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PrescriptionDrugAdapter extends RecyclerView.Adapter<PrescriptionDrugAdapter.DrugViewHolder> {
    private final List<PrescriptionDrugWithTimeTerm> drugsWithTimeTerm;
    private final PrescriptionDrugDao drugDao;
    private final Context context;

    public PrescriptionDrugAdapter(Context context, List<PrescriptionDrugWithTimeTerm> drugsWithTimeTerm) {
        this.context = context;
        this.drugsWithTimeTerm = new ArrayList<>(drugsWithTimeTerm);
        AppDatabase db = AppDatabase.getInstance(context);
        this.drugDao = db.prescriptionDrugDao();
    }

    @NonNull
    @Override
    public DrugViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.drug_item, parent, false);
        return new DrugViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrugViewHolder holder, int position) {
        PrescriptionDrugWithTimeTerm drugWithTerm = drugsWithTimeTerm.get(position);
        PrescriptionDrug drug = drugWithTerm.getPrescriptionDrug();

        holder.drugId.setText("ID: " + drug.getId());
        holder.drugName.setText("Name: " + drug.getShortName());
        holder.drugDescription.setText("Description: " + drug.getDescription());
        holder.drugStartDate.setText("Start Date: " + drug.getStartDate());
        holder.drugEndDate.setText("End Date: " + drug.getEndDate());
        holder.drugTimeTerm.setText("Time Term: " + drugWithTerm.getTimeTermName());
        holder.doctorName.setText("Doctor: " + (drug.getDoctorName() != null ? drug.getDoctorName() : "N/A"));
        holder.drugActive.setText("Active: " + drug.isActive());
        holder.drugLastReceived.setText("Last Received: " + (drug.getLastDateReceived() != null ? drug.getLastDateReceived() : "N/A"));

        holder.drugReceivedToday.setOnCheckedChangeListener(null);
        holder.drugReceivedToday.setChecked(drug.isHasReceivedToday());

        holder.drugReceivedToday.setEnabled(!drug.isHasReceivedToday());

        holder.drugReceivedToday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                new Thread(() -> {
                    String today = java.time.LocalDate.now()
                            .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                    drug.setHasReceivedToday(true);
                    drug.setLastDateReceived(today);

                    if (drug.isActive()) {
                        drug.setActive(false);
                    }

                    drugDao.updatePrescriptionDrug(drug);

                    holder.itemView.post(() -> {
                        holder.drugLastReceived.setText("Last Received: " + today);
                        holder.drugReceivedToday.setEnabled(false); // κλειδώνει το checkbox
                        notifyItemChanged(position);
                    });
                }).start();
            }
        });

        holder.editDrugButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditDrugActivity.class);
            intent.putExtra("DRUG_ID", drug.getId());
            try {
                ((ViewAllDrugsActivity) context).startActivityForResult(intent, 1);
            } catch (ClassCastException e) {
                context.startActivity(intent);
            }
        });

        holder.deleteDrugButton.setOnClickListener(v -> {
            new Thread(() -> {
                int rowsDeleted = drugDao.deletePrescriptionDrugById(drug.getId());
                if (rowsDeleted > 0) {
                    holder.itemView.post(() -> {
                        if (position >= 0 && position < drugsWithTimeTerm.size()) {
                            drugsWithTimeTerm.remove(position);
                            notifyItemRemoved(position);
                        }
                    });
                }
            }).start();
        });

        holder.viewOnMapButton.setOnClickListener(v -> {
            String location = drug.getDoctorLocation();
            if (location != null && !location.isEmpty()) {
                Uri gmmIntentUri = Uri.parse(
                        "https://www.google.com/maps/search/?api=1&query=" + Uri.encode(location)
                );

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                context.startActivity(mapIntent);
            } else {
                Toast.makeText(context, "No doctor location specified", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return drugsWithTimeTerm.size();
    }

    public void updateDrugs(List<PrescriptionDrugWithTimeTerm> newDrugs) {
        drugsWithTimeTerm.clear();
        drugsWithTimeTerm.addAll(newDrugs);
        notifyDataSetChanged();
    }

    public static class DrugViewHolder extends RecyclerView.ViewHolder {
        TextView drugId, drugName, drugDescription, drugStartDate, drugEndDate, drugTimeTerm,
                doctorName, drugActive, drugLastReceived;
        CheckBox drugReceivedToday;
        Button editDrugButton, deleteDrugButton, viewOnMapButton;

        public DrugViewHolder(@NonNull View itemView) {
            super(itemView);
            drugId = itemView.findViewById(R.id.drug_id);
            drugName = itemView.findViewById(R.id.drug_name);
            drugDescription = itemView.findViewById(R.id.drug_description);
            drugStartDate = itemView.findViewById(R.id.drug_start_date);
            drugEndDate = itemView.findViewById(R.id.drug_end_date);
            drugTimeTerm = itemView.findViewById(R.id.drug_time_term);
            doctorName = itemView.findViewById(R.id.drug_doctor_name);
            drugActive = itemView.findViewById(R.id.drug_active);
            drugLastReceived = itemView.findViewById(R.id.drug_last_received);
            drugReceivedToday = itemView.findViewById(R.id.drug_received_today);
            editDrugButton = itemView.findViewById(R.id.edit_drug_button);
            deleteDrugButton = itemView.findViewById(R.id.delete_drug_button);
            viewOnMapButton = itemView.findViewById(R.id.view_on_map_button);
        }
    }
}