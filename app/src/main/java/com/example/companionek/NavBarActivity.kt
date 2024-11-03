package com.example.companionek

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.companionek.databinding.ActivityNavBarBinding

//class NavBarActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityNavBarBinding
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding=ActivityNavBarBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Load the default HomeFragment when the activity starts
//        if (savedInstanceState == null) {
//            replaceFragment(HomeFragment())
//        }
//
//        binding.bottomNavigationBar.setOnNavigationItemSelectedListener{ item ->
//            when(item.itemId) {
//                R.id.home -> {
//                    replaceFragment(HomeFragment())
//                    true // Indicate that you handled the selection
//                }
//                R.id.chat_users -> {
//                    // Start the activity when `chat_users` is selected
//                    val intent = Intent(this, LatestMessagesActivity::class.java)
//                    startActivity(intent)
//                    true
//                }
//                R.id.diary_menu-> {
//                    replaceFragment(NotesMenuFragment())
//                    true // Indicate that you handled the selection
//                }
//                R.id.profile -> {
//                    replaceFragment(ProfileFragment())
//                    true // Indicate that you handled the selection
//                }
//                R.id.chat_menu -> {
//                    replaceFragment(ChatMenuFragment())
//                    true
//                }
//                else -> false
//            }
//        }
//        // Optional: Handle reselection to refresh the current fragment
//        binding.bottomNavigationBar.setOnItemReselectedListener { item ->
//            when(item.itemId) {
//                R.id.home -> {
//                    replaceFragment(HomeFragment())
//                    true // Indicate that you handled the selection
//                }
//                R.id.chat_users -> {
//                    // Start the activity when `chat_users` is selected
//                    val intent = Intent(this, LatestMessagesActivity::class.java)
//                    startActivity(intent)
//                    true
//                }
//                R.id.diary_menu-> {
//                    replaceFragment(NotesMenuFragment())
//                    true // Indicate that you handled the selection
//                }
//                R.id.profile -> {
//                    replaceFragment(ProfileFragment())
//                    true // Indicate that you handled the selection
//                }
//                R.id.chat_menu -> {
//                    replaceFragment(ChatMenuFragment())
//                    true
//                }
//                else -> false
//            }
//        }
//
//
//    }
//
//private fun replaceFragment(fragment: Fragment) {
//    val fragmentManager = supportFragmentManager
//    val fragmentTransaction = fragmentManager.beginTransaction()
//
//    // Check if the fragment is already added
//    val existingFragment = fragmentManager.findFragmentByTag(fragment::class.java.simpleName)
//    if (existingFragment != null) {
//        // If the fragment is already added, detach and re-attach it to refresh
//        fragmentTransaction.detach(existingFragment).attach(existingFragment)
//    } else {
//        // Otherwise, replace it normally and add it to back stack
//        fragmentTransaction.replace(R.id.frameLayout, fragment, fragment::class.java.simpleName)
//    }
//
//    fragmentTransaction.commit()
//}
//
//}
class NavBarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNavBarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavBarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load the default HomeFragment when the activity starts
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        binding.bottomNavigationBar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    // Go back to HomeFragment
                    navigateToHomeFragment()
                    true
                }
                R.id.chat_users -> {
                    // Start the activity when `chat_users` is selected
                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.diary_menu -> {
                    replaceFragment(NotesMenuFragment())
                    true
                }
                R.id.profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                R.id.chat_menu -> {
                    replaceFragment(ChatMenuFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun navigateToHomeFragment() {
        val fragmentManager = supportFragmentManager
        // Check if HomeFragment is already in the stack
        val homeFragment = fragmentManager.findFragmentByTag(HomeFragment::class.java.simpleName)

        if (homeFragment != null) {
            // If HomeFragment exists, pop everything above it in the back stack
            fragmentManager.popBackStack(HomeFragment::class.java.simpleName, 0)
        } else {
            // Otherwise, load HomeFragment
            replaceFragment(HomeFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        // Add the fragment to the back stack
        fragmentTransaction.replace(R.id.frameLayout, fragment, fragment::class.java.simpleName)
        fragmentTransaction.addToBackStack(fragment::class.java.simpleName) // Add to back stack

        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}
