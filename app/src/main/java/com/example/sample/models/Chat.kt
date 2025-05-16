package com.example.sample.models

data class Chat(
    val id: String,
    val name: String,
    val lastMessage: String,
    val timestamp: Long,
    val unreadCount: Int = 0,
    val chatType: ChatType = ChatType.PERSONAL,
    val avatarUrl: String? = null
) {
    enum class ChatType {
        PERSONAL, GROUP, CHANNEL
    }
}