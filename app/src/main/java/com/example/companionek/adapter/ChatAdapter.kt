package com.example.companionek.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.companionek.ChatItems
import com.example.companionek.R

class ChatAdapter(private val chatList: ArrayList<ChatItems>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.chat_items, parent, false)
        return ChatViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val currentItem  = chatList[position]
        //holder.images.setImageResource(currentItem.chatImages)
        holder.title.text = currentItem.chatTitle
        holder.text.text = currentItem.text
        holder.image.setImageResource(currentItem.image)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }



    class ChatViewHolder (itemView: View) :RecyclerView.ViewHolder(itemView){
        val image: ImageView = itemView.findViewById(R.id.chat_imageView)
        val title: TextView = itemView.findViewById(R.id.chat1)
        val text: TextView = itemView.findViewById(R.id.text)

    }

}