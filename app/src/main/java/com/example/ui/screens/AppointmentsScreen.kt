package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusInfo
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(
    appointments: List<Appointment>,
    onAddAppointment: (Appointment) -> Unit,
    onUpdateStatus: (Long, String) -> Unit
) {
    var selectedStatusFilter by remember { mutableStateOf("ALL") } // ALL, WAIT, IN_PROGRESS, COMPLETED
    var showAddDialog by remember { mutableStateOf(false) }

    val filtered = appointments.filter { appt ->
        when (selectedStatusFilter) {
            "WAIT" -> appt.status == "انتظار"
            "IN_PROGRESS" -> appt.status == "جاري"
            "COMPLETED" -> appt.status == "مكتمل"
            else -> true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "جدول المواعيد والانتظار اليومي",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "مواعيد الأطباء، الأشعة، المختبر والعمليات",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(onClick = { showAddDialog = true }) {
                Icon(imageVector = Icons.Default.Event, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("حجز موعد", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Status Filter Chips
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = selectedStatusFilter == "ALL", onClick = { selectedStatusFilter = "ALL" }, label = { Text("الكل (${appointments.size})") })
            FilterChip(selected = selectedStatusFilter == "WAIT", onClick = { selectedStatusFilter = "WAIT" }, label = { Text("في الانتظار") })
            FilterChip(selected = selectedStatusFilter == "IN_PROGRESS", onClick = { selectedStatusFilter = "IN_PROGRESS" }, label = { Text("جاري المعاينة") })
            FilterChip(selected = selectedStatusFilter == "COMPLETED", onClick = { selectedStatusFilter = "COMPLETED" }, label = { Text("مكتمل") })
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filtered, key = { it.id }) { appt ->
                val statusColor = when (appt.status) {
                    "انتظار" -> StatusWarning
                    "جاري" -> StatusInfo
                    "مكتمل" -> StatusSuccess
                    else -> StatusError
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = appt.patientName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "مع: ${appt.doctorName} (${appt.department})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Surface(shape = RoundedCornerShape(6.dp), color = statusColor.copy(alpha = 0.15f)) {
                                Text(text = appt.status, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "الوقت: ${appt.appointmentTime} • النوع: ${appt.type}", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (appt.status == "انتظار") {
                                    Button(
                                        onClick = { onUpdateStatus(appt.id, "جاري") },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("بدء المعاينة", fontSize = 10.sp)
                                    }
                                } else if (appt.status == "جاري") {
                                    Button(
                                        onClick = { onUpdateStatus(appt.id, "مكتمل") },
                                        colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("إنهاء الزيارة", fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddAppointmentModal(
            onDismiss = { showAddDialog = false },
            onSave = { a ->
                onAddAppointment(a)
                showAddDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppointmentModal(
    onDismiss: () -> Unit,
    onSave: (Appointment) -> Unit
) {
    var patientName by remember { mutableStateOf("") }
    var doctorName by remember { mutableStateOf("د. خالد السعيد") }
    var department by remember { mutableStateOf("الباطنية") }
    var time by remember { mutableStateOf("11:30 ص") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text("حجز موعد طبي جديد", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(value = patientName, onValueChange = { patientName = it }, label = { Text("اسم المريض") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = doctorName, onValueChange = { doctorName = it }, label = { Text("الطبيب المعالج") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = department, onValueChange = { department = it }, label = { Text("القسم الطبي") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("وقت الموعد") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (patientName.isNotBlank()) {
                        onSave(
                            Appointment(
                                patientName = patientName,
                                patientPhone = "+966 50 000 0000",
                                doctorName = doctorName,
                                department = department,
                                appointmentDate = "2026-07-22",
                                appointmentTime = time,
                                status = "انتظار",
                                type = "طبيب"
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("تأكيد الحجز")
            }
        }
    }
}
