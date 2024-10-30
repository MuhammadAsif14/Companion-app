package com.example.companionek
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.Calendar


class LoginActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var greetingTextView: TextView
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signInButton: Button
    private lateinit var auth: FirebaseAuth
    private var emailPattern = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        imageView = findViewById(R.id.imageView)
        greetingTextView = findViewById(R.id.textView)
        emailEditText = findViewById(R.id.emailEditText)  // Assume this is your EditText ID for email
        passwordEditText = findViewById(R.id.passwordEditText)  // Assume this is your EditText ID for password
        signInButton = findViewById(R.id.signInButton)  // Assume this is your Button ID for sign in


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
                loginUser(email,pass)
            }

        }


    }
    fun loginUser(email: String, password: String){
        MainScope().launch {
            try {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this@LoginActivity){task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(ContentValues.TAG, "Login with email password:success")
                        Toast.makeText(
                            baseContext,
                            "Login Successfully.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        val user = auth.currentUser
                        if (user != null) {
                            // Create an Intent to start LandingActivity1
                            val intent = Intent(this@LoginActivity, landing_1_activity::class.java)
//                            intent.putExtra("USERNAME", user.displayName) // Use displayName for username
//                            intent.putExtra("EMAIL", user.email) // Put email as well
                            // Start LandingActivity1
                            startActivity(intent)
                            finish() // Optionally finish this activity if you don't want to go back to it
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(ContentValues.TAG, "Login:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Login failed.",
                            Toast.LENGTH_SHORT,
                        ).show()

                    }
                }

                // Proceed to next activity or perform other actions with the user
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Registration failed: ${e.message}", Toast.LENGTH_LONG).show()
                println(e.message)
            }
        }

    }

    private fun setGreetings() {
        // Get current hour
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        // Change image and greeting based on the time of day
        when {
            hour in 5..11 -> { // Morning (5 AM to 11:59 AM)
                imageView.setImageResource(R.drawable.good_morning_img)
                greetingTextView.text = "Good Morning"
                greetingTextView.setTextColor(Color.parseColor("#FFD700")) // Gold color

//                emailEditText.setBackgroundColor(resources.getColor(R.color.edittext_morning_bg, theme))
//                passwordEditText.setBackgroundColor(resources.getColor(R.color.edittext_morning_bg, theme))
                emailEditText.setTextColor(resources.getColor(R.color.text_morning, theme))
                passwordEditText.setTextColor(resources.getColor(R.color.text_morning, theme))
                // Button Background and Text Colors
                signInButton.setBackgroundColor(resources.getColor(R.color.button_morning_bg, theme))
                signInButton.setTextColor(resources.getColor(R.color.text_morning, theme))

            }

            hour in 12..16 -> { // Afternoon (12 PM to 4:59 PM)
                imageView.setImageResource(R.drawable.afternoon)
                greetingTextView.text = "Good Afternoon"
                greetingTextView.setTextColor(Color.parseColor("#FF9800")) // OrangeRed color
                // EditText Background and Text Colors
//                emailEditText.setBackgroundColor(resources.getColor(R.color.edittext_afternoon_bg, theme))
//                passwordEditText.setBackgroundColor(resources.getColor(R.color.edittext_afternoon_bg, theme))
                emailEditText.setTextColor(resources.getColor(R.color.text_afternoon, theme))
                passwordEditText.setTextColor(resources.getColor(R.color.text_afternoon, theme))


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
                greetingTextView.setTextColor(Color.parseColor("#FF9800")) // OrangeRed color
                // EditText Background and Text Colors
//                emailEditText.setBackgroundColor(resources.getColor(R.color.edittext_afternoon_bg, theme))
//                passwordEditText.setBackgroundColor(resources.getColor(R.color.edittext_afternoon_bg, theme))
                emailEditText.setTextColor(resources.getColor(R.color.text_afternoon, theme))
                passwordEditText.setTextColor(resources.getColor(R.color.text_afternoon, theme))
                val backgroundDrawable = resources.getDrawable(R.drawable.buttonshapewhitebg)
                signInButton.background = backgroundDrawable


                // Button Background and Text Colors
                signInButton.setBackgroundColor(resources.getColor(R.color.button_afternoon_bg, theme))
                signInButton.setTextColor(resources.getColor(R.color.text_afternoon, theme))

            }

            else -> { // Evening/Night (5 PM to 4:59 AM)
                imageView.setImageResource(R.drawable.good_night_img)
                greetingTextView.text = "Good Night"
//                greetingTextView.setTextColor(Color.parseColor("#1E90FF")) // DodgerBlue color
                // EditText Background and Text Colors
//                emailEditText.setBackgroundColor(resources.getColor(R.color.edittext_evening_bg, theme))
//                passwordEditText.setBackgroundColor(resources.getColor(R.color.edittext_evening_bg, theme))
                emailEditText.setTextColor(resources.getColor(R.color.text_evening, theme))
                passwordEditText.setTextColor(resources.getColor(R.color.text_evening, theme))

                // Button Background and Text Colors
//                signUpButton.setBackgroundColor(resources.getColor(R.color.button_evening_bg, theme))
//                signInButton.setBackgroundColor(resources.getColor(R.color.button_evening_bg, theme))
//                signUpButton.setTextColor(resources.getColor(R.color.text_evening, theme))
//                signInButton.setTextColor(resources.getColor(R.color.text_evening, theme))


            }


        }

    }

    fun joinNowListener(view: View) {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }
}