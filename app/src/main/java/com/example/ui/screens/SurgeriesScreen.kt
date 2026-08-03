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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Surgery
import com.example.ui.theme.StatusInfo
import com.example.ui.theme.StatusSuccess

@Composable
fun SurgeriesScreen(surgeries: List<Surgery>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "جدول غرفة العمليات والجراحة",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "تنسيق الفريق الجراحي، حجز العمليات وتتبع حالات المرضى",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(surgeries) { surg ->
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
                            Column {
                                Text(text = surg.surgeryType, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(text = "المريض: ${surg.patientName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (surg.status == "مكتملة") StatusSuccess.copy(alpha = 0.15f) else StatusInfo.copy(alpha = 0.15f)
                            ) {
                                Text(text = surg.status, color = if (surg.status == "مكتملة") StatusSuccess else StatusInfo, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "• الجراح الرئيسي: ${surg.surgeonName}", fontSize = 12.sp)
                        Text(text = "• الفريق الطبي: ${surg.medicalTeam}", fontSize = 12.sp)
                        Text(text = "• الموعد والقاعة: ${surg.date} ${surg.time} (${surg.operatingRoom})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
