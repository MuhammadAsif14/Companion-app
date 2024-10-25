package com.example.companionek

import android.R.attr.value
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView


class MainActivity : Activity() {
    private var logo: ImageView? = null
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
        get_started.setOnClickListener {

            val myIntent: Intent=Intent(this,LoginActivity::class.java)
//            val myIntent: Intent = Intent(this,landing_1_activity::class.java)
            myIntent.putExtra("key", value) //Optional parameters

            this.startActivity(myIntent)

        }
        

    }
}

