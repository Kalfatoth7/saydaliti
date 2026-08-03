package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Medicine
import com.example.data.models.Pharmacy
import com.example.ui.components.PharmacyComparisonChart
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmaciesScreen(
    pharmacies: List<Pharmacy>,
    medicines: List<Medicine>,
    onTransferStock: (Long, String, Int) -> Unit = { _, _, _ -> },
    onOpenMessagingWithContext: ((String, String, String, String) -> Unit)? = null
) {
    val context = LocalContext.current
    var selectedPharmacyId by remember { mutableStateOf("ALL") }
    var showAiSuggestionApproved by remember { mutableStateOf(false) }

    val activePharmacies = pharmacies.filter { it.id != "central_warehouse" }

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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "إدارة ومقارنة الصيدليات الأربع (مركز الرحمة الطبي)",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "متابعة المبيعات، الربحية، المخزون والتوزيع الذكي بين صيدلية 1، 2، 3 و 4",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (onOpenMessagingWithContext != null) {
                Button(
                    onClick = {
                        onOpenMessagingWithContext(
                            "GROUP",
                            "pharmacists",
                            "💊 فريق الصيدليات الأربع",
                            "تواصل مباشر مع صيادلة المركز"
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlRahmaPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("مراسلة الصيادلة", fontSize = 11.sp)
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        // AI Stock Redistribution Recommendation Box
        if (!showAiSuggestionApproved) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AlRahmaGoldContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AlRahmaOnGoldContainer,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "توصية ذكاء اصطناعي لدكتور محي الدين الجعفري (توزيع المخزون)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = AlRahmaOnGoldContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "صيدلية 1 لديها 140 علبة بنادول إكسترا (فائض)، بينما صيدلية 3 (الطوارئ) تنفذ منها الكمية بسرعة. يُنصح بنقل 30 علبة فوراً.",
                        fontSize = 12.sp,
                        color = AlRahmaOnGoldContainer.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = {
                                Toast.makeText(context, "تم تجاهل التوصية مؤقتاً", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Text("تأجيل", fontSize = 11.sp, color = AlRahmaOnGoldContainer)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                // Execute stock transfer in DB!
                                val targetMed = medicines.find { it.pharmacyId == "pharmacy_1" && it.tradeName.contains("بنادول") }
                                if (targetMed != null) {
                                    onTransferStock(targetMed.id, "pharmacy_3", 30)
                                }
                                showAiSuggestionApproved = true
                                Toast.makeText(context, "تم اعتماد النقل المباشر وتحديث مخزون صيدلية 3 بنجاح!", Toast.LENGTH_LONG).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AlRahmaPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("موافقة واعتماد النقل", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Tab Filter Row
        ScrollableTabRow(
            selectedTabIndex = if (selectedPharmacyId == "ALL") 0 else (activePharmacies.indexOfFirst { it.id == selectedPharmacyId } + 1).coerceAtLeast(0),
            edgePadding = 0.dp
        ) {
            Tab(
                selected = selectedPharmacyId == "ALL",
                onClick = { selectedPharmacyId = "ALL" },
                text = { Text("جميع الصيدليات (4)", fontWeight = FontWeight.Bold) }
            )
            activePharmacies.forEachIndexed { index, pharmacy ->
                Tab(
                    selected = selectedPharmacyId == pharmacy.id,
                    onClick = { selectedPharmacyId = pharmacy.id },
                    text = { Text("صيدلية ${index + 1}", fontWeight = FontWeight.Bold) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (selectedPharmacyId == "ALL") {
                // Summary Comparison Matrix Table
                item {
                    PharmaciesMatrixTable(pharmacies = activePharmacies)
                }

                // Visual Comparison Chart
                item {
                    PharmacyComparisonChart(pharmacies = pharmacies)
                }

                item {
                    Text(
                        text = "تفاصيل أداء كل صيدلية:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                items(activePharmacies) { pharmacy ->
                    PharmacyDetailCard(pharmacy = pharmacy, medicines = medicines)
                }
            } else {
                val ph = pharmacies.find { it.id == selectedPharmacyId }
                if (ph != null) {
                    item {
                        PharmacyDetailCard(pharmacy = ph, medicines = medicines, isExpanded = true)
                    }
                }
            }
        }
    }
}

@Composable
fun PharmaciesMatrixTable(pharmacies: List<Pharmacy>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "مصفوفة المقارنة المباشرة للصيدليات الأربع",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = AlRahmaPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("الصيدلية", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1.2f))
                Text("المبيعات", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f))
                Text("الأرباح", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f))
                Text("الأداء", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(0.8f))
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            pharmacies.forEach { ph ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(ph.name.take(16), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.2f))
                    Text("${ph.salesToday.toInt()} ج.م", fontSize = 11.sp, modifier = Modifier.weight(1f))
                    Text("${ph.profitMonth.toInt()} ج.م", fontSize = 11.sp, color = StatusSuccess, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (ph.id == "pharmacy_3") AlRahmaGoldAccent else StatusSuccess,
                        modifier = Modifier.weight(0.8f)
                    ) {
                        Text(
                            text = if (ph.id == "pharmacy_3") "الأعلى 🏆" else "ممتاز",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PharmacyDetailCard(
    pharmacy: Pharmacy,
    medicines: List<Medicine>,
    isExpanded: Boolean = false
) {
    val phMeds = medicines.filter { it.pharmacyId == pharmacy.id }
    val lowStockCount = phMeds.count { it.stockQuantity <= it.minStockAlert }

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
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Medication,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = pharmacy.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            text = "عدد الكادر: ${pharmacy.staffCount} صيدلي • العمليات اليوم: ${pharmacy.txCountToday}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "مبيعات اليوم", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "${pharmacy.salesToday.toInt()} ج.م", fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
                Column {
                    Text(text = "أرباح الشهر", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "${pharmacy.profitMonth.toInt()} ج.م", fontWeight = FontWeight.Black, fontSize = 14.sp, color = StatusSuccess)
                }
                Column {
                    Text(text = "نقص المخزون", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "$lowStockCount أصناف", fontWeight = FontWeight.Black, fontSize = 14.sp, color = if (lowStockCount > 0) StatusError else StatusSuccess)
                }
            }

            if (isExpanded && phMeds.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "الأدوية والمخزون في هذه الصيدلية:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                phMeds.take(6).forEach { med ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = med.tradeName, fontSize = 12.sp)
                        Text(text = "${med.stockQuantity} عبوة", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AlRahmaPrimary)
                    }
                }
            }
        }
    }
}
