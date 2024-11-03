package com.example.companionek

import android.graphics.Color
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ProfileFragment : Fragment() {

    private lateinit var currentStreak: TextView
    private lateinit var longestStreak: TextView
    private lateinit var calendarView: MaterialCalendarView

    private val loginDates = mutableListOf<String>() // Store login dates

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentStreak = view.findViewById(R.id.current_streak)
        longestStreak = view.findViewById(R.id.longest_streak)
        calendarView = view.findViewById(R.id.calendar_view)

        loadLoginDates() // Load saved login dates
        setupCalendarView() // Setup calendar highlighting
    }


//    private fun loadLoginDates() {
//        val userId = "user_id" // Replace with the actual authenticated user's ID
//        val db = FirebaseFirestore.getInstance()
//        val userRef = db.collection("users").document(userId)
//
//        userRef.get().addOnSuccessListener { document ->
//            if (document.exists()) {
//                // Fetch the loginDates array from Firestore
//                val dates = document.get("loginDates") as? List<String>
//                if (dates != null) {
//                    loginDates.clear()  // Clear any existing dates
//                    loginDates.addAll(dates)  // Add dates from Firestore
//                    updateStreaks()  // Update streaks display with actual login dates
//                    setupCalendarView()  // Refresh calendar view with real data
//                }
//            }
//        }.addOnFailureListener { exception ->
//            println("Error fetching login dates: ${exception.message}")
//        }
//    }
//
//
//    private fun updateStreaks() {
//        val currentStreakCount = calculateCurrentStreak()
//        val longestStreakCount = calculateLongestStreak()
//
//        currentStreak.text = "Current Streak: $currentStreakCount days"
//        longestStreak.text = "Longest Streak: $longestStreakCount days"
//    }
//
//    private fun calculateCurrentStreak(): Int {
//        if (loginDates.isEmpty()) return 0
//
//        // Sort login dates
//        val sortedDates = loginDates.map { dateStr ->
//            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr)!!
//        }.sorted()
//
//        var currentStreak = 1
//        val today = Calendar.getInstance()
//        val lastLoginDate = Calendar.getInstance().apply { time = sortedDates.last() }
//
//        // Check if the last login date is today or yesterday
//        if (today.get(Calendar.YEAR) == lastLoginDate.get(Calendar.YEAR) &&
//            today.get(Calendar.DAY_OF_YEAR) - lastLoginDate.get(Calendar.DAY_OF_YEAR) in 0..1
//        ) {
//            // Continue counting the streak back from the last date
//            for (i in sortedDates.size - 2 downTo 0) {
//                val previousDate = Calendar.getInstance().apply { time = sortedDates[i] }
//                lastLoginDate.add(Calendar.DAY_OF_YEAR, -1)
//
//                if (lastLoginDate.time == previousDate.time) {
//                    currentStreak++
//                } else {
//                    break
//                }
//            }
//        } else {
//            currentStreak = 0 // No streak if last login wasn't today or yesterday
//        }
//
//        return currentStreak
//    }
//
//
//    private fun calculateLongestStreak(): Int {
//        if (loginDates.isEmpty()) return 0
//
//        // Sort login dates
//        val sortedDates = loginDates.map { dateStr ->
//            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr)!!
//        }.sorted()
//
//        var longestStreak = 1
//        var currentStreak = 1
//
//        for (i in 1 until sortedDates.size) {
//            val current = sortedDates[i]
//            val previous = sortedDates[i - 1]
//
//            // Check if the current date is exactly one day after the previous date
//            val calendar = Calendar.getInstance()
//            calendar.time = previous
//            calendar.add(Calendar.DAY_OF_YEAR, 1)
//
//            if (calendar.time == current) {
//                currentStreak++
//            } else {
//                longestStreak = maxOf(longestStreak, currentStreak)
//                currentStreak = 1
//            }
//        }
//
//        // Final check to account for the last streak
//        longestStreak = maxOf(longestStreak, currentStreak)
//
//        return longestStreak
//    }

    private fun loadLoginDates() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        val userRef = userId?.let { db.collection("users").document(it) }
        if (userRef != null) {
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val dates = document.get("loginDates") as? List<String>

                    val currentStreakValue = document.getLong("currentStreak")?.toInt() ?: 0
                    val longestStreakValue = document.getLong("longestStreak")?.toInt() ?: 0

                    currentStreak.text = "Current Streak: $currentStreakValue days"
                    longestStreak.text = "Longest Streak: $longestStreakValue days"
                    Log.d("currentStreak: ","$currentStreakValue")
                    Log.d("LongestStreak", "$longestStreakValue ")
                    if (dates != null) {
                        loginDates.clear()
                        loginDates.addAll(dates)
                        setupCalendarView()
                    }
                }
            }.addOnFailureListener { exception ->
                println("Error fetching login dates: ${exception.message}")
            }
        }
    }



    private fun setupCalendarView() {

//        calendarView.setOnDateChangedListener { _, date, _ ->
//            val selectedDate = String.format("%04d-%02d-%02d", date.year, date.month, date.day)
//            // Handle date clicks (e.g., show details)
//        }

        highlightLoginDates() // Highlight the login dates on the calendar
        highlightCurrentDate() // Highlight the current date differently

    }

