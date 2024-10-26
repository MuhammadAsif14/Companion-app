package com.example.companionek

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.companionek.adapter.ChatAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ChatMenuFragment : Fragment() {
    private lateinit var cImageId: Array<Int>
    private lateinit var cTitle: Array<String>
    private lateinit var cText: Array<String>
    private lateinit var newRecyclerView2: RecyclerView
    private lateinit var fabNewChat: FloatingActionButton
    private lateinit var newArrayList2: ArrayList<ChatItems>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_chat_menu, container, false)


        //random data
        cImageId = arrayOf(
            R.drawable.happy_emoji,
            R.drawable.sad_emoji,
            R.drawable.angry_emoji,
            R.drawable.fearful_emoji
        )
        cTitle = arrayOf("Happy", "Sad", "Angry", "Fearful")
        cText = arrayOf(
            "Will head to the chat session..",
            "Will head to the chat session..",
            "Will head to the chat session..",
            "Will head to the chat session.."
        )

        newRecyclerView2 = rootView.findViewById(R.id.ch_recyclerView)
        newRecyclerView2.layoutManager = LinearLayoutManager(activity)
        newRecyclerView2.setHasFixedSize(true)


        newArrayList2 = arrayListOf()

        getUserChatData()

        newRecyclerView2.adapter = ChatAdapter(newArrayList2)

        // Initialize the Floating Action Button
        fabNewChat = rootView.findViewById(R.id.fab_new_chat)

        // Set up the click listener
        fabNewChat.setOnClickListener {
            // Handle the click event here
            openNewChatActivity() // Replace this with your desired action
        }

        return  rootView
    }
    private fun openNewChatActivity() {
        // Start your new chat activity or perform your action
        val intent = Intent(requireContext(), chat_screen::class.java)
        startActivity(intent)
    }
    private fun getUserChatData() {
        for (i in cTitle.indices) {
            val chat = ChatItems(cImageId[i], cTitle[i], cText[i])
            newArrayList2.add(chat)
        }
    }

}