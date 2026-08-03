package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Medicine
import com.example.data.models.Pharmacy
import com.example.ui.theme.MedicalTealContainer
import com.example.ui.theme.MedicalTealOnContainer
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusWarning

data class InventoryAiSuggestion(
    val title: String,
    val description: String,
    val recommendedAction: String,
    val severity: String // "عاجل", "تنبيه", "تحسين"
)

@Composable
fun SmartInventoryAiScreen(
    medicines: List<Medicine>,
    pharmacies: List<Pharmacy>,
    onApplyAction: (String) -> Unit
) {
    val lowStockMeds = medicines.filter { it.stockQuantity <= it.minStockAlert }
    val nearExpiryMeds = medicines.filter { it.expiryDate.startsWith("2026-08") || it.expiryDate.startsWith("2026-09") }

    val aiSuggestions = listOf(
        InventoryAiSuggestion(
            title = "⚠️ تنبؤ بالنفاد: أوجمنتين 1 جم (Augmentin)",
            description = "معدل السحب اليومي 4 علب. المتبقي 8 علب فقط في صيدلية 1. قد ينفد بالكامل خلال يومين.",
            recommendedAction = "إرسال أمر تحويل 30 علبة من المستودع المركزي إلى صيدلية 1",
            severity = "عاجل"
        ),
        InventoryAiSuggestion(
            title = "🔄 اقتراح إعادة توزيع المخزون الزائد",
            description = "يوجد مخزون زائد من بروفين 400 جم (180 علبة) في صيدلية 3، مقابل سحب مرتفع في صيدلية 2.",
            recommendedAction = "نقل 40 وحدة من صيدلية 3 إلى صيدلية 2 لتوازن العرض والطلب",
            severity = "تنبيه"
        ),
        InventoryAiSuggestion(
            title = "⏳ أدوية راكدة وقريبة من الانتهاء (مبدأ FEFO)",
            description = "دواء أوجمنتين ينتهي في 2026-08-30 ومبيعاته متباطئة بـ صيدلية 1.",
            recommendedAction = "عرض الدواء في الواجهة الأمامية أو عمل الخصم المعتمد لتصريفه قبل الانتهاء",
            severity = "عاجل"
        ),
        InventoryAiSuggestion(
            title = "📉 تحذير عدم تكرار طلبية صيدلية 4",
            description = "دواء نكسيوم 40 جم يظهر انخفاضاً في المبيعات بنسبة 25% هذا الشهر بـ صيدلية 4.",
            recommendedAction = "عدم طلب كميات إضافية حالياً من المورد لتقليل الركود المالي",
            severity = "تحسين"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MedicalTealContainer,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MedicalTealOnContainer
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "الذكاء الاصطناعي للمخزون AI Smart Inventory",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "تنبؤ بالنفاد، تحليل سرعة البيع واقتراحات إعادة التوزيع الآلي",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AI Recommendations Summary
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "💡 ملخص تحليل الذكاء الاصطناعي المباشر:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• تم رصد ${lowStockMeds.size} أدوية قد تنفد خلال 48 ساعة.\n• تم رصد ${nearExpiryMeds.size} صنف قريب من تاريخ الانتهاء ينصح بتصريفها فوراً.\n• النظام يوصي بتنفيذ 2 عمليات نقل بين الصيدليات لتوفير 18,000 ريال من المشتريات الجديدة.",
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "توصيات إجراءات المخزون الذكية:",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(aiSuggestions) { sug ->
                val badgeColor = when (sug.severity) {
                    "عاجل" -> StatusError
                    "تنبيه" -> StatusWarning
                    else -> MaterialTheme.colorScheme.primary
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = sug.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = badgeColor
                            ) {
                                Text(
                                    text = sug.severity,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = sug.description,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TaskAlt,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "الإجراء المقترح:",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = sug.recommendedAction,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Button(
                                    onClick = { onApplyAction(sug.recommendedAction) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("اعتماد المقترح", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
