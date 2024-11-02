package com.example.companionek

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.companionek.utils.FirestoreHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch
import java.util.Calendar


class SignUpActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var greetingTextView: TextView

    private lateinit var belowGreetingTV:TextView
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var displayNameEditText: EditText // New EditText for display name
    private lateinit var loginTextView:TextView
    private lateinit var signUpButton: Button
    private lateinit var auth: FirebaseAuth
    private var emailPattern = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$"
    private lateinit var profilePic:CircleImageView
    private lateinit var loginText:TextView
    private lateinit var imageURI: Uri
    private lateinit var imageuri: String
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference:DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var storage: FirebaseStorage
    private lateinit var password: String
    private lateinit var email: String
    private lateinit var displayName: String
    private lateinit var id:String
    private lateinit var progressBar: ProgressBar



    private fun setGreetingBasedOnTime() {
        // Get current hour
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        // Change image and greeting based on the time of day
        when {
            hour in 5..11 -> { // Morning (5 AM to 11:59 AM)
                imageView.setImageResource(R.drawable.morning_img)
                greetingTextView.text = "Good Morning"
                greetingTextView.setTextColor(Color.parseColor("#FFD700")) // Gold color
                belowGreetingTV.setTextColor(resources.getColor(R.color.white))

                emailEditText.setTextColor(resources.getColor(R.color.white))
                passwordEditText.setTextColor(resources.getColor(R.color.white))
                displayNameEditText.setTextColor(resources.getColor(R.color.white))
                confirmPassword.setTextColor(resources.getColor(R.color.white))

                // Button Background and Text Colors

                val backgroundDrawable = resources.getDrawable(R.drawable.buttonshapegoldenbg)
                signUpButton.background = backgroundDrawable
                signUpButton.setTextColor(resources.getColor(R.color.black, theme))

            }

            hour in 12..16 -> { // Afternoon (12 PM to 4:59 PM)
                imageView.setImageResource(R.drawable.afternoon)
                greetingTextView.text = "Good Afternoon"
                greetingTextView.setTextColor(Color.parseColor("#FF9800")) // OrangeRed color
                belowGreetingTV.setTextColor(resources.getColor(R.color.black))
                loginTextView.setTextColor(resources.getColor(R.color.black))

                emailEditText.setTextColor(resources.getColor(R.color.text_afternoon, theme))
                passwordEditText.setTextColor(resources.getColor(R.color.text_afternoon, theme))
                displayNameEditText.setTextColor(resources.getColor(R.color.black))
                confirmPassword.setTextColor(resources.getColor(R.color.black))
                
                val backgroundDrawable = resources.getDrawable(R.drawable.buttonshapegoldenbg)
                signUpButton.background = backgroundDrawable
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        auth = Firebase.auth

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        imageView = findViewById(R.id.imageView)
        greetingTextView = findViewById(R.id.textView)
        belowGreetingTV=findViewById(R.id.textView2)
        emailEditText = findViewById(R.id.emailEditText)  // Assume this is your EditText ID for email
        passwordEditText = findViewById(R.id.passwordEditText)  // Assume this is your EditText ID for password
        signUpButton = findViewById(R.id.signUpButton)  // Assume this is your Button ID for sign up
        confirmPassword=findViewById(R.id.confirmPassword)
        displayNameEditText = findViewById(R.id.displayNameEditText) // Initialize the display name EditText
        profilePic= findViewById(R.id.profile_image)
        loginTextView=findViewById(R.id.loginText)
        loginText=findViewById(R.id.joinNow)
        progressBar = findViewById(R.id.progressBar) // Initializing the ProgressBar

        setGreetingBasedOnTime()

        profilePic.setOnClickListener(View.OnClickListener { // Handle click event here
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent,"Select Picture"),10);

        })



        // Set button click listener to register user
        signUpButton.setOnClickListener {
            email = emailEditText.text.toString().trim()
            password = passwordEditText.text.toString().trim()
            val confirmPass=confirmPassword.text.toString().trim()
            displayName = displayNameEditText.text.toString().trim() // Get display name


            if(email.isEmpty()){
                emailEditText.setError("Required")
                Toast.makeText(this, "Enter The Email", Toast.LENGTH_SHORT).show();


            }
            if(password.isEmpty()){
                emailEditText.setError("Required")
                Toast.makeText(this, "Enter The Password", Toast.LENGTH_SHORT).show();

            }
            else if(confirmPass.isEmpty() || !password.equals(confirmPass)){
                confirmPassword.setError("Password Mismatch")
            }
           else if (displayName.isEmpty()) {
                displayNameEditText.setError("Required") // Check if display name is empty
                Toast.makeText(this, "Enter The Username", Toast.LENGTH_SHORT).show();

            }
            else if (!email.matches(emailPattern.toRegex())) {
                emailEditText.setError("Please enter a valid email address")
            }
           else if(password.length<6){
                passwordEditText.setError("Password must be Longer than 6 character")
            }

           else if (email.isNotEmpty() && password.isNotEmpty() && displayName.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE // Show progress bar
                signUpButton.visibility=View.GONE
                signUpButton.isEnabled = false // Disable button to prevent multiple clicks
                registerUser(email, password, displayName) // Pass display name to registerUser
            }
            else {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }



    }

    fun loginNowListener(view: View){
        val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
        startActivity(intent)

    }
private fun registerUser(email: String, password: String, newDisplayName: String) {
    lifecycleScope.launch {
        try {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val id = task.result?.user?.uid ?: ""
                    storageReference = storage.reference.child("Upload").child(id)

                    // Image Upload Logic
                    if (imageURI != null) {
                        storageReference.putFile(imageURI)
                            .addOnSuccessListener {
                                storageReference.downloadUrl.addOnSuccessListener { uri ->
                                    val imageUri = uri.toString()
                                    callAddUser(imageUri, id) { isSuccess ->
                                        if (isSuccess) {
                                            Log.d(TAG, "User added successfully.")
                                            //also adding in realtime db for chat feature

                                            launchLandingActivity()
                                        } else {
                                            Log.d(TAG, "Failed to add user.")
                                            Toast.makeText(this@SignUpActivity, "Error adding user data", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }.addOnFailureListener { e ->
                                Log.e(TAG, "Image upload failed: ${e.message}")
                                Toast.makeText(this@SignUpActivity, "Image upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                            }

                    } else {
                        Toast.makeText(this@SignUpActivity, "User did not select profile pic, saving default pic", Toast.LENGTH_SHORT).show()
                        callAddUser("https://firebasestorage.googleapis.com/v0/b/companion-11996.appspot.com/o/man.png?alt=media&token=10104ab8-f99e-4447-bbb0-5c8ff35115b8", id) { isSuccess ->
                            if (isSuccess) {
                                Log.d(TAG, "User added successfully.")
                                launchLandingActivity()
                            } else {
                                Log.d(TAG, "Failed to add user.")
                                Toast.makeText(this@SignUpActivity, "Error adding user data", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                progressBar.visibility = View.GONE// Show progress bar
                signUpButton.visibility=View.VISIBLE
                signUpButton.isEnabled = true // Disable button to prevent multiple clicks
                Log.e(TAG, "Authentication failed: ${e.message}")
                Toast.makeText(baseContext, "Authentication failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@SignUpActivity, "Registration failed: ${e.message}", Toast.LENGTH_LONG).show()
            println(e.message)
        }
    }
}

    // Helper function to launch the landing activity
    private fun launchLandingActivity() {
        progressBar.visibility = View.GONE// Show progress bar
        signUpButton.visibility=View.VISIBLE
        signUpButton.isEnabled = true // Disable button to prevent multiple clicks
        val intent = Intent(this@SignUpActivity, landing_1_activity::class.java)
        startActivity(intent)
        finish()
    }

    private fun callAddUser(imageURI: String, userId: String, callback: (Boolean) -> Unit) {
        lifecycleScope.launch {  // Launch a coroutine
            val firestoreHelperobj = FirestoreHelper()
            try {
                // Save user data to Firestore and get the success status
                val isSuccess = firestoreHelperobj.addUser(displayName, imageURI.toString(), email, password, userId)
                Log.d(TAG, "User added successfully with ID: $userId")
                callback(isSuccess) // Notify the caller about success/failure
            } catch (e: Exception) {
                Log.e(TAG, "Error adding user: ${e.message}", e)
                Toast.makeText(this@SignUpActivity, "Error adding user: ${e.message}", Toast.LENGTH_SHORT).show()
                callback(false) // Notify failure
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10) {
            if (data != null) {
                imageURI = data.data!!
                profilePic.setImageURI(imageURI)
            }
        }
    }
















}


