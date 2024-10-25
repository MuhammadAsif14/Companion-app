package com.example.companionek

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.Calendar
class SignUpActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var greetingTextView: TextView
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var displayNameEditText: EditText // New EditText for display name
    private lateinit var signUpButton: Button
    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        auth = Firebase.auth
        imageView = findViewById(R.id.imageView)
        greetingTextView = findViewById(R.id.textView)
        emailEditText = findViewById(R.id.emailEditText)  // Assume this is your EditText ID for email
        passwordEditText = findViewById(R.id.passwordEditText)  // Assume this is your EditText ID for password
        signUpButton = findViewById(R.id.signUpButton)  // Assume this is your Button ID for sign up
        confirmPassword=findViewById(R.id.confirmPassword)
        displayNameEditText = findViewById(R.id.displayNameEditText) // Initialize the display name EditText


        setGreetingBasedOnTime()


        // Set button click listener to register user
        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPass=confirmPassword.text.toString().trim()
            var displayName = displayNameEditText.text.toString().trim() // Get display name


            if(email.isEmpty()){
                emailEditText.setError("Required")

            }
            if(password.isEmpty()){
                emailEditText.setError("Required")
            }
            if(confirmPass.isEmpty() || !password.equals(confirmPass)){
                confirmPassword.setError("Password Mismatch")
            }
            if (displayName.isEmpty()) {
                displayNameEditText.setError("Required") // Check if display name is empty
            }

            if (email.isNotEmpty() && password.isNotEmpty() && displayName.isNotEmpty()) {
                registerUser(email, password, displayName) // Pass display name to registerUser
            } else {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
//            registerUser(email, password)
        }



    }
    private fun registerUser(email: String, password: String, newDisplayName: String) {
        MainScope().launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this@SignUpActivity){task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        Toast.makeText(baseContext,"UserCreated Successfully.",Toast.LENGTH_SHORT).show()
                        val user = auth.currentUser
                        val profileUpdates = userProfileChangeRequest {
                            displayName = newDisplayName // Set the display name
                        }
                        user?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
                            if (profileTask.isSuccessful) {
                                Log.d(TAG, "User profile updated.")
                                // Optionally, navigate to the next activity
                                 val intent = Intent(this@SignUpActivity, landing_1_activity::class.java)
                                 startActivity(intent)
                                 finish()

                            }
                        }

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()

                    }
                }

                // Proceed to next activity or perform other actions with the user
            } catch (e: Exception) {
                Toast.makeText(this@SignUpActivity, "Registration failed: ${e.message}", Toast.LENGTH_LONG).show()
                println(e.message)
            }
        }
    }













    private fun setGreetingBasedOnTime() {
        // Get current hour
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        // Change image and greeting based on the time of day
        when {
            hour in 5..11 -> { // Morning (5 AM to 11:59 AM)
                imageView.setImageResource(R.drawable.morning_img)
                greetingTextView.text = "Good Morning"
                greetingTextView.setTextColor(Color.parseColor("#FFD700")) // Gold color

                emailEditText.setTextColor(resources.getColor(R.color.text_morning, theme))
                passwordEditText.setTextColor(resources.getColor(R.color.text_morning, theme))
                // Button Background and Text Colors
                signUpButton.setBackgroundColor(resources.getColor(R.color.button_morning_bg, theme))
                signUpButton.setTextColor(resources.getColor(R.color.text_morning, theme))

            }

            hour in 12..16 -> { // Afternoon (12 PM to 4:59 PM)
                imageView.setImageResource(R.drawable.afternoon_img)
                greetingTextView.text = "Good Afternoon"
                greetingTextView.setTextColor(Color.parseColor("#FF9800")) // OrangeRed color
                emailEditText.setTextColor(resources.getColor(R.color.text_afternoon, theme))
                passwordEditText.setTextColor(resources.getColor(R.color.text_afternoon, theme))

                // Button Background and Text Colors
                signUpButton.setBackgroundColor(resources.getColor(R.color.button_afternoon_bg, theme))
                signUpButton.setTextColor(resources.getColor(R.color.text_afternoon, theme))

            }

            else -> { // Evening/Night (5 PM to 4:59 AM)
                imageView.setImageResource(R.drawable.good_night_img)
                greetingTextView.text = "Good Night"
                emailEditText.setTextColor(resources.getColor(R.color.text_evening, theme))
                passwordEditText.setTextColor(resources.getColor(R.color.text_evening, theme))



            }


        }
//
    }



}