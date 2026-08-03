package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.data.models.Medicine
import com.example.data.models.Pharmacy
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    medicines: List<Medicine>,
    pharmacies: List<Pharmacy>,
    onAddMedicine: (Medicine) -> Unit,
    onUpdateMedicine: (Medicine) -> Unit,
    onDeleteMedicine: (Medicine) -> Unit,
    onTransferStock: (Long, String, Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, LOW_STOCK, EXPIRING_FEFO, WAREHOUSE
    var showAddDialog by remember { mutableStateOf(false) }
    var transferDialogMed by remember { mutableStateOf<Medicine?>(null) }

    // FEFO sorting: First Expired First Out
    val filteredMedicines = medicines.filter { med ->
        val matchesSearch = med.tradeName.contains(searchQuery, ignoreCase = true) ||
                med.genericName.contains(searchQuery, ignoreCase = true) ||
                med.batchNumber.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "LOW_STOCK" -> med.stockQuantity <= med.minStockAlert
            "EXPIRING_FEFO" -> med.expiryDate.startsWith("2026-08") || med.expiryDate.startsWith("2026-09")
            "WAREHOUSE" -> med.pharmacyId == "central_warehouse"
            else -> true
        }
        matchesSearch && matchesFilter
    }.sortedBy { it.expiryDate } // FEFO principle

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Action Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "إدارة الأدوية والمخزون الموحد",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "نظام FEFO (الأقرب للانتهاء أولاً) • المستودع والصيدليات",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "إضافة دواء", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("بحث باسم الدواء التجاري، العلمي أو رقم التشغيلة Batch...") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "مسح")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Chips Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FilterChip(
                selected = selectedFilter == "ALL",
                onClick = { selectedFilter = "ALL" },
                label = { Text("جميع الأدوية (${medicines.size})") }
            )
            FilterChip(
                selected = selectedFilter == "LOW_STOCK",
                onClick = { selectedFilter = "LOW_STOCK" },
                label = { Text("منخفض المخزون") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = StatusError,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = selectedFilter == "EXPIRING_FEFO",
                onClick = { selectedFilter = "EXPIRING_FEFO" },
                label = { Text("⏳ FEFO قريب الانتهاء") }
            )
            FilterChip(
                selected = selectedFilter == "WAREHOUSE",
                onClick = { selectedFilter = "WAREHOUSE" },
                label = { Text("المستودع الرئيسي") }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Medicines List
        if (filteredMedicines.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Inbox,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "لا توجد أدوية مطابقة لمعايير البحث", fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredMedicines, key = { it.id }) { med ->
                    MedicineCardItem(
                        medicine = med,
                        pharmacies = pharmacies,
                        onTransferClick = { transferDialogMed = med },
                        onDeleteClick = { onDeleteMedicine(med) }
                    )
                }
            }
        }
    }

    // Transfer Stock Dialog
    transferDialogMed?.let { med ->
        TransferStockModal(
            medicine = med,
            pharmacies = pharmacies,
            onDismiss = { transferDialogMed = null },
            onConfirmTransfer = { targetPhId, qty ->
                onTransferStock(med.id, targetPhId, qty)
                transferDialogMed = null
            }
        )
    }

    // Add Medicine Dialog
    if (showAddDialog) {
        AddEditMedicineModal(
            pharmacies = pharmacies,
            onDismiss = { showAddDialog = false },
            onSave = { newMed ->
                onAddMedicine(newMed)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun MedicineCardItem(
    medicine: Medicine,
    pharmacies: List<Pharmacy>,
    onTransferClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val isLowStock = medicine.stockQuantity <= medicine.minStockAlert
    val locName = pharmacies.find { it.id == medicine.pharmacyId }?.name ?: medicine.pharmacyId

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLowStock) StatusError.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = medicine.tradeName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${medicine.genericName} • ${medicine.concentration} (${medicine.dosageForm})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isLowStock) StatusError else StatusSuccess
                ) {
                    Text(
                        text = "${medicine.stockQuantity} عبوة",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "الموقع: $locName", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                Text(text = "الانتهاء: ${medicine.expiryDate}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = "سعر البيع: ${medicine.sellingPrice} ر.س", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onTransferClick,
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Icon(imageVector = Icons.Default.SyncAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "نقل مخزون", fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun TransferStockModal(
    medicine: Medicine,
    pharmacies: List<Pharmacy>,
    onDismiss: () -> Unit,
    onConfirmTransfer: (String, Int) -> Unit
) {
    var selectedTargetId by remember { mutableStateOf("pharmacy_1") }
    var transferQty by remember { mutableStateOf("10") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("نقل مخزون: ${medicine.tradeName}") },
        text = {
            Column {
                Text(text = "الكمية المتوفرة حالياً: ${medicine.stockQuantity} عبوة", fontSize = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "اختر الصيدلية أو المستودع المحول إليه:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                pharmacies.filter { it.id != medicine.pharmacyId }.forEach { ph ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedTargetId = ph.id }
                    ) {
                        RadioButton(
                            selected = selectedTargetId == ph.id,
                            onClick = { selectedTargetId = ph.id }
                        )
                        Text(text = ph.name, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = transferQty,
                    onValueChange = { transferQty = it },
                    label = { Text("الكمية المراد نقلها") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val q = transferQty.toIntOrNull() ?: 1
                    onConfirmTransfer(selectedTargetId, q)
                }
            ) {
                Text("تأكيد النقل")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMedicineModal(
    pharmacies: List<Pharmacy>,
    onDismiss: () -> Unit,
    onSave: (Medicine) -> Unit
) {
    var tradeName by remember { mutableStateOf("") }
    var genericName by remember { mutableStateOf("") }
    var concentration by remember { mutableStateOf("500mg") }
    var dosageForm by remember { mutableStateOf("أقراص") }
    var company by remember { mutableStateOf("الشركة العامة للأدوية") }
    var stockQuantity by remember { mutableStateOf("50") }
    var costPrice by remember { mutableStateOf("15") }
    var sellingPrice by remember { mutableStateOf("25") }
    var expiryDate by remember { mutableStateOf("2027-12-31") }
    var batchNumber by remember { mutableStateOf("BATCH-2026") }
    var selectedPharmacyId by remember { mutableStateOf("pharmacy_1") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "إضافة دواء جديد إلى المخزون", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(value = tradeName, onValueChange = { tradeName = it }, label = { Text("الاسم التجاري") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = genericName, onValueChange = { genericName = it }, label = { Text("الاسم العلمي") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = concentration, onValueChange = { concentration = it }, label = { Text("التركيز") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = dosageForm, onValueChange = { dosageForm = it }, label = { Text("الشكل الدوائي") }, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = stockQuantity, onValueChange = { stockQuantity = it }, label = { Text("الكمية") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = sellingPrice, onValueChange = { sellingPrice = it }, label = { Text("سعر البيع") }, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (tradeName.isNotBlank()) {
                        val med = Medicine(
                            tradeName = tradeName,
                            genericName = genericName,
                            concentration = concentration,
                            dosageForm = dosageForm,
                            company = company,
                            stockQuantity = stockQuantity.toIntOrNull() ?: 10,
                            costPrice = costPrice.toDoubleOrNull() ?: 10.0,
                            sellingPrice = sellingPrice.toDoubleOrNull() ?: 15.0,
                            expiryDate = expiryDate,
                            batchNumber = batchNumber,
                            supplier = "المورد الرئيسي",
                            storageLocation = "رف B1",
                            pharmacyId = selectedPharmacyId
                        )
                        onSave(med)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("حفظ الدواء")
            }
        }
    }
}
