package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Employee
import com.example.data.models.UserRole
import com.example.ui.theme.MedicalTealContainer
import com.example.ui.theme.MedicalTealOnContainer
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeesScreen(
    employees: List<Employee>,
    onAddEmployee: (Employee) -> Unit,
    onUpdateAttendance: (Employee, String) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, PRESENT, ABSENT, LEAVE
    var searchQuery by remember { mutableStateOf("") }
    var showAddModal by remember { mutableStateOf(false) }

    val presentCount = employees.count { it.attendanceStatus == "حاضر" }
    val absentCount = employees.count { it.attendanceStatus == "غائب" }
    val leaveCount = employees.count { it.attendanceStatus == "إجازة" }

    val filteredEmployees = employees.filter { emp ->
        val matchesSearch = emp.name.contains(searchQuery, ignoreCase = true) ||
                emp.jobTitle.contains(searchQuery, ignoreCase = true) ||
                emp.department.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "PRESENT" -> emp.attendanceStatus == "حاضر"
            "ABSENT" -> emp.attendanceStatus == "غائب"
            "LEAVE" -> emp.attendanceStatus == "إجازة"
            else -> true
        }
        matchesSearch && matchesFilter
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "سجل وشؤون الموظفين",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "الأطباء، الصيادلة، الممرضون، الفنيون والإداريون",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(onClick = { showAddModal = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("إضافة موظف", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live Attendance Summary Cards
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            EmpStatMiniCard(
                title = "من يعمل الآن؟",
                count = "$presentCount موظف",
                color = StatusSuccess,
                onClick = { selectedFilter = "PRESENT" },
                isSelected = selectedFilter == "PRESENT",
                modifier = Modifier.weight(1f)
            )
            EmpStatMiniCard(
                title = "من غائب اليوم؟",
                count = "$absentCount موظف",
                color = StatusError,
                onClick = { selectedFilter = "ABSENT" },
                isSelected = selectedFilter == "ABSENT",
                modifier = Modifier.weight(1f)
            )
            EmpStatMiniCard(
                title = "في إجازة رسمية",
                count = "$leaveCount موظفين",
                color = StatusWarning,
                onClick = { selectedFilter = "LEAVE" },
                isSelected = selectedFilter == "LEAVE",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("بحث باسم الموظف، المسمى الوظيفي أو القسم...") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Employees List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredEmployees, key = { it.id }) { emp ->
                val statusColor = when (emp.attendanceStatus) {
                    "حاضر" -> StatusSuccess
                    "غائب" -> StatusError
                    else -> StatusWarning
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = MedicalTealContainer,
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = emp.name.take(2),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MedicalTealOnContainer
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = emp.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "${emp.jobTitle} • ${emp.department}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = statusColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = emp.attendanceStatus,
                                    color = statusColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "الفرع/الصيدلية: ${emp.branchOrPharmacy}", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                            Text(text = "وقت الحضور: ${emp.clockInTime}", fontSize = 11.sp, fontWeight = FontWeight.Bold)

                            // Quick Attendance Status Toggle
                            TextButton(
                                onClick = {
                                    val nextStatus = when (emp.attendanceStatus) {
                                        "حاضر" -> "غائب"
                                        "غائب" -> "إجازة"
                                        else -> "حاضر"
                                    }
                                    onUpdateAttendance(emp, nextStatus)
                                }
                            ) {
                                Text(text = "تعديل الحالة 🔄", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddModal) {
        AddEmployeeModal(
            onDismiss = { showAddModal = false },
            onSave = { emp ->
                onAddEmployee(emp)
                showAddModal = false
            }
        )
    }
}

@Composable
fun EmpStatMiniCard(
    title: String,
    count: String,
    color: Color,
    onClick: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = count, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEmployeeModal(
    onDismiss: () -> Unit,
    onSave: (Employee) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var jobTitle by remember { mutableStateOf("طبيب أخصائي") }
    var department by remember { mutableStateOf("الباطنية") }
    var branch by remember { mutableStateOf("المركز الرئيسي") }
    var phone by remember { mutableStateOf("+966 50 000 0000") }
    var salary by remember { mutableStateOf("15000") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text("إضافة موظف جديد لشبكة HEALTH OS", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("اسم الموظف الكامل") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = jobTitle, onValueChange = { jobTitle = it }, label = { Text("المسمى الوظيفي") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = department, onValueChange = { department = it }, label = { Text("القسم الطبي أو الإداري") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = salary, onValueChange = { salary = it }, label = { Text("الراتب الشهري (ر.س)") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(
                            Employee(
                                name = name,
                                jobTitle = jobTitle,
                                role = UserRole.EMPLOYEE,
                                department = department,
                                branchOrPharmacy = branch,
                                phone = phone,
                                hireDate = "2026-07-22",
                                monthlySalary = salary.toDoubleOrNull() ?: 10000.0,
                                attendanceStatus = "حاضر"
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("حفظ الموظف")
            }
        }
    }
}
