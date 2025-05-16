package com.example.sample.models

data class VoiceUser(
    val id: String,
    val name: String,
    val isSpeaking: Boolean,
    val volumeLevel: Int // 0-100
)