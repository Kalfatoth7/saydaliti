package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Appointment
import com.example.data.models.Patient
import com.example.ui.theme.AlRahmaGoldAccent
import com.example.ui.theme.AlRahmaPrimary
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceptionQueueScreen(
    patients: List<Patient>,
    appointments: List<Appointment>,
    onAddPatient: (Patient) -> Unit,
    onAddAppointment: (Appointment) -> Unit,
    onUpdateAppointmentStatus: (Long, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Fast Queue (طابور الانتظار), 1: All Appointments, 2: Reception Register
    var showQuickRegisterDialog by remember { mutableStateOf(false) }

    val filteredAppointments = appointments.filter {
        it.patientName.contains(searchQuery, ignoreCase = true) ||
                it.doctorName.contains(searchQuery, ignoreCase = true) ||
                it.department.contains(searchQuery, ignoreCase = true)
    }

    val waitingAppointments = filteredAppointments.filter { it.status == "انتظار" }
    val inProgressAppointments = filteredAppointments.filter { it.status == "جاري" }
    val completedAppointments = filteredAppointments.filter { it.status == "مكتمل" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "نظام الاستقبال والطوابير المباشرة",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "إدارة شاشات الانتظار، استدعاء المرضى وتحويلهم للعيادات",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(
                onClick = { showQuickRegisterDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = AlRahmaPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("تسجيل وحجز سريع", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("بحث عن مريض، طبيب، أو قسم...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("طابور الانتظار (${waitingAppointments.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("العيادات والجلسات (${inProgressAppointments.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("سجل اليوم الكامل (${filteredAppointments.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
            0 -> {
                // Queue List
                if (waitingAppointments.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(48.dp), tint = StatusSuccess)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("لا يوجد مرضى بانتظار العيادات حالياً", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(waitingAppointments) { appt ->
                            QueuePatientCard(
                                appointment = appt,
                                onCallPatient = { onUpdateAppointmentStatus(appt.id, "جاري") },
                                onCancel = { onUpdateAppointmentStatus(appt.id, "ملغي") }
                            )
                        }
                    }
                }
            }
            1 -> {
                // In Progress Clinic Patients
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(inProgressAppointments) { appt ->
                        InProgressPatientCard(
                            appointment = appt,
                            onComplete = { onUpdateAppointmentStatus(appt.id, "مكتمل") }
                        )
                    }
                }
            }
            2 -> {
                // Full Day
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filteredAppointments) { appt ->
                        AppointmentFullRowItem(appointment = appt)
                    }
                }
            }
        }
    }

    if (showQuickRegisterDialog) {
        QuickRegisterPatientModal(
            onDismiss = { showQuickRegisterDialog = false },
            onSave = { pName, docName, dept, type ->
                // Register appointment
                val newAppt = Appointment(
                    patientName = pName,
                    patientPhone = "01000000000",
                    doctorName = docName,
                    department = dept,
                    appointmentDate = "2026-07-28",
                    appointmentTime = "الآن",
                    status = "انتظار",
                    type = type
                )
                onAddAppointment(newAppt)
                showQuickRegisterDialog = false
            }
        )
    }
}

@Composable
fun QueuePatientCard(
    appointment: Appointment,
    onCallPatient: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = StatusWarning.copy(alpha = 0.15f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "#${appointment.id}",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = StatusWarning
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = appointment.patientName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    text = "${appointment.department} • د. ${appointment.doctorName}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "وقت الموعد: ${appointment.appointmentTime} • نوع الخدمة: ${appointment.type}",
                    fontSize = 10.sp,
                    color = AlRahmaPrimary
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Button(
                    onClick = onCallPatient,
                    colors = ButtonDefaults.buttonColors(containerColor = AlRahmaPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("استدعاء الآن", fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                TextButton(
                    onClick = onCancel,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.height(24.dp)
                ) {
                    Text("إلغاء", fontSize = 10.sp, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun InProgressPatientCard(
    appointment: Appointment,
    onComplete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = StatusSuccess,
                modifier = Modifier.size(12.dp)
            ) {}
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = appointment.patientName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    text = "داخل العيادة الان مع د. ${appointment.doctorName} (${appointment.department})",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Button(
                onClick = onComplete,
                colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("إنهاء الكشف", fontSize = 11.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun AppointmentFullRowItem(appointment: Appointment) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = appointment.patientName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = "${appointment.department} - د. ${appointment.doctorName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = when (appointment.status) {
                    "مكتمل" -> StatusSuccess.copy(alpha = 0.15f)
                    "جاري" -> AlRahmaPrimary.copy(alpha = 0.15f)
                    "انتظار" -> StatusWarning.copy(alpha = 0.15f)
                    else -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                }
            ) {
                Text(
                    text = appointment.status,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (appointment.status) {
                        "مكتمل" -> StatusSuccess
                        "جاري" -> AlRahmaPrimary
                        "انتظار" -> StatusWarning
                        else -> MaterialTheme.colorScheme.error
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun QuickRegisterPatientModal(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var patientName by remember { mutableStateOf("") }
    var doctorName by remember { mutableStateOf("أحمد محمود (أطفال)") }
    var dept by remember { mutableStateOf("عيادة الأطفال") }
    var serviceType by remember { mutableStateOf("كشف طبي") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تسجيل مريض وحجز موعد سريع", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = patientName,
                    onValueChange = { patientName = it },
                    label = { Text("اسم المريض بالكامل") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dept,
                    onValueChange = { dept = it },
                    label = { Text("العيادة / القسم") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = doctorName,
                    onValueChange = { doctorName = it },
                    label = { Text("اسم الطبيب المعالج") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = serviceType,
                    onValueChange = { serviceType = it },
                    label = { Text("نوع الخدمة (كشف، استشارة، فحص)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (patientName.isNotBlank()) {
                        onSave(patientName, doctorName, dept, serviceType)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AlRahmaPrimary)
            ) {
                Text("تأكيد ودخول الطابور")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}
