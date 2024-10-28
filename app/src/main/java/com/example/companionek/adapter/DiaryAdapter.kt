package com.example.companionek.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.companionek.R
import com.example.companionek.data.Note
import java.text.SimpleDateFormat
class DiaryAdapter(
    private val entries: ArrayList<Pair<Note, String>>, // Pair of Note and noteId
    private val onItemClick: (noteId: String) -> Unit // Click listener with noteId
) : RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note, parent, false)
        return DiaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        val (entry, noteId) = entries[position] // Destructure the Pair into Note and noteId
        holder.title.text = truncateText(entry.title,45)
        holder.content.text = entry.content

        val dateFormat = SimpleDateFormat("dd MMM yyyy")
        val formattedDate: String = dateFormat.format(entry.timestamp)
        holder.date.text = formattedDate

        // Set the click listener and pass the noteId when clicked
        holder.itemView.setOnClickListener {
            onItemClick(noteId) // Pass only noteId
        }
    }
    fun truncateText(originalText: String, maxLength: Int): String {
        return if (originalText.length > maxLength) {
            originalText.substring(0, maxLength) + "..."
        } else {
            originalText
        }
    }

    override fun getItemCount() = entries.size

    class DiaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.note_title)
        val content: TextView = itemView.findViewById(R.id.text)
        val date: TextView = itemView.findViewById(R.id.date)
    }
}

//class DiaryAdapter(val entries: ArrayList<Note>) : RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.note, parent, false)
//        return DiaryViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
//        val entry = entries[position]
//        holder.title.text = entry.title
//        holder.content.text = entry.content
//        val dateFormat = SimpleDateFormat("dd MMM yyyy") // Change format as desired
//        val formattedDate: String = dateFormat.format(entry.timestamp)
//        holder.date.text = formattedDate
//        // Set date or other fields as needed
//
//    }
//
//    override fun getItemCount() = entries.size
//    class DiaryViewHolder (itemView: View) :RecyclerView.ViewHolder(itemView){
////        val image: ImageView = itemView.findViewById(R.id.chat_imageView)
//        val title: TextView = itemView.findViewById(R.id.note_title)
//        val content: TextView = itemView.findViewById(R.id.text)
//        val date:TextView=itemView.findViewById(R.id.date)
//
//    }
//
//}
