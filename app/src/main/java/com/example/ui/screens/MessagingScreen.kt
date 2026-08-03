package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagingScreen(
    currentRole: UserRole,
    conversations: List<Conversation>,
    announcements: List<InternalAnnouncement>,
    tasks: List<TaskItem>,
    medicines: List<Medicine>,
    patients: List<Patient>,
    selectedConvId: String?,
    onSelectConversation: (String?) -> Unit,
    getMessagesForConversation: (String) -> kotlinx.coroutines.flow.Flow<List<ChatMessage>>,
    onSendMessage: (ChatMessage) -> Unit,
    onTogglePinConv: (String) -> Unit,
    onToggleFavConv: (String) -> Unit,
    onAddAnnouncement: (InternalAnnouncement) -> Unit,
    onAddTask: (TaskItem) -> Unit,
    onUpdateTaskStatus: (Long, String, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterTab by remember { mutableIntStateOf(0) } // 0: الكل, 1: غير مقروءة, 2: المفضلة, 3: المجموعات, 4: المثبتة, 5: الإعلانات, 6: المهام
    var showNewAnnouncementModal by remember { mutableStateOf(false) }
    var showNewGroupModal by remember { mutableStateOf(false) }

    // Find active conversation object
    val activeConversation = conversations.find { it.id == selectedConvId }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth > 800.dp

        if (isWideScreen) {
            // Tablet / Desktop Layout: 2 or 3 Column Layout
            Row(modifier = Modifier.fillMaxSize()) {
                // Left Pane: Conversations List & Search (Width 320dp)
                Surface(
                    modifier = Modifier
                        .width(320.dp)
                        .fillMaxHeight(),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp
                ) {
                    ConversationsListPane(
                        currentRole = currentRole,
                        conversations = conversations,
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        selectedFilterTab = selectedFilterTab,
                        onFilterTabSelect = { selectedFilterTab = it },
                        selectedConvId = selectedConvId,
                        onSelectConv = onSelectConversation,
                        onNewAnnouncementClick = { showNewAnnouncementModal = true },
                        onNewGroupClick = { showNewGroupModal = true }
                    )
                }

                VerticalDivider()

                // Center Pane: Active Chat / Announcement / Task View
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    when {
                        selectedFilterTab == 5 -> AnnouncementsPane(
                            announcements = announcements,
                            currentRole = currentRole,
                            onNewAnnouncement = { showNewAnnouncementModal = true }
                        )
                        selectedFilterTab == 6 -> TasksPane(
                            tasks = tasks,
                            currentRole = currentRole,
                            onUpdateStatus = onUpdateTaskStatus
                        )
                        activeConversation != null -> ChatRoomPane(
                            conversation = activeConversation,
                            currentRole = currentRole,
                            medicines = medicines,
                            patients = patients,
                            messagesFlow = getMessagesForConversation(activeConversation.id),
                            onSendMessage = onSendMessage,
                            onTogglePin = { onTogglePinConv(activeConversation.id) },
                            onToggleFav = { onToggleFavConv(activeConversation.id) },
                            onAddTask = onAddTask,
                            onBack = { onSelectConversation(null) }
                        )
                        else -> EmptyChatPlaceholder()
                    }
                }
            }
        } else {
            // Mobile Layout: Switch between List and Active Chat
            if (selectedConvId != null && activeConversation != null && selectedFilterTab != 5 && selectedFilterTab != 6) {
                ChatRoomPane(
                    conversation = activeConversation,
                    currentRole = currentRole,
                    medicines = medicines,
                    patients = patients,
                    messagesFlow = getMessagesForConversation(activeConversation.id),
                    onSendMessage = onSendMessage,
                    onTogglePin = { onTogglePinConv(activeConversation.id) },
                    onToggleFav = { onToggleFavConv(activeConversation.id) },
                    onAddTask = onAddTask,
                    onBack = { onSelectConversation(null) }
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    ConversationsListPane(
                        currentRole = currentRole,
                        conversations = conversations,
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        selectedFilterTab = selectedFilterTab,
                        onFilterTabSelect = { selectedFilterTab = it },
                        selectedConvId = selectedConvId,
                        onSelectConv = onSelectConversation,
                        onNewAnnouncementClick = { showNewAnnouncementModal = true },
                        onNewGroupClick = { showNewGroupModal = true }
                    )

                    if (selectedFilterTab == 5) {
                        AnnouncementsPane(
                            announcements = announcements,
                            currentRole = currentRole,
                            onNewAnnouncement = { showNewAnnouncementModal = true }
                        )
                    } else if (selectedFilterTab == 6) {
                        TasksPane(
                            tasks = tasks,
                            currentRole = currentRole,
                            onUpdateStatus = onUpdateTaskStatus
                        )
                    }
                }
            }
        }
    }

    if (showNewAnnouncementModal) {
        CreateAnnouncementModal(
            onDismiss = { showNewAnnouncementModal = false },
            onSave = { ann ->
                onAddAnnouncement(ann)
                showNewAnnouncementModal = false
            }
        )
    }

    if (showNewGroupModal) {
        CreateGroupModal(
            currentRole = currentRole,
            onDismiss = { showNewGroupModal = false },
            onCreate = { title, desc, members ->
                showNewGroupModal = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationsListPane(
    currentRole: UserRole,
    conversations: List<Conversation>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedFilterTab: Int,
    onFilterTabSelect: (Int) -> Unit,
    selectedConvId: String?,
    onSelectConv: (String) -> Unit,
    onNewAnnouncementClick: () -> Unit,
    onNewGroupClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AlRahmaPrimary,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("المراسلات الداخلية", fontWeight = FontWeight.Black, fontSize = 16.sp)
                    Text("شبكة التواصل الآمنة لمركز الرحمة", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (currentRole == UserRole.SUPER_ADMIN || currentRole == UserRole.MANAGER || currentRole == UserRole.OWNER) {
                IconButton(onClick = onNewAnnouncementClick) {
                    Icon(Icons.Default.Campaign, contentDescription = "إعلان جديد", tint = AlRahmaGoldAccent)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Input Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("بحث في الرسائل والمحادثات...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Horizontal Filter Chips Bar
        ScrollableTabRow(
            selectedTabIndex = selectedFilterTab,
            edgePadding = 0.dp,
            divider = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            val tabs = listOf("الكل", "غير مقروءة", "المفضلة ⭐", "المجموعات 👥", "المثبتة 📌", "📢 الإعلانات", "📋 المهام")
            tabs.forEachIndexed { index, label ->
                Tab(
                    selected = selectedFilterTab == index,
                    onClick = { onFilterTabSelect(index) },
                    text = {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (selectedFilterTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Filter conversations list
        val filteredConversations = conversations.filter { conv ->
            val matchesSearch = conv.title.contains(searchQuery, ignoreCase = true) ||
                    conv.lastMessage.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (selectedFilterTab) {
                1 -> conv.unreadCount > 0
                2 -> conv.isFavorite
                3 -> conv.type == "GROUP"
                4 -> conv.isPinned
                else -> true
            }
            matchesSearch && matchesFilter
        }

        if (selectedFilterTab != 5 && selectedFilterTab != 6) {
            if (filteredConversations.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "لا توجد محادثات تطابق البحث" else "لا توجد محادثات حالياً",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredConversations) { conv ->
                        ConversationCardItem(
                            conversation = conv,
                            isSelected = conv.id == selectedConvId,
                            onClick = { onSelectConv(conv.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationCardItem(
    conversation: Conversation,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar & Online Presence Dot
            Box {
                Surface(
                    shape = CircleShape,
                    color = when (conversation.type) {
                        "GROUP" -> AlRahmaPrimary
                        "LINKED_WORK" -> AlRahmaGoldAccent
                        "LINKED_PATIENT" -> StatusWarning
                        else -> MaterialTheme.colorScheme.secondary
                    },
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = conversation.avatarInitials.ifBlank { conversation.title.take(2) },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
                // Presence Status Indicator
                Surface(
                    shape = CircleShape,
                    color = when (conversation.presenceStatus) {
                        PresenceStatus.ONLINE -> StatusSuccess
                        PresenceStatus.BUSY -> StatusWarning
                        else -> Color.Gray
                    },
                    modifier = Modifier
                        .size(10.dp)
                        .align(Alignment.BottomEnd)
                ) {}
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text(
                            text = conversation.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (conversation.isPinned) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.PushPin, contentDescription = "مثبتة", tint = AlRahmaGoldAccent, modifier = Modifier.size(12.dp))
                        }
                    }
                    Text(
                        text = conversation.lastMessageTime,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.lastMessage,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (conversation.unreadCount > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Badge(
                            containerColor = AlRahmaPrimary,
                            contentColor = Color.White
                        ) {
                            Text("${conversation.unreadCount}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomPane(
    conversation: Conversation,
    currentRole: UserRole,
    medicines: List<Medicine>,
    patients: List<Patient>,
    messagesFlow: kotlinx.coroutines.flow.Flow<List<ChatMessage>>,
    onSendMessage: (ChatMessage) -> Unit,
    onTogglePin: () -> Unit,
    onToggleFav: () -> Unit,
    onAddTask: (TaskItem) -> Unit,
    onBack: () -> Unit
) {
    val messages by messagesFlow.collectAsState(initial = emptyList())
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var textInput by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(MessagePriority.NORMAL) }
    var replyToMessage by remember { mutableStateOf<ChatMessage?>(null) }
    var showAiSummaryModal by remember { mutableStateOf(false) }
    var showAttachmentPicker by remember { mutableStateOf(false) }
    var isRecordingVoice by remember { mutableStateOf(false) }
    var recordingTimeSeconds by remember { mutableIntStateOf(0) }

    // Scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Chat Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                }

                Surface(
                    shape = CircleShape,
                    color = AlRahmaPrimary,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = conversation.avatarInitials.ifBlank { conversation.title.take(2) },
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = conversation.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = if (conversation.presenceStatus == PresenceStatus.ONLINE) StatusSuccess else Color.Gray,
                            modifier = Modifier.size(8.dp)
                        ) {}
                    }
                    Text(text = conversation.subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                IconButton(onClick = { showAiSummaryModal = true }) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "تلخيص AI", tint = AlRahmaGoldAccent)
                }

                IconButton(onClick = onTogglePin) {
                    Icon(
                        imageVector = if (conversation.isPinned) Icons.Default.PushPin else Icons.Default.PushPin,
                        contentDescription = "تثبيت",
                        tint = if (conversation.isPinned) AlRahmaGoldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Pinned Message Bar if exists
        if (conversation.pinnedMessage.isNotBlank()) {
            Surface(
                color = AlRahmaGoldContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.PushPin, contentDescription = null, tint = AlRahmaOnGoldContainer, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = conversation.pinnedMessage,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AlRahmaOnGoldContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                ChatMessageBubble(
                    message = msg,
                    currentRole = currentRole,
                    onReply = { replyToMessage = msg },
                    onConvertToTask = {
                        val task = TaskItem(
                            sourceMessageId = msg.id,
                            title = "مهمة من محادثة: ${msg.content.take(30)}",
                            description = msg.content,
                            assigneeName = "مسؤول القسم",
                            assigneeRole = msg.senderRole,
                            creatorName = currentRole.arabicName,
                            priority = msg.priority.arabicLabel,
                            dueDate = "اليوم"
                        )
                        onAddTask(task)
                    }
                )
            }
        }

        // Reply Preview Bar
        if (replyToMessage != null) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Reply, contentDescription = null, tint = AlRahmaPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("الرد على: ${replyToMessage!!.senderName}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AlRahmaPrimary)
                        Text(replyToMessage!!.content, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    IconButton(onClick = { replyToMessage = null }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "إلغاء الرد", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Input Field Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                // Priority Bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Text("درجة الأولوية:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    FilterChip(
                        selected = selectedPriority == MessagePriority.NORMAL,
                        onClick = { selectedPriority = MessagePriority.NORMAL },
                        label = { Text("عادي", fontSize = 10.sp) },
                        modifier = Modifier.height(26.dp)
                    )
                    FilterChip(
                        selected = selectedPriority == MessagePriority.IMPORTANT,
                        onClick = { selectedPriority = MessagePriority.IMPORTANT },
                        label = { Text("مهم 🟡", fontSize = 10.sp) },
                        modifier = Modifier.height(26.dp)
                    )
                    FilterChip(
                        selected = selectedPriority == MessagePriority.URGENT,
                        onClick = { selectedPriority = MessagePriority.URGENT },
                        label = { Text("عاجل 🔴", fontSize = 10.sp) },
                        modifier = Modifier.height(26.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { showAttachmentPicker = true }) {
                        Icon(Icons.Default.AttachFile, contentDescription = "إرفاق", tint = AlRahmaPrimary)
                    }

                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("اكتب رسالتك... (@ للإشارة)", fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp),
                        shape = RoundedCornerShape(20.dp),
                        maxLines = 3
                    )

                    IconButton(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                val msg = ChatMessage(
                                    conversationId = conversation.id,
                                    senderId = "curr_user",
                                    senderName = "أ.د. محي الدين الجعفري",
                                    senderRole = currentRole.arabicName,
                                    content = textInput,
                                    timestamp = "الآن",
                                    priority = selectedPriority,
                                    replyToSender = replyToMessage?.senderName ?: "",
                                    replyToText = replyToMessage?.content ?: "",
                                    linkedEntityType = conversation.linkedEntityType,
                                    linkedEntityId = conversation.linkedEntityId
                                )
                                onSendMessage(msg)
                                textInput = ""
                                replyToMessage = null
                            }
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(AlRahmaPrimary)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "إرسال", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }

    if (showAiSummaryModal) {
        AiChatSummaryModal(
            conversation = conversation,
            messages = messages,
            onDismiss = { showAiSummaryModal = false }
        )
    }

    if (showAttachmentPicker) {
        AttachmentPickerModal(
            medicines = medicines,
            patients = patients,
            onDismiss = { showAttachmentPicker = false },
            onSendAttachment = { type, title, subtitle ->
                val msg = ChatMessage(
                    conversationId = conversation.id,
                    senderId = "curr_user",
                    senderName = "أ.د. محي الدين الجعفري",
                    senderRole = currentRole.arabicName,
                    messageType = MessageType.LINKED_ENTITY,
                    content = "مرفق مرتبط بـ $title",
                    linkedEntityType = type,
                    linkedEntityTitle = title,
                    linkedEntitySubtitle = subtitle,
                    timestamp = "الآن"
                )
                onSendMessage(msg)
                showAttachmentPicker = false
            }
        )
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    currentRole: UserRole,
    onReply: () -> Unit,
    onConvertToTask: () -> Unit
) {
    val isOutgoing = message.senderName.contains("محي الدين") || message.senderId == "curr_user"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isOutgoing) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (isOutgoing) 14.dp else 2.dp,
                bottomEnd = if (isOutgoing) 2.dp else 14.dp
            ),
            color = if (isOutgoing) AlRahmaPrimary else MaterialTheme.colorScheme.surface,
            contentColor = if (isOutgoing) Color.White else MaterialTheme.colorScheme.onSurface,
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                // Sender Name if Group & Incoming
                if (!isOutgoing) {
                    Text(
                        text = "${message.senderName} (${message.senderRole})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = AlRahmaGoldAccent
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                // Priority Badge if Urgent/Important
                if (message.priority != MessagePriority.NORMAL) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (message.priority == MessagePriority.URGENT) MaterialTheme.colorScheme.error else AlRahmaGoldAccent,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Text(
                            text = message.priority.arabicLabel,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Quoted Reply Box if exists
                if (message.replyToText.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = (if (isOutgoing) Color.White else AlRahmaPrimary).copy(alpha = 0.15f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(6.dp)) {
                            Text(message.replyToSender, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(message.replyToText, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }

                // Message Main Text Content
                Text(
                    text = message.content,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                // Linked Work or Patient Entity Widget if attached
                if (message.linkedEntityTitle.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = (if (isOutgoing) Color.White else AlRahmaPrimary).copy(alpha = 0.2f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (message.linkedEntityType == "PATIENT") Icons.Default.Person else Icons.Default.Medication,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(message.linkedEntityTitle, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text(message.linkedEntitySubtitle, fontSize = 10.sp)
                            }
                        }
                    }
                }

                // PDF Attachment Card if exists
                if (message.attachmentName.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = (if (isOutgoing) Color.White else AlRahmaPrimary).copy(alpha = 0.2f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(message.attachmentName, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text(message.attachmentSize, fontSize = 9.sp)
                            }
                            Icon(Icons.Default.Download, contentDescription = "تنزيل", modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Bottom Metadata (Timestamp, Ticks, Actions)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = message.timestamp,
                            fontSize = 9.sp,
                            color = (if (isOutgoing) Color.White else MaterialTheme.colorScheme.onSurface).copy(alpha = 0.7f)
                        )
                        if (isOutgoing) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("✓✓", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AlRahmaGoldAccent)
                        }
                    }

                    Row {
                        Icon(
                            imageVector = Icons.Default.Reply,
                            contentDescription = "رد",
                            modifier = Modifier
                                .size(14.dp)
                                .clickable { onReply() }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.AddTask,
                            contentDescription = "تحويل لمهمة",
                            modifier = Modifier
                                .size(14.dp)
                                .clickable { onConvertToTask() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnnouncementsPane(
    announcements: List<InternalAnnouncement>,
    currentRole: UserRole,
    onNewAnnouncement: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("الإعلانات والتعميمات الداخلية", fontWeight = FontWeight.Black, fontSize = 18.sp)
                Text("التعليمات الإدارية الرسمية والتنبيهات الموجهة للكادر", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (currentRole == UserRole.SUPER_ADMIN || currentRole == UserRole.MANAGER || currentRole == UserRole.OWNER) {
                Button(
                    onClick = onNewAnnouncement,
                    colors = ButtonDefaults.buttonColors(containerColor = AlRahmaPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إعلان جديد", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(announcements) { ann ->
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
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (ann.isUrgent) MaterialTheme.colorScheme.error.copy(alpha = 0.15f) else AlRahmaPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "موجه إلى: ${ann.targetAudience}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (ann.isUrgent) MaterialTheme.colorScheme.error else AlRahmaPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(ann.date, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(ann.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(ann.content, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 18.sp)

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = AlRahmaGoldAccent, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("توقيع: ${ann.senderName} (${ann.senderRole})", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = AlRahmaPrimary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TasksPane(
    tasks: List<TaskItem>,
    currentRole: UserRole,
    onUpdateStatus: (Long, String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("المهام المحولة من المراسلات", fontWeight = FontWeight.Black, fontSize = 18.sp)
                Text("تتبع تنفيذ التوجيهات، المواعيد النهائية، والمسؤوليات", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(tasks) { task ->
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
                            Text(task.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (task.status) {
                                    "مكتملة" -> StatusSuccess.copy(alpha = 0.15f)
                                    "قيد التنفيذ" -> AlRahmaPrimary.copy(alpha = 0.15f)
                                    else -> StatusWarning.copy(alpha = 0.15f)
                                }
                            ) {
                                Text(
                                    text = task.status,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (task.status) {
                                        "مكتملة" -> StatusSuccess
                                        "قيد التنفيذ" -> AlRahmaPrimary
                                        else -> StatusWarning
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(task.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("المسؤول: ${task.assigneeName} • التسليم: ${task.dueDate}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AlRahmaPrimary)

                            if (task.status != "مكتملة") {
                                Button(
                                    onClick = { onUpdateStatus(task.id, "مكتملة", currentRole.arabicName) },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("تأكيد التنفيذ", fontSize = 10.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyChatPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.QuestionAnswer, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(12.dp))
            Text("اختر محادثة لبدء المراسلة والتواصل الداخلي", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("يمكنك أيضاً مناقشة ملفات الأدوية والمرضى مباشرة من أقسام النظام", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun AiChatSummaryModal(
    conversation: Conversation,
    messages: List<ChatMessage>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AlRahmaGoldAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text("التحليل الذكي وتلخيص المحادثة (AI)", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("ملخص ذكي تم توليده بواسطة المساعد الذكي لمحادثة «${conversation.title}»:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("🔍 الموضوع الرئيسي:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AlRahmaPrimary)
                        Text("تزويد مخزون الأدوية وتغطية الطلبيات العاجلة بين الأقسام.", fontSize = 11.sp)

                        Divider()

                        Text("💡 القرارات المتخذة:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AlRahmaPrimary)
                        Text("• نقل 30 وحدة بنادول إكسترا إلى صيدلية 3.\n• توريد 50 علبة أوجمنتين من مستودع الشرق.", fontSize = 11.sp)

                        Divider()

                        Text("📋 المهام والمسؤولين:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AlRahmaPrimary)
                        Text("• تنفيذ المناقلة بواسطة صيدلي محمود حسن (اليوم).\n• مراجعة نتائج التحليل بواسطة د. سارة الأحمد.", fontSize = 11.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = AlRahmaPrimary)) {
                Text("إغلاق الملخص")
            }
        }
    )
}

@Composable
fun AttachmentPickerModal(
    medicines: List<Medicine>,
    patients: List<Patient>,
    onDismiss: () -> Unit,
    onSendAttachment: (String, String, String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: دواء, 1: مريض

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إرفاق صنف أو ملف من النظام", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("أدوية ومخزون") })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("ملفات المرضى") })
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (selectedTab == 0) {
                    LazyColumn(modifier = Modifier.height(200.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(medicines.take(8)) { med ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSendAttachment(
                                            "MEDICINE",
                                            "الدواء: ${med.tradeName}",
                                            "المخزون: ${med.stockQuantity} علبة • السعر: ${med.sellingPrice} ج.م"
                                        )
                                    }
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(med.tradeName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("المخزون: ${med.stockQuantity} علبة", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.height(200.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(patients.take(8)) { pat ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSendAttachment(
                                            "PATIENT",
                                            "الملف الطبي: ${pat.name}",
                                            "رقم الملف: #${pat.fileNumber} • الهاتف: ${pat.phone}"
                                        )
                                    }
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(pat.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("رقم الملف: #${pat.fileNumber}", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

@Composable
fun CreateAnnouncementModal(
    onDismiss: () -> Unit,
    onSave: (InternalAnnouncement) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("الجميع") }
    var isUrgent by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إصدار إعلان أو تعميم إداري جديد", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان التعميم / الإعلان") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("نص التعميم والتعليمات التفصيلية") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                OutlinedTextField(
                    value = target,
                    onValueChange = { target = it },
                    label = { Text("الفئة المستهدفة (الجميع، الأطباء، الصيادلة...)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isUrgent, onCheckedChange = { isUrgent = it })
                    Text("تعميم عاجل ونشط فوراً", fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        val ann = InternalAnnouncement(
                            title = title,
                            content = content,
                            senderName = "أ.د. محي الدين الجعفري",
                            senderRole = "المدير العام",
                            targetAudience = target,
                            date = "الآن",
                            isUrgent = isUrgent
                        )
                        onSave(ann)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AlRahmaPrimary)
            ) {
                Text("نشر التعميم")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

@Composable
fun CreateGroupModal(
    currentRole: UserRole,
    onDismiss: () -> Unit,
    onCreate: (String, String, List<String>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إنشاء مجموعة تواصل جديدة", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("اسم المجموعة") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("وصف المجموعة والصلاحيات") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onCreate(title, desc, emptyList())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AlRahmaPrimary)
            ) {
                Text("إنشاء المجموعة")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}
