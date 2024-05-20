package com.example.companionek.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.companionek.MoodItems
import com.example.companionek.R

class MoodAdapter(private val moodList: ArrayList<MoodItems>) :
    RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.mood_items, parent, false)
        return MoodViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val currentItem  = moodList[position]
        holder.image.setImageResource(currentItem.image)
        holder.moodTitle.text = currentItem.title
        holder.moodDay.text = currentItem.day
        holder.moodDate.text = currentItem.date
    }

    override fun getItemCount(): Int {
        return moodList.size
    }

    class MoodViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.imageView)
        val moodTitle: TextView = itemView.findViewById(R.id.mood1)
        val moodDay: TextView = itemView.findViewById(R.id.mood_day1)
        val moodDate: TextView = itemView.findViewById(R.id.mood_date1)


    }

}