package com.example.companionek

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
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
    private lateinit var delete_button:ImageView
    private lateinit var titleEditText:EditText
    private lateinit var noteContentEditText:EditText
    private lateinit var title:String
    private lateinit var content:String
    private val firestore = FirebaseFirestore.getInstance()
    private var noteId:String? = null

    private var  userId = FirebaseAuth.getInstance().currentUser?.uid

    @SuppressLint("MissingInflatedId")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.diary)
        back_vector = findViewById<View>(R.id.backButton) as ImageView
        save_button=findViewById(R.id.saveButton)
        delete_button=findViewById(R.id.ic_delete_button)
        titleEditText=findViewById(R.id.titleEditText)
        noteContentEditText=findViewById(R.id.noteEditText)
        noteId = intent.getStringExtra("NOTE_ID")?:null
        Log.d("noteId: ","$noteId")
        if (noteId != null) {
            loadNoteContent()
            delete_button.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this note?")
                    .setPositiveButton("Yes") { _, _ ->
                        // Delete note if confirmed
                        val noteRef = firestore
                            .collection("users")
                            .document(userId!!)
                            .collection("Notes")

                        noteRef.document(noteId!!).delete()
                            .addOnSuccessListener {
                                Toast.makeText(this@diary_activity, "Note deleted successfully", Toast.LENGTH_SHORT).show()
                                // Optionally, finish the activity or update the UI
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this@diary_activity, "Error deleting note: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
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
