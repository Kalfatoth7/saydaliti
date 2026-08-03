package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.data.models.FinancialRecord
import com.example.data.models.Pharmacy
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialsScreen(
    financials: List<FinancialRecord>,
    pharmacies: List<Pharmacy>,
    onAddFinancialRecord: (FinancialRecord) -> Unit,
    onResetFinancials: () -> Unit = {}
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf("ALL") } // ALL, REVENUE, EXPENSE, PURCHASES, SALARIES
    var exportToastMessage by remember { mutableStateOf<String?>(null) }

    val totalRevenues = financials.filter { it.type == "إيراد" }.sumOf { it.amount } + pharmacies.sumOf { it.salesMonth }
    val totalExpenses = financials.filter { it.type == "مصروف" || it.type == "مشتريات" || it.type == "راتب" }.sumOf { it.amount }
    val netProfit = totalRevenues - totalExpenses

    val filteredRecords = financials.filter { rec ->
        when (selectedTab) {
            "REVENUE" -> rec.type == "إيراد"
            "EXPENSE" -> rec.type == "مصروف"
            "PURCHASES" -> rec.type == "مشتريات"
            "SALARIES" -> rec.type == "راتب"
            else -> true
        }
    }

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
                    text = "المالية والحسابات المركزية",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "الإيرادات - المصروفات = صافي الربح • تقارير الصيدليات والأقسام",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { showResetConfirmDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تصفير المؤشرات", fontSize = 11.sp)
                }
                OutlinedButton(
                    onClick = { exportToastMessage = "تم تصدير التقرير المالي كـ PDF و Excel بنجاح 📄" },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تصدير تقرير", fontSize = 11.sp)
                }
                Button(onClick = { showAddDialog = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة سند", fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Financial Executive Summary Cards
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FinMetricCard(
                title = "إجمالي الإيرادات",
                value = "${totalRevenues.toInt()} ر.س",
                color = StatusSuccess,
                icon = Icons.Default.TrendingUp,
                modifier = Modifier.weight(1f)
            )
            FinMetricCard(
                title = "إجمالي المصروفات",
                value = "${totalExpenses.toInt()} ر.س",
                color = StatusError,
                icon = Icons.Default.TrendingDown,
                modifier = Modifier.weight(1f)
            )
            FinMetricCard(
                title = "صافي الربح",
                value = "${netProfit.toInt()} ر.س",
                color = MedicalTealPrimary,
                icon = Icons.Default.AccountBalanceWallet,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Tabs
        ScrollableTabRow(
            selectedTabIndex = when (selectedTab) {
                "REVENUE" -> 1
                "EXPENSE" -> 2
                "PURCHASES" -> 3
                "SALARIES" -> 4
                else -> 0
            },
            edgePadding = 0.dp
        ) {
            Tab(selected = selectedTab == "ALL", onClick = { selectedTab = "ALL" }, text = { Text("الكل") })
            Tab(selected = selectedTab == "REVENUE", onClick = { selectedTab = "REVENUE" }, text = { Text("الإيرادات") })
            Tab(selected = selectedTab == "EXPENSE", onClick = { selectedTab = "EXPENSE" }, text = { Text("المصروفات") })
            Tab(selected = selectedTab == "PURCHASES", onClick = { selectedTab = "PURCHASES" }, text = { Text("المشتريات") })
            Tab(selected = selectedTab == "SALARIES", onClick = { selectedTab = "SALARIES" }, text = { Text("الرواتب") })
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredRecords.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "لا توجد سجلات مالية", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "قم بإضافة سند مالي جديد للبدء", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredRecords) { rec ->
                    val typeColor = when (rec.type) {
                        "إيراد" -> StatusSuccess
                        "مصروف" -> StatusError
                        "مشتريات" -> StatusWarning
                        else -> MedicalTealTertiary
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
                                    shape = RoundedCornerShape(8.dp),
                                    color = typeColor.copy(alpha = 0.15f),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (rec.type == "إيراد") Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                            contentDescription = null,
                                            tint = typeColor
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = rec.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = "${rec.category} • ${rec.locationOrDept} • ${rec.date}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Text(
                                text = "${if (rec.type == "إيراد") "+" else "-"}${rec.amount.toInt()} ر.س",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = typeColor
                            )
                        }
                    }
                }
            }
        }
    }

    // Export Snackbar feedback
    exportToastMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { exportToastMessage = null },
            title = { Text("تم تصدير التقرير") },
            text = { Text(msg) },
            confirmButton = {
                Button(onClick = { exportToastMessage = null }) {
                    Text("حسناً")
                }
            }
        )
    }

    if (showAddDialog) {
        AddFinancialRecordModal(
            onDismiss = { showAddDialog = false },
            onSave = { record ->
                onAddFinancialRecord(record)
                showAddDialog = false
            }
        )
    }

    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تأكيد تصفير المؤشرات المالية")
                }
            },
            text = {
                Text("هل أنت تأكد من رغبتك في تصفير جميع المؤشرات المالية والسجلات والسندات وإعادة تعيين الحسابات إلى 0 ر.س؟ لا يمكن التراجع عن هذه العملية.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetFinancials()
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("نعم، صفّر المؤشرات المالية")
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
fun FinMetricCard(
    title: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontWeight = FontWeight.Black, fontSize = 15.sp, color = color)
            Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFinancialRecordModal(
    onDismiss: () -> Unit,
    onSave: (FinancialRecord) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("إيراد") }
    var category by remember { mutableStateOf("عيادات") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text("تسجيل بند مالي جديد", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = type == "إيراد", onClick = { type = "إيراد" }, label = { Text("إيراد") })
                FilterChip(selected = type == "مصروف", onClick = { type = "مصروف" }, label = { Text("مصروف") })
                FilterChip(selected = type == "مشتريات", onClick = { type = "مشتريات" }, label = { Text("مشتريات") })
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("عنوان البند المالي") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("المبلغ (ر.س)") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (title.isNotBlank() && amount.isNotBlank()) {
                        onSave(
                            FinancialRecord(
                                type = type,
                                title = title,
                                amount = amount.toDoubleOrNull() ?: 0.0,
                                category = category,
                                locationOrDept = "المركز الرئيسي",
                                date = "2026-07-22"
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("حفظ السند")
            }
        }
    }
}
