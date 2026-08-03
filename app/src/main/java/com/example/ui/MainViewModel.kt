package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.*
import com.example.data.repository.HealthOsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val repository = HealthOsRepository(application)

    // Current Screen Route
    private val _currentRoute = MutableStateFlow("dashboard")
    val currentRoute: StateFlow<String> = _currentRoute.asStateFlow()

    fun navigateTo(route: String) {
        _currentRoute.value = route
    }

    // Role state
    val currentRole = repository.currentRole
    fun setCurrentRole(role: UserRole) {
        repository.setCurrentRole(role)
    }

    // Notifications
    val notifications = repository.notifications
    fun dismissNotification(id: String) {
        repository.dismissNotification(id)
    }

    // Flows
    val pharmacies = repository.pharmacies
    val medicines = repository.medicines
    val employees = repository.employees
    val patients = repository.patients
    val appointments = repository.appointments
    val financialRecords = repository.financialRecords
    val auditLogs = repository.auditLogs
    val surgeries = repository.surgeries
    val labTests = repository.labTests
    val radiologyScans = repository.radiologyScans
    val medicalHistoryRecords = repository.medicalHistoryRecords
    val medicationLogs = repository.medicationLogs

    // Messaging Flows
    val conversations = repository.conversations
    val announcements = repository.announcements
    val tasks = repository.tasks

    // Selected conversation ID for messaging screen
    private val _selectedConversationId = MutableStateFlow<String?>(null)
    val selectedConversationId: StateFlow<String?> = _selectedConversationId.asStateFlow()

    fun selectConversation(id: String?) {
        _selectedConversationId.value = id
    }

    fun getMessagesForConversation(conversationId: String) =
        repository.getMessagesForConversation(conversationId)

    fun sendMessage(message: ChatMessage) {
        viewModelScope.launch { repository.sendMessage(message) }
    }

    fun openLinkedConversation(
        type: String,
        entityId: String,
        title: String,
        subtitle: String,
        linkedEntityType: String = ""
    ) {
        viewModelScope.launch {
            val convId = repository.createOrGetConversationForEntity(
                type = type,
                entityId = entityId,
                title = title,
                subtitle = subtitle,
                linkedEntityType = linkedEntityType
            )
            _selectedConversationId.value = convId
            _currentRoute.value = "messaging"
        }
    }

    fun togglePinConversation(convId: String) {
        viewModelScope.launch { repository.togglePinConversation(convId) }
    }

    fun toggleFavoriteConversation(convId: String) {
        viewModelScope.launch { repository.toggleFavoriteConversation(convId) }
    }

    fun addAnnouncement(announcement: InternalAnnouncement) {
        viewModelScope.launch { repository.addAnnouncement(announcement) }
    }

    fun addTask(task: TaskItem) {
        viewModelScope.launch { repository.addTask(task) }
    }

    fun updateTaskStatus(taskId: Long, newStatus: String, completedBy: String = "") {
        viewModelScope.launch { repository.updateTaskStatus(taskId, newStatus, completedBy) }
    }

    // Actions
    fun addMedicine(medicine: Medicine) {
        viewModelScope.launch { repository.addMedicine(medicine) }
    }

    fun updateMedicine(medicine: Medicine) {
        viewModelScope.launch { repository.updateMedicine(medicine) }
    }

    fun deleteMedicine(medicine: Medicine) {
        viewModelScope.launch { repository.deleteMedicine(medicine) }
    }

    fun transferStock(medicineId: Long, targetPharmacyId: String, qty: Int) {
        viewModelScope.launch { repository.transferStock(medicineId, targetPharmacyId, qty) }
    }

    fun addPatient(patient: Patient) {
        viewModelScope.launch { repository.addPatient(patient) }
    }

    fun addEmployee(employee: Employee) {
        viewModelScope.launch { repository.addEmployee(employee) }
    }

    fun updateEmployeeAttendance(employee: Employee, status: String) {
        viewModelScope.launch {
            repository.updateEmployee(employee.copy(attendanceStatus = status))
        }
    }

    fun addAppointment(appointment: Appointment) {
        viewModelScope.launch { repository.addAppointment(appointment) }
    }

    fun updateAppointmentStatus(appointmentId: Long, newStatus: String) {
        viewModelScope.launch { repository.updateAppointmentStatus(appointmentId, newStatus) }
    }

    fun addFinancialRecord(record: FinancialRecord) {
        viewModelScope.launch { repository.addFinancialRecord(record) }
    }

    fun resetFinancialIndicators(performedBy: String = "أ.د. محي الدين الجعفري (مدير المركز)") {
        viewModelScope.launch { repository.resetFinancialRecords(performedBy) }
    }

    fun addMedicalHistoryRecord(record: MedicalHistoryRecord) {
        viewModelScope.launch { repository.addMedicalHistoryRecord(record) }
    }

    fun addMedicationLog(log: MedicationLog) {
        viewModelScope.launch { repository.addMedicationLog(log) }
    }

    fun updateMedicationLogStatus(logId: Long, newStatus: String, loggedTime: String) {
        viewModelScope.launch { repository.updateMedicationLogStatus(logId, newStatus, loggedTime) }
    }
}
