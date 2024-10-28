// SceneAdapter.kt
package com.example.companionek.adapter
// SceneAdapter.kt
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.companionek.MusicService
import com.example.companionek.R
import com.example.companionek.data.Scene

class SceneAdapter(
    private val context: Context,
    private val scenes: List<Scene>
) : RecyclerView.Adapter<SceneAdapter.SceneViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SceneViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.scene_item, parent, false)
        return SceneViewHolder(view)
    }

    override fun onBindViewHolder(holder: SceneViewHolder, position: Int) {
        val scene = scenes[position]
        holder.sceneImage.setImageResource(scene.imageResId)

        // Toggle music on click
        holder.itemView.setOnClickListener {
            val musicIntent = Intent(context, MusicService::class.java).apply {
                putExtra("ACTION", "TOGGLE")
                putExtra("MUSIC_RES_ID", scene.musicResId)
            }
            context.startService(musicIntent)
        }
    }

    override fun getItemCount(): Int = scenes.size

    inner class SceneViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sceneImage: ImageView = view.findViewById(R.id.sceneImage)
    }
}
