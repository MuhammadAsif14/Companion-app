package com.example.companionek

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.companionek.adapter.ChatAdapter
import com.example.companionek.adapter.DiaryAdapter
import com.example.companionek.adapter.MoodAdapter
import com.example.companionek.utils.DiaryItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Date

class HomeFragment : Fragment() {

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var newRecyclerView2: RecyclerView
    private lateinit var newRecyclerViewDiary: RecyclerView

    private lateinit var newArrayList: ArrayList<MoodItems>
    private lateinit var newArrayList2: ArrayList<ChatItems>
    private lateinit var diaryEntries: ArrayList<DiaryItems>

    private lateinit var wImageId: Array<Int>
    private lateinit var cImageId: Array<Int>
    private lateinit var wTitle: Array<String>
    private lateinit var wDay: Array<String>
    private lateinit var wDate: Array<String>
    private lateinit var dTitle: Array<String>
    private lateinit var dContent: Array<String>
    private lateinit var dDate: Array<Date>
    private lateinit var mTitle: Array<String>
    private lateinit var mDay: Array<String>
    private lateinit var mDate: Array<String>
    private lateinit var cTitle: Array<String>
    private lateinit var cText: Array<String>
    private lateinit var profilepic:CircleImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.home_activity, container, false)

        wImageId = arrayOf(R.drawable.happy_emoji, R.drawable.sad_emoji, R.drawable.tired_emoji, R.drawable.happy_emoji)
        wTitle = arrayOf("You've felt happy today", "You're feeling sad", "You're feeling tired", "You're feeling happy")
        wDay = arrayOf("Yesterday", "Thursday", "Wednesday", "Tuesday")
        wDate = arrayOf("May 11", "May 10", "May 9", "May 8")

        mTitle = arrayOf("You've felt irritation mostly", "You've felt happy most of the days", "You have been happiest in this month", "You felt stuck most of the days in this month")
        mDay = arrayOf("May", "April", "March", "February")
        mDate = arrayOf(" 8", " 20", " 17", " 27")

        cImageId = arrayOf(R.drawable.happy_emoji, R.drawable.sad_emoji, R.drawable.angry_emoji, R.drawable.fearful_emoji)
        cTitle = arrayOf("Happy", "Sad", "Angry", "Fearful")
        cText = arrayOf("Will head to the chat session..", "Will head to the chat session..", "Will head to the chat session..", "Will head to the chat session..")

        dTitle = arrayOf("First day at uni", "Journey to north Pakistan", "Worst day of my life")
        dContent = arrayOf("Will head to the note..", "Will head to the note..", "Will head to the note..")
        dDate = arrayOf(Date(2024, 4, 7), Date(2024, 4, 14), Date(2024, 5, 7))

        // Set up RecyclerViews for mood, chat, and diary data
        newRecyclerView = rootView.findViewById(R.id.mood_recyclerview)
        newRecyclerView.layoutManager = LinearLayoutManager(activity)
        newRecyclerView.setHasFixedSize(true)

        newRecyclerView2 = rootView.findViewById(R.id.ch_recyclerView)
        newRecyclerView2.layoutManager = LinearLayoutManager(activity)
        newRecyclerView2.setHasFixedSize(true)

        newRecyclerViewDiary = rootView.findViewById(R.id.diary_recyclerView)
        newRecyclerViewDiary.layoutManager = LinearLayoutManager(activity)
        newRecyclerViewDiary.setHasFixedSize(true)

        // Fetch data and set adapters
        newArrayList = arrayListOf()
        newArrayList2 = arrayListOf()
        diaryEntries = arrayListOf()

        getUserWeekData()
        getUserChatData()
        getDiaryData()

        newRecyclerView.adapter = MoodAdapter(newArrayList)
        newRecyclerView2.adapter = ChatAdapter(newArrayList2)
        newRecyclerViewDiary.adapter = DiaryAdapter(diaryEntries)

        // Button and title listeners for navigation
        rootView.findViewById<View>(R.id.mood_title).setOnClickListener {
            val intent = Intent(activity, my_mood_activity::class.java)
            startActivity(intent)
        }

        rootView.findViewById<View>(R.id.chat_title).setOnClickListener {
            val intent = Intent(activity, chat_screen::class.java)
            startActivity(intent)
        }

        rootView.findViewById<View>(R.id.diary_title).setOnClickListener {
            val intent = Intent(activity, diary_activity::class.java)
            startActivity(intent)
        }
// Fetch and display user data (username and profile picture)
        loadUserProfile(rootView)
        return rootView
    }

    private fun loadUserProfile(rootView: View) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userRef = FirebaseDatabase.getInstance().getReference("user").child(userId)

        userRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val displayName = snapshot.child("userName").value.toString()
                val profileImageUrl = snapshot.child("profilepic").value

                Log.d(TAG, "loadUserProfile: ${displayName}")
                Log.d(TAG, "loadUserProfile: ${profileImageUrl}")
                rootView.findViewById<TextView>(R.id.greeting_text).text ="Hello, "+ displayName
                val profileImageView = view?.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.profile_image)

                // Use Glide to load the image URL into the CircleImageView
                if (profileImageView != null) {
                    Glide.with(this)
                        .load(profileImageUrl)
                        .placeholder(R.drawable.userprofile) // Optional: show a placeholder image while loading
                        .error(R.drawable.picture)           // Optional: show a fallback image if URL fails to load
                        .into(profileImageView)
                }
            }
        }.addOnFailureListener {
            // Handle errors here
        }
    }


    private fun getUserChatData() {
        for (i in cTitle.indices) {
            val chat = ChatItems(cImageId[i], cTitle[i], cText[i])
            newArrayList2.add(chat)
        }
    }

    private fun getUserWeekData() {
        for (i in wTitle.indices) {
            val mood = MoodItems(wImageId[i], wTitle[i], wDay[i], wDate[i])
            newArrayList.add(mood)
        }
    }

    private fun getDiaryData() {
        for (i in dTitle.indices) {
            val diary = DiaryItems(dTitle[i], dContent[i], dDate[i])
            diaryEntries.add(diary)
        }
    }
}
