package com.example.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Pharmacy
import com.example.ui.theme.MedicalTealContainer
import com.example.ui.theme.MedicalTealOnContainer

@Composable
fun AiInsightsScreen(
    pharmacies: List<Pharmacy>,
    onOpenAiCommandCenter: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MedicalTealContainer,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Insights,
                        contentDescription = null,
                        tint = MedicalTealOnContainer
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "مركز التحليل الذكي والتقارير التنفيذية",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "رؤى تنبؤية وتوصيات إستراتيجية لرفع أرباح وكفاءة المركز",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AI Daily Executive Report Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "📊 التقرير المالي والتنفيذي اليومي (HEALTH OS AI):", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "1. الأداء المالي: حققت الصيدليات اليوم إجمالي مبيعات 53,900 ريال، بزيادة +12% مقارنة بمتوسط الأسبوع.\n\n" +
                            "2. كفاءة التشغيل: قسم الباطنية والجراحة هما الأكثر إيراداً وإشغالاً بالمركز.\n\n" +
                            "3. المخزون والمشتريات: يوصى ببدء تحويلات المخزون لتجنب شراء كميات جديدة بـ 18,000 ريال.\n\n" +
                            "4. انضباط الكادر: نسبة الحضور اليوم 87% وهي ضمن المؤشرات الممتازة.",
                    fontSize = 12.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onOpenAiCommandCenter,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("افتح مساعد المدير الذكي لمزيد من التحليلات 💬")
                }
            }
        }
    }
}
