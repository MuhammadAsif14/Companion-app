package com.example.companionek

import android.graphics.Color
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.text.SimpleDateFormat
import java.util.Calendar
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
        updateStreaks() // Update streaks display
        setupCalendarView() // Setup calendar highlighting
    }

    private fun loadLoginDates() {
        // Example of adding login dates (replace with actual login dates)
        loginDates.add("2024-10-20")
        loginDates.add("2024-10-21")
        loginDates.add("2024-10-22")
        loginDates.add("2024-10-23")
        loginDates.add("2024-10-24")
        loginDates.add(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))

        // You can load these dates from SharedPreferences or a database
    }

    private fun updateStreaks() {
        val currentStreakCount = calculateCurrentStreak()
        val longestStreakCount = calculateLongestStreak()

        currentStreak.text = "Current Streak: $currentStreakCount days"
        longestStreak.text = "Longest Streak: $longestStreakCount days"
    }

    private fun calculateCurrentStreak(): Int {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        var streak = 0

        // Calculate current streak from the latest date
        for (i in loginDates.size - 1 downTo 0) {
            if (loginDates[i] == today) {
                streak++
                continue
            } else {
                // Check if the previous date is one day before
                val previousDate = Calendar.getInstance().apply {
                    time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(loginDates[i])!!
                    add(Calendar.DAY_OF_MONTH, -0)
                }
                val previousDateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(previousDate.time)

                if (loginDates.contains(previousDateString)) {
                    streak++
                } else {
                    break // Streak broken
                }
            }
        }
        return streak
    }

    private fun calculateLongestStreak(): Int {
        // Implement logic for longest streak (based on your requirements)
        return 10 // Placeholder value, replace with actual calculation
    }

    private fun setupCalendarView() {

        calendarView.setOnDateChangedListener { _, date, _ ->
            val selectedDate = String.format("%04d-%02d-%02d", date.year, date.month, date.day)
            // Handle date clicks (e.g., show details)
        }

        highlightLoginDates() // Highlight the login dates on the calendar
        highlightCurrentDate() // Highlight the current date differently

    }

    private fun highlightLoginDates() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Create a decorator for highlighting login dates
        class LoginDateDecorator(private val dates: List<String>) : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                // Correctly format the CalendarDay to String, accounting for zero-based month
                val dateStr = String.format("%04d-%02d-%02d", day.year, day.month , day.day)

                // Check if the dateStr exists in loginDates
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

        // Create a decorator for highlighting the current date
        class CurrentDateDecorator(private val currentDate: String) : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                // Format the CalendarDay to String
                val dateStr = String.format("%04d-%02d-%02d", day.year, day.month, day.day)
                // Check if the dateStr is the current date
                return dateStr == currentDate
            }

            override fun decorate(view: DayViewFacade) {
                view.setBackgroundDrawable(
                    resources.getDrawable(R.drawable.current_date_bg, null) // Use the round background
                )
                // Optionally, you can also add other decorations here (like text color)
                view.addSpan(ForegroundColorSpan(Color.WHITE)) // Change text color if necessary
            }
        }

        calendarView.addDecorator(CurrentDateDecorator(today))
    }




}
