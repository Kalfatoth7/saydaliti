package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import com.example.data.models.Medicine
import com.example.data.models.Patient
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ui.MainViewModel
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.HealthOsTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

            HealthOsTheme(darkTheme = isDarkTheme) {
                HealthOsMainApp(
                    viewModel = viewModel,
                    isDarkTheme = isDarkTheme,
                    onToggleDarkTheme = { isDarkTheme = !isDarkTheme }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthOsMainApp(
    viewModel: MainViewModel,
    isDarkTheme: Boolean,
    onToggleDarkTheme: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "dashboard"

    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    val pharmacies by viewModel.pharmacies.collectAsStateWithLifecycle(initialValue = emptyList())
    val medicines by viewModel.medicines.collectAsStateWithLifecycle(initialValue = emptyList())
    val employees by viewModel.employees.collectAsStateWithLifecycle(initialValue = emptyList())
    val patients by viewModel.patients.collectAsStateWithLifecycle(initialValue = emptyList())
    val appointments by viewModel.appointments.collectAsStateWithLifecycle(initialValue = emptyList())
    val financials by viewModel.financialRecords.collectAsStateWithLifecycle(initialValue = emptyList())
    val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle(initialValue = emptyList())
    val surgeries by viewModel.surgeries.collectAsStateWithLifecycle(initialValue = emptyList())
    val labTests by viewModel.labTests.collectAsStateWithLifecycle(initialValue = emptyList())
    val radiologyScans by viewModel.radiologyScans.collectAsStateWithLifecycle(initialValue = emptyList())
    val medicalHistoryRecords by viewModel.medicalHistoryRecords.collectAsStateWithLifecycle(initialValue = emptyList())
    val medicationLogs by viewModel.medicationLogs.collectAsStateWithLifecycle(initialValue = emptyList())

    val conversations by viewModel.conversations.collectAsStateWithLifecycle(initialValue = emptyList())
    val announcements by viewModel.announcements.collectAsStateWithLifecycle(initialValue = emptyList())
    val tasks by viewModel.tasks.collectAsStateWithLifecycle(initialValue = emptyList())
    val selectedConvId by viewModel.selectedConversationId.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()


    var showAiModal by remember { mutableStateOf(false) }
    var showNotificationDrawer by remember { mutableStateOf(false) }
    var showBarcodeScannerModal by remember { mutableStateOf(false) }
    var showQuickAddPatient by remember { mutableStateOf(false) }
    var showQuickAddMedicine by remember { mutableStateOf(false) }
    var showQuickAddAppointment by remember { mutableStateOf(false) }
    var showQuickAddFinancial by remember { mutableStateOf(false) }
    var showQuickAddEmployee by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HealthOsSidebarDrawerContent(
                currentRoute = currentRoute,
                currentRole = currentRole,
                onNavigate = { route -> 
                    navController.navigate(route) { launchSingleTop = true; restoreState = true } 
                },
                onCloseDrawer = {
                    coroutineScope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                HealthOsTopAppBar(
                    currentRole = currentRole,
                    onRoleSelected = { viewModel.setCurrentRole(it) },
                    notificationCount = notifications.size,
                    onNotificationClick = { showNotificationDrawer = true },
                    onMessagingClick = { navController.navigate("messaging") { launchSingleTop = true; restoreState = true } },
                    onAiClick = { showAiModal = true },
                    onSearchClick = { showAiModal = true },
                    isDarkTheme = isDarkTheme,
                    onToggleDarkTheme = onToggleDarkTheme,
                    onMenuClick = {
                        coroutineScope.launch { drawerState.open() }
                    }
                )
            },

            bottomBar = {
                HealthOsBottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { route -> 
                        navController.navigate(route) { 
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true 
                        } 
                    }
                )
            },
            floatingActionButton = {
                QuickActionFab(
                    onAddPatient = { showQuickAddPatient = true },
                    onAddAppointment = { showQuickAddAppointment = true },
                    onAddMedicine = { showQuickAddMedicine = true },
                    onAddFinancial = { showQuickAddFinancial = true },
                    onAddEmployee = { showQuickAddEmployee = true },
                    onScanBarcode = { showBarcodeScannerModal = true }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                val navigateTo = { route: String -> 
                    navController.navigate(route) { launchSingleTop = true; restoreState = true } 
                }
                
                NavHost(
                    navController = navController,
                    startDestination = "dashboard"
                ) {
                    composable("dashboard") {
                        DashboardScreen(
                            pharmacies = pharmacies,
                            medicines = medicines,
                            employees = employees,
                            appointments = appointments,
                            financials = financials,
                            surgeries = surgeries,
                            labTests = labTests,
                            scans = radiologyScans,
                            onOpenAiCenter = { showAiModal = true },
                            onNavigateTo = navigateTo
                        )
                    }
                    composable("command_center") {
                        CommandCenterScreen(
                            pharmacies = pharmacies,
                            medicines = medicines,
                            employees = employees,
                            appointments = appointments,
                            financials = financials,
                            auditLogs = auditLogs,
                            onNavigateTo = navigateTo,
                            onOpenAiCenter = { showAiModal = true },
                            onResetFinancialData = { viewModel.resetFinancialIndicators("أ.د. محي الدين الجعفري (مدير المركز)") }
                        )
                    }
                    composable("messaging") {
                        MessagingScreen(
                            currentRole = currentRole,
                            conversations = conversations,
                            announcements = announcements,
                            tasks = tasks,
                            medicines = medicines,
                            patients = patients,
                            selectedConvId = selectedConvId,
                            onSelectConversation = { viewModel.selectConversation(it) },
                            getMessagesForConversation = { viewModel.getMessagesForConversation(it) },
                            onSendMessage = { viewModel.sendMessage(it) },
                            onTogglePinConv = { viewModel.togglePinConversation(it) },
                            onToggleFavConv = { viewModel.toggleFavoriteConversation(it) },
                            onAddAnnouncement = { viewModel.addAnnouncement(it) },
                            onAddTask = { viewModel.addTask(it) },
                            onUpdateTaskStatus = { id, status, name -> viewModel.updateTaskStatus(id, status, name) }
                        )
                    }
                    composable("pharmacies") {
                        PharmaciesScreen(
                            pharmacies = pharmacies,
                            medicines = medicines,
                            onTransferStock = { id, targetId, qty -> viewModel.transferStock(id, targetId, qty) },
                            onOpenMessagingWithContext = { type, id, title, sub ->
                                viewModel.openLinkedConversation(type, id, title, sub)
                            }
                        )
                    }
                    composable("reception_queue") {
                        ReceptionQueueScreen(
                            patients = patients,
                            appointments = appointments,
                            onAddPatient = { viewModel.addPatient(it) },
                            onAddAppointment = { viewModel.addAppointment(it) },
                            onUpdateAppointmentStatus = { id, status -> viewModel.updateAppointmentStatus(id, status) }
                        )
                    }
                    composable("inventory") {
                        InventoryScreen(
                            medicines = medicines,
                            pharmacies = pharmacies,
                            onAddMedicine = { viewModel.addMedicine(it) },
                            onUpdateMedicine = { viewModel.updateMedicine(it) },
                            onDeleteMedicine = { viewModel.deleteMedicine(it) },
                            onTransferStock = { id, targetId, qty -> viewModel.transferStock(id, targetId, qty) }
                        )
                    }
                    composable("smart_inventory_ai") {
                        InventoryScreen(
                            medicines = medicines,
                            pharmacies = pharmacies,
                            onAddMedicine = { viewModel.addMedicine(it) },
                            onUpdateMedicine = { viewModel.updateMedicine(it) },
                            onDeleteMedicine = { viewModel.deleteMedicine(it) },
                            onTransferStock = { id, targetId, qty -> viewModel.transferStock(id, targetId, qty) }
                        )
                    }
                    composable("financials") {
                        FinancialsScreen(
                            financials = financials,
                            pharmacies = pharmacies,
                            onAddFinancialRecord = { viewModel.addFinancialRecord(it) },
                            onResetFinancials = { viewModel.resetFinancialIndicators() }
                        )
                    }
                    composable("employees") {
                        EmployeesScreen(
                            employees = employees,
                            onAddEmployee = { viewModel.addEmployee(it) },
                            onUpdateAttendance = { emp, status -> viewModel.updateEmployeeAttendance(emp, status) }
                        )
                    }
                    composable("departments") {
                        DepartmentsScreen(
                            employees = employees,
                            appointments = appointments
                        )
                    }
                    composable("patients") {
                        PatientsScreen(
                            patients = patients,
                            onAddPatient = { viewModel.addPatient(it) }
                        )
                    }
                    composable("patient_dashboard") {
                        PatientDashboardScreen(
                            patients = patients,
                            medicalHistoryRecords = medicalHistoryRecords,
                            appointments = appointments,
                            medicationLogs = medicationLogs,
                            onAddMedicalHistoryRecord = { viewModel.addMedicalHistoryRecord(it) },
                            onAddMedicationLog = { viewModel.addMedicationLog(it) },
                            onUpdateMedicationStatus = { id, status, time -> viewModel.updateMedicationLogStatus(id, status, time) },
                            onAddAppointment = { viewModel.addAppointment(it) }
                        )
                    }
                    composable("appointments") {
                        AppointmentsScreen(
                            appointments = appointments,
                            onAddAppointment = { viewModel.addAppointment(it) },
                            onUpdateStatus = { id, status -> viewModel.updateAppointmentStatus(id, status) }
                        )
                    }
                    composable("lab_scans") {
                        LabRadiologyScreen(
                            labTests = labTests,
                            radiologyScans = radiologyScans
                        )
                    }
                    composable("surgeries") {
                        SurgeriesScreen(
                            surgeries = surgeries
                        )
                    }
                    composable("reports") {
                        ReportsScreen(
                            pharmacies = pharmacies,
                            medicines = medicines,
                            employees = employees,
                            financials = financials
                        )
                    }
                    composable("audit_logs") {
                        AuditLogsScreen(
                            currentRole = currentRole,
                            auditLogs = auditLogs,
                            onRoleSelected = { viewModel.setCurrentRole(it) }
                        )
                    }
                    composable("ai_insights") {
                        AiInsightsScreen(
                            pharmacies = pharmacies,
                            onOpenAiCommandCenter = { showAiModal = true }
                        )
                    }
                }
            }
        }
    }

    // AI Command Center Dialog Modal
    if (showAiModal) {
        AiCommandCenterModal(
            pharmacies = pharmacies,
            medicines = medicines,
            employees = employees,
            appointments = appointments,
            financials = financials,
            onDismiss = { showAiModal = false }
        )
    }

    // Barcode Scanner Modal
    if (showBarcodeScannerModal) {
        BarcodeScannerModal(
            medicines = medicines,
            pharmacies = pharmacies,
            onDismiss = { showBarcodeScannerModal = false },
            onSellMedicine = { med, qty ->
                viewModel.updateMedicine(med.copy(stockQuantity = (med.stockQuantity - qty).coerceAtLeast(0)))
            },
            onTransferStock = { id, targetId, qty ->
                viewModel.transferStock(id, targetId, qty)
            }
        )
    }

    // Notifications Drawer Sheet
    if (showNotificationDrawer) {
        NotificationDrawer(
            notifications = notifications,
            onDismissItem = { viewModel.dismissNotification(it) },
            onDismissDrawer = { showNotificationDrawer = false }
        )
    }

    // Quick Add Modals
    if (showQuickAddPatient) {
        AddPatientModal(
            onDismiss = { showQuickAddPatient = false },
            onSave = { p ->
                viewModel.addPatient(p)
                showQuickAddPatient = false
            }
        )
    }

    if (showQuickAddMedicine) {
        AddEditMedicineModal(
            pharmacies = pharmacies,
            onDismiss = { showQuickAddMedicine = false },
            onSave = { med ->
                viewModel.addMedicine(med)
                showQuickAddMedicine = false
            }
        )
    }

    if (showQuickAddAppointment) {
        AddAppointmentModal(
            onDismiss = { showQuickAddAppointment = false },
            onSave = { appt ->
                viewModel.addAppointment(appt)
                showQuickAddAppointment = false
            }
        )
    }

    if (showQuickAddFinancial) {
        AddFinancialRecordModal(
            onDismiss = { showQuickAddFinancial = false },
            onSave = { record ->
                viewModel.addFinancialRecord(record)
                showQuickAddFinancial = false
            }
        )
    }

    if (showQuickAddEmployee) {
        AddEmployeeModal(
            onDismiss = { showQuickAddEmployee = false },
            onSave = { emp ->
                viewModel.addEmployee(emp)
                showQuickAddEmployee = false
            }
        )
    }
}
// Trigger redeploy
