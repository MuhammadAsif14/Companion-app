package com.example.companionek

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.companionek.data.ChatMessage2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

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


//        val username= intent.getStringExtra(NewMessageActivity.USER_KEY)

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
    private fun listenForMessages() {

        val user = intent.getParcelableExtra<Users>(NewMessageActivity.USER_KEY)
        val toId = user?.userId
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")


        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage2::class.java)

                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {

                        adapter.add(ChatToItem(chatMessage.text, chatMessage.isseen))

                    } else {
                        val toref = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId/${chatMessage.id}")
                        toref.child("isseen").setValue(true)
                        adapter.add(ChatFromItem(chatMessage.text))

                    }

                }
                recyclerview_chat_log.scrollToPosition(adapter.itemCount-1)

            }


            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })

    }
private fun performSendMessage() {
    // how do we actually send a message to firebase...
    val text = edittext_chat_log.text.toString()
    if (!text.isEmpty()) {
        val user = intent.getParcelableExtra<Users>(NewMessageActivity.USER_KEY)
        val toId = user?.userId

        if (fromId == null) return

//    val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val reference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        Log.d("FromDI-oID", "$reference")
        val toReference: DatabaseReference


        val chatMessage = toId?.let {
            ChatMessage2(
                reference.key!!,
                text,
                fromId,
                it,
                isseen = false,
                System.currentTimeMillis() / 1000
            )


        }
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${reference.key}")
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }
        toReference =FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId/${chatMessage?.id}")
        Log.d("oID-FromDI", "$toReference")
        toReference.setValue(chatMessage)
        val latestMessageRef =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)
    }
    else{
        Toast.makeText(this@ChatLogActivity, "Type something", Toast.LENGTH_SHORT).show()

    }
}
}

class ChatFromItem(val text: String): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val textViewFrom = viewHolder.itemView.findViewById<TextView>(R.id.textViewFrom)


        textViewFrom.text=text
//        viewHolder.itemView.textview_from_row.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}
class ChatToItem(val text: String, private val isSeen: Boolean) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val textViewTo = viewHolder.itemView.findViewById<TextView>(R.id.textViewTo)
        val statusView = viewHolder.itemView.findViewById<TextView>(R.id.text_seen) // Assuming this TextView exists in layout
        Log.d("checking isseem", "bind: $isSeen")
        statusView.text = if (isSeen) "Seen" else "Sent"



        textViewTo.text = text


    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}
