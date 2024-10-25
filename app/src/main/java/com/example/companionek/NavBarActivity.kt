package com.example.companionek

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.companionek.databinding.ActivityNavBarBinding

class NavBarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNavBarBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityNavBarBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.bottomNavigationBar.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.home -> {
                    replaceFragment(HomeFragment())
                    true // Indicate that you handled the selection
                }
                R.id.profile -> {
                    replaceFragment(ProfileFragment())
                    true // Indicate that you handled the selection
                }
                else -> false
            }
        }

// Optional: Handle reselection to refresh the current fragment
        binding.bottomNavigationBar.setOnItemReselectedListener { item ->
            when(item.itemId) {
                R.id.home -> {
                    // Refresh or scroll to the top of HomeFragment if necessary
                }
                R.id.profile -> {
                    // Refresh or scroll to the top of ProfileFragment if necessary
                }
            }
        }

    }
    private fun replaceFragment(fragment: Fragment){
        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
    }
}