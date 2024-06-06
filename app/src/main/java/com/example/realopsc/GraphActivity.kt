package com.example.realopsc

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.firestore.FirebaseFirestore

class GraphActivity : AppCompatActivity() {

    private lateinit var lineChart: LineChart
    private lateinit var firestore: FirebaseFirestore
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_graph)

        lineChart = findViewById(R.id.lineChart)
        firestore = FirebaseFirestore.getInstance()
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        firestore.collection("data").get().addOnSuccessListener { snapshot ->
            val entries = mutableListOf<Entry>()
            snapshot.documents.forEachIndexed { index, document ->
                try {
                    val yValue = document.getDouble("value")?.toFloat() ?: 0f
                    entries.add(Entry(index.toFloat(), yValue))
                } catch (e: RuntimeException) {
                    Log.e("DataError", "Skipping document with incorrect format: ${document.id}", e)
                }
            }

            val dataSet = LineDataSet(entries, "Data")

            lineChart.data = LineData(dataSet)

            lineChart.invalidate()
        }



    }
}