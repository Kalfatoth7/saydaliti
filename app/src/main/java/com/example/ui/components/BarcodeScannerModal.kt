package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.ui.theme.AlRahmaGoldAccent
import com.example.ui.theme.AlRahmaPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeScannerModal(
    medicines: List<Medicine>,
    pharmacies: List<Pharmacy>,
    onDismiss: () -> Unit,
    onSellMedicine: (Medicine, Int) -> Unit,
    onTransferStock: (Long, String, Int) -> Unit
) {
    val context = LocalContext.current
    var scannedBarcode by remember { mutableStateOf("GSK-99812") }
    var matchedMedicine by remember {
        mutableStateOf(medicines.find { it.batchNumber.contains(scannedBarcode, ignoreCase = true) } ?: medicines.firstOrNull())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = AlRahmaPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("ماسح الباركود والتعرف الذكي على الأدوية", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Simulated Camera View Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = AlRahmaGoldAccent,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("الكاميرا نشطة - وُضِع الباركود في الإطار", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }

                OutlinedTextField(
                    value = scannedBarcode,
                    onValueChange = { code ->
                        scannedBarcode = code
                        matchedMedicine = medicines.find {
                            it.batchNumber.contains(code, ignoreCase = true) || it.tradeName.contains(code, ignoreCase = true)
                        }
                    },
                    label = { Text("رمز الباركود / Batch No") },
                    leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (matchedMedicine != null) {
                    val med = matchedMedicine!!
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = med.tradeName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                text = "المادة الفعالة: ${med.genericName} • ${med.concentration}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "المخزون المتاح: ${med.stockQuantity} عبوة", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text(text = "سعر البيع: ${med.sellingPrice} ج.م", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = AlRahmaPrimary)
                            }
                            Text(
                                text = "تاريخ الانتهاء (FEFO): ${med.expiryDate} • التشغيلة: ${med.batchNumber}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Text("لم يتم العثور على دواء بهذا الرمز", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            if (matchedMedicine != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            Toast.makeText(context, "تم تسجيل بيع عبوة واحدة من ${matchedMedicine!!.tradeName}", Toast.LENGTH_SHORT).show()
                            onSellMedicine(matchedMedicine!!, 1)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AlRahmaPrimary)
                    ) {
                        Text("إتمام البيع المباشر")
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إغلاق") }
        }
    )
}
