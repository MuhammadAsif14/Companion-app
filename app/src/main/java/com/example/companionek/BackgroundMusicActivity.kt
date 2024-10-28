package com.example.companionek

import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.companionek.adapter.SceneAdapter
import com.example.companionek.data.Scene
import com.google.android.material.switchmaterial.SwitchMaterial

class BackgroundMusicActivity : AppCompatActivity() {
    private lateinit var volumeSeekBar: SeekBar
    private lateinit var volumeLevel: TextView

    private lateinit var musicSwitch: SwitchMaterial
    private lateinit var sceneRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_background_music)

        volumeSeekBar = findViewById(R.id.volumeSeekBar)
        volumeLevel = findViewById(R.id.volumeLevel)
        sceneRecyclerView = findViewById(R.id.sceneRecyclerView)
        musicSwitch = findViewById(R.id.material_switch)



        // Sample scenes (replace drawable and raw resources with actual resources in your project)
        val scenes = listOf(
            Scene(R.drawable.scene1_image, R.raw.scene1_music),
            Scene(R.drawable.scene2_image, R.raw.scene2_music),
            Scene(R.drawable.scene3_image, R.raw.scene3_music)
        )

        val sceneAdapter = SceneAdapter(this, scenes)
        sceneRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        sceneRecyclerView.adapter = sceneAdapter

        // Set up volume SeekBar
        volumeSeekBar.progress = 50 // Default volume
        volumeSeekBar.max = 100 // Set max volume to 100
        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                volumeLevel.text = "$progress%"
                // Convert progress to float value between 0.0 and 1.0
                val volume = progress / 100f
                // Send volume change to MusicService
                val volumeIntent = Intent(this@BackgroundMusicActivity, MusicService::class.java).apply {
                    putExtra("ACTION", "SET_VOLUME")
                    putExtra("VOLUME", volume)
                }
                startService(volumeIntent)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })




    }



}