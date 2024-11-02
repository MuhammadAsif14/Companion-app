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
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.Calendar


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
                greetingTextView.setTextColor(Color.parseColor("#FF9800")) // OrangeRed color
                emailEditText.setTextColor(resources.getColor(R.color.text_afternoon, theme))
                passwordEditText.setTextColor(resources.getColor(R.color.text_afternoon, theme))
                val backgroundDrawable = resources.getDrawable(R.drawable.buttonshapewhitebg)
                signInButton.background = backgroundDrawable
                // Button Background and Text Colors
                signInButton.setBackgroundColor(resources.getColor(R.color.button_afternoon_bg, theme))
                signInButton.setTextColor(resources.getColor(R.color.text_afternoon, theme))

            }

            else -> { // to 4:59 AM)
                imageView.setImageResource(R.drawable.good_night_img)
                greetingTextView.text = "Good Night"
                emailEditText.setTextColor(resources.getColor(R.color.text_evening, theme))
                passwordEditText.setTextColor(resources.getColor(R.color.text_evening, theme))

                // Button Background and Text Colors
                signInButton.setBackgroundColor(resources.getColor(R.color.button_evening_bg, theme))
                signInButton.setTextColor(resources.getColor(R.color.text_evening, theme))


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

    fun loginUser(email: String, password: String){
        MainScope().launch {
            try {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this@LoginActivity){task ->

                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            progressBar.visibility = View.GONE // Hide progress bar
                            signInButton.visibility=View.VISIBLE
                            signInButton.isEnabled = true // Enable the button again

                            // Create an Intent to start LandingActivity1
                            val intent = Intent(this@LoginActivity, landing_1_activity::class.java)
                            startActivity(intent)
                            finish() // Optionally finish this activity if you don't want to go back to it
                        }
                    } else {
                        progressBar.visibility = View.GONE
                        signInButton.visibility=View.VISIBLE
                        signInButton.isEnabled = true

                    }
                }

                // Proceed to next activity or perform other actions with the user
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Registration failed: ${e.message}", Toast.LENGTH_LONG).show()
                println(e.message)
            }
        }

    }



    fun joinNowListener(view: View) {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }
}