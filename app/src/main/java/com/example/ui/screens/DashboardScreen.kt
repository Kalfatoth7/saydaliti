package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.*
import com.example.ui.components.PharmacyComparisonChart
import com.example.ui.components.RevenueTrendLineChart
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    pharmacies: List<Pharmacy>,
    medicines: List<Medicine>,
    employees: List<Employee>,
    appointments: List<Appointment>,
    financials: List<FinancialRecord>,
    surgeries: List<Surgery>,
    labTests: List<LabTest>,
    scans: List<RadiologyScan>,
    onOpenAiCenter: () -> Unit,
    onNavigateTo: (String) -> Unit
) {
    val totalPharmacySalesToday = pharmacies.sumOf { it.salesToday }
    val totalExpensesToday = financials.filter { it.type == "مصروف" }.sumOf { it.amount }
    val netProfitEstimated = totalPharmacySalesToday - totalExpensesToday
    val presentEmployeesCount = employees.count { it.attendanceStatus == "حاضر" }
    val lowStockMeds = medicines.filter { it.stockQuantity <= it.minStockAlert }
    val waitingAppts = appointments.count { it.status == "انتظار" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Hero Banner with Image
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_health_1784741597420),
                    contentDescription = "Healthcare Hero Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.55f))
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MedicalTealContainer
                    ) {
                        Text(
                            text = "مركز الرحمة الطبي • النظام الذكي الشامل",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedicalTealOnContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "نظام الإدارة الذكي للمركز الصحي والصيدليات",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "لوحة متابعة فورية للأرباح، المخزون، الموظفين والمواعيد",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Director Mohiuddin Al-Jafari Executive Panel Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AlRahmaGoldContainer,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "المدير العام",
                                tint = AlRahmaOnGoldContainer,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "أ.د. محي الدين الجعفري",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = AlRahmaGoldAccent
                            ) {
                                Text(
                                    text = "المدير العام",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "لوحة تحكم وتوجيه القرارات التنفيذية - مركز الرحمة الطبي",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                // Quick Director Shortcuts
                Text(
                    text = "مهام وصلاحيات مدير المركز:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SuggestionChip(
                        onClick = { onNavigateTo("financials") },
                        label = { Text("الاعتمادات الماليّة", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        border = null
                    )
                    SuggestionChip(
                        onClick = { onNavigateTo("employees") },
                        label = { Text("متابعة الكادر", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = AlRahmaGoldContainer,
                            labelColor = AlRahmaOnGoldContainer
                        ),
                        border = null
                    )
                    SuggestionChip(
                        onClick = { onNavigateTo("smart_inventory_ai") },
                        label = { Text("المخزون الذكي", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Inventory, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        border = null
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Executive Quick Status Banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = AlRahmaPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "نظام المركز يعمل بكفاءة 99.8%. جميع الصيدليات والعيادات والأشعة موصولة مباشرة بقرارات د. محي الدين الجعفري.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // AI Daily Executive Summary Box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenAiCenter() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "تقرير المساعد الذكي للدكتور محي الدين الجعفري",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "أهلاً بك د. محي الدين. الإيرادات ممتازة اليوم (+12%). صيدلية 3 حققت أعلى مبيعات. يوجد ${lowStockMeds.size} أدوية حتمية بانتظار موافقتكم لإعادة الطلب.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Key Metrics Grid
        Text(
            text = "المؤشرات الماليّة والتشغيليّة اليومية",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DashboardStatCard(
                title = "إيرادات الصيدليات",
                value = "${totalPharmacySalesToday.toInt()} ر.س",
                subtitle = "مجموع الـ 4 صيدليات",
                icon = Icons.Default.AttachMoney,
                iconBg = StatusSuccess,
                modifier = Modifier.weight(1f)
            )
            DashboardStatCard(
                title = "صافي الربح التقديري",
                value = "${netProfitEstimated.toInt()} ر.س",
                subtitle = "الإيرادات - المصروفات",
                icon = Icons.Default.AccountBalance,
                iconBg = MedicalTealPrimary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DashboardStatCard(
                title = "المرضى والمواعيد اليوم",
                value = "${appointments.size} مواعيد",
                subtitle = "$waitingAppts في قائمة الانتظار",
                icon = Icons.Default.People,
                iconBg = StatusInfo,
                modifier = Modifier.weight(1f)
            )
            DashboardStatCard(
                title = "الموظفون الحاضرون",
                value = "$presentEmployeesCount / ${employees.size}",
                subtitle = "${employees.size - presentEmployeesCount} غائبين أو إجازة",
                icon = Icons.Default.Badge,
                iconBg = StatusWarning,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DashboardStatCard(
                title = "نقص المخزون (أدوية)",
                value = "${lowStockMeds.size} أدوية",
                subtitle = "وصلت لحد الأمان",
                icon = Icons.Default.Warning,
                iconBg = StatusError,
                modifier = Modifier.weight(1f)
            )
            DashboardStatCard(
                title = "الفحوصات والعمليات",
                value = "${labTests.size + scans.size} فحوصات",
                subtitle = "${surgeries.size} عمليات مجدولة",
                icon = Icons.Default.Biotech,
                iconBg = MedicalTealTertiary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // What's Happening Now Live Feed Section
        Text(
            text = "⚡ ماذا يحدث الآن؟ (التنبيهات المباشرة)",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                LiveAlertChip(
                    title = "⚠️ دواء منخفض المخزون",
                    desc = "أوجمنتين 1 جم متبقي 8 علب في صيدلية 1",
                    severity = StatusError,
                    onClick = { onNavigateTo("inventory") }
                )
            }
            item {
                LiveAlertChip(
                    title = "⏳ FEFO انتهاء صلاحية قريب",
                    desc = "بروفين 400 ملجم ينتهي خلال 15 يوماً",
                    severity = StatusWarning,
                    onClick = { onNavigateTo("inventory") }
                )
            }
            item {
                LiveAlertChip(
                    title = "👨‍⚕️ تغطية موظفين",
                    desc = "غياب الصيدلانية سارة في صيدلية 2",
                    severity = StatusInfo,
                    onClick = { onNavigateTo("employees") }
                )
            }
            item {
                LiveAlertChip(
                    title = "🔪 موعد عملية قريب",
                    desc = "استئصال زائدة غداً 08:00 ص",
                    severity = MedicalTealPrimary,
                    onClick = { onNavigateTo("surgeries") }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Visual Charts Section
        RevenueTrendLineChart()

        Spacer(modifier = Modifier.height(16.dp))

        PharmacyComparisonChart(pharmacies = pharmacies)

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun DashboardStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = iconBg,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun LiveAlertChip(
    title: String,
    desc: String,
    severity: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = severity.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, severity.copy(alpha = 0.4f)),
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = severity
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = desc,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )
        }
    }
}
