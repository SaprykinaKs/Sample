package com.example.sample.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatViewModel : ViewModel() {
    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    fun loadMessages(chatId: String) {
        val testMessages = listOf(
            Message(
                id = "1",
                text = "Алексей присоединился к чату",
                senderId = "system",
                senderName = "Система",
                type = Message.MessageType.SYSTEM
            ),
            Message(
                id = "2",
                text = "Привет! Как дела?",
                senderId = "user1",
                senderName = "Алексей",
                isOutgoing = false,
                status = Message.MessageStatus.READ
            ),
            Message(
                id = "3",
                text = "Привет! Все очень плохо, спасибо!",
                senderId = "me",
                senderName = "Я",
                isOutgoing = true,
                status = Message.MessageStatus.READ
            )
        )
        if (chatId.toInt()==3){_messages.value = testMessages}
    }

    fun sendMessage(message: Message) {
        val currentList = _messages.value?.toMutableList() ?: mutableListOf()
        currentList.add(message)
        _messages.value = currentList
    }

    fun sendMessage(text: String) {
        val newMessage = Message(
            id = System.currentTimeMillis().toString(),
            text = text,
            senderId = "me",
            senderName = "Я",
            isOutgoing = true,
            status = Message.MessageStatus.SENT
        )
        sendMessage(newMessage)
    }

    fun deleteMessage(message: Message) {
        _messages.value = _messages.value?.filterNot { it.id == message.id }
    }
}