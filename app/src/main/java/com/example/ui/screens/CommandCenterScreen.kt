package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommandCenterScreen(
    pharmacies: List<Pharmacy>,
    medicines: List<Medicine>,
    employees: List<Employee>,
    appointments: List<Appointment>,
    financials: List<FinancialRecord>,
    auditLogs: List<AuditLog> = emptyList(),
    onNavigateTo: (String) -> Unit,
    onOpenAiCenter: () -> Unit,
    onResetFinancialData: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    // Calculate Center Health Score (0-100)
    val totalRevenue = financials.filter { it.type == "إيراد" }.sumOf { it.amount }
    val totalExpense = financials.filter { it.type != "إيراد" }.sumOf { it.amount }
    val netProfit = totalRevenue - totalExpense
    val lowStockMeds = medicines.filter { it.stockQuantity <= it.minStockAlert }
    val presentEmployees = employees.filter { it.attendanceStatus == "حاضر" }
    val staffRatio = if (employees.isNotEmpty()) (presentEmployees.size.toFloat() / employees.size.toFloat()) else 1f

    val healthScore = (85 + (if (netProfit > 0) 5 else 0) - (lowStockMeds.size * 2) + (staffRatio * 10).toInt()).coerceIn(60, 98)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Director Header Profile Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = AlRahmaGoldAccent,
                    modifier = Modifier.size(60.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "م.ج",
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "أ.د. محي الدين الجعفري",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "موثق",
                            tint = AlRahmaGoldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "المدير العام لمركز الرحمة الطبي",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "مركز القيادة الموحد والتوجيه الاستراتيجي المباشر",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Center Overall Health Score Card (تقييم صحة المركز)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = AlRahmaPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "مؤشر كفاءة وصحة المركز الإجمالي",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (healthScore >= 90) StatusSuccess.copy(alpha = 0.15f) else StatusWarning.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "$healthScore / 100",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = if (healthScore >= 90) StatusSuccess else StatusWarning,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { healthScore / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape),
                    color = AlRahmaPrimary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Breakdown list of ratings
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HealthSubRatingRow("الأداء المالي والأرباح", 94, StatusSuccess)
                    HealthSubRatingRow("كفاءة الصيدليات الأربع", 91, StatusSuccess)
                    HealthSubRatingRow("استقرار سلامة المخزون", 82, StatusWarning)
                    HealthSubRatingRow("معدل رضا المرضى", 96, StatusSuccess)
                    HealthSubRatingRow("انتظام الموظفين والكادر", (staffRatio * 100).toInt(), if (staffRatio > 0.8f) StatusSuccess else StatusWarning)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Departmental Live Status Grid (حالة المركز الآن)
        Text(
            text = "حالة أقسام مركز الرحمة الطبي الآن",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DeptStatusCard(
                modifier = Modifier.weight(1f),
                title = "المركز الطبي",
                status = "عمل طبيعي",
                statusColor = StatusSuccess,
                icon = Icons.Default.LocalHospital,
                details = "24 مريضاً اليوم"
            ) { onNavigateTo("departments") }

            DeptStatusCard(
                modifier = Modifier.weight(1f),
                title = "الصيدليات 4/4",
                status = "4 صيدليات نشطة",
                statusColor = StatusSuccess,
                icon = Icons.Default.Medication,
                details = "53.9k ج.م مبيعات"
            ) { onNavigateTo("pharmacies") }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DeptStatusCard(
                modifier = Modifier.weight(1f),
                title = "المستودع المركزي",
                status = "8 أصناف حرجة",
                statusColor = StatusWarning,
                icon = Icons.Default.Warehouse,
                details = "إعادة طلب مقترحة"
            ) { onNavigateTo("inventory") }

            DeptStatusCard(
                modifier = Modifier.weight(1f),
                title = "المختبر والأشعة",
                status = "طبيعي ومستقر",
                statusColor = StatusSuccess,
                icon = Icons.Default.Science,
                details = "14 فحصاً جاهزاً"
            ) { onNavigateTo("lab_scans") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Director Quick Executive Directives / Approvals
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.AssignmentTurnedIn,
                        contentDescription = null,
                        tint = AlRahmaGoldAccent,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "قرارات واعتمادات بانتظار موافقة د. محي الدين الجعفري",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                DirectiveApprovalItem(
                    title = "اعتماد شراء طلبية أدوية طوارئ (أوجمنتين وجلوكوفاج)",
                    subtitle = "المبلغ الإجمالي: 12,400 ج.م • مستودع الشرق الطبية",
                    badge = "مالي ومخزون",
                    onApprove = { onNavigateTo("inventory") }
                )

                DirectiveApprovalItem(
                    title = "نقل 30 وحدة بنادول إكسترا من صيدلية 1 إلى صيدلية 3",
                    subtitle = "تخفيف الازدحام وتلبية طلب طوارئ الليل",
                    badge = "مناقلة مخزون",
                    onApprove = { onNavigateTo("pharmacies") }
                )

                DirectiveApprovalItem(
                    title = "صرف مكافأة تميز لكادر صيدلية 3 (الأعلى مبيعات)",
                    subtitle = "المبلغ الإجمالي المقترح: 3,500 ج.م",
                    badge = "إداري",
                    onApprove = { onNavigateTo("financials") }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Financial Control & Audit Logs Card (التحكم المالي وسجل التدقيق)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "الضوابط والرقابة المالية وسجل التدقيق (AuditLogs)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "إعادة ضبط وتصفير كافة الحسابات والجداول المالية (الإيرادات، المصروفات، المشتريات، والرواتب) مع التوثيق الأمني في سجل الرقابة الحسابية.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { showResetConfirmDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تصفير البيانات المالية", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                if (auditLogs.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "سجل التدقيق الأمني المالي (AuditLogs):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    auditLogs.take(5).forEach { log ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = log.action, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                                    Text(text = log.timestamp, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(text = "تم بواسطة: ${log.performedBy}", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = log.details, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AI Assistant Launch Card
        Button(
            onClick = onOpenAiCenter,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AlRahmaPrimary)
        ) {
            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "استشارة المساعد الذكي لدكتور محي الدين الجعفري (AI)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }

    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تأكيد تصفير البيانات المالية", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text("هل أنت تأكد من رغبتك في تصفير كافة الجداول المالية (الإيرادات، المصروفات، المشتريات، والرواتب) وتصفير مبيعات وحسابات الصيدليات الأربع؟\n\n⚠️ سيتم تسجيل وتوثيق هذه العملية رسمياً في سجل التدقيق المالي والأمني (AuditLogs) تحت اسم أ.د. محي الدين الجعفري.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetFinancialData()
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("نعم، صفّر البيانات وسجّل العملية")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetConfirmDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun HealthSubRatingRow(label: String, score: Int, scoreColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "$score%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = scoreColor)
            Spacer(modifier = Modifier.width(8.dp))
            LinearProgressIndicator(
                progress = { score / 100f },
                modifier = Modifier
                    .width(80.dp)
                    .height(6.dp)
                    .clip(CircleShape),
                color = scoreColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
fun DeptStatusCard(
    modifier: Modifier = Modifier,
    title: String,
    status: String,
    statusColor: Color,
    icon: ImageVector,
    details: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(18.dp))
                    }
                }
                Surface(
                    shape = CircleShape,
                    color = statusColor,
                    modifier = Modifier.size(8.dp)
                ) {}
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = status, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = statusColor)
            Text(text = details, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun DirectiveApprovalItem(
    title: String,
    subtitle: String,
    badge: String,
    onApprove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = AlRahmaGoldContainer
                ) {
                    Text(
                        text = badge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AlRahmaOnGoldContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Button(
                    onClick = onApprove,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(28.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AlRahmaPrimary)
                ) {
                    Text("مراجعة واعتماد", fontSize = 10.sp, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
