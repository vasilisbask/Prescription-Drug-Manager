package com.example.drugapplication;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class GenerateFileHTML {
    private final Context context;

    public GenerateFileHTML(Context context) {
        this.context = context;
    }

    public String generateFileContent(List<PrescriptionDrugWithTimeTerm> drugs) {
        StringBuilder content = new StringBuilder();

        content.append("<html><body>");
        content.append("<h1>Active Prescription Drugs</h1>");
        content.append("<table border='1'>");
        content.append("<tr><th>Name</th><th>Description</th><th>Start Date</th><th>End Date</th><th>Time Term</th><th>Doctor Name</th><th>Doctor Location</th></tr>");

        for (PrescriptionDrugWithTimeTerm drugWithTimeTerm : drugs) {
            PrescriptionDrug drug = drugWithTimeTerm.getPrescriptionDrug();
            content.append("<tr>")
                    .append("<td>").append(drug.getShortName()).append("</td>")
                    .append("<td>").append(drug.getDescription()).append("</td>")
                    .append("<td>").append(drug.getStartDate()).append("</td>")
                    .append("<td>").append(drug.getEndDate()).append("</td>")
                    .append("<td>").append(drugWithTimeTerm.getTimeTermName()).append("</td>")
                    .append("<td>").append(drug.getDoctorName()).append("</td>")
                    .append("<td>").append(drug.getDoctorLocation()).append("</td>")
                    .append("</tr>");
        }

        content.append("</table>");
        content.append("</body></html>");

        return content.toString();
    }

    public void exportToFile(String fileName, String content) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.Files.FileColumns.MIME_TYPE, "text/html");
            values.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try (OutputStream os = resolver.openOutputStream(uri)) {
                    os.write(content.getBytes());
                    runOnMainThread(() -> Toast.makeText(context, "File saved to Downloads", Toast.LENGTH_SHORT).show());
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnMainThread(() -> Toast.makeText(context, "Error saving file.", Toast.LENGTH_SHORT).show());
                }
            } else {
                runOnMainThread(() -> Toast.makeText(context, "Failed to create file.", Toast.LENGTH_SHORT).show());
            }
        } else {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadsDir, fileName);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(content.getBytes());
                runOnMainThread(() -> Toast.makeText(context, "File saved to " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show());
            } catch (IOException e) {
                e.printStackTrace();
                runOnMainThread(() -> Toast.makeText(context, "Error saving file.", Toast.LENGTH_SHORT).show());
            }
        }
    }

    private void runOnMainThread(Runnable action) {
        new android.os.Handler(android.os.Looper.getMainLooper()).post(action);
    }
}