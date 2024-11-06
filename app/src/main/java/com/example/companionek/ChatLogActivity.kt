package com.example.companionek

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.companionek.data.ChatMessage2
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatLogActivity : AppCompatActivity() {
    companion object {
        val TAG = "ChatLog"
    }
    private lateinit var recyclerview_chat_log:RecyclerView
    private lateinit var send_button_chat_log:Button
    private lateinit var edittext_chat_log:EditText
    val adapter = GroupAdapter<GroupieViewHolder>()
    private val fromId = FirebaseAuth.getInstance().uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        recyclerview_chat_log=findViewById(R.id.recyclerview_chat_log)
        send_button_chat_log=findViewById(R.id.send_button_chat_log)
        edittext_chat_log=findViewById(R.id.edittext_chat_log)
        val user = intent.getParcelableExtra<Users>(NewMessageActivity.USER_KEY)
        if (user != null) {
            supportActionBar?.title = user.userName
        }
        send_button_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to send message....")
            performSendMessage()
        }
        listenForMessages()
        recyclerview_chat_log.adapter = adapter
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.delete_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                // Show confirmation dialog
                AlertDialog.Builder(this)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this chat?")
                    .setPositiveButton("Yes") { _, _ ->
                        // Delete chat messages if confirmed
                        deleteChatMessages()
                    }
                    .setNegativeButton("No", null)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun deleteChatMessages() {
        val user = intent.getParcelableExtra<Users>(NewMessageActivity.USER_KEY)
        val toId = user?.userId ?: return
        val refFromUser = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
//        val refToUser = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                refFromUser.removeValue().await()  // Delete messages from the current user's node
//                refToUser.removeValue().await()    // Delete messages from the recipient's node
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ChatLogActivity, "Chat deleted", Toast.LENGTH_SHORT).show()
                    adapter.clear() // Clear the adapter to remove deleted messages from the UI
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ChatLogActivity, "Failed to delete chat", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun listenForMessages() {
        val user = intent.getParcelableExtra<Users>(NewMessageActivity.USER_KEY)
        val toId = user?.userId
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        val receiverUser = toId?.let {
            FirebaseFirestore.getInstance().collection("users").document(it)
        }
        // Retrieve the token from Firestore
        receiverUser?.get()?.addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val receiversToken = document.getString("fcmToken") // Assuming "fcmToken" is the field name in Firestore
                if (receiversToken != null) {
                    Log.d("FCM Token", "Receiver's FCM Token: $receiversToken")
                    // Use receiversToken as needed
                } else {
                    Log.e("FCM Token", "FCM token not found for receiver.")
                }
            } else {
                Log.e("FCM Token", "No such document for receiver.")
            }
        }?.addOnFailureListener { exception ->
            Log.e("FCM Token", "Failed to retrieve FCM token", exception)
        }
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage2::class.java)
                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
