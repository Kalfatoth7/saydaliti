package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.ui.theme.AlRahmaGoldAccent
import com.example.ui.theme.AlRahmaPrimary

@Composable
fun QuickActionFab(
    onAddPatient: () -> Unit,
    onAddAppointment: () -> Unit,
    onAddMedicine: () -> Unit,
    onAddFinancial: () -> Unit,
    onAddEmployee: () -> Unit,
    onScanBarcode: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionItem(
                    label = "مسح باركود دواء",
                    icon = Icons.Default.QrCodeScanner,
                    iconTint = AlRahmaGoldAccent,
                    onClick = {
                        expanded = false
                        onScanBarcode()
                    }
                )
                QuickActionItem(
                    label = "إضافة مريض جديد",
                    icon = Icons.Default.PersonAdd,
                    onClick = {
                        expanded = false
                        onAddPatient()
                    }
                )
                QuickActionItem(
                    label = "حجز موعد جديد",
                    icon = Icons.Default.Event,
                    onClick = {
                        expanded = false
                        onAddAppointment()
                    }
                )
                QuickActionItem(
                    label = "إضافة دواء للمخزون",
                    icon = Icons.Default.Medication,
                    onClick = {
                        expanded = false
                        onAddMedicine()
                    }
                )
                QuickActionItem(
                    label = "إضافة مصروف/إيراد",
                    icon = Icons.Default.ReceiptLong,
                    onClick = {
                        expanded = false
                        onAddFinancial()
                    }
                )
                QuickActionItem(
                    label = "إضافة موظف جديد",
                    icon = Icons.Default.Badge,
                    onClick = {
                        expanded = false
                        onAddEmployee()
                    }
                )
            }
        }

        ExtendedFloatingActionButton(
            text = {
                Text(
                    text = if (expanded) "إغلاق" else "عمليات سريعة +",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            },
            icon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = null
                )
            },
            onClick = { expanded = !expanded },
            containerColor = AlRahmaPrimary,
            contentColor = Color.White
        )
    }
}

@Composable
fun QuickActionItem(
    label: String,
    icon: ImageVector,
    iconTint: Color = AlRahmaPrimary,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = iconTint
        ) {
            Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(20.dp))
        }
    }
}
