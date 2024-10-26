package com.example.companionek.data
// ChatSession.kt

data class ChatSession(
    val sessionId: String = "",
    val messages: List<Message> = emptyList(),
    val emotion: List<String> = emptyList()
)
