package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.*
import com.example.ui.theme.AlRahmaGoldAccent
import com.example.ui.theme.AlRahmaPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    pharmacies: List<Pharmacy>,
    medicines: List<Medicine>,
    employees: List<Employee>,
    financials: List<FinancialRecord>
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var selectedPeriod by remember { mutableStateOf("الشهر الحالي") }
    var selectedBranch by remember { mutableStateOf("جميع الفروع والصيدليات") }

    val totalRevenue = financials.filter { it.type == "إيراد" }.sumOf { it.amount }
    val totalExpense = financials.filter { it.type != "إيراد" }.sumOf { it.amount }
    val netProfit = totalRevenue - totalExpense

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "مركز التقارير المتكامل والبيانات الشاملة",
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "اصدار وتصدير تقارير الإيرادات، الأرباح، الصيدليات الأربع، والمخزون بصيغ PDF/Excel",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Controls
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "خيارات الفلترة والتصدير:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedPeriod == "اليوم",
                        onClick = { selectedPeriod = "اليوم" },
                        label = { Text("اليوم", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedPeriod == "الأسبوع",
                        onClick = { selectedPeriod = "الأسبوع" },
                        label = { Text("الأسبوع", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedPeriod == "الشهر الحالي",
                        onClick = { selectedPeriod = "الشهر الحالي" },
                        label = { Text("الشهر الحالي", fontSize = 11.sp) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List of Available Reports
        Text(
            text = "التقارير الجاهزة للتصدير والطباعة:",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ReportExportCard(
            title = "تقرير القوائم المالية والأرباح الموحدة",
            description = "إجمالي الإيرادات: ${String.format("%.0f", totalRevenue)} ج.م • صافي الأرباح: ${String.format("%.0f", netProfit)} ج.م",
            icon = Icons.Default.MonetizationOn,
            onExportPdf = {
                Toast.makeText(context, "جاري تصدير تقرير الأرباح المالي بصيغة PDF...", Toast.LENGTH_SHORT).show()
            },
            onExportExcel = {
                Toast.makeText(context, "جاري تصدير الجدول المالي بصيغة Excel...", Toast.LENGTH_SHORT).show()
            }
        )

        ReportExportCard(
            title = "تقرير الأداء والمبيعات المقارن للصيدليات الأربع",
            description = "مبيعات صيدلية 1، 2، 3، 4 والمستودع المركزي مع هوامش الربح",
            icon = Icons.Default.Medication,
            onExportPdf = {
                Toast.makeText(context, "جاري تصدير تقرير مقارنة الصيدليات PDF...", Toast.LENGTH_SHORT).show()
            },
            onExportExcel = {
                Toast.makeText(context, "جاري تصدير تقرير مقارنة الصيدليات Excel...", Toast.LENGTH_SHORT).show()
            }
        )

        ReportExportCard(
            title = "تقرير حركة المخزون والأدوية الراكدة و FEFO",
            description = "إجمالي الأصناف: ${medicines.size} دواء • المنتهية والقريبة من الانتهاء",
            icon = Icons.Default.Inventory,
            onExportPdf = {
                Toast.makeText(context, "جاري تصدير تقرير المخزون PDF...", Toast.LENGTH_SHORT).show()
            },
            onExportExcel = {
                Toast.makeText(context, "جاري تصدير جُداول الأدوية Excel...", Toast.LENGTH_SHORT).show()
            }
        )

        ReportExportCard(
            title = "تقرير حضور ورواتب الكادر الطبي والموظفين",
            description = "عدد الموظفين: ${employees.size} • السجلات ومكافآت التميز",
            icon = Icons.Default.Badge,
            onExportPdf = {
                Toast.makeText(context, "جاري تصدير تقرير الموظفين PDF...", Toast.LENGTH_SHORT).show()
            },
            onExportExcel = {
                Toast.makeText(context, "جاري تصدير كشف الموظفين Excel...", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun ReportExportCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onExportPdf: () -> Unit,
    onExportExcel: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onExportExcel,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Excel", fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onExportPdf,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AlRahmaPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تصدير PDF", fontSize = 11.sp)
                }
            }
        }
    }
}
