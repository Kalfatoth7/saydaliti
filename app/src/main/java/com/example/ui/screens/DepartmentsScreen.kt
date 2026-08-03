package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Appointment
import com.example.data.models.Employee
import com.example.ui.theme.MedicalTealContainer
import com.example.ui.theme.MedicalTealOnContainer

data class MedicalDepartmentInfo(
    val name: String,
    val icon: ImageVector,
    val doctorCount: Int,
    val todayAppointments: Int,
    val monthlyRevenue: Double
)

@Composable
fun DepartmentsScreen(
    employees: List<Employee>,
    appointments: List<Appointment>
) {
    val deptList = listOf(
        MedicalDepartmentInfo("الباطنية", Icons.Default.MedicalServices, employees.count { it.department == "الباطنية" }, appointments.count { it.department == "الباطنية" }, 45000.0),
        MedicalDepartmentInfo("الأطفال", Icons.Default.ChildCare, employees.count { it.department == "الأطفال" }, appointments.count { it.department == "الأطفال" }, 38000.0),
        MedicalDepartmentInfo("النساء والولادة", Icons.Default.Female, employees.count { it.department == "النساء والولادة" }, appointments.count { it.department == "النساء والولادة" }, 42000.0),
        MedicalDepartmentInfo("الجراحة", Icons.Default.LocalHospital, employees.count { it.department == "الجراحة" }, appointments.count { it.department == "الجراحة" }, 68000.0),
        MedicalDepartmentInfo("الأشعة", Icons.Default.Camera, employees.count { it.department == "الأشعة" }, appointments.count { it.department == "الأشعة" }, 29000.0),
        MedicalDepartmentInfo("المختبر", Icons.Default.Science, employees.count { it.department == "المختبر" }, appointments.count { it.department == "المختبر" }, 34000.0)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "الأقسام الطبية التخصصية",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "متابعة الأطباء، المواعيد والإنتاجية لكل قسم طبي",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(deptList) { dept ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MedicalTealContainer,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = dept.icon,
                                    contentDescription = null,
                                    tint = MedicalTealOnContainer
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "قسم ${dept.name}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "الأطباء: ${dept.doctorCount} أطباء",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "مواعيد اليوم: ${dept.todayAppointments} مواعيد",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "الإيراد المتوقع: ${dept.monthlyRevenue.toInt()} ر.س",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