//                        adapter.add(ChatToItem(chatMessage.text, chatMessage.isseen,chatMessage.timestamp))
                        adapter.add(
                            ChatToItem(
                                chatMessage.text,
                                chatMessage.isseen,
                                chatMessage.timestamp,
                                chatMessage.id,       // Pass the messageId for unsending
                                chatMessage.toId, // Pass the toId for unsending
                                adapter
                            )
                        )
                    } else {
                        val toref = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId/${chatMessage.id}")
                        toref.child("isseen").setValue(true)
                        adapter.add(ChatFromItem(chatMessage.text,chatMessage.timestamp))
                    }
                }
                recyclerview_chat_log.scrollToPosition(adapter.itemCount-1)
            }
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage2::class.java) ?: return

                if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                    // The current user sent this message, and it's been seen by the recipient
                    for (i in 0 until adapter.itemCount) {
                        val item = adapter.getItem(i)
                        if (item is ChatToItem && item.messageId == chatMessage.id) {
                            // Update the `isseen` status of the message
                            item.isSeen = chatMessage.isseen
                            adapter.notifyItemChanged(i)  // Notify adapter about the change
                            break
                        }
                    }
                }
            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }
            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })
    }
    private fun performSendMessage() {
    val text = edittext_chat_log.text.toString()
    edittext_chat_log.text.clear()
    if (text.isNotEmpty()) {
        val user = intent.getParcelableExtra<Users>(NewMessageActivity.USER_KEY)
        val toId = user?.userId
        if (fromId == null || toId == null) return
        // Launch a coroutine for the network operation
        CoroutineScope(Dispatchers.IO).launch {
            val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
            val chatMessage = ChatMessage2(
                reference.key!!,
                text,
                fromId,
                toId,
                isseen = false,
                System.currentTimeMillis() / 1000
            )
            try {
                // Save the message in the sender's reference
                reference.setValue(chatMessage).await()
                // Save the message in the receiver's reference
                val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId/${chatMessage.id}")
                toReference.setValue(chatMessage).await()
                // Save the latest message reference
                val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
                latestMessageRef.setValue(chatMessage).await()
                val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
                latestMessageToRef.setValue(chatMessage).await()
                // Fetch the receiver's FCM token
                val dataSnapshot = FirebaseDatabase.getInstance().getReference("users/$toId/fcmToken").get().await()
                val receiverToken = dataSnapshot.getValue(String::class.java)
                val fromId = FirebaseAuth.getInstance().uid
                var senderName = "Name"
                if (fromId != null) {
                    val userRef = FirebaseDatabase.getInstance().getReference("users/$fromId")
                    userRef.get().addOnSuccessListener { dataSnapshot ->
                        senderName = dataSnapshot.child("userName").getValue(String::class.java).toString()

                        if (receiverToken != null) {
                            // Send the FCM notification with the updated sender's name
                            sendFCMNotification(
                                this@ChatLogActivity,
                                receiverToken,
                                senderName,
                                text // The message content
                            )
                        } else {
                            println("No FCM token for the receiver.")
                        }
                    }.addOnFailureListener { e ->
                        println("Failed to retrieve sender's name: ${e.message}")
                    }
                }
                // Clear the text and scroll the RecyclerView on the main thread
                withContext(Dispatchers.Main) {
                    recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                }
            } catch (e: Exception) {
                println("Error sending message: ${e.message}")
            }
        }
    } else {
        Toast.makeText(this@ChatLogActivity, "Type something", Toast.LENGTH_SHORT).show()
    }
}
    // Function to get access token from the service account JSON in assets
    suspend fun getAccessToken(context: Context): String? = withContext(Dispatchers.IO) {
        val serviceAccountStream = context.assets.open("service-accounts.json") // Ensure this matches your file name
        val googleCredentials = GoogleCredentials.fromStream(serviceAccountStream)
            .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))
        googleCredentials.refreshIfExpired()
        googleCredentials.accessToken.tokenValue
    }

    fun sendFCMNotification(
        context: Context,
        receiverToken: String,
        senderName: String,
        message: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val accessToken = getAccessToken(context) ?: return@launch
            Log.d(TAG, "performSendMessage: $title")
            Log.d(TAG, "performSendMessage: $message")


            val fcmUrl = "https://fcm.googleapis.com/v1/projects/companion-11996/messages:send" // Replace with your project ID

            val dataPayload = JSONObject().apply {
                put("title", title)
                put("senderName", senderName)
                put("message", message)
            }


            val messagePayload = JSONObject().apply {
                put("token", receiverToken)
                put("data", dataPayload)
            }

            val payload = JSONObject().apply {
                put("message", messagePayload)
            }

            val client = OkHttpClient()
            val body = RequestBody.create("application/json; charset=utf-8".toMediaType(), payload.toString())
            val request = Request.Builder()
                .url(fcmUrl)
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Failed to send FCM Notification: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        println("FCM Notification sent successfully")
                    } else {
                        println("Failed to send FCM Notification: ${response.body?.string()}")
                    }
                }
            })
        }
    }

}
class ChatFromItem(val text: String, val time: Long): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val textViewFrom = viewHolder.itemView.findViewById<TextView>(R.id.textViewFrom)
        textViewFrom.text=text

        val messageTime = viewHolder.itemView.findViewById<TextView>(R.id.messageTime) // Assuming this TextView exists in layout
        // Check if timestamp is in seconds, convert to milliseconds if needed
        val correctedTime = if (time < 1_000_000_000_000) time * 1000 else time

        // Format and display date and time
        val dateFormat = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
        val formattedTime = dateFormat.format(Date(correctedTime))
        messageTime.text = formattedTime
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}
//class ChatToItem(val text: String, private val isSeen: Boolean, val time: Long) : Item<GroupieViewHolder>() {
//    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
//        val textViewTo = viewHolder.itemView.findViewById<TextView>(R.id.textViewTo)
//        textViewTo.text = text
//
//        val statusView = viewHolder.itemView.findViewById<TextView>(R.id.text_seen) // Assuming this TextView exists in layout
//        statusView.text = if (isSeen) "Seen" else "Sent"
//
//        val messageTime = viewHolder.itemView.findViewById<TextView>(R.id.messageTime) // Assuming this TextView exists in layout
//        // Check if timestamp is in seconds, convert to milliseconds if needed
//        val correctedTime = if (time < 1_000_000_000_000) time * 1000 else time
//
//        // Format and display date and time
//        val dateFormat = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
//        val formattedTime = dateFormat.format(Date(correctedTime))
//        messageTime.text = formattedTime
//
//    }
//
//    override fun getLayout(): Int {
//        return R.layout.chat_to_row
//    }
//}

