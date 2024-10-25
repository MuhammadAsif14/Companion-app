package com.example.companionek
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DatabaseCreationClass : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database_creation_class)

        // Initialize Firestore
        firestore = Firebase.firestore

        // Create database structure
        createDatabaseStructure()
    }

    private fun createDatabaseStructure() {
        // Call methods to create users, chat sessions, and notes
        createUser("user1", "user1@example.com")
    }

    private fun createUser(username: String, email: String) {
        // Create a new user document in the users collection
        val userId = firestore.collection("users").document().id // Auto-generated user ID
        val userData = hashMapOf(
            "username" to username,
            "email" to email,
            "loginStreak" to 0,
            "createdAt" to System.currentTimeMillis(), // Current time as a timestamp
            "updatedAt" to System.currentTimeMillis()
        )

        // Set user data
        firestore.collection("users").document(userId)
            .set(userData)
            .addOnSuccessListener {
                println("User document created with ID: $userId")
                // Create a chat session for this user
                createChatSession(userId)
            }
            .addOnFailureListener { e ->
                println("Error creating user document: $e")
            }
    }

    private fun createChatSession(userId: String) {
        // Create a new chat session document in the user's chatSessions sub-collection
        val chatSessionId = firestore.collection("users").document(userId).collection("chatSessions").document().id // Auto-generated session ID
        val chatSessionData = hashMapOf(
            "sessionStart" to System.currentTimeMillis(), // Current time as a timestamp
            "sessionEnd" to null, // Set to null initially
            "emotionRecords" to emptyList<String>() // Start with an empty list
        )

        // Set chat session data
        firestore.collection("users").document(userId).collection("chatSessions").document(chatSessionId)
            .set(chatSessionData)
            .addOnSuccessListener {
                println("Chat session document created with ID: $chatSessionId")
                // Create an emotion record for this chat session
                createEmotionRecord(userId, chatSessionId)
            }
            .addOnFailureListener { e ->
                println("Error creating chat session document: $e")
            }
    }

    private fun createEmotionRecord(userId: String, chatSessionId: String) {
        // Create a new emotion record document in the chat session's emotionRecords sub-collection
        val emotionRecordId = firestore.collection("users").document(userId)
            .collection("chatSessions").document(chatSessionId)
            .collection("emotionRecords").document().id // Auto-generated emotion record ID
        val emotionRecordData = hashMapOf(
            "timestamp" to System.currentTimeMillis(), // Current time as a timestamp
            "emotionType" to "happy", // Example emotion type
            "message" to "This is a test message." // Example message
        )

        // Set emotion record data
        firestore.collection("users").document(userId)
            .collection("chatSessions").document(chatSessionId)
            .collection("emotionRecords").document(emotionRecordId)
            .set(emotionRecordData)
            .addOnSuccessListener {
                println("Emotion record document created with ID: $emotionRecordId")
                // Optionally, create a note for this user
                createNote(userId)
            }
            .addOnFailureListener { e ->
                println("Error creating emotion record document: $e")
            }
    }

    private fun createNote(userId: String) {
        // Create a new note document in the user's notes collection
        val noteId = firestore.collection("users").document(userId).collection("notes").document().id // Auto-generated note ID
        val noteData = hashMapOf(
            "title" to "My First Note",
            "text" to "This is a note about my feelings.",
            "emotionTags" to listOf("happy", "excited"), // Example emotion tags
            "createdAt" to System.currentTimeMillis(), // Current time as a timestamp
            "updatedAt" to System.currentTimeMillis()
        )

        // Set note data
        firestore.collection("users").document(userId)
            .collection("notes").document(noteId)
            .set(noteData)
            .addOnSuccessListener {
                println("Note document created with ID: $noteId")
            }
            .addOnFailureListener { e ->
                println("Error creating note document: $e")
            }
    }
}
