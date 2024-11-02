package com.example.companionek
//
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.companionek.NewMessageActivity.Companion.USER_KEY
import com.example.companionek.data.ChatMessage2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
//
//
//class LatestMessagesActivity : AppCompatActivity() {
//
//    private lateinit var fabNewChatUser: FloatingActionButton
//    companion object {
//        var currentUser: User? = null
//        val TAG = "LatestMessages"
//    }
//
//    val adapter = GroupAdapter<GroupieViewHolder>()
//    @SuppressLint("MissingInflatedId")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_latest_messages)
//        // Enable the back button in the ActionBar
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//
//        val recycleView_latest_message=findViewById<RecyclerView>(R.id.recyclerview_latest_messages)
//        recycleView_latest_message.adapter=adapter
//        recycleView_latest_message.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
//        //set item click listener on adapter
//        adapter.setOnItemClickListener { item, view ->
//            val intent= Intent(this,ChatLogActivity::class.java)
//            val row=item as LatestMessageRow
//            intent.putExtra(USER_KEY,row.chatPartnerUser)
//            startActivity(intent)
//
//
//        }
//        fabNewChatUser = findViewById(R.id.fab_new_chat_user)
//        fabNewChatUser.setOnClickListener {
//            openNewChatActivity() // Replace this with your desired action
//        }
//
//        fetchCurrentUser()
//        verifyUserIsLoggedIn()
//        listenForLatestMessages()
//
//
//    }
//    // Handle the back button click
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            android.R.id.home -> {
//                onBackPressed() // Go back to the previous fragment
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
//    private fun openNewChatActivity() {
//        // Start your new chat activity or perform your action
//        val intent = Intent(this, NewMessageActivity::class.java)
//        startActivity(intent)
//    }
//
//    val latestMessagesMap = HashMap<String, ChatMessage2>()
//    private fun refreshRecyclerViewMessages() {
//        adapter.clear()
//        latestMessagesMap.values.forEach {
//            adapter.add(LatestMessageRow(it))
//        }
//    }
//
//    private fun listenForLatestMessages() {
//        val fromId = FirebaseAuth.getInstance().uid
//        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
//        ref.addChildEventListener(object: ChildEventListener {
//            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
//                val chatMessage = p0.getValue(ChatMessage2::class.java) ?: return
//                latestMessagesMap[p0.key!!] = chatMessage
//                refreshRecyclerViewMessages()
//            }
//
//            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
//                val chatMessage = p0.getValue(ChatMessage2::class.java) ?: return
//                latestMessagesMap[p0.key!!] = chatMessage
//                refreshRecyclerViewMessages()
//            }
//
//            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
//
//            }
//            override fun onChildRemoved(p0: DataSnapshot) {
//
//            }
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//        })
//    }
//    private fun fetchCurrentUser() {
//        val uid = FirebaseAuth.getInstance().uid
//        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
//        ref.addListenerForSingleValueEvent(object: ValueEventListener {
//
//            override fun onDataChange(p0: DataSnapshot) {
//                currentUser = p0.getValue(User::class.java)
//                Log.d("LatestMessages", "Current user ${currentUser?.profileImageUrl}")
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//        })
//    }
//
//
//    private fun verifyUserIsLoggedIn() {
//        val uid = FirebaseAuth.getInstance().uid
//        if (uid == null) {
//            val intent = Intent(this, LoginActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//
//        }
//    }
//
//
//
//}

class LatestMessagesActivity : AppCompatActivity() {

    private lateinit var fabNewChatUser: FloatingActionButton
    companion object {
        var currentUser: User? = null
        val TAG = "LatestMessages"
    }

    val adapter = GroupAdapter<GroupieViewHolder>()
    private val latestMessagesMap = HashMap<String, ChatMessage2>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val recycleView_latest_message = findViewById<RecyclerView>(R.id.recyclerview_latest_messages)
        recycleView_latest_message.adapter = adapter
        recycleView_latest_message.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this, ChatLogActivity::class.java)
            val row = item as LatestMessageRow
            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }

        fabNewChatUser = findViewById(R.id.fab_new_chat_user)
        fabNewChatUser.setOnClickListener {
            openNewChatActivity()
        }

        fetchCurrentUser()
        verifyUserIsLoggedIn()
        listenForLatestMessages()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openNewChatActivity() {
        val intent = Intent(this, NewMessageActivity::class.java)
        startActivity(intent)
    }

    private fun refreshRecyclerViewMessages() {
        adapter.clear()

        // Sort messages by timestamp in descending order (latest at the top)
        val sortedMessages = latestMessagesMap.values.sortedByDescending { it.timestamp }

        // Add sorted messages to the adapter
        sortedMessages.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage2::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage2::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // No need to handle for latest messages
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // Optional: handle removed messages if necessary
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "listenForLatestMessages cancelled: $error")
            }
        })
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
                Log.d(TAG, "Current user ${currentUser?.profileImageUrl}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "fetchCurrentUser cancelled: $error")
            }
        })
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}