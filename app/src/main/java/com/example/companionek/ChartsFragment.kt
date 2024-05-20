package com.example.companionek

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class ChartsFragment : Fragment() {

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//
//        return inflater.inflate(R.layout.fragment_charts, container, false)
//
//    }
private lateinit var chart: LineChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_charts, container, false)

        chart = view.findViewById(R.id.charts) as LineChart // Cast to LineChart

        // Prepare your data (replace with your actual data)
        val entries = ArrayList<Entry>()
        entries.add(Entry(0f, 4f))
        entries.add(Entry(1f, 8f))
        entries.add(Entry(2f, 6f))
        entries.add(Entry(3f, 12f))
        entries.add(Entry(4f, 2f))

        // Create a LineDataSet and set the data
        val lineDataSet = LineDataSet(entries, "Sample Data")
        chart.data = LineData(lineDataSet)

        // Optional chart customization (colors, labels, etc.)
        // ...

        return view
    }

}