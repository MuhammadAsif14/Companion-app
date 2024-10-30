package com.example.companionek

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.example.companionek.data.ChatMessage2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class LatestMessageRow(val chatMessage: ChatMessage2): Item<GroupieViewHolder>() {
    var chatPartnerUser: Users? = null
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val textViewlatestmessage= viewHolder.itemView.findViewById<TextView>(R.id.message_textview_latest_message)
        val chatPartnerId:String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }
        Log.d("UserID", "$chatPartnerId")
        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Use Users::class.java to match the database structure
                chatPartnerUser = snapshot.getValue(Users::class.java)

                // Assuming Users class has fields `username` and `profileImageUrl`
                val username = viewHolder.itemView.findViewById<TextView>(R.id.username_textview_latest_message)
                username.text = chatPartnerUser?.userName

                val imageViewLatestMessage = viewHolder.itemView.findViewById<ImageView>(R.id.imageview_latest_message)
                Picasso.get().load(chatPartnerUser?.profilepic).placeholder(R.drawable.profile).into(imageViewLatestMessage)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors here
            }
        })
        textViewlatestmessage.text=chatMessage.text
    }

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }
}
