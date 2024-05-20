package com.example.companionek.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.companionek.R
import com.example.companionek.utils.DiaryItems
import java.text.SimpleDateFormat

class DiaryAdapter(val entries: List<DiaryItems>) : RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.diary_items, parent, false)
        return DiaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        val entry = entries[position]
        holder.title.text = entry.title
        holder.content.text = entry.content
        val dateFormat = SimpleDateFormat("dd MMM yyyy") // Change format as desired
        val formattedDate: String = dateFormat.format(entry.date)
        holder.date.text = formattedDate
        // Set date or other fields as needed
    }

    override fun getItemCount() = entries.size
    class DiaryViewHolder (itemView: View) :RecyclerView.ViewHolder(itemView){
//        val image: ImageView = itemView.findViewById(R.id.chat_imageView)
        val title: TextView = itemView.findViewById(R.id.note_title)
        val content: TextView = itemView.findViewById(R.id.text)
        val date:TextView=itemView.findViewById(R.id.date)

    }

}
