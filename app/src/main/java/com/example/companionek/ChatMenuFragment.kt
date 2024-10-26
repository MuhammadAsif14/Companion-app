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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
//    private fun getUserChatData() {
//    userId?.let { uid ->
//        // Retrieve chat sessions for the user
//        database.child("user").child(uid).child("chatSessions").addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                newArrayList2.clear() // Clear the list before adding new data
//                for (sessionSnapshot in dataSnapshot.children) {
//                    // Retrieve emotions for the current session
//                    val emotions = sessionSnapshot.child("emotions").children.map { it.getValue(String::class.java) ?: "" }
//
//                    // Create a ChatItems instance for this chat session
//                    val chatItem = ChatItems(
//                        image = R.drawable.happy_emoji, // Choose an appropriate image based on your logic
//                        chatTitle = emotions.joinToString(", "), // Combine emotions into a single string
//                        text = "Chat session with ${emotions.joinToString(", ")} emotions." // Provide a description or summary of the session
//                    )
//                    newArrayList2.add(chatItem) // Add the session item to the list
//                }
//
//                // Notify the adapter of the data change
//                newRecyclerView2.adapter = ChatAdapter(newArrayList2)
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Log.e("ChatMenuFragment", "Error retrieving chat data: ${databaseError.message}")
//            }
//        })
//    }
//}
private fun getUserChatData() {
    userId?.let { uid ->
        // Retrieve chat sessions for the user
        database.child("user").child(uid).child("chatSessions").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                newArrayList2.clear() // Clear the list before adding new data
                for (sessionSnapshot in dataSnapshot.children) {
                    val emotions = sessionSnapshot.child("emotion").children.map { it.getValue(String::class.java) ?: "" }
                    val sessionId = sessionSnapshot.key ?: "" // Get the session ID

                    // Create a ChatItems instance for this chat session
                    val chatItem = ChatItems(
                        image = R.drawable.happy_emoji, // Choose an appropriate image
                        chatTitle = emotions.joinToString(", "),
                        text = "Chat session with ${emotions.joinToString(", ")} emotions.",
                        sessionId = sessionId // Pass the session ID here
                    )
                    newArrayList2.add(chatItem) // Add the session item to the list
                }

                // Notify the adapter of the data change
                newRecyclerView2.adapter = ChatAdapter(newArrayList2, ::onChatItemClick)
            }
            private fun onChatItemClick(sessionId: String) {
                // Start the chat screen activity and pass the session ID
                val intent = Intent(requireContext(), chat_screen::class.java)
                intent.putExtra("SESSION_ID", sessionId) // Pass the session ID
                startActivity(intent)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("ChatMenuFragment", "Error retrieving chat data: ${databaseError.message}")
            }
        })
    }
}

}
