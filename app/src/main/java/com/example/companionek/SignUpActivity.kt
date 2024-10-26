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
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
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
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var displayNameEditText: EditText // New EditText for display name
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




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        auth = Firebase.auth

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        imageView = findViewById(R.id.imageView)
        greetingTextView = findViewById(R.id.textView)
        emailEditText = findViewById(R.id.emailEditText)  // Assume this is your EditText ID for email
        passwordEditText = findViewById(R.id.passwordEditText)  // Assume this is your EditText ID for password
        signUpButton = findViewById(R.id.signUpButton)  // Assume this is your Button ID for sign up
        confirmPassword=findViewById(R.id.confirmPassword)
        displayNameEditText = findViewById(R.id.displayNameEditText) // Initialize the display name EditText
        profilePic= findViewById(R.id.profile_image)
        loginText=findViewById(R.id.joinNow)

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
//    private fun registerUser(email: String, password: String, newDisplayName: String) {
//        MainScope().launch {
//            try {
//                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this@SignUpActivity){task ->
//                    if (task.isSuccessful) {
//                        val id = task.result?.user?.uid ?: ""
//                         databaseReference = database.reference.child("user").child(id)
//                         storageReference = storage.reference.child("Upload").child(id)
//
//                        if (imageURI != null) {
//                            storageReference.putFile(imageURI)
//                                .addOnCompleteListener { task ->
//                                    if (task.isSuccessful) {
//                                        storageReference.downloadUrl
//                                            .addOnSuccessListener { uri ->
//                                                val imageUri = uri.toString()
//                                                val users = Users(id, newDisplayName, email, password, imageUri)
//                                                databaseReference.setValue(users)
//                                                    .addOnCompleteListener { task ->
//                                                        if (task.isSuccessful) {
//                                                            val intent = Intent(this@SignUpActivity, landing_1_activity::class.java)
//                                                            startActivity(intent)
//                                                            finish()
//                                                        } else {
//                                                            Toast.makeText(this@SignUpActivity, "Error in creating the user", Toast.LENGTH_SHORT).show()
//                                                        }
//                                                    }
//                                            }
//                                    }
//                                }
//                        }
//                         // Sign in success, update UI with the signed-in user's information
//                        Log.d(TAG, "createUserWithEmail:success")
//                        Toast.makeText(baseContext,"UserCreated Successfully.",Toast.LENGTH_SHORT).show()
////                        val user = auth.currentUser
////                        val profileUpdates = userProfileChangeRequest {
////                            displayName = newDisplayName // Set the display name
////                        }
////                        user?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
////                            if (profileTask.isSuccessful) {
////                                Log.d(TAG, "User profile updated.")
////                                // Optionally, navigate to the next activity
////                                 val intent = Intent(this@SignUpActivity, landing_1_activity::class.java)
////                                 startActivity(intent)
////                                 finish()
////
////                            }
////                        }
//
//                    } else {
//                        // If sign in fails, display a message to the user.
//                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
//                        Toast.makeText(
//                            baseContext,
//                            "Authentication failed         vv                       bbbb.",
//                            Toast.LENGTH_SHORT,
//                        ).show()
//
//                    }
//                }
//
//                // Proceed to next activity or perform other actions with the user
//            } catch (e: Exception) {
//                Toast.makeText(this@SignUpActivity, "Registration failed: ${e.message}", Toast.LENGTH_LONG).show()
//                println(e.message)
//            }
//        }
//    }
private fun registerUser(email: String, password: String, newDisplayName: String) {
    lifecycleScope.launch {
        try {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this@SignUpActivity) { task ->
                if (task.isSuccessful) {
                    val id = task.result?.user?.uid ?: ""
                    databaseReference = database.reference.child("user").child(id)
                    storageReference = storage.reference.child("Upload").child(id)

                    if (imageURI != null) {
                        storageReference.putFile(imageURI)
                            .addOnSuccessListener {
                                storageReference.downloadUrl.addOnSuccessListener { uri ->
                                    val imageUri = uri.toString()
                                    val users = Users(id, newDisplayName, email, password, imageUri)
                                    databaseReference.setValue(users).addOnCompleteListener { dbTask ->
                                        if (dbTask.isSuccessful) {
                                            Log.d(TAG, "User data added to database successfully.")
                                        } else {
                                            Toast.makeText(this@SignUpActivity, "Error adding user data", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                    } else {
                        Toast.makeText(this@SignUpActivity, "User did not select profile pic saving default pic", Toast.LENGTH_SHORT).show()
                            imageURI = Uri.parse("https://firebasestorage.googleapis.com/v0/b/companion-11996.appspot.com/o/man.png?alt=media&token=10104ab8-f99e-4447-bbb0-5c8ff35115b8")
                            profilePic.setImageURI(imageURI)
                            val users = Users(id, displayName, email, password, imageURI.toString())
                            databaseReference.setValue(users)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val intent = Intent(this@SignUpActivity, landing_1_activity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this@SignUpActivity, "Error in creating the user", Toast.LENGTH_SHORT).show()
                                    }
                                }


                    }

                    // Launch landing_1_activity regardless of image upload/database completion
                    val intent = Intent(this@SignUpActivity, landing_1_activity::class.java)
                    startActivity(intent)
                    finish()

                    Log.d(TAG, "createUserWithEmail:success")
                    Toast.makeText(baseContext, "User Created Successfully.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this@SignUpActivity, "Registration failed: ${e.message}", Toast.LENGTH_LONG).show()
            println(e.message)
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


