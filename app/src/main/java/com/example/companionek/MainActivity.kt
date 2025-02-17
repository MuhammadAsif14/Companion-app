package com.example.companionek

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings


class MainActivity : Activity() {
    private var logo: ImageView? = null
    private lateinit var settings:FirebaseFirestoreSettings
    private var db=FirebaseFirestore.getInstance()
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        Handler().postDelayed({
//            val intent= Intent(this,landing_1_activity::class.java);
//            startActivity(intent);
//            finish();
//        },1000)
        logo = findViewById<View>(R.id.logo) as ImageView
        val get_started = findViewById<Button>(R.id.get_started)

        settings=FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()


        get_started.setOnClickListener {

                val myIntent: Intent = Intent(this, LoginActivity::class.java)
                this.startActivity(myIntent)

        }
        

    }
}

