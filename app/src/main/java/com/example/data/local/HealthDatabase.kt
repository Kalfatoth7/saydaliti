package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.models.*

@Database(
    entities = [
        Pharmacy::class,
        Medicine::class,
        Employee::class,
        Patient::class,
        PatientProfile::class,
        Appointment::class,
        FinancialRecord::class,
        Surgery::class,
        LabTest::class,
        RadiologyScan::class,
        ChatMessage::class,
        Conversation::class,
        InternalAnnouncement::class,
        TaskItem::class,
        AuditLog::class,
        MedicalHistoryRecord::class,
        MedicationLog::class
    ],
    version = 5,
    exportSchema = false
)
abstract class HealthDatabase : RoomDatabase() {
    abstract fun pharmacyDao(): PharmacyDao
    abstract fun medicineDao(): MedicineDao
    abstract fun employeeDao(): EmployeeDao
    abstract fun patientDao(): PatientDao
    abstract fun patientProfileDao(): PatientProfileDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun financialRecordDao(): FinancialRecordDao
    abstract fun surgeryDao(): SurgeryDao
    abstract fun labRadiologyDao(): LabRadiologyDao
    abstract fun messagingDao(): MessagingDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun medicalHistoryDao(): MedicalHistoryDao
    abstract fun medicationLogDao(): MedicationLogDao

    companion object {
        @Volatile
        private var INSTANCE: HealthDatabase? = null

        fun getDatabase(context: Context): HealthDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HealthDatabase::class.java,
                    "health_os_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
