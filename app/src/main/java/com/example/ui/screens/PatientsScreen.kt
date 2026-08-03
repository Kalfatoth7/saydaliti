package com.example.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Patient
import com.example.ui.theme.MedicalTealContainer
import com.example.ui.theme.MedicalTealOnContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientsScreen(
    patients: List<Patient>,
    onAddPatient: (Patient) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedPatientForEmr by remember { mutableStateOf<Patient?>(null) }
    var showAddPatientModal by remember { mutableStateOf(false) }

    val filteredPatients = patients.filter { pat ->
        pat.name.contains(searchQuery, ignoreCase = true) ||
                pat.fileNumber.contains(searchQuery, ignoreCase = true) ||
                pat.phone.contains(searchQuery, ignoreCase = true)
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
                    text = "سجل المرضى والملف الطبي الموحد (EMR)",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "ملف طبي إلكتروني موحد يربط العيادات، الوصفات، المختبر والأشعة",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(onClick = { showAddPatientModal = true }) {
                Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("تسجيل مريض", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("بحث برقم الملف، اسم المريض أو رقم الجوال...") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredPatients, key = { it.id }) { patient ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedPatientForEmr = patient },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = MedicalTealContainer,
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = MedicalTealOnContainer)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = patient.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                                        Text(text = patient.fileNumber, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "العمر: ${patient.age} سنة • ${patient.gender} • فصيلة الدم: ${patient.bloodType}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(onClick = { selectedPatientForEmr = patient }) {
                            Icon(imageVector = Icons.Default.FolderShared, contentDescription = "الملف الطبي الموحد", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }

    // Unified EMR Modal
    selectedPatientForEmr?.let { patient ->
        UnifiedEmrModal(
            patient = patient,
            onDismiss = { selectedPatientForEmr = null }
        )
    }

    if (showAddPatientModal) {
        AddPatientModal(
            onDismiss = { showAddPatientModal = false },
            onSave = { p ->
                onAddPatient(p)
                showAddPatientModal = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedEmrModal(
    patient: Patient,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.9f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.FolderShared, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "الملف الطبي الموحد: ${patient.name}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = "رقم الملف: ${patient.fileNumber} • الجوال: ${patient.phone}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    EmrSectionCard(title = "📋 البيانات الأساسية والتنبيات الطبية") {
                        Text(text = "• الهوية الوطنية: ${patient.nationalId}", fontSize = 12.sp)
                        Text(text = "• فصيلة الدم: ${patient.bloodType}", fontSize = 12.sp)
                        Text(text = "• الحساسية المسجلة: ${patient.allergies}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    }
                }

                item {
                    EmrSectionCard(title = "🩺 آخر التشخيصات والزيارات الطبية") {
                        Text(text = "2026-07-22 - د. خالد السعيد (الباطنية)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(text = "التشخيص: ارتفاع ضغط الدم الخفيف، مع صداع إجهادي.", fontSize = 12.sp)
                        Text(text = "الوصفة الطبية: كونكور 5 ملجم (قرص صباحاً)، بنادول إكسترا عند الحاجة.", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }

                item {
                    EmrSectionCard(title = "🧪 نتائج المختبر والتحاليل") {
                        Text(text = "تحليل CBC + السكر التراكمي (مكتمل)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(text = "النتيجة: الهيموجلوبين 13.2 g/dL | السكر التراكمي 5.8%", fontSize = 12.sp)
                    }
                }

                item {
                    EmrSectionCard(title = "🩻 فحص الأشعة والتقارير") {
                        Text(text = "أشعة سينية للصدر (Chest X-Ray) - د. عمر الحربي", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(text = "التقرير: نتائج سليمة ولا توجد ارتشاحات رئوية.", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun EmrSectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPatientModal(
    onDismiss: () -> Unit,
    onSave: (Patient) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+966 55 ") }
    var age by remember { mutableStateOf("35") }
    var gender by remember { mutableStateOf("ذكر") }
    var bloodType by remember { mutableStateOf("O+") }
    var allergies by remember { mutableStateOf("لا يوجد") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text("فتح ملف مريض جديد", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("اسم المريض الرباعي") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("رقم الجوال") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("العمر") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = bloodType, onValueChange = { bloodType = it }, label = { Text("فصيلة الدم") }, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = allergies, onValueChange = { allergies = it }, label = { Text("الحساسية إن وجدت") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val newPat = Patient(
                            fileNumber = "P-${(1000..9999).random()}",
                            name = name,
                            phone = phone,
                            age = age.toIntOrNull() ?: 30,
                            gender = gender,
                            nationalId = "1090${(100000..999999).random()}",
                            bloodType = bloodType,
                            allergies = allergies
                        )
                        onSave(newPat)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("حفظ الملف الطبي")
            }
        }
    }
}
