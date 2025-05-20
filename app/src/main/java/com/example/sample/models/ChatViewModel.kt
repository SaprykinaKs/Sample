package com.example.sample.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sample.data.MockData

class ChatViewModel : ViewModel() {
    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    fun loadMessages(chatId: String) {
        if (chatId.toInt() == 3) {
            _messages.value = MockData.testMessages
        }
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