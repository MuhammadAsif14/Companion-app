package com.example.companionek


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.companionek.adapter.ChatAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class ChatMenuFragment : Fragment() {
    private lateinit var newRecyclerView2: RecyclerView
    private lateinit var fabNewChat: FloatingActionButton
    private lateinit var newArrayList2: ArrayList<ChatItems>
    private lateinit var database: DatabaseReference
    private var userId = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var cImageId: Array<Int>
    private lateinit var cTitle: Array<String>
    private lateinit var cText: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_chat_menu, container, false)

        newRecyclerView2 = rootView.findViewById(R.id.ch_recyclerView)
        newRecyclerView2.layoutManager = LinearLayoutManager(activity)
        newRecyclerView2.setHasFixedSize(true)

        newArrayList2 = arrayListOf()

        // Retrieve user chats
        getUserChatData()


        // Initialize the Floating Action Button
        fabNewChat = rootView.findViewById(R.id.fab_new_chat)

        // Set up the click listener
        fabNewChat.setOnClickListener {
            openNewChatActivity() // Replace this with your desired action
        }

        return rootView
    }

    private fun openNewChatActivity() {
        // Start your new chat activity or perform your action
        val intent = Intent(requireContext(), chat_screen::class.java)
        startActivity(intent)
    }

private fun getUserChatData() {
    userId?.let { uid ->
        // Reference to user's chatSessions collection in Firestore
        val chatSessionsRef = FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("chatSessions")
        // Create a map linking emotion strings to drawable resource IDs
        val emotionToImageMap = mapOf(
            "happy" to R.drawable.happy_emoji,
            "sad" to R.drawable.sad_emoji,
            "angry" to R.drawable.angry_emoji,
            "neutral" to R.drawable.neutral_emoji,
            "surprised" to R.drawable.surprised_emoji,
            "disgust" to R.drawable.tired_emoji,
            "fearful" to R.drawable.fearful_emoji
        )
        chatSessionsRef.get()
            .addOnSuccessListener { sessionsSnapshot ->
                newArrayList2.clear() // Clear the list before adding new data

                for (sessionSnapshot in sessionsSnapshot.documents) {
                    // Retrieve session ID
                    val sessionId = sessionSnapshot.id
                    // Retrieve emotions array from the session document

                    val emotions = sessionSnapshot.get("emotions") as? List<String> ?: emptyList()
                    // Choose the first available emotion or a default image if none match
                    val emotionImage = emotions.firstNotNullOfOrNull { emotionToImageMap[it.toLowerCase()] }
                        ?: R.drawable.neutral_emoji // Default to neutral if no match found
                    // Create a ChatItems instance for this chat session
                    val chatItem = ChatItems(
                        image = emotionImage, // Choose an appropriate image
                        chatTitle = emotions.joinToString(", "),
                        text = "Chat session with ${emotions.joinToString(", ")} emotions.",
                        sessionId = sessionId // Pass the session ID here
                    )
                    newArrayList2.add(chatItem) // Add the session item to the list
                }

                // Notify the adapter of the data change
                newRecyclerView2.adapter = ChatAdapter(newArrayList2, ::onChatItemClick)
            }
            .addOnFailureListener { exception ->
                Log.e("ChatMenuFragment", "Error retrieving chat data: ${exception.message}", exception)
            }
    }
}

    // Called when a chat item is clicked
    private fun onChatItemClick(sessionId: String) {
        // Start the chat screen activity and pass the session ID
        val intent = Intent(requireContext(), chat_screen::class.java)
        intent.putExtra("SESSION_ID", sessionId) // Pass the session ID
        intent.putExtra("isSessionCreated",true)
        startActivity(intent)
    }


}
