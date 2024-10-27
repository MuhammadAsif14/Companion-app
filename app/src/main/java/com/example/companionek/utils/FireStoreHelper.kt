package com.example.companionek.utils

import android.util.Log
import com.example.companionek.Users
import com.example.companionek.data.ChatMessage
import com.example.companionek.data.ChatSession
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreHelper {
    private val auth = FirebaseAuth.getInstance()

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    // Function to get user data
    // Function to get user data
    suspend fun getUserData(): Users? {
        return try {
            val document = db.collection("users").document(auth.currentUser?.uid ?: "").get().await()
            document.toObject(Users::class.java)
        } catch (e: Exception) {
            Log.e("FirestoreHelper", "Error retrieving user data", e)
            null
        }
    }

    // Function to add a new user
    suspend fun addUser(username: String, profilePicUrl: String, email: String, password: String, userId: String): Boolean {
        return try {
            // Create user data object
            val user = Users(
                userId = userId,
                userName = username,
                maill = email, // This will map to 'mail' in your Users class
                password = password, // You may want to handle passwords more securely
                profilepic = profilePicUrl
            )

            // Save user data to Firestore
            db.collection("users").document(userId).set(user).await()

            Log.e("FirestoreHelper", "SUCCESSFULLY ADDED USER")
            true // Return true indicating success

        } catch (e: Exception) {
            Log.e("FirestoreHelper", "Error adding user", e)
            false // Return false indicating failure
        }
    }



    // Function to get chat sessions
    suspend fun getChatSessions(): List<ChatSession>? {
        return try {
            val sessions = db.collection("users").document(userId ?: "").collection("chatSessions").get().await()
            sessions.toObjects(ChatSession::class.java)
        } catch (e: Exception) {
            Log.e("FirestoreHelper", "Error retrieving chat sessions", e)
            null
        }
    }

    // Function to add a new chat session
    suspend fun addChatSession(chatSession: ChatSession): Boolean {
        return try {
            db.collection("users").document(userId ?: "").collection("chatSessions")
                .document(chatSession.sessionId)
                .set(chatSession).await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreHelper", "Error adding chat session", e)
            false
        }
    }

    // Function to add a message to a chat session
    suspend fun addMessageToChat(sessionId: String, message: ChatMessage): Boolean {
        return try {
            val chatMessageRef = db.collection("users").document(userId ?: "")
                .collection("chatSessions").document(sessionId)
                .collection("messages").document()

            // Save the message data
            chatMessageRef.set(message).await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreHelper", "Error adding message to chat", e)
            false
        }
    }

    // Function to get chat messages from a specific chat session
    suspend fun getChatMessages(sessionId: String): List<ChatMessage> {
        return try {
            val messagesList = mutableListOf<ChatMessage>()

            if (userId != null) {
                // Reference to the messages subcollection for the given chat session
                val messagesRef = db.collection("users").document(userId)
                    .collection("chatSessions").document(sessionId)
                    .collection("messages")

                // Fetch the messages from the subcollection
                val messagesSnapshot = messagesRef.get().await()

                // Convert each document in the snapshot to ChatMessage
                for (document in messagesSnapshot.documents) {
                    val chatMessage = document.toObject(ChatMessage::class.java)
                    if (chatMessage != null) {
                        messagesList.add(chatMessage)
                    }
                }
            }

            messagesList
        } catch (e: Exception) {
            Log.e("FirestoreHelper", "Error retrieving messages", e)
            emptyList()
        }
    }
}
