package com.example.companionek

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.companionek.adapter.ChatAdapter
import com.example.companionek.adapter.DiaryAdapter
import com.example.companionek.adapter.MoodAdapter
import com.example.companionek.utils.DiaryItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.util.Date


class HomeActivity : AppCompatActivity() {

    private lateinit var newRecyclerView : RecyclerView
    private lateinit var newRecyclerView2 : RecyclerView
    private lateinit var newRecyclerViewDiary : RecyclerView

    private lateinit var newArrayList: ArrayList<MoodItems>
    private lateinit var newArrayList2: ArrayList<ChatItems>
    private lateinit var diaryEntries: ArrayList<DiaryItems>


    private lateinit var wImageId: Array<Int>
    //private lateinit var mImageId: Array<Int>
    private lateinit var cImageId: Array<Int>
    private lateinit var wTitle: Array<String>
    private lateinit var wDay: Array<String>
    private lateinit var wDate: Array<String>
    //diary
    private lateinit var dTitle:Array<String>
    private lateinit var dContent:Array<String>
    private lateinit var dDate:Array<Date>

    private lateinit var btn1: Button
    private lateinit var mTitle: Array<String>
    private lateinit var mDay: Array<String>
    private lateinit var mDate: Array<String>
    private lateinit var btn2: Button
    private lateinit var cTitle: Array<String>
    private lateinit var cText: Array<String>
    private lateinit var mood_title: TextView
    private lateinit var chat_title: TextView
    private lateinit var diary_title: TextView


    private lateinit var database: DatabaseReference
    private var userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_activity)

        wImageId = arrayOf(R.drawable.happy_emoji, R.drawable.sad_emoji, R.drawable.tired_emoji, R.drawable.happy_emoji)
        btn1 = findViewById(R.id.weekly_btn)

        wTitle = arrayOf("You've felt happy today", "You're feeling sad", "You're feeling tired", "You're feeling happy")
        wDay = arrayOf("Yesterday", "Thursday", "Wednesday", "Tuesday")
        wDate = arrayOf("May 11", "May 10", "May 9", "May 8")

        //mImageId = arrayOf(R.drawable.irritation, R.drawable.happy_emoji, R.drawable.happy_emoji, R.drawable.stuck_emoji)
        btn2 = findViewById(R.id.monthly_btn)
        mTitle = arrayOf("You've felt irritation mostly", "You've felt happy most of the days", "You have been happiest in this month", "You felt stuck most of the days in this month ")
        mDay = arrayOf("May", "April", "March", "February")
        mDate = arrayOf(" 8", " 20", " 17", " 27")

        cImageId = arrayOf(R.drawable.happy_emoji, R.drawable.sad_emoji, R.drawable.angry_emoji, R.drawable.fearful_emoji)
        cTitle = arrayOf("Happy", "Sad", "Angry", "Fearful")
        cText = arrayOf("Will head too the chat session..", "Will head too the chat session..", "Will head too the chat session..", "Will head too the chat session..")

        dTitle= arrayOf("First day at uni","Journy to north Pakistan","Worst day of my life")
        dContent= arrayOf("Will head too the note..","Will head too the note..","Will head too the note..")
        dDate= arrayOf(Date(2024, 4, 7),Date(2024, 4, 14),Date(2024,5,7))


        btn1.setOnClickListener(View.OnClickListener {

            newRecyclerView = findViewById(R.id.mood_recyclerview)


            newRecyclerView.layoutManager = LinearLayoutManager(this)
            newRecyclerView.setHasFixedSize(true)
            newArrayList = arrayListOf<MoodItems>()
            getUserWeekData()
        })

        btn2.setOnClickListener(View.OnClickListener {
            newRecyclerView = findViewById(R.id.mood_recyclerview)


            newRecyclerView.layoutManager = LinearLayoutManager(this)
            newRecyclerView.setHasFixedSize(true)

            newArrayList = arrayListOf<MoodItems>()

            getUserMonthData()
        })
        //chat recycle view
        newRecyclerView2 = findViewById(R.id.ch_recyclerView)

        newRecyclerView2.layoutManager = LinearLayoutManager(this)
        newRecyclerView2.setHasFixedSize(true)
        newArrayList2 = arrayListOf<ChatItems>()
        getUserChatData()

        //diary recycle view
        newRecyclerViewDiary = findViewById(R.id.diary_recyclerView)
        newRecyclerViewDiary.layoutManager=LinearLayoutManager(this)
        newRecyclerViewDiary.setHasFixedSize(true)
        diaryEntries= arrayListOf<DiaryItems>()
        getDiaryData()


        //arrowvector
        mood_title=findViewById(R.id.mood_title);

        mood_title.setOnClickListener(View.OnClickListener { // Handle click events here
            val intent = Intent(
                this@HomeActivity,
                my_mood_activity::class.java
            )
            startActivity(intent)
        })
        btn1.performClick()

        chat_title=findViewById(R.id.chat_title);

        chat_title.setOnClickListener(View.OnClickListener { // Handle click events here
            val intent = Intent(
                this@HomeActivity,
                chat_screen::class.java
            )
            startActivity(intent)
        })
        diary_title=findViewById(R.id.diary_title);

        diary_title.setOnClickListener(View.OnClickListener { // Handle click events here
            val intent = Intent(
                this@HomeActivity,
                diary_activity::class.java
            )
            startActivity(intent)
        })






    }

