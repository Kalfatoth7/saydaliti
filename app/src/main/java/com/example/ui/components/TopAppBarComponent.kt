package com.example.ui.components

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.UserRole
import com.example.ui.theme.MedicalTealContainer
import com.example.ui.theme.MedicalTealOnContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthOsTopAppBar(
    currentRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    notificationCount: Int,
    onNotificationClick: () -> Unit,
    onMessagingClick: () -> Unit,
    onAiClick: () -> Unit,
    onSearchClick: () -> Unit,
    isDarkTheme: Boolean,
    onToggleDarkTheme: () -> Unit,
    onMenuClick: (() -> Unit)? = null
) {
    var roleDropdownExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        navigationIcon = {
            if (onMenuClick != null) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "القائمة الجانبية",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onRoleSelected(currentRole) }
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LocalHospital,
                            contentDescription = "Health OS Logo",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "مركز الرحمة الطبي",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        text = "النظام الطبي الشامل والذكي",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        actions = {
            // Quick Messaging Trigger Button
            IconButton(onClick = onMessagingClick) {
                BadgedBox(
                    badge = {
                        Badge {
                            Text("3")
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "المراسلات والدردشة الداخليّة",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Quick Search Button
            IconButton(onClick = onSearchClick) {

                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "البحث السريع",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // AI Command Center Trigger
            IconButton(
                onClick = onAiClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MedicalTealContainer)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "مساعد المدير الذكي AI",
                    tint = MedicalTealOnContainer
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Notifications Bell
            IconButton(onClick = onNotificationClick) {
                BadgedBox(
                    badge = {
                        if (notificationCount > 0) {
                            Badge {
                                Text(notificationCount.toString())
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "الإشعارات",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Dark Mode Toggle
            IconButton(onClick = onToggleDarkTheme) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "الوضع الداكن/الفاتح",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // Role Switcher Dropdown Button
            Box {
                FilterChip(
                    selected = true,
                    onClick = { roleDropdownExpanded = true },
                    label = {
                        Text(
                            text = currentRole.arabicName.split(" ")[0],
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    modifier = Modifier.padding(end = 8.dp)
                )

                DropdownMenu(
                    expanded = roleDropdownExpanded,
                    onDismissRequest = { roleDropdownExpanded = false }
                ) {
                    Text(
                        text = "تبديل صلاحية المستخدم (معاينة):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                    HorizontalDivider()
                    UserRole.entries.forEach { role ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = role.arabicName,
                                    fontSize = 13.sp,
                                    fontWeight = if (role == currentRole) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onRoleSelected(role)
                                roleDropdownExpanded = false
                            },
                            leadingIcon = {
                                if (role == currentRole) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}
