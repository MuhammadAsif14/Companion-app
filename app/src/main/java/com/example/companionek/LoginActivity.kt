package com.example.companionek
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class LoginActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar

    private lateinit var imageView: ImageView
    private lateinit var greetingTextView: TextView
    private lateinit var belowGreetingTV:TextView
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signInButton: Button
    private lateinit var auth: FirebaseAuth
    private var emailPattern = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$"

    private fun setGreetings() {
        // Get current hour
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        // Change image and greeting based on the time of day
        when {
            hour in 5..11 -> { // Morning (5 AM to 11:59 AM)
                imageView.setImageResource(R.drawable.morning_img)
                greetingTextView.text = "Good Morning"
                greetingTextView.setTextColor(Color.parseColor("#FFD700")) // Gold color

//                emailEditText.setBackgroundColor(resources.getColor(R.color.edittext_morning_bg, theme))
//                passwordEditText.setBackgroundColor(resources.getColor(R.color.edittext_morning_bg, theme))
                emailEditText.setTextColor(resources.getColor(R.color.black))
                passwordEditText.setTextColor(resources.getColor(R.color.black))
                // Button Background and Text Colors
                signInButton.setBackground(resources.getDrawable(R.drawable.buttonshapegoldenbg))
                signInButton.setTextColor(resources.getColor(R.color.black, theme))

            }

            hour in 12..16 -> { // Afternoon (12 PM to 4:59 PM)
                belowGreetingTV.setTextColor(resources.getColor(R.color.black))
                imageView.setImageResource(R.drawable.afternoon)
                greetingTextView.text = "Good Afternoon"
                greetingTextView.setTextColor(Color.parseColor("#FF9800")) // OrangeRed color
                emailEditText.setTextColor(resources.getColor(R.color.text_afternoon, theme))
                passwordEditText.setTextColor(resources.getColor(R.color.text_afternoon, theme))
                emailEditText.setHintTextColor(resources.getColor(R.color.text_afternoon, theme))

                // Button Background and Text Colors
                signInButton.setBackgroundColor(resources.getColor(R.color.button_afternoon_bg, theme))
                signInButton.setTextColor(resources.getColor(R.color.text_afternoon, theme))
                val backgroundDrawable = resources.getDrawable(R.drawable.buttonshapegoldenbg)
                signInButton.background = backgroundDrawable
                val forgetpassText=findViewById<TextView>(R.id.forgetPass)
                val newtocompanionText=findViewById<TextView>(R.id.joinText)
                newtocompanionText.setTextColor(Color.BLACK)
                forgetpassText.setTextColor(Color.BLACK)



            }
            hour in 17..22 -> { // Afternoon (5 PM to 10 PM)
                imageView.setImageResource(R.drawable.good_night_img)
                greetingTextView.text = "Good Evening"
                greetingTextView.setTextColor(resources.getColor(R.color.white)) // OrangeRed color
                emailEditText.setTextColor(resources.getColor(R.color.white))
                passwordEditText.setTextColor(resources.getColor(R.color.white))

                val backgroundDrawable = resources.getDrawable(R.drawable.buttonshapewhitebg)
                signInButton.background = backgroundDrawable
                signInButton.setTextColor(resources.getColor(R.color.white))

            }

            else -> { // to 4:59 AM)
                imageView.setImageResource(R.drawable.good_night_img)
                greetingTextView.text = "Good Night"

                greetingTextView.setTextColor(resources.getColor(R.color.white)) // OrangeRed color
                emailEditText.setTextColor(resources.getColor(R.color.text_afternoon, theme))
                passwordEditText.setTextColor(resources.getColor(R.color.text_afternoon, theme))
                val backgroundDrawable = resources.getDrawable(R.drawable.buttonshapewhitebg)
                signInButton.background = backgroundDrawable
                signInButton.setTextColor(resources.getColor(R.color.white))


            }


        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        imageView = findViewById(R.id.imageView)
        greetingTextView = findViewById(R.id.textView)
        belowGreetingTV=findViewById(R.id.textView2)
        emailEditText = findViewById(R.id.emailEditText)  // Assume this is your EditText ID for email
        passwordEditText = findViewById(R.id.passwordEditText)  // Assume this is your EditText ID for password
        signInButton = findViewById(R.id.signInButton)  // Assume this is your Button ID for sign in
        progressBar = findViewById(R.id.progressBar) // Initializing the ProgressBar


        auth = Firebase.auth
        setGreetings()
        signInButton.setOnClickListener {
            val email=emailEditText.text.toString().trim()
            val pass=passwordEditText.text.toString().trim()
            if (email.isEmpty()){
                emailEditText.setError("Enter Email")
            }
            else if(pass.isEmpty()){
                passwordEditText.setError("Enter the password")
            }
            else if (!email.matches(emailPattern.toRegex())) {
                emailEditText.setError("Please enter a valid email address")
            }
            else{
                progressBar.visibility = View.VISIBLE // Show progress bar
                signInButton.visibility=View.GONE
                signInButton.isEnabled = false // Disable button to prevent multiple clicks
                loginUser(email,pass)
            }

        }


    }


    fun loginUser(email: String, password: String) {
        MainScope().launch {
            try {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this@LoginActivity) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            updateLoginStreak(user.uid)

                            progressBar.visibility = View.GONE
                            signInButton.visibility = View.VISIBLE
                            signInButton.isEnabled = true

                            // Proceed to the next activity
                            val intent = Intent(this@LoginActivity, landing_1_activity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        progressBar.visibility = View.GONE
                        signInButton.visibility = View.VISIBLE
                        signInButton.isEnabled = true
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Login failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

//    fun updateLoginStreak(userId: String) {
//        val db = FirebaseFirestore.getInstance()
//        val userRef = db.collection("users").document(userId)
//
//        userRef.get().addOnSuccessListener { document ->
//            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
//
//            val lastLoginDate = document.getString("lastLoginDate")
//            val currentStreak = document.getLong("currentStreak")?.toInt() ?: 0
//            val longestStreak = document.getLong("longestStreak")?.toInt() ?: 0
//
//            val newStreakData = if (lastLoginDate != null && isYesterday(lastLoginDate, currentDate)) {
//                val updatedCurrentStreak = currentStreak + 1
//                val updatedLongestStreak = maxOf(longestStreak, updatedCurrentStreak)
//                mapOf(
//                    "lastLoginDate" to currentDate,
//                    "currentStreak" to updatedCurrentStreak,
//                    "longestStreak" to updatedLongestStreak
//                )
//            } else if (lastLoginDate == currentDate) {
//                // If logged in today, no streak update
//                mapOf("lastLoginDate" to currentDate)
//            } else {
//                // Reset streak if last login was more than a day ago
//                mapOf(
//                    "lastLoginDate" to currentDate,
//                    "currentStreak" to 1,
//                    "longestStreak" to longestStreak
//                )
//            }
//
//            // Update streak information in Firestore
//            userRef.update(newStreakData)
//                .addOnSuccessListener {
//                    // Also add the current date to the loginDates array field in Firestore
//                    userRef.update("loginDates", FieldValue.arrayUnion(currentDate))
//                }
//                .addOnFailureListener { exception ->
//                    println("Error updating streak data: ${exception.message}")
//                }
//        }
//    }
//
//    // Helper function to check if a date is yesterday
//    fun isYesterday(lastLoginDate: String, currentDate: String): Boolean {
//        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        val lastDate = format.parse(lastLoginDate)
//        val currentDateObj = format.parse(currentDate)
//
//        val calendar = Calendar.getInstance()
//        calendar.time = currentDateObj
//        calendar.add(Calendar.DAY_OF_YEAR, -1)
//
//        return lastDate == calendar.time
//    }
fun updateLoginStreak(userId: String) {
    val db = FirebaseFirestore.getInstance()
    val userRef = db.collection("users").document(userId)

    userRef.get().addOnSuccessListener { document ->
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Retrieve last login data, initialize streaks if missing
        val lastLoginDate = document.getString("lastLoginDate")
        val currentStreak = document.getLong("currentStreak")?.toInt() ?: 1
        val longestStreak = document.getLong("longestStreak")?.toInt() ?: 1

        val newStreakData = when {
            lastLoginDate == null -> {
                // First-time login or data not yet set; start both streaks at 1
                mapOf(
                    "lastLoginDate" to currentDate,
                    "currentStreak" to 1,
                    "longestStreak" to 1
                )
            }
            lastLoginDate == currentDate -> {
                // If logged in today, no need to update the streak values
                mapOf("lastLoginDate" to currentDate)
            }
            isYesterday(lastLoginDate, currentDate) -> {
                // If logged in yesterday, continue the streak
                val updatedCurrentStreak = currentStreak + 1
                val updatedLongestStreak = maxOf(longestStreak, updatedCurrentStreak)
                mapOf(
                    "lastLoginDate" to currentDate,
                    "currentStreak" to updatedCurrentStreak,
                    "longestStreak" to updatedLongestStreak
                )
            }
            else -> {
                // If last login was more than a day ago, reset the streak
                mapOf(
                    "lastLoginDate" to currentDate,
                    "currentStreak" to 1,
                    "longestStreak" to longestStreak
                )
            }
        }

        // Update streak information in Firestore
        userRef.update(newStreakData)
            .addOnSuccessListener {
                // Also add the current date to the loginDates array field in Firestore
                userRef.update("loginDates", FieldValue.arrayUnion(currentDate))
            }
            .addOnFailureListener { exception ->
                println("Error updating streak data: ${exception.message}")
            }
    }
}


    // Helper function to check if a date is yesterday
    fun isYesterday(lastLogin: String, currentDate: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val lastDate = dateFormat.parse(lastLogin)
        val currentDateObj = dateFormat.parse(currentDate)

        val calendar = Calendar.getInstance()
        calendar.time = currentDateObj!!
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        return lastDate == calendar.time
    }


    fun joinNowListener(view: View) {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }
}