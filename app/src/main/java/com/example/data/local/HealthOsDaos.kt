package com.example.data.local

import androidx.room.*
import com.example.data.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PharmacyDao {
    @Query("SELECT * FROM pharmacies")
    fun getAllPharmacies(): Flow<List<Pharmacy>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPharmacies(pharmacies: List<Pharmacy>)

    @Update
    suspend fun updatePharmacy(pharmacy: Pharmacy)

    @Query("UPDATE pharmacies SET salesToday = 0.0, salesMonth = 0.0, purchasesMonth = 0.0, profitMonth = 0.0, txCountToday = 0")
    suspend fun resetPharmacySales()
}

@Dao
interface MedicineDao {
    @Query("SELECT * FROM medicines ORDER BY tradeName ASC")
    fun getAllMedicines(): Flow<List<Medicine>>

    @Query("SELECT * FROM medicines WHERE pharmacyId = :pharmacyId")
    fun getMedicinesByPharmacy(pharmacyId: String): Flow<List<Medicine>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: Medicine)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicines(medicines: List<Medicine>)

    @Update
    suspend fun updateMedicine(medicine: Medicine)

    @Delete
    suspend fun deleteMedicine(medicine: Medicine)
}

@Dao
interface EmployeeDao {
    @Query("SELECT * FROM employees ORDER BY id DESC")
    fun getAllEmployees(): Flow<List<Employee>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: Employee)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployees(employees: List<Employee>)

    @Update
    suspend fun updateEmployee(employee: Employee)

    @Delete
    suspend fun deleteEmployee(employee: Employee)
}

@Dao
interface PatientDao {
    @Query("SELECT * FROM patients ORDER BY id DESC")
    fun getAllPatients(): Flow<List<Patient>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: Patient)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatients(patients: List<Patient>)

    @Update
    suspend fun updatePatient(patient: Patient)

    @Delete
    suspend fun deletePatient(patient: Patient)
}

@Dao
interface PatientProfileDao {
    @Query("SELECT * FROM patient_profiles ORDER BY id DESC")
    fun getAllPatientProfiles(): Flow<List<PatientProfile>>

    @Query("SELECT * FROM patient_profiles WHERE id = :id")
    fun getPatientProfileById(id: Long): Flow<PatientProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatientProfile(patientProfile: PatientProfile): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatientProfiles(patientProfiles: List<PatientProfile>)

    @Update
    suspend fun updatePatientProfile(patientProfile: PatientProfile)

    @Delete
    suspend fun deletePatientProfile(patientProfile: PatientProfile)

    @Query("DELETE FROM patient_profiles WHERE id = :id")
    suspend fun deletePatientProfileById(id: Long)
}

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments ORDER BY id DESC")
    fun getAllAppointments(): Flow<List<Appointment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: Appointment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointments(appointments: List<Appointment>)

    @Update
    suspend fun updateAppointment(appointment: Appointment)

    @Delete
    suspend fun deleteAppointment(appointment: Appointment)
}

@Dao
interface FinancialRecordDao {
    @Query("SELECT * FROM financial_records ORDER BY id DESC")
    fun getAllRecords(): Flow<List<FinancialRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: FinancialRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<FinancialRecord>)

    @Query("DELETE FROM financial_records")
    suspend fun deleteAllRecords()
}

@Dao
interface SurgeryDao {
    @Query("SELECT * FROM surgeries ORDER BY id DESC")
    fun getAllSurgeries(): Flow<List<Surgery>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurgery(surgery: Surgery)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurgeries(surgeries: List<Surgery>)
}

@Dao
interface LabRadiologyDao {
    @Query("SELECT * FROM lab_tests ORDER BY id DESC")
    fun getAllLabTests(): Flow<List<LabTest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabTest(labTest: LabTest)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabTests(labTests: List<LabTest>)

    @Query("SELECT * FROM radiology_scans ORDER BY id DESC")
    fun getAllRadiologyScans(): Flow<List<RadiologyScan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: RadiologyScan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScans(scans: List<RadiologyScan>)
}

@Dao
interface MessagingDao {
    @Query("SELECT * FROM conversations ORDER BY isPinned DESC, id ASC")
    fun getAllConversations(): Flow<List<Conversation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: Conversation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<Conversation>)

    @Update
    suspend fun updateConversation(conversation: Conversation)

    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY id ASC")
    fun getMessagesForConversation(conversationId: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessage>)

    @Update
    suspend fun updateMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: Long)

    @Query("SELECT * FROM internal_announcements ORDER BY id DESC")
    fun getAllAnnouncements(): Flow<List<InternalAnnouncement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: InternalAnnouncement)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncements(announcements: List<InternalAnnouncement>)

    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasks(): Flow<List<TaskItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskItem>)

    @Update
    suspend fun updateTask(task: TaskItem)

}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY id DESC")
    fun getAllAuditLogs(): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLog): Long

    @Query("DELETE FROM audit_logs")
    suspend fun deleteAllAuditLogs()
}

@Dao
interface MedicalHistoryDao {
    @Query("SELECT * FROM medical_history_records ORDER BY id DESC")
    fun getAllMedicalHistoryRecords(): Flow<List<MedicalHistoryRecord>>

    @Query("SELECT * FROM medical_history_records WHERE patientId = :patientId ORDER BY id DESC")
    fun getMedicalHistoryForPatient(patientId: Long): Flow<List<MedicalHistoryRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: MedicalHistoryRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<MedicalHistoryRecord>)

    @Update
    suspend fun updateRecord(record: MedicalHistoryRecord)

    @Delete
    suspend fun deleteRecord(record: MedicalHistoryRecord)
}

@Dao
interface MedicationLogDao {
    @Query("SELECT * FROM medication_logs ORDER BY id DESC")
    fun getAllMedicationLogs(): Flow<List<MedicationLog>>

    @Query("SELECT * FROM medication_logs WHERE patientId = :patientId ORDER BY id DESC")
    fun getMedicationLogsForPatient(patientId: Long): Flow<List<MedicationLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: MedicationLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<MedicationLog>)

    @Update
    suspend fun updateLog(log: MedicationLog)

    @Delete
    suspend fun deleteLog(log: MedicationLog)
}

