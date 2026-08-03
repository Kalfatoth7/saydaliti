package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.models.UserRole

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Dashboard : Screen("dashboard", "الرئيسية", Icons.Default.Dashboard)
    data object CommandCenter : Screen("command_center", "مركز القيادة (د. محي الدين)", Icons.Default.AdminPanelSettings)
    data object Messaging : Screen("messaging", "المراسلات والدردشة", Icons.Default.Chat)
    data object Pharmacies : Screen("pharmacies", "الصيدليات 4/4", Icons.Default.Medication)
    data object Inventory : Screen("inventory", "المخزون والباركود", Icons.Default.Inventory)
    data object ReceptionQueue : Screen("reception_queue", "الاستقبال والطوابير", Icons.Default.ConfirmationNumber)
    data object Financials : Screen("financials", "المالية والفواتير", Icons.Default.AccountBalanceWallet)
    data object Employees : Screen("employees", "الموظفون والرواتب", Icons.Default.Badge)
    data object Departments : Screen("departments", "العيادات والأقسام", Icons.Default.Domain)
    data object Patients : Screen("patients", "سجل المرضى الموحد", Icons.Default.People)
    data object PatientDashboard : Screen("patient_dashboard", "لوحة تحكم المريض", Icons.Default.MedicalServices)
    data object Appointments : Screen("appointments", "المواعيد والتقويم", Icons.Default.CalendarToday)
    data object LabScans : Screen("lab_scans", "المختبر والأشعة", Icons.Default.Science)
    data object Surgeries : Screen("surgeries", "العمليات الجراحية", Icons.Default.LocalHospital)
    data object Reports : Screen("reports", "مركز التقارير PDF", Icons.Default.Summarize)
    data object AuditLogs : Screen("audit_logs", "سجل الأمان والتدقيق", Icons.Default.Shield)
    data object AiInsights : Screen("ai_insights", "التحليل الذكي (AI)", Icons.Default.Insights)
}

val mainNavigationItems = listOf(
    Screen.Dashboard,
    Screen.CommandCenter,
    Screen.Messaging,
    Screen.PatientDashboard,
    Screen.Pharmacies,
    Screen.Inventory,
    Screen.ReceptionQueue,
    Screen.Financials,
    Screen.Employees,
    Screen.Departments,
    Screen.Patients,
    Screen.Appointments,
    Screen.LabScans,
    Screen.Surgeries,
    Screen.Reports,
    Screen.AuditLogs,
    Screen.AiInsights
)

@Composable
fun HealthOsBottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        listOf(Screen.Dashboard, Screen.Messaging, Screen.Pharmacies, Screen.ReceptionQueue, Screen.Financials).forEach { screen ->

            val isSelected = currentRoute == screen.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(screen.route) },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title
                    )
                },
                label = {
                    Text(
                        text = screen.title.take(12),
                        fontSize = 9.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}

@Composable
fun HealthOsSidebarDrawerContent(
    currentRoute: String,
    currentRole: UserRole,
    onNavigate: (String) -> Unit,
    onCloseDrawer: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(310.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocalHospital,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "مركز الرحمة الطبي",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "نظام الإدارة والرعاية الصحية المتكامل",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Director Profile Badge inside Drawer
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "أ.د. محي الدين الجعفري",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "المدير العام لمركز الرحمة الطبي",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "القائمة والخدمات الشاملة",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(6.dp))

            // Scrollable Navigation Items List
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                mainNavigationItems.forEach { screen ->
                    val selected = currentRoute == screen.route
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = screen.title,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        selected = selected,
                        onClick = {
                            onNavigate(screen.route)
                            onCloseDrawer()
                        },
                        modifier = Modifier.padding(vertical = 1.dp),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(8.dp))

            // Current Role Card at bottom of sidebar
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "الدور النشط بالنظام:",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = currentRole.arabicName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
