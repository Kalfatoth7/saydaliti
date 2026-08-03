package com.example.data.repository

import android.content.Context
import com.example.data.local.DemoData
import com.example.data.local.HealthDatabase
import com.example.data.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HealthOsRepository(private val context: Context) {
    private val db = HealthDatabase.getDatabase(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    // Current Role State
    private val _currentRole = MutableStateFlow(UserRole.SUPER_ADMIN)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    fun setCurrentRole(role: UserRole) {
        _currentRole.value = role
    }

    // Notifications State
    private val _notifications = MutableStateFlow(DemoData.notifications)
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    fun dismissNotification(id: String) {
        _notifications.value = _notifications.value.filter { it.id != id }
    }

    init {
        // Seed initial data if DB is empty
        scope.launch {
            db.pharmacyDao().getAllPharmacies().firstOrNull().let { list ->
                if (list.isNullOrEmpty()) {
                    db.pharmacyDao().insertPharmacies(DemoData.pharmacies)
                    db.medicineDao().insertMedicines(DemoData.medicines)
                    db.employeeDao().insertEmployees(DemoData.employees)
                    db.patientDao().insertPatients(DemoData.patients)
                    db.appointmentDao().insertAppointments(DemoData.appointments)
                    db.financialRecordDao().insertRecords(DemoData.financialRecords)
                    db.surgeryDao().insertSurgeries(DemoData.surgeries)
                    db.labRadiologyDao().insertLabTests(DemoData.labTests)
                    db.labRadiologyDao().insertScans(DemoData.radiologyScans)
                }
            }
            db.medicalHistoryDao().getAllMedicalHistoryRecords().firstOrNull().let { list ->
                if (list.isNullOrEmpty()) {
                    db.medicalHistoryDao().insertRecords(DemoData.medicalHistoryRecords)
                }
            }
            db.medicationLogDao().getAllMedicationLogs().firstOrNull().let { list ->
                if (list.isNullOrEmpty()) {
                    db.medicationLogDao().insertLogs(DemoData.medicationLogs)
                }
            }
            db.messagingDao().getAllConversations().firstOrNull().let { convs ->
                if (convs.isNullOrEmpty()) {
                    db.messagingDao().insertConversations(DemoData.conversations)
                    db.messagingDao().insertMessages(DemoData.chatMessages)
                    db.messagingDao().insertAnnouncements(DemoData.announcements)
                    db.messagingDao().insertTasks(DemoData.tasks)
                }
            }
        }
    }

    // Flow Getters
    val pharmacies: Flow<List<Pharmacy>> = db.pharmacyDao().getAllPharmacies()
    val medicines: Flow<List<Medicine>> = db.medicineDao().getAllMedicines()
    val employees: Flow<List<Employee>> = db.employeeDao().getAllEmployees()
    val patients: Flow<List<Patient>> = db.patientDao().getAllPatients()
    val patientProfiles: Flow<List<PatientProfile>> = db.patientProfileDao().getAllPatientProfiles()
    val appointments: Flow<List<Appointment>> = db.appointmentDao().getAllAppointments()
    val financialRecords: Flow<List<FinancialRecord>> = db.financialRecordDao().getAllRecords()
    val auditLogs: Flow<List<AuditLog>> = db.auditLogDao().getAllAuditLogs()
    val surgeries: Flow<List<Surgery>> = db.surgeryDao().getAllSurgeries()
    val labTests: Flow<List<LabTest>> = db.labRadiologyDao().getAllLabTests()
    val radiologyScans: Flow<List<RadiologyScan>> = db.labRadiologyDao().getAllRadiologyScans()
    val medicalHistoryRecords: Flow<List<MedicalHistoryRecord>> = db.medicalHistoryDao().getAllMedicalHistoryRecords()
    val medicationLogs: Flow<List<MedicationLog>> = db.medicationLogDao().getAllMedicationLogs()

    fun getMedicalHistoryForPatient(patientId: Long): Flow<List<MedicalHistoryRecord>> =
        db.medicalHistoryDao().getMedicalHistoryForPatient(patientId)

    fun getMedicationLogsForPatient(patientId: Long): Flow<List<MedicationLog>> =
        db.medicationLogDao().getMedicationLogsForPatient(patientId)

    suspend fun addMedicalHistoryRecord(record: MedicalHistoryRecord): Long {
        return db.medicalHistoryDao().insertRecord(record)
    }

    suspend fun addMedicationLog(log: MedicationLog): Long {
        return db.medicationLogDao().insertLog(log)
    }

    suspend fun updateMedicationLogStatus(logId: Long, newStatus: String, loggedTime: String) {
        val logs = medicationLogs.firstOrNull() ?: emptyList()
        val log = logs.find { it.id == logId } ?: return
        db.medicationLogDao().updateLog(log.copy(status = newStatus, loggedTime = loggedTime))
    }

    // Messaging Flows & Methods
    val conversations: Flow<List<Conversation>> = db.messagingDao().getAllConversations()
    val announcements: Flow<List<InternalAnnouncement>> = db.messagingDao().getAllAnnouncements()
    val tasks: Flow<List<TaskItem>> = db.messagingDao().getAllTasks()

    fun getMessagesForConversation(conversationId: String): Flow<List<ChatMessage>> {
        return db.messagingDao().getMessagesForConversation(conversationId)
    }

    suspend fun sendMessage(message: ChatMessage): Long {
        val id = db.messagingDao().insertMessage(message)
        // Update last message in conversation
        val currentConvs = conversations.firstOrNull() ?: emptyList()
        val conv = currentConvs.find { it.id == message.conversationId }
        if (conv != null) {
            val updatedConv = conv.copy(
                lastMessage = "${message.senderName}: ${message.content.take(30)}",
                lastMessageTime = message.timestamp
            )
            db.messagingDao().updateConversation(updatedConv)
        }
        return id
    }

    suspend fun createOrGetConversationForEntity(
        type: String, // "LINKED_WORK", "LINKED_PATIENT", "INDIVIDUAL"
        entityId: String,
        title: String,
        subtitle: String,
        linkedEntityType: String = ""
    ): String {
        val convId = "conv_${type.lowercase()}_$entityId"
        val existingConvs = conversations.firstOrNull() ?: emptyList()
        val existing = existingConvs.find { it.id == convId }
        if (existing == null) {
            val newConv = Conversation(
                id = convId,
                title = title,
                subtitle = subtitle,
                type = type,
                avatarInitials = if (type == "LINKED_PATIENT") "مريض" else "عمل",
                lastMessage = "بدء محادثة جديدة مرتبطة بـ $title",
                lastMessageTime = "الآن",
                presenceStatus = PresenceStatus.ONLINE,
                linkedEntityType = linkedEntityType,
                linkedEntityId = entityId
            )
            db.messagingDao().insertConversation(newConv)
        }
        return convId
    }

    suspend fun togglePinConversation(conversationId: String) {
        val convs = conversations.firstOrNull() ?: emptyList()
        val conv = convs.find { it.id == conversationId } ?: return
        db.messagingDao().updateConversation(conv.copy(isPinned = !conv.isPinned))
    }

    suspend fun toggleFavoriteConversation(conversationId: String) {
        val convs = conversations.firstOrNull() ?: emptyList()
        val conv = convs.find { it.id == conversationId } ?: return
        db.messagingDao().updateConversation(conv.copy(isFavorite = !conv.isFavorite))
    }

    suspend fun addAnnouncement(announcement: InternalAnnouncement) {
        db.messagingDao().insertAnnouncement(announcement)
    }

    suspend fun addTask(task: TaskItem): Long {
        return db.messagingDao().insertTask(task)
    }

    suspend fun updateTaskStatus(taskId: Long, newStatus: String, completedBy: String = "") {
        val allTasks = tasks.firstOrNull() ?: emptyList()
        val task = allTasks.find { it.id == taskId } ?: return
        db.messagingDao().updateTask(
            task.copy(
                status = newStatus,
                completedBy = if (newStatus == "مكتملة") completedBy else task.completedBy,
                completedAt = if (newStatus == "مكتملة") "اليوم" else task.completedAt
            )
        )
    }


    // Add / Update / Delete Mutations
    suspend fun addMedicine(medicine: Medicine) {
        db.medicineDao().insertMedicine(medicine)
    }

    suspend fun updateMedicine(medicine: Medicine) {
        db.medicineDao().updateMedicine(medicine)
    }

    suspend fun deleteMedicine(medicine: Medicine) {
        db.medicineDao().deleteMedicine(medicine)
    }

    suspend fun transferStock(medicineId: Long, targetPharmacyId: String, transferQty: Int) {
        val allMeds = medicines.firstOrNull() ?: emptyList()
        val med = allMeds.find { it.id == medicineId } ?: return

        if (med.stockQuantity >= transferQty) {
            // Reduce stock from source
            db.medicineDao().updateMedicine(med.copy(stockQuantity = med.stockQuantity - transferQty))

            // Check if item already exists in target pharmacy
            val targetMed = allMeds.find { it.tradeName == med.tradeName && it.pharmacyId == targetPharmacyId }
            if (targetMed != null) {
                db.medicineDao().updateMedicine(targetMed.copy(stockQuantity = targetMed.stockQuantity + transferQty))
            } else {
                db.medicineDao().insertMedicine(med.copy(id = 0, pharmacyId = targetPharmacyId, stockQuantity = transferQty))
            }
        }
    }

    suspend fun addPatient(patient: Patient) {
        db.patientDao().insertPatient(patient)
    }

    suspend fun addPatientProfile(patientProfile: PatientProfile): Long {
        return db.patientProfileDao().insertPatientProfile(patientProfile)
    }

    suspend fun updatePatientProfile(patientProfile: PatientProfile) {
        db.patientProfileDao().updatePatientProfile(patientProfile)
    }

    suspend fun deletePatientProfile(patientProfile: PatientProfile) {
        db.patientProfileDao().deletePatientProfile(patientProfile)
    }

    fun getPatientProfileById(id: Long): Flow<PatientProfile?> {
        return db.patientProfileDao().getPatientProfileById(id)
    }

    suspend fun addEmployee(employee: Employee) {
        db.employeeDao().insertEmployee(employee)
    }

    suspend fun updateEmployee(employee: Employee) {
        db.employeeDao().updateEmployee(employee)
    }

    suspend fun addAppointment(appointment: Appointment) {
        db.appointmentDao().insertAppointment(appointment)
    }

    suspend fun updateAppointmentStatus(appointmentId: Long, newStatus: String) {
        val appts = appointments.firstOrNull() ?: emptyList()
        val appt = appts.find { it.id == appointmentId } ?: return
        db.appointmentDao().updateAppointment(appt.copy(status = newStatus))
    }

    suspend fun addFinancialRecord(record: FinancialRecord) {
        db.financialRecordDao().insertRecord(record)
    }

    suspend fun resetFinancialRecords(performedBy: String = "أ.د. محي الدين الجعفري (مدير المركز)") {
        // 1. Delete all financial records (Revenue, Expenses, Purchases, Salaries)
        db.financialRecordDao().deleteAllRecords()

        // 2. Reset sales and profit counters in all 4 pharmacies
        db.pharmacyDao().resetPharmacySales()

        // 3. Log the operation in AuditLogs table
        val currentTime = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
        val auditLog = AuditLog(
            action = "تصفير البيانات المالية والجداول الماليّة",
            performedBy = performedBy,
            details = "تم تصفير كافة الجداول المالية (الإيرادات، المصروفات، المشتريات، والرواتب) وتصغير حسابات الصيدليات الأربع إلى 0.0 ر.س.",
            timestamp = currentTime,
            category = "الأمن والرقابة المالية",
            severity = "عالي الخطورة"
        )
        db.auditLogDao().insertAuditLog(auditLog)

        // 4. Trigger system alert notification
        val alertNotification = NotificationItem(
            id = System.currentTimeMillis().toString(),
            title = "🚨 تصفير البيانات المالية الإجمالية",
            message = "قام $performedBy بتصفير كافة الجداول والبيانات المالية وتوثيق الإجراء في سجل الرقابة (AuditLogs).",
            severity = "عاجل",
            timestamp = "الآن"
        )
        _notifications.value = listOf(alertNotification) + _notifications.value
    }

    suspend fun addSurgery(surgery: Surgery) {
        db.surgeryDao().insertSurgery(surgery)
    }

    suspend fun addLabTest(labTest: LabTest) {
        db.labRadiologyDao().insertLabTest(labTest)
    }

    suspend fun addScan(scan: RadiologyScan) {
        db.labRadiologyDao().insertScan(scan)
    }
}
