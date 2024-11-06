package com.example.companionek

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ProfileFragment : Fragment() {

    private lateinit var currentStreak: TextView
    private lateinit var longestStreak: TextView
    private lateinit var calendarView: MaterialCalendarView
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private lateinit var profileImage: CircleImageView
    private lateinit var editUsername: EditText



    private val loginDates = mutableListOf<String>() // Store login dates
    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentStreak = view.findViewById(R.id.current_streak)
        longestStreak = view.findViewById(R.id.longest_streak)
        calendarView = view.findViewById(R.id.calendar_view)
        profileImage = view.findViewById(R.id.profile_image)
        editUsername = view.findViewById(R.id.edit_username)

        // Save new username on "Done" action in the EditText
        editUsername.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val newUsername = editUsername.text.toString()
//                updateUsername(newUsername)
                if (newUsername.isNotEmpty()) {
                    updateUsername(newUsername)
                }
                true
            } else {
                false
            }
        }
        // Profile Image Click Listener
        profileImage.setOnClickListener {
            openImagePicker()
        }

        loadProfilePicture()

        loadLoginDates() // Load saved login dates
        setupCalendarView() // Setup calendar highlighting
    }

    private fun loadProfilePicture() {
        val userId = auth.currentUser?.uid ?: return
        val userRef = db.collection("users").document(userId)

        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val profilePicUrl = document.getString("profilepic")
                if (!profilePicUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(profilePicUrl)
                        .placeholder(R.drawable.profile) // placeholder if needed
                        .into(profileImage)
                }
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(context, "Failed to load profile picture", Toast.LENGTH_SHORT).show()
            Log.e("ProfileFragment", "Error loading profile picture", exception)
        }
    }
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            imageUri?.let {
                uploadProfileImage(it)
            }
        }
    }
    private fun uploadProfileImage(imageUri: Uri) {
        val userId = auth.currentUser?.uid ?: return
        val storageRef = storage.reference.child("uploads/$userId.jpg")

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    updateProfileImageUrl(downloadUri.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                Log.e("ProfileFragment", "Error uploading image", it)
            }
    }

    private fun updateProfileImageUrl(profileImageUrl: String) {
        val userId = auth.currentUser?.uid ?: return
        val userRef = db.collection("users").document(userId)

        userRef.update("profilepic", profileImageUrl)
            .addOnSuccessListener {
                Toast.makeText(context, "Profile image updated", Toast.LENGTH_SHORT).show()
                Glide.with(this).load(profileImageUrl).into(profileImage)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to update profile image", Toast.LENGTH_SHORT).show()
                Log.e("ProfileFragment", "Error updating profile image", it)
            }
    }

    private fun updateUsername(newUsername: String) {
        val userId = auth.currentUser?.uid ?: return
        val userRef = db.collection("users").document(userId)

        userRef.update("userName", newUsername)
            .addOnSuccessListener {
                Toast.makeText(context, "Username updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to update username", Toast.LENGTH_SHORT).show()
                Log.e("ProfileFragment", "Error updating username", it)
            }
    }


    private fun loadLoginDates() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        val userRef = userId?.let { db.collection("users").document(it) }
        if (userRef != null) {
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val dates = document.get("loginDates") as? List<String>

                    val currentStreakValue = document.getLong("currentStreak")?.toInt() ?: 0
                    val longestStreakValue = document.getLong("longestStreak")?.toInt() ?: 0
                    currentStreak.text = "Current Streak: $currentStreakValue days"
                    longestStreak.text = "Longest Streak: $longestStreakValue days"
                    Log.d("currentStreak: ","$currentStreakValue")
                    Log.d("LongestStreak", "$longestStreakValue ")
                    if (dates != null) {
                        loginDates.clear()
                        loginDates.addAll(dates)
                        setupCalendarView()
                    }
                }
            }.addOnFailureListener { exception ->
                println("Error fetching login dates: ${exception.message}")
            }
        }
    }
    private fun setupCalendarView() {
        highlightLoginDates() // Highlight the login dates on the calendar
        highlightCurrentDate() // Highlight the current date differently

    }
    private fun highlightLoginDates() {
        class LoginDateDecorator(private val dates: List<String>) : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                val dateStr = String.format("%04d-%02d-%02d", day.year, day.month, day.day)
                return dates.contains(dateStr)
            }

            override fun decorate(view: DayViewFacade) {
                view.setBackgroundDrawable(
                    resources.getDrawable(R.drawable.calendar_selected_bg, null)
                )
            }
        }
        calendarView.addDecorator(LoginDateDecorator(loginDates))
    }
    private fun highlightCurrentDate() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        class CurrentDateDecorator(private val currentDate: String) : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                val dateStr = String.format("%04d-%02d-%02d", day.year, day.month, day.day)
                return dateStr == currentDate
            }
            override fun decorate(view: DayViewFacade) {
                view.setBackgroundDrawable(
                    resources.getDrawable(R.drawable.current_date_bg, null)
                )
                view.addSpan(ForegroundColorSpan(Color.WHITE))
            }
        }
        calendarView.addDecorator(CurrentDateDecorator(today))
    }

}
