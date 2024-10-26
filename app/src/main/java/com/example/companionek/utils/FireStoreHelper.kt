package com.example.companionek.utils

import com.example.companionek.data.ChatSession
import com.example.companionek.data.Message
import com.example.companionek.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreHelper {

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    // Function to get user data
    suspend fun getUserData(): UserData? {
        return try {
            val document = db.collection("user").document(userId ?: "").get().await()
            document.toObject(UserData::class.java)
        } catch (e: Exception) {
            null
        }
    }

    // Function to get chat sessions
    suspend fun getChatSessions(): List<ChatSession>? {
        return try {
            val sessions = db.collection("user").document(userId ?: "").collection("chatSessions").get().await()
            sessions.toObjects(ChatSession::class.java)
        } catch (e: Exception) {
            null
        }
    }

    // Function to add a new chat session
    suspend fun addChatSession(chatSession: ChatSession): Boolean {
        return try {
            db.collection("user").document(userId ?: "").collection("chatSessions")
                .document(chatSession.sessionId)
                .set(chatSession).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Function to add a message to a chat session
    suspend fun addMessageToChat(sessionId: String, message: Message): Boolean {
        return try {
            val sessionRef = db.collection("user").document(userId ?: "")
                .collection("chatSessions").document(sessionId)

            // Update the session with a new message
            sessionRef.update("messages", FieldValue.arrayUnion(message)).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