//class ChatToItem(
//    val text: String,
//    private val isSeen: Boolean,
//    val time: Long,
//    val messageId: String,
//    val toId: String
//) : Item<GroupieViewHolder>() {
//
//    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
//        val textViewTo = viewHolder.itemView.findViewById<TextView>(R.id.textViewTo)
//        textViewTo.text = text
//        val statusView = viewHolder.itemView.findViewById<TextView>(R.id.text_seen)
//        statusView.text = if (isSeen) "Seen" else "Sent"
//
//        val messageTime = viewHolder.itemView.findViewById<TextView>(R.id.messageTime)
//        val correctedTime = if (time < 1_000_000_000_000) time * 1000 else time
//        val dateFormat = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
//        messageTime.text = dateFormat.format(Date(correctedTime))
//
//        // Add long-click listener for unsending
//        viewHolder.itemView.setOnLongClickListener {
//            showUnsendConfirmation(viewHolder.itemView.context)
//            true
//        }
//    }
//
//    override fun getLayout(): Int {
//        return R.layout.chat_to_row
//    }
//
//    private fun showUnsendConfirmation(context: Context) {
//        AlertDialog.Builder(context)
//            .setTitle("Unsend Message")
//            .setMessage("Do you want to unsend this message?")
//            .setPositiveButton("Yes") { _, _ -> unsendMessage(context) }
//            .setNegativeButton("No", null)
//            .show()
//    }
//
//    private fun unsendMessage(context: Context) {
//        val fromId = FirebaseAuth.getInstance().uid ?: return
//
//        val senderRef = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId/$messageId")
//        val receiverRef = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId/$messageId")
//
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                senderRef.removeValue().await()
//                receiverRef.removeValue().await()
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(context, "Message unsent", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(context, "Failed to unsend message", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//}
class ChatToItem(
    val text: String,
    var isSeen: Boolean,
    val time: Long,
    val messageId: String,
    val toId: String,
    private val adapter: GroupAdapter<GroupieViewHolder> // Pass adapter here
) : Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val textViewTo = viewHolder.itemView.findViewById<TextView>(R.id.textViewTo)
        textViewTo.text = text
        val statusView = viewHolder.itemView.findViewById<TextView>(R.id.text_seen)
        statusView.text = if (isSeen) "Seen" else "Sent"

        val messageTime = viewHolder.itemView.findViewById<TextView>(R.id.messageTime)
        val correctedTime = if (time < 1_000_000_000_000) time * 1000 else time
        val dateFormat = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
        messageTime.text = dateFormat.format(Date(correctedTime))

        // Add long-click listener for unsending
        viewHolder.itemView.setOnLongClickListener {
            showUnsendConfirmation(viewHolder.itemView.context, viewHolder.adapterPosition)
            true
        }
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    private fun showUnsendConfirmation(context: Context, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Unsend Message")
            .setMessage("Do you want to unsend this message?")
            .setPositiveButton("Yes") { _, _ -> unsendMessage(context, position) }
            .setNegativeButton("No", null)
            .show()
    }

    private fun unsendMessage(context: Context, position: Int) {
        val fromId = FirebaseAuth.getInstance().uid ?: return

        // Firebase references for both sender and receiver
        val senderRef = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId/$messageId")
        val receiverRef = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId/$messageId")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Remove message from both references
                senderRef.removeValue().await()
                receiverRef.removeValue().await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Message unsent", Toast.LENGTH_SHORT).show()
                    adapter.removeGroup(position)  // Remove message from adapter
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to unsend message", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
