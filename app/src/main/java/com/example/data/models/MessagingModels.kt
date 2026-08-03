package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MessageType {
    TEXT,
    IMAGE,
    VOICE,
    PDF,
    DOCUMENT,
    PATIENT_REPORT,
    TASK,
    LINKED_ENTITY
}

enum class PresenceStatus(val arabicLabel: String, val colorHex: String) {
    ONLINE("متصل", "#4CAF50"),
    BUSY("مشغول", "#FFC107"),
    OFFLINE("غير متصل", "#9E9E9E")
}

enum class MessagePriority(val arabicLabel: String) {
    NORMAL("عادي"),
    IMPORTANT("مهم"),
    URGENT("عاجل 🔴")
}

enum class DeliveryStatus {
    SENDING,
    SENT,
    DELIVERED,
    READ,
    FAILED
}

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val senderRole: String,
    val receiverId: String = "",
    val messageType: MessageType = MessageType.TEXT,
    val content: String,
    val attachmentUrl: String = "",
    val attachmentName: String = "",
    val attachmentSize: String = "",
    val timestamp: String, // e.g. "04:15 م"
    val createdAt: Long = System.currentTimeMillis(),
    val status: DeliveryStatus = DeliveryStatus.READ,
    val isPinned: Boolean = false,
    val isImportant: Boolean = false,
    val priority: MessagePriority = MessagePriority.NORMAL,
    val replyToMessageId: Long? = null,
    val replyToSender: String = "",
    val replyToText: String = "",
    val linkedEntityType: String = "", // "MEDICINE", "PATIENT", "APPOINTMENT", "SURGERY", "PHARMACY", "LAB_TEST", "TASK"
    val linkedEntityId: String = "",
    val linkedEntityTitle: String = "",
    val linkedEntitySubtitle: String = "",
    val mentions: String = "", // comma separated mentioned names or roles
    val voiceDurationSeconds: Int = 0,
    val deletedAt: Long? = null
)

@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String = "",
    val type: String, // "INDIVIDUAL", "GROUP", "LINKED_WORK", "LINKED_PATIENT", "ANNOUNCEMENT"
    val avatarInitials: String = "",
    val roleRequired: String = "",
    val lastMessage: String = "",
    val lastMessageTime: String = "",
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val presenceStatus: PresenceStatus = PresenceStatus.ONLINE,
    val linkedEntityType: String = "",
    val linkedEntityId: String = "",
    val members: String = "", // comma-separated roles or member names
    val groupAdmin: String = "",
    val pinnedMessage: String = ""
)

@Entity(tableName = "internal_announcements")
data class InternalAnnouncement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val senderName: String,
    val senderRole: String,
    val targetAudience: String, // "الجميع", "الأطباء", "الصيادلة", "قسم الأطفال", etc.
    val date: String,
    val isUrgent: Boolean = false
)

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceMessageId: Long? = null,
    val title: String,
    val description: String,
    val assigneeName: String,
    val assigneeRole: String,
    val creatorName: String,
    val priority: String = "عادي", // "عادي", "مهم", "عاجل"
    val dueDate: String,
    val status: String = "جديدة", // "جديدة", "قيد التنفيذ", "مكتملة", "مرفوضة"
    val completedBy: String = "",
    val completedAt: String = ""
)
