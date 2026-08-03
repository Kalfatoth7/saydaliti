package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Appointment
import com.example.data.models.MedicalHistoryRecord
import com.example.data.models.MedicationLog
import com.example.data.models.Patient
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PatientDashboardScreen(
    patients: List<Patient>,
    medicalHistoryRecords: List<MedicalHistoryRecord>,
    appointments: List<Appointment>,
    medicationLogs: List<MedicationLog>,
    onAddMedicalHistoryRecord: (MedicalHistoryRecord) -> Unit,
    onAddMedicationLog: (MedicationLog) -> Unit,
    onUpdateMedicationStatus: (Long, String, String) -> Unit,
    onAddAppointment: (Appointment) -> Unit
) {
    PatientDashboard(
        patients = patients,
        medicalHistoryRecords = medicalHistoryRecords,
        appointments = appointments,
        medicationLogs = medicationLogs,
        onAddMedicalHistoryRecord = onAddMedicalHistoryRecord,
        onAddMedicationLog = onAddMedicationLog,
        onUpdateMedicationStatus = onUpdateMedicationStatus,
        onAddAppointment = onAddAppointment
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDashboard(
    patients: List<Patient>,
    medicalHistoryRecords: List<MedicalHistoryRecord>,
    appointments: List<Appointment>,
    medicationLogs: List<MedicationLog>,
    onAddMedicalHistoryRecord: (MedicalHistoryRecord) -> Unit,
    onAddMedicationLog: (MedicationLog) -> Unit,
    onUpdateMedicationStatus: (Long, String, String) -> Unit,
    onAddAppointment: (Appointment) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPatientId by remember { mutableStateOf<Long?>(patients.firstOrNull()?.id) }
    var activeTab by remember { mutableStateOf(0) } // 0: History, 1: Appointments, 2: Medication Logs
    var searchQuery by remember { mutableStateOf("") }

    var showAddHistoryModal by remember { mutableStateOf(false) }
    var showAddMedicationModal by remember { mutableStateOf(false) }
    var showAddApptModal by remember { mutableStateOf(false) }

    val selectedPatient = patients.find { it.id == selectedPatientId } ?: patients.firstOrNull()

    // Filtered data for selected patient
    val patientHistory = remember(selectedPatient, medicalHistoryRecords, searchQuery) {
        val list = if (selectedPatient != null) {
            medicalHistoryRecords.filter { it.patientId == selectedPatient.id || it.patientName == selectedPatient.name }
        } else {
            medicalHistoryRecords
        }
        if (searchQuery.isNotBlank()) {
            list.filter {
                it.diagnosisText.contains(searchQuery, ignoreCase = true) ||
                it.doctorName.contains(searchQuery, ignoreCase = true) ||
                it.department.contains(searchQuery, ignoreCase = true)
            }
        } else list
    }

    val patientAppointments = remember(selectedPatient, appointments) {
        if (selectedPatient != null) {
            appointments.filter { it.patientName == selectedPatient.name }
        } else {
            appointments
        }
    }

    val patientMedLogs = remember(selectedPatient, medicationLogs) {
        if (selectedPatient != null) {
            medicationLogs.filter { it.patientId == selectedPatient.id || it.patientName == selectedPatient.name }
        } else {
            medicationLogs
        }
    }

    val totalDoses = patientMedLogs.size
    val takenDoses = patientMedLogs.count { it.status == "تم التناول" }
    val adherenceRate = if (totalDoses > 0) (takenDoses.toFloat() / totalDoses * 100).toInt() else 100

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("patient_dashboard_container"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Title Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.MedicalServices,
                                    contentDescription = "لوحة المريض",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "لوحة تحكم وتتبع المريض (Patient Dashboard)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "السجل الطبي الموحد، المواعيد القادمة، وتتبع الجرعات الدوائية بالحفظ المحلي (Room DB)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        // 2. Patient Selector Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "اختر المريض لعرض الملف التفصيلي:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(patients) { p ->
                        val isSelected = p.id == selectedPatientId
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedPatientId = p.id },
                            label = {
                                Text(
                                    text = "${p.name} (${p.fileNumber})",
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("patient_chip_${p.id}")
                        )
                    }
                }
            }
        }

        // 3. Selected Patient Overview Card
        if (selectedPatient != null) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.size(54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = selectedPatient.name.take(1),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = selectedPatient.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.tertiaryContainer
                                    ) {
                                        Text(
                                            text = selectedPatient.fileNumber,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "العمر: ${selectedPatient.age} سنة",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(text = "•", color = MaterialTheme.colorScheme.outline)
                                    Text(
                                        text = "فصيلة الدم: ${selectedPatient.bloodType}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Text(text = "•", color = MaterialTheme.colorScheme.outline)
                                    Text(
                                        text = selectedPatient.phone,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        // Stats Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            PatientStatCard(
                                title = "السجلات الطبية",
                                value = "${patientHistory.size}",
                                icon = Icons.Default.Folder,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.weight(1f)
                            )

                            PatientStatCard(
                                title = "المواعيد القادمة",
                                value = "${patientAppointments.size}",
                                icon = Icons.Default.Event,
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.weight(1f)
                            )

                            PatientStatCard(
                                title = "نسبة التزام الدواء",
                                value = "$adherenceRate%",
                                icon = Icons.Default.CheckCircle,
                                containerColor = if (adherenceRate >= 80) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (selectedPatient.allergies.isNotBlank() && selectedPatient.allergies != "لا يوجد") {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.errorContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "الحساسية المسجلة: ${selectedPatient.allergies}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Tab Navigation (History, Appointments, Medication Logs)
        item {
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = {
                        Text(
                            text = "السجل الطبي (${patientHistory.size})",
                            fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    },
                    icon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("tab_medical_history")
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = {
                        Text(
                            text = "المواعيد القادمة (${patientAppointments.size})",
                            fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("tab_appointments")
                )
                Tab(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    text = {
                        Text(
                            text = "جدول الأدوية (${patientMedLogs.size})",
                            fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    },
                    icon = { Icon(Icons.Default.Medication, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("tab_medication_logs")
                )
                Tab(
                    selected = activeTab == 3,
                    onClick = { activeTab = 3 },
                    text = {
                        Text(
                            text = "المؤشرات الحيوية",
                            fontWeight = if (activeTab == 3) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    },
                    icon = { Icon(Icons.Default.ShowChart, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("tab_health_trends")
                )
            }
        }

        // TAB 0: Medical History
        if (activeTab == 0) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "السجل والتشخيص الطبي",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = { showAddHistoryModal = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("add_history_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "إضافة تشخيص", fontSize = 12.sp)
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("بحث في التشخيص أو اسم الطبيب أو القسم...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "مسح")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_history_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            if (patientHistory.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = "لا توجد سجلات طبية مسجلة",
                        subtitle = "انقر فوق 'إضافة تشخيص' لتسجيل زيارة طبية جديدة للمريض.",
                        icon = Icons.Default.FolderOpen
                    )
                }
            } else {
                items(patientHistory) { record ->
                    MedicalHistoryCard(record = record)
                }
            }
        }

        // TAB 1: Upcoming Appointments
        if (activeTab == 1) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "المواعيد والعيادات",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = { showAddApptModal = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("schedule_appt_btn")
                    ) {
                        Icon(Icons.Default.EventAvailable, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "حجز موعد", fontSize = 12.sp)
                    }
                }
            }

            if (patientAppointments.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = "لا توجد مواعيد قادمة",
                        subtitle = "يمكنك حجز موعد عيادة جديدة بالضغط على 'حجز موعد'.",
                        icon = Icons.Default.EventBusy
                    )
                }
            } else {
                items(patientAppointments) { appt ->
                    AppointmentCard(appointment = appt)
                }
            }
        }

        // TAB 2: Medication Logs & Adherence Tracker
        if (activeTab == 2) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "تتبع وسجل تناول الأدوية اليومي",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = { showAddMedicationModal = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("add_medication_btn")
                    ) {
                        Icon(Icons.Default.AddCircleOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "إضافة جرعة دواء", fontSize = 12.sp)
                    }
                }
            }

            // Progress Bar
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "معدل الالتزام بالدواء اليوم:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$takenDoses من $totalDoses جرعات ($adherenceRate%)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { if (totalDoses > 0) takenDoses.toFloat() / totalDoses else 1.0f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surface
                        )
                    }
                }
            }

            if (patientMedLogs.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = "لا توجد جرعات دواء مسجلة",
                        subtitle = "انقر على 'إضافة جرعة دواء' لتنظيم وتتبع أدوية المريض.",
                        icon = Icons.Default.MedicalInformation
                    )
                }
            } else {
                items(patientMedLogs) { log ->
                    MedicationLogCard(
                        log = log,
                        onToggleStatus = { newStatus ->
                            val now = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                            onUpdateMedicationStatus(log.id, newStatus, if (newStatus == "تم التناول") now else "")
                        }
                    )
                }
            }
        }

        // TAB 3: Health Trends Dashboard
        if (activeTab == 3) {
            item {
                Text(
                    text = "مؤشرات المريض الحيوية",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
                )
            }
            item {
                Box(modifier = Modifier.fillMaxWidth().height(600.dp), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("لوحة مؤشرات المريض قيد التطوير", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }

    // Modal: Add Medical History Record
    if (showAddHistoryModal) {
        AddMedicalHistoryModal(
            patient = selectedPatient,
            onDismiss = { showAddHistoryModal = false },
            onSave = { record ->
                onAddMedicalHistoryRecord(record)
                showAddHistoryModal = false
            }
        )
    }

    // Modal: Add Medication Log
    if (showAddMedicationModal) {
        AddMedicationLogModal(
            patient = selectedPatient,
            onDismiss = { showAddMedicationModal = false },
            onSave = { log ->
                onAddMedicationLog(log)
                showAddMedicationModal = false
            }
        )
    }

    // Modal: Add Appointment
    if (showAddApptModal) {
        AddAppointmentForPatientModal(
            patient = selectedPatient,
            onDismiss = { showAddApptModal = false },
            onSave = { appt ->
                onAddAppointment(appt)
                showAddApptModal = false
            }
        )
    }
}

@Composable
fun PatientStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MedicalHistoryCard(record: MedicalHistoryRecord) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = record.department,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (record.status) {
                        "تحت المتابعة" -> Color(0xFFFFF3E0)
                        "مزمن" -> Color(0xFFEDE7F6)
                        else -> Color(0xFFE8F5E9)
                    }
                ) {
                    Text(
                        text = record.status,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (record.status) {
                            "تحت المتابعة" -> Color(0xFFE65100)
                            "مزمن" -> Color(0xFF512DA8)
                            else -> Color(0xFF2E7D32)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = record.diagnosisText,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (record.prescription.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Medication,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "الوصفة الدوائية: ${record.prescription}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (record.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notes,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "الملاحظات: ${record.notes}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "الطبيب المعالج: ${record.doctorName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = record.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AppointmentCard(appointment: Appointment) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(50.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appointment.department,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "مع: ${appointment.doctorName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.outline)
                    Text(
                        text = "${appointment.appointmentDate} - ${appointment.appointmentTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = when (appointment.status) {
                    "انتظار" -> Color(0xFFFFF8E1)
                    "جاري" -> Color(0xFFE3F2FD)
                    "مكتمل" -> Color(0xFFE8F5E9)
                    else -> Color(0xFFFFEBEE)
                }
            ) {
                Text(
                    text = appointment.status,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = when (appointment.status) {
                        "انتظار" -> Color(0xFFF57F17)
                        "جاري" -> Color(0xFF1565C0)
                        "مكتمل" -> Color(0xFF2E7D32)
                        else -> Color(0xFFC62828)
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun MedicationLogCard(
    log: MedicationLog,
    onToggleStatus: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (log.status == "تم التناول") Color(0xFFF1F8E9) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (log.status == "تم التناول") Color(0xFFC8E6C9) else MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (log.status == "تم التناول") Icons.Default.CheckCircle else Icons.Default.Medication,
                        contentDescription = null,
                        tint = if (log.status == "تم التناول") Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.medicineName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "الجرعة: ${log.dosage} • الموعد: ${log.scheduledTime}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (log.loggedTime.isNotBlank()) {
                    Text(
                        text = "تم التناول الساعة: ${log.loggedTime}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                if (log.status != "تم التناول") {
                    Button(
                        onClick = { onToggleStatus("تم التناول") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("mark_taken_btn_${log.id}")
                    ) {
                        Text(text = "تناول الجرعة", fontSize = 11.sp)
                    }
                } else {
                    OutlinedButton(
                        onClick = { onToggleStatus("مجدول") },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "تراجع", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(44.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

// Dialog: Add Medical History Modal
@Composable
fun AddMedicalHistoryModal(
    patient: Patient?,
    onDismiss: () -> Unit,
    onSave: (MedicalHistoryRecord) -> Unit
) {
    var doctorName by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("العيادة العامة") }
    var diagnosisText by remember { mutableStateOf("") }
    var prescription by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("تحت المتابعة") }

    val dateNow = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "إضافة تشخيص وسجل طبي للمريض", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "المريض: ${patient?.name ?: "غير محدد"}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = diagnosisText,
                    onValueChange = { diagnosisText = it },
                    label = { Text("التشخيص والحالة الطبية *") },
                    modifier = Modifier.fillMaxWidth().testTag("input_diagnosis")
                )

                OutlinedTextField(
                    value = doctorName,
                    onValueChange = { doctorName = it },
                    label = { Text("اسم الطبيب المعالج *") },
                    modifier = Modifier.fillMaxWidth().testTag("input_doctor")
                )

                OutlinedTextField(
                    value = department,
                    onValueChange = { department = it },
                    label = { Text("القسم أو العيادة") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = prescription,
                    onValueChange = { prescription = it },
                    label = { Text("الوصفة الدوائية (إن وجدت)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات وتوصيات إضافية") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (diagnosisText.isNotBlank() && doctorName.isNotBlank() && patient != null) {
                        onSave(
                            MedicalHistoryRecord(
                                patientId = patient.id,
                                patientName = patient.name,
                                date = dateNow,
                                doctorName = doctorName,
                                department = department,
                                diagnosisText = diagnosisText,
                                prescription = prescription,
                                notes = notes,
                                status = status
                            )
                        )
                    }
                },
                enabled = diagnosisText.isNotBlank() && doctorName.isNotBlank() && patient != null,
                modifier = Modifier.testTag("save_history_btn")
            ) {
                Text("حفظ السجل")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

// Dialog: Add Medication Log Modal
@Composable
fun AddMedicationLogModal(
    patient: Patient?,
    onDismiss: () -> Unit,
    onSave: (MedicationLog) -> Unit
) {
    var medicineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("1 قرص") }
    var scheduledTime by remember { mutableStateOf("09:00 ص") }
    val dateNow = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "إضافة دواء وجرعة جديدة للمريض", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "المريض: ${patient?.name ?: "غير محدد"}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = medicineName,
                    onValueChange = { medicineName = it },
                    label = { Text("اسم الدواء والعيار *") },
                    placeholder = { Text("مثال: بنادول إكسترا 500mg") },
                    modifier = Modifier.fillMaxWidth().testTag("input_medicine_name")
                )

                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text("الجرعة (مثال: 1 قرص، 5ml)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = scheduledTime,
                    onValueChange = { scheduledTime = it },
                    label = { Text("الموعد (مثال: 08:00 ص، 09:00 م)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (medicineName.isNotBlank() && patient != null) {
                        onSave(
                            MedicationLog(
                                patientId = patient.id,
                                patientName = patient.name,
                                medicineName = medicineName,
                                dosage = dosage,
                                scheduledTime = scheduledTime,
                                status = "مجدول",
                                date = dateNow
                            )
                        )
                    }
                },
                enabled = medicineName.isNotBlank() && patient != null,
                modifier = Modifier.testTag("save_med_log_btn")
            ) {
                Text("حفظ الدواء")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

// Dialog: Add Appointment For Patient Modal
@Composable
fun AddAppointmentForPatientModal(
    patient: Patient?,
    onDismiss: () -> Unit,
    onSave: (Appointment) -> Unit
) {
    var doctorName by remember { mutableStateOf("د. خالد منصور") }
    var department by remember { mutableStateOf("الباطنية والقلب") }
    var time by remember { mutableStateOf("10:30 ص") }
    val dateNow = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "حجز موعد جديد للمريض", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "المريض: ${patient?.name ?: "غير محدد"}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = department,
                    onValueChange = { department = it },
                    label = { Text("القسم / العيادة *") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = doctorName,
                    onValueChange = { doctorName = it },
                    label = { Text("الطبيب المعالج *") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("وقت الموعد *") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (patient != null && doctorName.isNotBlank() && department.isNotBlank()) {
                        onSave(
                            Appointment(
                                patientName = patient.name,
                                patientPhone = patient.phone,
                                doctorName = doctorName,
                                department = department,
                                appointmentDate = dateNow,
                                appointmentTime = time,
                                status = "انتظار",
                                type = "طبيب"
                            )
                        )
                    }
                },
                enabled = patient != null && doctorName.isNotBlank() && department.isNotBlank(),
                modifier = Modifier.testTag("save_appt_btn")
            ) {
                Text("حفظ الموعد")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
