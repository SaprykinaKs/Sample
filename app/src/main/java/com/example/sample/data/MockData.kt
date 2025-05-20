package com.example.sample.data

import com.example.sample.models.Chat
import com.example.sample.models.Chat.ChatType
import com.example.sample.models.Message
import com.example.sample.models.Message.MessageStatus
import com.example.sample.models.Message.MessageType
import com.example.sample.models.VoiceChannel
import com.example.sample.models.VoiceUser

object MockData {
    // ChatFragment
    val demoChats = listOf(
        Chat("1", "Общий чат", "Последнее сообщение", System.currentTimeMillis(),
            3, ChatType.GROUP, "https://example.com/group1.jpg"),
        Chat("2", "Команда разработки", "Фиксим баги", System.currentTimeMillis() - 10000,
            0, ChatType.GROUP, "https://example.com/dev.jpg"),
        Chat("3", "Алексей Климов", "Привет!", System.currentTimeMillis() - 3600000,
            1, ChatType.PERSONAL, "https://example.com/user1.jpg"),
        Chat("4", "Новости компании", "Новый релиз!", System.currentTimeMillis() - 86400000,
            5, ChatType.CHANNEL, "https://example.com/news.jpg")
    )

    // ChatViewModel
    val testMessages = listOf(
        Message(
            id = "1",
            text = "Алексей присоединился к чату",
            senderId = "system",
            senderName = "Система",
            type = MessageType.SYSTEM
        ),
        Message(
            id = "2",
            text = "Привет! Как дела?",
            senderId = "user1",
            senderName = "Алексей",
            isOutgoing = false,
            status = MessageStatus.READ
        ),
        Message(
            id = "3",
            text = "Привет! Все очень плохо, спасибо!",
            senderId = "me",
            senderName = "Я",
            isOutgoing = true,
            status = MessageStatus.READ
        )
    )

    // VoiceChannelFragment
    val voiceChannels = listOf(
        VoiceChannel("1", "Общий канал", 5),
        VoiceChannel("2", "Музыка", 2),
        VoiceChannel("3", "Игры", 3)
    )

    // VoiceCallFragment
    val demoVoiceUsers = mutableListOf(
        VoiceUser("1", "Соня", true, 75),
        VoiceUser("2", "Костя", false, 0),
        VoiceUser("3", "Матвей", true, 45)
    )
}