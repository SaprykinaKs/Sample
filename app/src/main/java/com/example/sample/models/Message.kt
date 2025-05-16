package com.example.sample.models

import java.util.Date

data class Message(
    val id: String,
    val text: String,
    val timestamp: Long = Date().time,
    val senderId: String,
    val senderName: String,
    val type: MessageType = MessageType.TEXT,
    val status: MessageStatus = MessageStatus.SENT,
    val isOutgoing: Boolean = false,
    val imageUrl: String? = null
) {
    enum class MessageType {
        TEXT, SYSTEM, IMAGE
    }

    enum class MessageStatus {
        SENT, DELIVERED, READ
    }
}