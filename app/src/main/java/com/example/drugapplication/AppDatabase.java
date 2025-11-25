package com.example.drugapplication;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {PrescriptionDrug.class, TimeTerm.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PrescriptionDrugDao prescriptionDrugDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "prescription_drugs_db")
                            .addMigrations(MIGRATION_1_2)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE time_terms_new (" +
                    "drug_id INTEGER PRIMARY KEY NOT NULL, " +
                    "time_term_name TEXT NOT NULL, " +
                    "FOREIGN KEY(drug_id) REFERENCES prescription_drugs(id) ON DELETE CASCADE)");
            database.execSQL("INSERT INTO time_terms_new (drug_id, time_term_name) SELECT drug_id, time_term_name FROM time_terms");
            database.execSQL("DROP TABLE time_terms");
            database.execSQL("ALTER TABLE time_terms_new RENAME TO time_terms");
        }
    };

    public void closeDatabase() {
        if (INSTANCE != null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE.isOpen()) {
                    INSTANCE.close();
                }
                INSTANCE = null;
            }
        }
    }
}