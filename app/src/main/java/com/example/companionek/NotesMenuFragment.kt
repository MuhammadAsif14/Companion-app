package com.example.companionek

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.companionek.adapter.DiaryAdapter
import com.example.companionek.data.Note
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NotesMenuFragment : Fragment() {
    override fun onResume() {
        super.onResume()
        getUserNoteData()  // Reload notes when the fragment resumes
    }

    private lateinit var noteRecyclerView: RecyclerView
    private lateinit var fabNewNote: FloatingActionButton
//    private lateinit var notesArrayList: ArrayList<Note>
    private val notesArrayList = ArrayList<Pair<Note, String>>()
    private var userId = FirebaseAuth.getInstance().currentUser?.uid
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_notes_menu, container, false)
//        notesArrayList= arrayListOf()
        noteRecyclerView=rootView.findViewById(R.id.diary_recyclerView)
        noteRecyclerView.layoutManager = LinearLayoutManager(activity)
        noteRecyclerView.setHasFixedSize(true)

        getUserNoteData()

        fabNewNote = rootView.findViewById(R.id.fab_new_note)
        fabNewNote.setOnClickListener {
            openNewChatActivity() // Replace this with your desired action
        }


        return rootView
    }

private fun getUserNoteData() {
    userId?.let { uid ->
        val notesRef = FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("Notes")

        notesRef.get()
            .addOnSuccessListener { notesSnapshot ->
                notesArrayList.clear() // Clear the list before adding new data
                for (noteSnapshot in notesSnapshot.documents) {
                    val noteId = noteSnapshot.id
                    Log.d("noteID", "NoteID: $noteId")

                    // Convert Firestore document to Note object
                    val note = noteSnapshot.toObject(Note::class.java)
                    if (note != null) {
                        notesArrayList.add(Pair(note, noteId)) // Add Pair of Note and noteId
                    }
                }
                noteRecyclerView.adapter = DiaryAdapter(notesArrayList) { noteId ->
                    // Handle the click by opening the detail activity with the noteId
                    val intent = Intent(requireContext(), diary_activity::class.java)
                    intent.putExtra("NOTE_ID", noteId)
                    startActivity(intent)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ChatMenuFragment", "Error retrieving chat data: ${exception.message}", exception)
            }
    }
}





    private fun openNewChatActivity() {
        // Start your new chat activity or perform your action
        val intent = Intent(requireContext(), diary_activity::class.java)
        startActivity(intent)
    }


}