//    private fun getUserChatData() {
//        for(i in cTitle.indices){
//            val chat = ChatItems(cImageId[i],cTitle[i], cText[i],"avc")
//            newArrayList2.add(chat)
//        }
//        newRecyclerView2.adapter = ChatAdapter(newArrayList2)
//
//    }
private fun getUserChatData() {
    userId?.let { uid ->
        // Retrieve chat sessions for the user
        database.child("user").child(uid).child("chatSessions").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                newArrayList2.clear() // Clear the list before adding new data
                for (sessionSnapshot in dataSnapshot.children) {
                    val emotions = sessionSnapshot.child("emotion").children.map { it.getValue(String::class.java) ?: "" }
                    val sessionId = sessionSnapshot.key ?: "" // Get the session ID

                    // Create a ChatItems instance for this chat session
                    val chatItem = ChatItems(
                        image = R.drawable.happy_emoji, // Choose an appropriate image
                        chatTitle = emotions.joinToString(", "),
                        text = "Chat session with ${emotions.joinToString(", ")} emotions.",
                        sessionId = sessionId // Pass the session ID here
                    )
                    newArrayList2.add(chatItem) // Add the session item to the list
                }

                // Notify the adapter of the data change
                newRecyclerView2.adapter = ChatAdapter(newArrayList2, ::onChatItemClick)
            }
            private fun onChatItemClick(sessionId: String) {
                // Start the chat screen activity and pass the session ID
                val intent = Intent(this@HomeActivity, chat_screen::class.java)
                intent.putExtra("SESSION_ID", sessionId) // Pass the session ID
                startActivity(intent)
            }


            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("ChatMenuFragment", "Error retrieving chat data: ${databaseError.message}")
            }
        })
    }
}


    private fun getUserWeekData() {
        for(i in wTitle.indices){
            val mood = MoodItems(wImageId[i],wTitle[i], wDay[i], wDate[i])
            newArrayList.add(mood)
        }
        newRecyclerView.adapter = MoodAdapter(newArrayList)
    }

    private fun getUserMonthData() {
        for(i in mTitle.indices){
            val mood = MoodItems( wImageId[i],mTitle[i], mDay[i], mDate[i])
            newArrayList.add(mood)
        }
        newRecyclerView.adapter = MoodAdapter(newArrayList)
    }
    private fun getDiaryData(){
        for(i in dTitle.indices){
            val diary = DiaryItems(dTitle[i],dContent[i], dDate[i])
            diaryEntries.add(diary)
        }
        newRecyclerViewDiary.adapter = DiaryAdapter(diaryEntries)

    }


}