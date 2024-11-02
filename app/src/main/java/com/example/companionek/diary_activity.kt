package com.example.companionek

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.companionek.data.Note
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class diary_activity : Activity() {
    private var back_vector: ImageView? = null
    private lateinit var save_button:ImageView
    private lateinit var titleEditText:EditText
    private lateinit var noteContentEditText:EditText
    private lateinit var title:String
    private lateinit var content:String
    private val firestore = FirebaseFirestore.getInstance()
    private var noteId:String? = null

    private var  userId = FirebaseAuth.getInstance().currentUser?.uid
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.diary)
        back_vector = findViewById<View>(R.id.backButton) as ImageView
        save_button=findViewById(R.id.saveButton)
        titleEditText=findViewById(R.id.titleEditText)
        noteContentEditText=findViewById(R.id.noteEditText)

        noteId = intent.getStringExtra("NOTE_ID")?:null
        Log.d("noteId: ","$noteId")
        if(noteId!=null){
            loadNoteContent()
        }

        save_button.setOnClickListener{

            title=titleEditText.text.toString()
            content=noteContentEditText.text.toString()
            if(title.isNotEmpty()||content.isNotEmpty()){
                saveNote(title,content,noteId)
                Toast.makeText(this@diary_activity, "Note saved", Toast.LENGTH_SHORT).show()


            }else{
                Toast.makeText(this@diary_activity, "Empty Note title and content ", Toast.LENGTH_SHORT).show()

            }


        }
        //custom code goes here
        back_vector!!.setOnClickListener { finish() }
    }
    private fun loadNoteContent() {
        userId?.let { uid ->
            val noteRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("Notes")
                .document(noteId!!)

            noteRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Convert document to Note object
                        val note = documentSnapshot.toObject(Note::class.java)

                        // Populate UI with note data
                        note?.let {
                            titleEditText.setText(it.title)
                            noteContentEditText.setText(it.content)
                        }
                    } else {
                        Log.e("loadNoteContent", "No note found with ID: $noteId")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("loadNoteContent", "Error loading note content: ${exception.message}", exception)
                }
        }


    }



//    fun saveNote(title: String, content: String) {
//        if (userId != null&& noteId==null) {
//            // Generate a unique document ID for the note
//            val noteId = firestore
//                .collection("users")
//                .document(userId!!)
//                .collection("Notes")
//                .document().id
//
//            val note = Note(
//                title = title,
//                timestamp = System.currentTimeMillis(),
//                content = content
//            )
//
//            // Save the note in Firestore under the generated noteId
//            firestore.collection("users")
//                .document(userId!!)
//                .collection("Notes")
//                .document(noteId)
//                .set(note)
//                .addOnSuccessListener {
//                    Log.d("SaveNote", "Note saved successfully with ID: $noteId")
//                }
//                .addOnFailureListener { e ->
//                    Log.e("SaveNote", "Error saving note", e)
//                }
//        } else {
//            Log.e("SaveNote", "User ID is null; cannot save note.")
//        }
//    }
fun saveNote(title: String, content: String, noteId: String?) {
    if (userId != null) {
        // Reference to Firestore
        val noteRef = firestore
            .collection("users")
            .document(userId!!)
            .collection("Notes")

        // Check if noteId is null to determine if it's a new note or an update
        if (noteId == null) {
            // Generate a unique document ID for the new note
            val newNoteId = noteRef.document().id

            val note = Note(
                title = title,
                timestamp = System.currentTimeMillis(),
                content = content
            )

            // Save the new note in Firestore under the generated newNoteId
            noteRef.document(newNoteId)
                .set(note)
                .addOnSuccessListener {
                    Log.d("SaveNote", "New note saved successfully with ID: $newNoteId")
                }
                .addOnFailureListener { e ->
                    Log.e("SaveNote", "Error saving new note", e)
                }
        } else {
            // Update the existing note
            val note = Note(
                title = title,
                timestamp = System.currentTimeMillis(),
                content = content
            )

            // Update the note in Firestore
            noteRef.document(noteId)
                .set(note) // or you could use .update() if you only want to update specific fields
                .addOnSuccessListener {
                    Log.d("SaveNote", "Note updated successfully with ID: $noteId")
                }
                .addOnFailureListener { e ->
                    Log.e("SaveNote", "Error updating note", e)
                }
        }
    } else {
        Log.e("SaveNote", "User ID is null; cannot save note.")
    }
}


}