//    private fun highlightLoginDates() {
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//
//        // Create a decorator for highlighting login dates
//        class LoginDateDecorator(private val dates: List<String>) : DayViewDecorator {
//            override fun shouldDecorate(day: CalendarDay): Boolean {
//                // Correctly format the CalendarDay to String, accounting for zero-based month
//                val dateStr = String.format("%04d-%02d-%02d", day.year, day.month , day.day)
//
//                // Check if the dateStr exists in loginDates
//                return dates.contains(dateStr)
//            }
//
//            override fun decorate(view: DayViewFacade) {
//                view.setBackgroundDrawable(
//                    resources.getDrawable(R.drawable.calendar_selected_bg, null)
//                )
//            }
//        }
//        calendarView.addDecorator(LoginDateDecorator(loginDates))
//    }
//
//
//    private fun highlightCurrentDate() {
//        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
//
//        // Create a decorator for highlighting the current date
//        class CurrentDateDecorator(private val currentDate: String) : DayViewDecorator {
//            override fun shouldDecorate(day: CalendarDay): Boolean {
//                // Format the CalendarDay to String
//                val dateStr = String.format("%04d-%02d-%02d", day.year, day.month, day.day)
//                // Check if the dateStr is the current date
//                return dateStr == currentDate
//            }
//
//            override fun decorate(view: DayViewFacade) {
//                view.setBackgroundDrawable(
//                    resources.getDrawable(R.drawable.current_date_bg, null) // Use the round background
//                )
//                // Optionally, you can also add other decorations here (like text color)
//                view.addSpan(ForegroundColorSpan(Color.WHITE)) // Change text color if necessary
//            }
//        }
//
//        calendarView.addDecorator(CurrentDateDecorator(today))
//    }

    private fun highlightLoginDates() {
        class LoginDateDecorator(private val dates: List<String>) : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                val dateStr = String.format("%04d-%02d-%02d", day.year, day.month, day.day)
                return dates.contains(dateStr)
            }

            override fun decorate(view: DayViewFacade) {
                view.setBackgroundDrawable(
                    resources.getDrawable(R.drawable.calendar_selected_bg, null)
                )
            }
        }
        calendarView.addDecorator(LoginDateDecorator(loginDates))
    }

    private fun highlightCurrentDate() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        class CurrentDateDecorator(private val currentDate: String) : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                val dateStr = String.format("%04d-%02d-%02d", day.year, day.month, day.day)
                return dateStr == currentDate
            }

            override fun decorate(view: DayViewFacade) {
                view.setBackgroundDrawable(
                    resources.getDrawable(R.drawable.current_date_bg, null)
                )
                view.addSpan(ForegroundColorSpan(Color.WHITE))
            }
        }
        calendarView.addDecorator(CurrentDateDecorator(today))
    }




}
