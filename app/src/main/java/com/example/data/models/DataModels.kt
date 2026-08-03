package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole(val arabicName: String, val iconName: String) {
    SUPER_ADMIN("مدير النظام الأعلى (Super Admin)", "Shield"),
    OWNER("المالك (Owner)", "Business"),
    MANAGER("مدير المركز (Manager)", "AdminPanelSettings"),
    DOCTOR("طبيب (Doctor)", "LocalHospital"),
    PHARMACIST("صيدلي (Pharmacist)", "Medication"),
    NURSE("ممرض (Nurse)", "MedicalServices"),
    LAB_TECH("فني مختبر (Lab Tech)", "Science"),
    RADIOLOGY_TECH("فني أشعة (Radiology Tech)", "Camera"),
    ACCOUNTANT("محاسب (Accountant)", "AccountBalanceWallet"),
    RECEPTIONIST("موظف استقبال (Receptionist)", "PersonAdd"),
    EMPLOYEE("موظف (Employee)", "Badge")
}

@Entity(tableName = "pharmacies")
data class Pharmacy(
    @PrimaryKey val id: String, // "pharmacy_1", "pharmacy_2", "pharmacy_3", "pharmacy_4", "central_warehouse"
    val name: String,
    val salesToday: Double,
    val salesMonth: Double,
    val purchasesMonth: Double,
    val profitMonth: Double,
    val totalInventoryItems: Int,
    val lowStockCount: Int,
    val staffCount: Int,
    val txCountToday: Int,
    val avgTicket: Double
)

@Entity(tableName = "medicines")
data class Medicine(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tradeName: String,
    val genericName: String,
    val concentration: String,
    val dosageForm: String, // أقراص، شراب، حقن، مرهم...
    val company: String,
    val stockQuantity: Int,
    val costPrice: Double,
    val sellingPrice: Double,
    val expiryDate: String, // YYYY-MM-DD
    val batchNumber: String,
    val supplier: String,
    val storageLocation: String, // رف A1، مستودع B...
    val pharmacyId: String, // pharmacy_1..4 or central_warehouse
    val minStockAlert: Int = 10
)

@Entity(tableName = "employees")
data class Employee(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val jobTitle: String,
    val role: UserRole,
    val department: String,
    val branchOrPharmacy: String,
    val phone: String,
    val hireDate: String,
    val monthlySalary: Double,
    val attendanceStatus: String, // "حاضر", "غائب", "إجازة"
    val clockInTime: String = "08:00 ص"
)

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fileNumber: String,
    val name: String,
    val phone: String,
    val age: Int,
    val gender: String, // "ذكر", "أنثى"
    val nationalId: String,
    val bloodType: String,
    val allergies: String = "لا يوجد",
    val medicalHistory: String = "لا يوجد"
)

@Entity(tableName = "patient_profiles")
data class PatientProfile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val email: String = "",
    val contactDetails: String = "",
    val medicalHistory: String = "",
    val age: Int = 0,
    val gender: String = "",
    val bloodType: String = "",
    val allergies: String = "لا يوجد",
    val fileNumber: String = ""
)

@Entity(tableName = "medical_history_records")
data class MedicalHistoryRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientId: Long = 0,
    val patientName: String,
    val date: String,
    val doctorName: String,
    val department: String,
    val diagnosisText: String,
    val prescription: String = "",
    val notes: String = "",
    val status: String = "مكتمل" // "مكتمل", "تحت المتابعة", "مزمن"
)

@Entity(tableName = "medication_logs")
data class MedicationLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientId: Long = 0,
    val patientName: String,
    val medicineName: String,
    val dosage: String,
    val scheduledTime: String,
    val status: String, // "تم التناول", "مجدول", "تم التخطي"
    val loggedTime: String = "",
    val date: String = ""
)

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientName: String,
    val patientPhone: String,
    val doctorName: String,
    val department: String,
    val appointmentDate: String,
    val appointmentTime: String,
    val status: String, // "انتظار", "جاري", "مكتمل", "ملغي"
    val type: String // "طبيب", "أشعة", "مختبر", "عملية"
)

@Entity(tableName = "financial_records")
data class FinancialRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "إيراد", "مصروف", "مشتريات", "راتب"
    val title: String,
    val amount: Double,
    val category: String,
    val locationOrDept: String,
    val date: String,
    val notes: String = ""
)

@Entity(tableName = "surgeries")
data class Surgery(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientName: String,
    val surgeonName: String,
    val surgeryType: String,
    val date: String,
    val time: String,
    val operatingRoom: String,
    val medicalTeam: String,
    val status: String // "مجدولة", "جاري التنفيذ", "مكتملة", "ملغاة"
)

@Entity(tableName = "lab_tests")
data class LabTest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientName: String,
    val doctorName: String,
    val testType: String,
    val date: String,
    val status: String, // "طلب جديد", "جاري الفحص", "مكتمل"
    val resultSummary: String = "في الانتظار"
)

@Entity(tableName = "radiology_scans")
data class RadiologyScan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientName: String,
    val doctorName: String,
    val scanType: String,
    val date: String,
    val status: String, // "حجز", "تم الفحص", "التقرير جاهز"
    val reportText: String = "قيد الإعداد"
)

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val action: String,
    val performedBy: String,
    val details: String,
    val timestamp: String,
    val category: String = "مالية وحسابات",
    val severity: String = "تحذير أمني/مالي"
)

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val severity: String, // "عاجل", "مهم", "تنبيه", "معلومات"
    val timestamp: String,
    val isRead: Boolean = false
)

data class MedicalDiagnosis(
    val date: String,
    val doctorName: String,
    val department: String,
    val diagnosisText: String,
    val prescription: String,
    val notes: String
)
