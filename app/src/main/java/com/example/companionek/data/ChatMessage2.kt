package com.example.companionek.data
//this is used for chat feature with other users ChatMessage is used for chatbot
data class ChatMessage2
    (
    val id: String,
    val text: String,
    val fromId: String,
    val toId: String,
    var isseen: Boolean =false,
    val timestamp: Long) {
    constructor() : this("", "", "", "",false, -1)
}
